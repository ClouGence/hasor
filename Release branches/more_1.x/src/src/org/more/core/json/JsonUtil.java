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
import org.more.core.copybean.CopyBeanUtil;
/**
 * 属性stringBorder是用于决定字符串序列化时使用单引号“'”或者双引号“"”(默认值)。
 * JsonUtil在序列化字符串对象时支持了String、Character、CharSequence、Reader这些类型。
 * 到目前版本为止JsonUtil不支持科学计数法表示的数字。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class JsonUtil {
    private char stringBorder = 34; //34:", 39:'
    /**创建JsonUtil对象，字符串序列化使用双引号环抱。*/
    public JsonUtil() {};
    /**创建JsonUtil对象，字符串序列化使用参数决定。只有当设置了单引号“'”或者双引号“"”时候才有效，如果设置其他字符则会使用默认字符。*/
    public JsonUtil(char stringBorder) {
        this.setStringBorder(stringBorder);
    };
    /**创建JsonUtil对象，字符串序列化使用参数决定。只有当设置了单引号“'”或者双引号“"”时候才有效，如果设置其他字符则会使用默认字符。*/
    public JsonUtil(int stringBorder) {
        this.setStringBorder((char) stringBorder);
    };
    /**创建JsonUtil对象，字符串序列化使用参数决定。只有当设置了单引号“'”或者双引号“"”时候才有效，如果设置其他字符则会使用默认字符。*/
    public JsonUtil(String stringBorder) {
        if (stringBorder != null)
            this.setStringBorder(stringBorder.charAt(0));
    };
    /**获取当将对象序列化为json数据时字符串被环抱的形式是以单引号还是双引号。(默认是双引号)  */
    public char getStringBorder() {
        return this.stringBorder;
    };
    /**设置当将对象序列化为json数据时字符串被环抱的形式是以单引号还是双引号，只有当设置了单引号“'”或者双引号“"”时候才有效，如果设置其他字符则会忽略设置。*/
    public void setStringBorder(char stringBorder) {
        if (stringBorder == 34 || stringBorder == 39)
            this.stringBorder = stringBorder;
    };
    /**将json数据转换为指定的类型结构。*/
    public Object toObject(String str, Class<?> superType) throws Throwable {
        Object obj = superType.newInstance();
        Map<?, ?> data = this.toMap(str);
        return CopyBeanUtil.newInstance().copy(data, obj, "value");
    }
    /**将json数据转换为map形式，如果json数据仅仅表示一个对象而非json Object类型则这个json对象将被封装到返回的map中它的key是空字符串*/
    public Map<String, ?> toMap(String str) {
        String readStr = str.trim();
        if (readStr == null || readStr.equals(""))
            return null;
        /*----------------------------*/
        Object obj = this.passJsonString(readStr);
        if (obj instanceof Map == false) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("", obj);
            obj = map;
        }
        return (HashMap<String, Object>) obj;
    }
    /**将对象数据转换为map形式，该方法首先将对象转换为json然后在转换成map*/
    public Map<String, ?> toMap(Object value) {
        String readStr = this.toString(value);
        return this.toMap(readStr);
    }
    /**将对象转换为json格式数据，注意如果对象中出现递归引用则会引发堆栈溢出异常。*/
    public String toString(Object object) {
        //处理这个字符串数据的类型进行处理。
        if (object == null)
            return "null";
        else if (object instanceof Boolean)
            return (((Boolean) object) == true) ? "true" : "false";
        else if (object instanceof String || object instanceof Character || object instanceof CharSequence)
            return new JsonString(this).toString(object);
        else if (object instanceof Collection || object.getClass().isArray() == true)
            return new JsonArray(this).toString(object);
        else if (object instanceof Number)
            return new JsonNumber(this).toString(object);
        else
            return new JsonObject(this).toString(object);
    }
    /**解析一个json数据为对象。*/
    private Object passJsonString(String readStr) {
        if (readStr.equals("null"))
            return null;
        else if (readStr.equals("true"))
            return true;
        else if (readStr.equals("false"))
            return false;
        else if (readStr.charAt(0) == 34 || readStr.charAt(0) == 39)
            return new JsonString(this).toObject(readStr);
        else if (readStr.charAt(0) == '[')
            return new JsonArray(this).toObject(readStr);
        else if (readStr.charAt(0) == '{')
            return new JsonObject(this).toObject(readStr);
        else
            return new JsonNumber(this).toObject(readStr);
    }
}