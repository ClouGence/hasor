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
import net.hasor.dataql.fx.db.parser.DefaultFxQuery;

import java.util.List;
import java.util.Map;

/**
 * @author jmxd
 * @version : 2020-05-18
 */
class MybatisSqlQuery extends DefaultFxQuery {
    private SqlNode sqlNode;

    public MybatisSqlQuery(SqlNode sqlNode) {
        this.sqlNode = sqlNode;
    }

    @Override
    public String buildQueryString(Object context) {
        if (context instanceof Map) {
            return sqlNode.getSql((Map<String, Object>) context);
        } else {
            throw new IllegalArgumentException("context must be instance of Map");
        }
    }

    @Override
    public List<Object> buildParameterSource(Object context) {
        return this.sqlNode.getParameters();
    }
}