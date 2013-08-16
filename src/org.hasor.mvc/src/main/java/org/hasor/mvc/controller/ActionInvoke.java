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
package org.hasor.mvc.controller;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.context.AppContext;
/**
 * 用于调用Action的接口
 * @version : 2013-8-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionInvoke {
    /**获取ActionDefine*/
    public ActionDefine getActionDefine();
    /**获取AppContext*/
    public AppContext getAppContext();
    /**获取调用的目标类*/
    public Object getTargetObject();
    /**执行调用*/
    public Object invoke() throws InvocationTargetException;
    /**获取请求对象*/
    public HttpServletRequest getRequest();
    /**获取响应对象*/
    public HttpServletResponse getResponse();
}