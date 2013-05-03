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
import static org.platform.security.AuthSession.HttpSessionAuthSessionSetName;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.more.util.StringUtil;
import org.platform.Platform;
import org.platform.context.AppContext;
import org.platform.security.CookieDataUtil.CookieUserData;
import com.google.inject.Inject;
/**
 * 权限处理过程,负责处理登入、登出的核心逻辑。
 * @version : 2013-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalSecurityProcess implements SecurityProcess {
    @Inject
    private SecuritySettings settings   = null;
    @Inject
    private SecurityContext  secService = null;
    @Inject
    private AppContext       appContext = null;
    //
    private void writeHttpSession(HttpServletRequest httpRequest) {
        AuthSession[] authSessions = this.secService.getCurrentAuthSession();
        //1.写入HttpSession
        StringBuilder authSessionIDs = new StringBuilder("");
        for (AuthSession authSession : authSessions)
            authSessionIDs.append(authSession.getSessionID() + ",");
        httpRequest.getSession(true).setAttribute(HttpSessionAuthSessionSetName, authSessionIDs.toString());
    }
    //
    /**写入权限Cookie。*/
    public void writeAuthSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws SecurityException {
        this.writeHttpSession(httpRequest);
        //
        if (this.settings.isCookieEncryptionEnable() == false)
            return;
        //2.写入Cookie对象
        CookieDataUtil cookieData = CookieDataUtil.create();
        AuthSession[] authSessions = this.secService.getCurrentAuthSession();
        if (authSessions != null)
            for (AuthSession authSession : authSessions) {
                if (authSession.isLogin() == false)
                    continue;
                CookieUserData cookieUserData = new CookieUserData();
                cookieUserData.setUserCode(authSession.getUserObject().getUserCode());//用户Code
                cookieUserData.setAuthSystem(authSession.getAuthSystem());//用户来源
                cookieUserData.setAppStartTime(this.appContext.getAppStartTime());
                cookieData.addCookieUserData(cookieUserData);
            }
        //2.创建Cookie
        String cookieValue = CookieDataUtil.parseString(cookieData);
        if (this.settings.isCookieEncryptionEnable() == true) {
            Digest digest = this.secService.getCodeDigest(this.settings.getCookieEncryptionEncodeType());
            try {
                cookieValue = digest.encrypt(cookieValue, this.settings.getCookieEncryptionKey());
            } catch (Throwable e) {
                Platform.warning(this.settings.getCookieEncryptionEncodeType() + " encode cookieValue error. cookieValue=" + cookieValue);
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
        httpResponse.addCookie(cookie);
    }
    //
    /**通过userCode采用重新登陆的方式恢复AuthSession*/
    private void recoverUserByCode(String authSystem, String userCode) throws SecurityException {
        AuthSession newAuthSession = null;
        try {
            newAuthSession = this.secService.getCurrentBlankAuthSession();
            if (newAuthSession == null)
                newAuthSession = this.secService.createAuthSession();
            //
            newAuthSession.doLoginCode(authSystem, userCode);
        } catch (SecurityException e) {
            Platform.warning("recover cookieUser failure! userCode=" + userCode);
            if (newAuthSession != null)
                newAuthSession.close();
        }
    }
    //
    /**恢复Cookie中的登陆帐号,该方法会导致调用writeHttpSession方法。*/
    private boolean recoverAuthSession4Cookie(HttpServletRequest httpRequest) throws SecurityException {
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
                Digest digest = this.secService.getCodeDigest(this.settings.getCookieEncryptionEncodeType());
                try {
                    cookieValue = digest.decrypt(cookieValue, this.settings.getCookieEncryptionKey());
                } catch (Throwable e) {
                    Platform.warning(this.settings.getCookieEncryptionEncodeType() + " decode cookieValue error. cookieValue=" + cookieValue);
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
            Platform.debug("parseJson to CookieDataUtil error! " + this.settings.getCookieEncryptionEncodeType() + " decode . cookieValue=" + cookieValue);
            return false;
        }
        boolean returnData = false;
        //4.恢复Cookie里保存的会话
        for (CookieUserData info : infos) {
            if (this.settings.isLoseCookieOnStart() == true)
                if (this.appContext.getAppStartTime() != info.getAppStartTime())
                    continue;
            /*用userCode恢复出一个新的会话*/
            this.recoverUserByCode(info.getAuthSystem(), info.getUserCode());
            returnData = true;
        }
        this.writeHttpSession(httpRequest);
        return returnData;
    }
    //
    /**恢复HttpSession中的登陆帐号。*/
    private boolean recoverAuthSession4HttpSession(HttpSession httpSession) {
        String authSessionIDs = (String) httpSession.getAttribute(HttpSessionAuthSessionSetName);
        if (StringUtil.isBlank(authSessionIDs) == true)
            return false;
        String[] authSessionIDSet = authSessionIDs.split(",");
        boolean returnData = false;
        for (String authSessionID : authSessionIDSet) {
            try {
                if (this.secService.activateAuthSession(authSessionID) == true) {
                    Platform.debug("authSession : " + authSessionID + " activate!");
                    returnData = true;
                }
            } catch (SecurityException e) {
                Platform.info(authSessionID + " activate an error. " + Platform.logString(e));
            }
        }
        return returnData;
    }
    //
    /**恢复权限*/
    public void recoverAuthSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws SecurityException {
        //1.恢复会话
        boolean recoverMark = this.recoverAuthSession4HttpSession(httpRequest.getSession(true));
        if (recoverMark == false)
            recoverMark = this.recoverAuthSession4Cookie(httpRequest);
        if (recoverMark == true)
            return;
        //2.处理来宾账户
        if (this.settings.isGuestEnable() == true) {
            try {
                AuthSession targetAuthSession = this.secService.getCurrentBlankAuthSession();
                if (targetAuthSession == null)
                    targetAuthSession = this.secService.createAuthSession();
                String guestAccount = this.settings.getGuestAccount();
                String guestPassword = this.settings.getGuestPassword();
                String guestAuthSystem = this.settings.getGuestAuthSystem();
                targetAuthSession.doLogin(guestAuthSystem, guestAccount, guestPassword);/*登陆来宾帐号*/
            } catch (Exception e) {
                Platform.warning(Platform.logString(e));
            }
        }
    }
    //
    /**处理登入请求。*/
    public void processLogin(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws SecurityException {
        //1.获得登入相关信息
        String account = httpRequest.getParameter(this.settings.getAccountField());
        String password = httpRequest.getParameter(this.settings.getPasswordField());
        String formAuth = httpRequest.getParameter(this.settings.getAuthField());
        //3.执行登入
        AuthSession authSession = this.secService.getCurrentBlankAuthSession();
        if (authSession == null)
            authSession = this.secService.createAuthSession();
        try {
            authSession.doLogin(formAuth, account, password);/*登入新会话*/
            Platform.info("login OK. acc=" + account + " , at SessionID=" + authSession.getSessionID());
            this.writeAuthSession(httpRequest, httpResponse);
        } catch (SecurityException e) {
            Platform.warning("login failure! acc=" + account + " , msg=" + e.getMessage());
            authSession.close();
            throw e;
        }
    }
    //
    /**处理登出请求*/
    public void processLogout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws SecurityException {
        AuthSession[] authSessions = this.secService.getCurrentAuthSession();
        for (AuthSession authSession : authSessions) {
            /*将所有已登入的会话全部登出*/
            if (authSession.isLogin() == false)
                continue;
            String userCode = authSession.getUserObject().getUserCode();
            try {
                authSession.doLogout();/*退出会话*/
                Platform.info("logout OK. userCode=" + userCode + " , at SessionID=" + authSession.getSessionID());
            } catch (SecurityException e) {
                Platform.info("logout failure! userCode=" + userCode + " , at SessionID=" + authSession.getSessionID());
                throw e;
            }
        }
        //
        this.writeAuthSession(httpRequest, httpResponse);
    }
    //
    /**测试要处理的资源是否具有权限访问，如果权限检测失败会抛出PermissionException异常。*/
    public void processTestFilter(String reqPath) throws PermissionException {
        UriPatternMatcher uriMatcher = this.secService.getUriMatcher(reqPath);
        AuthSession[] authSessions = this.secService.getCurrentAuthSession();
        if (uriMatcher.testPermission(authSessions) == false)
            throw new PermissionException(reqPath);
    }
}