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
package org.hasor.view.template;
import org.hasor.Hasor;
import org.hasor.annotation.Module;
import org.hasor.context.AppContext;
import org.hasor.freemarker.ConfigurationFactory;
import org.hasor.freemarker.FreemarkerManager;
import org.hasor.freemarker.support.DefaultFreemarkerFactory;
import org.hasor.servlet.WebApiBinder;
import org.hasor.servlet.WebHasorModule;
/**
 * Freemarker服务，延迟一个级别是因为需要依赖icache，启动级别L1
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@Module(displayName = "TemplatePlatformListener", description = "org.hasor.view.template软件包功能支持。", startIndex = Module.Lv_1)
public class TemplatePlatformListener extends WebHasorModule {
    private FreemarkerSettings freemarkerSettings = null;
    private FreemarkerManager  freemarkerManager  = null;
    /**初始化.*/
    @Override
    public void init(WebApiBinder apiBinder) {
        this.freemarkerSettings = new FreemarkerSettings();
        this.freemarkerSettings.loadConfig(apiBinder.getSettings());
        //
        apiBinder.getGuiceBinder().bind(FreemarkerSettings.class).toInstance(freemarkerSettings);
        apiBinder.getGuiceBinder().bind(FreemarkerManager.class).to(InternalFreemarkerManager.class);
        String configurationFactory = apiBinder.getSettings().getString(FreemarkerConfig_ConfigurationFactory, DefaultFreemarkerFactory.class.getName());
        try {
            apiBinder.getGuiceBinder().bind(ConfigurationFactory.class).to((Class<? extends ConfigurationFactory>) Class.forName(configurationFactory)).asEagerSingleton();
        } catch (Exception e) {
            Hasor.error("bind configurationFactory error %s", e);
            apiBinder.getGuiceBinder().bind(ConfigurationFactory.class).to(DefaultFreemarkerFactory.class).asEagerSingleton();
        }
        //
        if (this.freemarkerSettings.isEnable() == true) {
            String[] suffix = this.freemarkerSettings.getSuffix();
            if (suffix != null)
                for (String suf : suffix)
                    event.serve(suf).with(TemplateHttpServlet.class);
        }
    }
    //
    /***/
    @Override
    public void initialized(AppContext appContext) {
        appContext.getSettings().addSettingsListener(this.freemarkerSettings);
        this.freemarkerManager = appContext.getInstance(FreemarkerManager.class);
        this.freemarkerManager.initManager(appContext);
        HasorFramework.info("online ->> freemarker is %s", (this.freemarkerSettings.isEnable() ? "enable." : "disable."));
    }
    @Override
    public void destroy(AppContext appContext) {
        appContext.getSettings().removeSettingsListener(this.freemarkerSettings);
        this.freemarkerSettings = null;
        this.freemarkerManager.destroyManager(appContext);
        HasorFramework.info("freemarker is destroy.");
    }
}