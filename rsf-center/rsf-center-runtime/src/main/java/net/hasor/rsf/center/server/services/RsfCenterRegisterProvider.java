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
package net.hasor.rsf.center.server.services;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Inject;
import net.hasor.rsf.center.RsfCenterRegister;
import net.hasor.rsf.center.domain.ConsumerPublishInfo;
import net.hasor.rsf.center.domain.ProviderPublishInfo;
import net.hasor.rsf.center.server.manager.ConsumerServiceManager;
import net.hasor.rsf.center.server.manager.ProviderServiceManager;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.domain.RsfServiceType;
/**
 * 客户端注册中心接口{@link RsfCenterRegister}实现，负责将来自客户端的RSF注册请求发到zk集群。
 * @version : 2015年6月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCenterRegisterProvider implements RsfCenterRegister {
    protected Logger               logger = LoggerFactory.getLogger(RsfConstants.RsfCenter_Logger);
    @Inject
    private ProviderServiceManager providerServiceManager;
    @Inject
    private ConsumerServiceManager consumerServiceManager;
    //
    //
    /**发布服务*/
    @Override
    public String publishService(String hostString, ProviderPublishInfo info) {
        try {
            return this.providerServiceManager.publishService(hostString, info);
        } catch (Throwable e) {
            logger.error("publishService -> " + e.getMessage(), e);
            return null;
        }
    }
    /**订阅服务*/
    @Override
    public String receiveService(String hostString, ConsumerPublishInfo info) {
        try {
            return this.consumerServiceManager.publishService(hostString, info);
        } catch (Throwable e) {
            logger.error("receiveService -> " + e.getMessage(), e);
            return null;
        }
    }
    /**服务下线*/
    @Override
    public boolean removePublish(String hostString, String serviceID) {
        try {
            return this.providerServiceManager.removeRegister(hostString, serviceID);
        } catch (Throwable e) {
            //虽然数据删除失败，但是客户端不会为其在进行心跳服务。随着leader对数据的清理，注册中心中服务信息的最终一致性可以保障。
            logger.error("removeRegister, error -> " + e.getMessage(), e);
            return false;
        }
    }
    /**解除服务订阅*/
    @Override
    public boolean removeReceive(String hostString, String serviceID) {
        try {
            return this.consumerServiceManager.removeRegister(hostString, serviceID);
        } catch (Throwable e) {
            //虽然数据删除失败，但是客户端不会为其在进行心跳服务。随着leader对数据的清理，注册中心中服务信息的最终一致性可以保障。
            logger.error("removeRegister, error -> " + e.getMessage(), e);
            return false;
        }
    }
    /**服务心跳*/
    @Override
    public Map<String, Boolean> publishServiceBeat(String hostString, Map<String, String> beatMap) {
        return serviceBeat(hostString, beatMap, RsfServiceType.Provider);
    }
    /**订阅心跳*/
    @Override
    public Map<String, Boolean> receiveServiceBeat(String hostString, Map<String, String> beatMap) {
        return serviceBeat(hostString, beatMap, RsfServiceType.Consumer);
    }
    //
    //
    protected Map<String, Boolean> serviceBeat(String hostString, Map<String, String> beatMap, RsfServiceType rsfServiceType) {
        if (beatMap == null || StringUtils.isBlank(hostString)) {
            logger.error("serviceBeat failed, hostString or beatMap is empty.");
            return null;
        }
        Map<String, Boolean> resultArrays = new HashMap<String, Boolean>();
        for (Entry<String, String> ent : beatMap.entrySet()) {
            String serviceID = ent.getKey();
            resultArrays.put(serviceID, false);
            //
            try {
                boolean beatResult = false;
                if (rsfServiceType == RsfServiceType.Consumer) {
                    beatResult = this.consumerServiceManager.serviceBeat(hostString, serviceID, rsfServiceType);
                } else if (rsfServiceType == RsfServiceType.Provider) {
                    beatResult = this.providerServiceManager.serviceBeat(hostString, serviceID, rsfServiceType);
                } else {
                    beatResult = false;
                }
                resultArrays.put(serviceID, beatResult);
                logger.error("serviceBeat {} -> hostString ={} ,serviceID ={}", ((beatResult == false) ? "failed" : "succeed"), hostString, serviceID);
            } catch (Throwable e) {
                logger.error("serviceBeat error -> hostString ={} ,serviceID ={} ,error={}", hostString, serviceID, e.getMessage(), e);
                resultArrays.put(serviceID, false);
            }
        }
        return resultArrays;
    }
}