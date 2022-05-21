package com.lqz.thread;

import com.lqz.constant.BaiduConstant;
import com.lqz.utils.BaiduUtils;

/**
 * @author LQZ
 * @date 2022-05-21 1:03
 */

public class UploadListener implements Runnable {
    private int complete;
    private final int total;

    public UploadListener(int total) {
        this.total = total;
    }

    @Override
    public void run() {
        startListener();
    }

    public void add() {
        complete++;
    }

    private void startListener() {
        while (complete < total) {
            showProcessBar();
        }
        showProcessBar();
        System.out.println();
    }

    private void showProcessBar() {
        try {
            Thread.sleep(BaiduConstant.SLEEP_TIME);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String bar = BaiduUtils.getBar(complete, total);
        String message = "上传中: {} / {} [{}]\r";
        BaiduUtils.info(message, complete, total, bar);
    }
}
