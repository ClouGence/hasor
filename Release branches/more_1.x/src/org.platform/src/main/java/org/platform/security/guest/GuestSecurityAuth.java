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
import org.more.util.StringUtil;
import org.platform.context.AppContext;
import org.platform.security.ISecurityAuth;
import org.platform.security.SecurityAuth;
import org.platform.security.SecuritySettings;
import org.platform.security.UserInfo;
/**
 * 
 * @version : 2013-4-28
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@SecurityAuth(authSystem = "GuestAuthSystem")
public class GuestSecurityAuth implements ISecurityAuth {
    private SecuritySettings settings = null;
    // 
    @Override
    public void initAuth(AppContext appContext) {
        this.settings = appContext.getInstance(SecuritySettings.class);
    }
    @Override
    public UserInfo getUserInfo(String userCode) {
        boolean res = StringUtil.eqUnCaseSensitive(this.settings.getGuestUserCode(), userCode);
        if (res == false)
            return null;
        GuestUser guestUser = new GuestUser();
        guestUser.setAccount(this.settings.getGuestAccount());
        guestUser.setPassword(this.settings.getGuestPassword());
        return guestUser;
    }
    @Override
    public UserInfo getUserInfo(String account, String password) {
        String configGuestAcc = this.settings.getGuestAccount();
        String configGuestPwd = this.settings.getGuestPassword();
        //
        boolean res1 = StringUtil.eqUnCaseSensitive(configGuestAcc, account);
        boolean res2 = StringUtil.eqUnCaseSensitive(configGuestPwd, password);
        if (res1 == true && res2 == true) {
            GuestUser guestUser = new GuestUser();
            guestUser.setAccount(configGuestAcc);
            guestUser.setPassword(configGuestPwd);
            return guestUser;
        }
        return null;
    }
    @Override
    public void destroyAuth(AppContext appContext) {}
}