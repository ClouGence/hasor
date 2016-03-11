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
package net.hasor.rsf.center.server;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.more.RepeateException;
import org.more.util.CommonCodeUtils.MD5;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.core.Inject;
import net.hasor.rsf.center.RsfCenterRegister;
import net.hasor.rsf.center.core.zktmp.ZkTmpService;
import net.hasor.rsf.center.core.zookeeper.ZkNodeType;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.domain.ConsumerPublishInfo;
import net.hasor.rsf.center.domain.ProviderPublishInfo;
import net.hasor.rsf.center.domain.PublishInfo;
import net.hasor.rsf.center.domain.constant.RsfCenterEvent;
import net.hasor.rsf.center.utils.DateCenterUtils;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.domain.RsfServiceType;
/**
 * 客户端注册中心接口{@link RsfCenterRegister}实现，负责将来自客户端的RSF注册请求发到zk集群。
 * @version : 2015年6月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCenterRegisterProvider implements RsfCenterRegister {
    protected Logger      logger = LoggerFactory.getLogger(getClass());
    @Inject
    private AppContext    appContext;
    @Inject
    private ZooKeeperNode zooKeeperNode;
    @Inject
    private ZkTmpService  zkTmpService;
    //
    //
    /**注册服务，该方法会检测服务的注册是否冲突。*/
    private String addServices(String hostString, PublishInfo info) throws Throwable, KeeperException, InterruptedException {
        String servicePath = "/rsf-center/services/" + info.getBindGroup() + "/" + info.getBindName() + "/" + info.getBindVersion();
        String serviceInfoPath = servicePath + "/info";
        String data = this.zooKeeperNode.readData(serviceInfoPath);
        if (StringUtils.isNotBlank(data)) {
            int startIndex = data.indexOf("<hashCode>");
            int endIndex = data.indexOf("</hashCode>");
            String hashCodeA = data.substring(startIndex + "<hashCode>".length(), endIndex);
            String hashCodeB = this.zkTmpService.publishInfoHashCode(info);
            if (!StringUtils.equals(hashCodeA, hashCodeB)) {
                throw new RepeateException("service " + info.getBindID() + " conflict.");
            }
        }
        //
        String serviceInfo = this.zkTmpService.serviceInfo(info);
        this.zooKeeperNode.createNode(ZkNodeType.Persistent, servicePath);
        Stat stat = this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, serviceInfoPath, serviceInfo);
        if (stat == null) {
            return null;
        }
        return servicePath;
    }
    //
    /**发布服务*/
    @Override
    public String publishService(String hostString, ProviderPublishInfo info) {
        try {
            //
            //1.注册服务：/rsf-center/services/group/name/version/info
            String servicePath = this.addServices(hostString, info);
            if (servicePath == null) {
                //数据保存失败，则反馈终端注册失败，等待RSF客户端下一次心跳再来注册。
                logger.error("publishService save serviceInfo to zk failed. -> hostString ={} ,serviceID ={} ,servicePath={}", //
                        hostString, info.getBindID(), servicePath);
                return null;
            }
            //
            //2.登记提供者：/rsf-center/services/group/name/version/provider/192.168.1.11:2180
            String providerInfo = this.zkTmpService.providerInfo(info);
            String providerInfoPath = servicePath + "/provider/" + hostString;
            this.zooKeeperNode.createNode(ZkNodeType.Persistent, providerInfoPath);
            Stat s1 = this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, providerInfoPath, providerInfo);
            if (s1 == null) {
                //数据保存失败，则反馈终端注册失败，等待RSF客户端下一次心跳再来注册。
                logger.error("publishService save provider to zk failed. -> hostString ={} ,serviceID ={} ,servicePath={} ,providerInfoPath={}", //
                        hostString, info.getBindID(), servicePath, providerInfoPath);
                return null;
            }
            //
            //3.服务注册信息：/rsf-center/registers/be0d771bda6aca49a262d9d9560c1081
            String registerID = (s1 == null) ? null : MD5.getMD5(servicePath + providerInfoPath);
            String registerPath = ZooKeeperNode.REGISTERS_PATH + "/" + registerID;
            String registerBody = DateCenterUtils.timestamp() + "@" + providerInfoPath;
            Stat s2 = this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, registerPath, registerBody);
            if (s2 == null) {
                //数据保存失败，则反馈终端注册失败，等待RSF客户端下一次心跳再来注册。
                logger.error("publishService save provider register to zk failed. -> hostString ={} ,serviceID ={} ,servicePath={} ,providerInfoPath={} ,registerID={} ,registerPath={}", //
                        hostString, info.getBindID(), servicePath, providerInfoPath, registerID, registerPath);
                return null;
            }
            //
            // --引发事件，通知推送进程推送服务地址
            logger.info("publishService host ={} ,serviceID ={} -> {}", hostString, info.getBindID(), registerID);
            appContext.getEnvironment().getEventContext().fireSyncEvent(RsfCenterEvent.ServicesChange_Event, info.getBindID());
            return registerID;
        } catch (Throwable e) {
            logger.error("publishService -> " + e.getMessage(), e);
            throw new RsfException(ProtocolStatus.InvokeError, e);
        }
    }
    //
    /**订阅服务*/
    @Override
    public String receiveService(String hostString, ConsumerPublishInfo info) {
        try {
            //
            //1.注册服务：/rsf-center/services/group/name/version/info
            String servicePath = this.addServices(hostString, info);
            if (servicePath == null) {
                //数据保存失败，则反馈终端注册失败，等待RSF客户端下一次心跳再来注册。
                logger.error("receiveService save serviceInfo to zk failed. -> hostString ={} ,serviceID ={} ,servicePath={}", //
                        hostString, info.getBindID(), servicePath);
                return null;
            }
            //
            //2.登记消费者：/rsf-center/services/group/name/version/consumer/192.168.1.11:2180
            String consumerInfo = this.zkTmpService.consumerInfo(info);
            String consumerInfoPath = servicePath + "/consumer/" + hostString;
            this.zooKeeperNode.createNode(ZkNodeType.Persistent, consumerInfoPath);
            Stat s1 = this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, consumerInfoPath, consumerInfo);
            if (s1 == null) {
                //数据保存失败，则反馈终端注册失败，等待RSF客户端下一次心跳再来注册。
                logger.error("receiveService save consumer to zk failed. -> hostString ={} ,serviceID ={} ,servicePath={} ,consumerInfoPath={}", //
                        hostString, info.getBindID(), servicePath, consumerInfoPath);
                return null;
            }
            //
            //3.服务注册信息：/rsf-center/registers/be0d771bda6aca49a262d9d9560c1081
            String registerID = (s1 == null) ? null : MD5.getMD5(servicePath + consumerInfoPath);
            String registerPath = ZooKeeperNode.REGISTERS_PATH + "/" + registerID;
            String registerBody = DateCenterUtils.timestamp() + "@" + consumerInfoPath;
            Stat s2 = this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, registerPath, registerBody);
            if (s2 == null) {
                //数据保存失败，则反馈终端注册失败，等待RSF客户端下一次心跳再来注册。
                logger.error("receiveService save consumer register to zk failed. -> hostString ={} ,serviceID ={} ,servicePath={} ,providerInfoPath={} ,registerID={} ,registerPath={}", //
                        hostString, info.getBindID(), servicePath, consumerInfoPath, registerID, registerPath);
                return null;
            }
            //
            // --引发事件，通知推送进程推送服务地址
            logger.info("receiveService host ={} ,serviceID ={} -> {}", hostString, info.getBindID(), registerID);
            appContext.getEnvironment().getEventContext().fireSyncEvent(RsfCenterEvent.ServicesChange_Event, info.getBindID());
            return registerID;
        } catch (Throwable e) {
            logger.error("receiveService -> " + e.getMessage(), e);
            throw new RsfException(ProtocolStatus.InvokeError, e);
        }
    }
    //
    /**服务心跳*/
    @Override
    public Map<String, Boolean> publishServiceBeat(String hostString, Map<String, String> beatMap) {
        return this.serviceBeat(hostString, beatMap, RsfServiceType.Provider);
    }
    @Override
    public Map<String, Boolean> receiveServiceBeat(String hostString, Map<String, String> beatMap) {
        return this.serviceBeat(hostString, beatMap, RsfServiceType.Consumer);
    }
    private Map<String, Boolean> serviceBeat(String hostString, Map<String, String> beatMap, RsfServiceType rsfServiceType) {
        Map<String, Boolean> resultArrays = new HashMap<String, Boolean>();
        for (Entry<String, String> ent : beatMap.entrySet()) {
            String serviceID = ent.getKey();
            resultArrays.put(serviceID, false);//预设值
            //
            String registerPath = ZooKeeperNode.SERVICES_PATH + "/" + serviceID + "/";
            resultArrays[i] = false;
            try {
                //1.心跳的注册信息：/rsf-center/registers/be0d771bda6aca49a262d9d9560c1081
                String registerBody = this.zooKeeperNode.readData(registerPath);
                String[] registerInfo = registerBody.split("@");
                //2.如果保存的心跳数据格式有误，则通知RSF客户端心跳失败。RSF客户端会对心跳失败的服务进行重新注册。
                if (registerInfo.length == 2) {
                    registerBody = DateCenterUtils.timestamp() + "@" + registerInfo[1];//new body
                    Stat s2 = this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, registerPath, registerBody);
                    if (s2 != null) {
                        resultArrays[i] = true;
                    } else {
                        logger.error("serviceBeat update beat failed -> hostString ={} ,registerPath ={}", hostString, registerPath);
                    }
                }
            } catch (Exception e) {
                logger.error("serviceBeat update beat failed -> hostString ={} ,registerPath ={} ,error ={}", hostString, registerPath, e.getMessage());
            }
        }
        return resultArrays;
    }
    //
    /**服务解除订阅*/
    @Override
    public boolean removePublish(String hostString, String serviceID) {
        return this.removeRegister(hostString, serviceID, RsfServiceType.Provider);
    }
    @Override
    public boolean removeReceive(String hostString, String serviceID) {
        return this.removeRegister(hostString, serviceID, RsfServiceType.Consumer);
    }
    private boolean removeRegister(String hostString, String serviceID, RsfServiceType rsfServiceType) {
        try {
            //1.删除注册信息：/rsf-center/registers/be0d771bda6aca49a262d9d9560c1081
            String deletePath = ZooKeeperNode.REGISTERS_PATH + "/" + registerID;
            String registerBody = this.zooKeeperNode.readData(deletePath);
            this.zooKeeperNode.deleteNode(deletePath);//删除失败也无所谓，Leader会定时清理数据
            String[] registerInfo = registerBody.split("@");
            //2.如果保存的心跳数据格式有误，则不继续执行删除动作。
            if (registerInfo.length != 2) {
                logger.error("removeRegister, body format error -> registerID= {} ,registerBody= {}", registerID, registerBody);
                return false;
            }
            //3.获取BindID于注册在ZK上的路径
            String terminalPath = registerInfo[1];
            if (StringUtils.isNotBlank(terminalPath)) {
                if (this.zooKeeperNode.getZooKeeper().exists(terminalPath, false) != null) {
                    //
                    String data = this.zooKeeperNode.readData(terminalPath);
                    if (StringUtils.isNotBlank(data)) {
                        int startIndex = data.indexOf("<bindID>");
                        int endIndex = data.indexOf("</bindID>");
                        String bindID = data.substring(startIndex + "<bindID>".length(), endIndex);
                        // --引发事件，通知推送进程推送服务地址
                        logger.info("removeRegister registerID= {} ,registerBody= {}", registerID, registerBody);
                        appContext.getEnvironment().getEventContext().fireSyncEvent(RsfCenterEvent.ServicesChange_Event, bindID);
                    }
                    this.zooKeeperNode.deleteNode(terminalPath);//删除失败也无所谓，Leader会定时清理数据
                    return true;
                }
            }
            //
            return false;
        } catch (Exception e) {
            logger.error("removeRegister, error -> " + e.getMessage(), e);
            //虽然数据删除失败，但是客户端不会为其在进行心跳服务。随着leader对数据的清理，注册中心中服务信息的最终一致性可以保障。
            return false;
        }
    }
}