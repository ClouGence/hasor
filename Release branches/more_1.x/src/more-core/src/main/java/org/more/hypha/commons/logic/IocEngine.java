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
package org.more.hypha.commons.logic;
import org.more.core.error.InitializationException;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
/**
 * 该接口是基本的bean获取接口，该接口的职责是给定bean定义并且将这个bean定义创建出来。
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class IocEngine {
    private ApplicationContext                 context    = null;
    private ValueMetaDataParser<ValueMetaData> rootParser = null;
    protected ApplicationContext getApplicationContext() {
        return this.context;
    }
    protected ValueMetaDataParser<ValueMetaData> getRootParser() {
        return this.rootParser;
    }
    /**初始化方法。 */
    public void init(ApplicationContext context, ValueMetaDataParser<ValueMetaData> rootParser) throws InitializationException {
        this.context = context;
        this.rootParser = rootParser;
    }
    /**销毁方法。*/
    public void destroy() throws Throwable {}
    /**执行注入，将define定义信息注入到obj中。*/
    public abstract void ioc(Object obj, BeanDefine define, Object[] params) throws Throwable;
};