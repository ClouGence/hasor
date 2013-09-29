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
package org.hasor.test.web.servlet;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.core.AppContext;
import net.hasor.gift.servlet3.WebServlet;
import org.hasor.test.simple.beans.customer.CustomerBean;
/**
 * 
 * @version : 2013-8-11
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
@WebServlet("/showname.c")
public class HelloWordServlet extends HttpServlet {
    @Inject
    private AppContext appContext = null;
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CustomerBean infoBean = this.appContext.getInstance(CustomerBean.class);
        infoBean.foo();
        //
        super.service(req, resp);
    }
}