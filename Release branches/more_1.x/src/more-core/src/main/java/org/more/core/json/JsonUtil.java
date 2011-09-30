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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.more.core.copybean.CopyBeanUtil;
import org.more.core.error.InitializationException;
import org.more.core.error.SupportException;
import org.more.core.global.Global;
/**
 * 属性stringBorder是用于决定字符串序列化时使用单引号“'”或者双引号“"”(默认值)。
 * JsonUtil在序列化字符串对象时支持了String、Character、CharSequence、Reader这些类型。
 * 到目前版本为止JsonUtil不支持科学计数法表示的数字。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class JsonUtil {
    /**默认字符串，34:", 39:'*/
    public static char                           StringBorder = '"';
    public static final String[]                 configs      = new String[] { "json_config.properties", "META-INF/json_config.properties", "META-INF/resource/core/json_config.properties" };
    //
    private char                                 stringBorder = JsonUtil.StringBorder;
    private LinkedHashMap<JsonCheck, JsonParser> jsonTypes    = new LinkedHashMap<JsonCheck, JsonParser>();
    /**创建JsonUtil对象，字符串序列化使用双引号环抱。 */
    protected JsonUtil() throws Exception {
        Global global = Global.newInstance("properties", configs);
        int _globalCount = global.getInteger("_global.count");
        //1.整理index
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < _globalCount; i++) {
            /*获取装载了多少个配置文件*/
            Object index = global.getObject("_global[" + i + "].index");
            if (index == null || index.getClass().isArray() == false)
                continue;//如果这个配置文件下不包含index或者该属性是一个非法的类型则忽略这个配置文件
            Object[] $index = (Object[]) index;
            for (Object obj : $index)
                if (names.contains(obj) == false)
                    names.add(obj.toString());
        }
        //2.装载index
        for (String name : names) {
            if (name == null || name.equals("") == true)
                continue;
            String _check = global.getString(name + "_Check");
            String _parser = global.getString(name + "_Parser");
            if (_check == null || _check.equals("") == true || _parser == null || _parser.equals("") == true)
                continue;
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> $_check = loader.loadClass(_check);
            Class<?> $_parser = loader.loadClass(_parser);
            JsonCheck $$_check = (JsonCheck) $_check.newInstance();
            JsonParser $$_parser = (JsonParser) $_parser.getConstructor(JsonUtil.class).newInstance(this);
            this.addType($$_check, $$_parser);
        }
        this.stringBorder = global.getChar("useDoubleBorder", JsonUtil.StringBorder);
    };
    /**添加一种类型的解析，添加的类型解析是被追加进去的。TODO 改为插入方式*/
    private void addType(JsonCheck check, JsonParser parser) {
        this.jsonTypes.put(check, parser);
    }
    /**获取当将对象序列化为json数据时字符串被环抱的形式是以单引号还是双引号。(默认是双引号)  */
    public char getStringBorder() {
        return this.stringBorder;
    };
    /**设置当将对象序列化为json数据时字符串被环抱的形式是以单引号还是双引号，只有当设置了单引号“'”或者双引号“"”时候才有效，如果设置其他字符则会忽略设置。*/
    public void setStringBorder(char stringBorder) {
        if (stringBorder == 34 || stringBorder == 39)
            this.stringBorder = stringBorder;
    };
    /**将json数据转换为map形式。*/
    public Map<?, ?> toMap(String str) {
        String readStr = str.trim();
        if (readStr == null || readStr.equals(""))
            return null;
        /*----------------------------*/
        Object obj = this.toObject(readStr);
        if (obj instanceof Map == false) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("", obj);
            obj = map;
        }
        return (Map<?, ?>) obj;
    }
    /**将对象数据转换为map形式，该方法首先将对象转换为json然后在转换成map*/
    public Map<?, ?> toMap(Object value) {
        String readStr = this.toString(value);
        return this.toMap(readStr);
    }
    /**将json数据转换为指定的类型结构。*/
    public Object toObject(String str, Class<?> superType) throws InstantiationException, IllegalAccessException {
        Object obj = superType.newInstance();
        Object data = this.toObject(str);
        return CopyBeanUtil.newInstance().copy(data, obj);
    }
    /**将json数据转换为object形式，如果json数据是一个数组则返回数组对象，如果是一个对象则返回一个map*/
    public Object toObject(String str) {
        String readStr = str.trim();
        for (JsonCheck jsonCheck : this.jsonTypes.keySet())
            if (jsonCheck.checkToObject(readStr) == true)
                return this.jsonTypes.get(jsonCheck).toObject(readStr);
        throw new SupportException("'" + readStr + "' is not supported to object.");
    }
    /**将对象转换为json格式数据，注意如果对象中出现递归引用则会引发堆栈溢出异常。*/
    public String toString(Object object) {
        for (JsonCheck jsonCheck : this.jsonTypes.keySet())
            if (jsonCheck.checkToString(object) == true)
                return this.jsonTypes.get(jsonCheck).toString(object);
        throw new SupportException(object.getClass() + " is not supported to json format");
    }
    /*---------------------------------------------------------------------------------*/
    private static JsonUtil defaultUtil = null;
    /**无论如何都创建一个新的JsonUtil实例。*/
    public static JsonUtil newInstance() {
        try {
            return new JsonUtil() {};
        } catch (Exception e) {
            throw new InitializationException(e);
        }
    };
    /**获取一个JsonUtil实例，该方法返回上一次调用该方法创建的实例对象。*/
    public static JsonUtil getJsonUtil() {
        if (defaultUtil == null)
            defaultUtil = newInstance();
        return defaultUtil;
    };
    /**将对象转换为json格式数据，注意如果对象中出现递归引用则会引发堆栈溢出异常。*/
    public static String transformToJson(Object dataBean) {
        return getJsonUtil().toString(dataBean);
    };
    /**将json数据转换为object形式，如果json数据是一个数组则返回数组对象，如果是一个对象则返回一个map*/
    public static Object transformToObject(String jsonData) {
        return getJsonUtil().toObject(jsonData);
    };
    /**将json数据转换为指定的类型结构。*/
    public static Object transformToObject(String jsonData, Class<?> toType) throws InstantiationException, IllegalAccessException {
        return getJsonUtil().toObject(jsonData, toType);
    };
    /**将json数据转换为map形式。*/
    public static Map<?, ?> transformToMap(String jsonData) {
        return getJsonUtil().toMap(jsonData);
    }
    /**将对象数据转换为map形式，该方法首先将对象转换为json然后在转换成map*/
    public static Map<?, ?> transformToMap(Object dataBean) {
        return getJsonUtil().toMap(dataBean);
    }
}