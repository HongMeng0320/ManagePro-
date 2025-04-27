package com.example.springdemo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * MD5工具类，用于密码加密和验证
 */
public class Md5Util {
    
    private static final String SALT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SALT_LENGTH = 8;
    
    /**
     * 获取随机盐值
     * @return 随机盐值
     */
    private static String getSalt() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < SALT_LENGTH; i++) {
            int index = random.nextInt(SALT_CHARS.length());
            sb.append(SALT_CHARS.charAt(index));
        }
        return sb.toString();
    }
    
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
     * 对密码进行加密（带盐值）
     * @param password 原始密码
     * @return 加密后的密码，格式：md5(password+salt)+salt
     */
    public static String getMD5String(String password) {
        String salt = getSalt();
        String md5 = md5(password + salt);
        return md5 + salt;
    }
    
    /**
     * 校验密码是否正确
     * @param password 要校验的密码
     * @param md5Str 数据库中存储的加密密码
     * @return 验证结果
     */
    public static boolean checkPassword(String password, String md5Str) {
        // md5Str格式：md5(password+salt)+salt
        if (md5Str.length() < SALT_LENGTH) {
            return false;
        }
        
        // 取出盐值
        String salt = md5Str.substring(md5Str.length() - SALT_LENGTH);
        // 取出MD5值
        String md5 = md5Str.substring(0, md5Str.length() - SALT_LENGTH);
        // 重新计算MD5值
        String newMd5 = md5(password + salt);
        
        // 比较MD5值
        return md5.equals(newMd5);
    }
}
