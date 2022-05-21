package com.lqz.constant;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author LQZ
 * @date 2022-05-20 15:21
 */

public class BaiduConstant {

    /**
     * 文件大小参数
     */
    public static final int ONE_KB = 1024;
    public static final int ONE_MB = 1024 * 1024;
    public static final int ONE_GB = 1024 * 1024 * 1024;

    /**
     * 切片参数
     */
    public static final int PART_SIZE = 256 * ONE_KB;
    public static final long FIRST_SLICE_SIZE = 4 * ONE_MB;
    public static final long OTHER_SLICE_SIZE = 32 * ONE_MB;

    /**
     * 线程等待时间参数
     */
    public static final long SLEEP_TIME = 50;
    public static final long WAIT_TIME = 100;

    /**
     * 请求体的大小参数
     */
    public static final int PRE_CREATE_MAP_SIZE = 6;
    public static final int SLICE_UPLOAD_MAP_SIZE = 1;
    public static final int CREATE_FILE_MAP_SIZE = 5;

    /**
     * 响应 json 的信息 key
     */
    public static final String UPLOAD_ID = "uploadid";
    public static final String FS_ID = "fs_id";
    public static final String ERRNO_KEY = "errno";

    /**
     * 请求成功
     */
    public static final int SUCCESS = 0;
    /**
     * 请求失败
     */
    public static final int FAILED = -1;
    /**
     * 出现异常
     */
    public static final int EXCEPTION = -2;

    /**
     * 线程池的参数
     */
    public static final int CORE_POOL_SIZE = 8;
    public static final int MAX_POOL_SIZE = 8;
    public static final long KEEP_ALIVE_TIME = 60;
    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    public static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();
    public static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().build();
}
