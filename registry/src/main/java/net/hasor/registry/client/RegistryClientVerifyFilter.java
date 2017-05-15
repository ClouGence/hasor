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
package net.hasor.registry.client;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.RegistryConstants;
import net.hasor.registry.RsfCenterSettings;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 对Center请求的服务接口都加上AppCode和AuthCode隐式参数。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class RegistryClientVerifyFilter implements RsfFilter {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private RsfCenterSettings centerSettings;
    private String appKey    = null;    //key
    private String keySecret = null;    //keySecret
    //
    //
    @Init
    public void init() {
        this.appKey = this.centerSettings.getAppKeyID();
        this.keySecret = this.centerSettings.getAppKeySecret();
    }
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (request.isLocal()) {
            //-如果是对外发送请求，则添加请求头参数用于注册中心校验
            request.addOption(RegistryConstants.Center_RSF_APP_KEY, this.appKey);
            request.addOption(RegistryConstants.Center_RSF_APP_KEY_SECRET, this.keySecret);
        }
        chain.doFilter(request, response);
    }
}