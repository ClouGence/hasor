package net.hasor.db.types;
import net.hasor.db.jdbc.SqlParameterUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.SqlXmlForInputStreamTypeHandler;
import net.hasor.db.types.handler.SqlXmlForReaderTypeHandler;
import net.hasor.db.types.handler.SqlXmlTypeHandler;
import net.hasor.test.db.utils.DsUtils;
import net.hasor.utils.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SqlXmlTypeTest {
    protected void preTable(JdbcTemplate jdbcTemplate) throws SQLException {
        try {
            jdbcTemplate.executeUpdate("drop table tb_oracle_types_onlyxml");
        } catch (Exception e) {
            /**/
        }
        jdbcTemplate.executeUpdate("create table tb_oracle_types_onlyxml (c_xml xmltype)");
    }

    protected void preProc(JdbcTemplate jdbcTemplate) throws SQLException {
        try {
            jdbcTemplate.executeUpdate("drop procedure proc_xmltype");
        } catch (Exception e) {
            /**/
        }
        jdbcTemplate.execute(""//
                + "create or replace procedure proc_xmltype(p_out out xmltype) as " //
                + "begin " //
                + "  SELECT (XMLTYPE('<xml>abc</xml>')) into p_out FROM DUAL; " //
                + "end;");
    }

    @Test
    public void testSqlXmlTypeHandler_1() throws SQLException {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            preTable(jdbcTemplate);
            //
            jdbcTemplate.executeUpdate("insert into tb_oracle_types_onlyxml (c_xml) values ('<xml>abc</xml>')");
            List<String> dat = jdbcTemplate.query("select c_xml from tb_oracle_types_onlyxml where c_xml is not null", (rs, rowNum) -> {
                return new SqlXmlTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).trim().equals("<xml>abc</xml>");
        }
    }

    @Test
    public void testSqlXmlTypeHandler_2() throws SQLException {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            preTable(jdbcTemplate);
            //
            jdbcTemplate.executeUpdate("insert into tb_oracle_types_onlyxml (c_xml) values ('<xml>abc</xml>')");
            List<String> dat = jdbcTemplate.query("select c_xml from tb_oracle_types_onlyxml where c_xml is not null", (rs, rowNum) -> {
                return new SqlXmlTypeHandler().getResult(rs, "c_xml");
            });
            assert dat.get(0).trim().equals("<xml>abc</xml>");
        }
    }

    @Test
    public void testSqlXmlTypeHandler_3() throws SQLException {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            //
            List<String> dat = jdbcTemplate.query("select ? from dual", ps -> {
                new SqlXmlTypeHandler().setParameter(ps, 1, "<xml>abc</xml>", JDBCType.SQLXML);
            }, (rs, rowNum) -> {
                return new SqlXmlTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0).equals("<xml>abc</xml>");
        }
    }

    @Test
    public void testSqlXmlTypeHandler_4() throws SQLException {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            preProc(jdbcTemplate);
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_xmltype(?)}",//
                    Collections.singletonList(SqlParameterUtils.withOutput("out", JDBCType.SQLXML, new SqlXmlTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof String;
            assert objectMap.get("out").equals("<xml>abc</xml>");
            assert objectMap.get("#update-count-1").equals(-1);// in oracle ,no more result is -1
        }
    }

    @Test
    public void testSqlXmlForInputStreamTypeHandler_1() throws Exception {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            preTable(jdbcTemplate);
            //
            jdbcTemplate.executeUpdate("insert into tb_oracle_types_onlyxml (c_xml) values ('<xml>abc</xml>')");
            List<InputStream> dat = jdbcTemplate.query("select c_xml from tb_oracle_types_onlyxml where c_xml is not null", (rs, rowNum) -> {
                return new SqlXmlForInputStreamTypeHandler().getResult(rs, 1);
            });
            String xmlBody = IOUtils.readToString(dat.get(0), "UTF-8");
            assert xmlBody.equals("<xml>abc</xml>");
        }
    }

    @Test
    public void testSqlXmlForInputStreamTypeHandler_2() throws Exception {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            preTable(jdbcTemplate);
            //
            jdbcTemplate.executeUpdate("insert into tb_oracle_types_onlyxml (c_xml) values ('<xml>abc</xml>')");
            List<InputStream> dat = jdbcTemplate.query("select c_xml from tb_oracle_types_onlyxml where c_xml is not null", (rs, rowNum) -> {
                return new SqlXmlForInputStreamTypeHandler().getResult(rs, "c_xml");
            });
            String xmlBody = IOUtils.readToString(dat.get(0), "UTF-8");
            assert xmlBody.equals("<xml>abc</xml>");
        }
    }

    @Test
    public void testSqlXmlForInputStreamTypeHandler_3() throws Exception {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            //
            List<InputStream> dat = jdbcTemplate.query("select ? from dual", ps -> {
                new SqlXmlForInputStreamTypeHandler().setParameter(ps, 1, new ByteArrayInputStream("<xml>abc</xml>".getBytes()), JDBCType.SQLXML);
            }, (rs, rowNum) -> {
                return new SqlXmlForInputStreamTypeHandler().getNullableResult(rs, 1);
            });
            String xmlBody = IOUtils.readToString(dat.get(0), "UTF-8");
            assert xmlBody.equals("<xml>abc</xml>");
        }
    }

    @Test
    public void testSqlXmlForInputStreamTypeHandler_4() throws Exception {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            preProc(jdbcTemplate);
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_xmltype(?)}",//
                    Collections.singletonList(SqlParameterUtils.withOutput("out", JDBCType.SQLXML, new SqlXmlForInputStreamTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof InputStream;
            String xmlBody = IOUtils.readToString((InputStream) objectMap.get("out"), "UTF-8");
            assert xmlBody.equals("<xml>abc</xml>");
            assert objectMap.get("#update-count-1").equals(-1);// in oracle ,no more result is -1
        }
    }

    @Test
    public void testSqlXmlForReaderTypeHandler_1() throws Exception {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            preTable(jdbcTemplate);
            //
            jdbcTemplate.executeUpdate("insert into tb_oracle_types_onlyxml (c_xml) values ('<xml>abc</xml>')");
            List<Reader> dat = jdbcTemplate.query("select c_xml from tb_oracle_types_onlyxml where c_xml is not null", (rs, rowNum) -> {
                return new SqlXmlForReaderTypeHandler().getResult(rs, 1);
            });
            String xmlBody = IOUtils.readToString(dat.get(0));
            assert xmlBody.equals("<xml>abc</xml>");
        }
    }

    @Test
    public void testSqlXmlForReaderTypeHandler_2() throws Exception {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            preTable(jdbcTemplate);
            //
            jdbcTemplate.executeUpdate("insert into tb_oracle_types_onlyxml (c_xml) values ('<xml>abc</xml>')");
            List<Reader> dat = jdbcTemplate.query("select c_xml from tb_oracle_types_onlyxml where c_xml is not null", (rs, rowNum) -> {
                return new SqlXmlForReaderTypeHandler().getResult(rs, "c_xml");
            });
            String xmlBody = IOUtils.readToString(dat.get(0));
            assert xmlBody.equals("<xml>abc</xml>");
        }
    }

    @Test
    public void testSqlXmlForReaderTypeHandler_3() throws Exception {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            //
            List<Reader> dat = jdbcTemplate.query("select ? from dual", ps -> {
                new SqlXmlForReaderTypeHandler().setParameter(ps, 1, new StringReader("<xml>abc</xml>"), JDBCType.SQLXML);
            }, (rs, rowNum) -> {
                return new SqlXmlForReaderTypeHandler().getNullableResult(rs, 1);
            });
            String xmlBody = IOUtils.readToString(dat.get(0));
            assert xmlBody.equals("<xml>abc</xml>");
        }
    }

    @Test
    public void testSqlXmlForReaderTypeHandler_4() throws Exception {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            preProc(jdbcTemplate);
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_xmltype(?)}",//
                    Collections.singletonList(SqlParameterUtils.withOutput("out", JDBCType.SQLXML, new SqlXmlForReaderTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Reader;
            String xmlBody = IOUtils.readToString((Reader) objectMap.get("out"));
            assert xmlBody.equals("<xml>abc</xml>");
            assert objectMap.get("#update-count-1").equals(-1);// in oracle ,no more result is -1
        }
    }
}