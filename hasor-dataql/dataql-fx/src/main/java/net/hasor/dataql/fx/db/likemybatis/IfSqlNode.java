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
package net.hasor.dataql.fx.db.likemybatis;
import net.hasor.db.dal.fxquery.DefaultFxQuery;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 对应XML中 <if>
 * @author jmxd
 * @version : 2020-05-18
 */
public class IfSqlNode extends SqlNode {
    /** 判断表达式 */
    private String test;

    public IfSqlNode(String test) {
        this.test = test;
    }

    @Override
    public String getSql(Map<String, Object> paramMap, List<Object> parameters) {
        // 执行表达式
        Object value = DefaultFxQuery.evalOgnl(test, paramMap);
        // 判断表达式返回结果是否是true，如果不是则过滤子节点
        if (Objects.equals(value, true)) {
            return executeChildren(paramMap, parameters);
        }
        return "";
    }
}
