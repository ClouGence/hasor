/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.security._;
/**
 * 
 * @version : 2013-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SecurityConfig {
    /**是否启用权限系统*/
    public static final String Security_Enable                             = "security.enable";
    /**当Security_Enable启用之后，该值决定是否启用针对URL部分的权限过滤。*/
    public static final String Security_EnableURL                          = "security.enableURL";
    /**当Security_Enable启用之后，该值决定是否启用针对方法调用中的权限过滤。*/
    public static final String Security_EnableMethod                       = "security.enableMethod";
    /**AuthSession数据缓存*/
    public static final String Security_AuthSessionCache                   = "security.authSessionCacheName";
    /**AuthSession超时时间*/
    public static final String Security_AuthSessionTimeout                 = "security.authSessionTimeout";
    /**登入地址*/
    public static final String Security_LoginURL                           = "security.loginURL";
    /**登出地址*/
    public static final String Security_LogoutURL                          = "security.logoutURL";
    /**登入表单，用户名*/
    public static final String Security_LoginFormData_AccountField         = "security.loginFormData.accountField";
    /**登入表单，密码*/
    public static final String Security_LoginFormData_PasswordField        = "security.loginFormData.passwordField";
    /**登入表单，使用的权限系统*/
    public static final String Security_LoginFormData_AuthField            = "security.loginFormData.authField";
    /**URL权限检查默认策略配置：Login|Logout|Guest|Permission|None*/
    public static final String Security_Rules_DefaultModel                 = "security.rules.defaultRule";
    /**包含在权限检查范畴的URL*/
    public static final String Security_Rules_Includes                     = "security.rules.includes";
    /**排除权限检查范畴的URL*/
    public static final String Security_Rules_Excludes                     = "security.rules.excludes";
    /**转发配置*/
    public static final String Security_Forwards                           = "security.forwards";
    /**是否启用来宾帐号，启用来宾帐号之后当用户尚未登陆时获取的AuthSession会获取来宾的AuthSession.*/
    public static final String Security_Guest_Enable                       = "security.guest.enable";
    /**来宾帐号的认证系统 .*/
    public static final String Security_Guest_AuthSystem                   = "security.guest.authSystem";
    /**来宾帐号.*/
    public static final String Security_Guest_Account                      = "security.guest.info.account";
    /**来宾帐号的密码.*/
    public static final String Security_Guest_Password                     = "security.guest.info.password";
    /**来宾帐号的Code.*/
    public static final String Security_Guest_UserCode                     = "security.guest.info.userCode";
    /**来宾帐号权限*/
    public static final String Security_Guest_Permissions                  = "security.guest.permissions";
    /**是否启用客户端cookie来协助认证。*/
    public static final String Security_ClientCookie_Enable                = "security.clientCookie.enable";
    /**当系统启动时是否强制所有客户端已经登陆过的Cookie信息失效*/
    public static final String Security_ClientCookie_LoseCookieOnStart     = "security.clientCookie.loseCookieOnStart";
    /**客户端cookie名称*/
    public static final String Security_ClientCookie_CookieName            = "security.clientCookie.cookieName";
    /**cookie超时时间，单位：秒*/
    public static final String Security_ClientCookie_Timeout               = "security.clientCookie.timeout";
    /**cookie的Domain配置，设置这个属性用来支持跨域访问cookie。（默认为空不对该值进行设置）*/
    public static final String Security_ClientCookie_Domain                = "security.clientCookie.cookieDomain";
    /**cookie的path属性（默认为空不对该值进行设置）*/
    public static final String Security_ClientCookie_Path                  = "security.clientCookie.cookiePath";
    /**是否加密cookie内容*/
    public static final String Security_ClientCookie_Encryption_Enable     = "security.clientCookie.encryption.enable";
    /**cookie内容加密方式，DES,BAS64等等.*/
    public static final String Security_ClientCookie_Encryption_EncodeType = "security.clientCookie.encryption.encodeType";
    /**cookie内容加密时使用的Key*/
    public static final String Security_ClientCookie_Encryption_Key        = "security.clientCookie.encryption.key";
    /**cookie加密范围（ALL,Security）*/
    public static final String Security_ClientCookie_Encryption_Scope      = "security.clientCookie.encryption.scope";
    /**加密算法配置*/
    public static final String Security_EncryptionDigestSet                = "security.encryptionDigestSet";
}