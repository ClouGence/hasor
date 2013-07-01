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
package org.platform.webapps.business.scene2.web;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.moreframework.context.AppContext;
import org.moreframework.general.WebServlet;
import org.moreframework.security.SecurityContext;
import org.moreframework.security.SecurityDispatcher;
import org.platform.webapps.business.scene2.service.Scene2_Bean;
import com.google.inject.Inject;
/**
 * 
 * @version : 2013-5-2
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@WebServlet("*/scene2.do")
public class Scene2_HttpServlet extends HttpServlet {
    private static final long serialVersionUID = -8203858833170622693L;
    @Inject
    private SecurityContext   securityContext  = null;
    @Inject
    private AppContext        appContext       = null;
    //
    //
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI().substring(req.getContextPath().length());
        SecurityDispatcher dispatcher = this.securityContext.getDispatcher(requestURI);
        //
        //
        Object o1 = appContext.getSettings().getXmlProperty("dataSourceSet");
        Object o2 = appContext.getSettings().getXmlProperty("framework");
        Object o3 = appContext.getSettings().getXmlProperty("httpServlet");
        Object o4 = appContext.getSettings().getXmlProperty("freemarker");
        Object o5 = appContext.getSettings().getXmlProperty("security");
        Object o6 = appContext.getSettings().getXmlProperty("cacheConfig");
        Object o7 = appContext.getSettings().getXmlProperty("workspace");
        //
        Scene2_Bean serBean = appContext.getBean("Scene2_ServiceBean");
        serBean.print();
        for (int i = 0; i < 10; i++) {
            //System.out.println(currentViewContext.genPath(System.currentTimeMillis(), 512));
            System.out.println(appContext.getWorkSpace().createTempFile());
        }
        //
        //
        String goID = req.getParameter("goID");
        dispatcher.forward(goID).forward(req, resp);
    }
}