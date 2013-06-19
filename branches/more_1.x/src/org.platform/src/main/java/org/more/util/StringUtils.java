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
/**
 * 字符串工具。
 * @version 2010-9-19
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class StringUtils extends StringConvertUtils {
    /**转换首字母大写。*/
    public static String toUpperCase(String value) {
        StringBuffer sb = new StringBuffer(value);
        char firstChar = sb.charAt(0);
        sb.delete(0, 1);
        sb.insert(0, (char) ((firstChar >= 97) ? firstChar - 32 : firstChar));
        return sb.toString();
    }
    /**转换首字母小写。*/
    public static String toLowerCase(String value) {
        StringBuffer sb = new StringBuffer(value);
        char firstChar = sb.charAt(0);
        sb.delete(0, 1);
        sb.insert(0, (char) ((firstChar <= 90) ? firstChar + 32 : firstChar));
        return sb.toString();
    }
    /**将通配符转换成正则表达式。*/
    public static String wildToRegex(String wild) {
        if (wild == null)
            throw new NullPointerException("wild param is null");
        StringBuffer result = new StringBuffer("");
        char metachar[] = { '$', '^', '[', ']', '(', ')', '{', '|', '+', '.', '\\' };
        for (int i = 0; i < wild.length(); i++) {
            char ch = wild.charAt(i);
            for (int j = 0; j < metachar.length; j++)
                if (ch == metachar[j])
                    result.append("\\");
            if (ch == '*')
                result.append(".*");
            else if (ch == '?')
                result.append(".");
            else
                result.append(ch);
        }
        result.append("$");
        return result.toString();
    }
    /**将字符串转换成成正则表达式。*/
    public static String stringToRegex(String wild) {
        if (wild == null)
            throw new NullPointerException("wild param is null");
        StringBuffer result = new StringBuffer("");
        char metachar[] = { '$', '^', '[', ']', '(', ')', '{', '|', '+', '.', '\\' };
        for (int i = 0; i < wild.length(); i++) {
            char ch = wild.charAt(i);
            for (int j = 0; j < metachar.length; j++)
                if (ch == metachar[j])
                    result.append("\\");
            result.append(ch);
        }
        return result.toString();
    }
    /**使用通配符匹配字符串。*/
    public static boolean matchWild(String pattern, String str) {
        if (str == null)
            return false;
        return str.matches(wildToRegex(pattern));
    }
    /** 字符串转换成十六进制字符串 */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }
    /** 十六进制转换字符串 */
    public static String hexStr2Str(String hexStr) {
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
    public static String byte2HexStr(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            //if (n<b.length-1) hs=hs+":";
        }
        return hs.toUpperCase();
    }
    private static byte uniteBytes(String src0, String src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        byte b1 = Byte.decode("0x" + src1).byteValue();
        byte ret = (byte) (b0 | b1);
        return ret;
    }
    /** bytes转换成十六进制字符串 */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        //System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
        }
        return ret;
    }
    /** String的字符串转换成unicode的String */
    public static String stringToUnicode(String strText) throws Exception {
        char c;
        String strRet = "";
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
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
    public static String unicodeToString(String hex) {
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
    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
    /**忽略大小写敏感匹配字符串*/
    public static boolean eqUnCaseSensitive(String str1, String str2) {
        if (str1 == null && str2 == null)
            return true;
        if ((str1 == null && str2 != null) || (str1 != null && str2 == null))
            return false;
        //
        String s1 = str1.toLowerCase();
        String s2 = str2.toLowerCase();
        return s1.equals(s2);
    }
};