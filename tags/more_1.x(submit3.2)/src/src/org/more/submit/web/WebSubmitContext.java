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
package org.more.submit.web;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.more.submit.SubmitContext;
/**
 * submit的核心接口，任何action的调用都是通过这个接口进行的。
 * @version 2009-12-1
 * @author 赵永春 (zyc@byshell.org)
 */
public interface WebSubmitContext extends SubmitContext {
    public static final String Default_Protocol = "action";
    public boolean isActionRequest(HttpServletRequest request);
    /**获取ServletContext对象。*/
    public ServletContext getServletContext();
    /**获取当使用request解析action时候所使用的协议前缀。*/
    public String getProtocol();
    /**设置当使用request解析action时候所使用的协议前缀，如果设置的值为空或者是一个空字符串则设置将会使用Default_Protocol默认值代替。*/
    public void setProtocol(String protocol);
    /**执行调用action的处理过程，并且返回执行结果。*/
    public Object doAction(HttpServletRequest request, HttpServletResponse response) throws Throwable;
    /**执行调用action的处理过程，并且返回执行结果。*/
    public Object doAction(String actionInvoke, HttpServletRequest request, HttpServletResponse response) throws Throwable;
    /**执行调用action的处理过程，并且返回执行结果。*/
    public Object doAction(String actionInvoke, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Throwable;
    /**执行调用action的处理过程，并且返回执行结果。*/
    public Object doAction(PageContext pageContext) throws Throwable;
    /**执行调用action的处理过程，并且返回执行结果。*/
    public Object doAction(PageContext pageContext, HttpServletRequest request, HttpServletResponse response) throws Throwable;
    /**执行调用action的处理过程，并且返回执行结果。*/
    public Object doAction(String actionInvoke, PageContext pageContext, HttpServletRequest request, HttpServletResponse response) throws Throwable;
    /**执行调用action的处理过程，并且返回执行结果。*/
    public Object doAction(String actionInvoke, PageContext pageContext, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Throwable;
};