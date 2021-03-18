/**
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.hasor.db.dal.fxquery;
import java.util.HashMap;
import java.util.Map;

/**
 * 原版在 mybatis 中同名类，本类改造了 openToken 允许是一个数组。另外 TokenHandler 接口增加了2个参数。
 * @author Clinton Begin
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-12
 */
class GenericTokenParser {
    private final String[]     openToken;
    private final String       closeToken;
    private final TokenHandler handler;

    public GenericTokenParser(String[] openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    private static class CurrentOpenToken {
        public String openToken = null;
        public int    start     = -1;
    }

    private CurrentOpenToken findOpenToken(String text, int offset) {
        Map<String, Integer> statusMap = new HashMap<>();
        for (String token : this.openToken) {
            statusMap.put(token, text.indexOf(token, offset));
        }
        //
        Map.Entry<String, Integer> entry = statusMap.entrySet().stream()//
                .sorted(Map.Entry.comparingByValue())    //
                .filter(ent -> ent.getValue() > -1)//
                .findFirst().orElse(null);
        //
        CurrentOpenToken info = new CurrentOpenToken();
        info.openToken = this.openToken[0];
        info.start = -1;
        if (entry == null) {
            return info;
        } else {
            info.openToken = entry.getKey();
            info.start = entry.getValue();
            return info;
        }
    }

    public String parse(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        // search open token
        CurrentOpenToken token = findOpenToken(text, 0);
        if (token.start == -1) {
            return text;
        }
        char[] src = text.toCharArray();
        int offset = 0;
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (token.start > -1) {
            if (token.start > 0 && src[token.start - 1] == '\\') {
                // this open token is escaped. remove the backslash and continue.
                builder.append(src, offset, token.start - offset - 1).append(token.openToken);
                offset = token.start + token.openToken.length();
            } else {
                // found open token. let's search close token.
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, token.start - offset);
                offset = token.start + token.openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if (end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        break;
                    }
                }
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, token.start, src.length - token.start);
                    offset = src.length;
                } else {
                    builder.append(handler.handleToken(builder, token.openToken, expression.toString()));
                    offset = end + closeToken.length();
                }
            }
            token = findOpenToken(text, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }
}
