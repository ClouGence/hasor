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
package org.more.core.json;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
public class JsonString extends JsonType {
    private static final Map<Character, String> charset = new HashMap<Character, String>(); ;
    static {
        JsonString.charset.put('"', "\\\"");
        JsonString.charset.put('\'', "\\\'");
        JsonString.charset.put('\\', "\\\\");
        JsonString.charset.put('/', "\\/");
        JsonString.charset.put('\b', "\\\b");
        JsonString.charset.put('\f', "\\\f");
        JsonString.charset.put('\n', "\\\n");
        JsonString.charset.put('\r', "\\\r");
        JsonString.charset.put('\t', "\\\t");
    }
    @Override
    public Object toObject(String str) {
        StringBuffer sb = new StringBuffer(str);
        int length = sb.length();
        sb.substring(1, length - 1);
        return sb.toString();
    }
    @Override
    public String toString(Object bean) {
        StringReader sr;
        if (bean instanceof String)
            sr = new StringReader((String) bean);
        else if (bean instanceof Character)
            sr = new StringReader(String.valueOf((Character) bean));
        else if (bean instanceof CharSequence)
            sr = new StringReader(((CharSequence) bean).toString());
        else
            throw new JsonException("无效字符串对象。");
        //
        StringBuffer sb = new StringBuffer("\"");
        try {
            while (true) {
                int c_read = sr.read();
                char c = (char) c_read;
                if (c_read == -1)
                    break;
                if (JsonString.charset.containsKey(c) == true)
                    sb.append(JsonString.charset.get(c));
                else
                    sb.append(c);
            }
        } catch (Exception e) {}
        sb.append("\"");
        return sb.toString();
    }
}