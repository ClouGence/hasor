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
package net.hasor.db.dal;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.types.TypeHandler;

import java.sql.JDBCType;

/**
 * 代表一个动态 SQL Build 之后的具体 SQL 和其参数
 * @version : 2021-06-05
 * @author 赵永春 (zyc@byshell.org)
 */
public interface DalBoundSql extends BoundSql {
    public SqlMode[] getSqlModes();

    public JDBCType[] getJdbcType();

    public Class<?>[] getJavaType();

    public TypeHandler<?>[] getTypeHandlers();
}
