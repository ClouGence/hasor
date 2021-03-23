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
package net.hasor.dataql.fx.basic;
import net.hasor.core.Singleton;
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串函数 <code>import 'net.hasor.dataql.fx.basic.StringUdfSource' as string;</code>
 * @version : 2019-12-12
 */
@Singleton
public class StringUdfSource implements UdfSourceAssembly {
    // startsWith/endsWith
    //-----------------------------------------------------------------------

    /** Check if a String starts with a specified prefix. */
    public static boolean startsWith(String str, String prefix) {
        return _startsWith(str, prefix, false);
    }

    /** Case insensitive check if a String starts with a specified prefix. */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return _startsWith(str, prefix, true);
    }

    /** Case insensitive check if a String ends with a specified suffix. */
    public static boolean endsWith(String str, String suffix) {
        return _endsWith(str, suffix, false);
    }

    /** Case insensitive check if a String ends with a specified suffix. */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return _endsWith(str, suffix, true);
    }

    private static boolean _startsWith(String str, String prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == null && prefix == null;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
    }

    private static boolean _endsWith(String str, String suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return str == null && suffix == null;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    // Hump
    //-----------------------------------------------------------------------
    private static final Pattern linePattern = Pattern.compile("_(\\w)");
    private static final Pattern humpPattern = Pattern.compile("[A-Z]");

    /** 下划线转驼峰 */
    public static String lineToHump(String str) {
        if (str == null) {
            return null;
        }
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /** 驼峰转下划线 */
    public static String humpToLine(String str) {
        if (str == null) {
            return null;
        }
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    // UpperCase/LowerCase
    //-----------------------------------------------------------------------

    /** 转换首字母大写 */
    public static String firstCharToUpperCase(String value) {
        return StringUtils.firstCharToUpperCase(value);
    }

    /** 转换首字母小写 */
    public static String firstCharToLowerCase(String value) {
        return StringUtils.firstCharToLowerCase(value);
    }

    /** 转换大写 */
    public static String toUpperCase(String value) {
        return StringUtils.upperCase(value);
    }

    /** 转换小写 */
    public static String toLowerCase(String value) {
        return StringUtils.lowerCase(value);
    }
    // IndexOf/lastIndexOf
    //-----------------------------------------------------------------------

    /** Finds the first index within a String, handling <code>null</code>. This method uses {@link String#indexOf(String)}. */
    public static int indexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return StringUtils.INDEX_NOT_FOUND;
        }
        return str.indexOf(searchStr);
    }

    /** Finds the first index within a String from a start position, handling <code>null</code>. This method uses {@link String#indexOf(String, int)}. */
    public static int indexOfWithStart(String str, String searchStr, int startPos) {
        if (StringUtils.isEmpty(str)) {
            return StringUtils.INDEX_NOT_FOUND;
        }
        return str.indexOf(searchStr, startPos);
    }

    /** Case in-sensitive find of the first index within a String. */
    public static int indexOfIgnoreCase(String str, String searchStr) {
        return StringUtils.indexOfIgnoreCase(str, searchStr, 0);
    }

    /** Case in-sensitive find of the first index within a String from the specified position. */
    public static int indexOfIgnoreCaseWithStart(String str, String searchStr, int startPos) {
        return StringUtils.indexOfIgnoreCase(str, searchStr, startPos);
    }

    /** Finds the last index within a String, handling <code>null</code>. This method uses {@link String#lastIndexOf(String)}. */
    public static int lastIndexOf(String str, String searchStr) {
        if (StringUtils.isEmpty(str)) {
            return StringUtils.INDEX_NOT_FOUND;
        }
        return str.lastIndexOf(searchStr);
    }

    /** Finds the last index within a String from a start position, handling <code>null</code>. This method uses {@link String#lastIndexOf(String, int)}. */
    public static int lastIndexOfWithStart(String str, String searchChar, int startPos) {
        if (StringUtils.isEmpty(str)) {
            return StringUtils.INDEX_NOT_FOUND;
        }
        return str.lastIndexOf(searchChar, startPos);
    }

    /** Case in-sensitive find of the last index within a String from the specified position. */
    public static int lastIndexOfIgnoreCase(String str, String searchStr) {
        return StringUtils.lastIndexOfIgnoreCase(str, searchStr, 0);
    }

    /** Case in-sensitive find of the last index within a String from the specified position. */
    public static int lastIndexOfIgnoreCaseWithStart(String str, String searchStr, int startPos) {
        return StringUtils.lastIndexOfIgnoreCase(str, searchStr, startPos);
    }
    // Contains
    //-----------------------------------------------------------------------

    /** Checks if String contains a search String, handling <code>null</code>. This method uses {@link String#indexOf(String)}. */
    public static boolean contains(String str, String searchStr) {
        return StringUtils.contains(str, searchStr);
    }

    /** Checks if String contains a search String irrespective of case, handling <code>null</code>. Case-insensitivity is defined as by {@link String#equalsIgnoreCase(String)}. */
    public static boolean containsIgnoreCase(String str, String searchStr) {
        return StringUtils.containsIgnoreCase(str, searchStr);
    }

    /** Checks if the String contains any character in the given set of string. */
    public static boolean containsAny(String str, List<String> searchStrArray) {
        if (StringUtils.isEmpty(str) || searchStrArray == null || searchStrArray.isEmpty()) {
            return false;
        }
        for (String item : searchStrArray) {
            if (contains(str, item)) {
                return true;
            }
        }
        return false;
    }

    /** Case in-sensitive Checks if the String contains any character in the given set of string. */
    public static boolean containsAnyIgnoreCase(String str, List<String> searchStrArray) {
        if (StringUtils.isEmpty(str) || searchStrArray == null || searchStrArray.isEmpty()) {
            return false;
        }
        for (String item : searchStrArray) {
            if (containsIgnoreCase(str, item)) {
                return true;
            }
        }
        return false;
    }
    // trim/Sub/left/right
    //-----------------------------------------------------------------------

    /** 截断两边空格，如果为空返回为空。 */
    public static String trim(final String str) {
        return str == null ? null : str.trim();
    }

    /** Gets a substring from the specified String avoiding exceptions. */
    public static String sub(String str, int start, int end) {
        return StringUtils.substring(str, start, end);
    }

    /** Gets the leftmost <code>len</code> characters of a String. */
    public static String left(String str, int len) {
        return StringUtils.left(str, len);
    }

    /** Gets the rightmost <code>len</code> characters of a String. */
    public static String right(String str, int len) {
        return StringUtils.right(str, len);
    }
    // align/pading
    //-----------------------------------------------------------------------

    /** 字符串在指定长度下进行右对齐，空出来的字符使用padChar补齐。如果传入多个字符将会取第一个字符。 */
    public static String alignRight(String str, String padChar, int len) {
        if (str != null) {
            if (str.length() > len) {
                return str;
            }
        }
        char pad = padChar.length() == 0 ? ' ' : padChar.charAt(0);
        return StringUtils.rightPad(str, len, pad);
    }

    /** 字符串在指定长度下进行左对齐，空出来的字符使用padChar补齐。如果传入多个字符将会取第一个字符。 */
    public static String alignLeft(String str, String padChar, int len) {
        if (str != null) {
            if (str.length() > len) {
                return str;
            }
        }
        char pad = padChar.length() == 0 ? ' ' : padChar.charAt(0);
        return StringUtils.leftPad(str, len, pad);
    }

    /** 字符串在指定长度下进行剧中对齐，空出来的字符使用padChar补齐。如果传入多个字符将会取第一个字符。 */
    public static String alignCenter(String str, String padChar, int len) {
        if (str != null) {
            if (str.length() > len) {
                return str;
            }
        }
        char pad = padChar.length() == 0 ? ' ' : padChar.charAt(0);
        return StringUtils.center(str, len, pad);
    }
    // compare
    //-----------------------------------------------------------------------

    /**
     * Compares two strings lexicographically.
     * The comparison is based on the Unicode value of each character in
     * the strings. The character sequence represented by this
     * {@code String} object is compared lexicographically to the
     * character sequence represented by the argument string. The result is
     * a negative integer if this {@code String} object
     * lexicographically precedes the argument string. The result is a
     * positive integer if this {@code String} object lexicographically
     * follows the argument string. The result is zero if the strings
     * are equal; {@code compareTo} returns {@code 0} exactly when
     * the {@link #equals(Object)} method would return {@code true}.
     * <p>
     * This is the definition of lexicographic ordering. If two strings are
     * different, then either they have different characters at some index
     * that is a valid index for both strings, or their lengths are different,
     * or both. If they have different characters at one or more index
     * positions, let <i>k</i> be the smallest such index; then the string
     * whose character at position <i>k</i> has the smaller value, as
     * determined by using the &lt; operator, lexicographically precedes the
     * other string. In this case, {@code compareTo} returns the
     * difference of the two character values at position {@code k} in
     * the two string -- that is, the value:
     * <blockquote><pre>
     * this.charAt(k)-anotherString.charAt(k)
     * </pre></blockquote>
     * If there is no index position at which they differ, then the shorter
     * string lexicographically precedes the longer string. In this case,
     * {@code compareTo} returns the difference of the lengths of the
     * strings -- that is, the value:
     * <blockquote><pre>
     * this.length()-anotherString.length()
     * </pre></blockquote>
     *
     * @param   str1   the {@code String} to be compared.
     * @param   str2   the {@code String} to be compared.
     * @return the value {@code 0} if the argument string is equal to
     *          this string; a value less than {@code 0} if this string
     *          is lexicographically less than the string argument; and a
     *          value greater than {@code 0} if this string is
     *          lexicographically greater than the string argument.
     */
    public static int compareString(String str1, String str2) {
        str1 = str1 == null ? "" : str1;
        str2 = str2 == null ? "" : str2;
        return str1.compareTo(str2);
    }

    /**
     * Compares two strings lexicographically, ignoring case
     * differences. This method returns an integer whose sign is that of
     * calling {@code compareTo} with normalized versions of the strings
     * where case differences have been eliminated by calling
     * {@code Character.toLowerCase(Character.toUpperCase(character))} on
     * each character.
     * <p>
     * Note that this method does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The java.text package provides <em>collators</em> to allow
     * locale-sensitive ordering.
     *
     * @param   str1   the {@code String} to be compared.
     * @param   str2   the {@code String} to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *          specified String is greater than, equal to, or less
     *          than this String, ignoring case considerations.
     * @see     java.text.Collator#compare(String, String)
     * @since 1.2
     */
    public static int compareStringIgnoreCase(String str1, String str2) {
        str1 = str1 == null ? "" : str1;
        str2 = str2 == null ? "" : str2;
        return str1.compareToIgnoreCase(str2);
    }
    // other
    //-----------------------------------------------------------------------

    /**
     * <p>Splits the provided text into an array, separators specified.
     * This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.</p>
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("abc def", null) = ["abc", "def"]
     * StringUtils.split("abc def", " ")  = ["abc", "def"]
     * StringUtils.split("abc  def", " ") = ["abc", "def"]
     * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChars  the characters used as the delimiters, <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static List<String> split(String str, String separatorChars) {
        return Arrays.asList(StringUtils.split(str, separatorChars));
    }

    /** Joins the elements of the provided array into a single String containing the provided list of elements. */
    public static String join(List<Object> array, String separator) {
        if (array == null) {
            return null;
        }
        return StringUtils.join(array.toArray(), separator);
    }

    /** Checks if a String is empty ("") or null.*/
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /** 忽略大小写比较相等 */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }
}
