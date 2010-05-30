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
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
/**
 * 负责处理Array类型或者Collection类型的json格式互转。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class JsonArray extends JsonMixed {
    protected JsonArray(JsonUtil currentContext) {
        super(currentContext);
    };
    @Override
    public Object toObject(String str) {
        StringBuffer sb = new StringBuffer(str);
        if (sb.charAt(0) == '[') {} else
            throw new JsonException("不是一个有效的JSON格式array值。");
        //
        sb.deleteCharAt(0);
        sb.deleteCharAt(sb.length() - 1);
        List<Object> al = new LinkedList<Object>();
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
            //处理这个字符串数据的类型进行处理。
            al.add(this.passJsonObject(readStr));
        }
        return al.toArray();
    }
    @Override
    public String toString(Object bean) {
        StringBuffer json = new StringBuffer('[');
        if (bean instanceof Collection == true) {
            //Collection类型
            Collection coll = (Collection) bean;
            for (Object obj : coll)
                this.appendObject(json, obj);
        } else if (bean.getClass().isArray() == true) {
            //Array类型
            int length = Array.getLength(bean);
            for (int i = 0; i < length; i++)
                this.appendObject(json, Array.get(bean, i));
        } else
            throw new JsonException("JsonArray不能将一个非Collection类型或者数组类型对象转换为JSON格式array值。");
        /*-----*/
        int index = json.length() - 1;
        if (json.charAt(index) == ',')
            json.deleteCharAt(index);
        json.append(']');
        return json.toString();
    }
    private void appendObject(StringBuffer json, Object var) {
        json.append(this.passJsonString(var));
        json.append(',');
    }
}