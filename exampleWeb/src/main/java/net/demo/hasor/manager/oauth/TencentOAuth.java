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
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.utils.QQConnectConfig;
import com.qq.connect.utils.http.HttpClient;
import com.qq.connect.utils.http.Response;
import net.demo.hasor.core.Service;
import net.demo.hasor.domain.AccessInfo;
import net.demo.hasor.domain.access.TencentAccessInfo;
import net.demo.hasor.domain.enums.ErrorCodes;
import net.demo.hasor.utils.JsonUtils;
import net.demo.hasor.utils.LogUtils;
import net.hasor.core.ApiBinder;
import net.hasor.core.InjectSettings;
import net.hasor.core.Settings;
import net.hasor.core.Singleton;
import org.more.bizcommon.ResultDO;
import org.more.util.ExceptionUtils;
import org.more.util.StringUtils;

import java.net.URLEncoder;
import java.util.Map;
/**
 * 封装腾讯登陆
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@Service("tencent")
public class TencentOAuth extends AbstractOAuth {
    public static final String PROVIDER_NAME = "Tencent";
    public static final String URL_DATA      = "provider=" + PROVIDER_NAME + "&type=website";
    //
    //QQ登录接入,授权key
    @InjectSettings("tencent.qqAdmins")
    private             String adminsCode    = null;
    //应用ID
    @InjectSettings("tencent.app_id")
    private             String appID         = null;
    //应用Key
    @InjectSettings("tencent.app_key")
    private             String appKey        = null;
    //权限
    @InjectSettings("tencent.oauth_scope")
    private             String scope         = null;
    //
    //
    public String getAdmins() {
        return this.adminsCode;
    }
    public String getAppID() {
        return appID;
    }
    public String getAppKey() {
        return appKey;
    }
    //
    public static void configTencent(ApiBinder apiBinder) {
        Settings settings = apiBinder.getEnvironment().getSettings();
        String tencentAppID = settings.getString("tencent.app_id", "");
        QQConnectConfig.updateProperties("app_ID", tencentAppID);
        String tencentAppKey = settings.getString("tencent.app_key", "");
        QQConnectConfig.updateProperties("app_KEY", tencentAppKey);
        String redirectURI = settings.getString("appExample.redirectURI", "127.0.0.1");
        String tencentRedirectURI = redirectURI + "?" + TencentOAuth.URL_DATA;
        QQConnectConfig.updateProperties("redirect_URI", tencentRedirectURI);
        String oauth_scope = settings.getString("tencent.oauth_scope", "");
        QQConnectConfig.updateProperties("scope", oauth_scope);
        //
        apiBinder.bindType(AbstractOAuth.class, TencentOAuth.class);
    }
    //
    /**首次登录的跳转地址(参数为回跳地址)*/
    public String evalLoginURL(String redirectTo) {
        //https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=[YOUR_APPID]&redirect_uri=[YOUR_REDIRECT_URI]&scope=[THE_SCOPE]
        try {
            String redirectURI = this.getRedirectURI() + "?" + TencentOAuth.URL_DATA + "&redirectURI=" + redirectTo;
            return "https://graph.qq.com/oauth2.0/authorize?response_type=code" //
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
        //https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=[YOUR_APP_ID]&client_secret=[YOUR_APP_Key]&code=[The_AUTHORIZATION_CODE]&state=[The_CLIENT_STATE]&redirect_uri=[YOUR_REDIRECT_URI]
        String tokenURL = null;
        try {
            tokenURL = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code" //
                    + "&client_id=" + this.appID //
                    + "&client_secret=" + this.appKey//
                    + "&code=" + authCode//
                    + "&state=" + (status == null ? "" : status) //
                    + "&redirect_uri=" + URLEncoder.encode(this.getRedirectURI() + "?" + TencentOAuth.URL_DATA, "utf-8");
        } catch (Exception e) {
            logger.error(LogUtils.create("ERROR_999_0002").logException(e).toJson(), e);
            throw ExceptionUtils.toRuntimeException(e);
        }
        //
        Response response = null;
        try {
            logger.error("tencent_access_token :authCode = {} , build token URL -> {}.", authCode, tokenURL);
            response = new HttpClient().get(tokenURL);
            String data = response.getResponseAsString();
            if (StringUtils.isBlank(data)) {
                //结果为空
                logger.error(LogUtils.create("ERROR_000_1105")//
                        .addLog("authCode", authCode)//
                        .addString("tencent_access_token : response is empty.").toJson());
                return new ResultDO<AccessInfo>(false).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_TOKEN_RESULT_EMPTY.getMsg());
            }
            if (data.startsWith("callback(")) {
                //返回结果失败
                String jsonData = data.substring(9, data.length() - 3);//callback( {"error":100020,"error_description":"code is reused error"} );
                Map<String, Object> errorInfo = JsonUtils.toMap(jsonData);
                String errorCoe = errorInfo.get("error").toString();
                String errorDesc = errorInfo.get("error_description").toString();
                //
                logger.error(LogUtils.create("ERROR_000_1106")//
                        .addLog("authCode", authCode)//
                        .addLog("errorCoe", errorCoe)//
                        .addLog("errorDesc", errorDesc)//
                        .addString("tencent_access_token : response failed.").toJson());
                return new ResultDO<AccessInfo>(false).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_TOKEN_ERROR.getMsg(errorCoe, errorDesc));
            }
        } catch (Exception e) {
            //
            logger.error(LogUtils.create("ERROR_999_0002")//
                    .logException(e)//
                    .addLog("authCode", authCode)//
                    .addString("tencent_access_token : remote error.").toJson(), e);
            return new ResultDO<AccessInfo>(e).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_ERROR.getMsg("OAuth 远程认证失败。"));
        }
        //
        try {
            AccessToken token = new AccessToken(response);
            OpenID openIDObj = new OpenID(token.getAccessToken());
            String openID = openIDObj.getUserOpenID();
            UserInfo userInfo = new UserInfo(token.getAccessToken(), openID);
            UserInfoBean infoBean = userInfo.getUserInfo();
            //
            TencentAccessInfo info = new TencentAccessInfo();
            info.setAccessToken(token.getAccessToken());
            info.setExpiresTime(token.getExpireIn());
            info.setOpenID(openID);
            info.setOriInfo(response.getResponseAsString());
            //
            info.setGender(infoBean.getGender());
            info.setNickName(infoBean.getNickname());
            info.setLevel(infoBean.getLevel());
            info.setVip(infoBean.isVip());
            info.setYellowYearVip(infoBean.isYellowYearVip());
            //
            info.setAvatarURL30(infoBean.getAvatar().getAvatarURL30());
            info.setAvatarURL50(infoBean.getAvatar().getAvatarURL50());
            info.setAvatarURL100(infoBean.getAvatar().getAvatarURL100());
            //
            logger.error("tencent_access_token : success -> token : {} , sourceID : {} , nick : {}.", //
                    info.getAccessToken(), info.getSource(), info.getNickName());
            return new ResultDO<AccessInfo>(true).setResult(info);
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
}