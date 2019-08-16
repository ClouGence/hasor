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
package net.hasor.registry.client.support;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.registry.RegistryCenter;
import net.hasor.registry.client.RsfCenterRegister;
import net.hasor.registry.client.RsfCenterResult;
import net.hasor.registry.client.domain.BeanInfo;
import net.hasor.registry.client.domain.ConsumerPublishInfo;
import net.hasor.registry.client.domain.ProviderPublishInfo;
import net.hasor.registry.client.domain.ServiceID;
import net.hasor.registry.common.InstanceInfo;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.rsf.utils.TimerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 负责维护RSF客户端服务在注册中心上的信息。
 * @version : 2016年2月18日
 * @author 赵永春 (zyc@hasor.net)
 */
class RegistryClientManager implements TimerTask {
    protected     Logger            logger = LoggerFactory.getLogger(getClass());
    private final RsfContext        rsfContext;
    private final TimerManager      timerManager;
    private final RsfCenterRegister centerRegister;
    private final RegistryCenter    registryCenter;

    //
    public RegistryClientManager(RsfContext rsfContext) {
        this.rsfContext = rsfContext;
        ClassLoader loader = rsfContext.getClassLoader();
        this.centerRegister = rsfContext.getRsfClient().wrapper(RsfCenterRegister.class);
        this.registryCenter = rsfContext.getAppContext().getInstance(RegistryCenter.class);
        this.timerManager = new TimerManager(this.registryCenter.getSettings().getHeartbeatTime(), "RsfCenter-BeatTimer", loader);
    }

    @Override
    public void run(Timeout timeout) {
        try {
            this.run();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        this.timerManager.atTime(this);
    }

    private void run() throws Exception {
        if (!this.rsfContext.isOnline()) {
            return;
        }
        List<String> serviceIDs = this.rsfContext.getServiceIDs();
        for (String serviceID : serviceIDs) {
            // .如果是工作隐藏模式下那么不参与注册
            RsfBindInfo<?> domain = this.rsfContext.getServiceInfo(serviceID);
            if (domain == null || domain.isShadow()) {
                continue;
            }
            // .重新注册服务
            this.onlineService(domain);
        }
    }
    //

    /**应用上线（所有服务都注册到中心）*/
    public synchronized void online() {
        logger.info("rsfCenterBeat-> received online signal.");
        List<String> serviceIDs = rsfContext.getServiceIDs();
        for (String serviceID : serviceIDs) {
            RsfBindInfo<Object> serviceInfo = rsfContext.getServiceInfo(serviceID);
            if (serviceInfo == null) {
                continue;
            }
            if (serviceInfo.isShadow()) {
                logger.info("online,failed -> {} is isShadow.", serviceInfo.getBindID());
                continue;
            }
            this.onlineService(serviceInfo);
        }
    }

    /**应用下线（所有服务都解除注册）*/
    public synchronized void offline() {
        logger.info("rsfCenterBeat-> received online signal.");
        List<String> serviceIDs = rsfContext.getServiceIDs();
        for (String serviceID : serviceIDs) {
            RsfBindInfo<Object> serviceInfo = rsfContext.getServiceInfo(serviceID);
            if (serviceInfo == null) {
                continue;
            }
            if (serviceInfo.isShadow()) {
                logger.info("offline,failed -> {} is isShadow.", serviceInfo.getBindID());
                continue;
            }
            this.offlineService(serviceInfo);
        }
    }
    //

    /**注册服务到中心*/
    public void onlineService(RsfBindInfo<?> domain) {
        this.onlineService(domain, 1);
    }

    private void onlineService(RsfBindInfo<?> domain, int tryTimes) {
        if (domain == null) {
            return;
        }
        // 最大重试 3次
        if (tryTimes >= 3) {
            logger.info("onlineService {} ,failed -> outof max retry count.", domain.getBindID());
            return;
        }
        if (!this.rsfContext.isOnline() || domain.isShadow()) {
            logger.info("onlineService {} ,failed -> online status is {} , shadow is {}.", domain.getBindID(), this.rsfContext.isOnline(), domain.isShadow());
            return;
        }
        try {
            // .检查要注册服务的协议
            Set<String> rsfRunProtocols = this.rsfContext.runProtocols();
            Set<String> readyProtocols = domain.getBindProtocols();
            if (readyProtocols != null) {
                for (String protocol : readyProtocols) {
                    if (!rsfRunProtocols.contains(protocol)) {
                        throw new IllegalStateException("not running '" + protocol + "' protocol, please check the configuration.");
                    }
                }
            }
            if (readyProtocols == null || readyProtocols.isEmpty()) {
                readyProtocols = rsfRunProtocols;
            }
            // .注册服务(提供者/消费者)
            RsfCenterResult<Void> registerInfo = null;
            if (RsfServiceType.Provider == domain.getServiceType()) {
                //
                Map<String, String> targetMap = new HashMap<String, String>();
                for (String protocol : readyProtocols) {
                    InterAddress interAddress = this.rsfContext.bindAddress(protocol);
                    targetMap.put(protocol, interAddress.toHostSchema());
                }
                ProviderPublishInfo info = new ProviderPublishInfo();
                info.setClientTimeout(domain.getClientTimeout());
                info.setSerializeType(domain.getSerializeType());
                info.setQueueMaxSize(this.rsfContext.getSettings().getQueueMaxSize());
                info.setSharedThreadPool(domain.isSharedThreadPool());
                info.setAddressMap(targetMap);
                info.setClientBeanInfo(BeanInfo.of(domain));
                //
                ServiceID serviceID = ServiceID.of(domain);
                registerInfo = this.centerRegister.registerProvider(this.registryCenter.getInstanceInfo(), serviceID, info);
                logger.info("publishService service {} register to center -> {}", domain.getBindID(), registerInfo);
            } else if (RsfServiceType.Consumer == domain.getServiceType()) {
                //
                ConsumerPublishInfo info = new ConsumerPublishInfo();
                info.setClientTimeout(domain.getClientTimeout());
                info.setSerializeType(domain.getSerializeType());
                info.setClientMaximumRequest(this.rsfContext.getSettings().getMaximumRequest());
                info.setMessage(domain.isMessage());
                info.setProtocol(new ArrayList<String>(readyProtocols));
                String protocol = this.rsfContext.getDefaultProtocol();
                InterAddress interAddress = this.rsfContext.bindAddress(protocol);
                info.setCommunicationAddress(interAddress.toHostSchema());
                info.setClientBeanInfo(BeanInfo.of(domain));
                //
                ServiceID serviceID = ServiceID.of(domain);
                registerInfo = this.centerRegister.registerConsumer(this.registryCenter.getInstanceInfo(), serviceID, info);
                logger.info("receiveService service {} register to center -> {}", domain.getBindID(), registerInfo);
            }
            //
            // .同步拉取地址数据
            if (registerInfo != null && registerInfo.isSuccess()) {
                pullAddress(domain);//更新地址池
            } else {
                this.onlineService(domain, tryTimes + 1); //重试
            }
        } catch (Exception e) {
            logger.error("service {} register to center error-> {}", domain.getBindID(), e.getMessage(), e);
            this.onlineService(domain, tryTimes + 1); //重试
        }
    }

    /**解除服务注册*/
    public void offlineService(RsfBindInfo<?> domain) {
        this.offlineService(domain, 1);
    }

    private void offlineService(RsfBindInfo<?> domain, int tryTimes) {
        if (domain == null) {
            return;
        }
        // 最大重试 3次
        if (tryTimes >= 3) {
            logger.info("offlineService {} ,failed -> outof max retry count.", domain.getBindID());
            return;
        }
        if (!this.rsfContext.isOnline() || domain.isShadow()) {
            logger.info("offlineService {} ,failed -> online status is {} , shadow is {}.", domain.getBindID(), this.rsfContext.isOnline(), domain.isShadow());
            return;
        }
        //
        try {
            String serviceID = domain.getBindID();
            RsfCenterResult<Void> result = this.centerRegister.unRegister(this.registryCenter.getInstanceInfo(), ServiceID.of(domain));
            if (result != null && result.isSuccess()) {
                logger.info("deleteService -> complete.", serviceID);
            } else {
                if (result == null) {
                    logger.error("deleteService -> failed , serviceID={} ,result is null.", serviceID);
                } else {
                    logger.error("deleteService -> failed , serviceID={} ,errorCode={} ,errorMessage={}.", //
                            serviceID, result.getErrorCode(), result.getErrorMessage());
                }
                Thread.sleep(500);
                this.offlineService(domain, tryTimes + 1);
            }
        } catch (Exception e) {
            logger.error("deleteService -> failed , serviceID={} ,error={}", domain.getBindID(), e.getMessage(), e);
            this.offlineService(domain, tryTimes + 1);
        }
    }
    //

    /** 拉地址，三次失败之后改为异步请求一次全量推送 */
    private void pullAddress(RsfBindInfo<?> domain) {
        if (RsfServiceType.Consumer != domain.getServiceType()) {
            return;/*只有Consumer才需要pull地址*/
        }
        // .拉地址3次尝试
        ServiceID serviceID = ServiceID.of(domain);
        InstanceInfo instanceInfo = this.registryCenter.getInstanceInfo();
        List<String> runProtocol = new ArrayList<String>(domain.getBindProtocols());
        logger.info("pullAddress '{}' 1st.", serviceID);
        RsfCenterResult<List<String>> providerResult = this.centerRegister.pullProviders(instanceInfo, serviceID, runProtocol);
        if (providerResult == null || !providerResult.isSuccess()) {
            logger.warn("pullAddress '{}' 2st.", serviceID);
            providerResult = this.centerRegister.pullProviders(instanceInfo, serviceID, runProtocol);
            if (providerResult == null || !providerResult.isSuccess()) {
                logger.error("pullAddress '{}' 3st.", serviceID);
                providerResult = this.centerRegister.pullProviders(instanceInfo, serviceID, runProtocol);
            }
        }
        //
        if (providerResult == null || !providerResult.isSuccess()) {
            if (providerResult == null) {
                logger.error("pullAddress {} failed at 3st. -> result is null.", serviceID);
            } else {
                logger.error("pullAddress {} failed at 3st. -> errorCode ={} ,errorMessage = {}", //
                        serviceID, providerResult.getErrorCode(), providerResult.getErrorMessage());
            }
            //
            logger.info("pullAddress {} failed try async request pullProviders.", serviceID);
            RsfCenterResult<Boolean> result = this.centerRegister.requestPushProviders(instanceInfo, serviceID, runProtocol);
            if (result == null || !result.isSuccess()) {
                if (result == null) {
                    logger.error("asyncPullAddress {} failed -> result is null.", serviceID);
                } else {
                    logger.error("asyncPullAddress {} failed -> errorCode ={} ,errorMessage = {}", //
                            serviceID, result.getErrorCode(), result.getErrorMessage());
                }
            } else {
                logger.info("asyncPullAddress {} successful -> waiting for the center pull providers.", serviceID);
            }
            return;
        }
        //
        // .准备服务提供者列表
        List<String> providerList = providerResult.getResult();
        List<InterAddress> newHostSet = new ArrayList<InterAddress>();
        if (providerList != null && !providerList.isEmpty()) {
            for (String providerAddress : providerList) {
                try {
                    newHostSet.add(new InterAddress(providerAddress));
                } catch (Throwable e) {
                    logger.error("pullAddress '" + providerAddress + "' formater error ->" + e.getMessage(), e);
                }
            }
        } else {
            logger.warn("pullAddress already up-to-date. pull empty.");
        }
        //
        // .更新服务提供者地址列表
        try {
            this.rsfContext.getUpdater().appendAddress(domain.getBindID(), newHostSet);
        } catch (Throwable e) {
            logger.error("pullAddress -> appendAddress failed ,serviceID={} ,message={}.", serviceID, e.getMessage(), e);
        }
    }
}