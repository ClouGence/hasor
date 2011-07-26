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
import java.util.Collection;
/**
 * 为提供Json格式数据互转的基类。该类可以直接处理null类型。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class JsonType {
    private JsonUtil currentContext;
    protected JsonUtil getCurrentContext() {
        return currentContext;
    };
    protected JsonType(JsonUtil currentContext) {
        this.currentContext = currentContext;
    };
    /**转换对象为json字符串*/
    public abstract String toString(Object bean);
    /**将字符串转换为json对象*/
    public abstract Object toObject(String str);
    /**将字符串变换为object*/
    protected Object passJsonObject(String readStr) {
        //处理这个字符串数据的类型进行处理。
        if (readStr != null) {
            readStr = readStr.trim();
            if (readStr.equals("null"))
                return null;
            else if (readStr.equals("true") || readStr.equals("false"))
                return new JsonBoolean(this.currentContext).toObject(readStr);
            else if (readStr.charAt(0) == 34 || readStr.charAt(0) == 39)
                return new JsonString(this.currentContext).toObject(readStr);
            else if (readStr.charAt(0) == '[')
                return new JsonArray(this.currentContext).toObject(readStr);
            else if (readStr.charAt(0) == '{')
                return new JsonObject(this.currentContext).toObject(readStr);
            else if (readStr.charAt(0) >= 30 && readStr.charAt(0) <= 39)
                return new JsonNumber(this.currentContext).toObject(readStr);
            else
                return new JsonString(this.currentContext).toObject(readStr);
        } else
            throw new JsonException("json数据字符串不能为空。");
    };
    /**将对象变换为json数据*/
    protected String passJsonString(Object object) {
        //处理这个字符串数据的类型进行处理。
        if (object == null)
            return "null";
        else if (object instanceof Boolean)
            return new JsonBoolean(this.currentContext).toString(object);
        else if (object instanceof String || object instanceof Character || object instanceof CharSequence)
            return new JsonString(this.currentContext).toString(object);
        else if (object instanceof Collection || object.getClass().isArray() == true)
            return new JsonArray(this.currentContext).toString(object);
        else if (object instanceof Number)
            return new JsonNumber(this.currentContext).toString(object);
        else
            return new JsonObject(this.currentContext).toString(object);
    };
};