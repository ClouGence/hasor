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
package org.platform.security.internal;
import org.platform.security.AuthSession;
import org.platform.security.Permission;
import org.platform.security.SecurityContext;
import org.platform.security.SecurityException;
import org.platform.security.UserInfo;
/**
 * 负责权限系统中的用户会话。用户会话中保存了用户登入之后的权限数据。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultAuthSession implements AuthSession {
    private String   sessionID = null;
    private UserInfo userObjet = null;
    @Override
    public String getSessionID() {
        // TODO Auto-generated method stub
        return null;
    }s
    @Override
    public UserInfo getUserObject() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public SecurityContext getSecurityContext() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void doLogin(UserInfo user) throws SecurityException {
        // TODO Auto-generated method stub
    }
    @Override
    public void doLoginCode(String userCode) throws SecurityException {
        // TODO Auto-generated method stub
    }
    @Override
    public void doLogin(String account, String password) throws SecurityException {
        // TODO Auto-generated method stub
    }
    @Override
    public void doLogout() throws SecurityException {
        // TODO Auto-generated method stub
    }
    @Override
    public void addPermission(Permission permission) {
        // TODO Auto-generated method stub
    }
    @Override
    public void removeTempPermission(Permission permission) {
        // TODO Auto-generated method stub
    }
    @Override
    public Permission[] getPermissions() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean hasPermission(Permission permission) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean hasPermission(String permissionCode) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isLogin() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isGuest() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void close() {
        // TODO Auto-generated method stub
    }
    @Override
    public long getCreatedTime() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public boolean supportCookieRecover() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void setSupportCookieRecover(boolean recover) {
        // TODO Auto-generated method stub
    }
}