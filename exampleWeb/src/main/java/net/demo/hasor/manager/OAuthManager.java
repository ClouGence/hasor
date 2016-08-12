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
package net.demo.hasor.manager;
import net.demo.hasor.domain.oauth.AccessInfo;
import net.demo.hasor.domain.UserDO;
import net.demo.hasor.domain.enums.ErrorCodes;
import net.demo.hasor.manager.oauth.AbstractOAuth;
import net.demo.hasor.utils.LogUtils;
import net.demo.hasor.utils.OAuthUtils;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import org.more.bizcommon.Message;
import org.more.bizcommon.Result;
import org.more.bizcommon.ResultDO;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 集成第三方登陆 & CAS 等
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class OAuthManager implements AppContextAware {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private UserManager                userManager;
    private Map<String, AbstractOAuth> oauthMap;
    //
    @Override
    public void setAppContext(AppContext appContext) {
        //初始化所有注册的Oauth
        this.oauthMap = new HashMap<String, AbstractOAuth>();
        List<AbstractOAuth> oauthList = appContext.findBindingBean(AbstractOAuth.class);
        if (oauthList != null) {
            for (AbstractOAuth oauth : oauthList) {
                if (oauth != null) {
                    String provider = oauth.getProviderName();
                    logger.error("oauth init provider {} -> {}", provider, oauth);
                    this.oauthMap.put(provider.toUpperCase(), oauth);
                }
            }
        }
    }
    //
    protected AbstractOAuth getOAuthByProvider(String provider) {
        if (StringUtils.isBlank(provider)) {
            return null;
        }
        return oauthMap.get(provider.toUpperCase());
    }
    //
    public Result<Long> processAccess(String authCode, String provider, String status) {
        AbstractOAuth oauth = this.getOAuthByProvider(provider);
        if (oauth == null) {
            logger.error(LogUtils.create("ERROR_999_0001")//
                    .addString("oauth_" + provider + " : provider is not support.").toJson());
            return new ResultDO<Long>(false).setResult(0L).addMessage(ErrorCodes.RESULT_NULL.getMsg());
        }
        //
        ResultDO<AccessInfo> info = oauth.evalToken(status, authCode);
        if (info == null) {
            logger.error(LogUtils.create("ERROR_999_0001")//
                    .addString("oauth_" + provider + " : evalToken result is null.").toJson());
            return new ResultDO<Long>(false).setResult(0L).addMessage(ErrorCodes.RESULT_NULL.getMsg());
        }
        //
        if (!info.isSuccess()) {
            Message errorMsg = info.firstMessage();
            logger.error(LogUtils.create("ERROR_000_1004")//
                    .addString("oauth_" + provider + " : evalToken failed.")//
                    .addLog("authCode", authCode)//
                    .addLog("provider", provider).toJson());
            return new ResultDO<Long>(false).setResult(0L).addMessage(errorMsg);
        }
        //
        if (info.getResult() == null) {
            logger.error(LogUtils.create("ERROR_999_0001")//
                    .addString("oauth_" + provider + " : login success , but result is null.").toJson());
            return new ResultDO<Long>(false).setResult(0L).addMessage(ErrorCodes.RESULT_NULL.getMsg());
        }
        //
        try {
            String uniqueID = info.getResult().getExternalUserID();
            UserDO userDO = this.userManager.getUserByProvider(provider, uniqueID);
            long dataResult = 0L;
            if (userDO == null) {
                userDO = convertTo(info.getResult());
                if (userDO == null) {
                    logger.error(LogUtils.create("ERROR_999_0001")//
                            .addString("oauth_" + provider + " : 'AccessInfo' convertTo 'UserDO' return null.").toJson());
                    return new ResultDO<Long>(false).setResult(0L).addMessage(ErrorCodes.RESULT_NULL.getMsg());
                }
                dataResult = this.userManager.newUser(userDO);//插入新用户
            } else {
                dataResult = this.userManager.updateAccessInfo(userDO, provider, OAuthUtils.convertAccessInfo(info.getResult()));
            }
            //
            if (dataResult > 0) {
                this.userManager.loginUpdate(userDO, provider);//更新登录信息(忽略返回值)
                logger.error("oauth_" + provider + " : login success , userID = {}.", dataResult);
                return new ResultDO<Long>(true).setResult(dataResult);
            } else {
                logger.error(LogUtils.create("ERROR_999_0001")//
                        .addLog("result", dataResult) //
                        .addString("oauth_" + provider + " : login success , but user save to db failed.").toJson());
                return new ResultDO<Long>(false).setResult(dataResult).addMessage(ErrorCodes.LOGIN_USER_SAVE.getMsg());
            }
        } catch (Exception e) {
            //
            logger.error(LogUtils.create("ERROR_999_0002")//
                    .logException(e)//
                    .addLog("authCode", authCode)//
                    .addString("tencent_access_token : save or updata userinfo failed.").toJson(), e);
            return new ResultDO<Long>(e).addMessage(ErrorCodes.LOGIN_USER_SAVE.getMsg("保存或者更新数据错误。"));
        }
    }
    //
    private UserDO convertTo(AccessInfo result) {
        return this.getOAuthByProvider(result.getProvider()).convertTo(result);
    }
}