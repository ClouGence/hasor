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
package net.hasor.graphql.ctx;
import net.hasor.graphql.runtime.QueryContext;

import java.util.HashMap;
import java.util.Map;
/**
 * QL 查询上下文，一个扩展的 Map 对象。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
abstract class QueryContextImpl extends HashMap<String, Object> implements QueryContext {
    public QueryContextImpl(Map<String, Object> queryContext) {
        super(queryContext);
    }
    @Override
    public Object get(String name) {
        return super.get(name);
    }
}