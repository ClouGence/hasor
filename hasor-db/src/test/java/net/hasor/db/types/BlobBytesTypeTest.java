package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.BlobBytesForWrapTypeHandler;
import net.hasor.db.types.handler.BlobBytesTypeHandler;
import net.hasor.db.types.handler.BlobInputStreamTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.utils.CommonCodeUtils;
import net.hasor.utils.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;

public class BlobBytesTypeTest {
    private byte[] toPrimitive(Byte[] bytes) {
        byte[] dat = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            dat[i] = bytes[i];
        }
        return dat;
    }

    private Byte[] toWrapped(byte[] bytes) {
        Byte[] dat = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            dat[i] = bytes[i];
        }
        return dat;
    }

    @Test
    public void testBlobBytesForWrapTypeHandler_1() throws SQLException, NoSuchAlgorithmException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            byte[] testData = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_blob) values (?);", new Object[] { testData });
            List<Byte[]> dat = jdbcTemplate.query("select c_blob from tb_h2types where c_blob is not null limit 1;", (rs, rowNum) -> {
                return new BlobBytesForWrapTypeHandler().getResult(rs, 1);
            });
            //
            String s1 = CommonCodeUtils.MD5.encodeMD5(testData);
            String s2 = CommonCodeUtils.MD5.encodeMD5(toPrimitive(dat.get(0)));
            assert s1.equals(s2);
        }
    }

    @Test
    public void testBlobBytesForWrapTypeHandler_2() throws SQLException, NoSuchAlgorithmException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            byte[] testData = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_blob) values (?);", new Object[] { testData });
            List<Byte[]> dat = jdbcTemplate.query("select c_blob from tb_h2types where c_blob is not null limit 1;", (rs, rowNum) -> {
                return new BlobBytesForWrapTypeHandler().getResult(rs, "c_blob");
            });
            //
            String s1 = CommonCodeUtils.MD5.encodeMD5(testData);
            String s2 = CommonCodeUtils.MD5.encodeMD5(toPrimitive(dat.get(0)));
            assert s1.equals(s2);
        }
    }

    @Test
    public void testBlobBytesForWrapTypeHandler_3() throws SQLException, NoSuchAlgorithmException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            byte[] testData = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            List<Byte[]> dat = jdbcTemplate.query("select ?", ps -> {
                new BlobBytesForWrapTypeHandler().setParameter(ps, 1, toWrapped(testData), JDBCType.BLOB);
            }, (rs, rowNum) -> {
                return new BlobBytesForWrapTypeHandler().getNullableResult(rs, 1);
            });
            //
            String s1 = CommonCodeUtils.MD5.encodeMD5(testData);
            String s2 = CommonCodeUtils.MD5.encodeMD5(toPrimitive(dat.get(0)));
            assert s1.equals(s2);
        }
    }

    @Test
    public void testBlobBytesForWrapTypeHandler_4() throws SQLException {
        //        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
        //            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        //            //
        //            jdbcTemplate.executeUpdate("CREATE ALIAS AS_BIGINTEGER FOR \"net.hasor.test.db.CallableFunction.asBigInteger\";");
        //            BigDecimal bigDecimal = jdbcTemplate.execute("call AS_BIGINTEGER(?)", (CallableStatementCallback<BigDecimal>) cs -> {
        //                cs.ge
        //                return null;
        //            });
        //            assert bigDecimal.intValue() == 123;
        //        }
    }

    @Test
    public void testBlobBytesTypeHandler_1() throws SQLException, NoSuchAlgorithmException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            byte[] testData = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_blob) values (?);", new Object[] { testData });
            List<byte[]> dat = jdbcTemplate.query("select c_blob from tb_h2types where c_blob is not null limit 1;", (rs, rowNum) -> {
                return new BlobBytesTypeHandler().getResult(rs, 1);
            });
            //
            String s1 = CommonCodeUtils.MD5.encodeMD5(testData);
            String s2 = CommonCodeUtils.MD5.encodeMD5(dat.get(0));
            assert s1.equals(s2);
        }
    }

    @Test
    public void testBlobBytesTypeHandler_2() throws SQLException, NoSuchAlgorithmException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            byte[] testData = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_blob) values (?);", new Object[] { testData });
            List<byte[]> dat = jdbcTemplate.query("select c_blob from tb_h2types where c_blob is not null limit 1;", (rs, rowNum) -> {
                return new BlobBytesTypeHandler().getResult(rs, "c_blob");
            });
            //
            String s1 = CommonCodeUtils.MD5.encodeMD5(testData);
            String s2 = CommonCodeUtils.MD5.encodeMD5(dat.get(0));
            assert s1.equals(s2);
        }
    }

    @Test
    public void testBlobBytesTypeHandler_3() throws SQLException, NoSuchAlgorithmException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            byte[] testData = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            List<byte[]> dat = jdbcTemplate.query("select ?", ps -> {
                new BlobBytesTypeHandler().setParameter(ps, 1, testData, JDBCType.BLOB);
            }, (rs, rowNum) -> {
                return new BlobBytesTypeHandler().getNullableResult(rs, 1);
            });
            //
            String s1 = CommonCodeUtils.MD5.encodeMD5(testData);
            String s2 = CommonCodeUtils.MD5.encodeMD5(dat.get(0));
            assert s1.equals(s2);
        }
    }

    @Test
    public void testBlobBytesTypeHandler_4() throws SQLException {
        //        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
        //            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        //            //
        //            jdbcTemplate.executeUpdate("CREATE ALIAS AS_BIGINTEGER FOR \"net.hasor.test.db.CallableFunction.asBigInteger\";");
        //            BigDecimal bigDecimal = jdbcTemplate.execute("call AS_BIGINTEGER(?)", (CallableStatementCallback<BigDecimal>) cs -> {
        //                cs.ge
        //                return null;
        //            });
        //            assert bigDecimal.intValue() == 123;
        //        }
    }

    @Test
    public void testBlobInputStreamTypeHandler_1() throws Exception {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            byte[] testData = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_blob) values (?);", new Object[] { testData });
            List<InputStream> dat = jdbcTemplate.query("select c_blob from tb_h2types where c_blob is not null limit 1;", (rs, rowNum) -> {
                return new BlobInputStreamTypeHandler().getResult(rs, 1);
            });
            //
            String s1 = CommonCodeUtils.MD5.encodeMD5(testData);
            String s2 = CommonCodeUtils.MD5.encodeMD5(IOUtils.toByteArray(dat.get(0)));
            assert s1.equals(s2);
        }
    }

    @Test
    public void testBlobInputStreamTypeHandler_2() throws Exception {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            byte[] testData = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_blob) values (?);", new Object[] { testData });
            List<InputStream> dat = jdbcTemplate.query("select c_blob from tb_h2types where c_blob is not null limit 1;", (rs, rowNum) -> {
                return new BlobInputStreamTypeHandler().getResult(rs, "c_blob");
            });
            //
            String s1 = CommonCodeUtils.MD5.encodeMD5(testData);
            String s2 = CommonCodeUtils.MD5.encodeMD5(IOUtils.toByteArray(dat.get(0)));
            assert s1.equals(s2);
        }
    }

    @Test
    public void testBlobInputStreamTypeHandler_3() throws Exception {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            byte[] testData = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            List<InputStream> dat = jdbcTemplate.query("select ?", ps -> {
                new BlobInputStreamTypeHandler().setParameter(ps, 1, new ByteArrayInputStream(testData), JDBCType.BLOB);
            }, (rs, rowNum) -> {
                return new BlobInputStreamTypeHandler().getNullableResult(rs, 1);
            });
            //
            String s1 = CommonCodeUtils.MD5.encodeMD5(testData);
            String s2 = CommonCodeUtils.MD5.encodeMD5(IOUtils.toByteArray(dat.get(0)));
            assert s1.equals(s2);
        }
    }

    @Test
    public void testBlobInputStreamTypeHandler_4() throws SQLException {
        //        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
        //            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        //            //
        //            jdbcTemplate.executeUpdate("CREATE ALIAS AS_BIGINTEGER FOR \"net.hasor.test.db.CallableFunction.asBigInteger\";");
        //            BigDecimal bigDecimal = jdbcTemplate.execute("call AS_BIGINTEGER(?)", (CallableStatementCallback<BigDecimal>) cs -> {
        //                cs.ge
        //                return null;
        //            });
        //            assert bigDecimal.intValue() == 123;
        //        }
    }
    //
}