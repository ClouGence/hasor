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
import org.hasor.context.AppContext;
import org.hasor.security.SecAuth;
import org.hasor.security.SecurityAuth;
import org.hasor.security.UserInfo;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-4-28
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@SecAuth(authSystem = "BBSAuth")
public class BBSSecurityAuth implements SecurityAuth {
    //
    @Override
    public void initAuth(AppContext appContext) {}
    @Override
    public UserInfo getUserInfo(String userCode) {
        BBSUser bbsUser = new BBSUser();
        bbsUser.setAccount(userCode.split("@")[0]);
        bbsUser.setPassword(userCode.split("@")[1]);
        return bbsUser;
    }
    @Override
    public UserInfo getUserInfo(String account, String password) {
        // 
        if (StringUtils.eqUnCaseSensitive(account, password)) {
            BBSUser bbsUser = new BBSUser();
            bbsUser.setAccount(account);
            bbsUser.setPassword(password);
            return bbsUser;
        }
        return null;
    }
    @Override
    public void destroyAuth(AppContext appContext) {}
}