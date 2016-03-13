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
package net.hasor.rsf.center.server.manager;
import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import net.hasor.rsf.center.domain.ConsumerPublishInfo;
import net.hasor.rsf.center.domain.ReceiveResult;
import net.hasor.rsf.center.server.core.zookeeper.ZkNodeType;
import net.hasor.rsf.domain.RsfServiceType;
/**
 * 
 * @version : 2016年2月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class ConsumerServiceManager extends BaseServiceManager {
    /**发布服务*/
    public ReceiveResult publishService(String hostString, ConsumerPublishInfo info) throws KeeperException, InterruptedException, Throwable {
        //
        //1.注册服务：/rsf-center/services/group/name/version/info
        String serviceID = info.getBindID();
        String servicePath = this.addServices(hostString, info);
        if (servicePath == null) {
            logger.error("receiveService save serviceInfo failed. -> hostString ={} ,serviceID ={}", hostString, serviceID);
            return null; //服务信息保存失败，反馈终端注册失败
        }
        //
        //2.登记消费者：/rsf-center/services/group/name/version/consumer/192.168.1.11:2180
        String consumerInfo = this.zkTmpService.consumerInfo(info);
        String consumerTermPath = pathManager.evalConsumerTermPath(serviceID, hostString);
        this.createNode(ZkNodeType.Persistent, consumerTermPath);
        Stat s1 = this.saveOrUpdateNode(ZkNodeType.Persistent, consumerTermPath, consumerInfo);
        if (s1 == null) {
            logger.error("receiveService save consumer to zk failed. -> hostString ={} ,serviceID ={} ,servicePath={} ,consumerTermPath={}", //
                    hostString, serviceID, servicePath, consumerTermPath);
            return null; //提供者数据保存失败，反馈终端注册失败
        }
        //
        //3.服务注册信息：/rsf-center/services/group/name/version/consumer/192.168.1.11:2180/beat
        String consumerBeatPath = pathManager.evalConsumerTermBeatPath(serviceID, hostString);
        boolean beatResult = updateBeat(consumerBeatPath);
        if (beatResult == false) {
            logger.error("receiveService init beat failed. -> hostString ={} ,serviceID ={}", hostString, serviceID);
            return null; //初始化心跳数据失败
        }
        //
        //4.返回心跳时间
        String snapshotInfo = this.readData(consumerBeatPath);
        logger.info("receiveService host ={} ,serviceID ={} -> {}", hostString, serviceID, snapshotInfo);
        //
        //5.准备返回值
        ReceiveResult result = new ReceiveResult();
        result.setCenterSnapshot(snapshotInfo);
        List<String> providerList = this.getProviderList(serviceID);
        result.setProviderList(providerList);
        return result;
    }
    /**删除服务订阅者*/
    public boolean removeRegister(String hostString, String serviceID) throws Throwable {
        return super.removeRegister(hostString, serviceID, RsfServiceType.Consumer);
    }
}