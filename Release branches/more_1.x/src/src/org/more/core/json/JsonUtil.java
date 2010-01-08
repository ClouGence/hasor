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
import java.util.HashMap;
import java.util.Map;
/**
 * 负责处理Array类型或者Collection类型的json格式互转。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class JsonUtil {
    //    public Object toObject(String str) {
    //        return null;
    //    }
    //    /**将json数据转换为指定的类型结构。*/
    //    public Object toObject(String str, Class<?> superType) {
    //        return null;
    //    }
    /**将json数据转换为map形式，如果json数据仅仅表示一个对象而非json Object类型则这个json对象将被封装到返回的map中它的key是空字符串*/
    public Map toMap(String str) {
        String readStr = str.trim();
        if (readStr == null || readStr.equals(""))
            return null;
        /*----------------------------*/
        Object obj = this.passJsonString(readStr);
        if (obj instanceof Map == false) {
            HashMap map = new HashMap();
            map.put("", obj);
            obj = map;
        }
        return (Map) obj;
    }
    /**将对象转换为json格式数据，注意如果对象中出现递归引用则会引发堆栈溢出异常。*/
    public String toString(Object object) {
        //处理这个字符串数据的类型进行处理。
        if (object == null)
            return "null";
        else if (object instanceof Boolean)
            return (((Boolean) object) == true) ? "true" : "false";
        else if (object instanceof String || object instanceof Character || object instanceof CharSequence)
            return new JsonString().toString(object);
        else if (object instanceof Collection || object.getClass().isArray() == true)
            return new JsonArray().toString(object);
        else if (object instanceof Number)
            return new JsonNumber().toString(object);
        else
            return new JsonObject().toString(object);
    }
    /**解析一个json数据为对象。*/
    private Object passJsonString(String readStr) {
        if (readStr.equals("null"))
            return null;
        else if (readStr.equals("true"))
            return true;
        else if (readStr.equals("false"))
            return false;
        else if (readStr.charAt(0) == '\"' || readStr.charAt(0) == '\'')
            return new JsonString().toObject(readStr);
        else if (readStr.charAt(0) == '[')
            return new JsonArray().toObject(readStr);
        else if (readStr.charAt(0) == '{')
            return new JsonObject().toObject(readStr);
        else
            return new JsonNumber().toObject(readStr);
    }
}