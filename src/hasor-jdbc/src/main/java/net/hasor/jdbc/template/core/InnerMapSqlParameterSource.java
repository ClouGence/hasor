/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.jdbc.template.core;
import java.util.Map;
import net.hasor.jdbc.template.SqlParameterSource;
/**
 * 
 * @version : 2014-3-31
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
class InnerMapSqlParameterSource implements SqlParameterSource {
    private Map<String, ?> values;
    public InnerMapSqlParameterSource(Map<String, ?> values) {
        this.values = values;
    }
    public boolean hasValue(String paramName) {
        return this.values.containsKey(paramName);
    }
    public Object getValue(String paramName) throws IllegalArgumentException {
        return this.values.get(paramName);
    }
}