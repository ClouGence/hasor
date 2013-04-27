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
import static org.platform.PlatformConfig.Security_AuthSessionCache;
import static org.platform.PlatformConfig.Security_ClientCookie_CookieName;
import static org.platform.PlatformConfig.Security_ClientCookie_Domain;
import static org.platform.PlatformConfig.Security_ClientCookie_Enable;
import static org.platform.PlatformConfig.Security_ClientCookie_Encryption_Enable;
import static org.platform.PlatformConfig.Security_ClientCookie_Encryption_EncodeType;
import static org.platform.PlatformConfig.Security_ClientCookie_Encryption_Key;
import static org.platform.PlatformConfig.Security_ClientCookie_LoseCookieOnStart;
import static org.platform.PlatformConfig.Security_ClientCookie_Path;
import static org.platform.PlatformConfig.Security_ClientCookie_Timeout;
import static org.platform.PlatformConfig.Security_Enable;
import static org.platform.PlatformConfig.Security_EnableMethod;
import static org.platform.PlatformConfig.Security_EnableURL;
import static org.platform.PlatformConfig.Security_EncryptionDigestSet;
import static org.platform.PlatformConfig.Security_Forwards;
import static org.platform.PlatformConfig.Security_Guest_ClassType;
import static org.platform.PlatformConfig.Security_Guest_Enable;
import static org.platform.PlatformConfig.Security_Guest_Permissions;
import static org.platform.PlatformConfig.Security_LoginFormData_AccountField;
import static org.platform.PlatformConfig.Security_LoginFormData_PasswordField;
import static org.platform.PlatformConfig.Security_LoginURL;
import static org.platform.PlatformConfig.Security_LogoutURL;
import static org.platform.PlatformConfig.Security_Rules_DefaultModel;
import static org.platform.PlatformConfig.Security_Rules_Excludes;
import static org.platform.PlatformConfig.Security_Rules_Includes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.global.assembler.xml.XmlProperty;
import org.more.util.StringConvertUtil;
import org.more.util.StringUtil;
import org.platform.Platform;
import org.platform.context.SettingListener;
import org.platform.context.setting.Settings;
import org.platform.security.SecurityDispatcher.DispatcherType;
/**
 * 
 * @version : 2013-4-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class SecuritySettings implements SettingListener {
    private boolean                    enable                     = false; //启用禁用
    private boolean                    enableMethod               = true; //方法权限检查
    private boolean                    enableURL                  = true; //URL权限检查
    private String                     authSessionCacheName       = null; //权限数据缓存名
    private String                     accountField               = null; //帐号字段
    private String                     passwordField              = null; //密码字段
    private String                     loginURL                   = null; //登入地址
    private String                     logoutURL                  = null; //登出地址
    private boolean                    guestEnable                = false; //是否启用来宾帐号
    private String                     guestClassType             = null; //来宾帐号类型
    private String[]                   guestPermissions           = null; //来宾帐号权限
    private UriPatternMatcher          rulesDefault               = null; //URL权限检查默认策略配置：Login|Logout|Guest|Permission|None
    private List<UriPatternMatcher>    rulesIncludeList           = null; //包含到权限检查路径
    private List<UriPatternMatcher>    rulesExcludeList           = null; //排除权限检查
    private boolean                    cookieEnable               = true; //是否启用客户端cookie来协助认证。
    private boolean                    loseCookieOnStart          = true; //当系统启动时是否强制所有客户端已经登陆过的Cookie信息失效
    private String                     cookieName                 = null; //客户端cookie名称
    private int                        cookieTimeout              = 0;    //cookie超时时间，单位：秒
    private boolean                    cookieEncryptionEnable     = true; //是否加密cookie内容
    private String                     cookieEncryptionEncodeType = null; //cookie内容加密方式，DES,BAS64等等.
    private String                     cookieEncryptionKey        = null; //cookie内容加密时使用的Key
    private String                     cookieDomain               = null; //cookie的Domain配置，设置这个属性用来支持跨域访问cookie。（默认为空不对该值进行设置）
    private String                     cookiePath                 = null; //cookie的path属性（默认为空不对该值进行设置）
    private List<SecurityDispatcher>   dispatcherForwardList      = null; //转发配置
    private Map<String, Class<Digest>> digestMap                  = null; //加密算法配置
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
        //
        this.accountField = newConfig.getString(Security_LoginFormData_AccountField);
        this.passwordField = newConfig.getString(Security_LoginFormData_PasswordField);
        this.loginURL = newConfig.getString(Security_LoginURL);
        this.logoutURL = newConfig.getString(Security_LogoutURL);
        //
        this.guestEnable = newConfig.getBoolean(Security_Guest_Enable); //是否启用来宾帐号
        this.guestClassType = newConfig.getString(Security_Guest_ClassType); //来宾帐号类型
        String guestPermissions = newConfig.getString(Security_Guest_Permissions); //来宾帐号权限
        this.guestPermissions = (guestPermissions != null) ? guestPermissions.split(",") : new String[0];
        //
        XmlProperty defaultRule = newConfig.getXmlProperty(Security_Rules_DefaultModel); //URL权限检查默认策略配置
        Map<String, String> itemAtt = defaultRule.getAttributeMap();
        itemAtt = (itemAtt == null) ? new HashMap<String, String>() : itemAtt;
        String modeType = itemAtt.get("mode");
        String permissionCodes = itemAtt.get("permissions");
        UriPatternType patternType = StringConvertUtil.changeType(modeType, UriPatternType.class, UriPatternType.None);
        String requestURI = defaultRule.getText();
        this.rulesDefault = UriPatternType.get(patternType, requestURI, permissionCodes);
        //
        XmlProperty rulesIncludes = newConfig.getXmlProperty(Security_Rules_Includes); //包含在权限检查范畴的URL配置
        this.rulesIncludeList = new ArrayList<UriPatternMatcher>();
        this.readIncludeRules(rulesIncludes);
        XmlProperty rulesExcludes = newConfig.getXmlProperty(Security_Rules_Excludes); //排除权限检查范畴的URL配置
        this.rulesExcludeList = new ArrayList<UriPatternMatcher>();
        this.readExcludesRules(rulesExcludes);
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
        // 
        XmlProperty dispatcherXml = newConfig.getXmlProperty(Security_Forwards); //转发配置
        this.dispatcherForwardList = new ArrayList<SecurityDispatcher>();
        this.readDispatcherForward(dispatcherXml);
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
            if (StringUtil.eqUnCaseSensitive("include", item.getName()) == false)
                continue;
            Map<String, String> itemAtt = item.getAttributeMap();
            if (itemAtt == null)
                continue;
            //
            String modeType = itemAtt.get("mode");
            String permissionCodes = itemAtt.get("permissions");
            UriPatternType patternType = StringConvertUtil.changeType(modeType, UriPatternType.class, UriPatternType.None);
            String requestURI = item.getText();
            //
            UriPatternMatcher matcher = UriPatternType.get(patternType, requestURI, permissionCodes);
            this.rulesIncludeList.add(matcher);
            //
            Platform.info("read Include Rule : " + matcher);
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
            if (StringUtil.eqUnCaseSensitive("exclude", item.getName()) == false)
                continue;
            Map<String, String> itemAtt = item.getAttributeMap();
            if (itemAtt == null)
                continue;
            //
            String requestURI = item.getText();
            UriPatternMatcher matcher = UriPatternType.get(UriPatternType.None, requestURI, null);
            this.rulesExcludeList.add(matcher);
            //
            Platform.info("read Exclude Rule : " + matcher);
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
            if (StringUtil.eqUnCaseSensitive("dispatch", item.getName()) == false)
                continue;
            Map<String, String> itemAtt = item.getAttributeMap();
            String contentPath = itemAtt.get("contentPath");
            DispatcherType defaultType = StringConvertUtil.changeType(itemAtt.get("defaultType"), DispatcherType.class, DispatcherType.Forward);
            if (StringUtil.isBlank(contentPath) == true)
                continue;
            //
            InternalSecurityDispatcher dispatcher = new InternalSecurityDispatcher(contentPath);
            List<XmlProperty> forwardList = item.getChildren();
            if (forwardList != null)
                for (XmlProperty forwardItem : forwardList) {
                    String toURL = forwardItem.getText();
                    String $toType = forwardItem.getAttributeMap().get("type");
                    DispatcherType toType = StringConvertUtil.changeType($toType, DispatcherType.class, DispatcherType.Forward);
                    if (StringUtil.eqUnCaseSensitive("forwardIndex", forwardItem.getName()) == true) {
                        dispatcher.setForwardIndex(toURL, (toType == null) ? defaultType : toType);
                    } else if (StringUtil.eqUnCaseSensitive("forwardLogout", forwardItem.getName()) == true) {
                        dispatcher.setForwardLogout(toURL, (toType == null) ? defaultType : toType);
                    } else if (StringUtil.eqUnCaseSensitive("forwardFailure", forwardItem.getName()) == true) {
                        dispatcher.setForwardFailure(toURL, (toType == null) ? defaultType : toType);
                    } else if (StringUtil.eqUnCaseSensitive("forward", forwardItem.getName()) == true) {
                        String id = forwardItem.getAttributeMap().get("id");
                        dispatcher.addForward(id, toURL, (toType == null) ? defaultType : toType);
                    }
                }
            //
            this.dispatcherForwardList.add(dispatcher);
            Platform.info("read SecurityDispatcher : " + dispatcher);
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
            if (StringUtil.eqUnCaseSensitive("digest", item.getName()) == false)
                continue;
            String digestCode = item.getAttributeMap().get("name");
            String digestType = item.getText();
            try {
                Class<?> digestClass = Class.forName(digestType);
                if (Digest.class.isAssignableFrom(digestClass) == false) {
                    Platform.warning(digestCode + " digest " + digestType + " is not implemented CodeDigest");
                    continue;
                }
                this.digestMap.put(digestCode, (Class<Digest>) digestClass);
                Platform.info("read CodeDigest " + digestCode + " = " + digestType);
            } catch (Exception e) {
                Platform.warning("create " + digestType + " an error ." + Platform.logString(e));
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
    public boolean isGuestEnable() {
        return guestEnable;
    }
    public String getGuestClassType() {
        return guestClassType;
    }
    public String[] getGuestPermissions() {
        return guestPermissions;
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
    public boolean isCookieEncryptionEnable() {
        return cookieEncryptionEnable;
    }
    public String getCookieEncryptionEncodeType() {
        return cookieEncryptionEncodeType;
    }
    public String getCookieEncryptionKey() {
        return cookieEncryptionKey;
    }
    public String getCookieDomain() {
        return cookieDomain;
    }
    public String getCookiePath() {
        return cookiePath;
    }
    public List<SecurityDispatcher> getDispatcherForwardList() {
        return dispatcherForwardList;
    }
    public Map<String, Class<Digest>> getDigestMap() {
        return digestMap;
    }
};