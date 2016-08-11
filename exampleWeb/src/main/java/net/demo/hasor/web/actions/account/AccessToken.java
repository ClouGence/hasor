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
package net.demo.hasor.web.actions.account;
import net.demo.hasor.core.Action;
import net.demo.hasor.domain.AppConstant;
import net.demo.hasor.domain.UserDO;
import net.demo.hasor.domain.enums.ErrorCodes;
import net.demo.hasor.manager.UserManager;
import net.demo.hasor.manager.oauth.OAuthManager;
import net.demo.hasor.utils.LogUtils;
import net.demo.hasor.web.forms.LoginCallBackForm;
import net.hasor.core.Inject;
import net.hasor.restful.api.MappingTo;
import net.hasor.restful.api.Params;
import org.more.bizcommon.Result;
import org.more.util.StringUtils;

import java.io.IOException;
/**
 * OAuth : 服务器获取 AccessToken
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/account/access_token.do")
public class AccessToken extends Action {
    @Inject
    private OAuthManager oauthManager;
    @Inject
    private UserManager  userManager;
    //
    public void execute(@Params LoginCallBackForm loginForm) throws IOException {
        //
        String ajaxTo = this.getRequest().getHeader("ajaxTo");
        if (StringUtils.isBlank(ajaxTo) || !StringUtils.equalsIgnoreCase(ajaxTo, "true")) {
            logger.error(LogUtils.create("ERROR_000_0002")//
                    .addString("login_error : request not accepted.").toJson());
            sendError(ErrorCodes.BAD_REQUEST.getMsg());
            return;
        }
        // .csrf
        if (!this.csrfTokenTest()) {
            logger.error(LogUtils.create("ERROR_000_0001")//
                    .addString("login_error : csrfToken failed.").toJson());
            sendJsonError(ErrorCodes.SECURITY_CSRF.getMsg());
            return;
        }
        //
        if (StringUtils.isBlank(loginForm.getCode())) {
            logger.error(LogUtils.create("ERROR_000_1001")//
                    .addLog("provider", loginForm.getProvider())//
                    .addLog("code", loginForm.getCode())//
                    .addString("login_error : get access_token failed, response is empty.").toJson());
            sendJsonError(ErrorCodes.LOGIN_OAUTH_CODE_EMPTY.getMsg(loginForm.getCode()));
            return;
        }
        //
        Result<Long> result = this.oauthManager.processAccess(loginForm.getCode(), loginForm.getProvider(), this.csrfTokenString());
        if (result == null) {
            logger.error(LogUtils.create("ERROR_999_0001")//
                    .addString("login_error : result is null.").toJson());
            sendJsonError(ErrorCodes.RESULT_NULL.getMsg());
            return;
        }
        //
        if (!result.isSuccess()) {
            logger.error(LogUtils.create("ERROR_000_1002")//
                    .addString("login_error : access process failed.").toJson());
            sendJsonError(result.firstMessage());
            return;
        }
        //
        // .跳转到目标页面
        if (result.getResult() != null && result.getResult() > 0) {
            long userID = result.getResult();
            UserDO userDO = this.userManager.getUserByID(userID);
            if (userDO != null) {
                this.setSessionAttr(AppConstant.SESSION_KEY_USER_ID, userDO.getUserID());
                this.setSessionAttr(AppConstant.SESSION_KEY_USER_NICK, userDO.getNick());
                sendJsonData(loginForm.getRedirectURI());//跳转的目标地址
                return;
            } else {
                //
                logger.error(LogUtils.create("ERROR_999_0001")//
                        .addString("login_error : query user by id result is null.").toJson());
                sendJsonError(ErrorCodes.RESULT_NULL.getMsg());
                return;
            }
        } else {
            //
            logger.error(LogUtils.create("ERROR_000_1003")//
                    .addString("login_error : access failed.").toJson());
            sendJsonError(ErrorCodes.LOGIN_OAUTH_ACCESS_FAILED.getMsg());
            return;
        }
    }
}