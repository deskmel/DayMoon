package com.example.daymoon.Tool;

import android.util.Log;

import java.security.MessageDigest;

public class MD5Tool {
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }

    public static String encode(String str) {
        String strDigest;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] data;
            data = md5.digest(str.getBytes("UTF-8"));
            strDigest = bytesToHexString(data);
        } catch (Exception ex) {
            Log.e("daymoon", "MD5tool error");
            throw new RuntimeException(ex);
        }
        return strDigest;
    }
}
