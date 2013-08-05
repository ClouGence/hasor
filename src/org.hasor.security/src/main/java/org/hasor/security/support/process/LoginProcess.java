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
package org.hasor.security.support.process;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.Hasor;
import org.hasor.security.AuthSession;
import org.hasor.security.SecurityContext;
import org.hasor.security.SecurityDispatcher;
import org.hasor.security.SecurityException;
import org.hasor.security.SecurityForward;
/**
 * {@link LoginProcess}接口默认实现。
 * @version : 2013-5-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class LoginProcess extends AbstractProcess {
    /**处理登入请求。*/
    public SecurityForward processLogin(SecurityContext secContext, HttpServletRequest request, HttpServletResponse response) throws SecurityException, ServletException, IOException {
        String reqPath = request.getRequestURI().substring(request.getContextPath().length());
        SecurityDispatcher dispatcher = secContext.getDispatcher(reqPath);
        //1.获得登入相关信息
        String account = request.getParameter(this.settings.getAccountField());
        String password = request.getParameter(this.settings.getPasswordField());
        String formAuth = request.getParameter(this.settings.getAuthField());
        //3.执行登入
        AuthSession authSession = secContext.getCurrentBlankAuthSession();
        if (authSession == null)
            authSession = secContext.createAuthSession();
        try {
            authSession.doLogin(formAuth, account, password);/*登入新会话*/
            Hasor.info("login OK. acc=%s , at SessionID= %s", account, authSession.getSessionID());
            return dispatcher.forwardIndex();
        } catch (SecurityException e) {
            Hasor.warning("login failure! acc=%s , msg= %s", account, e.getMessage());
            authSession.close();
            return dispatcher.forwardFailure(e);
        }
    }
}