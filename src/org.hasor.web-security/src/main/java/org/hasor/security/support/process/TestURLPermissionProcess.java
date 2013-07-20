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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.security.AuthSession;
import org.hasor.security.SecurityContext;
import org.hasor.security.UriPatternMatcher;
/**
 * {@link TestURLPermissionProcess}接口默认实现。
 * @version : 2013-5-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class TestURLPermissionProcess extends AbstractProcess {
    /**测试要处理的资源是否具有权限访问，如果权限检测失败会抛出PermissionException异常。*/
    public boolean testURL(SecurityContext secContext, AuthSession[] authSessions, HttpServletRequest request, HttpServletResponse response) {
        String reqPath = request.getRequestURI().substring(request.getContextPath().length());
        UriPatternMatcher uriMatcher = secContext.getUriMatcher(reqPath);
        return uriMatcher.testPermission(authSessions);
    }
}