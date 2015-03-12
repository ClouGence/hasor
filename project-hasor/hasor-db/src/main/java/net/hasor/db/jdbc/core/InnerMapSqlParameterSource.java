/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.db.jdbc.core;
import java.util.Map;
import net.hasor.db.jdbc.SqlParameterSource;
/**
 * 
 * @version : 2014-3-31
 * @author 赵永春(zyc@hasor.net)
 */
class InnerMapSqlParameterSource implements SqlParameterSource, ParameterDisposer {
    private Map<String, ?> values;
    public InnerMapSqlParameterSource(final Map<String, ?> values) {
        this.values = values;
    }
    @Override
    public boolean hasValue(final String paramName) {
        return this.values.containsKey(paramName);
    }
    @Override
    public Object getValue(final String paramName) throws IllegalArgumentException {
        return this.values.get(paramName);
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