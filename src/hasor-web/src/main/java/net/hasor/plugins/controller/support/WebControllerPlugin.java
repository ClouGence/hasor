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
package net.hasor.plugins.controller.support;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.core.plugin.Plugin;
import net.hasor.web.AbstractWebHasorPlugin;
import net.hasor.web.WebApiBinder;
/**
 * 
 * @version : 2013-9-26
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@Plugin
public class WebControllerPlugin extends AbstractWebHasorPlugin {
    public void loadPlugin(WebApiBinder apiBinder) {
        Settings settings = apiBinder.getEnvironment().getSettings();
        ControllerSettings acSettings = new ControllerSettings(settings);
        //
        if (acSettings.isEnable() == false) {
            Hasor.logInfo("WebController Module is disable.");
            return;
        }
        //
        Hasor.logInfo("WebController intercept %s.", acSettings.getIntercept());
        apiBinder.getGuiceBinder().bind(ControllerSettings.class).toInstance(acSettings);
        apiBinder.serve(acSettings.getIntercept()).with(ControllerServlet.class);
    }
}