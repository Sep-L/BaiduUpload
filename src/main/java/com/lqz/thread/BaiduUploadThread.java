package com.lqz.thread;

import com.lqz.constant.BaiduConstant;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author LQZ
 * @date 2022-05-20 23:53
 */

public class BaiduUploadThread {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            BaiduConstant.CORE_POOL_SIZE,
            BaiduConstant.MAX_POOL_SIZE,
            BaiduConstant.KEEP_ALIVE_TIME,
            BaiduConstant.TIME_UNIT,
            BaiduConstant.WORK_QUEUE,
            BaiduConstant.THREAD_FACTORY
    );


    public static Future<String> startMd5(File file) {
        return THREAD_POOL_EXECUTOR.submit(new Md5Thread(file));
    }

    public static Future<String> startUpload(String url, Map<String, Object> map) {
        return THREAD_POOL_EXECUTOR.submit(new UploadThread(url, map));
    }

    public static void startListener(Runnable r) {
        THREAD_POOL_EXECUTOR.execute(r);
    }

    public static void close() {
        THREAD_POOL_EXECUTOR.shutdownNow();
    }
}
