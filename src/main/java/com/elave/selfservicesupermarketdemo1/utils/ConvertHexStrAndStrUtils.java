package com.elave.selfservicesupermarketdemo1.utils;


import java.nio.charset.StandardCharsets;

/**
 *  蚂蚁舞
 */
public class ConvertHexStrAndStrUtils {

    private static final char[] HEXES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String bytesToHexStr(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(HEXES[(b >> 4) & 0x0F]);
            hex.append(HEXES[b & 0x0F]);
        }
        return hex.toString().toUpperCase();
    }

    public static byte[] hexStrToBytes(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }
        char[] hexChars = hex.toCharArray();
        byte[] bytes = new byte[hexChars.length / 2];   // 如果 hex 中的字符不是偶数个, 则忽略最后一个
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt("" + hexChars[i * 2] + hexChars[i * 2 + 1], 16);
        }
        return bytes;
    }

    public static String strToHexStr(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(HEXES[bit]);
            bit = bs[i] & 0x0f;
            sb.append(HEXES[bit]);
        }
        return sb.toString().trim();
    }

    public static String hexStrToStr(String hexStr) {
        //能被16整除,肯定可以被2整除
        byte[] array = new byte[hexStr.length() / 2];
        try {
            for (int i = 0; i < array.length; i++) {
                array[i] = (byte) (0xff & Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16));
            }
            hexStr = new String(array, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return hexStr;
    }

}



