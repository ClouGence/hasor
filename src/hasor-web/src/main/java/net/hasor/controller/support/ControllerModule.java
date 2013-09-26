/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.controller.support;
import net.hasor.Hasor;
import net.hasor.core.AppContext;
import net.hasor.core.Settings;
import net.hasor.core.context.AnnoModule;
import net.hasor.servlet.WebApiBinder;
import net.hasor.servlet.WebModule;
/**
 * 
 * @version : 2013-9-26
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@AnnoModule
public class ControllerModule extends WebModule {
    public void init(WebApiBinder apiBinder) {
        Settings settings = apiBinder.getEnvironment().getSettings();
        ControllerSettings acSettings = new ControllerSettings(settings);
        Hasor.info("ActionController intercept %s.", acSettings.getIntercept());
        //
        apiBinder.getGuiceBinder().bind(ControllerSettings.class).toInstance(acSettings);
        apiBinder.serve(acSettings.getIntercept()).with(ControllerServlet.class);
    }
    public void start(AppContext appContext) {
        //
    }
    public void stop(AppContext appContext) {
        //
    }
}