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
package net.demo.hasor.manager.oauth;
import com.qq.connect.utils.http.HttpClientUtil;
import com.qq.connect.utils.http.Response;
import net.demo.hasor.core.Service;
import net.demo.hasor.domain.UserDO;
import net.demo.hasor.domain.UserSourceDO;
import net.demo.hasor.domain.enums.ErrorCodes;
import net.demo.hasor.domain.enums.GenderType;
import net.demo.hasor.domain.enums.UserStatus;
import net.demo.hasor.domain.enums.UserType;
import net.demo.hasor.domain.futures.UserContactInfo;
import net.demo.hasor.domain.futures.UserFutures;
import net.demo.hasor.domain.oauth.AccessInfo;
import net.demo.hasor.domain.oauth.GithubAccessInfo;
import net.demo.hasor.utils.JsonUtils;
import net.demo.hasor.utils.LogUtils;
import net.demo.hasor.utils.OAuthUtils;
import net.hasor.core.ApiBinder;
import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;
import net.hasor.core.Singleton;
import org.more.bizcommon.ResultDO;
import org.more.util.ExceptionUtils;
import org.more.util.StringUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * 封装Github登陆
 * https://status.github.com/
 * https://developer.github.com/v3/oauth/
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@Service("github")
public class GithubOAuth extends AbstractOAuth {
    public static final String         PROVIDER_NAME = "Github";
    public static final String         URL_DATA      = "provider=" + PROVIDER_NAME + "&type=website";
    //
    @Inject
    private             HttpClientUtil httpClient    = null;
    //应用ID
    @InjectSettings("github.app_id")
    private             String         appID         = null;
    //应用Key
    @InjectSettings("github.app_key")
    private             String         appKey        = null;
    @InjectSettings("github.oauth_scope")
    private             String         scope         = null;
    //
    //
    public String getAppID() {
        return appID;
    }
    public String getAppKey() {
        return appKey;
    }
    //
    //
    public GithubOAuth() {
        super();
    }
    public GithubOAuth(ApiBinder apiBinder) {
        super(apiBinder);
    }
    //
    //
    @Override
    public void configOAuth(ApiBinder apiBinder) {
    }
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    //
    /**首次登录的跳转地址(参数为回跳地址)*/
    public String evalLoginURL(String redirectTo) {
        try {
            String redirectURI = this.getRedirectURI() + "?" + GithubOAuth.URL_DATA + "&redirectURI=" + redirectTo;
            return "https://github.com/login/oauth/authorize?response_type=code" //
                    + "&client_id=" + this.appID //
                    + "&redirect_uri=" + URLEncoder.encode(redirectURI, "utf-8") //
                    + "&scope=" + this.scope;//
        } catch (Exception e) {
            logger.error(LogUtils.create("ERROR_999_0002").logException(e).toJson(), e);
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    /**拿到远程Code之后通过code获取 AccessInfo 认证信息对象。*/
    public ResultDO<AccessInfo> evalToken(String status, String authCode) {
        String tokenURL = null;
        try {
            tokenURL = "https://github.com/login/oauth/access_token?1=1" //
                    + "&client_id=" + this.appID //
                    + "&client_secret=" + this.appKey//
                    + "&code=" + authCode//
                    + "&state=" + (status == null ? "" : status) //
                    + "&redirect_uri=" + URLEncoder.encode(this.getRedirectURI() + "?" + GithubOAuth.URL_DATA, "utf-8");
        } catch (Exception e) {
            logger.error(LogUtils.create("ERROR_999_0002").logException(e).toJson(), e);
            throw ExceptionUtils.toRuntimeException(e);
        }
        //
        Map<String, String> dataMaps = new HashMap<String, String>();
        try {
            logger.error("github_access_token :authCode = {} , build token URL -> {}.", authCode, tokenURL);
            Response response = this.httpClient.get(tokenURL);
            String data = response.getResponseAsString();
            if (StringUtils.isBlank(data)) {
                //结果为空
                logger.error(LogUtils.create("ERROR_000_1105")//
                        .addLog("authCode", authCode)//
                        .addString("github_access_token : response is empty.").toJson());
                return new ResultDO<AccessInfo>(false).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_TOKEN_RESULT_EMPTY.getMsg());
            }
            //
            String[] dataItems = data.split("&");
            for (String dataItem : dataItems) {
                //
                String[] arrs = dataItem.split("=");
                String keyStr = URLDecoder.decode(arrs[0], "utf-8");
                String varStr = URLDecoder.decode(arrs[1], "utf-8");
                dataMaps.put(keyStr, varStr);
            }
        } catch (Exception e) {
            //
            logger.error(LogUtils.create("ERROR_999_0002")//
                    .logException(e)//
                    .addLog("authCode", authCode)//
                    .addString("github_access_token : remote error.").toJson(), e);
            return new ResultDO<AccessInfo>(e).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_ERROR.getMsg("OAuth 远程认证失败。"));
        }
        //
        if (dataMaps.containsKey("error")) {
            //        0 = "error=bad_verification_code"
            //        1 = "error_description=The+code+passed+is+incorrect+or+expired."
            //        2 = "error_uri=https%3A%2F%2Fdeveloper.github.com%2Fv3%2Foauth%2F%23bad-verification-code"
            //返回结果失败
            String errorCoe = dataMaps.get("error").toString();
            String errorDesc = dataMaps.get("error_description").toString();
            String errorRUL = dataMaps.get("error_uri").toString();
            //
            logger.error(LogUtils.create("ERROR_000_1106")//
                    .addLog("authCode", authCode)//
                    .addLog("errorCoe", errorCoe)//
                    .addLog("errorDesc", errorDesc)//
                    .addString("tencent_access_token : response failed.").toJson());
            return new ResultDO<AccessInfo>(false).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_TOKEN_ERROR.getMsg(errorCoe, errorDesc));
        }
        //
        try {
            String access_token = (String) dataMaps.get("access_token");
            Response response = this.httpClient.get("https://api.github.com/user?access_token=" + access_token);
            String data = response.getResponseAsString();
            GithubAccessInfo accessInfo = JsonUtils.toObject(data, GithubAccessInfo.class);
            accessInfo.setAccessToken(access_token);
            //
            logger.error("tencent_access_token : success -> token : {} , sourceID : {} , nick : {}.", //
                    accessInfo.getAccessToken(), accessInfo.getSource(), accessInfo.getName());
            return new ResultDO<AccessInfo>(true).setResult(accessInfo);
        } catch (Exception e) {
            //
            logger.error(LogUtils.create("ERROR_999_0002")//
                    .logException(e)//
                    .addLog("authCode", authCode)//
                    .addString("tencent_access_token : get data failed.").toJson(), e);
            return new ResultDO<AccessInfo>(e).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_ERROR.getMsg("OAuth 获取数据失败。"));
        }
        //
    }
    @Override
    public UserDO convertTo(AccessInfo result) {
        GithubAccessInfo accessInfo = (GithubAccessInfo) result;
        UserDO userDO = new UserDO();
        userDO.setPassword("-");
        userDO.setNick(accessInfo.getName());
        userDO.setAvatar(accessInfo.getAvatar_url());
        if (StringUtils.isBlank(userDO.getNick())) {
            if (StringUtils.isBlank(accessInfo.getLogin())) {
                userDO.setNick(TencentOAuth.PROVIDER_NAME + "_" + System.currentTimeMillis());
            } else {
                userDO.setNick(accessInfo.getLogin());
            }
        }
        //
        if (userDO.getUserSourceList() == null) {
            userDO.setUserSourceList(new ArrayList<UserSourceDO>());
        }
        userDO.getUserSourceList().add(OAuthUtils.convertAccessInfo(result));
        userDO.setGender(GenderType.None);
        userDO.setStatus(UserStatus.Normal);
        userDO.setType(UserType.Temporary);
        userDO.setEmail(accessInfo.getEmail());
        //
        userDO.setFutures(new UserFutures());
        userDO.getFutures().setName(accessInfo.getName());
        userDO.getFutures().setPresent(accessInfo.getBio());
        //
        userDO.setContactInfo(new UserContactInfo());
        userDO.getContactInfo().setBlogHome(accessInfo.getBlog());
        return userDO;
    }
}