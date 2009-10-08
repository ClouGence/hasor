/*
 * Copyright 2008-2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.more.core.serialization;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.CastException;
/**
 * 表类型，该类型是more序列化系统中最丰富的基本数据类型。表类型的原始结构可以看作是一个树。其中每一个数节点可以是任意的more序列化数据类型。
 * 由此可以组合成无限深的表树。从而达到复杂数据对象的目的。但是对于实际的语言平台对象比方说时间日期。就需要特殊的表进行处理，然后在进行序列化。
 * 表类型也可以存放开发人员自定义数据类型。但是对于反序列化时需要创建相应的自定义数据类型对象因此这部分数据会在序列化扩展位置上进行保存。
 * 如果在一个平台上序列化之后在另外的一个平台上反序列化找不到指定类型或者无法装载指定类型这时more系列化系统讲以表的形式进行反序列化，具体表现形式
 * 为一个Map对象。表类型对象的原始类型名是“[More Table]”
 * Date : 2009-7-8
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public final class TableType extends BaseType {
    TableType() {}
    @Override
    protected String getShortOriginalName() {
        return "Table";
    }
    @Override
    public boolean testString(String string) {
        // ^T|{}(:org.test.User)?$
        return Pattern.matches("^T\\|\\{.*\\}(:.*)?$", string);
    }
    // T|{}:org.UserInfo
    @Override
    public boolean testObject(Object object) {
        return true;
    }
    @Override
    public Object toObject(String string) throws CastException {
        if (this.testString(string) == false)
            throw new CastException("原始数据可能受到破坏或者不是一个表结构因此无法反序列化字符串为表类型。");
        else {
            Pattern p = Pattern.compile("^T\\|\\{(.*)\\}(:(.*))?$");
            Matcher m = p.matcher(string);
            m.find();
            StringBuffer context_data = new StringBuffer(m.group(1));//表数据体
            String userType = (m.groupCount() == 3) ? m.group(3) : null;//表数据体所依托的数据类型。
            //1.试图装载依托数据类型，如果无法装载依托的数据则使用Map进行替代。
            Object object = null;
            try {
                Class<?> type = Class.forName(userType);
                object = type.newInstance();
            } catch (Exception e) {
                object = new HashMap();
            }
            //2.循环处理表中的数据体
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
                //分离key-Value
                String key = readStr.substring(0, readStr.indexOf("="));
                String value = readStr.substring(key.length() + 1, readStr.length());
                //查找可以处理这个字符串数据的类型进行处理。
                BaseType bt = BaseType.findType(value);
                if (bt == null)
                    bt = this;
                Object obj = bt.toObject(value);
                //将处理结果添加到集合中。
                this.setValue(object, key, obj);
            }
            //4.返回处理结果
            return object;
        }
    }
    @Override
    public String toString(Object object) throws CastException {
        if (this.testObject(object) == false)
            throw new CastException("目标对象已经有其他数据类型处理器进行处理表类型不能处理已经有处理器可以处理的对象，表类型可以处理Map，和任意类型对象但是任意类型对象必须是没有其他数据类型处理对象可以处理的。");
        else {
            String result = "";
            String type = (object instanceof Map) ? null : object.getClass().getName();
            //序列化对象
            Object[] ns = this.getNS(object);
            for (Object n : ns) {
                Object value = this.getValue(object, n.toString());
                BaseType bt = BaseType.findType(value);
                if (bt == null)
                    bt = this;
                result += n + "=" + bt.toString(value) + ",";
            }
            //处理最后一个逗号
            if (ns.length > 0)
                result = result.substring(0, result.length() - 1);
            //
            if (type == null)
                //默认表结构
                return "T|{" + result + "}";
            else
                //附带自定义数据类型
                return "T|{" + result + "}:" + type;
        }
    }
    // ====================================================
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
    //获取目标对象的属性名集合。
    @SuppressWarnings("unchecked")
    private Object[] getNS(Object obj) {
        if (obj instanceof Map) {
            Map obj_map = (Map) obj;
            return obj_map.keySet().toArray();
        } else {
            ArrayList<String> al = new ArrayList<String>();
            for (Field f : obj.getClass().getDeclaredFields())
                al.add(f.getName());
            return al.toArray();
        }
    }
    //从目标读取某个属性
    @SuppressWarnings( { "unused", "unchecked" })
    private Object getValue(Object object, String ns) {
        if (object instanceof Map) {
            return ((Map) object).get(ns);
        } else
            try {
                PropertyDescriptor pd = new PropertyDescriptor(ns, object.getClass());
                return pd.getReadMethod().invoke(object);
            } catch (Exception e) {
                return null;
            }
    }
    //向目标设置某个属性
    @SuppressWarnings( { "unused", "unchecked" })
    private void setValue(Object object, String name, Object value) {
        if (object instanceof Map) {
            ((Map) object).put(name, value);
        } else
            try {
                PropertyDescriptor pd = new PropertyDescriptor(name, object.getClass());
                pd.getWriteMethod().invoke(object, value);
            } catch (Exception e) {}
    }
}
