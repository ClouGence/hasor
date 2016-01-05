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
package net.test.hasor.web;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.test.hasor.web._01_filter.VarFilter;
import net.test.hasor.web._02_servlet.MyServlet;
/**
 * 
 * @version : 2015年12月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class MyModule extends WebModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
        apiBinder.filter("/*").through(new VarFilter());
        apiBinder.serve("/myServlet.do").with(MyServlet.class);
        //
        //resource插件用法
        //        ServletContext sc = apiBinder.getServletContext();
        //        apiBinder.bindType(ResourceLoader.class).uniqueName().toInstance(new ZipResourceLoader(sc.getRealPath("/static/jquery-2.1.4.zip")));
        //        apiBinder.bindType(ResourceLoader.class).uniqueName().toInstance(new ZipResourceLoader(sc.getRealPath("/static/bootstrap-3.3.5.zip")));
        //
        //
    }
}
