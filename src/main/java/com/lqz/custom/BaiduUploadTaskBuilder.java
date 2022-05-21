package com.lqz.custom;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author LQZ
 * @date 2022-05-20 16:14
 */

@Data
@Accessors(chain = true)
public class BaiduUploadTaskBuilder {
    /**
     * 设置要处理的文件
     */
    private File srcFile;
    /**
     * 设置临时文件的路径, 会随着文件的确定一同设置
     */
    private String tempPath;
    /**
     * 设置授权码
     */
    private String accessToken;
    /**
     * 设置云端的保存路径
     */
    private String appPath;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public BaiduUploadTask build() {
        checkArgs();
        return BaiduUploadTask.getInstance(accessToken, appPath, srcFile, tempPath);
    }

    public BaiduUploadTaskBuilder setFile(File file) {
        this.srcFile = file;
        this.tempPath = setTempPathByFile(file);
        return this;
    }

    private String setTempPathByFile(File file) {
        String parentFolder = file.getParent();
        String random = "temp" + LocalDateTime.now().format(formatter);
        return parentFolder + "\\" + random;
    }

    private void checkArgs() {
        if (srcFile == null) {
            throw new RuntimeException("未指定上传的文件");
        }
        if (accessToken == null) {
            throw new RuntimeException("未设置授权码");
        }
        if (!srcFile.exists()) {
            throw new RuntimeException("指定的文件不存在");
        }
    }
}
