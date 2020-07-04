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
package net.hasor.db.jdbc.paramer;
import net.hasor.db.jdbc.SqlParameterSource;
import net.hasor.db.jdbc.core.ParameterDisposer;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 一个 Map 到 SqlParameterSource 的桥，同时支持自动识别 Supplier 接口以获取具体参数。
 * @version : 2014-3-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MapSqlParameterSource implements SqlParameterSource, ParameterDisposer {
    private final Map<String, ?> values;

    public MapSqlParameterSource(final Map<String, ?> values) {
        this.values = values;
    }

    @Override
    public boolean hasValue(final String paramName) {
        return this.values.containsKey(paramName);
    }

    @Override
    public Object getValue(final String paramName) throws IllegalArgumentException {
        Object object = this.values.get(paramName);
        if (object instanceof Supplier) {
            object = ((Supplier<?>) object).get();
        }
        return object;
    }

    @Override
    public void cleanupParameters() {
        for (Object val : this.values.values()) {
            if (val instanceof ParameterDisposer) {
                ((ParameterDisposer) val).cleanupParameters();
            }
        }
    }
}