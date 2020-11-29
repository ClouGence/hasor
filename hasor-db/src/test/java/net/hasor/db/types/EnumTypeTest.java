package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.types.handler.EnumTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.CharacterSensitiveEnum;
import net.hasor.test.db.dto.LicenseOfCodeEnum;
import net.hasor.test.db.dto.LicenseOfValueEnum;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class EnumTypeTest {
    @Test
    public void testEnumTypeHandler_CharacterSensitive_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('READ_UNCOMMITTED');");
            Isolation dat1 = jdbcTemplate.queryForObject("select c_varchar from tb_h2types where c_varchar is not null limit 1;", Isolation.class);
            assert dat1 == Isolation.READ_UNCOMMITTED;
            //
            CharacterSensitiveEnum dat2 = jdbcTemplate.queryForObject("select 'a';", CharacterSensitiveEnum.class);
            assert dat2 == CharacterSensitiveEnum.a;
            CharacterSensitiveEnum dat3 = jdbcTemplate.queryForObject("select 'A';", CharacterSensitiveEnum.class);
            assert dat3 == CharacterSensitiveEnum.a;
        }
    }

    @Test
    public void testEnumTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('READ_UNCOMMITTED');");
            List<Isolation> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 1;", (rs, rowNum) -> {
                return new EnumTypeHandler<>(Isolation.class).getResult(rs, 1);
            });
            //
            assert dat.get(0) == Isolation.READ_UNCOMMITTED;
        }
    }

    @Test
    public void testEnumTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('READ_UNCOMMITTED');");
            List<Isolation> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 1;", (rs, rowNum) -> {
                return new EnumTypeHandler<>(Isolation.class).getResult(rs, "c_varchar");
            });
            //
            assert dat.get(0) == Isolation.READ_UNCOMMITTED;
        }
    }

    @Test
    public void testEnumTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<Isolation> dat = jdbcTemplate.query("select ?", ps -> {
                new EnumTypeHandler<>(Isolation.class).setParameter(ps, 1, Isolation.READ_UNCOMMITTED, null);
            }, (rs, rowNum) -> {
                return new EnumTypeHandler<>(Isolation.class).getNullableResult(rs, 1);
            });
            //
            assert dat.get(0) == Isolation.READ_UNCOMMITTED;
        }
    }

    @Test
    public void testEnumTypeHandler_4() throws SQLException {
    }

    @Test
    public void testEnumTypeHandler_ofCode_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('Apache 2.0');");
            List<LicenseOfCodeEnum> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 1;", (rs, rowNum) -> {
                return new EnumTypeHandler<>(LicenseOfCodeEnum.class).getResult(rs, 1);
            });
            //
            assert dat.get(0) == LicenseOfCodeEnum.Apache2;
        }
    }

    @Test
    public void testEnumTypeHandler_ofCode_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('Apache 2.0');");
            List<LicenseOfCodeEnum> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 1;", (rs, rowNum) -> {
                return new EnumTypeHandler<>(LicenseOfCodeEnum.class).getResult(rs, "c_varchar");
            });
            //
            assert dat.get(0) == LicenseOfCodeEnum.Apache2;
        }
    }

    @Test
    public void testEnumTypeHandler_ofCode_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<LicenseOfCodeEnum> dat = jdbcTemplate.query("select ?", ps -> {
                new EnumTypeHandler<>(LicenseOfCodeEnum.class).setParameter(ps, 1, LicenseOfCodeEnum.Apache2, null);
            }, (rs, rowNum) -> {
                return new EnumTypeHandler<>(LicenseOfCodeEnum.class).getNullableResult(rs, 1);
            });
            //
            assert dat.get(0) == LicenseOfCodeEnum.Apache2;
        }
    }

    @Test
    public void testEnumTypeHandler_ofCode_4() throws SQLException {
    }

    @Test
    public void testEnumTypeHandler_ofValue_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_integer) values (4);");
            List<LicenseOfValueEnum> dat = jdbcTemplate.query("select c_integer from tb_h2types where c_integer is not null limit 1;", (rs, rowNum) -> {
                return new EnumTypeHandler<>(LicenseOfValueEnum.class).getResult(rs, 1);
            });
            //
            assert dat.get(0) == LicenseOfValueEnum.Apache2;
        }
    }

    @Test
    public void testEnumTypeHandler_ofValue_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_integer) values (4);");
            List<LicenseOfValueEnum> dat = jdbcTemplate.query("select c_integer from tb_h2types where c_integer is not null limit 1;", (rs, rowNum) -> {
                return new EnumTypeHandler<>(LicenseOfValueEnum.class).getResult(rs, "c_integer");
            });
            //
            assert dat.get(0) == LicenseOfValueEnum.Apache2;
        }
    }

    @Test
    public void testEnumTypeHandler_ofValue_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<LicenseOfValueEnum> dat = jdbcTemplate.query("select ?", ps -> {
                new EnumTypeHandler<>(LicenseOfValueEnum.class).setParameter(ps, 1, LicenseOfValueEnum.Apache2, null);
            }, (rs, rowNum) -> {
                return new EnumTypeHandler<>(LicenseOfValueEnum.class).getNullableResult(rs, 1);
            });
            //
            assert dat.get(0) == LicenseOfValueEnum.Apache2;
        }
    }

    @Test
    public void testEnumTypeHandler_ofValue_4() throws SQLException {
    }
}