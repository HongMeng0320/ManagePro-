package com.example.springdemo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 测试工具类 - 用于生成密码的MD5值
 */
public class TestMD5 {
    
    /**
     * 计算字符串的MD5值
     */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes());
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
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 主方法，用于生成密码的MD5值
     */
    public static void main(String[] args) {
        String[] passwords = {"admin123", "123456", "password", "admin", "user1"};
        
        System.out.println("====== 密码MD5值对照表 ======");
        for (String password : passwords) {
            System.out.println(password + " => " + md5(password));
        }
        System.out.println("===========================");
        
        // 特别注意：数据库中已有的密码值
        System.out.println("\n已知密码的MD5值对照:");
        System.out.println("admin123 (0192023a7bbd73250516f069df18b500) 是否匹配: " 
                          + md5("admin123").equals("0192023a7bbd73250516f069df18b500"));
        System.out.println("123456 (e10adc3949ba59abbe56e057f20f883e) 是否匹配: " 
                          + md5("123456").equals("e10adc3949ba59abbe56e057f20f883e"));
    }
} 