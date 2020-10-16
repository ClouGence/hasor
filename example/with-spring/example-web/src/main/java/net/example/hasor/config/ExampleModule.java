package net.example.hasor.config;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.DimModule;
import net.hasor.dataql.QueryApiBinder;
import net.hasor.dataql.fx.db.FxSqlCheckChainSpi;
import net.hasor.dataway.DatawayService;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.ResultProcessChainSpi;
import net.hasor.db.JdbcModule;
import net.hasor.db.JdbcUtils;
import net.hasor.db.Level;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.spring.SpringModule;
import net.hasor.utils.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;

@DimModule
@Component
public class ExampleModule implements SpringModule {
    //    @NacosInjected
    //    private ConfigService configService;
    @Autowired
    private DataSource dataSource = null;

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //        apiBinder.tryCast(QueryApiBinder.class).loadUdf(
        //                Object.class,
        //                springTypeSupplier(apiBinder)
        //        );
        //        apiBinder.tryCast(QueryApiBinder.class).addShareVarInstance(
        //                "name",
        //                ..
        //        );
        //
        //
        //
        //apiBinder.bindType(EurekaClient.class).toProvider(getSupplierOfType(apiBinder, EurekaClient.class));
        //        apiBinder.bindType(ConfigService.class).toInstance(this.configService);
        //apiBinder.bindType(NamingService.class).toInstance(this.namingService);
        //        try {
        //            String serverAddr = "{serverAddr}";
        //            String dataId = "{dataId}";
        //            String group = "{group}";
        //            Properties properties = new Properties();
        //            properties.put("serverAddr", serverAddr);
        //            ConfigService configService = NacosFactory.createConfigService(properties);
        //            String content = configService.getConfig(dataId, group, 5000);
        //            System.out.println(content);
        //        } catch (NacosException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        //
        new JdbcTemplate(this.dataSource).execute((ConnectionCallback<String>) con -> {
            DatabaseMetaData metaData = con.getMetaData();
            String dbType = JdbcUtils.getDbType(metaData.getURL(), metaData.getDriverName());
            boolean localDB = dbType.equalsIgnoreCase(JdbcUtils.SQLITE) //
                    || dbType.equalsIgnoreCase(JdbcUtils.H2)//
                    || dbType.equalsIgnoreCase(JdbcUtils.DERBY)//
                    || dbType.equalsIgnoreCase(JdbcUtils.HSQL);
            if (localDB) {
                try {
                    if (dbType.equalsIgnoreCase(JdbcUtils.HSQL)) {
                        new JdbcTemplate(con).execute("drop table interface_info if exists");
                    } else {
                        new JdbcTemplate(con).execute("drop table interface_info");
                    }
                } catch (SQLException e) { /**/ }
                try {
                    if (dbType.equalsIgnoreCase(JdbcUtils.HSQL)) {
                        new JdbcTemplate(con).execute("drop table interface_release if exists");
                    } else {
                        new JdbcTemplate(con).execute("drop table interface_release");
                    }
                } catch (SQLException e) { /**/ }
                try {
                    if (dbType.equalsIgnoreCase(JdbcUtils.DERBY)) {
                        new JdbcTemplate(con).loadSplitSQL(";", "/META-INF/hasor-framework/" + dbType.toLowerCase() + "/interface_info.sql");
                        new JdbcTemplate(con).loadSplitSQL(";", "/META-INF/hasor-framework/" + dbType.toLowerCase() + "/interface_release.sql");
                    } else {
                        new JdbcTemplate(con).loadSQL("/META-INF/hasor-framework/" + dbType.toLowerCase() + "/interface_info.sql");
                        new JdbcTemplate(con).loadSQL("/META-INF/hasor-framework/" + dbType.toLowerCase() + "/interface_release.sql");
                    }
                } catch (IOException e) {
                    throw ExceptionUtils.toRuntimeException(e);
                }
            }
            return "OK";
        });
        //
        //        apiBinder.tryCast(QueryApiBinder.class).loadUdf(Object.class, springTypeSupplier(apiBinder));
        // .DataSource form Spring boot into Hasor
        apiBinder.installModule(new JdbcModule(Level.Full, this.dataSource));
        // .custom DataQL
        //
        //        // .负责首页导出 CVS
        apiBinder.bindSpiListener(FxSqlCheckChainSpi.class, infoObject -> {
            System.out.println(String.format("[%s] %s", infoObject.getSourceName(), infoObject.getQueryString().trim()));
            return FxSqlCheckChainSpi.NEXT;
        });
        //
        //apiBinder.tryCast(QueryApiBinder.class).loadUdfSource(apiBinder.findClass(DimUdfSource.class));
        //        final Set<String> codeSet = AuthorizationType.Group_ReadOnly.toCodeSet();
        //        apiBinder.bindSpiListener(AuthorizationChainSpi.class, (checkType, apiId, defaultCheck) -> {
        //            return checkType.testAuthorization(codeSet);
        //        });
        //        apiBinder.bindSpiListener(PreExecuteChainSpi.class, (apiInfo, future) -> {
        //            apiInfo.getParameterMap().put("self", "me");
        //            if (apiInfo.getApiPath().equals("/api/demos/find_user_by_name")) {
        //                future.completed(new HashMap<String, Object>() {{
        //                    put("status", false);
        //                    put("message", "no power");
        //                }});
        //            }
        //            // future.failed(new StatusMessageException(401, "not power"));
        //        });
        //
//        apiBinder.bindSpiListener(ResultProcessChainSpi.class, new ResultProcessChainSpi() {
//            public Object callError(boolean formPre, ApiInfo apiInfo, Throwable e) {
//                return new HashMap<String, Object>() {{
//                    put("method", apiInfo.getMethod());
//                    put("path", apiInfo.getApiPath());
//                    put("errorMessage", e.getMessage());
//                }};
//            }
//        });
        //        {
        //
        //
        //        apiBinder.bindSpiListener(SerializationChainSpi.class, (apiInfo, mimeType, result) -> {
        //            if (!"/api/serialization".equalsIgnoreCase(apiInfo.getApiPath())) {
        //                return result;
        //            }
        //            String jsonString = JSON.toJSONString(result);
        //            return SerializationChainSpi.SerializationInfo.ofBytes("abc/text/plain", jsonString.getBytes()); //JSON.toJSONString(result);
        //        });
        //
        //        apiBinder.bindSpiListener(SerializationChainSpi.class, (apiInfo, mimeType, result) -> {
        //            //
        //            try {
        //                BufferedImage bi = new BufferedImage(150, 70, BufferedImage.TYPE_INT_RGB);
        //                Graphics2D g2 = (Graphics2D) bi.getGraphics();
        //                // background color
        //                g2.fillRect(0, 0, 150, 70);
        //                g2.setColor(Color.WHITE);
        //                // text
        //                g2.setFont(new Font("宋体", Font.BOLD, 18));
        //                g2.setColor(Color.BLACK);
        //                g2.drawString(String.valueOf(result), 3, 50);
        //                // save to bytes
        //                ByteArrayOutputStream oat = new ByteArrayOutputStream();
        //                ImageIO.write(bi, "JPEG", oat);
        //                //
        //                return SerializationChainSpi.SerializationInfo.ofBytes(//
        //                        mimeType.getMimeType("jpeg"),   // response context-type
        //                        oat.toByteArray()                       // response body
        //                );
        //            } catch (Exception e) {
        //                throw ExceptionUtils.toRuntimeException(e);
        //            }
        //        });
    }

    @Override
    public void onStart(AppContext appContext) throws Throwable {
        DatawayService datawayService = appContext.getInstance(DatawayService.class);
        //        Map<String, Object> objectMap = datawayService.invokeApi("post", "/api/demos/find_user_by_name", new HashMap<String, Object>() {{
        //            put("userName", "1");
        //        }});
        //        System.out.println(JSONObject.toJSONString(objectMap));
    }
}