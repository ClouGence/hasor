package net.example.hasor.config;
import com.alibaba.fastjson.JSONObject;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.DimModule;
import net.hasor.dataway.DatawayService;
import net.hasor.dataway.spi.PreExecuteChainSpi;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.spring.SpringModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

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
        apiBinder.bindSpiListener(PreExecuteChainSpi.class, (apiInfo, future) -> {
            apiInfo.getParameterMap().put("self", "me");
        });
    }

    @Override
    public void onStart(AppContext appContext) throws Throwable {
        DatawayService datawayService = appContext.getInstance(DatawayService.class);
        Map<String, Object> objectMap = datawayService.invokeApi("post", "/api/demos/find_user_by_name", new HashMap<String, Object>() {{
            put("userName", "1");
        }});
        //
        System.out.println(JSONObject.toJSONString(objectMap));
    }
}