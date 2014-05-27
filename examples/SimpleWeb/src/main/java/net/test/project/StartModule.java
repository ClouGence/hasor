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
package net.test.project;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.core.AppContext;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebAppContext;
import net.hasor.web.module.AbstractWebModule;
/**
 * 
 * @version : 2014-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class StartModule extends AbstractWebModule {
    public void init(WebApiBinder apiBinder) {
        //1.Servlet
        apiBinder.serve("test.c").with(MyServlet.class);
        //2.Bean
        apiBinder.defineBean("myName1").bindType(String.class).toInstance("赵永春");
        apiBinder.defineBean("myName2").bindType(String.class).toInstance("查理");
    }
    public void start(WebAppContext appContext) {
        System.out.println(appContext.getBean("myName1"));
        System.out.println(appContext.getBean("myName2"));
    }
}
class MyServlet extends HttpServlet {
    @Inject
    AppContext app;
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getRequestURI() + app);
        super.service(req, resp);
    }
}