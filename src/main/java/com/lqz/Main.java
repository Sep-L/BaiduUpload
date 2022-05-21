package com.lqz;

import com.lqz.custom.BaiduUploadTask;
import com.lqz.custom.BaiduUploadTaskBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author LQZ
 * @date 2022-05-20 14:30
 */

public class Main {

    public static void main(String[] args) throws IOException {
        // 创建上传任务
        BaiduUploadTaskBuilder builder = new BaiduUploadTaskBuilder();
        builder.setAccessToken("百度网盘的授权码")
                .setFile(new File("上传的文件"))
                .setAppPath("需要保存在网盘的哪个位置");
        BaiduUploadTask task = builder.build();

        // 设置上传参数
        task.setIsDir(0)
                .setRType(0)
                .setDeleteAll(false);

        // 开始上传
        List<String> result = task.startUpload();
        String secondLink = result.get(0);
        String fsId = result.get(1);
        System.out.println("secondLink = " + secondLink);
        System.out.println("fsId = " + fsId);
    }
}
