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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.more.RepeateException;
import org.more.json.JSON;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.core.EventListener;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.RsfCenterRegister;
import net.hasor.rsf.center.domain.CenterEventBody;
import net.hasor.rsf.center.domain.ConsumerPublishInfo;
import net.hasor.rsf.center.domain.ProviderPublishInfo;
import net.hasor.rsf.center.domain.PublishInfo;
import net.hasor.rsf.center.domain.ReceiveResult;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.rsf.domain.ServiceDomain;
import net.hasor.rsf.utils.TimerManager;
/**
 * 负责维护RSF客户端服务在注册中心上的信息。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfCenterBeatManager implements TimerTask, EventListener<CenterEventBody> {
    public static final String                            CenterUpdate_Event = "CenterUpdate_Event";
    protected static Logger                               logger             = LoggerFactory.getLogger(RsfConstants.RsfCenter_Logger);
    private final RsfContext                              rsfContext;
    private final String                                  hostString;
    private final TimerManager                            timerManager;
    private final RsfCenterRegister                       centerRegister;
    private final ConcurrentMap<String, ServiceDomain<?>> serviceMap;
    private final AtomicBoolean                           inited             = new AtomicBoolean(false);
    //
    public RsfCenterBeatManager(RsfContext rsfContext) {
        rsfContext.getAppContext().getEnvironment().getEventContext().addListener(CenterUpdate_Event, this);
        this.rsfContext = rsfContext;
        this.hostString = rsfContext.bindAddress().toHostSchema();
        this.timerManager = new TimerManager(rsfContext.getSettings().getCenterHeartbeatTime(), "RsfCenterBeatTimer");
        this.centerRegister = rsfContext.getRsfClient().wrapper(RsfCenterRegister.class);
        this.serviceMap = new ConcurrentHashMap<String, ServiceDomain<?>>();
        this.timerManager.atTime(this);
    }
    //
    @Override
    public void onEvent(String event, CenterEventBody eventData) throws Throwable {
        String serviceID = eventData.getServiceID();
        String snapshotInfo = eventData.getSnapshotInfo();
        if (StringUtils.isBlank(serviceID) == true) {
            return;
        }
        //
        if (this.serviceMap.containsKey(serviceID)) {
            ServiceDomain<?> domain = this.serviceMap.get(serviceID);
            if (domain != null) {
                domain.setCenterSnapshot(snapshotInfo);
                logger.info("update CenterSnapshotInfo success -> serviceID={} , snapshotInfo=", serviceID, snapshotInfo);
            } else {
                logger.error("update CenterSnapshotInfo failed, domain is undefined-> serviceID={} , snapshotInfo=", serviceID, snapshotInfo);
            }
        }
    }
    //
    /**异步注册到注册中心*/
    public synchronized void newService(ServiceDomain<?> domain, String eventType) {
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
    public void deleteService(ServiceDomain<?> domain) {
        if (domain == null) {
            return;
        }
        if (this.serviceMap.containsKey(domain)) {
            this.serviceMap.remove(domain);
            this.offlineService(domain);//下线服务应用
        }
    }
    //
    @Override
    public void run(Timeout timeout) {
        try {
            if (this.inited.get()) {
                this.run();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        timerManager.atTime(this);
    }
    //
    public synchronized void online() {
        this.inited.set(true);
    }
    public synchronized void offline() {
        if (this.inited.compareAndSet(true, false) == false) {
            return;
        }
        this.inited.set(false);
        List<ServiceDomain<?>> serviceList = new ArrayList<ServiceDomain<?>>(this.serviceMap.values());
        for (ServiceDomain<?> domain : serviceList) {
            if (domain != null) {
                this.offlineService(domain);
            }
        }
    }
    private void offlineService(ServiceDomain<?> domain) {
        try {
            String serviceID = domain.getBindID();
            if (StringUtils.isNotBlank(serviceID)) {
                //从注册中心删除
                Boolean res = null;
                if (RsfServiceType.Provider == domain.getServiceType()) {
                    res = this.centerRegister.removePublish(this.hostString, serviceID);
                } else if (RsfServiceType.Consumer == domain.getServiceType()) {
                    res = this.centerRegister.removeReceive(this.hostString, serviceID);
                }
                //打印Log
                if (res != null && res) {
                    logger.info("deleteService complete -> serviceID={} ,result={}", serviceID, res);
                } else {
                    logger.error("deleteService failed -> serviceID={} ,result={}", serviceID, res);
                }
            } else {
                logger.info("deleteService complete -> serviceID={}", serviceID);
            }
        } catch (Exception e) {
            logger.error("deleteService error -> serviceID={} ,error={}", domain.getBindID(), e.getMessage(), e);
        }
    }
    //
    private void run() throws Exception {
        List<ServiceDomain<?>> needBeat = new ArrayList<ServiceDomain<?>>();//需要心跳
        List<ServiceDomain<?>> needRegister = new ArrayList<ServiceDomain<?>>();//需要注册
        List<ServiceDomain<?>> needRepair = new ArrayList<ServiceDomain<?>>();//心跳失败，需要重新注册
        //
        //1.对所有服务进行分类
        List<ServiceDomain<?>> iterator = new ArrayList<ServiceDomain<?>>(this.serviceMap.values());
        for (ServiceDomain<?> domain : iterator) {
            if (domain != null) {
                if (StringUtils.isEmpty(domain.getCenterSnapshot())) {
                    needRegister.add(domain);//需要新注册
                } else {
                    needBeat.add(domain);//服务需要进行心跳
                }
            }
        }
        //
        //2.服务注册
        for (ServiceDomain<?> domain : needRegister) {
            try {
                String snapshotInfo = null;
                if (RsfServiceType.Provider == domain.getServiceType()) {
                    //
                    ProviderPublishInfo info = fillTo(domain, new ProviderPublishInfo());
                    info.setQueueMaxSize(this.rsfContext.getSettings().getQueueMaxSize());
                    snapshotInfo = this.centerRegister.publishService(this.hostString, info);
                    logger.info("publishService service {} register to center -> {}", domain.getBindID(), snapshotInfo);
                } else if (RsfServiceType.Consumer == domain.getServiceType()) {
                    //
                    ConsumerPublishInfo info = fillTo(domain, new ConsumerPublishInfo());
                    info.setClientMaximumRequest(this.rsfContext.getSettings().getMaximumRequest());
                    ReceiveResult receiveResult = this.centerRegister.receiveService(this.hostString, info);
                    snapshotInfo = processResult(domain.getBindID(), receiveResult);
                    logger.info("receiveService service {} register to center -> {}", domain.getBindID(), snapshotInfo);
                }
                if (StringUtils.isNotBlank(snapshotInfo)) {
                    domain.setCenterSnapshot(snapshotInfo);//更新远程信息
                }
            } catch (Exception e) {
                logger.error("service {} register to center error-> {}", domain.getBindID(), e.getMessage());
                logger.debug(e.getMessage(), e);
            }
        }
        //
        //3.服务心跳
        if (needBeat.isEmpty() == false) {
            //-区分提供者和订阅者-
            Map<String, ServiceDomain<?>> beatAllMap = new HashMap<String, ServiceDomain<?>>();
            Map<String, String> beatPMap = new HashMap<String, String>();//提供者
            Map<String, String> beatCMap = new HashMap<String, String>();//消费者
            for (ServiceDomain<?> domain : needBeat) {
                beatAllMap.put(domain.getBindID(), domain);
                /*   */if (RsfServiceType.Consumer == domain.getServiceType()) {
                    beatCMap.put(domain.getBindID(), domain.getCenterSnapshot());//心跳的服务ID和其对应的centerMarkData建立一个Map
                    //
                } else if (RsfServiceType.Provider == domain.getServiceType()) {
                    beatPMap.put(domain.getBindID(), domain.getCenterSnapshot());//心跳的服务ID和其对应的centerMarkData建立一个Map
                    //
                }
            }
            //-提供者心跳-
            try {
                Map<String, Boolean> beatResult = this.centerRegister.publishServiceBeat(this.hostString, beatPMap);//进行服务心跳
                if (beatResult != null && beatResult.size() == beatPMap.size()) {
                    for (Entry<String, Boolean> beatEnt : beatResult.entrySet()) {
                        Boolean val = beatEnt.getValue();
                        if (val != null && val.equals(true)) {
                            String key = beatEnt.getKey();
                            beatAllMap.remove(key);//心跳被注册中心接受，从Map中删除
                        }
                    }
                    logger.info("publishServiceBeat complete ->{}", JSON.toString(beatResult));
                } else {
                    long realLength = (beatResult == null) ? 0 : beatResult.size();
                    logger.info("publishServiceBeat failed->the beat return value length error ,expect {} but the actual is {}.", beatPMap.size(), realLength);
                }
            } catch (Exception e) {
                logger.info("publishServiceBeat failed->services={} ,error={}", new Date(), JSON.toString(beatPMap), e.getMessage());
                logger.debug(e.getMessage(), e);
            }
            //-订阅者心跳-
            try {
                Map<String, Boolean> beatResult = this.centerRegister.receiveServiceBeat(this.hostString, beatCMap);//进行服务心跳
                if (beatResult != null && beatResult.size() == beatCMap.size()) {
                    for (Entry<String, Boolean> beatEnt : beatResult.entrySet()) {
                        Boolean val = beatEnt.getValue();
                        if (val != null && val.equals(true)) {
                            String key = beatEnt.getKey();
                            beatAllMap.remove(key);//心跳被注册中心接受，从Map中删除
                        }
                    }
                    logger.info("receiveServiceBeat complete ->{}", JSON.toString(beatResult));
                } else {
                    long realLength = (beatResult == null) ? 0 : beatResult.size();
                    logger.info("receiveServiceBeat failed->the beat return value length error ,expect {} but the actual is {}.", beatCMap.size(), realLength);
                }
            } catch (Exception e) {
                logger.info("receiveServiceBeat failed->services={} ,error={}", new Date(), JSON.toString(beatCMap), e.getMessage());
                logger.debug(e.getMessage(), e);
            }
            //
            needRepair.addAll(beatAllMap.values());
        }
        //
        //4.重新注册服务
        for (ServiceDomain<?> domain : needRepair) {
            try {
                String snapshotInfo = null;
                /*   */if (RsfServiceType.Provider == domain.getServiceType()) {
                    ProviderPublishInfo info = fillTo(domain, new ProviderPublishInfo());
                    info.setQueueMaxSize(this.rsfContext.getSettings().getQueueMaxSize());
                    snapshotInfo = this.centerRegister.publishService(this.hostString, info);
                    logger.info("repairPublishService service {} register to center -> {}", domain.getBindID(), snapshotInfo);
                    //
                } else if (RsfServiceType.Consumer == domain.getServiceType()) {
                    ConsumerPublishInfo info = fillTo(domain, new ConsumerPublishInfo());
                    info.setClientMaximumRequest(this.rsfContext.getSettings().getMaximumRequest());
                    ReceiveResult receiveResult = this.centerRegister.receiveService(this.hostString, info);
                    snapshotInfo = processResult(domain.getBindID(), receiveResult);
                    logger.info("repairReceiveService service {} register to center -> {}", domain.getBindID(), snapshotInfo);
                    //
                }
                if (StringUtils.isNotBlank(snapshotInfo)) {
                    domain.setCenterSnapshot(snapshotInfo);//更新远程服务信息
                }
            } catch (Exception e) {
                logger.error("repairService service {} register to center error-> {}", domain.getBindID(), e.getMessage());
                logger.debug(e.getMessage(), e);
            }
        }
        //
    }
    private String processResult(String serviceID, ReceiveResult receiveResult) {
        //1.准备服务提供者列表
        List<InterAddress> newHostSet = new ArrayList<InterAddress>();
        List<String> providerList = receiveResult.getProviderList();
        if (providerList != null && providerList.isEmpty() == false) {
            for (String providerAddress : providerList) {
                try {
                    newHostSet.add(new InterAddress(providerAddress));
                } catch (Throwable e) {
                    logger.error("address '{}' formater error ->{}", providerAddress, e.getMessage());
                }
            }
        }
        //2.更新服务提供者地址列表
        try {
            this.rsfContext.getUpdater().appendAddress(serviceID, newHostSet);
        } catch (Throwable e) {
            logger.error("appendAddress failed ,serviceID=" + serviceID + " ,message=" + e.getMessage(), e);
            logger.debug(e.getMessage(), e);
        }
        //3.返回注册中心centerSnapshot
        return receiveResult.getCenterSnapshot();
    }
    private <T extends PublishInfo> T fillTo(ServiceDomain<?> eventData, T info) {
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