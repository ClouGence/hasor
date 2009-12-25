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
/**
 * boolean类型数据，more序列化组建的基本类型之一在more的序列化类型中没有包装类型的概念。
 * 任何为空的类型都是空类型。而BooleanType类表示的是一个特定布尔类型值这个值可以是true也可以是false。
 * more序列化组建中的原始类型名是“[More Boolean]”。
 * Date : 2009-7-7
 * @author 赵永春
 */
public final class BooleanType extends BaseType {
    BooleanType() {}
    @Override
    protected String getShortOriginalName() {
        return "Boolean";
    }
    @Override
    public boolean testString(String string) {
        // ^B|(true|false)$
        return Pattern.matches("^B\\|(true|false)$", string);
    }
    @Override
    public boolean testObject(Object object) {
        //测试数据是否为boolean。并且对象不能为null。
        if (object == null || object instanceof Boolean == false)
            return false;
        else
            return true;
    }
    @Override
    public Object toObject(String string) throws CastException {
        //测试数据格式
        if (this.testString(string) == false)
            throw new CastException("无法反转数据！原始数据格式错误或者不完整，无法反序列化字符串为Boolean类型。");
        else if (string.indexOf("true") != -1)
            //如果值中包含true则代表true
            return true;
        else
            return false;
    }
    @Override
    public String toString(Object object) throws CastException {
        if (this.testObject(object) == false)
            throw new CastException("不能执行转换，目标格式不是一个有效的Boolean类型对象因此不能使用BooleanType类进行序列化。");
        else
            return "B|" + object;
    }
}
