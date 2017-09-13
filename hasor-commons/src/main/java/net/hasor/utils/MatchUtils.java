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
package net.hasor.utils;
/**
 *
 * @version : 2014年7月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class MatchUtils {
    public static enum MatchTypeEnum {
        /**通配符：通过：? 和 * 进行匹配。*/
        Wild, /**正则表达式匹配。*/
        Regex
    }
    /**将通配符转换成正则表达式。*/
    public static String wildToRegex(final String wild) {
        if (wild == null) {
            throw new NullPointerException("wild param is null");
        }
        StringBuffer result = new StringBuffer("");
        char metachar[] = { '$', '^', '[', ']', '(', ')', '{', '|', '+', '.', '\\' };
        for (int i = 0; i < wild.length(); i++) {
            char ch = wild.charAt(i);
            for (char element : metachar) {
                if (ch == element) {
                    result.append("\\");
                }
            }
            if (ch == '*') {
                result.append(".*");
            } else if (ch == '?') {
                result.append(".");
            } else {
                result.append(ch);
            }
        }
        result.append("$");
        return result.toString();
    }
    /**使用通配符匹配字符串。*/
    public static boolean matchWild(final String pattern, final String str) {
        if (str == null) {
            return false;
        }
        return str.matches(MatchUtils.wildToRegex(pattern));
    }
    //
    /**将通配符转换成正则表达式。*/
    public static boolean wildToRegex(final String pattern, final String str, MatchTypeEnum matchType) {
        if (MatchTypeEnum.Regex == matchType) {
            return str.matches(pattern);
        } else if (MatchTypeEnum.Wild == matchType) {
            return matchWild(pattern, str);
        }
        return false;
    }
}