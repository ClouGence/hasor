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
package org.more.core.serialization;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.CastException;
import org.more.util.Base64;
/**
 * 字符串类型，在more的序列化组建所支持的类型中字符串和字符是一种类型都是字符串。只是字符串长度不一致而已。
 * 字符串类型在more序列化组建的原始类型名是“[More String]”，more可以将以下对象直接序列化为字符串，
 * String、Character、StringBuffer、StringBuilder，在more序列化字符串时会将字符串进行base64编码然后封装。
 * base64字符串的作用是放置特殊字符破坏序列化格式，比方说回车换行，制表符和逗号以及引号等常见符号。
 * Date : 2009-7-7
 * @author 赵永春
 */
public final class StringType extends BaseType {
    StringType() {}
    @Override
    protected String getShortOriginalName() {
        return "String";
    }
    @Override
    public boolean testString(String string) {
        // ^S|".*"$
        return Pattern.matches("^S\\|\".*\"$", string);
    }
    @Override
    public boolean testObject(Object object) {
        if (object == null)
            //null
            return false;
        else if (object instanceof Character)
            //string|char
            return true;
        else if (object instanceof CharSequence)
            //string|char buffer,string
            return true;
        else
            return false;
    }
    @Override
    public Object toObject(String string) throws CastException {
        if (this.testString(string) == false)
            throw new CastException("无法反序列化字符串类型数据，可能序列化数据不完整或者序列化数据不是字符串类型。");
        Pattern p = Pattern.compile("^S\\|\"(.*)\"$");
        Matcher m = p.matcher(string);
        m.find();
        return Base64.base64Decode(m.group(1));
    }
    @Override
    public String toString(Object object) throws CastException {
        if (this.testObject(object) == false)
            throw new CastException("无法序列化目标对象，请确保序列化的对象是以下类型之一：String、Character、StringBuffer、StringBuilder，并且保证数据不能为null。");
        else
            return "S|\"" + Base64.base64Encode(object.toString()) + "\"";
    }
}
