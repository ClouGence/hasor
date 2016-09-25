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
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.core.EventListener;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.RsfCenterRegister;
import net.hasor.rsf.center.RsfCenterResult;
import net.hasor.rsf.center.domain.CenterEventBody;
import net.hasor.rsf.center.domain.ConsumerPublishInfo;
import net.hasor.rsf.center.domain.ProviderPublishInfo;
import net.hasor.rsf.center.domain.PublishInfo;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.rsf.utils.TimerManager;
import org.more.RepeateException;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * 负责维护RSF客户端服务在注册中心上的信息。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfCenterClientManager implements TimerTask, EventListener<CenterEventBody> {
    public static final String CenterUpdate_Event = "CenterUpdate_Event";
    protected           Logger logger             = LoggerFactory.getLogger(getClass());
    private final RsfContext                            rsfContext;
    private final TimerManager                          timerManager;
    private final RsfCenterRegister                     centerRegister;
    private final ConcurrentMap<String, RsfBindInfo<?>> serviceMap;
    //
    public RsfCenterClientManager(RsfContext rsfContext) {
        rsfContext.getAppContext().getEnvironment().getEventContext().addListener(CenterUpdate_Event, this);
        this.rsfContext = rsfContext;
        this.timerManager = new TimerManager(rsfContext.getSettings().getCenterHeartbeatTime(), "RsfCenterBeatTimer");
        this.centerRegister = rsfContext.getRsfClient().wrapper(RsfCenterRegister.class);
        this.serviceMap = new ConcurrentHashMap<String, RsfBindInfo<?>>();
        this.timerManager.atTime(this);
    }
    //
    @Override
    public void onEvent(String event, CenterEventBody eventData) throws Throwable {
        String serviceID = eventData.getServiceID();
        if (StringUtils.isBlank(serviceID)) {
            return;
        }
        //
        if (this.serviceMap.containsKey(serviceID)) {
            RsfBindInfo<?> domain = this.serviceMap.get(serviceID);
            if (domain != null) {
                logger.info("update CenterSnapshotInfo success -> serviceID={}.", serviceID);
            } else {
                logger.error("update CenterSnapshotInfo failed, domain is undefined-> serviceID={}.", serviceID);
            }
        }
    }
    /**异步注册到注册中心*/
    public synchronized void newService(RsfBindInfo<?> domain, String eventType) {
        if (domain == null) {
            return;
        }
        if (this.serviceMap.containsKey(domain)) {
            String logMsg = "repeate serviceID is " + domain.getBindID();
            logger.error(logMsg);
            throw new RepeateException(logMsg);
        } else {
            logger.info("newService serviceID={} , eventType=", domain.getBindID(), eventType);
            this.serviceMap.put(domain.getBindID(), domain);
        }
    }
    /**同步从注册中心解除注册*/
    public void deleteService(RsfBindInfo<?> domain) {
        if (domain == null) {
            return;
        }
        if (this.serviceMap.containsKey(domain)) {
            this.serviceMap.remove(domain);
            this.offlineService(domain);//下线服务应用
        }
    }
    @Override
    public void run(Timeout timeout) {
        try {
            if (this.rsfContext.isOnline()) {
                this.run();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        timerManager.atTime(this);
    }
    public synchronized void online() {
        try {
            if (this.rsfContext.isOnline()) {
                logger.info("rsfCenterBeat-> received online signal.");
                this.run();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    public synchronized void offline() {
        List<RsfBindInfo<?>> serviceList = new ArrayList<RsfBindInfo<?>>(this.serviceMap.values());
        for (RsfBindInfo<?> domain : serviceList) {
            if (domain != null) {
                this.offlineService(domain);
            }
        }
    }
    private void offlineService(RsfBindInfo<?> domain) {
        String serviceID = domain.getBindID();
        String registerID = (String) domain.getMetaData(RsfConstants.Center_Ticket);
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
    private void run() throws Exception {
        List<RsfBindInfo<?>> needBeat = new ArrayList<RsfBindInfo<?>>();//需要心跳
        List<RsfBindInfo<?>> needRegister = new ArrayList<RsfBindInfo<?>>();//需要注册
        List<RsfBindInfo<?>> needRepair = new ArrayList<RsfBindInfo<?>>();//心跳失败，需要重新注册
        //
        //1.对所有服务进行分类
        List<RsfBindInfo<?>> iterator = new ArrayList<RsfBindInfo<?>>(this.serviceMap.values());
        for (RsfBindInfo<?> domain : iterator) {
            if (domain != null) {
                String ticketInfo = (String) domain.getMetaData(RsfConstants.Center_Ticket);
                if (StringUtils.isEmpty(ticketInfo)) {
                    needRegister.add(domain);//需要新注册
                } else {
                    needBeat.add(domain);//服务需要进行心跳
                }
            }
        }
        //
        //2.服务注册
        for (RsfBindInfo<?> domain : needRegister) {
            registerService(domain);
        }
        //
        //3.服务心跳
        if (!needBeat.isEmpty()) {
            for (RsfBindInfo<?> domain : needBeat) {
                String serviceID = domain.getBindID();
                String registerID = (String) domain.getMetaData(RsfConstants.Center_Ticket);
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
            domain.removeMetaData(RsfConstants.Center_Ticket);
            registerService(domain);
        }
    }
    private void registerService(RsfBindInfo<?> domain) {
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
                registerInfo = this.centerRegister.registerConsumer(info);
                logger.info("receiveService service {} register to center -> {}", domain.getBindID(), registerInfo);
            }
            //
            // .保存Ticket
            if (registerInfo != null && registerInfo.isSuccess()) {
                String ticketInfo = registerInfo.getResult();
                if (StringUtils.isNotBlank(ticketInfo)) {
                    domain.setMetaData(RsfConstants.Center_Ticket, ticketInfo);
                    pullAddress(domain);//更新地址池
                }
            }
        } catch (Exception e) {
            logger.error("service {} register to center error-> {}", domain.getBindID(), e.getMessage());
            logger.debug(e.getMessage(), e);
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
        String registerID = (String) domain.getMetaData(RsfConstants.Center_Ticket);
        logger.info("pullAddress '{}' 1st.", serviceID);
        RsfCenterResult<List<String>> providerResult = this.centerRegister.pullProviders(registerID, serviceID);
        if (providerResult == null || !providerResult.isSuccess()) {
            logger.warn("pullAddress '{}' 2st.", serviceID);
            providerResult = this.centerRegister.pullProviders(registerID, serviceID);
            if (providerResult == null || !providerResult.isSuccess()) {
                logger.error("pullAddress '{}' 3st.", serviceID);
                providerResult = this.centerRegister.pullProviders(registerID, serviceID);
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
            logger.info("pullAddress {} failed try async request pullProviders.", serviceID);
            RsfCenterResult<Boolean> result = this.centerRegister.requestPushProviders(registerID, serviceID);
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
        }
        //
        // .更新服务提供者地址列表
        try {
            this.rsfContext.getUpdater().appendAddress(serviceID, newHostSet);
        } catch (Throwable e) {
            logger.error("pullAddress -> appendAddress failed ,serviceID=" + serviceID + " ,message=" + e.getMessage(), e);
        }
    }
    private <T extends PublishInfo> T fillTo(RsfBindInfo<?> eventData, T info) {
        info.setBindID(eventData.getBindID());
        info.setBindGroup(eventData.getBindGroup());
        info.setBindName(eventData.getBindName());
        info.setBindVersion(eventData.getBindVersion());
        info.setBindType(eventData.getBindType().getName());
        info.setClientTimeout(eventData.getClientTimeout());
        info.setSerializeType(eventData.getSerializeType());
        return info;
    }
}