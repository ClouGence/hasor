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
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.registry.RsfCenterRegister;
import net.hasor.registry.RsfCenterResult;
import net.hasor.registry.RsfCenterSettings;
import net.hasor.registry.domain.client.ConsumerPublishInfo;
import net.hasor.registry.domain.client.ProviderPublishInfo;
import net.hasor.registry.domain.client.PublishInfo;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.rsf.utils.StringUtils;
import net.hasor.rsf.utils.TimerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static net.hasor.registry.RegistryConstants.Center_Ticket;
/**
 * 负责维护RSF客户端服务在注册中心上的信息。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
class RegistryClientManager implements TimerTask {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final RsfContext        rsfContext;
    private final TimerManager      timerManager;
    private final RsfCenterRegister centerRegister;
    //
    public RegistryClientManager(RsfContext rsfContext) {
        this.rsfContext = rsfContext;
        RsfCenterSettings settings = this.rsfContext.getAppContext().getInstance(RsfCenterSettings.class);
        ClassLoader loader = rsfContext.getClassLoader();
        this.timerManager = new TimerManager(settings.getHeartbeatTime(), "RsfCenterBeatTimer", loader);
        this.centerRegister = rsfContext.getRsfClient().wrapper(RsfCenterRegister.class);
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
        List<RsfBindInfo<?>> needBeat = new ArrayList<RsfBindInfo<?>>();    // 需要心跳
        List<RsfBindInfo<?>> needRegister = new ArrayList<RsfBindInfo<?>>();// 需要注册
        List<RsfBindInfo<?>> needRepair = new ArrayList<RsfBindInfo<?>>();  // 需要重新注册
        //
        //1.对所有服务进行分类
        List<String> serviceIDs = rsfContext.getServiceIDs();
        List<RsfBindInfo<?>> iterator = new ArrayList<RsfBindInfo<?>>(serviceIDs.size());
        for (String serviceID : serviceIDs) {
            iterator.add(rsfContext.getServiceInfo(serviceID));
        }
        for (RsfBindInfo<?> domain : iterator) {
            // .如果是工作隐藏模式下那么不参与注册
            if (domain == null || domain.isShadow()) {
                continue;
            }
            // -
            String ticketInfo = (String) domain.getMetaData(Center_Ticket);
            if (StringUtils.isEmpty(ticketInfo)) {
                needRegister.add(domain);//需要新注册
            } else {
                needBeat.add(domain);//服务需要进行心跳
            }
        }
        //
        //2.服务注册
        for (RsfBindInfo<?> domain : needRegister) {
            this.onlineService(domain);
        }
        //
        //3.服务心跳
        if (!needBeat.isEmpty()) {
            for (RsfBindInfo<?> domain : needBeat) {
                String serviceID = domain.getBindID();
                String registerID = (String) domain.getMetaData(Center_Ticket);
                try {
                    RsfCenterResult<Boolean> beatResult = this.centerRegister.serviceBeat(registerID, serviceID);
                    if (beatResult == null || !beatResult.isSuccess()) {
                        needRepair.add(domain);
                        if (beatResult == null) {
                            logger.error("serviceBeat failed -> beatResult is null , serviceID ={} ,registerID ={}", serviceID, registerID);
                        } else {
                            logger.error("serviceBeat failed -> error , serviceID ={} ,registerID ={} ,errorCode ={} ,errorMessage ={}", //
                                    serviceID, registerID, beatResult.getErrorCode(), beatResult.getErrorMessage());
                        }
                        continue;
                    }
                    //
                    logger.info("serviceBeat complete -> serviceID ={} ,registerID ={}", serviceID, registerID);
                } catch (Exception e) {
                    logger.error("serviceBeat error -> serviceID ={} ,registerID ={} , error = {}", serviceID, registerID, e.getMessage(), e);
                }
            }
        }
        //
        //4.重新注册服务
        for (RsfBindInfo<?> domain : needRepair) {
            domain.removeMetaData(Center_Ticket);
            onlineService(domain);
        }
    }
    //
    //
    /**同步从注册中心解除注册*/
    public synchronized void offline() {
        logger.info("rsfCenterBeat-> received online signal.");
        List<String> serviceIDs = rsfContext.getServiceIDs();
        for (String serviceID : serviceIDs) {
            RsfBindInfo<Object> serviceInfo = rsfContext.getServiceInfo(serviceID);
            if (serviceInfo != null) {
                this.offlineService(serviceInfo);
            }
        }
    }
    /**解除服务注册*/
    public void offlineService(RsfBindInfo<?> domain) {
        if (domain == null || !this.rsfContext.isOnline()) {
            return;
        }
        //
        String serviceID = domain.getBindID();
        String registerID = (String) domain.getMetaData(Center_Ticket);
        try {
            //
            // .解除服务注册
            if (StringUtils.isBlank(registerID)) {
                logger.warn("deleteService -> service is not registered, serviceID={}", serviceID);
                return;
            }
            //
            RsfCenterResult<Boolean> result = this.centerRegister.unRegister(registerID, serviceID);
            if (result != null && result.isSuccess()) {
                logger.info("deleteService -> complete.", serviceID);
            } else {
                if (result == null) {
                    logger.error("deleteService -> failed , serviceID={} ,result is null.", serviceID);
                } else {
                    logger.error("deleteService -> failed , serviceID={} ,errorCode={} ,errorMessage={}.", //
                            serviceID, result.getErrorCode(), result.getErrorMessage());
                }
            }
        } catch (Exception e) {
            logger.error("deleteService -> failed , serviceID={} ,error={}", domain.getBindID(), e.getMessage(), e);
        }
    }
    //
    /**服务上线（所有服务都上线）*/
    public synchronized void online() {
        logger.info("rsfCenterBeat-> received online signal.");
        List<String> serviceIDs = rsfContext.getServiceIDs();
        for (String serviceID : serviceIDs) {
            RsfBindInfo<Object> serviceInfo = rsfContext.getServiceInfo(serviceID);
            if (serviceInfo != null) {
                this.onlineService(serviceInfo);
            }
        }
    }
    /**注册服务到中心*/
    public void onlineService(RsfBindInfo<?> domain) {
        if (domain == null || !this.rsfContext.isOnline()) {
            return;
        }
        try {
            // .注册服务(提供者/消费者)
            RsfCenterResult<String> registerInfo = null;
            if (RsfServiceType.Provider == domain.getServiceType()) {
                //
                ProviderPublishInfo info = fillTo(domain, new ProviderPublishInfo());
                info.setQueueMaxSize(this.rsfContext.getSettings().getQueueMaxSize());
                info.setSharedThreadPool(domain.isSharedThreadPool());
                registerInfo = this.centerRegister.registerProvider(info);
                logger.info("publishService service {} register to center -> {}", domain.getBindID(), registerInfo);
            } else if (RsfServiceType.Consumer == domain.getServiceType()) {
                //
                ConsumerPublishInfo info = fillTo(domain, new ConsumerPublishInfo());
                info.setClientMaximumRequest(this.rsfContext.getSettings().getMaximumRequest());
                info.setMessage(domain.isMessage());
                registerInfo = this.centerRegister.registerConsumer(info);
                logger.info("receiveService service {} register to center -> {}", domain.getBindID(), registerInfo);
            }
            //
            // .同步拉取地址数据
            if (registerInfo != null && registerInfo.isSuccess()) {
                domain.setMetaData(Center_Ticket, registerInfo.getResult());
                pullAddress(domain);//更新地址池
            }
        } catch (Exception e) {
            logger.error("service {} register to center error-> {}", domain.getBindID(), e.getMessage(), e);
        }
    }
    //
    /** 拉地址 */
    private void pullAddress(RsfBindInfo<?> domain) {
        if (RsfServiceType.Consumer != domain.getServiceType()) {
            return;/*只有Consumer才需要pull地址*/
        }
        // .拉地址3次尝试
        String serviceID = domain.getBindID();
        String registerID = (String) domain.getMetaData(Center_Ticket);
        String protocol = this.rsfContext.getDefaultProtocol();
        logger.info("pullAddress[{}] '{}' 1st.", protocol, serviceID);
        RsfCenterResult<List<String>> providerResult = this.centerRegister.pullProviders(registerID, serviceID, protocol);
        if (providerResult == null || !providerResult.isSuccess()) {
            logger.warn("pullAddress[{}] '{}' 2st.", protocol, serviceID);
            providerResult = this.centerRegister.pullProviders(registerID, serviceID, protocol);
            if (providerResult == null || !providerResult.isSuccess()) {
                logger.error("pullAddress[{}] '{}' 3st.", protocol, serviceID);
                providerResult = this.centerRegister.pullProviders(registerID, serviceID, protocol);
            }
        }
        //
        if (providerResult == null || !providerResult.isSuccess()) {
            if (providerResult == null) {
                logger.error("pullAddress[{}] {} failed at 3st. -> result is null.", protocol, serviceID);
            } else {
                logger.error("pullAddress[{}] {} failed at 3st. -> errorCode ={} ,errorMessage = {}", //
                        protocol, serviceID, providerResult.getErrorCode(), providerResult.getErrorMessage());
            }
            //
            InterAddress callBackAddress = this.rsfContext.publishAddress(protocol);
            String callBackTo = callBackAddress.toHostSchema();
            logger.info("pullAddress[{}] {} failed try async request pullProviders. callBack is {}", protocol, serviceID, callBackTo);
            RsfCenterResult<Boolean> result = this.centerRegister.requestPushProviders(registerID, serviceID, protocol, callBackTo);
            if (result == null || !result.isSuccess()) {
                if (result == null) {
                    logger.error("asyncPullAddress[{}] {} failed -> result is null.", protocol, serviceID);
                } else {
                    logger.error("asyncPullAddress[{}] {} failed -> errorCode ={} ,errorMessage = {}", //
                            protocol, serviceID, result.getErrorCode(), result.getErrorMessage());
                }
            } else {
                logger.info("asyncPullAddress[{}] {} successful -> waiting for the center pull providers.", protocol, serviceID);
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
                    logger.error("pullAddress[{}] '" + providerAddress + "' formater error ->" + e.getMessage(), protocol, e);
                }
            }
        } else {
            logger.warn("pullAddress[{}] already up-to-date. pull empty.", protocol);
        }
        //
        // .更新服务提供者地址列表
        try {
            this.rsfContext.getUpdater().appendAddress(serviceID, newHostSet);
        } catch (Throwable e) {
            logger.error("pullAddress[{}] -> appendAddress failed ,serviceID={} ,message={}.", protocol, serviceID, e.getMessage(), e);
        }
    }
    private <T extends PublishInfo> T fillTo(RsfBindInfo<?> eventData, T info) {
        //
        Set<String> protocols = this.rsfContext.runProtocols();
        if (protocols == null || protocols.isEmpty()) {
            throw new IllegalStateException("not running any protocol, please check the configuration.");
        }
        StringBuilder addressList = new StringBuilder("");
        if (RsfServiceType.Provider == eventData.getServiceType()) {
            // - 提供者需要上报所有地址
            for (String protocol : protocols) {
                InterAddress interAddress = this.rsfContext.publishAddress(protocol);
                String rsfURL = interAddress.toHostSchema();
                this.logger.info("rsfContext -> doStart , bindAddress : {}", rsfURL);
                if (addressList.length() > 0) {
                    addressList.append(',');
                }
                addressList.append(rsfURL);
            }
        } else {
            // - 订阅者仅上报默认协议地址
            String protocol = this.rsfContext.getDefaultProtocol();
            InterAddress interAddress = this.rsfContext.publishAddress(protocol);
            String rsfURL = interAddress.toHostSchema();
            addressList.append(rsfURL);
        }
        //
        info.setBindID(eventData.getBindID());
        info.setBindGroup(eventData.getBindGroup());
        info.setBindName(eventData.getBindName());
        info.setBindVersion(eventData.getBindVersion());
        info.setBindType(eventData.getBindType().getName());
        info.setClientTimeout(eventData.getClientTimeout());
        info.setSerializeType(eventData.getSerializeType());
        info.setTargetList(addressList.toString());
        return info;
    }
}