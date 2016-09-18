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
import org.more.bizcommon.log.LogUtils;
import org.more.util.CommonCodeUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
/**
 * 提供者Manager
 * @version : 2016年9月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class ServiceManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private RsfRequest        rsfRequest;
    @Inject
    private AppContext        appContext;
    @Inject
    private DataAdapter       dataAdapter;
    @Inject
    private RsfPusher         rsfPusher;
    @Inject
    private AuthQuery         authQuery;
    @Inject
    private RsfCenterSettings rsfCenterCfg;
    //
    //
    /**获取授权信息*/
    protected AuthInfo getAuthInfo() {
        return (AuthInfo) rsfRequest.getAttribute(RsfCenterConstants.Center_Request_AuthInfo);
    }
    /**处理失败的情况*/
    protected Result<String> buildFailedResult(Result<?> resultInfo) {
        ResultDO<String> result = new ResultDO<String>();
        if (resultInfo == null || resultInfo.getResult() == null) {
            result.setErrorInfo(ErrorCode.EmptyResult);
        } else {
            result.setErrorInfo(resultInfo.getErrorInfo());
            result.setThrowable(resultInfo.getThrowable());
        }
        result.setSuccess(false);
        return result;
    }
    /** 计算registerID */
    protected Result<String> evalRegisterID(String consumerObjectKey) {
        try {
            String registerID = CommonCodeUtils.MD5.getMD5(consumerObjectKey);
            ResultDO<String> resultDO = new ResultDO<String>();
            resultDO.setSuccess(true);
            resultDO.setErrorInfo(ErrorCode.OK);
            resultDO.setResult(registerID);
            return resultDO;
        } catch (Exception e) {
            ResultDO<String> resultDO = new ResultDO<String>();
            resultDO.setSuccess(false);
            resultDO.setErrorInfo(ErrorCode.Exception);
            resultDO.setThrowable(e);
            return resultDO;
        }
    }
    //
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
        //        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, serviceInfo);
        //        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
        //            return buildFailedResult(checkResult);
        //        }
        //        if (!checkResult.getResult()) {
        //            ResultDO<String> result = new ResultDO<String>();
        //            result.setErrorInfo(ErrorCode.EmptyResult);
        //            result.setSuccess(false);
        //            return result;
        //        }
        //
        // .获取保存的服务信息
        Result<ObjectDO> resultInfo = dataAdapter.queryObjectByID(serviceObjectKey);
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
            Result<Boolean> storeResult = dataAdapter.storeObject(newObjectDO);
            if (storeResult == null || !storeResult.isSuccess()) {
                return buildFailedResult(storeResult);
            }
            if (storeResult.getResult() == null || !storeResult.getResult()) {
                ResultDO<String> result = new ResultDO<String>();
                result.setSuccess(false);
                result.setErrorInfo(ErrorCode.StoreServiceFailed);
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
        opt.setType(RsfCenterConstants.Center_DataKey_Consumer);//尝试过滤结果,只保留Consumer数据
        Result<List<ObjectDO>> refList = this.dataAdapter.queryObjectListByID(serviceObjectKey, opt);
        if (refList == null || !refList.isSuccess()) {
            return buildFailedResult(refList);
        }
        List<ObjectDO> consumerList = refList.getResult();
        Collection<InterAddress> newHostSet = Collections.singletonList(rsfAddress);
        List<InterAddress> targets = new ArrayList<InterAddress>();
        if (consumerList != null && !consumerList.isEmpty()) {
            for (ObjectDO objectDO : consumerList) {
                // - .过滤数据,只保留订阅者
                if (!StringUtils.equalsIgnoreCase(objectDO.getType(), RsfCenterConstants.Center_DataKey_Consumer)) {
                    continue;
                }
                // - .过滤长时间没有心跳的订阅者
                long lastRefreshTime = (objectDO.getRefreshTime() == null) ? System.currentTimeMillis() : objectDO.getRefreshTime().getTime();
                long overRefreshTime = lastRefreshTime + this.rsfCenterCfg.getConsumerExpireTime();
                if (System.currentTimeMillis() >= overRefreshTime) {
                    continue;/*过期了*/
                }
                // - .待推送列表
                try {
                    ConsumerInfo consumerInfo = JsonUtils.converToService(objectDO.getContent(), ConsumerInfo.class);
                    targets.add(new InterAddress(consumerInfo.getRsfAddress()));
                } catch (Exception e) {
                    logger.error(LogUtils.create("ERROR_200_00402")//
                            .logException(e)//
                            .addLog("objectID", objectDO.getObjectID())//
                            .addLog("serviceID", serviceInfo.getBindID())//
                            .toJson());
                }
            }
        }
        //
        // .推送新的提供者地址(三次尝试)
        if (targets != null && !targets.isEmpty()) {
            boolean result = this.rsfPusher.appendAddress(serviceID, newHostSet, targets);
            if (!result) {
                result = this.rsfPusher.appendAddress(serviceID, newHostSet, targets);//二次尝试
                if (!result) {
                    result = this.rsfPusher.appendAddress(serviceID, newHostSet, targets);//三次尝试
                }
            }
        }
        //
        // .返回registerID
        return evalRegisterID(providerObjectKey);
    }
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
        //        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, serviceInfo);
        //        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
        //            return buildFailedResult(checkResult);
        //        }
        //        if (!checkResult.getResult()) {
        //            ResultDO<String> result = new ResultDO<String>();
        //            result.setErrorInfo(ErrorCode.EmptyResult);
        //            result.setSuccess(false);
        //            return result;
        //        }
        //
        // .获取保存的服务信息
        Result<ObjectDO> resultInfo = dataAdapter.queryObjectByID(serviceObjectKey);
        if (resultInfo == null || !resultInfo.isSuccess()) {
            return buildFailedResult(resultInfo);
        }
        ObjectDO oldServiceInfo = resultInfo.getResult();
        if (oldServiceInfo == null) {
            ResultDO<String> result = new ResultDO<String>();
            result.setSuccess(false);
            result.setErrorInfo(ErrorCode.ServiceUndefined);//服务未定义
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
        // .查询提供者列表
        QueryOption opt = new QueryOption();
        opt.setType(RsfCenterConstants.Center_DataKey_Provider);//尝试过滤结果,只保留Provider数据
        Result<List<ObjectDO>> refList = this.dataAdapter.queryObjectListByID(serviceObjectKey, opt);
        if (refList == null || !refList.isSuccess()) {
            return buildFailedResult(refList);
        }
        List<ObjectDO> providerDataList = refList.getResult();
        List<InterAddress> providerList = new ArrayList<InterAddress>();
        if (providerDataList != null && !providerDataList.isEmpty()) {
            for (ObjectDO objectDO : providerDataList) {
                // - .过滤数据,只保留提供者
                if (!StringUtils.equalsIgnoreCase(objectDO.getType(), RsfCenterConstants.Center_DataKey_Provider)) {
                    continue;
                }
                // - .过滤长时间没有心跳的订阅者
                long lastRefreshTime = (objectDO.getRefreshTime() == null) ? System.currentTimeMillis() : objectDO.getRefreshTime().getTime();
                long overRefreshTime = lastRefreshTime + this.rsfCenterCfg.getProviderExpireTime();
                if (System.currentTimeMillis() >= overRefreshTime) {
                    continue;/*过期了*/
                }
                // - .提供者列表
                try {
                    ProviderInfo providerInfo = JsonUtils.converToService(objectDO.getContent(), ProviderInfo.class);
                    providerList.add(new InterAddress(providerInfo.getRsfAddress()));
                } catch (Exception e) {
                    logger.error(LogUtils.create("ERROR_200_00402")//
                            .logException(e)//
                            .addLog("objectID", objectDO.getObjectID())//
                            .addLog("serviceID", serviceInfo.getBindID())//
                            .toJson());
                }
            }
        }
        //
        // .推送提供者地址(三次尝试)
        if (providerList != null && !providerList.isEmpty()) {
            List<InterAddress> target = Collections.singletonList(rsfAddress);
            boolean result = this.rsfPusher.refreshAddress(serviceID, providerList, target);
            if (!result) {
                result = this.rsfPusher.refreshAddress(serviceID, providerList, target);//二次尝试
                if (!result) {
                    result = this.rsfPusher.refreshAddress(serviceID, providerList, target);//三次尝试
                }
            }
        }
        //
        // .返回registerID
        return evalRegisterID(consumerObjectKey);
    }
    //
    /**删除订阅*/
    public Result<Boolean> removeRegister(InterAddress rsfHost, String registerID, String serviceID) {
        return null;
        //  return super.removeTerminal(rsfHost, forBindID, RsfServiceType.Consumer);
    }
    //
    /**订阅者心跳*/
    public Result<Boolean> serviceBeat(InterAddress rsfAddress, String registerID, String serviceID) {
        return null;
    }
    //
    public Result<List<String>> queryProviders(InterAddress rsfAddress, String registerID, String serviceID) {
        return null;
    }
    public Result<Boolean> requestProviders(InterAddress rsfAddress, String registerID, String serviceID) {
        return null;
    }
}