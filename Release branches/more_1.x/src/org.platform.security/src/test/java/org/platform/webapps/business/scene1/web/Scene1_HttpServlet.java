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
package org.platform.webapps.business.scene1.web;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.platform.general.WebServlet;
import org.platform.webapps.business.scene1.service.Power_Services;
import com.google.inject.Inject;
/**
 * 
 * @version : 2013-5-2
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@WebServlet("/business/scene1.do")
public class Scene1_HttpServlet extends HttpServlet {
    private static final long serialVersionUID = 9157509300789665741L;
    @Inject
    private Power_Services    scene1Service    = null;
    //
    //
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter printWriter = resp.getWriter();
        //
        try {
            printWriter.write("callFree:" + this.scene1Service.callFree(UUID.randomUUID().toString(), "callFree"));
        } catch (Exception e) {
            e.printStackTrace(printWriter);
        }
        printWriter.write("\n/*-------------------------------------------------------------------*/\n");
        try {
            printWriter.write("callLogin:" + this.scene1Service.callLogin(UUID.randomUUID().toString(), "callLogin"));
        } catch (Exception e) {
            e.printStackTrace(printWriter);
        }
        printWriter.write("\n/*-------------------------------------------------------------------*/\n");
        try {
            printWriter.write("callAccess1:" + this.scene1Service.callAccess1(UUID.randomUUID().toString(), "callAccess1"));
        } catch (Exception e) {
            e.printStackTrace(printWriter);
        }
        printWriter.write("\n/*-------------------------------------------------------------------*/\n");
        try {
            printWriter.write("callAccess2:" + this.scene1Service.callAccess2(UUID.randomUUID().toString(), "callAccess2"));
        } catch (Exception e) {
            e.printStackTrace(printWriter);
        }
        printWriter.write("\n/*-------------------------------------------------------------------*/\n");
        printWriter.flush();
    }
}