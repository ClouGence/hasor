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
import com.qq.connect.utils.http.Response;
import net.demo.hasor.core.Service;
import net.demo.hasor.domain.UserDO;
import net.demo.hasor.domain.UserSourceDO;
import net.demo.hasor.domain.enums.ErrorCodes;
import net.demo.hasor.domain.enums.GenderType;
import net.demo.hasor.domain.enums.UserStatus;
import net.demo.hasor.domain.enums.UserType;
import net.demo.hasor.domain.futures.ContactAddressInfo;
import net.demo.hasor.domain.futures.UserContactInfo;
import net.demo.hasor.domain.futures.UserFutures;
import net.demo.hasor.domain.oauth.AccessInfo;
import net.demo.hasor.domain.oauth.WeiboAccessInfo;
import net.demo.hasor.utils.JsonUtils;
import net.demo.hasor.utils.LogUtils;
import net.demo.hasor.utils.OAuthUtils;
import net.hasor.core.ApiBinder;
import net.hasor.core.InjectSettings;
import net.hasor.core.Singleton;
import org.more.bizcommon.ResultDO;
import org.more.util.ExceptionUtils;
import org.more.util.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
/**
 * 封装新浪微博登陆
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@Service("weibo")
public class WeiboOAuth extends AbstractOAuth {
    public static final String PROVIDER_NAME = "Weibo";
    public static final String URL_DATA      = "provider=" + PROVIDER_NAME + "&type=website";
    //
    //QQ登录接入,授权key
    @InjectSettings("weibo.admins")
    private             String adminsCode    = null;
    //应用ID
    @InjectSettings("weibo.app_id")
    private             String appID         = null;
    //应用Key
    @InjectSettings("weibo.app_key")
    private             String appKey        = null;
    //权限
    @InjectSettings("weibo.oauth_scope")
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
    //
    public WeiboOAuth() {
        super();
    }
    public WeiboOAuth(ApiBinder apiBinder) {
        super(apiBinder);
    }
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    @Override
    public void configOAuth(ApiBinder apiBinder) {
    }
    //
    /**首次登录的跳转地址(参数为回跳地址)*/
    @Override
    public String evalLoginURL(String redirectTo) {
        try {
            String redirectURI = this.getRedirectURI() + "?" + WeiboOAuth.URL_DATA + "&redirectURI=" + redirectTo;
            return "https://api.weibo.com/oauth2/authorize?response_type=code" //
                    + "&forcelogin=true" //
                    + "&display=default" //
                    + "&client_id=" + this.appID //
                    + "&redirect_uri=" + URLEncoder.encode(redirectURI, "utf-8") //
                    + "&scope=" + this.scope;
        } catch (Exception e) {
            logger.error(LogUtils.create("ERROR_999_0002").logException(e).toJson(), e);
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    /**拿到远程Code之后通过code获取 AccessInfo 认证信息对象。*/
    @Override
    public ResultDO<AccessInfo> evalToken(String status, String authCode) {
        String tokenURL = null;
        try {
            tokenURL = "https://api.weibo.com/oauth2/access_token?grant_type=authorization_code" //
                    + "&client_id=" + this.appID //
                    + "&client_secret=" + this.appKey//
                    + "&code=" + authCode//
                    + "&redirect_uri=" + URLEncoder.encode(this.getRedirectURI() + "?" + WeiboOAuth.URL_DATA, "utf-8");
        } catch (Exception e) {
            logger.error(LogUtils.create("ERROR_999_0002").logException(e).toJson(), e);
            throw ExceptionUtils.toRuntimeException(e);
        }
        //
        Map<String, Object> dataMaps = null;
        try {
            logger.error("weibo_access_token :authCode = {} , build token URL -> {}.", authCode, tokenURL);
            Response response = this.httpClient.httpPost(tokenURL);
            String data = response.getResponseAsString();
            if (StringUtils.isBlank(data)) {
                //结果为空
                logger.error(LogUtils.create("ERROR_000_1105")//
                        .addLog("authCode", authCode)//
                        .addString("weibo_access_token : response is empty.").toJson());
                return new ResultDO<AccessInfo>(false).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_TOKEN_RESULT_EMPTY.getMsg());
            }
            //
            dataMaps = JsonUtils.toMap(response.getResponseAsString());
        } catch (Exception e) {
            //
            logger.error(LogUtils.create("ERROR_999_0002")//
                    .logException(e)//
                    .addLog("authCode", authCode)//
                    .addString("tencent_access_token : remote error.").toJson(), e);
            return new ResultDO<AccessInfo>(e).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_ERROR.getMsg("OAuth 远程认证失败。"));
        }
        //
        if (dataMaps == null || dataMaps.containsKey("error")) {
            //返回结果失败
            String errorKey = dataMaps.get("error").toString();
            String errorCode = dataMaps.get("error_code").toString();
            String errorDesc = dataMaps.get("error_description").toString();
            //
            logger.error(LogUtils.create("ERROR_000_1106")//
                    .addLog("authCode", authCode)//
                    .addLog("errorKey", errorKey)//
                    .addLog("errorCoe", errorCode)//
                    .addLog("errorDesc", errorDesc)//
                    .addString("tencent_access_token : response failed.").toJson());
            return new ResultDO<AccessInfo>(false).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_TOKEN_ERROR.getMsg(errorKey, errorDesc));
        }
        //
        try {
            String accessToken = dataMaps.get("access_token").toString();
            String userID = dataMaps.get("uid").toString();
            Response response = this.httpClient.httpGet("https://api.weibo.com/2/users/show.json"//
                    + "?access_token=" + accessToken //
                    + "&uid=" + userID);//
            String data = response.getResponseAsString();
            if (StringUtils.isBlank(data)) {
                //结果为空
                logger.error(LogUtils.create("ERROR_000_1105")//
                        .addLog("authCode", authCode)//
                        .addString("weibo_access_token : response is empty.").toJson());
                return new ResultDO<AccessInfo>(false).addMessage(ErrorCodes.LOGIN_OAUTH_ACCESS_TOKEN_RESULT_EMPTY.getMsg());
            }
            //
            //
            WeiboAccessInfo info = JsonUtils.toObject(response.getResponseAsString(), WeiboAccessInfo.class);
            info.setAccessToken(dataMaps.get("access_token").toString());
            info.setExpires_in(Long.parseLong(dataMaps.get("expires_in").toString()));
            info.setRemind_in(dataMaps.get("remind_in").toString());
            info.setAccessUserID(dataMaps.get("uid").toString());
            //
            logger.error("tencent_access_token : success -> token : {} , sourceID : {} , nick : {}.", //
                    info.getAccessToken(), info.getSource(), ""/*info.getNickName()*/);
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
    @Override
    public UserDO convertTo(AccessInfo result) {
        WeiboAccessInfo accessInfo = (WeiboAccessInfo) result;
        UserDO userDO = new UserDO();
        userDO.setPassword("-");
        userDO.setNick(accessInfo.getScreen_name());
        userDO.setAvatar(accessInfo.getAvatar_large());
        if (StringUtils.isBlank(userDO.getNick())) {
            userDO.setNick(WeiboOAuth.PROVIDER_NAME + "_" + System.currentTimeMillis());
        }
        //
        userDO.setUserSourceList(new ArrayList<UserSourceDO>());
        userDO.getUserSourceList().add(OAuthUtils.convertAccessInfo(result));
        if (StringUtils.equalsIgnoreCase(accessInfo.getGender(), "m")) {
            userDO.setGender(GenderType.Male);
        } else if (StringUtils.equalsIgnoreCase(accessInfo.getGender(), "f")) {
            userDO.setGender(GenderType.Female);
        } else {
            userDO.setGender(GenderType.None);
        }
        userDO.setStatus(UserStatus.Normal);
        userDO.setType(UserType.Temporary);
        userDO.setEmail("");
        //
        userDO.setFutures(new UserFutures());
        userDO.getFutures().setName(accessInfo.getName());
        userDO.getFutures().setPresent(accessInfo.getDescription());
        //
        userDO.setContactInfo(new UserContactInfo());
        userDO.getContactInfo().setBlogHome(accessInfo.getUrl());
        //
        ContactAddressInfo userAddressInfo = new ContactAddressInfo();
        //
        userAddressInfo.setProvinceCode(String.valueOf(accessInfo.getProvince()));
        userAddressInfo.setCityCode(String.valueOf(accessInfo.getCity()));
        userAddressInfo.setLocation(accessInfo.getLocation());
        userDO.getContactInfo().setUserAddress(userAddressInfo);
        return userDO;
    }
    //
}