package net.hasor.db.dal.repository;
import org.junit.Test;

public class Mapper3DalTest {
    @Test
    public void bindTest_01() throws Throwable {
        MapperRegistry registry = new MapperRegistry();
        registry.loadMapper("/net_hasor_db/dal_dynamic/mapper_3.xml");
        //
        System.out.println();
        //        registry.findDynamicSql();
        //        Map<String, Object> data1 = new HashMap<>();
        //        data1.put("sellerId", "123");
        //        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        //        assert builder1.getSqlString().trim().equals(querySql1.trim());
        //        assert builder1.getArgs()[0].equals("123abc");
    }
}
