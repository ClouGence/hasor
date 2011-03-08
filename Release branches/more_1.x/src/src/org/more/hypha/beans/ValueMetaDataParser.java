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
package org.more.hypha.beans;
import org.more.hypha.ApplicationContext;
/**
 * {@link ValueMetaData}值元信息解析器，其实类可以决定如何解析这个元信息，并且将解析的结果返回。
 * 如果在解析期间需要递归调用值元信息解析器或者调用其他类型值元信息解析器那么可以通过rootParser参数对象进行调用。
 * @version 2011-1-18
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ValueMetaDataParser<T extends ValueMetaData> {
    /**
     * 解析元信息，并且将解析的对象结果返回。
     * @param data 要解析的元信息对象。
     * @param rootParser 根解析器。
     * @param context {@link ApplicationContext}接口对象。
     * @return 返回解析的结果。
     */
    public Object parser(T data, ValueMetaDataParser<T> rootParser, ApplicationContext context) throws Throwable;
    /**
     * 解析元信息，并且将解析的对象类型返回。
     * @param data 要解析的值元信息对象。
     * @param rootParser 根解析器。
     * @param context {@link ApplicationContext}接口对象。
     * @return 返回解析的结果。
     */
    public Class<?> parserType(T data, ValueMetaDataParser<T> rootParser, ApplicationContext context) throws Throwable;
};