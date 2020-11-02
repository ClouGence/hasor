/*
 * Copyright 2002-2010 the original author or authors.
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
package net.hasor.db.jdbc.lambda.dialect;
import net.hasor.db.JdbcUtils;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;

import java.util.Map;

/**
 * 方言管理器
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class SqlDialectRegister {
    private static final Map<String, Class<?>>   dialectAliasMap = new LinkedCaseInsensitiveMap<>();
    private static final Map<String, SqlDialect> dialectCache    = new LinkedCaseInsensitiveMap<>();

    public static void registerDialectAlias(String alias, Class<? extends SqlDialect> dialectClass) {
        dialectAliasMap.put(alias, dialectClass);
    }

    public static SqlDialect findOrCreate(String dbType) {
        SqlDialect dialect = dialectCache.get(dbType);
        if (dialect != null) {
            return dialect;
        }
        Class<?> aClass = dialectAliasMap.get(dbType);
        if (aClass != null) {
            try {
                dialect = (SqlDialect) aClass.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("create dialect failed " + e.getMessage(), e);
            }
        } else {
            return SqlDialect.DEFAULT;
        }
        dialectCache.put(dbType, dialect);
        return dialect;
    }

    static {
        //
        // registerDialectAlias(JdbcUtils.POSTGRESQL, PostgreSqlDialect.class);
        // registerDialectAlias(JdbcUtils.H2, PostgreSqlDialect.class);
        // registerDialectAlias(JdbcUtils.HSQL, PostgreSqlDialect.class);
        // registerDialectAlias(JdbcUtils.PHOENIX, PostgreSqlDialect.class);
        // registerDialectAlias(JdbcUtils.IMPALA, PostgreSqlDialect.class);
        //
        registerDialectAlias(JdbcUtils.MYSQL, MySqlDialect.class);
        registerDialectAlias(JdbcUtils.MARIADB, MySqlDialect.class);
        registerDialectAlias(JdbcUtils.SQLITE, MySqlDialect.class);
        registerDialectAlias(JdbcUtils.HERDDB, MySqlDialect.class);
        registerDialectAlias(JdbcUtils.ALIYUN_ADS, MySqlDialect.class);
        registerDialectAlias(JdbcUtils.ALIYUN_DRDS, MySqlDialect.class);
        //
        // registerDialectAlias("sqlserver2012", SqlServer2012Dialect.class);
        // registerDialectAlias(JdbcUtils.DERBY, SqlServer2012Dialect.class);// Apache Derby
        //
        // registerDialectAlias(JdbcUtils.ORACLE, OracleDialect.class);
        //
        // registerDialectAlias(JdbcUtils.DB2, Db2Dialect.class);
        //
        // registerDialectAlias(JdbcUtils.INFORMIX, InformixDialect.class);
    }
}