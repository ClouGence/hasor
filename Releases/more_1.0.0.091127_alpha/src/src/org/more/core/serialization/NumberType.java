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
import java.util.regex.Pattern;
import org.more.CastException;
import org.more.util.StringConvert;
/**
 * 数字类型，该类型是more序列化类型的基本类型之一，在more中无论是byte还是int、long、float等都统一属于数字类型。
 * more在转换数字类型时，会优先考虑其数字的最短类型。这样保证在编程中尽可能的使用系统默认数字类型转换而不使用损失精度的强类型转换。
 * more序列化组建支持如下几种数据表示形式-12，13，+56.5，.234，+.159，-1.2对于23.这样的数据more序列化组建将认为是错误的数据格式。
 * 数字类型的原始类型名是“[More Number]”注意使用testObject和toString时参数必须传递Number类型的数据而不能是字符串或其他类型。
 * 并且当前版本more序列化组建不支持科学记数法表示的数字。
 * Date : 2009-7-7
 * @author 赵永春
 */
public final class NumberType extends BaseType {
    NumberType() {}
    @Override
    protected String getShortOriginalName() {
        return "Number";
    }
    @Override
    public boolean testString(String string) {
        // ^N\|(\+|-)?\d{0,}(\.\d+){0,}$
        //规定字符串格式
        if (Pattern.matches("^N\\|(\\+|-)?\\d{0,}(\\.\\d+){0,}$", string) == true)
            //不允许 N| 这种情况出现
            if (Pattern.matches("^N\\|.+$", string) == true)
                return true;
        return false;
    }
    @Override
    public boolean testObject(Object object) {
        return (object instanceof Number) ? true : false;
    }
    @Override
    public Object toObject(String string) throws CastException {
        //测试数据格式
        if (this.testString(string) == false)
            throw new CastException("无法反序列化数字，原始数据可能不完整或者类型错误。");
        else
            return StringConvert.parseNumber(string.substring(2), 0);
    }
    @Override
    public String toString(Object object) throws CastException {
        //测试数据格式
        if (this.testObject(object) == false)
            throw new CastException("不能执行序列化过程，目标对象不是一个有效的Number类型。");
        else
            return "N|" + object;
    }
}
