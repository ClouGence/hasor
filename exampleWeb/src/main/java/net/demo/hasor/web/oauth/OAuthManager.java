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
package net.demo.hasor.web.oauth;
import com.qq.connect.utils.http.HttpClient;
import com.qq.connect.utils.http.Response;
import net.demo.hasor.domain.ErrorCodes;
import net.demo.hasor.utils.JsonUtils;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import org.more.bizcommon.Result;
import org.more.bizcommon.ResultDO;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
/**
 * 集成第三方登陆 & CAS 等
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class OAuthManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private TencentOAuthConfig tencentOAuthConfig;
    //
    public AbstractOAuthConfig getAuthConfig(String provider) {
        if (StringUtils.equalsIgnoreCase("qq", provider)) {
            return this.tencentOAuthConfig;
        } else {
            return null;
        }
    }
    //
    public Result<Boolean> processAccess(String authCode, String provider) {
        String data = "";
        try {
            AbstractOAuthConfig config = getAuthConfig(provider);
            String authURL = config.getTokenURL("1234", authCode);
            logger.error("login_access :{ authCode = {} , provider = {}} , build token URL -> {}.", authCode, provider, authURL);
            Response response = new HttpClient().get(authURL);
            data = response.getResponseAsString();
            logger.error("login_access :{ authCode = {} , provider = {}} , response data -> {}.", authCode, provider, data);
        } catch (Exception e) {
            //
            logger.error("login_access(failed) :{ authCode = {} , provider = {}} , error -> {}.", authCode, provider, e.getMessage(), e);
            return new ResultDO<Boolean>(false).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_ERROR.getMsg("OAuth 远程认证失败。"));
        }
        //
        if (StringUtils.isBlank(data)) {
            //
            //结果为空
            logger.error("login_access :{ authCode = {} , provider = {}} , response is empty.", authCode, provider);
            return new ResultDO<Boolean>(false).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_TOKEN_RESULT_EMPTY.getMsg());
        }
        if (data.startsWith("callback(")) {
            //
            //返回结果失败
            String jsonData = data.substring(9, data.length() - 3);//callback( {"error":100020,"error_description":"code is reused error"} );
            Map<String, String> errorInfo = JsonUtils.toObject(jsonData, Map.class);
            String errorCoe = errorInfo.get("error").toString();
            String errorDesc = errorInfo.get("error_description").toString();
            return new ResultDO<Boolean>(false).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_TOKEN_ERROR.getMsg(errorCoe, errorDesc));
        } else {
            //
            //成功
            //access_token=CA79780C16DC7EFC5B43277B6DE9B30D&expires_in=7776000&refresh_token=10DFC680DD904E8B15BE066094DD338B
        }
        return new ResultDO<Boolean>(true).setResult(true);
    }
    //    protected Result<String> getTencentAuthURL(String provider, String status, String authCode) {
    //        AbstractOAuthConfig config = getAuthConfig(provider);
    //        if (config == null) {
    //            return new ResultDO<String>().setSuccess(false).addMessage();//"(不支持)";
    //        }
    //        try {
    //            String configAuthURL = config.getTokenURL(status, authCode);
    //        } catch (Exception e) {
    //            return new ResultDO<String>().setSuccess(false).setThrowable(e).addMessage();//"(跳转到错误页)"
    //        }
    //    }
    //
}