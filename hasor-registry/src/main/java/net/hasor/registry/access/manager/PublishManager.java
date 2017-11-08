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
package net.hasor.registry.access.manager;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.access.adapter.DataAdapter;
import net.hasor.registry.access.domain.*;
import net.hasor.registry.access.manager.TaskManager.PublishTask;
import net.hasor.registry.access.manager.TaskManager.RemoveTask;
import net.hasor.rsf.domain.RsfServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
/**
 * 提供服务的发布能力（注册、解除注册）
 * @version : 2016年9月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class PublishManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private DataAdapter dataAdapter;
    @Inject
    private TaskManager taskManager;
    //
    /** 保存或者更新服务信息 */
    private Result<Void> saveService(ServiceInfo serviceInfo) {
        //
        // .获取保存的服务信息
        boolean result = this.dataAdapter.storeService(serviceInfo.getBindID(), JsonUtils.converToString(serviceInfo));
        if (result) {
            ResultDO<Void> resultDO = new ResultDO<Void>();
            resultDO.setErrorInfo(ErrorCode.OK);
            resultDO.setSuccess(true);
            return resultDO;
        } else {
            ResultDO<Void> resultDO = new ResultDO<Void>();
            resultDO.setErrorInfo(ErrorCode.Failed_Save_ServiceInfo);
            resultDO.setSuccess(false);
            return resultDO;
        }
    }
    //
    /**订阅服务*/
    public Result<Void> publishConsumer(ServiceInfo serviceInfo, ConsumerInfo info) {
        // .获取保存或者更新服务信息
        Result<Void> saveResult = this.saveService(serviceInfo);
        if (saveResult == null || !saveResult.isSuccess()) {
            return DateCenterUtils.buildFailedResult(saveResult);
        }
        // .登记为服务消费者
        String instanceID = info.getInstanceID();
        String serviceID = serviceInfo.getBindID();
        boolean consumerResult = this.dataAdapter.storePoint(//
                instanceID, serviceID, Collections.singletonList(info.getRsfAddress()), RsfServiceType.Consumer//
        );
        if (!consumerResult) {
            ResultDO<Void> result = new ResultDO<Void>();
            result.setErrorInfo(ErrorCode.Failed_Publish_Consumer);
            result.setSuccess(false);
            return result;
        }
        this.dataAdapter.storeAddition(instanceID, serviceID, RsfServiceType.Consumer, JsonUtils.converToString(info));
        //
        // .返回registerID
        ResultDO<Void> resultDO = new ResultDO<Void>();
        resultDO.setSuccess(true);
        resultDO.setErrorInfo(ErrorCode.OK);
        return resultDO;
    }
    //
    /**发布服务*/
    public Result<Void> publishService(ServiceInfo serviceInfo, ProviderInfo info) {
        // .获取保存或者更新服务信息
        Result<Void> saveResult = this.saveService(serviceInfo);
        if (saveResult == null || !saveResult.isSuccess()) {
            return DateCenterUtils.buildFailedResult(saveResult);
        }
        // .登记服务提供者
        String instanceID = info.getInstanceID();
        String serviceID = serviceInfo.getBindID();
        boolean providerResult = this.dataAdapter.storePoint(//
                instanceID, serviceID, info.getRsfAddress(), RsfServiceType.Provider//
        );
        if (!providerResult) {
            ResultDO<Void> result = new ResultDO<Void>();
            result.setErrorInfo(ErrorCode.Failed_Publish_Provider);
            result.setSuccess(false);
            return result;
        }
        this.dataAdapter.storeAddition(instanceID, serviceID, RsfServiceType.Provider, JsonUtils.converToString(info));
        //
        // .异步任务，要求推送新地址给所有消费者(增量)
        PublishTask task = new PublishTask(serviceID, info.getRsfAddress());
        this.taskManager.asyncToPublish(serviceID, task);
        //
        // .返回
        ResultDO<Void> resultDO = new ResultDO<Void>();
        resultDO.setSuccess(true);
        resultDO.setErrorInfo(ErrorCode.OK);
        return resultDO;
    }
    //
    /**删除订阅*/
    public Result<Void> removeRegister(String instanceID, String serviceID) throws URISyntaxException {
        //
        // .查询当前身份（如果身份为提供者，那么查询pointList并推送给所有消费者）
        RsfServiceType serviceType = this.dataAdapter.getPointTypeByID(instanceID, serviceID);
        List<String> invalidAddressSet = null;
        if (RsfServiceType.Provider == serviceType) {
            invalidAddressSet = this.dataAdapter.getPointByID(instanceID, serviceID, RsfServiceType.Provider);
        }
        // .获取服务Info
        boolean removeResult = this.dataAdapter.remove(instanceID, serviceID);
        if (!removeResult) {
            ResultDO<Void> result = new ResultDO<Void>();
            result.setErrorInfo(ErrorCode.Failed_RemoveRegister);
            result.setSuccess(false);
            return result;
        }
        // .异步任务，要求推送失效的地址给所有消费者(增量)
        if (invalidAddressSet != null && !invalidAddressSet.isEmpty()) {
            RemoveTask task = new RemoveTask(serviceID, invalidAddressSet);
            this.taskManager.asyncToPublish(serviceID, task);
        }
        //
        ResultDO<Void> resultDO = new ResultDO<Void>();
        resultDO.setSuccess(true);
        resultDO.setErrorInfo(ErrorCode.OK);
        return resultDO;
    }
}