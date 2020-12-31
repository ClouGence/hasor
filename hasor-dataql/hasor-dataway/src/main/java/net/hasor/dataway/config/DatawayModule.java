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

import net.hasor.core.Environment;
import net.hasor.core.HasorUtils;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.dataway.DatawayService;
import net.hasor.dataway.authorization.InterfaceAuthorizationFilter;
import net.hasor.dataway.dal.ApiDataAccessLayer;
import net.hasor.dataway.service.DatawayServiceImpl;
import net.hasor.dataway.service.InterfaceApiFilter;
import net.hasor.dataway.service.InterfaceUiFilter;
import net.hasor.dataway.web.*;
import net.hasor.utils.StringUtils;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dataway 启动入口
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
public class DatawayModule implements WebModule {
    protected static Logger logger = LoggerFactory.getLogger(DatawayModule.class);

    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        // .是否启用 Dataway
        Environment environment = apiBinder.getEnvironment();
        Settings settings = environment.getSettings();
        boolean datawayApi = settings.getBoolean("hasor.dataway.enable", false);
        boolean datawayAdmin = settings.getBoolean("hasor.dataway.enableAdmin", false);
        if (!datawayApi) {
            logger.info("dataway is disable. (enable: settings 'hasor.dataway.enable = true' or env 'HASOR_DATAQL_DATAWAY = true')");
            return;
        }
        //
        // .Api接口
        String apiBaseUri = settings.getString("hasor.dataway.baseApiUrl", "/api/");
        if (StringUtils.isBlank(apiBaseUri)) {
            apiBaseUri = "/api/";
        }
        if (!apiBaseUri.endsWith("/")) {
            apiBaseUri = apiBaseUri + "/";
        }
        String adminBaseUri = settings.getString("hasor.dataway.baseAdminUrl", "/interface-ui/");
        if (StringUtils.isBlank(adminBaseUri)) {
            adminBaseUri = "/interface-ui/";
        }
        if (!adminBaseUri.endsWith("/")) {
            adminBaseUri = adminBaseUri + "/";
        }
        //
        logger.info("dataway api workAt " + apiBaseUri);



        apiBinder.filter(fixUrl(apiBaseUri + "/*")).through(Integer.MAX_VALUE, new InterfaceApiFilter(apiBaseUri));
        //
        String dalType = settings.getString("hasor.dataway.dataAccessLayer.dalType", "db");
        if (StringUtils.isBlank(dalType)) {
            throw new IllegalArgumentException("dataway dalType is missing.");
        }
        boolean setupProvider = false;
        XmlNode[] nodeArray = settings.getXmlNodeArray("hasor.dataway.dataAccessLayer.provider");
        if (nodeArray != null) {
            for (XmlNode xmlNode : nodeArray) {
                if (!"provider".equalsIgnoreCase(xmlNode.getName())) {
                    continue;
                }
                String providerName = xmlNode.getAttribute("name");
                String providerType = xmlNode.getText();
                if (!dalType.equalsIgnoreCase(providerName) || StringUtils.isBlank(providerType)) {
                    continue;
                }
                setupProvider = true;
                Class<?> loadClass = environment.getClassLoader().loadClass(providerType);
                logger.info("use '" + providerName + "' as the dataAccessLayer, provider = " + loadClass.getName());
                apiBinder.bindType(ApiDataAccessLayer.class).toProvider(//
                        HasorUtils.autoAware(environment, new InnerApiDalCreator(loadClass))//
                );
                break;
            }
        }
        if (!setupProvider) {
            throw new RuntimeException("DataAccessLayer is not specified.");
        }
        //
        // .注册 DatawayService接口
        apiBinder.bindType(DatawayService.class).to(DatawayServiceImpl.class);
        //
        // .Dataway 后台管理界面
        if (!datawayAdmin) {
            logger.info("dataway admin is disable.");
            return;
        }
        String uiBaseUri = settings.getString("hasor.dataway.baseAdminUrl", "/interface-ui/");
        if (!uiBaseUri.endsWith("/")) {
            uiBaseUri = uiBaseUri + "/";
        }
        logger.info("dataway admin workAt " + uiBaseUri);
        //
        // 使用 findClass 虽然可以降低代码复杂度，但是会因为引入代码扫描而增加初始化时间
        Class<?>[] controllerSet = new Class<?>[]{ //
                ApiDetailController.class,          //
                ApiHistoryListController.class,     //
                ApiInfoController.class,            //
                ApiListController.class,            //
                ApiHistoryGetController.class,      //
                //
                DisableController.class,            //
                SmokeController.class,              //
                SaveApiController.class,            //
                PublishController.class,            //
                PerformController.class,            //
                DeleteController.class,             //
                //                AnalyzeSchemaController.class,      //
                //
                Swagger2Controller.class,           //
        };
        for (Class<?> aClass : controllerSet) {
            MappingToUrl toUrl = aClass.getAnnotation(MappingToUrl.class);
            apiBinder.mappingTo(fixUrl(uiBaseUri + "/" + toUrl.value())).with(aClass);
        }
        apiBinder.filter(fixUrl(uiBaseUri + "/*")).through(Integer.MAX_VALUE, new InterfaceAuthorizationFilter(uiBaseUri));
        apiBinder.filter(fixUrl(uiBaseUri + "/*")).through(Integer.MAX_VALUE, new InterfaceUiFilter(apiBaseUri, uiBaseUri));
    }

    private static String fixUrl(String url) {
        return url.replaceAll("/+", "/");
    }
}