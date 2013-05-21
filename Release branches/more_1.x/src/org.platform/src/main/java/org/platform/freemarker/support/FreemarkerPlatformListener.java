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
package org.platform.freemarker.support;
import static org.platform.PlatformConfig.FreemarkerConfig_ConfigurationFactory;
import org.platform.Platform;
import org.platform.binder.ApiBinder;
import org.platform.context.AppContext;
import org.platform.context.PlatformListener;
import org.platform.context.startup.PlatformExt;
import org.platform.freemarker.ConfigurationFactory;
import org.platform.freemarker.FreemarkerManager;
/**
 * Freemarker服务，延迟一个级别是因为需要依赖icache
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@PlatformExt(displayName = "FreemarkerPlatformListener", description = "org.platform.freemarker软件包功能支持。", startIndex = Integer.MIN_VALUE + 1)
public class FreemarkerPlatformListener implements PlatformListener {
    private FreemarkerSettings freemarkerSettings = null;
    private FreemarkerManager  freemarkerManager  = null;
    /**初始化.*/
    @Override
    public void initialize(ApiBinder event) {
        this.freemarkerSettings = new FreemarkerSettings();
        this.freemarkerSettings.loadConfig(event.getSettings());
        //
        event.getGuiceBinder().bind(FreemarkerSettings.class).toInstance(freemarkerSettings);
        event.getGuiceBinder().bind(FreemarkerManager.class).to(DefaultFreemarkerManager.class);
        String configurationFactory = event.getSettings().getString(FreemarkerConfig_ConfigurationFactory, DefaultConfigurationFactory.class.getName());
        try {
            event.getGuiceBinder().bind(ConfigurationFactory.class).to((Class<? extends ConfigurationFactory>) Class.forName(configurationFactory)).asEagerSingleton();
        } catch (Exception e) {
            Platform.error("bind configurationFactory error %s", e);
            event.getGuiceBinder().bind(ConfigurationFactory.class).to(DefaultConfigurationFactory.class);
        }
        //
    }
    //
    /*装载Listener*/
    protected void loadListener(AppContext appContext) {
        //TODO sss
    }
    @Override
    public void initialized(AppContext appContext) {
        appContext.getSettings().addSettingsListener(this.freemarkerSettings);
        this.freemarkerManager = appContext.getInstance(FreemarkerManager.class);
        this.freemarkerManager.initManager(appContext);
        Platform.info("online ->> freemarker is %s", (this.freemarkerSettings.isEnable() ? "enable." : "disable."));
    }
    @Override
    public void destroy(AppContext appContext) {
        appContext.getSettings().removeSettingsListener(this.freemarkerSettings);
        this.freemarkerSettings = null;
        this.freemarkerManager.destroyManager(appContext);
        Platform.info("freemarker is destroy.");
    }
}