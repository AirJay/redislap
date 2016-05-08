package com.airjay.redis.benchmark.common;

import java.security.MessageDigest;

public class StringGenerator {
    public static String generateValue(int n, int length) {
        StringBuilder result = new StringBuilder(String.valueOf(n));
        while (result.length() < length) {
            result.append("0");
        }
        return result.toString();
    }

    public static String generateKey(int n) {
        return getMd5Str(String.valueOf(n));
    }

    public static String getMd5Str(final String src) {
        try {
            byte[] byteArray = MessageDigest.getInstance("MD5").digest(
                    src.getBytes("utf-8"));
            StringBuffer md5StrBuff = new StringBuffer();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(
                            Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
            return md5StrBuff.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static void main(String[] args) {
        int length = 100;
        for (int i = 0; i < 10; i++) {
            System.out.println(StringGenerator.generateKey(i));
            System.out.println(StringGenerator.generateValue(i, length));
        }
    }
}
