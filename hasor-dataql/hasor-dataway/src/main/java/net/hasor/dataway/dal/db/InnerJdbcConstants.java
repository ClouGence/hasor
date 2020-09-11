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
package net.hasor.dataway.dal.db;
/**
 * 工具类来自于 druid-1.1.23.jar
 * com.alibaba.druid.util.JdbcConstants
 * com.alibaba.druid.util.JdbcUtils
 * @author wenshao [szujobs@hotmail.com]
 * @version : 2020-09-12
 */
class InnerJdbcConstants {
    private static final String JTDS             = "jtds";
    private static final String MOCK             = "mock";
    private static final String HSQL             = "hsql";
    private static final String DB2              = "db2";
    private static final String POSTGRESQL       = "postgresql";
    private static final String SYBASE           = "sybase";
    private static final String SQL_SERVER       = "sqlserver";
    private static final String ORACLE           = "oracle";
    private static final String ALI_ORACLE       = "AliOracle";
    private static final String MYSQL            = "mysql";
    private static final String MARIADB          = "mariadb";
    private static final String DERBY            = "derby";
    private static final String HBASE            = "hbase";
    private static final String HIVE             = "hive";
    private static final String H2               = "h2";
    private static final String DM               = "dm";
    private static final String KINGBASE         = "kingbase";
    private static final String GBASE            = "gbase";
    private static final String XUGU             = "xugu";
    private static final String OCEANBASE        = "oceanbase";
    private static final String OCEANBASE_ORACLE = "oceanbase_oracle";
    private static final String INFORMIX         = "informix";
    /** 阿里云 odps */
    private static final String ODPS             = "odps";
    private static final String TERADATA         = "teradata";
    /** Log4JDBC */
    private static final String LOG4JDBC         = "log4jdbc";
    private static final String PHOENIX          = "phoenix";
    private static final String ENTERPRISEDB     = "edb";
    private static final String KYLIN            = "kylin";
    private static final String SQLITE           = "sqlite";
    private static final String ALIYUN_ADS       = "aliyun_ads";
    private static final String ALIYUN_DRDS      = "aliyun_drds";
    private static final String PRESTO           = "presto";
    private static final String ELASTIC_SEARCH   = "elastic_search";
    private static final String CLICKHOUSE       = "clickhouse";
    private static final String KDB              = "kdb";
    /** Aliyun PolarDB */
    private static final String POLARDB          = "polardb";

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