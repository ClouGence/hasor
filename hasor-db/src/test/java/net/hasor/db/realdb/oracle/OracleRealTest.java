package net.hasor.db.realdb.oracle;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.metadata.provider.JdbcMetadataProvider;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class OracleRealTest {
    protected void preTable(JdbcTemplate jdbcTemplate) throws SQLException, IOException {
        try {
            jdbcTemplate.executeUpdate("drop table tb_oracle_types");
        } catch (Exception e) {
            /**/
        }
        jdbcTemplate.loadSQL("/net_hasor_db/tb_oracle_types.sql");
    }

    // oracle  dbms_metadata.get_ddl result is CLOB
    @Test
    public void queryForObject_String_1() throws Exception {
        try (Connection conn = DsUtils.localOracle();) {
            //
            JdbcMetadataProvider provider = new JdbcMetadataProvider(conn);
            provider.getVersion();
            //
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            preTable(jdbcTemplate);
            //
            String ddl = jdbcTemplate.queryForString("select dbms_metadata.get_ddl('TABLE',?,?) from dual", "TB_ORACLE_TYPES", "SCOTT");
            assert true;
        }
    }
}
