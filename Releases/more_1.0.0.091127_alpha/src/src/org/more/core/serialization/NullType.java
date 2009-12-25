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
 * null是more序列化组建的基本类型之一。而NullType类就是处理值为空的类型转换。
 * 无论任何类型只要值为空则就可以使用该类型进行数据序列化和反序列化。
 * 该类型的原始类型名是“[More Void]”。
 * Date : 2009-7-7
 * @author 赵永春
 */
public final class NullType extends BaseType {
    NullType() {}
    @Override
    protected String getShortOriginalName() {
        return "Void";
    }
    @Override
    public boolean testString(String string) {
        // ^V|void$
        return Pattern.matches("^V\\|void$", string);
    }
    @Override
    public boolean testObject(Object object) {
        //如果对象是空值则返回true否则返回false
        return (object == null) ? true : false;
    }
    @Override
    public Object toObject(String string) throws CastException {
        //测试数据是否正确
        if (this.testString(string) == false)
            throw new CastException("无法反转数据！原始数据格式错误或者不完整，无法反序列化字符串为void类型。");
        return null;
    }
    @Override
    public String toString(Object object) throws CastException {
        //测试数据是否正确
        if (this.testObject(object) == false)
            throw new CastException("类型转换异常，不能将非空的类型转换成空的类型。");
        return "V|void";
    }
}
