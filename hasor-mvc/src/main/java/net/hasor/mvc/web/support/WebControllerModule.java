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
package net.hasor.mvc.web.support;
import java.lang.reflect.Method;
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.mvc.strategy.CallStrategyFactory;
import net.hasor.mvc.support.ControllerModule;
import net.hasor.mvc.support.MappingDefine;
import net.hasor.web.WebApiBinder;
/***
 * 创建WebMVC环境
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public class WebControllerModule extends ControllerModule implements Module {
    public final void loadModule(final ApiBinder apiBinder) throws Throwable {
        if (apiBinder instanceof WebApiBinder == false) {
            Hasor.logWarn("does not support ‘%s’ Web plug-in.", this.getClass());
            return;
        }
        this.loadModule((WebApiBinder) apiBinder);
        Hasor.logInfo("‘%s’ Plug-in loaded successfully", this.getClass());
    }
    protected MappingDefine createMappingDefine(String newID, Method atMethod, CallStrategyFactory strategyFactory) {
        return new WebMappingDefine(newID, atMethod, strategyFactory);
    }
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //1.安装基本服务
        super.loadModule(apiBinder);
        //4.安装Filter
        apiBinder.filter("/*").through(new ControllerFilter());
    }
}