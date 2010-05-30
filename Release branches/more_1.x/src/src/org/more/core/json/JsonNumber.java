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
import org.more.util.StringConvert;
/**
 * 负责处理Number类型的json格式互转。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
public class JsonNumber extends JsonType {
    protected JsonNumber(JsonUtil currentContext) {
        super(currentContext);
    };
    @Override
    public Object toObject(String str) {
        return StringConvert.parseNumber(str, 0);
    }
    @Override
    public String toString(Object bean) {
        if (bean instanceof Number == true)
            return bean.toString();
        else
            throw new JsonException("JsonNumber不能将一个非数字类型对象转换为JSON对应格式。");
    }
}