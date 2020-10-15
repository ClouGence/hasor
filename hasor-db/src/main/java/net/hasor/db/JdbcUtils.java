/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package net.hasor.db;
/**
 * 工具类来自于 druid-1.1.23.jar
 * com.alibaba.druid.util.JdbcConstants
 * com.alibaba.druid.util.JdbcUtils
 * @author wenshao [szujobs@hotmail.com]
 * @version : 2020-09-12
 */
public class JdbcUtils {
    public static final String JTDS             = "jtds";
    public static final String MOCK             = "mock";
    public static final String HSQL             = "hsql";
    public static final String DB2              = "db2";
    public static final String POSTGRESQL       = "postgresql";
    public static final String SYBASE           = "sybase";
    public static final String SQL_SERVER       = "sqlserver";
    public static final String ORACLE           = "oracle";
    public static final String ALI_ORACLE       = "AliOracle";
    public static final String MYSQL            = "mysql";
    public static final String MARIADB          = "mariadb";
    public static final String DERBY            = "derby";
    public static final String HBASE            = "hbase";
    public static final String HIVE             = "hive";
    public static final String H2               = "h2";
    public static final String DM               = "dm";
    public static final String KINGBASE         = "kingbase";
    public static final String GBASE            = "gbase";
    public static final String XUGU             = "xugu";
    public static final String OCEANBASE        = "oceanbase";
    public static final String OCEANBASE_ORACLE = "oceanbase_oracle";
    public static final String INFORMIX         = "informix";
    public static final String HERDDB           = "herddb";
    /** 阿里云 odps */
    public static final String ODPS             = "odps";
    public static final String TERADATA         = "teradata";
    /** Log4JDBC */
    public static final String LOG4JDBC         = "log4jdbc";
    public static final String PHOENIX          = "phoenix";
    public static final String ENTERPRISEDB     = "edb";
    public static final String KYLIN            = "kylin";
    public static final String SQLITE           = "sqlite";
    public static final String ALIYUN_ADS       = "aliyun_ads";
    public static final String ALIYUN_DRDS      = "aliyun_drds";
    public static final String PRESTO           = "presto";
    public static final String ELASTIC_SEARCH   = "elastic_search";
    public static final String CLICKHOUSE       = "clickhouse";
    public static final String KDB              = "kdb";
    /** Aliyun PolarDB */
    public static final String POLARDB          = "polardb";

    public static String getDbType(String rawUrl, String driverClassName) {
        if (rawUrl == null) {
            return null;
        }
        if (rawUrl.startsWith("jdbc:derby:") || rawUrl.startsWith("jdbc:log4jdbc:derby:")) {
            return DERBY;
        } else if (rawUrl.startsWith("jdbc:mysql:") || rawUrl.startsWith("jdbc:cobar:") || rawUrl.startsWith("jdbc:log4jdbc:mysql:")) {
            return MYSQL;
        } else if (rawUrl.startsWith("jdbc:mariadb:")) {
            return MARIADB;
        } else if (rawUrl.startsWith("jdbc:oracle:") || rawUrl.startsWith("jdbc:log4jdbc:oracle:")) {
            return ORACLE;
        } else if (rawUrl.startsWith("jdbc:alibaba:oracle:")) {
            return ALI_ORACLE;
        } else if (rawUrl.startsWith("jdbc:oceanbase:")) {
            return OCEANBASE;
        } else if (rawUrl.startsWith("jdbc:oceanbase:oracle:")) {
            return OCEANBASE_ORACLE;
        } else if (rawUrl.startsWith("jdbc:microsoft:") || rawUrl.startsWith("jdbc:log4jdbc:microsoft:")) {
            return SQL_SERVER;
        } else if (rawUrl.startsWith("jdbc:sqlserver:") || rawUrl.startsWith("jdbc:log4jdbc:sqlserver:")) {
            return SQL_SERVER;
        } else if (rawUrl.startsWith("jdbc:sybase:Tds:") || rawUrl.startsWith("jdbc:log4jdbc:sybase:")) {
            return SYBASE;
        } else if (rawUrl.startsWith("jdbc:jtds:") || rawUrl.startsWith("jdbc:log4jdbc:jtds:")) {
            return JTDS;
        } else if (rawUrl.startsWith("jdbc:fake:") || rawUrl.startsWith("jdbc:mock:")) {
            return MOCK;
        } else if (rawUrl.startsWith("jdbc:postgresql:") || rawUrl.startsWith("jdbc:log4jdbc:postgresql:")) {
            return POSTGRESQL;
        } else if (rawUrl.startsWith("jdbc:edb:")) {
            return ENTERPRISEDB;
        } else if (rawUrl.startsWith("jdbc:hsqldb:") || rawUrl.startsWith("jdbc:log4jdbc:hsqldb:")) {
            return HSQL;
        } else if (rawUrl.startsWith("jdbc:odps:")) {
            return ODPS;
        } else if (rawUrl.startsWith("jdbc:db2:")) {
            return DB2;
        } else if (rawUrl.startsWith("jdbc:sqlite:")) {
            return SQLITE;
        } else if (rawUrl.startsWith("jdbc:ingres:")) {
            return "ingres";
        } else if (rawUrl.startsWith("jdbc:h2:") || rawUrl.startsWith("jdbc:log4jdbc:h2:")) {
            return H2;
        } else if (rawUrl.startsWith("jdbc:mckoi:")) {
            return "mckoi";
        } else if (rawUrl.startsWith("jdbc:cloudscape:")) {
            return "cloudscape";
        } else if (rawUrl.startsWith("jdbc:informix-sqli:") || rawUrl.startsWith("jdbc:log4jdbc:informix-sqli:")) {
            return INFORMIX;
        } else if (rawUrl.startsWith("jdbc:timesten:")) {
            return "timesten";
        } else if (rawUrl.startsWith("jdbc:as400:")) {
            return "as400";
        } else if (rawUrl.startsWith("jdbc:sapdb:")) {
            return "sapdb";
        } else if (rawUrl.startsWith("jdbc:JSQLConnect:")) {
            return "JSQLConnect";
        } else if (rawUrl.startsWith("jdbc:JTurbo:")) {
            return "JTurbo";
        } else if (rawUrl.startsWith("jdbc:firebirdsql:")) {
            return "firebirdsql";
        } else if (rawUrl.startsWith("jdbc:interbase:")) {
            return "interbase";
        } else if (rawUrl.startsWith("jdbc:pointbase:")) {
            return "pointbase";
        } else if (rawUrl.startsWith("jdbc:edbc:")) {
            return "edbc";
        } else if (rawUrl.startsWith("jdbc:mimer:multi1:")) {
            return "mimer";
        } else if (rawUrl.startsWith("jdbc:dm:")) {
            return DM;
        } else if (rawUrl.startsWith("jdbc:kingbase:")) {
            return KINGBASE;
        } else if (rawUrl.startsWith("jdbc:gbase:")) {
            return GBASE;
        } else if (rawUrl.startsWith("jdbc:xugu:")) {
            return XUGU;
        } else if (rawUrl.startsWith("jdbc:log4jdbc:")) {
            return LOG4JDBC;
        } else if (rawUrl.startsWith("jdbc:hive:")) {
            return HIVE;
        } else if (rawUrl.startsWith("jdbc:hive2:")) {
            return HIVE;
        } else if (rawUrl.startsWith("jdbc:phoenix:")) {
            return PHOENIX;
        } else if (rawUrl.startsWith("jdbc:elastic:")) {
            return ELASTIC_SEARCH;
        } else if (rawUrl.startsWith("jdbc:clickhouse:")) {
            return CLICKHOUSE;
        } else if (rawUrl.startsWith("jdbc:presto:")) {
            return PRESTO;
        } else if (rawUrl.startsWith("jdbc:inspur:")) {
            return KDB;
        } else if (rawUrl.startsWith("jdbc:polardb")) {
            return POLARDB;
        } else {
            return null;
        }
    }
}