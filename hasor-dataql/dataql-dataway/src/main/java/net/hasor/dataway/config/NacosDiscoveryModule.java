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
package net.hasor.dataway.config;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.naming.NamingService;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.utils.StringUtils;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 基于 Nacos 的 服务注册
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-01-01
 */
public class NacosDiscoveryModule implements WebModule {
    protected static Logger        logger = LoggerFactory.getLogger(NacosDiscoveryModule.class);
    private          String        nacosServerAddr;
    private          String        nacosNamespace;
    private          String        nacosGroupName;
    //
    private          String        serviceName;
    private          String        serviceIP;
    private          int           servicePort;
    private          NamingService nacosNamingService;

    public NacosDiscoveryModule(String serviceName, String serviceIP, int servicePort) {
        if (StringUtils.isBlank(serviceName)) {
            throw new IllegalArgumentException("serviceName is not specified");
        }
        if (StringUtils.isBlank(serviceIP)) {
            throw new IllegalArgumentException("serviceIP is not specified");
        }
        if (servicePort <= 0) {
            throw new IllegalArgumentException("servicePort is not specified");
        }
        this.serviceName = serviceName;
        this.serviceIP = serviceIP;
        this.servicePort = servicePort;
    }

    //    spring.cloud.nacos.discovery. = eth0
    //    spring.cloud.nacos.discovery.preferred = 192.168.
    @Override
    public void loadModule(WebApiBinder apiBinder) {
        String nacosServerAddr = apiBinder.getEnvironment().getSettings().getString("hasor.dataway.settings.dal_nacos_addr");
        String nacosNamespace = apiBinder.getEnvironment().getSettings().getString("hasor.dataway.settings.dal_nacos_namespace");
        String nacosGroupName = apiBinder.getEnvironment().getSettings().getString("hasor.dataway.settings.dal_nacos_group");
        BindInfo<NamingService> bindInfo = apiBinder.getBindInfo(NamingService.class);
        //
        if (bindInfo == null && StringUtils.isBlank(nacosServerAddr)) {
            throw new IllegalArgumentException("config dal_nacos_addr is missing.");
        }
        if (bindInfo == null && StringUtils.isBlank(nacosNamespace)) {
            throw new IllegalArgumentException("config dal_nacos_namespace is missing.");
        }
        if (StringUtils.isBlank(nacosGroupName)) {
            throw new IllegalArgumentException("config dal_nacos_group is missing.");
        }
        //
        this.nacosServerAddr = nacosServerAddr;
        this.nacosNamespace = nacosNamespace;
        this.nacosGroupName = nacosGroupName;
    }

    @Override
    public void onStart(AppContext appContext) throws Throwable {
        // .初始化 Nacos
        this.nacosNamingService = appContext.getInstance(NamingService.class);
        if (this.nacosNamingService == null) {
            Properties properties = new Properties();
            properties.put("serverAddr", this.nacosServerAddr);
            properties.put("namespace", this.nacosNamespace);
            this.nacosNamingService = NacosFactory.createNamingService(properties);
            logger.info("nacosNaming init NamingService, serverAddr = " + this.nacosServerAddr + ", namespace=" + this.nacosNamespace);
        } else {
            logger.info("nacosNaming Containers provide NamingService.");
        }
        // 注册服务
        this.nacosNamingService.registerInstance(this.serviceName, this.serviceIP, this.servicePort, this.nacosGroupName);
        logger.info("nacosNaming register Service, serviceName=" + this.serviceName + ", ip=" + this.serviceIP + ", port=" + this.servicePort + ", groupName=" + this.nacosGroupName);
    }

    @Override
    public void onStop(AppContext appContext) throws Throwable {
        this.nacosNamingService.deregisterInstance(this.serviceName, this.serviceIP, this.servicePort, this.nacosGroupName);
        logger.info("nacosNaming deregister Service, serviceName=" + this.serviceName + ", ip=" + this.serviceIP + ", port=" + this.servicePort + ", groupName=" + this.nacosGroupName);
    }
}