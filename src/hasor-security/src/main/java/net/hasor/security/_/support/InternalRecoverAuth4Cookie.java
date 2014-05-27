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
package net.hasor.security._.support;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.hasor.security.SecurityException;
import net.hasor.security._.AuthSession;
import net.hasor.security._.SecurityContext;
import net.hasor.security._.support.CookieDataUtil.CookieUserData;
import org.hasor.Hasor;
import org.more.util.StringUtils;
import com.google.inject.Inject;
/**
 * 
 * @version : 2013-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalRecoverAuth4Cookie {
    protected SecuritySettings settings = null;
    @Inject
    public InternalRecoverAuth4Cookie(SecuritySettings settings) {
        this.settings = settings;
    };
    /**恢复权限*/
    public AuthSession[] recoverCookie(SecurityContext secContext, HttpServletRequest request, HttpServletResponse response) throws SecurityException {
        //1.恢复会话
        boolean recoverMark = this.recoverAuthSession4HttpSession(secContext, request.getSession(true));
        if (recoverMark == false)
            recoverMark = this.recoverAuthSession4Cookie(secContext, request);
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
                Hasor.warning("%s", e);
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
            Hasor.warning("recover cookieUser failure! userCode=%s", userCode);
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
        String cookieValue = null;
        Cookie[] cookieArray = httpRequest.getCookies();
        if (cookieArray != null)
            for (Cookie cookie : cookieArray) {
                //匹配cookie名称
                if (cookie.getName().endsWith(this.settings.getCookieName()) == false)
                    continue;
                cookieValue = cookie.getValue();
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
            Hasor.debug("parseJson to CookieDataUtil error! %s decode. cookieValue=%s", this.settings.getCookieEncryptionEncodeType(), cookieValue);
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
        String authSessionIDs = (String) httpSession.getAttribute(AuthSession.HttpSessionAuthSessionSetName);
        if (StringUtils.isBlank(authSessionIDs) == true)
            return false;
        String[] authSessionIDSet = authSessionIDs.split(",");
        boolean returnData = false;
        for (String authSessionID : authSessionIDSet) {
            try {
                if (secContext.activateAuthSession(authSessionID) == true) {
                    Hasor.debug("authSession : %s activate!", authSessionID);
                    returnData = true;
                }
            } catch (SecurityException e) {
                Hasor.warning("%s activate an error.%s", authSessionID, e);
            }
        }
        return returnData;
    }
}