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
package org.more.workflow.util;
import org.more.workflow.context.ApplicationContext;
/**
 * 在初始化时会使用到该接口，用于向目标接口传递初始化参数和流程系统上下文。
 * Date : 2010-6-14
 * @author 赵永春
 */
public interface Config {
    /**获取可以使用的参数名称集合，并且以迭代器方式返回。*/
    public Iterable<String> getParamNamesIterable();
    /**根据key值获取一个初始化参数。key值必须是getParamNamesIterable方法返回的迭代器中存在的属性。否则该方法将返回null。*/
    public Object getParam(String key);
};