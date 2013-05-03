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
package org.platform.security.guest;
import org.platform.security.BaseUserInfo;
/**
 * 表示来宾用户，来宾用户表示未登录状态下的用户。
 * @version : 2013-4-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class GuestUser extends BaseUserInfo {
    private String account;
    private String password;
    //
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String getUserCode() {
        return this.getAccount() + "@" + this.getPassword();
    }
    @Override
    public boolean isGuest() {
        return true;
    }
}