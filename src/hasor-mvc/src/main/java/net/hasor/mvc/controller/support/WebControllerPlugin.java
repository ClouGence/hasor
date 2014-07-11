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
package net.hasor.mvc.controller.support;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.web.WebApiBinder;
import net.hasor.web.plugin.WebModule;
/**
 * 
 * @version : 2013-9-26
 * @author 赵永春(zyc@hasor.net)
 */
public class WebControllerPlugin extends WebModule {
    /**action拦截器.*/
    public static final String ActionServlet_Intercept    = "hasor-mvc.controller.intercept";
    /**方法忽略的方法（逗号分割多组方法名），注意：在这里配置的忽略会应用到所有action上.*/
    public static final String ActionServlet_GlobalIgnore = "hasor-mvc.controller.globalIgnore";
    //
    public void loadModule(WebApiBinder apiBinder) {
        Settings settings = apiBinder.getSettings();
        //
        String intercept = settings.getString(ActionServlet_Intercept, "*.do");
        String[] interceptArray = intercept.split(",");
        for (int i = 0; i < interceptArray.length; i++)
            interceptArray[i] = interceptArray[i].trim();
        //
        Hasor.logInfo("WebController intercept %s.", intercept);
        apiBinder.serve(interceptArray).with(ControllerServlet.class);
    }
}