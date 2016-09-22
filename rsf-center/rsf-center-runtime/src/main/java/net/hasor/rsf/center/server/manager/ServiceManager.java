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
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.server.AuthQuery;
import net.hasor.rsf.center.server.DataAdapter;
import net.hasor.rsf.center.server.QueryOption;
import net.hasor.rsf.center.server.domain.*;
import net.hasor.rsf.center.server.pushing.RsfPusher;
import net.hasor.rsf.center.server.utils.JsonUtils;
import net.hasor.rsf.domain.RsfServiceType;
import org.more.bizcommon.log.LogUtils;
import org.more.util.CommonCodeUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.*;
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
        ResultDO<T> result = new ResultDO<>();
        if (resultInfo == null || resultInfo.getResult() == null) {
            result.setErrorInfo(ErrorCode.EmptyResult);
        } else {
            result.setErrorInfo(resultInfo.getErrorInfo());
            result.setThrowable(resultInfo.getThrowable());
        }
        result.setSuccess(false);
        return result;
    }
    private String eval(String objectKey) {
        try {
            return CommonCodeUtils.MD5.getMD5(objectKey + this.envConfig.getSaltValue());//Key要加盐
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    /* 计算registerID */
    private Result<String> evalRegisterID(String objectKey, RsfServiceType serviceType) {
        // .计算registerID( 相同的入参,会产出相同的registerID )
        String registerID = eval(objectKey);
        if (StringUtils.isNotBlank(registerID) && RsfServiceType.Provider == serviceType) {
            registerID = RsfCenterConstants.Center_DataKey_Provider + registerID;
        } else if (StringUtils.isNotBlank(registerID) && RsfServiceType.Consumer == serviceType) {
            registerID = RsfCenterConstants.Center_DataKey_Consumer + registerID;
        } else {
            ResultDO<String> resultDO = new ResultDO<>();
            resultDO.setSuccess(false);
            if (StringUtils.isBlank(registerID)) {
                resultDO.setErrorInfo(ErrorCode.BuildRegisterIDFailed_Null);
            } else {
                resultDO.setErrorInfo(ErrorCode.ServiceTypeFailed_Null);
            }
            resultDO.setResult(registerID);
            return resultDO;
        }
        // .返回结果
        ResultDO<String> resultDO = new ResultDO<>();
        resultDO.setSuccess(true);
        resultDO.setErrorInfo(ErrorCode.OK);
        resultDO.setResult(registerID);
        return resultDO;
    }
    /* 根据信息重新计算ObjectID,并且校验 registerID 是否正确。*/
    private Result<String> checkAndEvalObjectID(InterAddress rsfAddress, String registerID, String serviceID) {
        rsfAddress = Hasor.assertIsNotNull(rsfAddress, "param rsfAddress is null.");
        registerID = Hasor.assertIsNotNull(registerID, "param registerID is null.");
        serviceID = Hasor.assertIsNotNull(serviceID, "param serviceID is null.");
        //
        // .根据传入的参数重新计算registerID
        String evalRegisterID = null;
        String oriObjectKey = serviceID + "@" + rsfAddress.getHostPort();
        String preKey = "";
        if (StringUtils.startsWith(registerID, RsfCenterConstants.Center_DataKey_Consumer)) {
            preKey = RsfCenterConstants.Center_DataKey_Consumer;
        } else if (StringUtils.startsWith(registerID, RsfCenterConstants.Center_DataKey_Provider)) {
            preKey = RsfCenterConstants.Center_DataKey_Provider;
        } else {
            ResultDO<String> result = new ResultDO<>();
            result.setSuccess(false);
            result.setErrorInfo(ErrorCode.ServiceTypeFailed_Null);
            return result;
        }
        oriObjectKey = preKey + oriObjectKey;
        evalRegisterID = preKey + eval(oriObjectKey);
        //
        // .验证registerID
        if (!StringUtils.equals(registerID, evalRegisterID)) {
            ResultDO<String> result = new ResultDO<>();
            result.setSuccess(false);
            result.setErrorInfo(ErrorCode.RegisterCheckInvalid);
            return result;
        }
        //
        ResultDO<String> result = new ResultDO<>();
        result.setSuccess(true);
        result.setResult(oriObjectKey);
        result.setErrorInfo(ErrorCode.OK);
        return result;
    }
    //
    //
    //
    /**订阅服务*/
    public Result<String> publishConsumer(InterAddress rsfAddress, ServiceInfo serviceInfo, ConsumerInfo info) {
        rsfAddress = Hasor.assertIsNotNull(rsfAddress, "param InterAddress is null.");
        serviceInfo = Hasor.assertIsNotNull(serviceInfo, "param ServiceInfo is null.");
        info = Hasor.assertIsNotNull(info, "param ConsumerInfo is null.");
        AuthInfo authInfo = this.getAuthInfo();
        //
        String serviceID = serviceInfo.getBindID();
        String serviceObjectKey = RsfCenterConstants.Center_DataKey_Service + serviceID;
        String consumerObjectKey = RsfCenterConstants.Center_DataKey_Consumer + serviceID + "@" + rsfAddress.getHostPort();
        //
        // .检查权限
        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, rsfAddress, serviceInfo, RsfServiceType.Consumer);
        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
            return buildFailedResult(checkResult);
        }
        if (!checkResult.getResult()) {
            ResultDO<String> result = new ResultDO<>();
            result.setErrorInfo(ErrorCode.AuthCheckFailed_ResultEmpty);
            result.setSuccess(false);
            return result;
        }
        //
        // .获取保存的服务信息
        Result<ObjectDO> resultInfo = this.dataAdapter.queryObjectByID(serviceObjectKey);
        if (resultInfo == null || !resultInfo.isSuccess()) {
            return buildFailedResult(resultInfo);
        }
        ObjectDO oldServiceInfo = resultInfo.getResult();
        if (oldServiceInfo == null) {
            ResultDO<String> result = new ResultDO<>();
            result.setSuccess(false);
            result.setErrorInfo(ErrorCode.SubscribeServiceFailed_Undefined);//服务未定义
            return result;
        } else {
            String content = oldServiceInfo.getContent();
            serviceInfo = JsonUtils.converToService(content, ServiceInfo.class);
        }
        //
        // .登记服务消费者
        ObjectDO consumerObject = new ObjectDO();
        consumerObject.setObjectID(consumerObjectKey);
        consumerObject.setType(RsfCenterConstants.Center_DataKey_Consumer);
        consumerObject.setRefObjectID(serviceObjectKey);
        consumerObject.setRefreshTime(new Date());
        consumerObject.setContent(JsonUtils.converToString(info));
        Result<Boolean> consumerResult = this.dataAdapter.storeObject(consumerObject);
        if (consumerResult == null || !consumerResult.isSuccess()) {
            return buildFailedResult(consumerResult);
        }
        //
        // .刷新更新时间
        this.dataAdapter.refreshObject(consumerObjectKey);
        //
        // .请求推送服务提供者列表
        Result<Boolean> requestResult = requestProviders(rsfAddress, serviceID);
        if (requestResult == null || !requestResult.isSuccess()) {
            return buildFailedResult(requestResult);
        }
        //
        // .返回registerID
        return evalRegisterID(consumerObjectKey, RsfServiceType.Consumer);
    }
    /**订阅者心跳*/
    public Result<Boolean> serviceBeat(InterAddress rsfAddress, String registerID, String serviceID) {
        // .重新计算ObjectID,并且校验registerID有效性。
        Result<String> objectIDResult = this.checkAndEvalObjectID(rsfAddress, registerID, serviceID);
        String oriObjectKey = null;/* 提供者 or 订阅者 ObjectID */
        if (!objectIDResult.isSuccess()) {
            ResultDO<Boolean> result = new ResultDO<>();
            result.setSuccess(false);
            result.setResult(false);
            result.setErrorInfo(objectIDResult.getErrorInfo());
            return result;
        } else {
            oriObjectKey = objectIDResult.getResult();
        }
        //
        // .获取服务Info
        Result<ObjectDO> serviceResult = this.dataAdapter.queryObjectByID(RsfCenterConstants.Center_DataKey_Service + serviceID);
        if (serviceResult == null || !serviceResult.isSuccess() || serviceResult.getResult() == null) {
            return buildFailedResult(serviceResult);
        }
        ServiceInfo serviceInfo = JsonUtils.converToService(serviceResult.getResult().getContent(), ServiceInfo.class);
        //
        // .检查权限
        AuthInfo authInfo = this.getAuthInfo();
        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, rsfAddress, serviceInfo, RsfServiceType.Provider);
        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
            return buildFailedResult(checkResult);
        }
        if (!checkResult.getResult()) {
            ResultDO<Boolean> result = new ResultDO<>();
            result.setErrorInfo(ErrorCode.AuthCheckFailed_ResultEmpty);
            result.setSuccess(false);
            return result;
        }
        //
        // .执行心跳
        Result<ObjectDO> result = this.dataAdapter.queryObjectByID(oriObjectKey);
        Result<Boolean> beatResult = null;
        if (result != null && result.isSuccess() && result.getResult() != null) {
            beatResult = this.dataAdapter.refreshObject(oriObjectKey);
        }
        if (beatResult == null) {
            ResultDO<Boolean> newResult = new ResultDO<>();
            newResult.setSuccess(false);
            newResult.setResult(false);
            newResult.setErrorInfo(ErrorCode.BeatFailed_RefreshResultNull);
            beatResult = newResult;
        }
        return beatResult;
    }
    //
    //
    //
    /* 过滤出订阅者列表 */
    private List<InterAddress> filterConsumerList(String serviceID, List<ObjectDO> allObjectList) {
        List<InterAddress> targets = new ArrayList<>();
        if (allObjectList != null && !allObjectList.isEmpty()) {
            for (ObjectDO objectDO : allObjectList) {
                // - .过滤数据,只保留订阅者
                if (!StringUtils.equalsIgnoreCase(objectDO.getType(), RsfCenterConstants.Center_DataKey_Consumer)) {
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
                    this.logger.error(LogUtils.create("ERROR_200_00402")//
                            .logException(e)//
                            .addLog("objectID", objectDO.getObjectID())//
                            .addLog("serviceID", serviceID)//
                            .toJson());
                }
            }
        }
        return targets;
    }
    /**发布服务*/
    public Result<String> publishService(InterAddress rsfAddress, ServiceInfo serviceInfo, ProviderInfo info) {
        rsfAddress = Hasor.assertIsNotNull(rsfAddress, "param InterAddress is null.");
        serviceInfo = Hasor.assertIsNotNull(serviceInfo, "param ServiceInfo is null.");
        info = Hasor.assertIsNotNull(info, "param ProviderInfo is null.");
        AuthInfo authInfo = this.getAuthInfo();
        //
        String serviceID = serviceInfo.getBindID();
        String serviceObjectKey = RsfCenterConstants.Center_DataKey_Service + serviceID;
        String providerObjectKey = RsfCenterConstants.Center_DataKey_Provider + serviceID + "@" + rsfAddress.getHostPort();
        //
        // .检查权限
        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, rsfAddress, serviceInfo, RsfServiceType.Provider);
        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
            return buildFailedResult(checkResult);
        }
        if (!checkResult.getResult()) {
            ResultDO<String> result = new ResultDO<>();
            result.setErrorInfo(ErrorCode.AuthCheckFailed_ResultEmpty);
            result.setSuccess(false);
            return result;
        }
        //
        // .获取保存的服务信息
        Result<ObjectDO> resultInfo = this.dataAdapter.queryObjectByID(serviceObjectKey);
        if (resultInfo == null || !resultInfo.isSuccess()) {
            return buildFailedResult(resultInfo);
        }
        //
        // .保存或更新服务信息
        ObjectDO oldServiceInfo = resultInfo.getResult();
        if (oldServiceInfo == null) {
            ObjectDO newObjectDO = new ObjectDO();//服务对象
            newObjectDO.setObjectID(serviceObjectKey);//关联
            newObjectDO.setType(RsfCenterConstants.Center_DataKey_Service);
            newObjectDO.setRefreshTime(new Date());
            newObjectDO.setContent(JsonUtils.converToString(serviceInfo));
            Result<Boolean> storeResult = this.dataAdapter.storeObject(newObjectDO);
            if (storeResult == null || !storeResult.isSuccess()) {
                return buildFailedResult(storeResult);
            }
            if (storeResult.getResult() == null || !storeResult.getResult()) {
                ResultDO<String> result = new ResultDO<>();
                result.setSuccess(false);
                result.setErrorInfo(ErrorCode.PublishServiceFailed_StoreInfo);
                return result;
            }
        } else {
            String content = oldServiceInfo.getContent();
            serviceInfo = JsonUtils.converToService(content, ServiceInfo.class);
        }
        //
        // .登记服务提供者
        ObjectDO providerObject = new ObjectDO();
        providerObject.setObjectID(providerObjectKey);
        providerObject.setType(RsfCenterConstants.Center_DataKey_Provider);
        providerObject.setRefObjectID(serviceObjectKey);
        providerObject.setRefreshTime(new Date());
        providerObject.setContent(JsonUtils.converToString(info));
        Result<Boolean> providerResult = this.dataAdapter.storeObject(providerObject);
        if (providerResult == null || !providerResult.isSuccess()) {
            return buildFailedResult(providerResult);
        }
        //
        // .刷新更新时间
        this.dataAdapter.refreshObject(serviceObjectKey);
        this.dataAdapter.refreshObject(providerObjectKey);
        //
        // .查询订阅者列表
        QueryOption opt = new QueryOption();
        opt.setObjectType(RsfCenterConstants.Center_DataKey_Consumer);//尝试过滤结果,只保留Consumer数据
        Result<List<ObjectDO>> refList = this.dataAdapter.queryObjectListByID(serviceObjectKey, opt);
        if (refList == null || !refList.isSuccess()) {
            return buildFailedResult(refList);
        }
        List<ObjectDO> allList = refList.getResult();
        List<InterAddress> consumerList = filterConsumerList(serviceID, allList);
        Collection<InterAddress> newHostSet = Collections.singletonList(rsfAddress);
        //
        // .推送新的提供者地址
        if (consumerList != null && !consumerList.isEmpty()) {
            boolean result = this.rsfPusher.appendAddress(serviceID, newHostSet, consumerList); // 第一次尝试
            if (!result) {
                result = this.rsfPusher.appendAddress(serviceID, newHostSet, consumerList);     // 第二次尝试
                if (!result) {
                    result = this.rsfPusher.appendAddress(serviceID, newHostSet, consumerList); // 第三次尝试
                }
            }
        }
        //
        // .返回registerID
        return evalRegisterID(providerObjectKey, RsfServiceType.Provider);
    }
    /**删除订阅*/
    public Result<Boolean> removeRegister(InterAddress rsfAddress, String registerID, String serviceID) {
        // .重新计算ObjectID,并且校验registerID有效性。
        Result<String> objectIDResult = this.checkAndEvalObjectID(rsfAddress, registerID, serviceID);
        String oriObjectKey = null;
        if (!objectIDResult.isSuccess()) {
            ResultDO<Boolean> result = new ResultDO<>();
            result.setSuccess(false);
            result.setResult(false);
            result.setErrorInfo(objectIDResult.getErrorInfo());
            return result;
        } else {
            oriObjectKey = objectIDResult.getResult();
        }
        //
        // .获取服务Info
        Result<ObjectDO> serviceResult = this.dataAdapter.queryObjectByID(RsfCenterConstants.Center_DataKey_Service + serviceID);
        if (serviceResult == null || !serviceResult.isSuccess() || serviceResult.getResult() == null) {
            return buildFailedResult(serviceResult);
        }
        ServiceInfo serviceInfo = JsonUtils.converToService(serviceResult.getResult().getContent(), ServiceInfo.class);
        //
        // .检查权限
        AuthInfo authInfo = this.getAuthInfo();
        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, rsfAddress, serviceInfo, RsfServiceType.Provider);
        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
            return buildFailedResult(checkResult);
        }
        if (!checkResult.getResult()) {
            ResultDO<Boolean> result = new ResultDO<>();
            result.setErrorInfo(ErrorCode.AuthCheckFailed_ResultEmpty);
            result.setSuccess(false);
            return result;
        }
        //
        // .删除订阅(订阅者直接返回结果)
        Result<Boolean> removeResult = this.dataAdapter.removeObjectByID(oriObjectKey);
        if (!StringUtils.startsWith(oriObjectKey, RsfCenterConstants.Center_DataKey_Provider)) {
            return removeResult;
        }
        //
        // .查询订阅者列表
        QueryOption opt = new QueryOption();
        opt.setObjectType(RsfCenterConstants.Center_DataKey_Consumer);//尝试过滤结果,只保留Consumer数据
        String serviceObjectKey = RsfCenterConstants.Center_DataKey_Service + serviceID;
        Result<List<ObjectDO>> refList = this.dataAdapter.queryObjectListByID(serviceObjectKey, opt);
        if (refList == null || !refList.isSuccess()) {
            return buildFailedResult(refList);
        }
        List<ObjectDO> allList = refList.getResult();
        List<InterAddress> consumerList = filterConsumerList(serviceID, allList);
        Collection<InterAddress> newHostSet = Collections.singletonList(rsfAddress);
        //
        // .提供者下线通知所有订阅者
        boolean result = false;
        if (consumerList != null && !consumerList.isEmpty()) {
            result = this.rsfPusher.removeAddress(serviceID, newHostSet, consumerList);         // 第一次尝试
            if (!result) {
                result = this.rsfPusher.removeAddress(serviceID, newHostSet, consumerList);     // 第二次尝试
                if (!result) {
                    result = this.rsfPusher.removeAddress(serviceID, newHostSet, consumerList); // 第三次尝试
                }
            }
        }
        ResultDO<Boolean> finalResult = new ResultDO<>();
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
    //
    //
    /** 请求Center做一次全量推送 */
    public Result<Boolean> requestProviders(InterAddress rsfAddress, String registerID, String serviceID) {
        // .重新计算ObjectID,并且校验registerID有效性。
        Result<String> objectIDResult = this.checkAndEvalObjectID(rsfAddress, registerID, serviceID);
        String oriObjectKey = null;
        if (!objectIDResult.isSuccess()) {
            ResultDO<Boolean> result = new ResultDO<>();
            result.setSuccess(false);
            result.setResult(false);
            result.setErrorInfo(objectIDResult.getErrorInfo());
            return result;
        } else {
            oriObjectKey = objectIDResult.getResult();
        }
        //
        // .刷新更新时间
        this.dataAdapter.refreshObject(oriObjectKey);
        //
        // .向目标请求推送地址
        return requestProviders(rsfAddress, serviceID);
    }
    /** 查询提供者列表 */
    public Result<List<String>> queryProviders(InterAddress rsfAddress, String registerID, String serviceID) {
        // .重新计算ObjectID,并且校验registerID有效性。
        Result<String> objectIDResult = this.checkAndEvalObjectID(rsfAddress, registerID, serviceID);
        String oriObjectKey = null;
        if (!objectIDResult.isSuccess()) {
            ResultDO<List<String>> result = new ResultDO<>();
            result.setSuccess(false);
            result.setErrorInfo(objectIDResult.getErrorInfo());
            return result;
        } else {
            oriObjectKey = objectIDResult.getResult();
        }
        //
        // .刷新更新时间
        this.dataAdapter.refreshObject(oriObjectKey);
        //
        // .查询提供者列表
        QueryOption opt = new QueryOption();
        String serviceKey = RsfCenterConstants.Center_DataKey_Service + serviceID;
        opt.setObjectType(RsfCenterConstants.Center_DataKey_Provider);//尝试过滤结果,只保留Provider数据
        Result<List<ObjectDO>> refList = this.dataAdapter.queryObjectListByID(serviceKey, opt);
        if (refList == null || !refList.isSuccess()) {
            return buildFailedResult(refList);
        }
        List<ObjectDO> providerDataList = refList.getResult();
        List<InterAddress> providerList = this.filterProviderList(serviceID, providerDataList);
        if (providerList == null) {
            ResultDO<List<String>> result = new ResultDO<>();
            result.setSuccess(false);
            result.setErrorInfo(ErrorCode.EmptyResult);//空数据
            return result;
        }
        //
        // .返回提供者列表
        List<String> allList = new ArrayList<>();
        for (InterAddress provider : providerList) {
            if (provider == null) {
                continue;
            }
            allList.add(provider.toHostSchema());
        }
        ResultDO<List<String>> result = new ResultDO<>();
        result.setSuccess(true);
        result.setResult(allList);
        result.setErrorInfo(ErrorCode.OK);
        return result;
    }
    //
    //
    //
    /* 过滤出提供者列表 */
    private List<InterAddress> filterProviderList(String serviceID, List<ObjectDO> allObjectList) {
        List<InterAddress> targets = new ArrayList<>();
        if (allObjectList != null && !allObjectList.isEmpty()) {
            for (ObjectDO objectDO : allObjectList) {
                // - .过滤数据,只保留订阅者
                if (!StringUtils.equalsIgnoreCase(objectDO.getType(), RsfCenterConstants.Center_DataKey_Provider)) {
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
                    targets.add(new InterAddress(providerInfo.getRsfAddress()));
                } catch (Exception e) {
                    this.logger.error(LogUtils.create("ERROR_200_00402")//
                            .logException(e)//
                            .addLog("objectID", objectDO.getObjectID())//
                            .addLog("serviceID", serviceID)//
                            .toJson());
                }
            }
        }
        return targets;
    }
    /* 对订阅做请求全量推送提供者列表 */
    private Result<Boolean> requestProviders(InterAddress targetRsfAddress, String serviceID) {
        //
        // .查询提供者列表
        QueryOption opt = new QueryOption();
        String serviceKey = RsfCenterConstants.Center_DataKey_Service + serviceID;
        opt.setObjectType(RsfCenterConstants.Center_DataKey_Provider);//尝试过滤结果,只保留Provider数据
        Result<List<ObjectDO>> refList = this.dataAdapter.queryObjectListByID(serviceKey, opt);
        if (refList == null || !refList.isSuccess()) {
            return buildFailedResult(refList);
        }
        List<ObjectDO> providerDataList = refList.getResult();
        List<InterAddress> providerList = this.filterProviderList(serviceID, providerDataList);
        //
        // .推送提供者地址(三次尝试),即使全部失败也不用担心,依靠客户端主动拉取来换的最终成功
        boolean result = false;
        if (providerList != null && !providerList.isEmpty()) {
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
        ResultDO<Boolean> requestResult = new ResultDO<>();
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