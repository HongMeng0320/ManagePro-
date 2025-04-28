package com.example.springdemo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类，用于密码加密和验证
 */
public class Md5Util {
    
    /**
     * 计算MD5值
     * @param str 要计算的字符串
     * @return MD5值
     */
    private static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    sb.append("0");
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
    
    /**
     * 对密码进行加密
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String getMD5String(String password) {
        System.out.println("进行MD5加密，密码: " + password);
        String md5Value = md5(password);
        System.out.println("MD5加密结果: " + md5Value);
        return md5Value;
    }
    
    /**
     * 对密码进行加密 (别名方法，保持兼容性)
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return getMD5String(password);
    }
    
    /**
     * 校验密码是否正确
     * @param password 要校验的密码
     * @param md5Str 数据库中存储的加密密码
     * @return 验证结果
     */
    public static boolean checkPassword(String password, String md5Str) {
        System.out.println("验证密码: " + password);
        System.out.println("数据库中存储的密码: " + md5Str);
        
        // 对输入的密码进行MD5加密
        String calculated = getMD5String(password);
        System.out.println("计算得到的MD5值: " + calculated);
        
        // 比较两个MD5值
        boolean result = md5Str.equals(calculated);
        System.out.println("验证结果: " + result);
        return result;
    }
    
    /**
     * 用于测试的主方法
     */
    public static void main(String[] args) {
        String[] passwords = {"admin123", "123456", "password", "admin", "user1"};
        
        System.out.println("====== 密码MD5值对照表 ======");
        for (String password : passwords) {
            System.out.println(password + " => " + encode(password));
        }
        System.out.println("===========================");
    }
} 