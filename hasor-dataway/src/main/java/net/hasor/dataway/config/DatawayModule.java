/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.dataway.config;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.QueryApiBinder;
import net.hasor.dataway.web.*;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.StringUtils;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dataway 启动入口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
public class DatawayModule implements WebModule {
    protected static    Logger logger            = LoggerFactory.getLogger(DatawayModule.class);
    public static final String ISOLATION_CONTEXT = "net.hasor.dataway.config.DatawayModule";

    @Override
    public void loadModule(WebApiBinder apiBinder) {
        //
        // .是否启用 Dataway
        Environment environment = apiBinder.getEnvironment();
        if (!Boolean.parseBoolean(environment.getVariable("HASOR_DATAQL_DATAWAY"))) {
            logger.info("dataway is disable.");
            return;
        }
        //
        // .Api接口
        String apiBaseUri = environment.getVariable("HASOR_DATAQL_DATAWAY_API_URL");
        if (StringUtils.isBlank(apiBaseUri)) {
            apiBaseUri = "/api/";
        }
        logger.info("dataway api workAt " + apiBaseUri);
        environment.addVariable("HASOR_DATAQL_DATAWAY_API_URL", apiBaseUri);
        apiBinder.filter(fixUrl(apiBaseUri + "/*")).through(new InterfaceApiFilter(apiBaseUri));
        //
        // .Dataway 后台管理界面
        if (!Boolean.parseBoolean(environment.getVariable("HASOR_DATAQL_DATAWAY_ADMIN"))) {
            logger.info("dataway admin is disable.");
            return;
        }
        String uiBaseUri = environment.getVariable("HASOR_DATAQL_DATAWAY_UI_URL");
        if (StringUtils.isBlank(uiBaseUri)) {
            uiBaseUri = "/interface-ui/";
        }
        logger.info("dataway admin workAt " + uiBaseUri);
        //
        // 使用 findClass 虽然可以降低代码复杂度，但是会因为引入代码扫描而增加初始化时间
        Class<?>[] controllerSet = new Class<?>[] {//
                ApiDetailController.class,      //
                ApiHistoryListController.class, //
                ApiInfoController.class,        //
                ApiListController.class,        //
                ApiHistoryGetController.class,  //
                //
                DisableController.class,        //
                SmokeController.class,          //
                SaveApiController.class,        //
                PublishController.class,        //
                PerformController.class,        //
                DeleteController.class,         //
        };
        for (Class<?> aClass : controllerSet) {
            MappingToUrl toUrl = aClass.getAnnotation(MappingToUrl.class);
            apiBinder.mappingTo(fixUrl(uiBaseUri + "/" + toUrl.value())).with(aClass);
        }
        apiBinder.filter(fixUrl(uiBaseUri + "/*")).through(new InterfaceUiFilter(apiBaseUri, uiBaseUri));
        //
        // .Finder,实现引用其它定义的 DataQL
        QueryApiBinder defaultContext = apiBinder.tryCast(QueryApiBinder.class);
        defaultContext.bindFinder(apiBinder.getProvider(DatawayFinder.class));
        // .Dataway 自身使用的隔离环境
        QueryApiBinder isolation = defaultContext.isolation(ISOLATION_CONTEXT);
        isolation.bindFragment("inner_dataway_sql", InnerSqlExecFragment.class);
    }

    @Override
    public void onStart(AppContext appContext) {
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        if (jdbcTemplate == null) {
            throw new IllegalStateException("jdbcTemplate is not init.");
        }
        //
        DataBaseType dataBaseType = appContext.getInstance(DataBaseType.class);
        if (dataBaseType == null) {
            throw new IllegalStateException("unknown DataBaseType.");
        }
        appContext.getInstance(DataQL.class).addShareVar(DataBaseType.class.getName(), () -> dataBaseType.name().toLowerCase());
        appContext.findBindingBean(ISOLATION_CONTEXT, DataQL.class).addShareVar(DataBaseType.class.getName(), () -> dataBaseType.name().toLowerCase());
    }

    static String fixUrl(String url) {
        return url.replaceAll("/+", "/");
    }
}