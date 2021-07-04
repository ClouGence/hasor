package net.hasor.db.dal;
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.lambda.LambdaOperations;
import net.hasor.db.lambda.LambdaTemplate;
import net.hasor.db.mapping.MappingRegistry;
import net.hasor.db.metadata.MetaDataService;
import net.hasor.db.metadata.provider.JdbcMetadataProvider;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.test.db.dto.TbUser;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DalTest {
    @Test
    public void test2() throws SQLException {
        System.out.println(UUID.randomUUID().toString());
    }

    @Test
    public void test() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            // 元信息服务（可选）
            MetaDataService metaDataService = new JdbcMetadataProvider(dataSource);
            // 类型处理器（可选）
            TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
            // ORM注册管理（可选）
            MappingRegistry mappingRegistry = new MappingRegistry(typeHandlerRegistry, metaDataService, null);
            //
            // 两个相关的数据操作接口
            LambdaTemplate lambdaTemplate = new LambdaTemplate(dataSource, mappingRegistry);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource, mappingRegistry);
            // Lambda 操作
            LambdaOperations.LambdaQuery<TbUser> lambdaQuery = lambdaTemplate.lambdaQuery(TbUser.class);
            List<TbUser> tbUsers = lambdaQuery.queryForList();
            assert tbUsers.size() == 3;
        }
    }
}
