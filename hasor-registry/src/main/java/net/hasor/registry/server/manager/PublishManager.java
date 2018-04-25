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
package net.hasor.registry.server.manager;
import com.alibaba.fastjson.JSON;
import net.hasor.core.EventContext;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.client.domain.ConsumerPublishInfo;
import net.hasor.registry.client.domain.ProviderPublishInfo;
import net.hasor.registry.client.domain.ServiceID;
import net.hasor.registry.common.InstanceInfo;
import net.hasor.registry.server.domain.*;
import net.hasor.registry.server.pusher.RsfPusher;
import net.hasor.registry.storage.DataAdapter;
import net.hasor.registry.storage.DataEntity;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import static net.hasor.registry.server.domain.ErrorCode.*;
import static net.hasor.registry.server.utils.CenterUtils.*;
/**
 * 提供服务的发布能力（注册、解除注册）
 * @version : 2016年9月18日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class PublishManager implements RsfCenterConstants {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private DataAdapter  dataAdapter;
    @Inject
    private QueryManager queryManager;
    @Inject
    private RsfPusher    rsfPusher;
    @Inject
    private EventContext eventContext;
    //
    //
    //
    /** 发布服务 */
    public Result<Void> publishProvider(InstanceInfo instance, final ServiceID serviceID, final ProviderPublishInfo info) {
        // .服务是否存在，不存在的话新增
        if (!this.dataAdapter.exist(getDataKey(serviceID))) {
            ServiceBean serviceBean = new ServiceBean();
            serviceBean.setGroup(serviceID.getBindGroup());
            serviceBean.setName(serviceID.getBindName());
            serviceBean.setVersion(serviceID.getBindVersion());
            serviceBean.setMethodSet(new ArrayList<String>());
            //
            // .登记服务信息
            String dataKey = getDataKey(serviceBean);
            String dataValue = JSON.toJSONString(serviceBean);
            boolean result = this.dataAdapter.writeData(dataKey, dataValue, TAG_Service);
            if (!result) {
                return DateCenterUtils.buildFailedResult(failedResult(Storage_Service_Failed));
            }
        }
        // .登记为服务提供者
        String dataKey = getDataKey(instance, serviceID, RsfServiceType.Provider);
        String dataValue = JSON.toJSONString(info);
        if (isNoChange(dataKey, dataValue)) {
            return resultOK(null);
        }
        boolean result = this.dataAdapter.writeData(dataKey, dataValue, TAG_Provider);
        if (!result) {
            return failedResult(Storage_Provider_Failed);
        }
        // .推送服务(异步)
        try {
            this.eventContext.asyncTask(new Runnable() {
                @Override
                public void run() {
                    asyncPushProviders(serviceID, info, false);
                }
            });
            return resultOK(null);
        } catch (RejectedExecutionException e) {
            return DateCenterUtils.buildFailedResult(failedResult(SystemTooBusy));
        }
    }
    public void asyncPushProviders(ServiceID serviceID, ProviderPublishInfo info, boolean isRemove) {
        // .Provider提供的不同协议
        List<String> protocolSet = new ArrayList<String>(info.getAddressMap().keySet());
        // .根据支持的协议获取可以执行推送的消费者列表
        List<ConsumerPublishInfo> consumerList = this.queryManager.queryConsumerList(protocolSet, serviceID);
        // .逐个消费者推送，每个消费者只推送它所支持的协议
        for (ConsumerPublishInfo consumerInfo : consumerList) {
            List<String> addressList = new ArrayList<String>();
            for (String useProtocol : consumerInfo.getProtocol()) {
                String newAddress = info.getAddressMap().get(useProtocol);
                if (StringUtils.isNotBlank(newAddress)) {
                    addressList.add(newAddress);
                }
            }
            //
            if (addressList.isEmpty()) {
                continue;
            }
            //
            List<String> targetList = Collections.singletonList(consumerInfo.getCommunicationAddress());
            if (isRemove) {
                this.rsfPusher.removeAddress(serviceID, addressList, targetList);
            } else {
                this.rsfPusher.appendAddress(serviceID, addressList, targetList);
            }
        }
    }
    //
    /**订阅服务（不存在的服务不能订阅） */
    public Result<Void> publishConsumer(InstanceInfo instance, final ServiceID serviceID, final ConsumerPublishInfo info) {
        // .服务是否存在
        if (!this.dataAdapter.exist(getDataKey(serviceID))) {
            return failedResult(ErrorCode.ServiceUndefined);
        }
        // .消费者是否已经订阅
        String dataKey = getDataKey(instance, serviceID, RsfServiceType.Consumer);
        String dataValue = JSON.toJSONString(info);
        if (isNoChange(dataKey, dataValue)) {
            return resultOK(null);
        }
        // .登记为消费者
        boolean result = this.dataAdapter.writeData(dataKey, dataValue, TAG_Consumer);
        if (!result) {
            return failedResult(Storage_Consumer_Failed);
        }
        // .推送服务(异步)
        try {
            this.eventContext.asyncTask(new Runnable() {
                @Override
                public void run() {
                    asyncPushProviders(serviceID, info);
                }
            });
            return resultOK(null);
        } catch (RejectedExecutionException e) {
            return DateCenterUtils.buildFailedResult(failedResult(SystemTooBusy));
        }
    }
    public void asyncPushProviders(ServiceID serviceID, ConsumerPublishInfo info) {
        List<String> providerList = this.queryManager.queryProviderList(info.getProtocol(), serviceID);
        List<String> targetList = Collections.singletonList(info.getCommunicationAddress());
        this.rsfPusher.refreshAddress(serviceID, providerList, targetList);
    }
    //
    /** 删除订阅 */
    public Result<Void> removeRegister(InstanceInfo instance, final ServiceID serviceID) throws Throwable {
        String[] dataKeys = new String[] {//
                getDataKey(instance, serviceID, RsfServiceType.Provider),//
                getDataKey(instance, serviceID, RsfServiceType.Consumer) //
        };
        final AtomicReference<ProviderPublishInfo> removeProvider = new AtomicReference<ProviderPublishInfo>(null);
        final DataEntity[] dataEntities = this.dataAdapter.readData(dataKeys);
        for (DataEntity entity : dataEntities) {
            if (entity == null) {
                continue;
            }
            boolean removeResult = this.dataAdapter.deleteData(entity.getDataKey());
            if (!removeResult) {
                return failedResult(RemoveRegister_Failed);
            }
            long entityTags = entity.getTags();
            if (entityTags == (entityTags | TAG_Provider)) {
                removeProvider.set(JSON.parseObject(entity.getDataValue(), ProviderPublishInfo.class));
            }
        }
        //
        // .推送服务(异步)
        if (removeProvider.get() == null) {
            return resultOK(null);
        }
        try {
            this.eventContext.asyncTask(new Runnable() {
                @Override
                public void run() {
                    asyncPushProviders(serviceID, removeProvider.get(), true);
                }
            });
            return resultOK(null);
        } catch (RejectedExecutionException e) {
            return DateCenterUtils.buildFailedResult(failedResult(SystemTooBusy));
        }
    }
    //
    /** 请求推送地址 */
    public Result<Void> requestPushProviders(final InstanceInfo instance, final ServiceID serviceID, final List<String> protocol) {
        try {
            this.eventContext.asyncTask(new Runnable() {
                @Override
                public void run() {
                    List<String> providerList = queryManager.queryProviderList(protocol, serviceID);
                    List<String> targetList = Collections.singletonList(instance.getRsfAddress());
                    rsfPusher.refreshAddress(serviceID, providerList, targetList);
                }
            });
            return resultOK(null);
        } catch (RejectedExecutionException e) {
            return DateCenterUtils.buildFailedResult(failedResult(SystemTooBusy));
        }
    }
    //
    //
    private boolean isNoChange(String dataKey, String dataValue) {
        DataEntity dataEntity = this.dataAdapter.readData(dataKey); // 1扫描、1读
        if (dataEntity != null) {
            String md5NewValue = evalMD5(dataValue);
            if (dataEntity.getMD5().equalsIgnoreCase(md5NewValue)) {
                return true;
            }
        }
        return false;
    }
}