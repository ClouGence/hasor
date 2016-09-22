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
package net.hasor.rsf.center.server.register;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.center.server.domain.AuthInfo;
import net.hasor.rsf.center.server.domain.ErrorCode;
import net.hasor.rsf.center.server.domain.Result;
import net.hasor.rsf.center.server.domain.RsfCenterConstants;
import net.hasor.rsf.center.server.manager.AuthManager;
import net.hasor.rsf.center.server.utils.JsonUtils;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 检验来自Client的请求是否准许访问Cenrer。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class RsfCenterServerVerifyFilter implements RsfFilter {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private AuthManager authManager;
    //
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (!request.isLocal()) {
            // .校验应用接入Key
            String appKey = request.getOption(RsfConstants.Center_RSF_APP_KEY);              //appKey 授权码
            String appKeySecret = request.getOption(RsfConstants.Center_RSF_APP_KEY_SECRET); //appKeySecret  应用程序编码
            AuthInfo authInfo = new AuthInfo();
            authInfo.setAppKey(appKey);
            authInfo.setAppKeySecret(appKeySecret);
            request.setAttribute(RsfCenterConstants.Center_Request_AuthInfo, authInfo);
            Result<Boolean> authResult = this.authManager.checkAuth(authInfo, request.getBindInfo(), request.getMethod());
            // .error
            if (authResult == null || !authResult.isSuccess()) {
                String errorMessage = "";
                if (authResult == null || authResult.getResult() == null) {
                    errorMessage = JsonUtils.converToString(ErrorCode.EmptyResult);
                } else {
                    errorMessage = JsonUtils.converToString(authResult.getErrorInfo());
                }
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