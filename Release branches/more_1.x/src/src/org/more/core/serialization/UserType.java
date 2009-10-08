/*
 * Copyright 2008-2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.more.core.serialization;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.CastException;
/**
 * 用户自定义类型，该类型是为了解决特定类型序列化而设立。时间日期型就是一种用户自定义类型，
 * 用户自定义类型必须指定类型。用户自定义类型对象的原始类型名是“[More User_xxx]”
 * Date : 2009-7-8
 * @author 赵永春
 */
public abstract class UserType extends BaseType {
    @Override
    protected String getShortOriginalName() {
        return "User_" + this.getUserOriginalName();
    }
    @Override
    public boolean testString(String string) {
        // ^U|{}:org.test.User$
        return Pattern.matches("^U\\|\\{.*\\}:" + this.getType() + "$", string);
    }
    @Override
    public Object toObject(String string) throws CastException {
        if (this.testString(string) == false)
            throw new CastException("原始数据可能受到破坏或者不是一个表结构因此无法反序列化字符串为用户自定义类型。");
        else {
            Pattern p = Pattern.compile("^U\\|\\{(.*)\\}:(.*)$");
            Matcher m = p.matcher(string);
            m.find();
            String date = m.group(1);
            String type = m.group(2);
            return this.deserialize(date, type);
        }
    }
    @Override
    public String toString(Object object) throws CastException {
        if (this.testObject(object) == false)
            throw new CastException("目标对象已经有其他数据类型处理器进行处理表类型不能处理已经有处理器可以处理的对象，用户自定义类型必须是语言平台上已经定义的数据类型。");
        else
            return "U|{" + this.serialization(object) + "}:" + this.getType();
    }
    // ====================================================
    /**
     * 该方法是被保护的方法，该方法返回类型的具体类型名。该方法的返回值将被getShortOriginalName调用。
     * getShortOriginalName方法会自动组合类型原始名。
     * @return 返回类型的具体类型名。
     */
    protected abstract String getUserOriginalName();
    /**
     * 获取对象的类型的匹配正则表达试，该类型在最终序列化时候会追加到序列化对象的类型部分，也用于在反序列化时候匹配数据类型。
     * @return 返回对象的类型，该类型在最终序列化时候会追加到序列化对象的类型部分。
     */
    protected abstract String getType();
    /**
     * 序列化对象。该方法直接序列化对象成为字符串就可以不需考虑序列化之后的数据在自定义数据类型中的格式问题。
     * @param object 要序列化的对象
     * @return 返回序列化结果。
     * @throws CastException 当执行数据转换时发生异常。
     */
    protected abstract String serialization(Object object) throws CastException;
    /**
     * 执行反序列化过程。
     * @param date 要反序列化的字符串数据。
     * @param type 这个数据的原始数据类型。
     * @return 返回反序列化之后的结果。
     * @throws CastException 当执行数据转换时发生异常。
     */
    protected abstract Object deserialize(String date, String type) throws CastException;
}
