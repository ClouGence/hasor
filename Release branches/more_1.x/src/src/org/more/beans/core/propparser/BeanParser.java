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
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProp;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.PropBean;
/**
 * 对类型为{@link PropBean}的属性定义提供解析支持，如果{@link PropBean}没有配置beanDefinition属性则会引发空引用异常，对于BeanParser类型是不需要配置propType属性的。
 * <br/>Date : 2009-11-18
 * @author 赵永春
 */
public class BeanParser implements PropertyParser {
    @Override
    public Object parser(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception {
        PropBean p = (PropBean) prop;
        BeanDefinition bean = p.getBeanDefinition();
        if (bean != null)
            return factory.getBean(bean.getName(), contextParams);
        else
            throw new NullPointerException("属性[" + propContext.getName() + "]配置异常。无法引用名称为空的Bean对象。");
    }
    @Override
    public Class<?> parserType(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception {
        PropBean p = (PropBean) prop;
        BeanDefinition bean = p.getBeanDefinition();
        return factory.getBeanType(bean.getName());
    }
}
