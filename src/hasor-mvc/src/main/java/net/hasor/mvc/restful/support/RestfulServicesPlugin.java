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
package net.hasor.mvc.restful.support;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.web.WebApiBinder;
import net.hasor.web.plugin.AbstractWebHasorPlugin;
/**
 * Restful服务启动类.
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
public class RestfulServicesPlugin extends AbstractWebHasorPlugin {
    public void loadPlugin(WebApiBinder apiBinder) {
        Settings settings = apiBinder.getEnvironment().getSettings();
        boolean enable = settings.getBoolean("hasor-web.restfulServices.enable");
        if (enable == false) {
            Hasor.logInfo("RestfulServices Module is disable.");
            return;
        }
        String onPath = settings.getString("hasor-web.restfulServices.onPath");
        int sortBy = settings.getInteger("hasor-web.restfulServices.sortBy", 0);
        Hasor.logInfo("%s Bind to RestfulServices, filter sort is %s.", onPath, sortBy);
        apiBinder.filter(onPath).through(sortBy, RestfulController.class);
    }
}