package com.lqz.thread;

import cn.hutool.http.HttpUtil;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author LQZ
 * @date 2022-05-21 1:03
 */

public class UploadThread implements Callable<String> {
    private final String url;
    private final Map<String, Object> requestMap;

    public UploadThread(String url, Map<String, Object> requestMap) {
        this.url = url;
        this.requestMap = requestMap;
    }

    @Override
    public String call() {
        String response;
        // try {
            response = HttpUtil.post(url, requestMap);
        // } catch (Exception e) {
        //     response = HttpRequest.post(url)
        //             .header(Header.CONTENT_TYPE, ContentType.FORM_URLENCODED.toString())
        //             .form(requestMap).execute().body();
        // }
        return response;
    }
}
