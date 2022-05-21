package com.lqz.custom;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.lqz.constant.BaiduConstant;
import com.lqz.thread.BaiduUploadThread;
import com.lqz.thread.SliceListener;
import com.lqz.thread.UploadListener;
import com.lqz.utils.BaiduUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author LQZ
 * @date 2022-05-20 16:14
 */


@Data
@Accessors(chain = true)
public class BaiduUploadTask {

    /**
     * 云端保存的路径
     */
    private final String appPath;
    /**
     * 要处理的文件
     */
    private final File file;
    /**
     * 临时文件的父目录
     */
    private final String tempPath;
    /**
     * 授权码
     */
    private final String accessToken;
    /**
     * 固定参数 1
     */
    private final int autoInit = 1;
    /**
     * 遇到重名时的解决方式
     */
    private int rType = 0;
    /**
     * 上传的是否是文件
     */
    private int isDir = 0;
    /**
     * 是否删除源文件
     */
    private boolean deleteAll = false;

    private BaiduUploadTask(String accessToken, String appPath, File file, String tempPath) {
        this.accessToken = accessToken;
        this.appPath = appPath;
        this.file = file;
        this.tempPath = tempPath;
    }


    /**
     * 创建唯一的任务对象
     * @param accessToken 授权码
     * @param file 上传的文件
     * @param tempPath 临时文件夹的路径
     * @return 任务对象
     */
    public static BaiduUploadTask getInstance(String accessToken, String appPath, File file, String tempPath) {
        return new BaiduUploadTask(accessToken, appPath, file, tempPath);
    }

    /**
     * 开始执行上传任务
     * @return 文件的 secondLink 和 fsId 构成的字符串列表
     */
    public List<String> startUpload() {
        // 文件的处理
        if (file.isFile()) {
            return uploadFile();
        }
        // 文件夹的处理 TODO
        throw new RuntimeException("现在没做文件夹的上传");
    }

    /**
     * 开始上传文件
     * @return 文件的 secondLink 和 fsId 构成的字符串列表
     */
    private List<String> uploadFile() {
        String secondLink;
        String fsId;
        try {
            FileUtil.mkdir(tempPath);
            BaiduUtils.newInfo("当前文件名: {}", file.getName());
            // 先对文件进行切分
            List<FragFileInfo> fragFileInfos = sliceFile();
            // 计算分片文件所构成的 md5 列表
            String blockList = integrateMd5(
                    fragFileInfos.stream().map(FragFileInfo::getMd5).collect(Collectors.toList()));
            // 预上传
            String response = createPreUploadTask(blockList);
            checkResponse(response);
            // 获得上传标识 ID
            String uploadId = BaiduUtils.getJsonMsg(response);
            // 分片上传
            response = baiduUploadFile(fragFileInfos, uploadId);
            checkResponse(response);
            secondLink = BaiduUtils.getJsonMsg(response);
            // 创建文件
            response = baiduCreateFile(blockList, uploadId);
            checkResponse(response);
            BaiduUtils.newInfo("上传完成, 保存路径: {}", appPath);
            fsId = BaiduUtils.getJsonStrValue(response, BaiduConstant.FS_ID);
            if (deleteAll) {
                FileUtil.del(file);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeResources();
        }
        return ListUtil.toList(secondLink, fsId);
    }


    private void closeResources() {
        // 关闭连接池
        BaiduUploadThread.close();
        // 删除临时文件
        FileUtil.del(tempPath);
    }

    private void checkResponse(String response) {
        if (BaiduUtils.getJsonCode(response) != BaiduConstant.SUCCESS) {
            closeResources();
            throw new RuntimeException(response);
        }
    }
    
    private String integrateMd5(List<String> md5List) {
        StringBuilder md5s = new StringBuilder();
        md5s.append("[");
        for (String md5 : md5List) {
            md5s.append("\"").append(md5).append("\"").append(",");
        }
        md5s.setCharAt(md5s.length() - 1, ']');
        return md5s.toString();
    }

    private List<FragFileInfo> sliceFile() {
        // BaiduUtils.newInfo("正在对文件进行分片, 当前文件名: {}", file.getName());
        // 获得文件的大小
        long totalSize = FileUtil.size(file);
        // 如果小于分片的大小, 那就不需要分片
        if (totalSize <= BaiduConstant.FIRST_SLICE_SIZE) {
            FragFileInfo fileInfo = new FragFileInfo().
                    setTmpFile(file).setPartIndex(0).setMd5(DigestUtil.md5Hex(file));
            return Collections.singletonList(fileInfo);
        }
        // 大于分片大小, 开始算分片数
        int splitNums = (int) (((totalSize - BaiduConstant.FIRST_SLICE_SIZE) / BaiduConstant.OTHER_SLICE_SIZE + 1) + 1);
        // 创建分片信息列表
        List<FragFileInfo> fragFileInfos = new ArrayList<>();
        // 设置监听器
        SliceListener listener = new SliceListener(totalSize);
        BaiduUploadThread.startListener(listener);
        // 创建输入流
        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
            // 将文件内容写到各个分片中
            for (int i = 0; i < splitNums; i++) {
                // 当前创建的临时文件路径
                String tempFilePath = tempPath + "\\" + i + ".temp";
                // 创建临时文件对象
                File tempFile = new File(tempFilePath);
                // 创建输出流
                try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(tempFilePath)))) {
                    // 写入文件
                    int len;
                    long hasRead = 0;
                    byte[] buffer = new byte[8192];
                    while ((len = bis.read(buffer)) != -1) {
                        hasRead += len;
                        listener.add(len);
                        bos.write(buffer, 0, len);
                        if (hasRead >= BaiduConstant.OTHER_SLICE_SIZE || (i == 0 & hasRead >= BaiduConstant.FIRST_SLICE_SIZE)) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // 设置分片文件信息并保存到集合中
                FragFileInfo fileInfo = new FragFileInfo()
                        .setTmpFile(tempFile).setPartIndex(i).setMd5(DigestUtil.md5Hex(tempFile));
                fragFileInfos.add(fileInfo);
            }
            Thread.sleep(BaiduConstant.WAIT_TIME);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return fragFileInfos;
    }

    private String createPreUploadTask(String blockList) {
        // 生成请求链接
        String preCreateUrl = String.format(
                "https://pan.baidu.com/rest/2.0/xpan/file?method=precreate&access_token=%s", accessToken);
        // 设置请求体
        Map<String, Object> requestBody = new HashMap<>(BaiduConstant.PRE_CREATE_MAP_SIZE);
        requestBody.put("path", appPath);
        requestBody.put("size", FileUtil.size(file));
        requestBody.put("isdir", isDir);
        requestBody.put("block_list", blockList);
        requestBody.put("autoinit", autoInit);
        requestBody.put("rtype", rType);
        // 获得响应
        String response = HttpUtil.post(preCreateUrl, requestBody);
        // 判断是否出错
        if (JSONUtil.parseObj(response).getInt(BaiduConstant.ERRNO_KEY) != BaiduConstant.SUCCESS) {
            return BaiduUtils.newJsonStr(BaiduConstant.FAILED, response);
        }
        // 没有出错会获得带有上传 ID 的 JSON
        String uploadId = JSONUtil.parseObj(response).getStr(BaiduConstant.UPLOAD_ID);
        return BaiduUtils.newJsonStr(BaiduConstant.SUCCESS, uploadId);
    }

    private String baiduUploadFile(List<FragFileInfo> fileInfos, String uploadId) {
        // 开启计算总文件 md5 的任务
        Future<String> md5Task = BaiduUploadThread.startMd5(file);
        // 开启监听器
        UploadListener listener = new UploadListener(fileInfos.size());
        BaiduUploadThread.startListener(listener);
        List<Future<String>> futureList = new ArrayList<>();
        for (FragFileInfo fileInfo : fileInfos) {
            // url 不在请求体中的 path 路径需要 urlEncode
            String uploadUrl = String.format("https://d.pcs.baidu.com/rest/2.0/pcs/superfile2?" +
                    "method=upload&access_token=%s&type=tmpfile&path=%s&uploadid=%s&partseq=%d",
                                   accessToken, URLUtil.encode(appPath), uploadId, fileInfo.getPartIndex());
            Map<String, Object> requestBody = new HashMap<>(BaiduConstant.SLICE_UPLOAD_MAP_SIZE);
            requestBody.put("file", fileInfo.getTmpFile());
            Future<String> future = BaiduUploadThread.startUpload(uploadUrl, requestBody);
            futureList.add(future);
        }
        String secondLink;
        try {
            for (Future<String> future : futureList) {
                future.get();
                listener.add();
            }
            secondLink = md5Task.get();
            Thread.sleep(BaiduConstant.WAIT_TIME);
        } catch (InterruptedException | ExecutionException e) {
            BaiduUploadThread.close();
            throw new RuntimeException(e.getMessage());
        }
        return BaiduUtils.newJsonStr(BaiduConstant.SUCCESS, secondLink);
    }

    private String baiduCreateFile(String blockList, String uploadId) {
        // BaiduUtils.newInfo("正在创建文件");
        String url = String.format(
                "https://pan.baidu.com/rest/2.0/xpan/file?method=create&access_token=%s", accessToken);
        // 请求参数
        Map<String, Object> requestBody = new HashMap<>(BaiduConstant.CREATE_FILE_MAP_SIZE);
        requestBody.put("path", appPath);
        requestBody.put("size", FileUtil.size(file));
        requestBody.put("isdir", isDir);
        requestBody.put("block_list", blockList);
        requestBody.put("uploadid", uploadId);
        // 解析返回值
        String response = HttpUtil.post(url, requestBody);
        if (JSONUtil.parseObj(response).getInt(BaiduConstant.ERRNO_KEY) != BaiduConstant.SUCCESS) {
            return BaiduUtils.newJsonStr(BaiduConstant.FAILED, response);
        }
        return BaiduUtils.newJsonStr(BaiduConstant.SUCCESS, response);
    }
}
