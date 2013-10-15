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
 * SqlParameter 的子类，用于表示输出参数的基础类。
 * @version : 2013-10-14
 * @author 赵永春(zyc@hasor.net)
 */
public class SqlOutParameter extends SqlParameter {
    /**创建一个匿名的 SQL 参数.*/
    public SqlOutParameter(int sqlType) {
        super(sqlType);
    }
    /**根据参数名 和参数类型创建一个 SqlParameter.*/
    public SqlOutParameter(String name, int sqlType) {
        super(name, sqlType);
    }
    /**根据一个 SqlParameter 拷贝创建一个新的 SqlParameter.*/
    public SqlOutParameter(SqlParameter otherParam) {
        super(otherParam);
    }
    //
    public boolean isInput() {
        return false;
    }
    public boolean isOutput() {
        return true;
    }
}