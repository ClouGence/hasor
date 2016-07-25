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
import org.more.bizcommon.Result;
import net.hasor.core.Singleton;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.domain.ProviderPublishInfo;
import net.hasor.rsf.center.server.push.PushEvent;
import net.hasor.rsf.center.server.push.RsfCenterEventEnum;
import net.hasor.rsf.domain.RsfServiceType;
/**
 * 提供者Manager
 * @version : 2016年2月22日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class ProviderServiceManager extends BaseServiceManager {
    /**发布服务*/
    public String publishService(InterAddress hostString, ProviderPublishInfo info) throws Throwable {
        //
        // 1.保存服务信息：/rsf-center/services/group/name/version/info
        String serviceID = info.getBindID();
        String servicePath = this.addServices(info);
        if (servicePath == null) {
            logger.error("publishService save serviceInfo failed. -> hostString ={} ,serviceID ={}", hostString, serviceID);
            return null; //服务信息保存失败，反馈终端注册失败
        }
        //
        // 2.登记提供者：/rsf-center/services/group/name/version/provider/192.168.1.11:2180
        String providerInfo = this.zkTmpService.providerInfo(info);
        String providerTermPath = pathManager.evalProviderTermPath(serviceID, hostString);
        this.createNode(ZkNodeType.Persistent, providerTermPath);
        Stat s1 = this.saveOrUpdateNode(ZkNodeType.Persistent, providerTermPath, providerInfo);
        if (s1 == null) {
            logger.error("publishService save provider to zk failed. -> hostString ={} ,serviceID ={} ,providerTermPath={}", //
                    hostString, serviceID, providerTermPath);
            return null; //提供者数据保存失败，反馈终端注册失败
        }
        //
        // 3.初始化提供者的心跳：/rsf-center/services/group/name/version/consumer/192.168.1.11:2180/beat
        String providerBeatPath = pathManager.evalProviderTermBeatPath(serviceID, hostString);
        boolean beatResult = updateBeat(providerBeatPath);
        if (beatResult == false) {
            logger.error("publishService init beat failed. -> hostString ={} ,serviceID ={}", hostString, serviceID);
            return null; //初始化心跳数据失败
        }
        //
        // 4.更新提供者列表时间戳：/rsf-center/services/group/name/version/provider
        String snapshotInfo = null;
        try {
            String providerPath = pathManager.evalProviderPath(serviceID);
            snapshotInfo = updateSnapshot(hostString, serviceID, providerPath);
            return snapshotInfo;
        } finally {
            List<String> consumerList = this.getConsumerList(serviceID);
            String rsfHostString = convertTo(hostString);
            PushEvent event = RsfCenterEventEnum.AppendAddressEvent//
                    .newEvent(serviceID).addTarget(consumerList).setEventBody(rsfHostString).setSnapshotInfo(snapshotInfo);
            this.pushEvent(event);
        }
    }
    /**删除提供者*/
    public boolean removeRegister(String hostString, String serviceID) throws Throwable {
        //
        // 1.删除注册的服务
        boolean result = super.removeRegister(hostString, serviceID, RsfServiceType.Provider);
        //
        // 2.更新提供者列表时间戳：/rsf-center/services/group/name/version/provider
        if (result == true) {
            String providerPath = pathManager.evalProviderPath(serviceID);
            String snapshotInfo = updateSnapshot(hostString, serviceID, providerPath);
            logger.info("publishService host ={} ,serviceID ={} -> {}", hostString, serviceID);
            //
            List<String> consumerList = this.getConsumerList(serviceID);
            String rsfHostString = convertTo(hostString);
            PushEvent event = RsfCenterEventEnum.RemoveAddressEvent//
                    .newEvent(serviceID).addTarget(consumerList).setEventBody(rsfHostString).setSnapshotInfo(snapshotInfo);
            this.pushEvent(event);
        }
        return result;
    }
    private String updateSnapshot(String hostString, String serviceID, String providerTermPath) throws Throwable {
        //
        // 1.更新心跳时间
        updateBeat(providerTermPath);
        //
        // 2.引发事件，通知推送进程推送服务地址
        return this.readData(providerTermPath);
    }
    /**提供者者心跳*/
    public Result<Boolean> serviceBeat(InterAddress rsfHost, String forBindID) throws Throwable {
        return super.serviceBeat(rsfHost, forBindID, RsfServiceType.Provider);
    }
}