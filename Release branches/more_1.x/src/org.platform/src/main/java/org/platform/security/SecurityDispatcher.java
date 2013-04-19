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
package org.platform.security;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 权限系统URL请求处理支持。
 * @version : 2013-4-9  
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SecurityDispatcher {
    /**跳转到登入成功后的页面。*/
    public void forwardIndex(HttpServletRequest httpRequest, HttpServletResponse httpResponse);
    /**跳转到登出之后的页面。*/
    public void forwardLogout(HttpServletRequest httpRequest, HttpServletResponse httpResponse);
    /**权限判断模式下的跳转，具体跳转方式由配置文件定义。*/
    public void forwardError(ServletRequest request, ServletResponse response);
    /**跳转到配置的页面。*/
    public void forward(String id, ServletRequest request, ServletResponse response);
}