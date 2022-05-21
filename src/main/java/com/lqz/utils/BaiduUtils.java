package com.lqz.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lqz.constant.BaiduConstant;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author LQZ
 * @date 2022-05-20 14:45
 */

public class BaiduUtils {

    private static final String CODE = "code";
    private static final String MSG = "message";
    private static final String OBJECT = "object";

    public static final String INFO = " - INFO - ";
    public static final String ERROR = " - ERROR - ";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static String newJsonStr(int code, String msg, @Nullable Map<String, Object> map, @Nullable Object o) {
        JSONObject json = JSONUtil.createObj();
        json.putOnce(CODE, code);
        json.putOnce(MSG, msg);
        if (map != null) {
            json.putAll(map);
        }
        if (o != null) {
            json.putOnce(OBJECT, JSONUtil.toJsonStr(o));
        }
        return json.toString();
    }

    public static String newJsonStr(int code, String msg) {
        return newJsonStr(code, msg, null, null);
    }

    public static int getJsonCode(String json) {
        return JSONUtil.parseObj(json).getInt(CODE);
    }

    public static String getJsonMsg(String json) {
        return JSONUtil.parseObj(json).getStr(MSG);
    }

    public static String getJsonStrValue(JSONObject json, String targetKey) {
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            if (key.equals(targetKey)) {
                return String.valueOf(value);
            }
            if (JSONUtil.isTypeJSONObject(value)) {
                return getJsonStrValue(JSONUtil.parseObj(value), targetKey);
            }
        }
        throw new RuntimeException("指定的key: " + targetKey + "不存在");
    }

    public static String getJsonStrValue(String json, String targetKey) {
        JSONObject jsonObject = JSONUtil.parseObj(json);
        return getJsonStrValue(jsonObject, targetKey);
    }

    public static int getJsonIntValue(String json, String key) {
        JSONObject jsonObject = JSONUtil.parseObj(json);
        String value = getJsonStrValue(json, key);
        return Integer.parseInt(value);
    }

    /**
     * INFO 级别, 每次输出不会另起一行
     *
     * @param message 要打印的基本信息
     * @param args    替换掉 message 中 "{}" 的参数
     */
    public static void info(String message, Object... args) {
        print(message, INFO, args);
    }

    /**
     * ERROR 级别, 每次输出不会另起一行
     *
     * @param message 要打印的基本信息
     * @param args    替换掉 message 中 "{}" 的参数
     */
    public static void error(String message, Object... args) {
        print(message, ERROR, args);
    }

    /**
     * INFO 级别, 每次输出另起一行
     *
     * @param message 要打印的基本信息
     * @param args    替换掉 message 中 "{}" 的参数
     */
    public static void newInfo(String message, Object... args) {
        println(message, INFO, args);
    }

    /**
     * ERROR 级别, 每次输出另起一行
     *
     * @param message 要打印的基本信息
     * @param args    替换掉 message 中 "{}" 的参数
     */
    public static void newError(String message, Object... args) {
        println(message, ERROR, args);
    }

    /**
     * 不换行的 System.out.print 输出
     *
     * @param message 要打印的基本信息
     * @param level   打印的信息级别
     * @param args    替换掉 message 中 "{}" 的参数
     */
    private static void print(String message, String level, Object... args) {
        if (args != null && args.length > 0) {
            message = String.format(message.replace("{}", "%s"), args);
        }
        System.out.print(LocalDateTime.now().format(DATE_TIME_FORMATTER) + level + message);
    }

    /**
     * 换行的 System.out.println 输出
     *
     * @param message 要打印的基本信息
     * @param level   打印的信息级别
     * @param args    替换掉 message 中 "{}" 的参数
     */
    private static void println(String message, String level, Object... args) {
        if (args != null && args.length > 0) {
            message = String.format(message.replace("{}", "%s"), args);
        }
        System.out.println(LocalDateTime.now().format(DATE_TIME_FORMATTER) + level + message);
    }

    public static String getBar(float complete, float total) {
        int barLength = 50;
        // 进度百分比
        float percent = complete * 100 / total;
        // 进度条内容
        int done = (int) percent / 2;
        StringBuilder bar = new StringBuilder(barLength);
        for (int i = 0; i < barLength; i++) {
            bar.append(i > done ? " " : "=");
        }
        return bar.toString();
    }

    public static String changeNumberToSize(float size) {
        if (size > BaiduConstant.ONE_GB) {
            return String.format("%.2fGB", size / BaiduConstant.ONE_GB);
        } else if (size > BaiduConstant.ONE_MB) {
            return String.format("%.2fMB", size / BaiduConstant.ONE_MB);
        } else {
            return String.format("%.2fKB", size / BaiduConstant.ONE_KB);
        }
    }
}
