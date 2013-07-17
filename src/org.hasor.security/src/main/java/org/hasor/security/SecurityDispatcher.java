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
package org.hasor.security;
import java.io.IOException;
import javax.servlet.ServletException;
/**
 * 权限系统URL请求处理支持。
 * @version : 2013-4-9  
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SecurityDispatcher {
    /**匹配的路径模式*/
    public String getContentPath();
    /**跳转到登入成功后的地址。*/
    public SecurityForward forwardIndex() throws IOException, ServletException;
    /**跳转到登出之后的地址。*/
    public SecurityForward forwardLogout() throws IOException, ServletException;
    /**跳转到登入登出执行s登入失败时的地址。*/
    public SecurityForward forwardFailure(Throwable e) throws IOException, ServletException;
    /**跳转到配置的页面。*/
    public SecurityForward forward(String id) throws IOException, ServletException, SecurityException;
}