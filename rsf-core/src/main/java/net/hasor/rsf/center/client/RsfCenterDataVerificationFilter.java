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
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.center.domain.RSFCenterConstants;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
/**
 * 注册中心数据接收器安全过滤器，负责验证注册中心的消息是否可靠。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCenterDataVerificationFilter implements RsfFilter {
    protected Logger logger     = LoggerFactory.getLogger(getClass());
    private String   authCode   = null;                               //RSF_APP_CODE  应用程序编码
    private String   appCode    = null;                               //RSF_AUTH_CODE 授权码
    private String   rsfVersion = null;                               //              客户端版本
    //
    public RsfCenterDataVerificationFilter(RsfContext rsfContext) {
        this.authCode = rsfContext.getAppContext().getEnvironment().envVar("RSF_APP_CODE");
        this.appCode = rsfContext.getAppContext().getEnvironment().envVar("RSF_AUTH_CODE");
        this.rsfVersion = rsfContext.getSettings().getVersion();
    }
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (request.isLocal()) {
            //-如果是对外发送请求，则添加请求头参数用于注册中心校验
            request.addOption(RSFCenterConstants.RSF_APP_CODE, this.appCode);
            request.addOption(RSFCenterConstants.RSF_AUTH_CODE, this.authCode);
            request.addOption(RSFCenterConstants.RSF_VERSION, this.rsfVersion);
        } else {
            //-如果是来自远程的响应，则校验来自注册中心的响应中授权码是否是本地预先保存的
            String appCode = response.getOption(RSFCenterConstants.RSF_AUTH_CODE); //RSF_AUTH_CODE 授权码
            String authCode = response.getOption(RSFCenterConstants.RSF_APP_CODE); //RSF_APP_CODE  应用程序编码
            if (StringUtils.equals(this.appCode, appCode) == false || StringUtils.equals(this.authCode, authCode) == false) {
                String remoteData = "appCode=" + appCode + " ,authCode=" + authCode;
                String errorMessage = "rsfCenter auth code failed. -> RSF_APP_CODE or RSF_AUTH_CODE error , remoteData :{ " + remoteData + " }";
                logger.error(errorMessage);
                throw new RsfException(ProtocolStatus.Unauthorized, errorMessage);
            }
        }
        chain.doFilter(request, response);
    }
}