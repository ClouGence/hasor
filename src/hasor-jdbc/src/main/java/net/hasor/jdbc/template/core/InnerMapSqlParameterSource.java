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
    public InnerMapSqlParameterSource(Map<String, ?> values) {
        // TODO Auto-generated constructor stub
    }
    @Override
    public boolean hasValue(String paramName) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public Object getValue(String paramName) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int getSqlType(String paramName) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public String getTypeName(String paramName) {
        // TODO Auto-generated method stub
        return null;
    }
}
