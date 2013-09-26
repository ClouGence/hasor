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
package net.hasor.web.controller.plugins.result.support;
import net.hasor.core.AppContext;
import net.hasor.core.context.AnnoModule;
import net.hasor.web.controller.support.WebControllerModule;
import net.hasor.web.restful.ActionDefine;
import net.hasor.web.servlet.WebApiBinder;
import net.hasor.web.servlet.WebModule;
/**
 * 负责处理Action调用之后返回值的处理。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
@AnnoModule(description = "org.hasor.mvc.controller.plugins.result软件包功能支持。")
public class ControllerPluginResultSupportModule extends WebModule {
    public void init(WebApiBinder apiBinder) {
        apiBinder.dependency().forced(WebControllerModule.class);
        apiBinder.getGuiceBinder().bind(Caller.class);
    }
    public void start(AppContext appContext) {
        Caller caller = appContext.getInstance(Caller.class);
        appContext.getEnvironment().getEventManager().addEventListener(ActionDefine.Event_AfterInvoke, caller);
    }
    public void stop(AppContext appContext) {
        Caller caller = appContext.getInstance(Caller.class);
        appContext.getEnvironment().getEventManager().removeEventListener(ActionDefine.Event_AfterInvoke, caller);;
    }
}