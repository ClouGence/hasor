/*
 * Copyright 2002-2006 the original author or authors.
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
package net.hasor.jdbc.jdbc.parameter;
/**
 * 值类型输出参数（非结果集）。
 * @version : 2013-10-15
 * @author 赵永春(zyc@hasor.net)
 */
public class SqlOutVarParameter extends SqlOutParameter {
    private SqlReturnType sqlReturnType;
    /**
     * Create a new SqlOutParameter.
     * @param name name of the parameter, as used in input and output maps
     * @param sqlType SQL type of the parameter according to java.sql.Types
     */
    public SqlOutVarParameter(String name, int sqlType) {
        super(name, sqlType);
    }
    /**
     * Create a new SqlOutParameter.
     * @param name name of the parameter, as used in input and output maps
     * @param sqlType SQL type of the parameter according to java.sql.Types
     * @param typeName the type name of the parameter (optional)
     * @param sqlReturnType custom value handler for complex type (optional)
     */
    public SqlOutVarParameter(String name, int sqlType, SqlReturnType sqlReturnType) {
        super(name, sqlType);
        this.sqlReturnType = sqlReturnType;
    }
    /**Return the custom return type, if any.*/
    public SqlReturnType getSqlReturnType() {
        return this.sqlReturnType;
    }
    /**Return whether this parameter holds a custom return type.*/
    public boolean isReturnTypeSupported() {
        return (this.sqlReturnType != null);
    }
}