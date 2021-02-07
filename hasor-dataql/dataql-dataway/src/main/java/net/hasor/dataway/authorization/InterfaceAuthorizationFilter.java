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
package net.hasor.dataway.authorization;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataway.DatawayApi;
import net.hasor.dataway.DatawayService;
import net.hasor.dataway.service.CrossDomainService;
import net.hasor.dataway.spi.AuthorizationChainSpi;
import net.hasor.utils.StringUtils;
import net.hasor.web.*;

import java.lang.reflect.Method;

/**
 * 负责UI界面调用的权限判断。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
public class InterfaceAuthorizationFilter implements InvokerFilter {
    private SpiTrigger         spiTrigger         = null;
    private DatawayService     datawayService     = null;
    private String             uiAdminBaseUri     = null;
    private CrossDomainService crossDomainService = null;

    public InterfaceAuthorizationFilter(String uiBaseUri) {
        this.uiAdminBaseUri = (uiBaseUri + "/api/").replaceAll("/+", "/");
    }

    /**
     * 初始化过滤器
     * @param config 配置信息
     */
    public void init(InvokerConfig config) {
        this.spiTrigger = config.getAppContext().getInstance(SpiTrigger.class);
        this.datawayService = config.getAppContext().getInstance(DatawayService.class);
        this.crossDomainService = config.getAppContext().getInstance(CrossDomainService.class);
    }

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        if (!invoker.getRequestPath().startsWith(uiAdminBaseUri)) {
            return chain.doNext(invoker);
        }
        Mapping mapping = invoker.ownerMapping();
        if (mapping == null) {
            return chain.doNext(invoker);
        }
        Method mappingMethod = mapping.findMethod(invoker.getHttpRequest());
        if (mappingMethod == null) {
            return chain.doNext(invoker);
        }
        // .获取调用特征
        RefAuthorization refAuthorization = mappingMethod.getAnnotation(RefAuthorization.class);
        if (refAuthorization == null) {
            refAuthorization = mappingMethod.getDeclaringClass().getAnnotation(RefAuthorization.class);
        }
        AuthorizationType uiAuthorization = (refAuthorization != null) ? refAuthorization.value() : null;
        String apiId = null;
        String queryString = invoker.getHttpRequest().getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            for (String queryItem : queryString.split("&")) {
                if (queryItem.startsWith("id=")) {
                    apiId = queryItem.split("=")[1];
                    break;
                }
            }
        }
        // .执行权限检查SPI
        DatawayApi datawayApi = (StringUtils.isNotBlank(apiId) && !"-1".equalsIgnoreCase(apiId)) ?  //
                this.datawayService.getApiById(apiId) :                                             //
                null;
        Boolean checkResult = spiTrigger.chainSpi(AuthorizationChainSpi.class, (listener, lastResult) -> {
            return listener.doCheck(uiAuthorization, datawayApi, lastResult);
        }, true);
        if (checkResult) {
            return chain.doNext(invoker);
        }
        // .扔一个错误出去
        this.crossDomainService.configureCross(datawayApi, invoker);
        invoker.getHttpResponse().sendError(401, "No permission.");
        return null;
    }
}
