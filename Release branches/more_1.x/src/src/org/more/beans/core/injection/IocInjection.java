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
package org.more.beans.core.injection;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.more.beans.BeanFactory;
import org.more.beans.core.TypeParser;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProperty;
import org.more.util.StringConvert;
/**
 * 传统的Ioc反射注入方式，这种方式使用java.lang.reflect包中的类进行反射调用来实现依赖注入。
 * 在IocInjection类中属性写入方法是由set + 属性名(首字母大写) 定义的这个方法当被查找到之后会
 * 被缓存在{@link BeanProperty}中。<br/>
 * Date : 2009-11-7
 * @author 赵永春
 */
public class IocInjection implements Injection {
    //========================================================================================Field
    /** 属性缓存对象，缓存属性名。 */
    private String propCatchName = "$more_Injection_Ioc";
    //==========================================================================================Job
    /** 使用set + 属性名(首字母大写)名称来查找目标反射注入方法。 */
    @Override
    public void ioc(Object object, Object[] params, BeanDefinition definition, BeanFactory context) throws Exception {
        BeanProperty[] bps = definition.getPropertys();
        if (bps == null)
            return;
        ClassLoader loader = context.getBeanClassLoader();
        for (int i = 0; i < bps.length; i++) {
            BeanProperty prop = bps[i];
            Method writeMethod = null;
            //这个if可以提升7倍的运行速度，BeanDefinition的资源对象必须拥有缓存功能的前提下。
            if (prop.containsKey(this.propCatchName) == false) {
                //转换首字母大写
                StringBuffer sb = new StringBuffer(prop.getName());
                char firstChar = sb.charAt(0);
                sb.delete(0, 1);
                firstChar = (char) ((firstChar >= 97) ? firstChar - 32 : firstChar);
                sb.insert(0, firstChar);
                sb.insert(0, "set");
                writeMethod = object.getClass().getMethod(sb.toString(), this.getType(prop, context, loader));
                prop.setAttribute(this.propCatchName, writeMethod);
            } else
                writeMethod = (Method) prop.get(this.propCatchName);
            writeMethod.invoke(object, this.getValue(object, params, prop, context));
        }
    }
    /** 获取属性注入器 */
    private Class<?> getType(BeanProperty prop, BeanFactory context, ClassLoader loader) throws ClassNotFoundException {
        String propString = prop.getPropType();
        if (propString == BeanProperty.TS_Integer)
            return int.class;
        else if (propString == BeanProperty.TS_Byte)
            return byte.class;
        else if (propString == BeanProperty.TS_Char) {
            return char.class;
        } else if (propString == BeanProperty.TS_Double)
            return double.class;
        else if (propString == BeanProperty.TS_Float)
            return float.class;
        else if (propString == BeanProperty.TS_Long)
            return long.class;
        else if (propString == BeanProperty.TS_Short)
            return short.class;
        else if (propString == BeanProperty.TS_Boolean)
            return boolean.class;
        else if (propString == BeanProperty.TS_String)
            return String.class;
        else if (propString == BeanProperty.TS_Array)
            return Object[].class;
        else if (propString == BeanProperty.TS_List)
            return List.class;
        else if (propString == BeanProperty.TS_Map)
            return Map.class;
        else if (propString == BeanProperty.TS_Set)
            return Set.class;
        else
            return loader.loadClass(propString);
    }
    /** 根据BeanProperty获得属性的值 */
    private Object getValue(Object object, Object[] params, BeanProperty prop, BeanFactory context) {
        String propType = prop.getPropType();
        if (propType == BeanProperty.TS_Integer)
            return StringConvert.parseInt(prop.getValue());
        else if (propType == BeanProperty.TS_Byte)
            return StringConvert.parseByte(prop.getValue());
        else if (propType == BeanProperty.TS_Char) {
            if (prop.getValue() == null)
                return (char) 0;
            else
                return prop.getValue().charAt(0);
        } else if (propType == BeanProperty.TS_Double)
            return StringConvert.parseDouble(prop.getValue());
        else if (propType == BeanProperty.TS_Float)
            return StringConvert.parseFloat(prop.getValue());
        else if (propType == BeanProperty.TS_Long)
            return StringConvert.parseLong(prop.getValue());
        else if (propType == BeanProperty.TS_Short)
            return StringConvert.parseShort(prop.getValue());
        else if (propType == BeanProperty.TS_Boolean)
            return StringConvert.parseBoolean(prop.getValue());
        else if (propType == BeanProperty.TS_String)
            return prop.getValue();
        else if (propType == BeanProperty.TS_Array)
            return TypeParser.passerArray(object, params, prop, context);
        else if (propType == BeanProperty.TS_List)
            return TypeParser.passerList(object, params, prop, context);
        else if (propType == BeanProperty.TS_Map)
            return TypeParser.passerMap(object, params, prop, context);
        else if (propType == BeanProperty.TS_Set)
            return TypeParser.passerSet(object, params, prop, context);
        else if (prop.getRefBean() != null)
            return context.getBean(prop.getRefBean(), params);
        else
            return TypeParser.passerType(object, params, prop, context);
    }
}
