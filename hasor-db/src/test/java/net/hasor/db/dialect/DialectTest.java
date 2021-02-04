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
import net.hasor.db.types.mapping.FieldInfo;
import net.hasor.db.types.mapping.MappingHandler;
import net.hasor.db.types.mapping.MappingRowMapper;
import net.hasor.db.types.mapping.TableInfo;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TbUser;
import org.junit.Test;

/***
 * 字典
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class DialectTest extends AbstractDbTest {
    @Test
    public void dialect_default_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate("");
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("userUUID");
        assert buildTableName.equals("tb_user");
        assert buildCondition.equals("userUUID");
    }

    @Test
    public void dialect_mysql_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.MYSQL);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("`userUUID`");
        assert buildTableName.equals("`tb_user`");
        assert buildCondition.equals("`userUUID`");
    }

    @Test
    public void dialect_postgresql_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.POSTGRESQL);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("\"userUUID\"");
        assert buildTableName.equals("\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
    }

    @Test
    public void dialect_oracle_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.ORACLE);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("\"userUUID\"");
        assert buildTableName.equals("\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
    }

    @Test
    public void dialect_h2_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.H2);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("\"userUUID\"");
        assert buildTableName.equals("\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
    }

    @Test
    public void dialect_hive_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.HIVE);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("\"userUUID\"");
        assert buildTableName.equals("\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
    }

    @Test
    public void dialect_sqllite_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.SQLITE);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("`userUUID`");
        assert buildTableName.equals("`tb_user`");
        assert buildCondition.equals("`userUUID`");
    }

    @Test
    public void dialect_herddb_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.HERDDB);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("`userUUID`");
        assert buildTableName.equals("`tb_user`");
        assert buildCondition.equals("`userUUID`");
    }

    @Test
    public void dialect_sqlserver2012_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate("sqlserver2012");
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("[userUUID]");
        assert buildTableName.equals("[tb_user]");
        assert buildCondition.equals("[userUUID]");
    }

    @Test
    public void dialect_informix_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.INFORMIX);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("\"userUUID\"");
        assert buildTableName.equals("\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
    }

    @Test
    public void dialect_db2_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.DB2);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("\"userUUID\"");
        assert buildTableName.equals("\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
    }

    @Test
    public void dialect_hsql_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.HSQL);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("\"userUUID\"");
        assert buildTableName.equals("\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
    }

    @Test
    public void dialect_phoenix_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.PHOENIX);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("\"userUUID\"");
        assert buildTableName.equals("\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
    }

    @Test
    public void dialect_impala_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.IMPALA);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("\"userUUID\"");
        assert buildTableName.equals("\"tb_user\"");
        assert buildCondition.equals("\"userUUID\"");
    }

    @Test
    public void dialect_mariadb_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.MARIADB);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("`userUUID`");
        assert buildTableName.equals("`tb_user`");
        assert buildCondition.equals("`userUUID`");
    }

    @Test
    public void dialect_aliyun_ads_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.ALIYUN_ADS);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("`userUUID`");
        assert buildTableName.equals("`tb_user`");
        assert buildCondition.equals("`userUUID`");
    }

    @Test
    public void dialect_aliyun_drds_1() {
        MappingHandler handler = MappingHandler.DEFAULT;
        MappingRowMapper<TbUser> rowMapper = handler.resolveMapper(TbUser.class);
        TableInfo tableInfo = rowMapper.getTableInfo();
        FieldInfo property = rowMapper.findFieldInfoByProperty("uid");
        //
        SqlDialect dialect = SqlDialectRegister.findOrCreate(JdbcUtils.ALIYUN_DRDS);
        String buildSelect = dialect.buildSelect(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        String buildTableName = dialect.buildTableName(tableInfo.getCategory(), tableInfo.getTableName());
        String buildCondition = dialect.buildColumnName(tableInfo.getCategory(), tableInfo.getTableName(), property.getColumnName(), property.getJdbcType(), property.getJavaType());
        //
        assert buildSelect.equals("`userUUID`");
        assert buildTableName.equals("`tb_user`");
        assert buildCondition.equals("`userUUID`");
    }
}
