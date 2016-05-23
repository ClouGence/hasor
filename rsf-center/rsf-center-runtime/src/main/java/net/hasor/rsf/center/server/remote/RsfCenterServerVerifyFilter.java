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
package net.hasor.rsf.center.server.remote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Singleton;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.center.domain.RsfCenterConstants;
import net.hasor.rsf.center.server.manager.AuthManager;
import net.hasor.rsf.domain.ProtocolStatus;
/**
 * 检验来自Client的请求是否准许访问Cenrer。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class RsfCenterServerVerifyFilter implements RsfFilter {
    protected Logger    logger = LoggerFactory.getLogger(getClass());
    private AuthManager authManager;
    //
    public RsfCenterServerVerifyFilter(RsfContext rsfContext) {
        this.authManager = rsfContext.getAppContext().getInstance(AuthManager.class);
    }
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (request.isLocal() == false) {
            //-如果是来自远程的请求响应，则校验注册中心需要校验应用接入Key
            String appCode = request.getOption(RsfCenterConstants.RSF_AUTH_CODE); //RSF_AUTH_CODE 授权码
            String authCode = request.getOption(RsfCenterConstants.RSF_APP_CODE); //RSF_APP_CODE  应用程序编码
            boolean authResult = this.authManager.checkAuth(appCode, authCode);
            if (authResult == false) {
                response.sendStatus(ProtocolStatus.Unauthorized, "check auth code failed.");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}