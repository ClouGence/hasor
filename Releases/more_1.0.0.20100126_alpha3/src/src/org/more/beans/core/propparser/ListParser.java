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
import java.util.ArrayList;
import java.util.List;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProp;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.PropList;
/**
 * 对类型为{@link PropList}的属性定义提供解析支持，如果{@link PropList}没有配置具体list类型则使用{@link ArrayList}作为{@link List}对象。 
 * @version 2009-11-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class ListParser implements PropertyParser {
    @Override
    @SuppressWarnings("unchecked")
    public Object parser(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception {
        PropList p = (PropList) prop;
        Class<?> setType = this.parserType(context, contextParams, prop, propContext, definition, factory, contextParser);
        if (setType == List.class)
            setType = ArrayList.class;
        //循环遍历数组元素定义以处理各个元素。
        List returnObject = (List) setType.newInstance();
        BeanProp[] element = p.getListElements();
        for (int i = 0; i < element.length; i++) {
            BeanProp e = element[i];
            Object itemObject = null;
            if (e instanceof PropList)
                itemObject = this.parser(context, contextParams, e, propContext, definition, factory, contextParser);
            else
                itemObject = contextParser.parser(context, contextParams, e, propContext, definition, factory, contextParser);
            returnObject.add(itemObject);
        }
        return returnObject;
    }
    @Override
    public Class<?> parserType(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception {
        /*
         * propType属性规则。
         * 1.如果PropList配置了propType属性则直接返回，否则返回propContext的propType属性。
         * 2.如果propContext也没有配置propType属性则返回java.util.List。
         */
        String propType = prop.getPropType();
        if (propType == null)
            propType = propContext.getPropType();
        if (propType == null)
            propType = "java.util.List";
        return factory.getBeanClassLoader().loadClass(propType);
    }
}
