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
package net.hasor.rsf.utils;
import java.security.NoSuchAlgorithmException;
/**
 *
 * @version : 2012-6-21
 * @author 赵永春 (zyc@hasor.net)
 */
public class CommonCodeUtils {
    /**
     * 简单编码类，该类提供了Base64的编码，该编码器与配套的js编码器可以实现互相编码互相转化功能。
     * @version 2009-4-28
     * @author 赵永春 (zyc@hasor.net)
     */
    public static abstract class Base64 {
        protected static final String Base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@*-"; // supplement
        /***/
        //    /**返回与Base64对应的JSbase64编码/解码脚本*/
        //    public static Reader getJSReader() throws UnsupportedEncodingException {
        //        return new InputStreamReader(Base64.class.getResourceAsStream("/META-INF/resource/util/base64.js"), "utf-8");
        //    };
        /**
         * 使用UTF-8编码进行Base64编码
         * @param s 要编码的原始数据
         * @return 返回编码之后的字符串。
         */
        public static String base64Encode(final String s) {
            if (s == null || s.length() == 0) {
                return s;
            }
            byte[] b = null;
            try {
                b = s.getBytes("UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                e.printStackTrace();
                return s;
            }
            return Base64.base64EncodeFoArray(b);
        }
        /** Encoding a byte array to a string follow the Base64 regular. */
        public static String base64EncodeFoArray(final byte[] s) {
            if (s == null) {
                return null;
            }
            if (s.length == 0) {
                return "";
            }
            StringBuffer buf = new StringBuffer();
            int b0, b1, b2, b3;
            int len = s.length;
            int i = 0;
            while (i < len) {
                byte tmp = s[i++];
                b0 = (tmp & 0xfc) >> 2;
                b1 = (tmp & 0x03) << 4;
                if (i < len) {
                    tmp = s[i++];
                    b1 |= (tmp & 0xf0) >> 4;
                    b2 = (tmp & 0x0f) << 2;
                    if (i < len) {
                        tmp = s[i++];
                        b2 |= (tmp & 0xc0) >> 6;
                        b3 = tmp & 0x3f;
                    } else {
                        b3 = 64; // 1 byte "-" is supplement
                    }
                } else {
                    b2 = b3 = 64;// 2 bytes "-" are supplement
                }
                buf.append(Base64.Base64Chars.charAt(b0));
                buf.append(Base64.Base64Chars.charAt(b1));
                buf.append(Base64.Base64Chars.charAt(b2));
                buf.append(Base64.Base64Chars.charAt(b3));
            }
            return buf.toString();
        }
        /** Decoding a string to a string follow the Base64 regular. */
        public static String base64Decode(final String s) {
            byte[] b = Base64.base64DecodeToArray(s);
            if (b == null) {
                return null;
            }
            if (b.length == 0) {
                return "";
            }
            try {
                return new String(b, "UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
        /** Decoding a string to a byte array follow the Base64 regular */
        public static byte[] base64DecodeToArray(final String s) {
            if (s == null) {
                return null;
            }
            int len = s.length();
            if (len == 0) {
                return new byte[0];
            }
            if (len % 4 != 0) {
                throw new IllegalArgumentException(s);
            }
            byte[] b = new byte[len / 4 * 3];
            int i = 0, j = 0, e = 0, c, tmp;
            while (i < len) {
                c = Base64.Base64Chars.indexOf(s.charAt(i++));
                tmp = c << 18;
                c = Base64.Base64Chars.indexOf(s.charAt(i++));
                tmp |= c << 12;
                c = Base64.Base64Chars.indexOf(s.charAt(i++));
                if (c < 64) {
                    tmp |= c << 6;
                    c = Base64.Base64Chars.indexOf(s.charAt(i++));
                    if (c < 64) {
                        tmp |= c;
                    } else {
                        e = 1;
                    }
                } else {
                    e = 2;
                    i++;
                }
                b[j + 2] = (byte) (tmp & 0xff);
                tmp >>= 8;
                b[j + 1] = (byte) (tmp & 0xff);
                tmp >>= 8;
                b[j + 0] = (byte) (tmp & 0xff);
                j += 3;
            }
            if (e != 0) {
                len = b.length - e;
                byte[] copy = new byte[len];
                System.arraycopy(b, 0, copy, 0, len);
                return copy;
            }
            return b;
        }
    }
    /**
     * MD5算法提供
     * @version : 2011-11-7
     * @author 赵永春 (zyc@hasor.net)
     */
    public static abstract class MD5 {
        public static String encodeMD5(final byte[] source) throws NoSuchAlgorithmException {
            String s = null;
            // 用来将字节转换成 16 进制表示的字符
            char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串
            return s;
        }
        public static String getMD5(final String source) throws NoSuchAlgorithmException {
            return MD5.getMD5(source.getBytes());
        }
        public static String getMD5(final byte[] source) throws NoSuchAlgorithmException {
            String s = null;
            // 用来将字节转换成 16 进制表示的字符
            char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串
            return s;
        }
    }
    /**
     * 基于十六进制的字符转换
     * @version : 2013-8-13
     * @author 赵永春 (zyc@hasor.net)
     */
    public static abstract class HexConversion {
        /** 字符串转换成十六进制字符串 */
        public static String str2HexStr(final String str) {
            char[] chars = "0123456789ABCDEF".toCharArray();
            StringBuilder sb = new StringBuilder("");
            byte[] bs = str.getBytes();
            int bit;
            for (byte element : bs) {
                bit = (element & 0x0f0) >> 4;
                sb.append(chars[bit]);
                bit = element & 0x0f;
                sb.append(chars[bit]);
            }
            return sb.toString();
        }
        /** 十六进制转换字符串 */
        public static String hexStr2Str(final String hexStr) {
            String str = "0123456789ABCDEF";
            char[] hexs = hexStr.toCharArray();
            byte[] bytes = new byte[hexStr.length() / 2];
            int n;
            for (int i = 0; i < bytes.length; i++) {
                n = str.indexOf(hexs[2 * i]) * 16;
                n += str.indexOf(hexs[2 * i + 1]);
                bytes[i] = (byte) (n & 0xff);
            }
            return new String(bytes);
        }
        /** bytes转换成十六进制字符串 */
        public static String byte2HexStr(final byte[] b) {
            String hs = "";
            String stmp = "";
            for (byte element : b) {
                stmp = Integer.toHexString(element & 0XFF);
                if (stmp.length() == 1) {
                    hs = hs + "0" + stmp;
                } else {
                    hs = hs + stmp;
                    //if (n<b.length-1) hs=hs+":";
                }
            }
            return hs.toUpperCase();
        }
        /** bytes转换成十六进制字符串 */
        public static byte[] hexStr2Bytes(final String src) {
            int m = 0, n = 0;
            int l = src.length() / 2;
            //System.out.println(l);
            byte[] ret = new byte[l];
            for (int i = 0; i < l; i++) {
                m = i * 2 + 1;
                n = m + 1;
                ret[i] = HexConversion.uniteBytes(src.substring(i * 2, m), src.substring(m, n));
            }
            return ret;
        }
        /** String的字符串转换成unicode的String */
        public static String stringToUnicode(final String strText) throws Exception {
            char c;
            String strRet = "";
            int intAsc;
            String strHex;
            for (int i = 0; i < strText.length(); i++) {
                c = strText.charAt(i);
                intAsc = c;
                strHex = Integer.toHexString(intAsc);
                if (intAsc > 128) {
                    strRet += "\\u" + strHex;
                } else {
                    // 低位在前面补00
                    strRet += "\\u00" + strHex;
                }
            }
            return strRet;
        }
        /** unicode的String转换成String的字符串 */
        public static String unicodeToString(final String hex) {
            int t = hex.length() / 6;
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < t; i++) {
                String s = hex.substring(i * 6, (i + 1) * 6);
                // 高位需要补上00再转
                String s1 = s.substring(2, 4) + "00";
                // 低位直接转
                String s2 = s.substring(4);
                // 将16进制的string转为int
                int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
                // 将int转换为字符
                char[] chars = Character.toChars(n);
                str.append(new String(chars));
            }
            return str.toString();
        }
        private static byte uniteBytes(final String src0, final String src1) {
            byte b0 = Byte.decode("0x" + src0).byteValue();
            b0 = (byte) (b0 << 4);
            byte b1 = Byte.decode("0x" + src1).byteValue();
            byte ret = (byte) (b0 | b1);
            return ret;
        }
    }
}