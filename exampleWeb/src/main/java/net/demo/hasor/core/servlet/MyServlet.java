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
package net.demo.hasor.core.servlet;
import net.hasor.core.AppContext;
import net.hasor.core.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 
 * @version : 2015年12月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class MyServlet extends HttpServlet {
    private static final long serialVersionUID = 1897490522575674516L;
    @Inject
    private AppContext        appContext;
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String myName = appContext.getEnvironment().getSettings().getString("myName");
        //
        resp.getWriter().print("this message form MyServlet -&lt; my name is " + myName);
        super.service(req, resp);
    }
}