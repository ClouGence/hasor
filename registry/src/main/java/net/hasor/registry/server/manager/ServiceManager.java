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
import net.hasor.core.AppContext;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.domain.server.*;
import net.hasor.registry.server.adapter.AuthQuery;
import net.hasor.registry.server.adapter.DataAdapter;
import net.hasor.registry.server.adapter.QueryOption;
import net.hasor.registry.server.domain.*;
import net.hasor.registry.server.pushing.RsfPusher;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.rsf.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.*;

import static net.hasor.registry.server.domain.RsfCenterConstants.*;
/**
 * Center功能实现
 * @version : 2016年9月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class ServiceManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private RsfRequest        rsfRequest;   // 当前 Rsf 请求
    @Inject
    private AppContext        appContext;   // Hasor AppContext
    @Inject
    private DataAdapter       dataAdapter;  // Center数据读写接口
    @Inject
    private RsfPusher         rsfPusher;    // 服务推送触发器
    @Inject
    private AuthQuery         authQuery;    // 接口权限查询接口
    @Inject
    private EnvironmentConfig envConfig;    // Center配置
    //
    /* 获取授权信息 */
    private AuthInfo getAuthInfo() {
        return (AuthInfo) this.rsfRequest.getAttribute(RsfCenterConstants.Center_Request_AuthInfo);
    }
    /* 处理失败的情况 */
    private <T> Result<T> buildFailedResult(Result<?> resultInfo) {
        return DateCenterUtils.buildFailedResult(resultInfo);
    }
    /* 过滤出订阅者列表 */
    private List<InterAddress> filterConsumerList(List<ObjectDO> allObjectList) {
        List<InterAddress> targets = new ArrayList<InterAddress>();
        if (allObjectList != null && !allObjectList.isEmpty()) {
            for (ObjectDO objectDO : allObjectList) {
                // - .过滤数据,只保留订阅者
                if (!RsfCenterConstants.Center_DataKey_Consumer.equalsIgnoreCase(objectDO.getType())) {
                    continue;
                }
                // - .过滤长时间没有心跳的订阅者
                long lastRefreshTime = (objectDO.getRefreshTime() == null) ? System.currentTimeMillis() : objectDO.getRefreshTime().getTime();
                long overRefreshTime = lastRefreshTime + this.envConfig.getConsumerExpireTime();
                if (System.currentTimeMillis() >= overRefreshTime) {
                    continue;/*过期了*/
                }
                // - .待推送列表
                try {
                    ConsumerInfo consumerInfo = JsonUtils.converToService(objectDO.getContent(), ConsumerInfo.class);
                    targets.add(new InterAddress(consumerInfo.getRsfAddress()));
                } catch (Exception e) {
                    this.logger.error(LogUtils.create("ERROR_300_00006")//
                            .addLog("objectID", objectDO.getObjectID())//
                            .addLog("error", e.getMessage())//
                            .toJson(), e);
                }
            }
        }
        return targets;
    }
    /* 过滤出提供者列表 */
    private List<InterAddress> filterProviderList(List<ObjectDO> allObjectList, String protocol) {
        List<InterAddress> targets = new ArrayList<InterAddress>();
        if (allObjectList != null && !allObjectList.isEmpty()) {
            for (ObjectDO objectDO : allObjectList) {
                // - .过滤数据,只保留订阅者
                if (!RsfCenterConstants.Center_DataKey_Provider.equalsIgnoreCase(objectDO.getType())) {
                    continue;
                }
                // - .过滤长时间没有心跳的订阅者
                long lastRefreshTime = (objectDO.getRefreshTime() == null) ? System.currentTimeMillis() : objectDO.getRefreshTime().getTime();
                long overRefreshTime = lastRefreshTime + this.envConfig.getProviderExpireTime();
                if (System.currentTimeMillis() >= overRefreshTime) {
                    continue;/*过期了*/
                }
                // - .待推送列表
                try {
                    ProviderInfo providerInfo = JsonUtils.converToService(objectDO.getContent(), ProviderInfo.class);
                    List<String> rsfAddressList = providerInfo.getRsfAddress();
                    for (String host : rsfAddressList) {
                        if (StringUtils.isBlank(host)) {
                            continue;
                        }
                        InterAddress interAddress = new InterAddress(host);
                        String sechma = interAddress.getSechma();
                        if (sechma == null || !sechma.equalsIgnoreCase(protocol)) {
                            continue;
                        }
                        targets.add(interAddress);
                    }
                } catch (Exception e) {
                    this.logger.error(LogUtils.create("ERROR_300_00006")//
                            .addLog("objectID", objectDO.getObjectID())//
                            .addLog("error", e.getMessage())//
                            .toJson(), e);
                }
            }
        }
        return targets;
    }
    //
    //
    /** 保存或者更新服务信息 */
    private Result<String> saveService(ServiceInfo serviceInfo) {
        //
        // .获取保存的服务信息
        String serviceObjectKey = Center_DataKey_Service + serviceInfo.getBindID();
        Result<ObjectDO> resultInfo = this.dataAdapter.queryObjectByID(serviceObjectKey);
        if (resultInfo == null || !resultInfo.isSuccess()) {
            return buildFailedResult(resultInfo);
        }
        //
        // .保存或更新服务信息
        ObjectDO serviceObjectDO = resultInfo.getResult();
        if (serviceObjectDO == null) {
            serviceObjectDO = new ObjectDO();//服务对象
            serviceObjectDO.setObjectID(serviceObjectKey);
            serviceObjectDO.setType(Center_DataKey_Service);
            serviceObjectDO.setRefreshTime(new Date());
            serviceObjectDO.setContent(JsonUtils.converToString(serviceInfo));
            Result<Boolean> storeResult = this.dataAdapter.storeObject(serviceObjectDO);
            if (storeResult == null || !storeResult.isSuccess()) {
                return buildFailedResult(storeResult);
            }
            if (storeResult.getResult() == null || !storeResult.getResult()) {
                ResultDO<String> result = new ResultDO<String>();
                result.setSuccess(false);
                result.setErrorInfo(ErrorCode.PublishServiceFailed_StoreInfo);
                return result;
            }
            serviceObjectDO.setObjectID(serviceObjectKey);
        } else {
            // TODO update service Info
            this.dataAdapter.refreshObject(serviceObjectKey);
        }
        //
        // .返回数据
        ResultDO<String> result = new ResultDO<String>();
        result.setErrorInfo(ErrorCode.OK);
        result.setResult(serviceObjectDO.getObjectID());
        result.setSuccess(true);
        return result;
    }
    //
    /**订阅服务*/
    public Result<String> publishConsumer(ServiceInfo serviceInfo, ConsumerInfo info) throws URISyntaxException {
        //
        // .检查权限
        AuthInfo authInfo = this.getAuthInfo();
        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, serviceInfo, RsfServiceType.Consumer);
        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
            return buildFailedResult(checkResult);
        }
        if (!checkResult.getResult()) {
            ResultDO<String> result = new ResultDO<String>();
            result.setErrorInfo(ErrorCode.AuthCheckFailed_ResultEmpty);
            result.setSuccess(false);
            return result;
        }
        //
        // .获取保存或者更新服务信息
        Result<String> saveResult = this.saveService(serviceInfo);
        if (saveResult == null || !saveResult.isSuccess()) {
            return buildFailedResult(saveResult);
        }
        //
        // .登记服务消费者
        ObjectDO consumerObject = new ObjectDO();
        consumerObject.setType(Center_DataKey_Consumer);
        consumerObject.setObjectID(UUID.randomUUID().toString().replace("-", "").toUpperCase());
        consumerObject.setRefObjectID(saveResult.getResult());
        consumerObject.setRefreshTime(new Date());
        consumerObject.setContent(JsonUtils.converToString(info));
        Result<Boolean> consumerResult = this.dataAdapter.storeObject(consumerObject);
        if (consumerResult == null || !consumerResult.isSuccess() || !consumerResult.getResult()) {
            return buildFailedResult(consumerResult);
        }
        //
        // .请求推送服务提供者列表()
        InterAddress interAddress = new InterAddress(info.getRsfAddress());
        Result<Boolean> requestResult = requestProviders(interAddress, serviceInfo.getBindID(), interAddress.getSechma());
        if (requestResult == null || !requestResult.isSuccess() || !requestResult.getResult()) {
            return buildFailedResult(requestResult);
        }
        //
        // .返回registerID
        ResultDO<String> resultDO = new ResultDO<String>();
        resultDO.setSuccess(true);
        resultDO.setErrorInfo(ErrorCode.OK);
        resultDO.setResult(consumerObject.getObjectID());
        return resultDO;
    }
    //
    /**订阅者心跳*/
    public Result<Boolean> serviceBeat(String registerID, String serviceID) {
        //
        // .获取服务Info
        Result<ObjectDO> objResult = this.dataAdapter.queryObjectByID(registerID);
        ObjectDO registerObj = objResult.getResult();
        if (objResult == null || !objResult.isSuccess() || registerObj == null) {
            return buildFailedResult(objResult);
        }
        boolean testC = Center_DataKey_Consumer.equals(registerObj.getType());
        boolean testP = Center_DataKey_Provider.equals(registerObj.getType());
        if (!(testC || testP)) {
            ResultDO<Boolean> result = new ResultDO<Boolean>();
            result.setErrorInfo(ErrorCode.ServiceTypeFailed_Error);
            result.setSuccess(false);
            return result;
        }
        //
        String serviceObjectID = registerObj.getRefObjectID();
        Result<ObjectDO> serviceResult = this.dataAdapter.queryObjectByID(serviceObjectID);
        if (serviceResult == null || !serviceResult.isSuccess() || serviceResult.getResult() == null) {
            return buildFailedResult(serviceResult);
        }
        ServiceInfo serviceInfo = JsonUtils.converToService(serviceResult.getResult().getContent(), ServiceInfo.class);
        //
        // .检查权限
        AuthInfo authInfo = this.getAuthInfo();
        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, serviceInfo, RsfServiceType.Provider);
        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
            return buildFailedResult(checkResult);
        }
        if (!checkResult.getResult()) {
            ResultDO<Boolean> result = new ResultDO<Boolean>();
            result.setErrorInfo(ErrorCode.AuthCheckFailed_ResultEmpty);
            result.setSuccess(false);
            return result;
        }
        //
        // .执行心跳
        Result<ObjectDO> result = this.dataAdapter.queryObjectByID(registerID);
        Result<Boolean> beatResult = null;
        if (result != null && result.isSuccess() && result.getResult() != null) {
            beatResult = this.dataAdapter.refreshObject(registerID);
        }
        if (beatResult == null) {
            ResultDO<Boolean> newResult = new ResultDO<Boolean>();
            newResult.setSuccess(false);
            newResult.setResult(false);
            newResult.setErrorInfo(ErrorCode.BeatFailed_RefreshResultNull);
            beatResult = newResult;
        }
        return beatResult;
    }
    //
    /**发布服务*/
    public Result<String> publishService(ServiceInfo serviceInfo, ProviderInfo info) throws URISyntaxException {
        //
        // .检查权限
        AuthInfo authInfo = this.getAuthInfo();
        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, serviceInfo, RsfServiceType.Provider);
        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
            return buildFailedResult(checkResult);
        }
        if (!checkResult.getResult()) {
            ResultDO<String> result = new ResultDO<String>();
            result.setErrorInfo(ErrorCode.AuthCheckFailed_ResultEmpty);
            result.setSuccess(false);
            return result;
        }
        //
        // .获取保存或者更新服务信息
        Result<String> saveResult = this.saveService(serviceInfo);
        if (saveResult == null || !saveResult.isSuccess()) {
            return buildFailedResult(saveResult);
        }
        String objID = saveResult.getResult();
        //
        // .登记服务提供者
        ObjectDO providerObject = new ObjectDO();
        providerObject.setType(Center_DataKey_Provider);
        providerObject.setObjectID(UUID.randomUUID().toString().replace("-", "").toUpperCase());
        providerObject.setRefObjectID(objID);
        providerObject.setRefreshTime(new Date());
        providerObject.setContent(JsonUtils.converToString(info));
        Result<Boolean> providerResult = this.dataAdapter.storeObject(providerObject);
        if (providerResult == null || !providerResult.isSuccess() || !providerResult.getResult()) {
            return buildFailedResult(providerResult);
        }
        //
        // .查询订阅者列表
        QueryOption opt = new QueryOption();
        opt.setObjectType(Center_DataKey_Consumer);//尝试过滤结果,只保留Consumer数据
        Result<List<ObjectDO>> refList = this.dataAdapter.queryObjectListByID(objID, opt);
        if (refList == null || !refList.isSuccess()) {
            return buildFailedResult(refList);
        }
        List<ObjectDO> allList = refList.getResult();
        List<InterAddress> consumerList = filterConsumerList(allList);
        //
        // .因为多协议关系，需要为每个服务订阅者建立一个可推送的地址列表
        Map<InterAddress, List<InterAddress>> readyToPushMap = new HashMap<InterAddress, List<InterAddress>>();
        for (InterAddress consumer : consumerList) {
            List<String> newHosts = info.getRsfAddress();
            List<InterAddress> newHostSet = new ArrayList<InterAddress>();
            if (newHosts != null) {
                for (String host : newHosts) {
                    InterAddress interAddress = new InterAddress(host);
                    if (consumer.getSechma().equalsIgnoreCase(interAddress.getSechma())) {
                        newHostSet.add(interAddress);
                    }
                }
            }
            readyToPushMap.put(consumer, newHostSet);
        }
        //
        // .推送新的提供者地址
        if (!readyToPushMap.isEmpty()) {
            String serviceID = serviceInfo.getBindID();
            for (InterAddress toPush : readyToPushMap.keySet()) {
                List<InterAddress> pushData = readyToPushMap.get(toPush);
                boolean result = this.rsfPusher.appendAddress(serviceID, pushData, Arrays.asList(toPush)); // 第一次尝试
                if (!result) {
                    result = this.rsfPusher.appendAddress(serviceID, pushData, Arrays.asList(toPush));     // 第二次尝试
                    if (!result) {
                        result = this.rsfPusher.appendAddress(serviceID, pushData, Arrays.asList(toPush)); // 第三次尝试
                    }
                }
                //
            }
        }
        //
        // .返回registerID
        ResultDO<String> resultDO = new ResultDO<String>();
        resultDO.setSuccess(true);
        resultDO.setErrorInfo(ErrorCode.OK);
        resultDO.setResult(providerObject.getObjectID());
        return resultDO;
    }
    //
    /**删除订阅*/
    public Result<Boolean> removeRegister(String registerID, String serviceID) throws URISyntaxException {
        //
        // .获取服务Info
        Result<ObjectDO> objResult = this.dataAdapter.queryObjectByID(registerID);
        if (objResult == null || !objResult.isSuccess() || objResult.getResult() == null) {
            return buildFailedResult(objResult);
        }
        boolean testC = Center_DataKey_Consumer.equals(objResult.getResult().getType());
        boolean testP = Center_DataKey_Provider.equals(objResult.getResult().getType());
        if (!(testC || testP)) {
            ResultDO<Boolean> result = new ResultDO<Boolean>();
            result.setErrorInfo(ErrorCode.ServiceTypeFailed_Error);
            result.setSuccess(false);
            return result;
        }
        //
        String serviceObjectID = objResult.getResult().getRefObjectID();
        Result<ObjectDO> serviceResult = this.dataAdapter.queryObjectByID(serviceObjectID);
        if (serviceResult == null || !serviceResult.isSuccess() || serviceResult.getResult() == null) {
            return buildFailedResult(serviceResult);
        }
        ServiceInfo serviceInfo = JsonUtils.converToService(serviceResult.getResult().getContent(), ServiceInfo.class);
        //
        // .检查权限
        AuthInfo authInfo = this.getAuthInfo();
        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, serviceInfo, RsfServiceType.Provider);
        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
            return buildFailedResult(checkResult);
        }
        if (!checkResult.getResult()) {
            ResultDO<Boolean> result = new ResultDO<Boolean>();
            result.setErrorInfo(ErrorCode.AuthCheckFailed_ResultEmpty);
            result.setSuccess(false);
            return result;
        }
        //
        // .删除订阅(订阅者直接返回结果)
        this.dataAdapter.removeObjectByID(registerID);
        if (!testP) {
            ResultDO<Boolean> result = new ResultDO<Boolean>();
            result.setErrorInfo(ErrorCode.OK);
            result.setSuccess(true);
            return result;
        }
        //
        // .查询订阅者列表
        QueryOption opt = new QueryOption();
        opt.setObjectType(Center_DataKey_Consumer);//尝试过滤结果,只保留Consumer数据
        Result<List<ObjectDO>> refList = this.dataAdapter.queryObjectListByID(serviceObjectID, opt);
        if (refList == null || !refList.isSuccess()) {
            return buildFailedResult(refList);
        }
        List<ObjectDO> allList = refList.getResult();
        List<InterAddress> consumerList = filterConsumerList(allList);
        //
        ProviderInfo providerInfo = JsonUtils.converToService(objResult.getResult().getContent(), ProviderInfo.class);
        List<String> providerHost = providerInfo.getRsfAddress();
        List<InterAddress> newHostSet = new ArrayList<InterAddress>();
        if (providerHost != null) {
            for (String host : providerHost) {
                newHostSet.add(new InterAddress(host));
            }
        }
        //
        // .删除地址
        boolean result = true;
        if (consumerList != null && !consumerList.isEmpty()) {
            result = this.rsfPusher.removeAddress(serviceID, newHostSet, consumerList);         // 第一次尝试
            if (!result) {
                result = this.rsfPusher.removeAddress(serviceID, newHostSet, consumerList);     // 第二次尝试
                if (!result) {
                    result = this.rsfPusher.removeAddress(serviceID, newHostSet, consumerList); // 第三次尝试
                }
            }
        }
        //
        ResultDO<Boolean> finalResult = new ResultDO<Boolean>();
        finalResult.setSuccess(true);
        finalResult.setResult(true);
        if (!result) {
            finalResult.setSuccess(false);
            finalResult.setResult(false);
            finalResult.setErrorInfo(ErrorCode.PushAddressFailed_TooBusy);
        }
        return finalResult;
    }
    //
    /** 请求Center做一次全量推送 */
    public Result<Boolean> requestProviders(InterAddress rsfAddress, String registerID, String serviceID, String protocol) {
        //
        // .检查registerID是否为订阅者
        Result<ObjectDO> consumerResult = this.dataAdapter.queryObjectByID(registerID);
        if (consumerResult == null || !consumerResult.isSuccess() || consumerResult.getResult() == null) {
            return buildFailedResult(consumerResult);
        }
        if (!Center_DataKey_Consumer.equals(consumerResult.getResult().getType())) {
            ResultDO<Boolean> result = new ResultDO<Boolean>();
            result.setErrorInfo(ErrorCode.ServiceTypeFailed_Error);
            result.setSuccess(false);
            return result;
        }
        this.dataAdapter.refreshObject(registerID);
        //
        // .向目标请求推送地址
        return requestProviders(rsfAddress, serviceID, protocol);
    }
    //
    /** 查询提供者列表 */
    public Result<List<String>> queryProviders(String registerID, String serviceID, String protocol) {
        //
        // .检查registerID是否为订阅者
        Result<ObjectDO> consumerResult = this.dataAdapter.queryObjectByID(registerID);
        if (consumerResult == null || !consumerResult.isSuccess() || consumerResult.getResult() == null) {
            return buildFailedResult(consumerResult);
        }
        if (!Center_DataKey_Consumer.equals(consumerResult.getResult().getType())) {
            ResultDO<List<String>> result = new ResultDO<List<String>>();
            result.setErrorInfo(ErrorCode.ServiceTypeFailed_Error);
            result.setSuccess(false);
            return result;
        }
        this.dataAdapter.refreshObject(registerID);
        //
        // .查询提供者列表
        QueryOption opt = new QueryOption();
        opt.setObjectType(Center_DataKey_Provider);//尝试过滤结果,只保留Provider数据
        String serviceObjectID = Center_DataKey_Service + serviceID;
        Result<List<ObjectDO>> refList = this.dataAdapter.queryObjectListByID(serviceObjectID, opt);
        if (refList == null || !refList.isSuccess()) {
            return buildFailedResult(refList);
        }
        List<ObjectDO> providerDataList = refList.getResult();
        List<InterAddress> providerList = this.filterProviderList(providerDataList, protocol);
        if (providerList == null) {
            ResultDO<List<String>> result = new ResultDO<List<String>>();
            result.setSuccess(false);
            result.setErrorInfo(ErrorCode.EmptyResult);//空数据
            return result;
        }
        //
        // .返回提供者列表
        List<String> allList = new ArrayList<String>();
        for (InterAddress provider : providerList) {
            if (provider == null) {
                continue;
            }
        }
        ResultDO<List<String>> result = new ResultDO<List<String>>();
        result.setSuccess(true);
        result.setResult(allList);
        result.setErrorInfo(ErrorCode.OK);
        return result;
    }
    /* 对订阅做请求全量推送提供者列表 */
    private Result<Boolean> requestProviders(InterAddress targetRsfAddress, String serviceID, String protocol) {
        //
        // .查询提供者列表
        QueryOption opt = new QueryOption();
        opt.setObjectType(Center_DataKey_Provider);//尝试过滤结果,只保留Provider数据
        String serviceObjectID = Center_DataKey_Service + serviceID;
        Result<List<ObjectDO>> refList = this.dataAdapter.queryObjectListByID(serviceObjectID, opt);
        if (refList == null || !refList.isSuccess()) {
            return buildFailedResult(refList);
        }
        List<ObjectDO> providerDataList = refList.getResult();
        List<InterAddress> providerList = this.filterProviderList(providerDataList, protocol);
        //
        // .推送提供者地址(三次尝试),即使全部失败也不用担心,依靠客户端主动拉取来换的最终成功
        boolean result = false;
        if (providerList != null && !providerList.isEmpty()) {
            Result<ObjectDO> serviceResult = this.dataAdapter.queryObjectByID(serviceObjectID);
            if (serviceResult == null || !serviceResult.isSuccess() || serviceResult.getResult() == null) {
                return buildFailedResult(serviceResult);
            }
            List<InterAddress> target = Collections.singletonList(targetRsfAddress);
            result = this.rsfPusher.refreshAddress(serviceID, providerList, target);            // 第一次尝试
            if (!result) {
                result = this.rsfPusher.refreshAddress(serviceID, providerList, target);        // 第二次尝试
                if (!result) {
                    result = this.rsfPusher.refreshAddress(serviceID, providerList, target);    // 第三次尝试
                }
            }
        }
        //
        // .返回结果
        ResultDO<Boolean> requestResult = new ResultDO<Boolean>();
        requestResult.setSuccess(true);
        if (!result) {
            requestResult.setResult(false);
            requestResult.setErrorInfo(ErrorCode.PushAddressFailed_TooBusy);
        } else {
            requestResult.setResult(true);
            requestResult.setErrorInfo(ErrorCode.OK);
        }
        return requestResult;
    }
}