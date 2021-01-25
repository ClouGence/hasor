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
package net.hasor.db.dialect;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.db.JdbcUtils;
import net.hasor.db.dialect.provider.*;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
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

    static {
        registerDialectAlias(JdbcUtils.POSTGRESQL, PostgreSqlDialect.class);
        registerDialectAlias(JdbcUtils.HSQL, HSQLDialect.class);
        registerDialectAlias(JdbcUtils.PHOENIX, PhoenixDialect.class);
        registerDialectAlias(JdbcUtils.IMPALA, ImpalaDialect.class);
        registerDialectAlias(JdbcUtils.MYSQL, MySqlDialect.class);
        registerDialectAlias(JdbcUtils.MARIADB, MariaDBDialect.class);
        registerDialectAlias(JdbcUtils.ALIYUN_ADS, AliyunAdsDialect.class);
        registerDialectAlias(JdbcUtils.ALIYUN_DRDS, AliyunDRDSDialect.class);
        registerDialectAlias(JdbcUtils.SQLITE, SqlLiteDialect.class);
        registerDialectAlias(JdbcUtils.HERDDB, HerdDBDialect.class);
        registerDialectAlias("sqlserver2012", SqlServer2012Dialect.class);
        registerDialectAlias(JdbcUtils.DERBY, SqlServer2012Dialect.class);// Apache Derby
        registerDialectAlias(JdbcUtils.ORACLE, OracleDialect.class);
        registerDialectAlias(JdbcUtils.ALI_ORACLE, OracleDialect.class);
        registerDialectAlias(JdbcUtils.H2, H2Dialect.class);
        registerDialectAlias(JdbcUtils.HIVE, HiveDialect.class);
        registerDialectAlias(JdbcUtils.DB2, Db2Dialect.class);
        registerDialectAlias(JdbcUtils.INFORMIX, InformixDialect.class);
    }

    public static void registerDialectAlias(String dialectName, Class<? extends SqlDialect> dialectClass) {
        dialectAliasMap.put(dialectName, dialectClass);
    }

    public static void registerDialect(String dialectName, SqlDialect sqlDialect) {
        dialectCache.put(dialectName, sqlDialect);
    }

    public static SqlDialect findOrCreate(String dialectName) {
        return findOrCreate(dialectName, null);
    }

    public static SqlDialect findOrCreate(String dialectName, AppContext appContext) {
        if (StringUtils.isBlank(dialectName)) {
            return SqlDialect.DEFAULT;
        }
        SqlDialect dialect = dialectCache.get(dialectName);
        if (dialect != null) {
            return dialect;
        }
        //
        String lastMessage = null;
        Class<?> aClass = dialectAliasMap.get(dialectName);
        if (aClass == null) {
            try {
                if (appContext != null) {
                    aClass = appContext.getClassLoader().loadClass(dialectName);
                } else {
                    aClass = ResourcesUtils.classForName(dialectName);
                }
            } catch (Exception e) {
                lastMessage = "load dialect '" + dialectName + "' class failed -> " + e.getMessage();
            }
        }
        //
        if (aClass != null) {
            if (appContext != null) {
                dialect = (SqlDialect) appContext.getInstance(aClass);
            } else {
                try {
                    dialect = (SqlDialect) aClass.newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException("create dialect failed -> " + e.getMessage(), e);
                }
            }
        } else {
            if (appContext != null) {
                BindInfo<SqlDialect> dialectBindInfo = appContext.findBindingRegister(dialectName, SqlDialect.class);
                if (dialectBindInfo != null) {
                    dialect = appContext.getInstance(dialectBindInfo);
                }
            }
            //
            if (dialect == null) {
                if (StringUtils.isNotBlank(lastMessage)) {
                    throw new IllegalStateException(lastMessage);
                } else {
                    throw new IllegalStateException("No dialect '" + dialectName + "' found.");
                }
            }
        }
        //
        dialectCache.put(dialectName, dialect);
        return dialect;
    }
}