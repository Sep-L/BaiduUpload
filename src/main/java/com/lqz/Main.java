package com.lqz;

import com.lqz.custom.BaiduUploadTask;
import com.lqz.custom.BaiduUploadTaskBuilder;

import java.io.File;
import java.io.IOException;

/**
 * @author LQZ
 * @date 2022-05-20 14:30
 */

public class Test {

    public static void main(String[] args) throws IOException {
        BaiduUploadTaskBuilder builder = new BaiduUploadTaskBuilder();
        builder.setAccessToken("121.67e6ff18e924641e1f4fc965788b5611.Y5Bh1CpPMLuFZYPydkVfykCGCi6wJoddv2E0Lbw.3boO6w");
        builder.setFile(new File("D:\\Documents\\upload\\[猫羽かりん][2022.04.08] [自录]猫羽かりん♡快感に貪欲なVTuberの10回イキ耐久オナニー実演配信♡おまんこASMR♡【#けもみみりふれ】.rar"));
        builder.setAppPath("/我的资源/资源合/集/[猫羽かりん][2022.04.08] [自录]猫羽かりん♡快感に貪欲なVTuberの10回イキ耐久オナニー実演配信♡おまんこASMR♡【#けもみみりふれ】.rar.rar");
        BaiduUploadTask task = builder.build();
        task.startUpload();
        String secondLink, fsId;
    }
}
