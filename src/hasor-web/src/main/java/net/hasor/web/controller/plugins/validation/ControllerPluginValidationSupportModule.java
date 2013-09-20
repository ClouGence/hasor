/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.web.controller.plugins.validation;
import net.hasor.core.AppContext;
import net.hasor.core.context.AnnoModule;
import net.hasor.web.controller.support.ServletControllerSupportModule;
import net.hasor.web.servlet.AbstractWebModule;
import net.hasor.web.servlet.WebApiBinder;
/**
 * 负责处理请求验证。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
@AnnoModule(description = "org.hasor.mvc.controller.plugins.validation软件包功能支持。")
public class ControllerPluginValidationSupportModule extends AbstractWebModule {
    public void init(WebApiBinder apiBinder) {
        apiBinder.dependency().forced(ServletControllerSupportModule.class);
    }
    public void start(AppContext appContext) {}
    public void stop(AppContext appContext) {}
}