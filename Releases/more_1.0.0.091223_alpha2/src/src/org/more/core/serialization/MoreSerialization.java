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
import org.more.CastException;
/**
 * more序列化和反序列化工具类，通过使用该类静态方法可以实现将任意对象进行序列化为more字符串。
 * 也可以通过该类的toObject方法将字符串反序列化为对象。
 * Date : 2009-7-8
 * @author 赵永春
 */
public class MoreSerialization {
    /**
     * 将对象序列化。返回序列化之后的more字符串。
     * @param object 要序列化的对象
     * @return 返回序列化之后的more字符串。
     * @throws CastException 在序列化过程中发生异常。
     */
    public static String toString(Object object) throws CastException {
        BaseType type = BaseType.findType(object);
        if (type == null)
            throw new CastException(object.getClass() + "不是一个受支持的类型。More无法序列化这个类型的对象。");
        else
            return type.toString(object);
    }
    /**
     * 将more序列化字符串反序列化为对象。返回反序列化之后的对象。
     * @param data 兑现序列化字符串
     * @return 返回反序列化之后的对象。
     * @throws CastException 在反序列化过程中发生异常。
     */
    public static Object toObject(String data) throws CastException {
        BaseType type = BaseType.findType(data);
        if (type == null) {
            String shortString = (data.length() > 20) ? data.substring(0, 20) + "..." : data;
            throw new CastException("由于More中没有匹配的处理类型所以More无法反序列化这个字符串[" + shortString + "]");
        } else
            return type.toObject(data);
    }
}
