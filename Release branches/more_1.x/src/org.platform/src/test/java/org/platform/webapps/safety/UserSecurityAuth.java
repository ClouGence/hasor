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
package org.platform.webapps.safety;
import org.more.util.StringUtil;
import org.platform.context.AppContext;
import org.platform.security.ISecurityAuth;
import org.platform.security.SecurityAuth;
import org.platform.security.UserInfo;
/**
 * 
 * @version : 2013-4-28
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@SecurityAuth(authSystem = "UserAuth")
public class UserSecurityAuth implements ISecurityAuth {
    //
    @Override
    public void initAuth(AppContext appContext) {}
    @Override
    public UserInfo getUserInfo(String userCode) {
        UserUser userUser = new UserUser();
        userUser.setAccount(userCode.split("@")[0]);
        userUser.setPassword(userCode.split("@")[1]);
        return userUser;
    }
    @Override
    public UserInfo getUserInfo(String account, String password) {
        // 
        if (StringUtil.eqUnCaseSensitive(account, password)) {
            UserUser userUser = new UserUser();
            userUser.setAccount(account);
            userUser.setPassword(password);
            return userUser;
        }
        return null;
    }
    @Override
    public void destroyAuth(AppContext appContext) {}
}