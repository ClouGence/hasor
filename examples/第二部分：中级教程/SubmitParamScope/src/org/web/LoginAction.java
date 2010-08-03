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
package org.web;
import org.more.submit.web.WebActionStack;
/**
 * 作为Action类必须是共有的非抽象的，同时也不能是接口。
 * Date : 2009-12-11
 */
public class LoginAction {
    /**当帐号和密码一致时登陆成功*/
    public void go(WebActionStack stack) {
        stack.setScope(WebActionStack.Scope_Stack);
        stack.setAttribute("var", "StackValue", WebActionStack.Scope_Stack);
        stack.setAttribute("var", "CookieValue", WebActionStack.Scope_Cookie);
        stack.setAttribute("var", "HttpSessionValue", WebActionStack.Scope_HttpSession);
        try {
            stack.setAttribute("var", "JspPageValue", WebActionStack.Scope_JspPage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        stack.setAttribute("var", "RequestValue", WebActionStack.Scope_HttpRequest);
        stack.setAttribute("var", "ServletContextValue", WebActionStack.Scope_ServletContext);
    }
    /**当帐号和密码一致时登陆成功
     * @throws Throwable */
    public void goshow(WebActionStack stack) throws Throwable {
        stack.getContext().doActionOnStack("safety.go", stack, null);
        stack.setResultsScript("jspforward", "/goshow.jsp", "server");
    }
}