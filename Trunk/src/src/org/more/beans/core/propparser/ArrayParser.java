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
package org.more.beans.core.propparser;
import java.lang.reflect.Array;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProp;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.Prop;
import org.more.beans.info.PropArray;
/**
 * 对类型为{@link PropArray}的属性定义提供解析支持，如果{@link PropArray}没有配置具体数组类型则使用{@link Object}作为数组类型。
 * @version 2009-11-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class ArrayParser implements PropertyParser {
    @Override
    public Object parser(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception {
        PropArray p = (PropArray) prop;
        Class<?> arrayType = this.parserType(context, contextParams, prop, propContext, definition, factory, contextParser);
        Object returnObject = Array.newInstance(arrayType.getComponentType(), p.getLength());
        //三、循环遍历数组元素定义以处理各个元素。
        BeanProp[] element = p.getArrayElements();
        for (int i = 0; i < element.length; i++) {
            BeanProp e = element[i];
            Object itemObject = null;
            if (e instanceof PropArray)
                itemObject = this.parser(context, contextParams, e, propContext, definition, factory, contextParser);
            else
                itemObject = contextParser.parser(context, contextParams, e, propContext, definition, factory, contextParser);
            Array.set(returnObject, i, itemObject);
        }
        return returnObject;
    }
    @Override
    public Class<?> parserType(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception {
        /*
         * propType属性规则。
         * 1.如果PropArray配置了propType属性则直接返回，否则返回propContext的propType属性。
         * 2.如果propContext也没有配置propType属性则返回java.lang.Object。
         */
        String propType = prop.getPropType();
        if (propType == null)
            propType = propContext.getPropType();
        if (propType == null)
            propType = "java.lang.Object";
        Class<?> arrayType = Prop.getType(propType, factory.getBeanClassLoader());
        return Array.newInstance(arrayType, 1).getClass();
    }
}
