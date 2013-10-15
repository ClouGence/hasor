/*
 * Copyright 2002-2007 the original author or authors.
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
package net.hasor.jdbc.operations.core.parameter;
/**
 * 输入输出双向参数（不支持结果集）
 * @version : 2013-10-15
 * @author 赵永春(zyc@hasor.net)
 */
public class SqlInOutParameter extends SqlOutVarParameter {
    /**
     * Create a new SqlOutParameter.
     * @param name name of the parameter, as used in input and output maps
     * @param sqlType SQL type of the parameter according to java.sql.Types
     */
    public SqlInOutParameter(String name, int sqlType) {
        super(name, sqlType);
    }
    /**
     * Create a new SqlOutParameter.
     * @param name name of the parameter, as used in input and output maps
     * @param sqlType SQL type of the parameter according to java.sql.Types
     * @param typeName the type name of the parameter (optional)
     * @param sqlReturnType custom value handler for complex type (optional)
     */
    public SqlInOutParameter(String name, int sqlType, SqlReturnType sqlReturnType) {
        super(name, sqlType, sqlReturnType);
    }
    public boolean isInput() {
        return true;
    }
}