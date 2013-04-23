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
import static org.platform.PlatformConfigEnum.Security_Enable;
import static org.platform.PlatformConfigEnum.Security_EnableMethod;
import static org.platform.PlatformConfigEnum.Security_EnableURL;
import static org.platform.PlatformConfigEnum.Security_LoginFormData_AccountField;
import static org.platform.PlatformConfigEnum.Security_LoginFormData_PasswordField;
import static org.platform.PlatformConfigEnum.Security_LoginURL;
import static org.platform.PlatformConfigEnum.Security_LogoutURL;
import org.platform.context.SettingListener;
import org.platform.context.setting.Settings;
/**
 * 
 * @version : 2013-4-23
 * @author 赵永春 (zyc@byshell.org)
 */
class SecuritySettings implements SettingListener {
    private boolean enable        = false; //启用禁用
    private boolean enableMethod  = true; //方法权限检查
    private boolean enableURL     = true; //URL权限检查
    private String  accountField  = null; //帐号字段
    private String  passwordField = null; //密码字段
    private String  loginURL      = null; //登入地址
    private String  logoutURL     = null; //登出地址
    //
    @Override
    public void reLoadConfig(Settings oldConfig, Settings newConfig) {
        this.enable = newConfig.getBoolean(Security_Enable, false);
        this.enableMethod = newConfig.getBoolean(Security_EnableMethod, false);
        this.enableURL = newConfig.getBoolean(Security_EnableURL, true);
        if (this.enable == false) {
            this.enableMethod = false;
            this.enableURL = false;
        }
        //
        this.accountField = newConfig.getString(Security_LoginFormData_AccountField);
        this.passwordField = newConfig.getString(Security_LoginFormData_PasswordField);
        this.loginURL = newConfig.getString(Security_LoginURL);
        this.logoutURL = newConfig.getString(Security_LogoutURL);
        //
    }
    public void loadConfig(Settings config) {
        this.reLoadConfig(null, config);
    }
    //
    public boolean isEnable() {
        return enable;
    }
    public boolean isEnableMethod() {
        return enableMethod;
    }
    public boolean isEnableURL() {
        return enableURL;
    }
    public String getAccountField() {
        return accountField;
    }
    public String getPasswordField() {
        return passwordField;
    }
    public String getLoginURL() {
        return loginURL;
    }
    public String getLogoutURL() {
        return logoutURL;
    }
};
