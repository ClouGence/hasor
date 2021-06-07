package net.hasor.db.dal.repository;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RepositoryTest {
    private String loadString(String queryConfig) throws IOException {
        return IOUtils.readToString(ResourcesUtils.getResourceAsStream(queryConfig), "UTF-8");
    }

    @Test
    public void dynamicTest_05() throws Throwable {
        MapperRegistry.DEFAULT.loadMapper("/net_hasor_db/dal_dynamic/mapper_1.xml");
        //
        DynamicSql infoQueryByID = MapperRegistry.DEFAULT.findDynamicSql("sp_project_info_space", "projectInfo_queryByID");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("projectID", "123");
        QuerySqlBuilder buildQuery = infoQueryByID.buildQuery(new BuilderContext(data1));
        //
        //        assert buildQuery.getSqlString().trim().equals(querySql1.trim());
        //        assert buildQuery.getArgs()[0].equals("33322");
        //        assert buildQuery.getArgs()[1].equals(LicenseOfCodeEnum.Private);
        //        assert buildQuery.getArgs()[2].equals(LicenseOfCodeEnum.GPLv3);
        //        assert buildQuery.getArgs()[3].equals(CharacterSensitiveEnum.A);
        //        assert buildQuery.getArgs()[4]
        assert buildQuery.getArgs()[0].equals("123");
    }
}
