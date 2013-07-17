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
package org.hasor.security.support;
import static org.hasor.security.SecurityConfig.Security_AuthSessionCache;
import static org.hasor.security.SecurityConfig.Security_AuthSessionTimeout;
import static org.hasor.security.SecurityConfig.Security_ClientCookie_CookieName;
import static org.hasor.security.SecurityConfig.Security_ClientCookie_Domain;
import static org.hasor.security.SecurityConfig.Security_ClientCookie_Enable;
import static org.hasor.security.SecurityConfig.Security_ClientCookie_Encryption_Enable;
import static org.hasor.security.SecurityConfig.Security_ClientCookie_Encryption_EncodeType;
import static org.hasor.security.SecurityConfig.Security_ClientCookie_Encryption_Key;
import static org.hasor.security.SecurityConfig.Security_ClientCookie_Encryption_Scope;
import static org.hasor.security.SecurityConfig.Security_ClientCookie_LoseCookieOnStart;
import static org.hasor.security.SecurityConfig.Security_ClientCookie_Path;
import static org.hasor.security.SecurityConfig.Security_ClientCookie_Timeout;
import static org.hasor.security.SecurityConfig.Security_Enable;
import static org.hasor.security.SecurityConfig.Security_EnableMethod;
import static org.hasor.security.SecurityConfig.Security_EnableURL;
import static org.hasor.security.SecurityConfig.Security_EncryptionDigestSet;
import static org.hasor.security.SecurityConfig.Security_Forwards;
import static org.hasor.security.SecurityConfig.Security_Guest_Account;
import static org.hasor.security.SecurityConfig.Security_Guest_AuthSystem;
import static org.hasor.security.SecurityConfig.Security_Guest_Enable;
import static org.hasor.security.SecurityConfig.Security_Guest_Password;
import static org.hasor.security.SecurityConfig.Security_Guest_Permissions;
import static org.hasor.security.SecurityConfig.Security_Guest_UserCode;
import static org.hasor.security.SecurityConfig.Security_LoginFormData_AccountField;
import static org.hasor.security.SecurityConfig.Security_LoginFormData_AuthField;
import static org.hasor.security.SecurityConfig.Security_LoginFormData_PasswordField;
import static org.hasor.security.SecurityConfig.Security_LoginURL;
import static org.hasor.security.SecurityConfig.Security_LogoutURL;
import static org.hasor.security.SecurityConfig.Security_Rules_DefaultModel;
import static org.hasor.security.SecurityConfig.Security_Rules_Excludes;
import static org.hasor.security.SecurityConfig.Security_Rules_Includes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hasor.HasorFramework;
import org.hasor.security.Digest;
import org.hasor.security.SecurityDispatcher;
import org.hasor.security.SecurityForward.ForwardType;
import org.hasor.security.UriPatternMatcher;
import org.hasor.setting.SettingListener;
import org.hasor.setting.Settings;
import org.more.global.assembler.xml.XmlProperty;
import org.more.util.StringConvertUtils;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-4-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class SecuritySettings implements SettingListener {
    private boolean                    enable;                        //Security_Enable                             :启用禁用
    private boolean                    enableMethod;                  //Security_EnableMethod                       :方法权限检查
    private boolean                    enableURL;                     //Security_EnableURL                          :URL权限检查
    private String                     authSessionCacheName;          //Security_AuthSessionCache                   :权限数据缓存名
    private long                       authSessionTimeout;            //Security_AuthSessionTimeout                 :Session超时时间
    private long                       authSessionCacheDefaultTimeout; //Security_AuthSessionCache_Timeout          :AuthSessionCache，设置的超时时间.
    private boolean                    authSessionCacheEternal;       //Security_AuthSessionCache_Eternal           :AuthSessionCache，缓存是否永远不销毁.
    private boolean                    authSessionCacheAutoRenewal;   //Security_AuthSessionCache_AutoRenewal       :AuthSessionCache，每当访问缓存对象时是否自动对其续约（续约时间同加入时缓存超时时间）.
    private long                       authSessionCacheThreadSeep;    //Security_AuthSessionCache_ThreadSeep        :AuthSessionCache，缓存回收线程工作的时间频率(毫秒).
    private String                     loginURL;                      //登入地址
    private String                     logoutURL;                     //登出地址
    private String                     accountField;                  //帐号字段
    private String                     passwordField;                 //密码字段
    private String                     authField;                     //使用的认证系统字段
    private UriPatternMatcher          rulesDefault;                  //URL权限检查默认策略配置                                       :Login|Logout|Guest|Permission|None
    private List<UriPatternMatcher>    rulesIncludeList;              //包含到权限检查路径
    private List<UriPatternMatcher>    rulesExcludeList;              //排除权限检查
    private List<SecurityDispatcher>   dispatcherForwardList;         //转发配置
    private boolean                    guestEnable;                   //Security_Guest_Enable                       :是否启用来宾帐号
    private String                     guestAuthSystem;               //Security_Guest_AuthSystem                   :来宾帐号的认证系统
    private String                     guestAccount;                  //Security_Guest_Account                      :来宾帐号
    private String                     guestPassword;                 //Security_Guest_Password                     :来宾帐号密码
    private String                     guestUserCode;                 //Security_Guest_UserCode                     :来宾用户Code
    private String[]                   guestPermissions;              //Security_Guest_Permissions                  :来宾帐号权限
    private boolean                    cookieEnable;                  //Security_ClientCookie_Enable                :是否启用客户端cookie来协助认证。
    private boolean                    loseCookieOnStart;             //Security_ClientCookie_LoseCookieOnStart     :当系统启动时是否强制所有客户端已经登陆过的Cookie信息失效
    private String                     cookieName;                    //Security_ClientCookie_CookieName            :客户端cookie名称
    private int                        cookieTimeout;                 //Security_ClientCookie_Timeout               :cookie超时时间，单位：秒
    private String                     cookieDomain;                  //Security_ClientCookie_Domain                :cookie的Domain配置，设置这个属性用来支持跨域访问cookie。（默认为空不对该值进行设置）
    private String                     cookiePath;                    //Security_ClientCookie_Path                  :cookie的path属性（默认为空不对该值进行设置）
    private boolean                    cookieEncryptionEnable;        //Security_ClientCookie_Encryption_Enable     :是否加密cookie内容
    private String                     cookieEncryptionEncodeType;    //Security_ClientCookie_Encryption_EncodeType :cookie内容加密方式，DES,BAS64等等.
    private String                     cookieEncryptionKey;           //Security_ClientCookie_Encryption_Key        :cookie内容加密时使用的Key
    private String                     cookieEncryptionScope;         //Security_ClientCookie_Encryption_Scope      :cookie加密范围（ALL,Security）
    private Map<String, Class<Digest>> digestMap;                     //Security_EncryptionDigestSet                :加密算法配置
    //
    //
    public void loadConfig(Settings newConfig) {
        this.enable = newConfig.getBoolean(Security_Enable, false);
        this.enableMethod = newConfig.getBoolean(Security_EnableMethod, false);
        this.enableURL = newConfig.getBoolean(Security_EnableURL, true);
        if (this.enable == false) {
            this.enableMethod = false;
            this.enableURL = false;
        }
        //
        this.authSessionCacheName = newConfig.getString(Security_AuthSessionCache);
        this.authSessionTimeout = newConfig.getLong(Security_AuthSessionTimeout); //Session超时时间
        //
        this.loginURL = newConfig.getString(Security_LoginURL);
        this.logoutURL = newConfig.getString(Security_LogoutURL);
        this.accountField = newConfig.getString(Security_LoginFormData_AccountField);
        this.passwordField = newConfig.getString(Security_LoginFormData_PasswordField);
        this.authField = newConfig.getString(Security_LoginFormData_AuthField);
        //
        XmlProperty defaultRule = newConfig.getXmlProperty(Security_Rules_DefaultModel); //URL权限检查默认策略配置
        Map<String, String> itemAtt = defaultRule.getAttributeMap();
        itemAtt = (itemAtt == null) ? new HashMap<String, String>() : itemAtt;
        String modeType = itemAtt.get("mode");
        String permissionCodes = itemAtt.get("permissions");
        UriPatternType patternType = StringConvertUtils.changeType(modeType, UriPatternType.class, UriPatternType.None);
        this.rulesDefault = UriPatternType.get(patternType, "(Default)", permissionCodes);
        HasorFramework.warning("rules.defaultRule ->%s", this.rulesDefault);
        XmlProperty rulesIncludes = newConfig.getXmlProperty(Security_Rules_Includes); //包含在权限检查范畴的URL配置
        this.rulesIncludeList = new ArrayList<UriPatternMatcher>();
        this.readIncludeRules(rulesIncludes);
        XmlProperty rulesExcludes = newConfig.getXmlProperty(Security_Rules_Excludes); //排除权限检查范畴的URL配置
        this.rulesExcludeList = new ArrayList<UriPatternMatcher>();
        this.readExcludesRules(rulesExcludes);
        // 
        XmlProperty dispatcherXml = newConfig.getXmlProperty(Security_Forwards); //转发配置
        this.dispatcherForwardList = new ArrayList<SecurityDispatcher>();
        this.readDispatcherForward(dispatcherXml);
        //
        this.guestEnable = newConfig.getBoolean(Security_Guest_Enable); //是否启用来宾帐号
        this.guestAuthSystem = newConfig.getString(Security_Guest_AuthSystem); //来宾帐号的认证系统 guest.authSystem
        this.guestAccount = newConfig.getString(Security_Guest_Account); //来宾帐号
        this.guestPassword = newConfig.getString(Security_Guest_Password); //来宾帐号密码
        this.guestUserCode = newConfig.getString(Security_Guest_UserCode); //来宾用户Code
        String guestPermissions = newConfig.getString(Security_Guest_Permissions); //来宾帐号权限
        this.guestPermissions = (guestPermissions != null) ? guestPermissions.split(",") : new String[0];
        //
        this.cookieEnable = newConfig.getBoolean(Security_ClientCookie_Enable); //是否启用客户端cookie来协助认证。
        this.loseCookieOnStart = newConfig.getBoolean(Security_ClientCookie_LoseCookieOnStart);//当系统启动时是否强制所有客户端已经登陆过的Cookie信息失效
        this.cookieName = newConfig.getString(Security_ClientCookie_CookieName); //客户端cookie名称
        this.cookieTimeout = newConfig.getInteger(Security_ClientCookie_Timeout); //cookie超时时间，单位：秒
        this.cookieDomain = newConfig.getString(Security_ClientCookie_Domain); //cookie的Domain配置，设置这个属性用来支持跨域访问cookie。（默认为空不对该值进行设置）
        this.cookiePath = newConfig.getString(Security_ClientCookie_Path);//cookie的path属性（默认为空不对该值进行设置）
        this.cookieEncryptionEnable = newConfig.getBoolean(Security_ClientCookie_Encryption_Enable); //是否加密cookie内容
        this.cookieEncryptionEncodeType = newConfig.getString(Security_ClientCookie_Encryption_EncodeType); //cookie内容加密方式，DES,BAS64等等.
        this.cookieEncryptionKey = newConfig.getString(Security_ClientCookie_Encryption_Key); //cookie内容加密时使用的Key
        this.cookieEncryptionScope = newConfig.getString(Security_ClientCookie_Encryption_Scope); //scope加密范围
        //
        XmlProperty encryptionDigestXml = newConfig.getXmlProperty(Security_EncryptionDigestSet); //加密算法配置
        this.digestMap = new HashMap<String, Class<Digest>>();
        this.readCodeDigest(encryptionDigestXml);
    }
    //
    private void readIncludeRules(XmlProperty rulesIncludes) {
        if (rulesIncludes == null)
            return;
        List<XmlProperty> includeList = rulesIncludes.getChildren();
        if (includeList == null)
            return;
        //
        for (XmlProperty item : includeList) {
            if (StringUtils.eqUnCaseSensitive("include", item.getName()) == false)
                continue;
            Map<String, String> itemAtt = item.getAttributeMap();
            if (itemAtt == null)
                continue;
            //
            String modeType = itemAtt.get("mode");
            String permissionCodes = itemAtt.get("permissions");
            UriPatternType patternType = StringConvertUtils.changeType(modeType, UriPatternType.class, UriPatternType.None);
            String requestURI = item.getText();
            if (StringUtils.isBlank(requestURI) == true)
                continue;
            //
            UriPatternMatcher matcher = UriPatternType.get(patternType, requestURI, permissionCodes);
            this.rulesIncludeList.add(matcher);
            //
            HasorFramework.info("read Include Rule : %s", matcher);
        }
    }
    //
    private void readExcludesRules(XmlProperty rulesExcludes) {
        if (rulesExcludes == null)
            return;
        List<XmlProperty> excludeList = rulesExcludes.getChildren();
        if (excludeList == null)
            return;
        //
        for (XmlProperty item : excludeList) {
            if (StringUtils.eqUnCaseSensitive("exclude", item.getName()) == false)
                continue;
            Map<String, String> itemAtt = item.getAttributeMap();
            if (itemAtt == null)
                continue;
            //
            String requestURI = item.getText();
            if (StringUtils.isBlank(requestURI) == true)
                continue;
            UriPatternMatcher matcher = UriPatternType.get(UriPatternType.None, requestURI, null);
            this.rulesExcludeList.add(matcher);
            //
            HasorFramework.info("read Exclude Rule : %s", matcher);
        }
    }
    //
    private void readDispatcherForward(XmlProperty dispatcherXml) {
        if (dispatcherXml == null)
            return;
        List<XmlProperty> dispatcherList = dispatcherXml.getChildren();
        if (dispatcherList == null)
            return;
        for (XmlProperty item : dispatcherList) {
            if (StringUtils.eqUnCaseSensitive("dispatch", item.getName()) == false)
                continue;
            Map<String, String> itemAtt = item.getAttributeMap();
            String contentPath = itemAtt.get("contentPath");
            ForwardType defaultType = StringConvertUtils.changeType(itemAtt.get("defaultType"), ForwardType.class, ForwardType.Forward);
            if (StringUtils.isBlank(contentPath) == true)
                continue;
            //
            InternalSecurityDispatcher dispatcher = new InternalSecurityDispatcher(contentPath);
            List<XmlProperty> forwardList = item.getChildren();
            if (forwardList != null)
                for (XmlProperty forwardItem : forwardList) {
                    String toURL = forwardItem.getText();
                    String $toType = forwardItem.getAttributeMap().get("type");
                    ForwardType toType = StringConvertUtils.changeType($toType, ForwardType.class, ForwardType.Forward);
                    if (StringUtils.eqUnCaseSensitive("forwardIndex", forwardItem.getName()) == true) {
                        dispatcher.setForwardIndex(toURL, (toType == null) ? defaultType : toType);
                    } else if (StringUtils.eqUnCaseSensitive("forwardLogout", forwardItem.getName()) == true) {
                        dispatcher.setForwardLogout(toURL, (toType == null) ? defaultType : toType);
                    } else if (StringUtils.eqUnCaseSensitive("forwardFailure", forwardItem.getName()) == true) {
                        dispatcher.setForwardFailure(toURL, (toType == null) ? defaultType : toType);
                    } else if (StringUtils.eqUnCaseSensitive("forward", forwardItem.getName()) == true) {
                        String id = forwardItem.getAttributeMap().get("id");
                        dispatcher.addForward(id, toURL, (toType == null) ? defaultType : toType);
                    }
                }
            //
            this.dispatcherForwardList.add(dispatcher);
            HasorFramework.info("read %s", dispatcher);
        }
    }
    //
    private void readCodeDigest(XmlProperty encryptionDigestXml) {
        if (encryptionDigestXml == null)
            return;
        List<XmlProperty> dispatcherList = encryptionDigestXml.getChildren();
        if (dispatcherList == null)
            return;
        for (XmlProperty item : dispatcherList) {
            if (StringUtils.eqUnCaseSensitive("digest", item.getName()) == false)
                continue;
            String digestCode = item.getAttributeMap().get("name");
            String digestType = item.getText();
            try {
                Class<?> digestClass = Class.forName(digestType);
                if (Digest.class.isAssignableFrom(digestClass) == false) {
                    HasorFramework.warning("%s digest %s is not implemented CodeDigest.", digestCode, digestType);
                    continue;
                }
                this.digestMap.put(digestCode, (Class<Digest>) digestClass);
                HasorFramework.info("read CodeDigest %s : %s", digestCode, digestType);
            } catch (Exception e) {
                HasorFramework.warning("create %s an error.%s", digestType, e);
            }
        }
    }
    //
    //
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
    public String getAuthSessionCacheName() {
        return authSessionCacheName;
    }
    public long getAuthSessionTimeout() {
        return authSessionTimeout;
    }
    public long getAuthSessionCacheDefaultTimeout() {
        return authSessionCacheDefaultTimeout;
    }
    public boolean isAuthSessionCacheEternal() {
        return authSessionCacheEternal;
    }
    public boolean isAuthSessionCacheAutoRenewal() {
        return authSessionCacheAutoRenewal;
    }
    public long getAuthSessionCacheThreadSeep() {
        return authSessionCacheThreadSeep;
    }
    public String getLoginURL() {
        return loginURL;
    }
    public String getLogoutURL() {
        return logoutURL;
    }
    public String getAccountField() {
        return accountField;
    }
    public String getPasswordField() {
        return passwordField;
    }
    public String getAuthField() {
        return authField;
    }
    public UriPatternMatcher getRulesDefault() {
        return rulesDefault;
    }
    public List<UriPatternMatcher> getRulesIncludeList() {
        return rulesIncludeList;
    }
    public List<UriPatternMatcher> getRulesExcludeList() {
        return rulesExcludeList;
    }
    public List<SecurityDispatcher> getDispatcherForwardList() {
        return dispatcherForwardList;
    }
    public boolean isGuestEnable() {
        return guestEnable;
    }
    public String getGuestAuthSystem() {
        return guestAuthSystem;
    }
    public String getGuestAccount() {
        return guestAccount;
    }
    public String getGuestPassword() {
        return guestPassword;
    }
    public String getGuestUserCode() {
        return guestUserCode;
    }
    public String[] getGuestPermissions() {
        return guestPermissions;
    }
    public boolean isCookieEnable() {
        return cookieEnable;
    }
    public boolean isLoseCookieOnStart() {
        return loseCookieOnStart;
    }
    public String getCookieName() {
        return cookieName;
    }
    public int getCookieTimeout() {
        return cookieTimeout;
    }
    public String getCookieDomain() {
        return cookieDomain;
    }
    public String getCookiePath() {
        return cookiePath;
    }
    public boolean isCookieEncryptionEnable() {
        return cookieEncryptionEnable;
    }
    public String getCookieEncryptionEncodeType() {
        return cookieEncryptionEncodeType;
    }
    public String getCookieEncryptionKey() {
        return cookieEncryptionKey;
    }
    public String getCookieEncryptionScope() {
        return cookieEncryptionScope;
    }
    public Map<String, Class<Digest>> getDigestMap() {
        return digestMap;
    }
};