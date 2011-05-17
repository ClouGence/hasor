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
package org.more.hypha.commons.engine;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ApplicationContext;
/**
 * 该接口是针对某个{@link AbstractBeanDefine}类型的特定支持。该接口的功能是负责创建某种类型的Bean。
 * @version : 2011-5-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AbstractBeanBuilder<T extends AbstractBeanDefine> {
    /**设置当前{@link AbstractBeanBuilder}使用的{@link ApplicationContext}环境对象。*/
    public void setApplicationContext(ApplicationContext applicationContext);
    /**装载bean定义的类型。*/
    public Class<?> loadType(AbstractBeanDefine define, Object[] params);
    /**创建Bean对象。*/
    public <O> O createBean(AbstractBeanDefine define, Object[] params);
};