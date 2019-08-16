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
package net.hasor.registry.server;
import com.alibaba.fastjson.JSON;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.common.RegistryConstants;
import net.hasor.registry.server.domain.*;
import net.hasor.registry.server.manager.AuthQuery;
import net.hasor.registry.server.utils.CenterUtils;
import net.hasor.rsf.*;
import net.hasor.rsf.domain.ProtocolStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检验来自Client的请求是否准许访问Cenrer。
 * @version : 2016年2月18日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class RsfCenterServerVerifyFilter implements RsfFilter {
    protected Logger    logger = LoggerFactory.getLogger(getClass());
    @Inject
    private   AuthQuery authQuery;

    //
    private Result<Boolean> checkAuth(AuthBean authInfo, InterAddress remoteAddress) {
        LogUtils logUtils = LogUtils.create("INFO_200_00002")//
                .addLog("appCode", authInfo.getAppKey())//
                .addLog("authCode", authInfo.getAppKeySecret())//
                .addLog("remoteAddress", remoteAddress.toHostSchema());
        //
        Result<Boolean> checkResult = authQuery.checkKeySecret(authInfo);
        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
            logger.error(logUtils.addLog("result", "failed.").toJson());
            return DateCenterUtils.buildFailedResult(checkResult);
        }
        //
        logger.info(logUtils.addLog("result", checkResult.getResult()).toJson());
        return CenterUtils.resultOK(checkResult.getResult());
    }

    //
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (!request.isLocal()) {
            // .校验应用接入Key
            String appKey = request.getOption(RegistryConstants.Center_RSF_APP_KEY);              //appKey 授权码
            String appKeySecret = request.getOption(RegistryConstants.Center_RSF_APP_KEY_SECRET); //appKeySecret  应用程序编码
            AuthBean authInfo = new AuthBean();
            authInfo.setAppKey(appKey);
            authInfo.setAppKeySecret(appKeySecret);
            request.setAttribute(RsfCenterConstants.Center_Request_AuthInfo, authInfo);
            Result<Boolean> authResult = checkAuth(authInfo, request.getRemoteAddress());
            // .error
            if (authResult == null || !authResult.isSuccess()) {
                String errorMessage = "";
                if (authResult == null || authResult.getResult() == null) {
                    errorMessage = JSON.toJSONString(ErrorCode.EmptyResult);
                } else {
                    errorMessage = JSON.toJSONString(authResult.getErrorInfo());
                }
                logger.error(LogUtils.create("ERROR_300_00001")//
                        .addLog("rsfAddress", request.getRemoteAddress().toHostSchema())//
                        .addLog("errorMessage", errorMessage)//
                        .addLog("appKey", appKey)//
                        .addLog("appKeySecret", appKeySecret)//
                        .toJson());
                response.sendStatus(ProtocolStatus.Unauthorized, errorMessage);
                return;
            }
            // .failed
            if (!authResult.getResult()) {
                response.sendStatus(ProtocolStatus.Unauthorized, "unauthorized.");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}