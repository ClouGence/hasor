package net.hasor.db.dal.repository;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.test.db.dal.Mapper2Dal;
import net.hasor.test.db.dal.dynamic.TextBuilderContext;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Mapper2DalTest {
    private String loadString(String queryConfig) throws IOException {
        return IOUtils.readToString(ResourcesUtils.getResourceAsStream(queryConfig), "UTF-8");
    }

    @Before
    public void loadMapping() throws IOException {
        MapperRegistry.DEFAULT.loadMapper(Mapper2Dal.class, true);
    }

    @Test
    public void bindTest_01() throws Throwable {
        DynamicSql parseXml = MapperRegistry.DEFAULT.findDynamicSql(Mapper2Dal.class, "testBind");
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/mapper_result/Mapper2Dal_testBind.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("sellerId", "123");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("123abc");
    }

    @Test
    public void bindTest_02() {
        DynamicSql parseXml = MapperRegistry.DEFAULT.findDynamicSql(Mapper2Dal.class, "testBind");
        //
        Map<String, Object> data1 = new HashMap<>();
        data1.put("sellerId", "123");
        data1.put("abc", "aaa");
        try {
            parseXml.buildQuery(new TextBuilderContext(data1));
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("duplicate key 'abc'");
        }
    }

    @Test
    public void chooseTest_01() throws Throwable {
        DynamicSql parseXml = MapperRegistry.DEFAULT.findDynamicSql(Mapper2Dal.class, "testChoose");
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/mapper_result/Mapper2Dal_testChoose.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("title", "123");
        data1.put("content", "aaa");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("123");
        assert builder1.getArgs()[1].equals("aaa");
    }

    @Test
    public void chooseTest_02() throws Throwable {
        DynamicSql parseXml = MapperRegistry.DEFAULT.findDynamicSql(Mapper2Dal.class, "testChoose");
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/mapper_result/Mapper2Dal_testChoose.sql_2");
        Map<String, Object> data1 = new HashMap<>();
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
    }

    @Test
    public void foreachTest_01() throws Throwable {
        DynamicSql parseXml = MapperRegistry.DEFAULT.findDynamicSql(Mapper2Dal.class, "testForeach");
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/mapper_result/Mapper2Dal_testForeach.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("eventTypes", Arrays.asList("a", "b", "c", "d", "e"));
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("a");
        assert builder1.getArgs()[1].equals("b");
        assert builder1.getArgs()[2].equals("c");
        assert builder1.getArgs()[3].equals("d");
        assert builder1.getArgs()[4].equals("e");
    }

    @Test
    public void ifTest_01() throws Throwable {
        DynamicSql parseXml = MapperRegistry.DEFAULT.findDynamicSql(Mapper2Dal.class, "testIf");
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/mapper_result/Mapper2Dal_testIf.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("ownerID", "123");
        data1.put("ownerType", "SYSTEM");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("123");
        assert builder1.getArgs()[1].equals("SYSTEM");
        //
        //
        String querySql2 = loadString("/net_hasor_db/dal_dynamic/mapper_result/Mapper2Dal_testIf.sql_2");
        Map<String, Object> data2 = new HashMap<>();
        data1.put("ownerID", "123");
        data1.put("ownerType", null);
        QuerySqlBuilder builder2 = parseXml.buildQuery(new TextBuilderContext(data2));
        assert builder2.getSqlString().trim().equals(querySql2.trim());
        assert builder2.getArgs().length == 0;
    }
}
