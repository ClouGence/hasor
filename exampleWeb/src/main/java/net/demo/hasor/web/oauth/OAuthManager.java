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
import net.demo.hasor.domain.AccessInfo;
import net.demo.hasor.domain.UserDO;
import net.demo.hasor.domain.UserSourceDO;
import net.demo.hasor.domain.access.TencentAccessInfo;
import net.demo.hasor.domain.enums.ErrorCodes;
import net.demo.hasor.domain.enums.UserStatus;
import net.demo.hasor.domain.enums.UserType;
import net.demo.hasor.manager.UserManager;
import net.demo.hasor.utils.LogUtils;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import org.more.bizcommon.Message;
import org.more.bizcommon.Result;
import org.more.bizcommon.ResultDO;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
/**
 * 集成第三方登陆 & CAS 等
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class OAuthManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private TencentOAuth tencentOAuth;
    @Inject
    private UserManager  userManager;
    //
    public Result<Long> processAccess(String authCode, String provider, String status) {
        ResultDO<AccessInfo> info = this.tencentOAuth.evalToken(status, authCode);
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
                dataResult = this.userManager.updateAccessInfo(userDO, provider, convertAccessInfo(info.getResult()));
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
    private UserSourceDO convertAccessInfo(AccessInfo accessInfo) {
        UserSourceDO sourceDO = new UserSourceDO();
        sourceDO.setProvider(accessInfo.getProvider());
        sourceDO.setUniqueID(accessInfo.getExternalUserID());
        sourceDO.setAccessInfo(accessInfo);
        sourceDO.setStatus(true);
        return sourceDO;
    }
    //
    private UserDO convertTo(AccessInfo result) {
        if (StringUtils.equalsIgnoreCase(result.getProvider(), TencentOAuth.PROVIDER_NAME)) {
            TencentAccessInfo accessInfo = (TencentAccessInfo) result;
            UserDO userDO = new UserDO();
            userDO.setPassword("-");
            userDO.setNick(accessInfo.getNickName());
            userDO.setAvatar(accessInfo.getAvatarURL100());
            if (StringUtils.isBlank(userDO.getNick())) {
                userDO.setNick(TencentOAuth.PROVIDER_NAME + "_" + System.currentTimeMillis());
            }
            //
            if (userDO.getUserSourceList() == null) {
                userDO.setUserSourceList(new ArrayList<UserSourceDO>());
            }
            userDO.getUserSourceList().add(convertAccessInfo(result));
            userDO.setStatus(UserStatus.Normal);
            userDO.setType(UserType.Temporary);
            userDO.setCreateTime(new Date());
            return userDO;
        } else {
            return null;
        }
    }
}