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
package org.hasor.web.encoding;
import org.hasor.annotation.HasorModule;
import org.hasor.context.AppContext;
import org.hasor.context.PlatformListener;
import org.hasor.context.binder.ApiBinder;
/**
 * 请求响应编码。启动级别：Lv0
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@HasorModule(displayName = "EncodingFilterListener", description = "org.platform.servlet.encoding软件包功能支持。", startIndex = HasorModule.Lv_0)
public class EncodingFilterListener implements PlatformListener {
    @Override
    public void initialize(WebApiBinder binder) {
        EncodingSettings setting = new EncodingSettings();
        setting.loadConfig(binder.getSettings());
        binder.getGuiceBinder().bind(EncodingSettings.class).toInstance(setting);
        //
        binder.filter("*").through(EncodingFilter.class);
    }
    @Override
    public void initialized(AppContext appContext) {
        EncodingSettings setting = appContext.getInstance(EncodingSettings.class);
        appContext.getSettings().addSettingsListener(setting);
    }
    @Override
    public void destroy(AppContext appContext) {
        EncodingSettings setting = appContext.getInstance(EncodingSettings.class);
        appContext.getSettings().removeSettingsListener(setting);
    }
}