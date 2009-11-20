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
import org.more.beans.BeanFactory;
import org.more.beans.info.BeanDefinition;
/**
 * 属性注入请求处理接口，该接口负责对某个bean进行复杂注入请求的处理。
 * <br/>Date : 2009-11-7
 * @author 赵永春
 */
public interface ExportInjectionProperty {
    /**
     * 处理bean注入方法。
     * @param object 被请求注入的bean
     * @param getBeanParam 在调用getbean时传递的参数。
     * @param definition 被请求注入的bean定义。
     * @param context 整个beans容器上下文。
     * @return 返回注入完毕的对象
     */
    public Object injectionProperty(Object object, Object[] getBeanParam, BeanDefinition definition, BeanFactory context);
}