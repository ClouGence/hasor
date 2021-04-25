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
package net.hasor.db.dialect;
import net.hasor.db.JdbcUtils;
import net.hasor.db.dialect.provider.Oracle12cDialect;
import net.hasor.db.dialect.provider.SqlServer2005Dialect;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.TableDef;
import net.hasor.test.db.AbstractDbTest;
import org.junit.Test;

import java.sql.JDBCType;

/***
 * 方言
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class DialectTest extends AbstractDbTest {
    private final BoundSql queryBoundSql  = new BoundSql() {
        @Override
        public String getSqlString() {
            return "select * from tb_user where age > 12 and sex = ?";
        }

        @Override
        public Object[] getArgs() {
            return new Object[] { 'F' };
        }
    };
    private final BoundSql queryBoundSql2 = new BoundSql() {
        @Override
        public String getSqlString() {
            return "select * from tb_user where age > 12 and sex = ? order by a desc";
        }

        @Override
        public Object[] getArgs() {
            return new Object[] { 'F' };
        }
    };

    private TableDef tableDef(String category, String tableName) {
        return new TableDef() {
            @Override
            public String getSchema() {
                return category;
            }

            @Override
            public String getTable() {
                return tableName;
            }
        };
    }

    private ColumnDef columnDef(String name, JDBCType jdbcType) {
        return new ColumnDef() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public JDBCType getJdbcType() {
                return jdbcType;
            }

            @Override
            public boolean isPrimaryKey() {
                return false;
            }
        };
    }

    @Test
    public void dialect_default_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate("");
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("tb_user");
        assert buildTableName2.equals("abc.tb_user");
        assert buildCondition.equals("userUUID");
        //
        try {
            dialect.countSql(this.queryBoundSql);
            assert false;
        } catch (Exception e) {
            assert true;
        }
        try {
            dialect.pageSql(this.queryBoundSql, 1, 3);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    public void dialect_mysql_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.MYSQL);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("`tb_user`");
        assert buildTableName2.equals("`abc`.`tb_user`");
        assert buildCondition.equals("`userUUID`");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?, ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(1);
        assert pageSql.getArgs()[2].equals(3);
        //
        BoundSql pageSql2 = dialect.pageSql(this.queryBoundSql, 0, 3);
        assert pageSql2.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?");
        assert pageSql2.getArgs().length == 2;
        assert pageSql2.getArgs()[0].equals('F');
        assert pageSql2.getArgs()[1].equals(3);
    }

    @Test
    public void dialect_dm_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.DM);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("\"tb_user\"");
        assert buildTableName2.equals("\"abc\".\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?, ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(1);
        assert pageSql.getArgs()[2].equals(3);
        //
        BoundSql pageSql2 = dialect.pageSql(this.queryBoundSql, 0, 3);
        assert pageSql2.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?");
        assert pageSql2.getArgs().length == 2;
        assert pageSql2.getArgs()[0].equals('F');
        assert pageSql2.getArgs()[1].equals(3);
    }

    @Test
    public void dialect_postgresql_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.POSTGRESQL);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("\"tb_user\"");
        assert buildTableName2.equals("\"abc\".\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ? OFFSET ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(3);
        assert pageSql.getArgs()[2].equals(1);
    }

    @Test
    public void dialect_oracle_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.ORACLE);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("\"tb_user\"");
        assert buildTableName2.equals("\"abc\".\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM ( select * from tb_user where age > 12 and sex = ? ) TMP WHERE ROWNUM <= ? ) WHERE ROW_ID > ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(4);
        assert pageSql.getArgs()[2].equals(1);
    }

    @Test
    public void dialect_h2_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.H2);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("\"tb_user\"");
        assert buildTableName2.equals("\"abc\".\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ? OFFSET ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(3);
        assert pageSql.getArgs()[2].equals(1);
    }

    @Test
    public void dialect_hive_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.HIVE);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("\"tb_user\"");
        assert buildTableName2.equals("\"abc\".\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
        //
        //
        try {
            dialect.countSql(this.queryBoundSql);
            assert false;
        } catch (Exception e) {
            assert true;
        }
        try {
            dialect.pageSql(this.queryBoundSql, 1, 3);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    @Test
    public void dialect_sqllite_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.SQLITE);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("`tb_user`");
        assert buildTableName2.equals("`abc`.`tb_user`");
        assert buildCondition.equals("`userUUID`");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?, ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(1);
        assert pageSql.getArgs()[2].equals(3);
        //
        BoundSql pageSql2 = dialect.pageSql(this.queryBoundSql, 0, 3);
        assert pageSql2.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?");
        assert pageSql2.getArgs().length == 2;
        assert pageSql2.getArgs()[0].equals('F');
        assert pageSql2.getArgs()[1].equals(3);
    }

    @Test
    public void dialect_herddb_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.HERDDB);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("`tb_user`");
        assert buildTableName2.equals("`abc`.`tb_user`");
        assert buildCondition.equals("`userUUID`");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?, ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(1);
        assert pageSql.getArgs()[2].equals(3);
        //
        BoundSql pageSql2 = dialect.pageSql(this.queryBoundSql, 0, 3);
        assert pageSql2.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?");
        assert pageSql2.getArgs().length == 2;
        assert pageSql2.getArgs()[0].equals('F');
        assert pageSql2.getArgs()[1].equals(3);
    }

    @Test
    public void dialect_sqlserver2012_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate("sqlserver2012");
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("[tb_user]");
        assert buildTableName2.equals("[abc].[tb_user]");
        assert buildCondition.equals("[userUUID]");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? ORDER BY CURRENT_TIMESTAMP offset ? rows fetch next ? rows only");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(1);
        assert pageSql.getArgs()[2].equals(3);
        //
        BoundSql countSql2 = dialect.countSql(this.queryBoundSql2);
        assert countSql2.getSqlString().equals("SELECT COUNT(*) FROM (SELECT * FROM tb_user WHERE age > 12 AND sex = ?) as TEMP_T");
        assert countSql2.getArgs().length == 1;
        BoundSql pageSql2 = dialect.pageSql(this.queryBoundSql2, 1, 3);
        assert pageSql2.getSqlString().equals("select * from tb_user where age > 12 and sex = ? order by a desc offset ? rows fetch next ? rows only");
        assert pageSql2.getArgs().length == 3;
        assert pageSql2.getArgs()[0].equals('F');
        assert pageSql2.getArgs()[1].equals(1);
        assert pageSql2.getArgs()[2].equals(3);
    }

    @Test
    public void dialect_informix_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.INFORMIX);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("\"tb_user\"");
        assert buildTableName2.equals("\"abc\".\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("SELECT  SKIP ?  FIRST ?  * FROM ( select * from tb_user where age > 12 and sex = ? ) TEMP_T");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals(1);
        assert pageSql.getArgs()[1].equals(3);
        assert pageSql.getArgs()[2].equals('F');
    }

    @Test
    public void dialect_db2_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.DB2);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("\"tb_user\"");
        assert buildTableName2.equals("\"abc\".\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("SELECT * FROM (SELECT TMP_PAGE.*,ROWNUMBER() OVER() AS ROW_ID FROM ( select * from tb_user where age > 12 and sex = ? ) AS TMP_PAGE) TMP_PAGE WHERE ROW_ID BETWEEN ? AND ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(1);
        assert pageSql.getArgs()[2].equals(3);
    }

    @Test
    public void dialect_hsql_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.HSQL);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("\"tb_user\"");
        assert buildTableName2.equals("\"abc\".\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ? OFFSET ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(3);
        assert pageSql.getArgs()[2].equals(1);
    }

    @Test
    public void dialect_phoenix_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.PHOENIX);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("\"tb_user\"");
        assert buildTableName2.equals("\"abc\".\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ? OFFSET ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(3);
        assert pageSql.getArgs()[2].equals(1);
    }

    @Test
    public void dialect_impala_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.IMPALA);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("\"tb_user\"");
        assert buildTableName2.equals("\"abc\".\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ? OFFSET ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(3);
        assert pageSql.getArgs()[2].equals(1);
    }

    @Test
    public void dialect_mariadb_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.MARIADB);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("`tb_user`");
        assert buildTableName2.equals("`abc`.`tb_user`");
        assert buildCondition.equals("`userUUID`");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?, ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(1);
        assert pageSql.getArgs()[2].equals(3);
        //
        BoundSql pageSql2 = dialect.pageSql(this.queryBoundSql, 0, 3);
        assert pageSql2.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?");
        assert pageSql2.getArgs().length == 2;
        assert pageSql2.getArgs()[0].equals('F');
        assert pageSql2.getArgs()[1].equals(3);
    }

    @Test
    public void dialect_aliyun_ads_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.ALIYUN_ADS);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("`tb_user`");
        assert buildTableName2.equals("`abc`.`tb_user`");
        assert buildCondition.equals("`userUUID`");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?, ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(1);
        assert pageSql.getArgs()[2].equals(3);
        //
        BoundSql pageSql2 = dialect.pageSql(this.queryBoundSql, 0, 3);
        assert pageSql2.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?");
        assert pageSql2.getArgs().length == 2;
        assert pageSql2.getArgs()[0].equals('F');
        assert pageSql2.getArgs()[1].equals(3);
    }

    @Test
    public void dialect_aliyun_drds_1() {
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.ALIYUN_DRDS);
        String buildTableName1 = dialect.tableName(true, tableDef("", "tb_user"));
        String buildTableName2 = dialect.tableName(true, tableDef("abc", "tb_user"));
        String buildCondition = dialect.columnName(true, tableDef("", "tb_user"), columnDef("userUUID", JDBCType.VARCHAR));
        //
        assert buildTableName1.equals("`tb_user`");
        assert buildTableName2.equals("`abc`.`tb_user`");
        assert buildCondition.equals("`userUUID`");
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) as TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?, ?");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(1);
        assert pageSql.getArgs()[2].equals(3);
        //
        BoundSql pageSql2 = dialect.pageSql(this.queryBoundSql, 0, 3);
        assert pageSql2.getSqlString().equals("select * from tb_user where age > 12 and sex = ? LIMIT ?");
        assert pageSql2.getArgs().length == 2;
        assert pageSql2.getArgs()[0].equals('F');
        assert pageSql2.getArgs()[1].equals(3);
    }

    @Test
    public void dialect_oracle12c_1() {
        SqlDialect dialect = new Oracle12cDialect();
        //
        BoundSql countSql = dialect.countSql(this.queryBoundSql);
        assert countSql.getSqlString().equals("SELECT COUNT(*) FROM (select * from tb_user where age > 12 and sex = ?) TEMP_T");
        assert countSql.getArgs().length == 1;
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("select * from tb_user where age > 12 and sex = ? OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(1);
        assert pageSql.getArgs()[2].equals(3);
    }

    @Test
    public void dialect_sqlserver2005_1() {
        SqlDialect dialect = new SqlServer2005Dialect();
        //
        BoundSql pageSql = dialect.pageSql(this.queryBoundSql, 1, 3);
        assert pageSql.getSqlString().equals("WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  * from tb_user where age > 12 and sex = ?) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 4 ORDER BY __row_number__");
        assert pageSql.getArgs().length == 3;
        assert pageSql.getArgs()[0].equals('F');
        assert pageSql.getArgs()[1].equals(2L);
        assert pageSql.getArgs()[2].equals(4L);
    }
}
