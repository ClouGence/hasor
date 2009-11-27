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
import java.util.HashMap;
import java.util.Map;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProp;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.PropMap;
/**
 * 对类型为{@link PropMap}的属性定义提供解析支持，如果{@link PropMap}没有配置具体map类型则使用{@link HashMap}作为{@link Map}对象。
 * <br/>Date : 2009-11-18
 * @author 赵永春
 */
public class MapParser implements PropertyParser {
    @Override
    @SuppressWarnings("unchecked")
    public Object parser(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception {
        PropMap p = (PropMap) prop;
        Class<?> mapType = this.parserType(context, contextParams, prop, propContext, definition, factory, contextParser);
        if (mapType == Map.class)
            mapType = HashMap.class;
        //三、循环遍历数组元素定义以处理各个元素。
        Map returnObject = (Map) mapType.newInstance();
        BeanProp[][] element = p.getMapElements();
        for (int i = 0; i < element.length; i++) {
            BeanProp k = element[i][0];
            BeanProp v = element[i][1];
            Object keyObject = null;
            Object valueObject = null;
            //
            if (k instanceof PropMap == true)
                keyObject = this.parser(context, contextParams, k, propContext, definition, factory, contextParser);
            else
                keyObject = contextParser.parser(context, contextParams, k, propContext, definition, factory, contextParser);
            if (v instanceof PropMap == true)
                valueObject = this.parser(context, contextParams, v, propContext, definition, factory, contextParser);
            else
                valueObject = contextParser.parser(context, contextParams, v, propContext, definition, factory, contextParser);
            //
            returnObject.put(keyObject, valueObject);
        }
        return returnObject;
    }
    @Override
    public Class<?> parserType(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception {
        /*
         * propType属性规则。
         * 1.如果PropMap配置了propType属性则直接返回，否则返回propContext的propType属性。
         * 2.如果propContext也没有配置propType属性则返回java.util.Map。
         */
        String propType = prop.getPropType();
        if (propType == null)
            propType = propContext.getPropType();
        if (propType == null)
            propType = "java.util.Map";
        return factory.getBeanClassLoader().loadClass(propType);
    }
}
