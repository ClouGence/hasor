/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.more.util;
import java.io.UnsupportedEncodingException;
/**
 * 简单编码类，该类提供了Base64的编码和解码以及MD5加密串提取的方法。注意该类不适宜将大数据进行编码操作。
 * @version 2009-4-28
 * @author 赵永春 (zyc@byshell.org)
 */
public final class SimpleCode {
    // ===============================================================
    private static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
    private static byte[] base64DecodeChars = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };
    // ===============================================================
    /**
     * 将数据进行Base64编码。
     * @param data 要编码的原始数据。
     * @return 返回编码之后的结果。
     */
    public static String encodeBase64(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(SimpleCode.base64EncodeChars[b1 >>> 2]);
                sb.append(SimpleCode.base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(SimpleCode.base64EncodeChars[b1 >>> 2]);
                sb.append(SimpleCode.base64EncodeChars[(b1 & 0x03) << 4 | (b2 & 0xf0) >>> 4]);
                sb.append(SimpleCode.base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(SimpleCode.base64EncodeChars[b1 >>> 2]);
            sb.append(SimpleCode.base64EncodeChars[(b1 & 0x03) << 4 | (b2 & 0xf0) >>> 4]);
            sb.append(SimpleCode.base64EncodeChars[(b2 & 0x0f) << 2 | (b3 & 0xc0) >>> 6]);
            sb.append(SimpleCode.base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }
    /**
     * 解码Base64数据。
     * @param str 要解码的Base64字符串。
     * @return 返回解码之后的原始数据。
     * @throws UnsupportedEncodingException 如果解码过程中发生异常。
     */
    public static byte[] decodeBase64(String str) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        byte[] data = str.getBytes("US-ASCII");
        int len = data.length;
        int i = 0;
        int b1, b2, b3, b4;
        while (i < len) {
            /* b1 */
            do
                b1 = SimpleCode.base64DecodeChars[data[i++]];
            while (i < len && b1 == -1);
            if (b1 == -1)
                break;
            /* b2 */
            do
                b2 = SimpleCode.base64DecodeChars[data[i++]];
            while (i < len && b2 == -1);
            if (b2 == -1)
                break;
            sb.append((char) (b1 << 2 | (b2 & 0x30) >>> 4));
            /* b3 */
            do {
                b3 = data[i++];
                if (b3 == 61)
                    return sb.toString().getBytes("ISO-8859-1");
                b3 = SimpleCode.base64DecodeChars[b3];
            } while (i < len && b3 == -1);
            if (b3 == -1)
                break;
            sb.append((char) ((b2 & 0x0f) << 4 | (b3 & 0x3c) >>> 2));
            /* b4 */
            do {
                b4 = data[i++];
                if (b4 == 61)
                    return sb.toString().getBytes("ISO-8859-1");
                b4 = SimpleCode.base64DecodeChars[b4];
            } while (i < len && b4 == -1);
            if (b4 == -1)
                break;
            sb.append((char) ((b3 & 0x03) << 6 | b4));
        }
        return sb.toString().getBytes("ISO-8859-1");
    }
    /**
     * 取得字节数组的MD5码
     * @param source 字节数组
     * @return 返回MD5码
     */
    public static String parseMD5(byte[] source) {
        String s = null;
        // 用来将字节转换成 16 进制表示的字符
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest(); // MD5 的计算结果是一个 128
            // 位的长整数， 用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16
            // 进制表示的话，使用两个字符， 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对
                // MD5 的每一个字节转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高
                // 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低
                // 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}