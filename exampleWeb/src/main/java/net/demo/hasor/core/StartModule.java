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
package net.demo.hasor.core;
import net.demo.hasor.manager.EnvironmentConfig;
import net.demo.hasor.manager.VersionInfoManager;
import net.hasor.restful.RenderEngine;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebAppContext;
import net.hasor.web.WebModule;
import org.more.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @version : 2015年12月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class StartModule extends WebModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
        apiBinder.filter("/*").through(0, new JumpFilter(apiBinder.getEnvironment()));
        //
        apiBinder.installModule(new DataSourceModule());
        apiBinder.bindType(RenderEngine.class, new FreemarkerTemplateEngine() {
            @Override
            public void initEngine(WebAppContext appContext) throws Throwable {
                super.initEngine(appContext);
                try {
                    this.configuration.setSharedVariable("ctx_path", appContext.getServletContext().getContextPath());
                    this.configuration.setSharedVariable("env", appContext.getInstance(EnvironmentConfig.class));
                    this.configuration.setSharedVariable("versionMap", appContext.getInstance(VersionInfoManager.class));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw ExceptionUtils.toRuntimeException(e);
                }
            }
        });
    }
}
