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
import net.demo.hasor.domain.ErrorCodes;
import net.demo.hasor.utils.LogUtils;
import net.demo.hasor.web.forms.LoginCallBackForm;
import net.demo.hasor.web.oauth.OAuthManager;
import net.hasor.core.Inject;
import net.hasor.restful.api.MappingTo;
import net.hasor.restful.api.Params;
import org.more.bizcommon.Message;
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
    //
    public void execute(@Params LoginCallBackForm loginForm) throws IOException {
        //
        String ajaxTo = this.getRequest().getHeader("ajaxTo");
        if (StringUtils.isBlank(ajaxTo) || !StringUtils.equalsIgnoreCase(ajaxTo, "true")) {
            Message errorMsg = ErrorCodes.SECURITY_CSRF.getMsg();
            logger.error(LogUtils.create("ERROR_000_0001").addMessage(errorMsg)//
                    .addString("login_error : need ajax header.").toJson());
            sendError(errorMsg);
            return;
        }
        // .csrf
        if (!this.csrfTokenTest()) {
            Message errorMsg = ErrorCodes.SECURITY_CSRF.getMsg();
            logger.error(LogUtils.create("ERROR_000_0002").addMessage(errorMsg)//
                    .addString("login_error : csrfToken failed.").toJson());
            sendJsonError(errorMsg);
            return;
        }
        //
        if (StringUtils.isBlank(loginForm.getCode())) {
            Message errorMsg = ErrorCodes.LOGIN_OAUTH_CODE_EMPTY.getMsg(loginForm.getCode());
            logger.error(LogUtils.create("ERROR_000_0003").addMessage(errorMsg)//
                    .addString("login_error : get access_token failed, response is empty.").toJson());
            sendJsonError(errorMsg);
            return;
        }
        //
        Result<Boolean> result = this.oauthManager.processAccess(loginForm.getCode(), loginForm.getProvider());
        if (result == null) {
            Message errorMsg = ErrorCodes.RESULT_NULL.getMsg();
            logger.error(LogUtils.create("ERROR_000_0004").addMessage(errorMsg)//
                    .addString("login_error : processAccess result is empty.").toJson());
            sendJsonError(errorMsg);
            return;
        }
        //
        if (!result.isSuccess()) {
            Message errorMsg = result.firstMessage();
            logger.error(LogUtils.create("ERROR_000_0005").addMessage(errorMsg)//
                    .addString("login_error : access process failed.").toJson());
            sendJsonError(errorMsg);
            return;
        }
        //
        // .跳转到目标页面
        if (result.getResult() != null && result.getResult()) {
            this.getResponse().sendRedirect(loginForm.getRedirectURI());
            return;
        } else {
            //
            Message errorMsg = ErrorCodes.LOGIN_OAUTH_ACCESS_FAILED.getMsg();
            logger.error(LogUtils.create("ERROR_000_0006").addMessage(errorMsg)//
                    .addString("login_error : access failed.").toJson());
            sendJsonError(errorMsg);
            return;
        }
    }
}