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
package org.more.submit.web.result;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.submit.ActionStack;
import org.more.submit.ResultProcess;
import org.more.submit.web.WebActionStack;
import org.more.submit.web.result.WebForwardResult.ForwardEnum;
/**
 * 处理转发
 * @version : 2011-7-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class WebForwardResultProcess implements ResultProcess<WebForwardResult> {
    public Object invoke(ActionStack onStack, WebForwardResult res) throws ServletException, IOException {
        WebActionStack webStack = (WebActionStack) onStack;
        HttpServletRequest request = webStack.getHttpRequest();
        HttpServletResponse response = webStack.getHttpResponse();
        //1.处理转发
        boolean isForword = false;
        String toURL = null;
        if (res.getEnumType() == ForwardEnum.Forward) {
            isForword = true;
            toURL = res.getToURL();
        } else if (res.getEnumType() == ForwardEnum.Redirect) {
            isForword = false;
            toURL = res.getToURL();
        } else if (res.getEnumType() == ForwardEnum.F_Home) {
            isForword = true;
            toURL = webStack.getServletContext().getContextPath();
        } else if (res.getEnumType() == ForwardEnum.R_Home) {
            isForword = false;
            toURL = webStack.getServletContext().getContextPath();
        } else if (res.getEnumType() == ForwardEnum.Refresh) {
            isForword = false;
            toURL = webStack.getHttpRequest().getRequestURI();
        }
        //2.执行转发
        if (isForword == true) {
            RequestDispatcher rd = request.getRequestDispatcher(toURL);
            rd.forward(request, response);
        } else {
            if (response.isCommitted() == false)
                response.sendRedirect(toURL);
        }
        return res.getReturnValue();
    }
    public void addParam(String key, String value) {}
}