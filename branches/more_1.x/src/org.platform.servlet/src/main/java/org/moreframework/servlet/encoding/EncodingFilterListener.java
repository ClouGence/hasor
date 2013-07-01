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
package org.moreframework.servlet.encoding;
import org.moreframework.binder.ApiBinder;
import org.moreframework.context.AppContext;
import org.moreframework.context.PlatformListener;
import org.moreframework.context.startup.PlatformExt;
/**
 * 请求响应编码。启动级别：Lv0
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@PlatformExt(displayName = "EncodingFilterListener", description = "org.platform.servlet.encoding软件包功能支持。", startIndex = PlatformExt.Lv_0)
public class EncodingFilterListener implements PlatformListener {
    @Override
    public void initialize(ApiBinder binder) {
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