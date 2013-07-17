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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 
 * @version : 2013-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AutoLoginProcess {
    /**写入会话数据。*/
    public void writeCookie(SecurityContext secContext, AuthSession[] authSessions, HttpServletRequest request, HttpServletResponse response) throws SecurityException;
    /**恢复权限*/
    public AuthSession[] recoverCookie(SecurityContext secContext, HttpServletRequest request, HttpServletResponse response) throws SecurityException;
}