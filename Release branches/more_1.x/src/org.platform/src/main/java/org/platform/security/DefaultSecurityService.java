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
import javax.servlet.http.HttpSession;
/**
 * 
 * @version : 2013-4-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class DefaultSecurityService implements SecurityContext {
    @Override
    public AuthSession getAuthSession(HttpServletRequest request, HttpServletResponse response, boolean created) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public AuthSession getAuthSession(String authSessionID, boolean created) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public AuthSession getAuthSession(HttpSession session, boolean created) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public AuthSession getCurrentAuthSession() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public UriPatternMatcher getUriMatcher(String requestPath) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public SecurityDispatcher getDispatcher(String requestPath) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public SecurityNode getSecurityCondition(Permission permission) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public SecurityNode getSecurityCondition(String permissionCode) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public SecurityQuery newSecurityQuery() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public SecurityQuery newSecurityQuery(Permission permission) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public SecurityQuery newSecurityQuery(String permissionCode) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public SecurityQuery newSecurityQuery(SecurityNode testNode) {
        // TODO Auto-generated method stub
        return null;
    }
}s
