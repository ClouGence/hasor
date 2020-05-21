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
package net.hasor.dataql.fx.db.runsql;
import net.hasor.core.AppContext;
import net.hasor.dataql.fx.db.runsql.dialect.*;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;

import java.util.Map;

/**
 * 分页查询方言管理器
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-08
 */
public class SqlPageDialectRegister {
    private static final Map<String, Class<?>>       dialectAliasMap = new LinkedCaseInsensitiveMap<>();
    private static final Map<String, SqlPageDialect> dialectCache    = new LinkedCaseInsensitiveMap<>();

    public static void registerDialectAlias(String alias, Class<? extends SqlPageDialect> dialectClass) {
        dialectAliasMap.put(alias, dialectClass);
    }

    public static SqlPageDialect findOrCreate(String dialectName, AppContext appContext) {
        SqlPageDialect dialect = dialectCache.get(dialectName);
        if (dialect != null) {
            return dialect;
        }
        //
        Class<?> aClass = dialectAliasMap.get(dialectName);
        if (aClass != null) {
            dialect = (SqlPageDialect) appContext.getInstance(aClass);
        } else {
            try {
                aClass = appContext.getClassLoader().loadClass(dialectName);
                dialect = (SqlPageDialect) appContext.getInstance(aClass);
            } catch (ClassNotFoundException e) {
                dialect = appContext.getInstance(dialectName);
            }
        }
        //
        if (dialect == null) {
            throw new IllegalArgumentException("Unable to load dialect " + dialectName);
        }
        dialectCache.put(dialectName, dialect);
        return dialect;
    }

    static {
        //
        registerDialectAlias("postgresql", PostgreSqlDialect.class);
        registerDialectAlias("h2", PostgreSqlDialect.class);
        registerDialectAlias("hsqldb", PostgreSqlDialect.class);
        registerDialectAlias("phoenix", PostgreSqlDialect.class);
        //
        registerDialectAlias("mysql", MySqlDialect.class);
        registerDialectAlias("mariadb", MySqlDialect.class);
        registerDialectAlias("sqlite", MySqlDialect.class);
        registerDialectAlias("herddb", MySqlDialect.class);
        //
        registerDialectAlias("sqlserver2012", SqlServer2012Dialect.class);
        registerDialectAlias("derby", SqlServer2012Dialect.class);// Apache Derby
        //
        registerDialectAlias("oracle", OracleDialect.class);
        //
        registerDialectAlias("db2", Db2Dialect.class);
        //
        registerDialectAlias("informix", InformixDialect.class);
    }
}