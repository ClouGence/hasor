/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package javax.activation;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @version $Rev: 467742 $ $Date: 2011/05/07 04:32:39 $
 */
public class MimeTypeParameterList {

    private final Map params = new HashMap();

    public MimeTypeParameterList() {
    }

    public MimeTypeParameterList(String parameterList) throws MimeTypeParseException {
        parse(parameterList);
    }

    protected void parse(String parameterList) throws MimeTypeParseException {
        if (parameterList == null) {
            throw new MimeTypeParseException("parameterList is null");
        }

        RFC2045Parser parser = new RFC2045Parser(parameterList);
        while (parser.hasMoreParams()) {
            String attribute = parser.expectAttribute();
            parser.expectEquals();
            String value = parser.expectValue();
            params.put(attribute.toLowerCase(), value);
        }
    }

    public int size() {
        return params.size();
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    public String get(String name) {
        return (String) params.get(name.toLowerCase());
    }

    public void set(String name, String value) {
        params.put(name.toLowerCase(), value);
    }

    public void remove(String name) {
        params.remove(name.toLowerCase());
    }

    public Enumeration getNames() {
        return Collections.enumeration(params.keySet());
    }

    /**
     * String representation of this parameter list.
     *
     * @return
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(params.size() << 4);
        for (Iterator i = params.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            buf.append("; ").append(entry.getKey()).append('=');
            quote(buf, (String) entry.getValue());
        }
        return buf.toString();
    }

    private void quote(StringBuffer buf, String value) {
        int length = value.length();
        boolean quote = false;
        for (int i = 0; i < length; i++) {
            if (MimeType.isSpecial(value.charAt(i))) {
                quote = true;
                break;
            }
        }
        if (quote) {
            buf.append('"');
            for (int i = 0; i < length; i++) {
                char c = value.charAt(i);
                if (c == '\\' || c == '"') {
                    buf.append('\\');
                }
                buf.append(c);
            }
            buf.append('"');
        } else {
            buf.append(value);
        }
    }

    private static class RFC2045Parser {
        private final String text;
        private int index = 0;

        private RFC2045Parser(String text) {
            this.text = text;
        }

        /**
         * Look the next ";" to start a parameter (skipping whitespace)
         *
         * @return
         */
        private boolean hasMoreParams() throws MimeTypeParseException {
            char c;
            do {
                if (index == text.length()) {
                    return false;
                }
                c = text.charAt(index++);
            } while (Character.isWhitespace(c));
            if (c != ';') {
                throw new MimeTypeParseException("Expected \";\" at " + (index - 1) + " in " + text);
            }
            return true;
        }

        private String expectAttribute() throws MimeTypeParseException {
            char c;
            do {
                if (index == text.length()) {
                    throw new MimeTypeParseException("Expected attribute at " + (index - 1) + " in " + text);
                }
                c = text.charAt(index++);
            } while (Character.isWhitespace(c));
            int start = index - 1;
            while (index != text.length() && !MimeType.isSpecial(text.charAt(index))) {
                index += 1;
            }
            return text.substring(start, index);
        }

        private void expectEquals() throws MimeTypeParseException {
            char c;
            do {
                if (index == text.length()) {
                    throw new MimeTypeParseException("Expected \"=\" at " + (index - 1) + " in " + text);
                }
                c = text.charAt(index++);
            } while (Character.isWhitespace(c));
            if (c != '=') {
                throw new MimeTypeParseException("Expected \"=\" at " + (index - 1) + " in " + text);
            }
        }

        private String expectValue() throws MimeTypeParseException {
            char c;
            do {
                if (index == text.length()) {
                    throw new MimeTypeParseException("Expected value at " + (index - 1) + " in " + text);
                }
                c = text.charAt(index++);
            } while (Character.isWhitespace(c));
            if (c == '"') {
                // quoted-string
                StringBuffer buf = new StringBuffer();
                while (true) {
                    if (index == text.length()) {
                        throw new MimeTypeParseException("Expected closing quote at " + (index - 1) + " in " + text);
                    }
                    c = text.charAt(index++);
                    if (c == '"') {
                        break;
                    }
                    if (c == '\\') {
                        if (index == text.length()) {
                            throw new MimeTypeParseException("Expected escaped char at " + (index - 1) + " in " + text);
                        }
                        c = text.charAt(index++);
                    }
                    buf.append(c);
                }
                return buf.toString();
            } else {
                // token
                int start = index - 1;
                while (index != text.length() && !MimeType.isSpecial(text.charAt(index))) {
                    index += 1;
                }
                return text.substring(start, index);
            }
        }
    }
}