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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.more.core.json.JsonException;
import org.more.core.json.JsonUtil;
import org.more.util.attribute.IAttribute;
/**
 * 负责处理Object类型的json格式互转。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
public class JsonObject extends JsonMixed {
    public JsonObject(JsonUtil currentContext) {
        super(currentContext);
    };
    /**分离KV，这个函数是专门分离 {}:{}类型的或者[]:{}等组合*/
    protected int kvIndex(String str) {
        int depth = 0;
        //获取最近的一个属性值
        for (int i = 0; i < str.length(); i++) {
            char s_temp = str.charAt(i);
            if (s_temp == ':' && depth == 0)
                return i;
            else if (s_temp == '[' || s_temp == '{')
                depth++;
            else if (s_temp == ']' || s_temp == '}')
                depth--;
        }
        return -1;
    }
    public Object toObject(String str) {
        StringBuffer sb = new StringBuffer(str);
        if (sb.charAt(0) == '{') {} else
            throw new JsonException("不是一个有效的JSON格式Object值。");
        //
        sb.deleteCharAt(0);
        sb.deleteCharAt(sb.length() - 1);
        Map<Object, Object> map = new HashMap<Object, Object>();
        //循环处理集合中的数据体
        while (true) {
            //当处理完最后一个数据之后返回的值就是null或者空字符串，一旦出现上述一种情况就跳出循环。
            if (sb.toString().equals("") == true || sb == null)
                break;
            //读取第一条属性。
            String readStr = this.readJSONString(sb.toString());
            //将读取的属性从context_data中删除
            if (sb.length() == readStr.length())
                sb = sb.delete(0, readStr.length());
            else
                sb = sb.delete(0, readStr.length() + 1);
            //分离key-Value,如果在分离KV时候返回了-1则会引发数组下标越界异常
            int firstIndex = this.kvIndex(readStr);
            String key = readStr.substring(0, firstIndex);
            String value = readStr.substring(firstIndex + 1);
            //处理这个字符串数据的类型进行处理。
            JsonUtil json = this.getCurrentContext();
            try {
                map.put(json.toObject(key), json.toObject(value));
            } catch (Exception e) {
                map.put(key, json.toObject(value));
            }
        }
        return map;
    }
    public String toString(Object bean) {
        StringBuffer json = new StringBuffer("{");
        if (bean instanceof IAttribute) {
            IAttribute<Object> att = (IAttribute<Object>) bean;
            String[] ns = att.getAttributeNames();
            for (int i = 0; i < ns.length; i++)
                this.appendObject(json, ns[i], att.getAttribute(ns[i]));
        } else if (bean instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) bean;
            Set<?> ns = map.keySet();
            for (Object key : ns)
                this.appendObject(json, key, map.get(key));
        } else {
            Class<?> type = bean.getClass();
            //FIXME:修改为BeanUtil。
            Map<String, Field> fields = new HashMap<String, Field>();
            //
            for (Field f : type.getFields())
                fields.put(f.getName(), f);
            for (Field f : type.getDeclaredFields())
                fields.put(f.getName(), f);
            //
            for (Field f : fields.values())
                this.appendField(bean, f, json);
        }
        /*-----*/
        int index = json.length() - 1;
        if (json.charAt(index) == ',')
            json.deleteCharAt(index);
        json.append("}");
        return json.toString();
    }
    private void appendField(Object bean, Field field, StringBuffer json) {
        Class<?> type = bean.getClass();
        try {
            int access = field.getModifiers();
            if ((access | Modifier.PUBLIC) == access)
                this.appendObject(json, field.getName(), field.get(bean)); //直接访问字段
            else {
                //转换首字母大写
                StringBuffer sb = new StringBuffer(field.getName());
                char firstChar = sb.charAt(0);
                sb.delete(0, 1);
                sb.insert(0, (char) ((firstChar >= 97) ? firstChar - 32 : firstChar));
                sb.insert(0, "get");
                //通过get/set访问
                Method m = type.getMethod(sb.toString());
                this.appendObject(json, field.getName(), m.invoke(bean));
            }
        } catch (Exception e) {}
    };
    private void appendObject(StringBuffer jsonStr, Object key, Object var) {
        JsonUtil json = this.getCurrentContext();
        jsonStr.append(json.toString(key));
        jsonStr.append(":");
        jsonStr.append(json.toString(var));
        jsonStr.append(",");
    }
}