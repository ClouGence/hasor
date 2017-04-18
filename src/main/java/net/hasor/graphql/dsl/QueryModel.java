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
package net.hasor.graphql.dsl;
import net.hasor.graphql.dsl.domain.QueryDomain;
/**
 * 查询模型
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface QueryModel {
    /** 查询字符串，部分 QL 使用片段形式表达便于阅读。 */
    public String buildQuery();

    /** 查询字符串，不使用片段形式表达。 */
    public String buildQueryWithoutFragment();

    /**查询模型对象*/
    public QueryDomain getDomain();
}