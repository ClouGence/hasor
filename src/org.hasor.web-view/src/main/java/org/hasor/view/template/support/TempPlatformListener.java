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
package org.hasor.view.template.support;
import org.hasor.annotation.Module;
import org.hasor.servlet.WebApiBinder;
import org.hasor.servlet.WebHasorModule;
import org.hasor.view.template.TemplateService;
import org.hasor.view.template.ext.freemarker.FreemarkerTemplateService;
/**
 * 模板服务，启动级别Lv_1
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@Module(displayName = "TemplatePlatformListener", description = "org.hasor.view.template软件包功能支持。", startIndex = Module.Lv_1)
public class TempPlatformListener extends WebHasorModule {
    /**初始化.*/
    @Override
    public void init(WebApiBinder apiBinder) {
        TempSettings tempSettings = new TempSettings();
        tempSettings.onLoadConfig(apiBinder.getInitContext().getSettings());
        apiBinder.getGuiceBinder().bind(TempSettings.class).toInstance(tempSettings);
        //
        apiBinder.getGuiceBinder().bind(TemplateService.class).to(FreemarkerTemplateService.class);
        //
        if (tempSettings.isEnable() == true) {
            String[] suffix = tempSettings.getSuffix();
            if (suffix != null)
                for (String suf : suffix)
                    apiBinder.serve(suf).with(TempHttpServlet.class);
        }
    }
}