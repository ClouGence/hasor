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
package net.hasor.rsf.center.client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.center.domain.RsfCenterConstants;
/**
 * 对Center请求的服务接口都加上AppCode和AuthCode隐式参数。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfCenterClientVerifyFilter implements RsfFilter {
    protected Logger logger     = LoggerFactory.getLogger(getClass());
    private String   appCode    = null;                               //RSF_AUTH_CODE 授权码
    private String   authCode   = null;                               //RSF_APP_CODE  应用程序编码
    private String   rsfVersion = null;                               //客户端版本
    //
    public RsfCenterClientVerifyFilter(RsfContext rsfContext) throws Throwable {
        this.appCode = rsfContext.getAppContext().getEnvironment().envVar("RSF_APP_CODE");
        this.authCode = rsfContext.getAppContext().getEnvironment().envVar("RSF_AUTH_CODE");
        this.rsfVersion = rsfContext.getSettings().getVersion();
    }
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (request.isLocal()) {
            //-如果是对外发送请求，则添加请求头参数用于注册中心校验
            request.addOption(RsfCenterConstants.RSF_APP_CODE, this.appCode);
            request.addOption(RsfCenterConstants.RSF_AUTH_CODE, this.authCode);
            request.addOption(RsfCenterConstants.RSF_VERSION, this.rsfVersion);
        }
        chain.doFilter(request, response);
    }
}