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
package org.more.core.json.parser;
import org.more.core.json.JsonException;
import org.more.core.json.JsonParser;
import org.more.core.json.JsonUtil;
/**
 * 负责处理Boolean类型的json格式互转。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
public class JsonBoolean extends JsonParser {
    public JsonBoolean(JsonUtil currentContext) {
        super(currentContext);
    };
    public Object toObject(String str) {
        if (str.equals("true"))
            return true;
        else if (str.equals("false"))
            return false;
        else
            throw new JsonException("json数据非法不能作为boolean类型转换。");
    }
    public String toString(Object object) {
        if (object instanceof Boolean == false)
            throw new JsonException("JsonBoolean不能将一个布尔类型对象转换为JSON对应格式。");
        return (((Boolean) object) == true) ? "true" : "false";
    }
}