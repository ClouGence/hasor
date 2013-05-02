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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.inject.ImplementedBy;
/**
 * 
 * @version : 2013-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
@ImplementedBy(InternalSecurityProcess.class)
public interface SecurityProcess {
    /**写入会话数据。*/
    public void writeAuthSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws SecurityException;
    /**处理登入请求。*/
    public void processLogin(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws SecurityException;
    /**处理登出请求*/
    public void processLogout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws SecurityException;
    /**测试要处理的资源是否具有权限访问，如果权限检测失败会抛出PermissionException异常。*/
    public void processTestFilter(String reqPath) throws PermissionException;
    /**恢复权限*/
    public void recoverAuthSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws SecurityException;
}