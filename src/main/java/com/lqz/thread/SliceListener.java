package com.lqz.thread;

import com.lqz.constant.BaiduConstant;
import com.lqz.utils.BaiduUtils;

/**
 * @author LQZ
 * @date 2022-05-21 1:02
 */

public class SliceListener implements Runnable {

    private float curSize;
    private final float totalSize;

    public SliceListener(float totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public void run() {
        startListen();
    }

    public void add(long len) {
        curSize += len;
    }

    private void startListen() {
        String message = "分片中: {}%% [{}] {} / {}";
        while (curSize < totalSize) {
            showProcessBar(message);
        }
        showProcessBar(message);
        System.out.println();
    }

    private void showProcessBar(String message) {
        try {
            Thread.sleep(BaiduConstant.SLEEP_TIME);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        BaiduUtils.info(
                message + "\r",
                String.format("%.2f", curSize * 100 / totalSize),
                BaiduUtils.getBar(curSize, totalSize),
                BaiduUtils.changeNumberToSize(curSize),
                BaiduUtils.changeNumberToSize(totalSize)
        );
    }
}
