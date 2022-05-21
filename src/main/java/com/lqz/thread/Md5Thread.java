package com.lqz.thread;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.lqz.constant.BaiduConstant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;

/**
 * @author LQZ
 * @date 2022-05-21 1:04
 */

public class Md5Thread implements Callable<String> {
    private final File file;

    public Md5Thread(File file) {
        this.file = file;
    }

    @Override
    public String call() {
        String wholeMd5 = DigestUtil.md5Hex(file);
        String partMd5;
        try {
            partMd5 = DigestUtil.md5Hex(
                    IoUtil.readBytes(Files.newInputStream(file.toPath()), BaiduConstant.PART_SIZE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long fileSize = FileUtil.size(file);
        String filename = file.getName();
        // BaiduUtils.newInfo("秒传计算完成");
        return StrUtil.join("#", wholeMd5, partMd5, fileSize, filename);
    }
}

