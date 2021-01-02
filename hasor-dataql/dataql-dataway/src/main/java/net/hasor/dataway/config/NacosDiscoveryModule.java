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
import com.alibaba.nacos.api.naming.pojo.Instance;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.utils.StringUtils;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

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
    private          String        serviceName;
    private          String        serviceIP;
    private          int           servicePort;
    private          String        serviceClusterName;
    //
    private          NamingService nacosNamingService;
    private          boolean       registerStatus;

    @Override
    public void loadModule(WebApiBinder apiBinder) throws SocketException {
        boolean nacosDiscovery = apiBinder.getEnvironment().getSettings().getBoolean("hasor.dataway.settings.dal_nacos_discovery", false);
        if (!nacosDiscovery) {
            logger.info("nacos Discovery is disable.");
            throw new IgnoreModuleException();
        }
        //
        BindInfo<NamingService> bindInfo = apiBinder.getBindInfo(NamingService.class);
        String nacosServerAddr = apiBinder.getEnvironment().getSettings().getString("hasor.dataway.settings.dal_nacos_addr");
        String nacosNamespace = apiBinder.getEnvironment().getSettings().getString("hasor.dataway.settings.dal_nacos_namespace", "public");
        String nacosGroupName = apiBinder.getEnvironment().getSettings().getString("hasor.dataway.settings.dal_nacos_group", "DEFAULT_GROUP");
        String serviceName = apiBinder.getEnvironment().getSettings().getString("hasor.dataway.settings.dal_nacos_service_name", "app");
        if (bindInfo == null && StringUtils.isBlank(nacosServerAddr)) {
            throw new IllegalArgumentException("config dal_nacos_addr is missing.");
        }
        if (bindInfo == null && StringUtils.isBlank(nacosNamespace)) {
            throw new IllegalArgumentException("config dal_nacos_namespace is missing.");
        }
        if (StringUtils.isBlank(nacosGroupName)) {
            throw new IllegalArgumentException("config dal_nacos_group is missing.");
        }
        if (StringUtils.isBlank(serviceName)) {
            throw new IllegalArgumentException("config dal_nacos_service_name is not specified");
        }
        this.nacosServerAddr = nacosServerAddr;
        this.nacosNamespace = nacosNamespace;
        this.nacosGroupName = nacosGroupName;
        this.serviceName = serviceName;
        //
        int nacosDiscoveryPort = apiBinder.getEnvironment().getSettings().getInteger("hasor.dataway.settings.dal_nacos_discovery_port", 0);
        if (nacosDiscoveryPort <= 0) {
            throw new IllegalArgumentException("config dal_nacos_service_port is not specified");
        }
        String nacosDiscoveryIP = apiBinder.getEnvironment().getSettings().getString("hasor.dataway.settings.dal_nacos_discovery_ip");
        String nacosDiscoveryPrefix = apiBinder.getEnvironment().getSettings().getString("hasor.dataway.settings.dal_nacos_discovery_prefix");
        String nacosDiscoveryNetworkInterface = apiBinder.getEnvironment().getSettings().getString("hasor.dataway.settings.dal_nacos_discovery_networkInterface");
        String nacosClusterName = apiBinder.getEnvironment().getSettings().getString("hasor.dataway.settings.dal_nacos_cluster_name");
        this.servicePort = nacosDiscoveryPort;
        this.serviceClusterName = nacosClusterName;
        if (StringUtils.isBlank(nacosDiscoveryIP) && StringUtils.isBlank(nacosDiscoveryPrefix) && StringUtils.isBlank(nacosDiscoveryNetworkInterface)) {
            throw new IllegalArgumentException("must config dal_nacos_discovery_ip or dal_nacos_discovery_prefix or dal_nacos_discovery_networkInterface");
        }
        if (StringUtils.isNotBlank(nacosDiscoveryIP)) {
            this.serviceIP = nacosDiscoveryIP;
            return;
        }
        Set<NetworkInterface> interfaces = new HashSet<>();
        if (StringUtils.isNotBlank(nacosDiscoveryNetworkInterface)) {
            Set<String> match = new HashSet<>(Arrays.asList(nacosDiscoveryNetworkInterface.split(",")));
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface nextElement = networkInterfaces.nextElement();
                if (match.isEmpty()) {
                    interfaces.add(nextElement);
                } else if (match.contains(nextElement.getName())) {
                    interfaces.add(nextElement);
                }
            }
        }
        //
        Set<String> foundNetwork = new HashSet<>();
        for (NetworkInterface networkInterface : interfaces) {
            Enumeration<InetAddress> enumeration = networkInterface.getInetAddresses();
            while (enumeration.hasMoreElements()) {
                InetAddress nextElement = enumeration.nextElement();
                String hostAddress = nextElement.getHostAddress();
                if (StringUtils.isBlank(nacosDiscoveryPrefix) || hostAddress.startsWith(nacosDiscoveryPrefix)) {
                    foundNetwork.add(hostAddress);
                }
            }
        }
        this.serviceIP = foundNetwork.stream().findAny().orElse(null);
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
        List<Instance> allInstances = this.nacosNamingService.getAllInstances(this.serviceName, this.nacosGroupName, Collections.singletonList(this.serviceClusterName));
        for (Instance instance : allInstances) {
            if (instance.getIp().equals(this.serviceIP) && instance.getPort() == this.servicePort) {
                logger.info("nacosNaming register already exists, " + commonLogMessage());
                this.registerStatus = false;
                return;
            }
        }
        this.nacosNamingService.registerInstance(this.serviceName, this.nacosGroupName, this.serviceIP, this.servicePort, this.serviceClusterName);
        this.registerStatus = true;
        logger.info("nacosNaming register Service, " + commonLogMessage());
    }

    @Override
    public void onStop(AppContext appContext) throws Throwable {
        if (this.registerStatus) {
            logger.info("nacosNaming deregister Service, " + commonLogMessage());
            this.nacosNamingService.deregisterInstance(this.serviceName, this.nacosGroupName, this.serviceIP, this.servicePort, this.serviceClusterName);
        }
    }

    protected String commonLogMessage() {
        return "serviceName=" + this.serviceName            //
                + ", groupName=" + this.nacosGroupName      //
                + ", ip=" + this.serviceIP                  //
                + ", port=" + this.servicePort              //
                + ", clusterName=" + this.serviceClusterName;
    }
}