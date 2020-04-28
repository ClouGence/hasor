package net.example.hasor.config;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.DimModule;
import net.hasor.dataway.DatawayService;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.PreExecuteChainSpi;
import net.hasor.dataway.spi.ResultProcessChainSpi;
import net.hasor.dataway.spi.StatusMessageException;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.spring.SpringModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;

@DimModule
@Component
public class ExampleModule implements SpringModule {
    @Autowired
    private DataSource dataSource = null;

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // .DataSource form Spring boot into Hasor
        apiBinder.installModule(new JdbcModule(Level.Full, this.dataSource));
        // .custom DataQL
        //apiBinder.tryCast(QueryApiBinder.class).loadUdfSource(apiBinder.findClass(DimUdfSource.class));
        //
        apiBinder.bindSpiListener(PreExecuteChainSpi.class, (apiInfo, future) -> {
            apiInfo.getParameterMap().put("self", "me");
            if (apiInfo.getApiPath().equals("/api/demos/find_user_by_name")) {
                future.completed(new HashMap<String, Object>() {{
                    put("status", false);
                    put("message", "no power");
                }});
            }
            // future.failed(new StatusMessageException(401, "not power"));
        });
        apiBinder.bindSpiListener(ResultProcessChainSpi.class, new ResultProcessChainSpi() {
            public Object callAfter(boolean formPre, ApiInfo apiInfo, Object result) {
                if (formPre) {
                    //为了避免和 PreExecuteChainSpi 的冲突，如果前置拦截器处理了。那么后置拦截器就不处理。
                    return result;
                }
                if (apiInfo.getApiPath().equals("/demos/mock")) {
                    throw new StatusMessageException(401, "not power2222");
                }
                return new HashMap<String, Object>() {{
                    put("method", apiInfo.getMethod());
                    put("path", apiInfo.getApiPath());
                    put("result", result);
                }};
            }

            public Object callError(boolean formPre, ApiInfo apiInfo, Throwable e) {
                e.printStackTrace();
                return new HashMap<String, Object>() {{
                    put("method", apiInfo.getMethod());
                    put("path", apiInfo.getApiPath());
                    put("errorMessage", e.getMessage());
                }};
            }
        });
    }

    @Override
    public void onStart(AppContext appContext) throws Throwable {
        DatawayService datawayService = appContext.getInstance(DatawayService.class);
        //        Map<String, Object> objectMap = datawayService.invokeApi("post", "/api/demos/find_user_by_name", new HashMap<String, Object>() {{
        //            put("userName", "1");
        //        }});
        //
        //        System.out.println(JSONObject.toJSONString(objectMap));
    }
}