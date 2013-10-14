/*
 * Copyright 2002-2008 the original author or authors.
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
import net.hasor.jdbc.jdbc.SqlParameter;
/**
 * 代表传入参数。
 * @see java.sql.Types
 * @version : 2013-10-14
 * @author 赵永春(zyc@hasor.net)
 */
public class SqlInputParameter extends SqlParameter {
    /**创建一个匿名的 SQL 参数.*/
    public SqlInputParameter(int sqlType) {
        super(sqlType);
    }
    /**根据参数名 和参数类型创建一个 SqlParameter.*/
    public SqlInputParameter(String name, int sqlType) {
        super(name, sqlType);
    }
    /**根据一个 SqlParameter 拷贝创建一个新的 SqlParameter.*/
    public SqlInputParameter(SqlParameter otherParam) {
        super(otherParam);
    }
    //
    public boolean isInput() {
        return true;
    }
    public boolean isOutput() {
        return false;
    }
}