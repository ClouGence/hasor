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
package org.platform.security.process;
import static org.platform.security.AuthSession.HttpSessionAuthSessionSetName;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.more.util.StringUtil;
import org.platform.Platform;
import org.platform.context.ViewContext;
import org.platform.security.AuthSession;
import org.platform.security.AutoLoginProcess;
import org.platform.security.Digest;
import org.platform.security.SecurityContext;
import org.platform.security.SecurityException;
import org.platform.security.process.CookieDataUtil.CookieUserData;
/**
 * {@link AutoLoginProcess}接口默认实现。
 * @version : 2013-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultAutoLoginProcess extends AbstractProcess implements AutoLoginProcess {
    /**写入会话数据。*/
    /**写入会话数据。*/
    public void writeCookie(SecurityContext secContext, AuthSession[] authSessions, ViewContext viewContext) throws SecurityException {
        //
        if (this.settings.isCookieEncryptionEnable() == false)
            return;
        //2.写入Cookie对象
        CookieDataUtil cookieData = CookieDataUtil.create();
        if (authSessions != null)
            for (AuthSession authSession : authSessions) {
                if (authSession.isLogin() == false)
                    continue;
                CookieUserData cookieUserData = new CookieUserData();
                cookieUserData.setUserCode(authSession.getUserObject().getUserCode());//用户Code
                cookieUserData.setAuthSystem(authSession.getAuthSystem());//用户来源
                cookieUserData.setAppStartTime(secContext.getAppContext().getAppStartTime());
                cookieData.addCookieUserData(cookieUserData);
            }
        //2.创建Cookie
        String cookieValue = CookieDataUtil.parseString(cookieData);
        if (this.settings.isCookieEncryptionEnable() == true) {
            Digest digest = secContext.getCodeDigest(this.settings.getCookieEncryptionEncodeType());
            try {
                cookieValue = digest.encrypt(cookieValue, this.settings.getCookieEncryptionKey());
            } catch (Throwable e) {
                Platform.warning("%s encode cookieValue error. cookieValue=%s", this.settings.getCookieEncryptionEncodeType(), cookieValue);
                return;
            }
        }
        Cookie cookie = new Cookie(this.settings.getCookieName(), cookieValue);
        cookie.setMaxAge(this.settings.getCookieTimeout());
        String cookiePath = this.settings.getCookiePath();
        String cookieDomain = this.settings.getCookieDomain();
        if (StringUtil.isBlank(cookiePath) == false)
            cookie.setPath(cookiePath);
        if (StringUtil.isBlank(cookieDomain) == false)
            cookie.setDomain(cookieDomain);
        //3.写入响应流
        viewContext.getHttpResponse().addCookie(cookie);
    }
    /**恢复权限*/
    public AuthSession[] recoverCookie(SecurityContext secContext, ViewContext viewContext) throws SecurityException {
        //1.恢复会话
        boolean recoverMark = this.recoverAuthSession4HttpSession(secContext, viewContext.getHttpSession(true));
        if (recoverMark == false)
            recoverMark = this.recoverAuthSession4Cookie(secContext, viewContext.getHttpRequest());
        if (recoverMark == true)
            return secContext.getCurrentAuthSession();
        //2.处理来宾账户
        if (this.settings.isGuestEnable() == true) {
            try {
                AuthSession targetAuthSession = secContext.getCurrentBlankAuthSession();
                if (targetAuthSession == null)
                    targetAuthSession = secContext.createAuthSession();
                String guestAccount = this.settings.getGuestAccount();
                String guestPassword = this.settings.getGuestPassword();
                String guestAuthSystem = this.settings.getGuestAuthSystem();
                targetAuthSession.doLogin(guestAuthSystem, guestAccount, guestPassword);/*登陆来宾帐号*/
            } catch (Exception e) {
                Platform.warning("%s", e);
            }
        }
        return secContext.getCurrentAuthSession();
    }
    private void recoverUserByCode(SecurityContext secContext, String authSystem, String userCode) throws SecurityException {
        /**通过userCode采用重新登陆的方式恢复AuthSession*/
        AuthSession newAuthSession = null;
        try {
            newAuthSession = secContext.getCurrentBlankAuthSession();
            if (newAuthSession == null)
                newAuthSession = secContext.createAuthSession();
            //
            newAuthSession.doLoginCode(authSystem, userCode);
        } catch (SecurityException e) {
            Platform.warning("recover cookieUser failure! userCode=%s", userCode);
            if (newAuthSession != null)
                newAuthSession.close();
        }
    }
    private boolean recoverAuthSession4Cookie(SecurityContext secContext, HttpServletRequest httpRequest) throws SecurityException {
        /**恢复Cookie中的登陆帐号,该方法会导致调用writeHttpSession方法。*/
        //1.检测Cookie
        if (this.settings.isCookieEnable() == false)
            return false;
        //2.解码cookie的value
        Cookie[] cookieArray = httpRequest.getCookies();
        String cookieValue = null;
        for (Cookie cookie : cookieArray) {
            //匹配cookie名称
            if (cookie.getName().endsWith(this.settings.getCookieName()) == false)
                continue;
            cookieValue = cookie.getValue();
            if (this.settings.isCookieEncryptionEnable() == true) {
                Digest digest = secContext.getCodeDigest(this.settings.getCookieEncryptionEncodeType());
                try {
                    cookieValue = digest.decrypt(cookieValue, this.settings.getCookieEncryptionKey());
                } catch (Throwable e) {
                    Platform.warning("%s decode cookieValue error. cookieValue=%s", this.settings.getCookieEncryptionEncodeType(), cookieValue);
                    return false;/*解密失败意味着后面的恢复操作都不会用到有效数据因此return.*/
                }
            }
            break;
        }
        //3.读取cookie内容恢复权限会话
        CookieUserData[] infos = null;
        try {
            CookieDataUtil cookieData = CookieDataUtil.parseJson(cookieValue);
            infos = cookieData.getCookieUserDatas();
            if (infos == null)
                return false;
        } catch (Exception e) {
            Platform.debug("parseJson to CookieDataUtil error! %s decode. cookieValue=%s", this.settings.getCookieEncryptionEncodeType(), cookieValue);
            return false;
        }
        boolean returnData = false;
        //4.恢复Cookie里保存的会话
        for (CookieUserData info : infos) {
            if (this.settings.isLoseCookieOnStart() == true)
                if (secContext.getAppContext().getAppStartTime() != info.getAppStartTime())
                    continue;
            /*用userCode恢复出一个新的会话*/
            this.recoverUserByCode(secContext, info.getAuthSystem(), info.getUserCode());
            returnData = true;
        }
        return returnData;
    }
    private boolean recoverAuthSession4HttpSession(SecurityContext secContext, HttpSession httpSession) {
        /**恢复HttpSession中的登陆帐号。*/
        String authSessionIDs = (String) httpSession.getAttribute(HttpSessionAuthSessionSetName);
        if (StringUtil.isBlank(authSessionIDs) == true)
            return false;
        String[] authSessionIDSet = authSessionIDs.split(",");
        boolean returnData = false;
        for (String authSessionID : authSessionIDSet) {
            try {
                if (secContext.activateAuthSession(authSessionID) == true) {
                    Platform.debug("authSession : %s activate!", authSessionID);
                    returnData = true;
                }
            } catch (SecurityException e) {
                Platform.warning("%s activate an error.%s", authSessionID, e);
            }
        }
        return returnData;
    }
}