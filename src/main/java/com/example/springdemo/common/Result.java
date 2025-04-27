package com.example.springdemo.common;

import lombok.Data;

/**
 * 统一响应结果类
 * @param <T> 返回数据类型
 */
@Data
public class Result<T> {
    private Integer code; // 状态码
    private String message; // 提示信息
    private T data; // 返回数据

    // 私有构造方法
    private Result() {
    }

    // 私有构造方法
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     * @param <T> 返回数据类型
     * @return 包含默认成功状态码和信息的Result对象
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功返回结果
     * @param data 返回数据
     * @param <T> 返回数据类型
     * @return 包含默认成功状态码、信息和返回数据的Result对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功返回结果
     * @param message 提示信息
     * @param data 返回数据
     * @param <T> 返回数据类型
     * @return 包含默认成功状态码、自定义信息和返回数据的Result对象
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败返回结果
     * @param <T> 返回数据类型
     * @return 包含默认失败状态码和信息的Result对象
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     * @param <T> 返回数据类型
     * @return 包含默认失败状态码和自定义信息的Result对象
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 失败返回结果
     * @param code 状态码
     * @param message 提示信息
     * @param <T> 返回数据类型
     * @return 包含自定义状态码和信息的Result对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
} 