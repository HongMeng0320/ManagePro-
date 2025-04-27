package com.example.springdemo.utils;

import java.util.Map;

/**
 * ThreadLocal工具类，用于在当前线程中存储和获取数据
 * 主要用于存储JWT解析后的用户信息
 */
public class ThreadLocalUtil {
    // 创建ThreadLocal对象
    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前线程中的数据
     * @param data 要存储的数据
     */
    public static void set(Map<String, Object> data) {
        THREAD_LOCAL.set(data);
    }

    /**
     * 获取当前线程中的数据
     * @return 存储的数据
     */
    public static Map<String, Object> get() {
        return THREAD_LOCAL.get();
    }

    /**
     * 清除当前线程中的数据
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
