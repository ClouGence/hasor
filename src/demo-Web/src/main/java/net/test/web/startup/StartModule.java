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
package net.test.web.startup;
import net.hasor.web.WebApiBinder;
import net.hasor.web.plugin.WebModule;
import net.test.web.filters.MyFilter;
import net.test.web.module.OSSModule;
/**
 * 
 * @version : 2014年7月24日
 * @author 赵永春(zyc@hasor.net)
 */
public class StartModule extends WebModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //1.注册Filter
        apiBinder.filter("/*").through(MyFilter.class);
        //2.注册Servlet
        //        apiBinder.serve("/my.do").with(MyServlet.class);
        apiBinder.installModule(new OSSModule());
    }
}