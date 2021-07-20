package net.hasor.db.dal.execute;
import net.hasor.db.dal.dynamic.rule.RuleRegistry;
import net.hasor.db.dal.repository.MapperRegistry;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.metadata.AbstractMetadataServiceSupplierTest;
import net.hasor.db.metadata.provider.JdbcMetadataProvider;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class ExecuteTest extends AbstractMetadataServiceSupplierTest<JdbcMetadataProvider> {
    @Override
    protected Connection initConnection() throws SQLException {
        return DsUtils.localMySQL();
    }

    @Override
    protected JdbcMetadataProvider initRepository(Connection con) {
        return new JdbcMetadataProvider(con);
    }

    @Override
    protected void beforeTest(JdbcTemplate jdbcTemplate, JdbcMetadataProvider repository) throws SQLException, IOException {
        applySql("drop table tb_user");
        applySql("drop table proc_table_ref");
        applySql("drop table proc_table");
        applySql("drop table t3");
        applySql("drop table t1");
        applySql("drop table test_user");
        //
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/metadata/mysql_script.sql");
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/dal_dynamic/execute/execute_for_mysql.sql");
    }

    @Test
    public void dalProxy() throws IOException, SQLException {
        MapperRegistry.DEFAULT.loadMapper("/net_hasor_db/dal_dynamic/execute/execute.xml");
        MapperDalExecute dalExecute = new MapperDalExecute("net.hasor.test.db.dal.Mapper1Dal", MapperRegistry.DEFAULT, RuleRegistry.DEFAULT);
        //
        Object execute1 = dalExecute.execute(connection, "initUser", new HashMap<>());
        Object execute2 = dalExecute.execute(connection, "listUserList", new HashMap<>());
        //
        assert execute2 != null;
    }
}
