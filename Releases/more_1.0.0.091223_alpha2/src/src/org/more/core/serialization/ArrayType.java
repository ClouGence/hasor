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
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.CastException;
/**
 * 集合类型，该类型是more数据类型中的基本类型之一，作为集合类型在java中主要有两种表现形式，
 * 一种是数组类型，另一种是Collection接口对象。Collection接口对象包括了List,Set,Queue类型
 * 在序列化集合类型对象时ArrayType类会自动到more序列化系统中查找集合相应元素的类型进行对应类型的
 * 序列化。在more序列化系统中集合类型的原始类型名是“[More Array]”。
 * Date : 2009-7-7
 * @author 赵永春
 */
public final class ArrayType extends BaseType {
    ArrayType() {}
    @Override
    protected String getShortOriginalName() {
        return "Array";
    }
    @Override
    public boolean testString(String string) {
        // ^A|[.*]$
        return Pattern.matches("^A\\|\\[.*\\](:.*)?$", string);
    }
    @Override
    public boolean testObject(Object object) {
        if (object == null)
            return false;
        else
            return object.getClass().isArray() || Collection.class.isAssignableFrom(object.getClass());
    }
    @Override
    @SuppressWarnings("unchecked")
    public Object toObject(String string) throws CastException {
        if (this.testString(string) == false)
            throw new CastException("无法执行反序列化，在检测数据格式时失败，可能序列化数据数据不是集合类型或者序列化数据不完整。");
        else {
            Pattern p = Pattern.compile("^A\\|\\[(.*)\\](:(.*))?$");
            Matcher m = p.matcher(string);
            m.find();
            StringBuffer context_data = new StringBuffer(m.group(1));//表数据体
            String userType = (m.groupCount() == 3) ? m.group(3) : null;//表数据体所依托的数据类型。
            //1.试图装载依托数据类型，如果无法装载依托的数据则使用Map进行替代。
            Collection<Object> al = null;
            if ("A".equals(userType))
                al = new ArrayList<Object>();
            else {
                try {
                    al = (Collection<Object>) Class.forName(userType).newInstance();
                } catch (Exception e) {
                    al = new ArrayList<Object>();
                }
            }
            //2.循环处理集合中的数据体
            while (true) {
                //当处理完最后一个数据之后返回的值就是null或者空字符串，一旦出现上述一种情况就跳出循环。
                if (context_data.toString().equals("") == true || context_data == null)
                    break;
                //读取第一条属性。
                String readStr = this.readString(context_data.toString());
                //将读取的属性从context_data中删除
                if (context_data.length() == readStr.length())
                    context_data = context_data.delete(0, readStr.length());
                else
                    context_data = context_data.delete(0, readStr.length() + 1);
                //查找可以处理这个字符串数据的类型进行处理。
                Object obj = super.findType(readStr).toObject(readStr);
                //将处理结果添加到集合中。
                al.add(obj);
            }
            //返回处理的结果。
            if ("A".equals(userType))
                return al.toArray();
            else
                return al;
        }
    }
    @Override
    @SuppressWarnings("unchecked")
    public String toString(Object object) throws CastException {
        if (this.testObject(object) == false)
            throw new CastException("不能执行转换，目标对象类型不是一个有效的集合类型，合法的集合类型是数组或者Collection接口的实现类。");
        else {
            String result = "";//用于存放序列化结果
            Collection al = null;//用于存放需要序列化的数据，这些数据被封装到集合中进行跌待。
            String type = object.getClass().getName();
            if (object.getClass().isArray() == true) {
                //如果是树组则将树组对象放入集合中等待处理
                al = new ArrayList<Object>();
                Object[] objs = (Object[]) object;
                for (Object o : objs)
                    al.add(o);
                type = "A";
            } else
                //如果是集合则直接转换
                al = (Collection) object;
            //处理最末尾的逗号
            for (Object obj : al)
                result += BaseType.findType(obj).toString(obj) + ",";
            //拼出序列化数据并且返回。
            if (result.length() > 0)
                result = result.substring(0, result.length() - 1);
            return "A|[" + result + "]:" + type;
        }
    }
    private String readString(String str) {
        String returnS = "";
        int depth = 0;
        //获取最近的一个属性值
        for (int i = 0; i < str.length(); i++) {
            char s_temp = str.charAt(i);
            if (s_temp == ',' && depth == 0)
                return returnS;
            else if (s_temp == '[' || s_temp == '{')
                depth++;
            else if (s_temp == ']' || s_temp == '}')
                depth--;
            returnS += s_temp;
        }
        return returnS;
    }
}
