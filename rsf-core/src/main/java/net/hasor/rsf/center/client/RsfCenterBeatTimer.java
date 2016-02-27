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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.more.RepeateException;
import org.more.json.JSON;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.center.RsfCenterRegister;
import net.hasor.rsf.center.domain.PublishInfo;
import net.hasor.rsf.domain.Events;
import net.hasor.rsf.domain.ServiceDomain;
import net.hasor.rsf.utils.TimerManager;
/**
 * 负责维护RSF客户端服务在注册中心上的心跳。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfCenterBeatTimer implements TimerTask {
    protected Logger                                      logger = LoggerFactory.getLogger(getClass());
    private final String                                  hostString;
    private final TimerManager                            timerManager;
    private final RsfCenterRegister                       centerRegister;
    private final ConcurrentMap<ServiceDomain<?>, String> serviceMap;
    //
    public RsfCenterBeatTimer(RsfContext rsfContext) {
        this.hostString = rsfContext.bindAddress().getHostPort();
        this.timerManager = new TimerManager(rsfContext.getSettings().getCenterHeartbeatTime());
        this.centerRegister = rsfContext.getRsfClient().wrapper(RsfCenterRegister.class);
        this.serviceMap = new ConcurrentHashMap<ServiceDomain<?>, String>();
        this.timerManager.atTime(this);
    }
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
            this.serviceMap.put(domain, eventType);
        }
    }
    /**同步从注册中心解除注册*/
    public void deleteService(ServiceDomain<?> domain) {
        if (domain == null) {
            return;
        }
        String centerID = domain.getCenterID();
        if (this.serviceMap.containsKey(domain)) {
            this.serviceMap.remove(domain);
            try {
                if (StringUtils.isNotBlank(centerID)) {
                    boolean res = this.centerRegister.removeRegister(this.hostString, centerID);
                    if (res) {
                        logger.info("deleteService complete -> serviceID={} ,centerID={}", domain.getBindID(), centerID);
                    } else {
                        logger.error("deleteService failed -> serviceID={} ,centerID={}", domain.getBindID(), centerID);
                    }
                } else {
                    logger.info("deleteService complete -> serviceID={}", domain.getBindID());
                }
            } catch (Exception e) {
                logger.error("deleteService error -> serviceID={} ,centerID={} error={}", domain.getBindID(), centerID, e.getMessage(), e);
            }
        }
    }
    //
    @Override
    public void run(Timeout timeout) throws Exception {
        List<ServiceDomain<?>> needBeat = new ArrayList<ServiceDomain<?>>();//需要心跳
        List<ServiceDomain<?>> needRegister = new ArrayList<ServiceDomain<?>>();//需要注册
        List<ServiceDomain<?>> needRepair = new ArrayList<ServiceDomain<?>>();//心跳失败，需要重新注册
        //
        //1.对所有服务进行分类
        List<ServiceDomain<?>> iterator = new ArrayList<ServiceDomain<?>>(this.serviceMap.keySet());
        for (ServiceDomain<?> domain : iterator) {
            if (domain != null) {
                if (StringUtils.isEmpty(domain.getCenterID())) {
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
                String eventType = this.serviceMap.get(domain);
                String registerID = null;
                if (StringUtils.equals(eventType, Events.Rsf_ProviderService)) {
                    //
                    registerID = this.centerRegister.publishService(this.hostString, converTo(domain));
                    logger.info("publishService service {} register to center -> {}", domain.getBindID(), registerID);
                } else if (StringUtils.equals(eventType, Events.Rsf_ConsumerService)) {
                    //
                    registerID = this.centerRegister.receiveService(this.hostString, converTo(domain));
                    logger.info("receiveService service {} register to center -> {}", domain.getBindID(), registerID);
                }
                if (StringUtils.isNotBlank(registerID)) {
                    domain.setCenterID(registerID);//更新远程服务注册ID
                }
            } catch (Exception e) {
                logger.error("service {} register to center error-> {}", domain.getBindID(), e.getMessage());
            }
        }
        //
        //3.服务心跳
        if (needBeat.isEmpty() == false) {
            Map<String, ServiceDomain<?>> beatFailedMap = new HashMap<String, ServiceDomain<?>>();
            for (ServiceDomain<?> domain : needBeat) {
                beatFailedMap.put(domain.getCenterID(), domain);//心跳的服务和其对应的centerID建立一个Map
            }
            String[] centerIDArrays = beatFailedMap.keySet().toArray(new String[beatFailedMap.size()]);
            //
            try {
                Map<String, Boolean> resultLog = new HashMap<String, Boolean>();
                boolean[] regData = this.centerRegister.serviceBeat(this.hostString, centerIDArrays);//进行服务心跳
                if (regData != null && regData.length == centerIDArrays.length) {
                    for (int i = 0; i < centerIDArrays.length; i++) {
                        resultLog.put(centerIDArrays[i], regData[i]);
                        if (regData[i] == true) {
                            beatFailedMap.remove(centerIDArrays[i]);//心跳被注册中心接受，从Map中删除
                        }
                    }
                    logger.info("serviceBeat complete ->{}", JSON.toString(resultLog));
                } else {
                    long realLength = (regData == null) ? 0 : regData.length;
                    logger.info("serviceBeat failed->the beat return value length error ,expect {} but the actual is {}.", centerIDArrays.length, realLength);
                }
            } catch (Exception e) {
                logger.info("serviceBeat failed->services={} ,error={}", new Date(), JSON.toString(centerIDArrays), e.getMessage());
            }
            //
            needRepair.addAll(beatFailedMap.values());
        }
        //
        //4.重新注册服务
        for (ServiceDomain<?> domain : needRepair) {
            try {
                String eventType = this.serviceMap.get(domain);
                String registerID = null;
                if (StringUtils.equals(eventType, Events.Rsf_ConsumerService)) {
                    //
                    registerID = this.centerRegister.repairPublishService(this.hostString, domain.getCenterID(), converTo(domain));
                    logger.info("repairPublishService service {} register to center -> {}", domain.getBindID(), registerID);
                } else if (StringUtils.equals(eventType, Events.Rsf_ProviderService)) {
                    //
                    registerID = this.centerRegister.repairReceiveService(this.hostString, domain.getCenterID(), converTo(domain));
                    logger.info("repairReceiveService service {} register to center -> {}", domain.getBindID(), registerID);
                }
                if (StringUtils.isNotBlank(registerID)) {
                    domain.setCenterID(registerID);//更新远程服务注册ID
                }
            } catch (Exception e) {
                logger.error("repairService service {} register to center error-> {}", domain.getBindID(), e.getMessage());
            }
        }
        //
        timerManager.atTime(this);
    }
    private PublishInfo converTo(ServiceDomain<?> eventData) {
        PublishInfo info = new PublishInfo();
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