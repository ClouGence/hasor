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
import net.hasor.mvc.support.ControllerModule;
import net.hasor.mvc.support.RootController;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/***
 * 创建WebMVC环境
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public class WebControllerModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //1.安装基本服务
        apiBinder.installModule(new ControllerModule());
        //2.安装服务
        WebRootController root = new WebRootController();
        apiBinder.bindType(RootController.class).toInstance(root);
        //3.安装Filter
        apiBinder.filter("/*").through(new ControllerFilter());
    }
}