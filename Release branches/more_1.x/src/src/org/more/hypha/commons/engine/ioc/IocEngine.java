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
package org.more.hypha.commons.engine.ioc;
import org.more.hypha.ApplicationContext;
import org.more.hypha.commons.engine.ValueMetaDataParser;
/**
 * 该接口是基本的bean获取接口，该接口的职责是给定bean定义并且将这个bean定义创建出来。
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class IocEngine {
    //----------------------------------------------------------------------------------------------------------
    /**初始化方法。 */
    public abstract void init(ApplicationContext context, ValueMetaDataParser rootParser) throws Throwable;
    /**销毁方法。*/
    public void destroy() throws Throwable {}
};