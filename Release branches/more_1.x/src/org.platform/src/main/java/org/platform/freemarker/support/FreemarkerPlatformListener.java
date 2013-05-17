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
import org.platform.Platform;
import org.platform.binder.ApiBinder;
import org.platform.context.AppContext;
import org.platform.context.PlatformListener;
import org.platform.context.startup.PlatformExt;
import org.platform.freemarker.FreemarkerManager;
/**
 * Freemarker服务，延迟一个级别是因为需要依赖icache
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@PlatformExt(displayName = "FreemarkerPlatformListener", description = "org.platform.freemarker软件包功能支持。", startIndex = Integer.MIN_VALUE + 1)
public class FreemarkerPlatformListener implements PlatformListener {
    private FreemarkerSettings freemarkerSettings = null;
    /**初始化.*/
    @Override
    public void initialize(ApiBinder event) {
        this.freemarkerSettings = new FreemarkerSettings();
        //
        event.getGuiceBinder().bind(FreemarkerSettings.class).toInstance(freemarkerSettings);
        event.getGuiceBinder().bind(FreemarkerManager.class).to(DefaultFreemarkerManager.class);
    }s
    //
    /*装载Listener*/
    protected void loadListener(AppContext appContext) {
        //TODO sss
    }
    @Override
    public void initialized(AppContext appContext) {
        appContext.getSettings().addSettingsListener(this.freemarkerSettings);
        Platform.info("online ->> freemarker is %s", (this.freemarkerSettings.isEnable() ? "enable." : "disable."));
    }
    @Override
    public void destroy(AppContext appContext) {
        appContext.getSettings().removeSettingsListener(this.freemarkerSettings);
        this.freemarkerSettings = null;
        Platform.info("freemarker is destroy.");
    }
}