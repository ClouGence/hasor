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
package net.hasor.jdbc.opface.parameter;
/**
 * SqlParameter 的子类，用于表示带有值的输入参数。
 * @see java.sql.Types
 * @version : 2013-10-14
 * @author 赵永春(zyc@hasor.net)
 */
public class SqlVarParameter extends SqlParameter {
    private final Object value;
    /**创建一个匿名的 SQL 参数.*/
    public SqlVarParameter(int sqlType, Object value) {
        super(sqlType);
        this.value = value;
    }
    /**根据参数名 和参数类型创建一个 SqlParameter.*/
    public SqlVarParameter(String name, int sqlType, Object value) {
        super(name, sqlType);
        this.value = value;
    }
    /**
     * Create a new SqlParameterValue based on the given SqlParameter declaration.
     * @param declaredParam the declared SqlParameter to define a value for
     * @param value the value object
     */
    public SqlVarParameter(SqlParameter declaredParam, Object value) {
        super(declaredParam);
        this.value = value;
    }
    /**参数值.*/
    public Object getValue() {
        return this.value;
    }
}