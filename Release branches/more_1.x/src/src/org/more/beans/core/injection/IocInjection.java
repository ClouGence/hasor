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
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.core.propparser.MainPropertyParser;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.IocTypeEnum;
/**
 * 该类是实现了{@link IocTypeEnum#Ioc Ioc}反射注入方式，这种方式使用java.lang.reflect包中的类进行反射调用来实现依赖注入。
 * 在IocInjection类中属性写入方法是由set + 属性名(首字母大写) 定义的这个方法当被查找到之后会
 * 被缓存在{@link BeanProperty}中。
 * @version 2009-11-7
 * @author 赵永春 (zyc@byshell.org)
 */
public class IocInjection implements Injection {
    //========================================================================================Field
    /** 属性缓存对象，缓存属性名。 */
    private String             propCatchName = "$more_Injection_Ioc";
    /**属性解析器*/
    private MainPropertyParser propParser    = null;
    //==================================================================================Constructor
    /**创建一个IocInjection对象，创建时必须指定属性解析器。*/
    public IocInjection(MainPropertyParser propParser) {
        if (propParser == null)
            throw new NullPointerException("必须指定propParser参数对象，IocInjection使用这个属性解析器解析属性。");
        this.propParser = propParser;
    }
    //==========================================================================================Job
    /** 使用set + 属性名(首字母大写)名称来查找目标反射注入方法。 */
    @Override
    public Object ioc(Object object, Object[] params, BeanDefinition definition, ResourceBeanFactory context) throws Exception {
        BeanProperty[] bps = definition.getPropertys();
        if (bps == null)
            return object;
        for (int i = 0; i < bps.length; i++) {
            BeanProperty prop = bps[i];
            Method writeMethod = null;
            //这个if可以提升7倍的运行速度，BeanDefinition的资源对象必须拥有缓存功能的前提下。
            if (prop.containsKey(this.propCatchName) == false) {
                //转换首字母大写
                StringBuffer sb = new StringBuffer(prop.getName());
                char firstChar = sb.charAt(0);
                sb.delete(0, 1);
                sb.insert(0, (char) ((firstChar >= 97) ? firstChar - 32 : firstChar));
                sb.insert(0, "set");
                Class<?> mt = this.propParser.parserType(context, params, prop.getRefValue(), prop, definition);
                writeMethod = object.getClass().getMethod(sb.toString(), mt);
                prop.setAttribute(this.propCatchName, writeMethod);
            } else
                writeMethod = (Method) prop.get(this.propCatchName);
            //
            Object obj = propParser.parser(object, params, prop.getRefValue(), prop, definition);
            writeMethod.invoke(object, obj);
        }
        return object;
    }
}