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
package net.hasor.registry.access;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.InstanceInfo;
import net.hasor.registry.RsfCenterRegister;
import net.hasor.registry.RsfCenterResult;
import net.hasor.registry.access.adapter.AuthQuery;
import net.hasor.registry.access.domain.*;
import net.hasor.registry.access.manager.PublishManager;
import net.hasor.registry.access.manager.QueryManager;
import net.hasor.registry.domain.ConsumerPublishInfo;
import net.hasor.registry.domain.ProviderPublishInfo;
import net.hasor.registry.domain.PublishInfo;
import net.hasor.registry.trace.TraceUtil;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.domain.RsfServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
/**
 * 客户端注册中心接口{@link RsfCenterRegister}实现类，负责接收来自客户端的请求调用。
 * (这个类做的最多的是输入输出校验)
 * @version : 2015年6月8日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class RsfCenterRegisterProvider implements RsfCenterRegister {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private AuthQuery      authQuery;
    @Inject
    private RsfRequest     rsfRequest;
    @Inject
    private QueryManager   serviceManager;
    @Inject
    private PublishManager publishManager;
    //
    //
    /**服务信息*/
    private ServiceInfo getServiceInfo(PublishInfo info) {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setBindID(info.getBindID());
        serviceInfo.setBindGroup(info.getBindGroup());
        serviceInfo.setBindName(info.getBindName());
        serviceInfo.setBindVersion(info.getBindVersion());
        return serviceInfo;
    }
    /**发布服务*/
    @Override
    public RsfCenterResult<Void> registerProvider(InstanceInfo instance, ProviderPublishInfo info) {
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.setInstanceID(instance.getInstanceID());
        providerInfo.setUnitName(instance.getUnitName());
        providerInfo.setClientTimeout(info.getClientTimeout());
        providerInfo.setQueueMaxSize(info.getQueueMaxSize());
        providerInfo.setSerializeType(info.getSerializeType());
        providerInfo.setSharedThreadPool(info.isSharedThreadPool());
        providerInfo.setRsfAddress(Arrays.asList(info.getTargetList().split(",")));
        ServiceInfo serviceInfo = getServiceInfo(info);
        return this.register(serviceInfo, providerInfo, RsfServiceType.Provider);
    }
    /**订阅服务*/
    @Override
    public RsfCenterResult<Void> registerConsumer(InstanceInfo instance, ConsumerPublishInfo info) {
        ConsumerInfo consumerInfo = new ConsumerInfo();
        consumerInfo.setInstanceID(instance.getInstanceID());
        consumerInfo.setUnitName(instance.getUnitName());
        consumerInfo.setClientTimeout(info.getClientTimeout());
        consumerInfo.setMaximumRequestSize(info.getClientMaximumRequest());
        consumerInfo.setMessage(info.getMessage());
        consumerInfo.setSerializeType(info.getSerializeType());
        consumerInfo.setRsfAddress(info.getTargetList());
        ServiceInfo serviceInfo = getServiceInfo(info);
        return this.register(serviceInfo, consumerInfo, RsfServiceType.Consumer);
    }
    //
    //
    private RsfCenterResult<Void> register(ServiceInfo serviceInfo, Object info, RsfServiceType type) {
        // .检查权限
        AuthBean authInfo = (AuthBean) this.rsfRequest.getAttribute(RsfCenterConstants.Center_Request_AuthInfo);
        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, serviceInfo, type);
        if (checkResult == null || !checkResult.isSuccess() || checkResult.getResult() == null) {
            RsfCenterResultDO<Void> centerResult = new RsfCenterResultDO<Void>();
            centerResult.setSuccess(false);
            if (checkResult != null && checkResult.getErrorInfo() != null) {
                centerResult.setErrorCode(checkResult.getErrorInfo().getCodeType());
                centerResult.setErrorMessage(checkResult.getErrorInfo().getMessage());
            }
            return centerResult;
        }
        if (!checkResult.getResult()) {
            RsfCenterResultDO<Void> centerResult = new RsfCenterResultDO<Void>();
            centerResult.setSuccess(false);
            centerResult.setErrorCode(ErrorCode.AuthCheckFailed_ResultEmpty.getCodeType());
            centerResult.setErrorMessage(ErrorCode.AuthCheckFailed_ResultEmpty.getMessage());
            return centerResult;
        }
        //
        RsfCenterResultDO<Void> centerResult = new RsfCenterResultDO<Void>();
        centerResult.setMessageID(this.rsfRequest.getRequestID());
        InterAddress remoteRsfAddress = this.rsfRequest.getRemoteAddress();
        try {
            // .发布服务,并获取结果
            Result<Void> result = null;
            if (RsfServiceType.Provider == type && info instanceof ProviderInfo) {
                result = this.publishManager.publishService(serviceInfo, (ProviderInfo) info);
            } else if (RsfServiceType.Consumer == type && info instanceof ConsumerInfo) {
                result = this.publishManager.publishConsumer(serviceInfo, (ConsumerInfo) info);
            } else {
                centerResult.setErrorCode(ErrorCode.EmptyResult.getCodeType());
                centerResult.setErrorMessage("registerType mast in Provider or Consumer.");
                logger.error(LogUtils.create("ERROR_100_00001")//
                        .addLog("traceID", TraceUtil.getTraceID())//
                        .addLog("errorCode", centerResult.getErrorCode())//
                        .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                        .addLog("serviceID", serviceInfo.getBindID())//
                        .addLog("registerType", type)//
                        .toJson());
                return centerResult;
            }
            // .判断异常
            if (!result.isSuccess()) {
                centerResult.setSuccess(false);
                ErrorCode errorInfo = result.getErrorInfo();
                centerResult.setErrorCode(errorInfo.getCodeType());
                centerResult.setErrorMessage(errorInfo.getMessage());
                logger.error(LogUtils.create("ERROR_100_00002")//
                        .addLog("traceID", TraceUtil.getTraceID())//
                        .addLog("errorCode", centerResult.getErrorCode())//
                        .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                        .addLog("serviceID", serviceInfo.getBindID())//
                        .addLog("registerType", type)//
                        .toJson());
            } else {
                centerResult.setSuccess(true);
                centerResult.setErrorCode(ErrorCode.OK.getCodeType());
            }
        } catch (Throwable e) {
            centerResult.setSuccess(false);
            centerResult.setErrorCode(ErrorCode.Exception.getCodeType());
            centerResult.setErrorMessage(e.getMessage());
            logger.error(LogUtils.create("ERROR_100_00003")//
                    .addLog("traceID", TraceUtil.getTraceID())//
                    .addLog("errorCode", centerResult.getErrorCode())//
                    .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                    .addLog("serviceID", serviceInfo.getBindID())//
                    .addLog("registerType", type)//
                    .addLog("error", e.getMessage())//
                    .toJson(), e);
        }
        return centerResult;
    }
    //
    //
    //
    /**服务下线*/
    @Override
    public RsfCenterResult<Void> unRegister(InstanceInfo instance, String serviceID) {
        RsfCenterResultDO<Void> centerResult = new RsfCenterResultDO<Void>();
        centerResult.setMessageID(this.rsfRequest.getRequestID());
        InterAddress remoteRsfAddress = this.rsfRequest.getRemoteAddress();
        try {
            // .判断异常
            Result<Void> result = this.publishManager.removeRegister(instance.getInstanceID(), serviceID);
            if (!result.isSuccess()) {
                centerResult.setSuccess(false);
                ErrorCode errorInfo = result.getErrorInfo();
                centerResult.setErrorCode(errorInfo.getCodeType());
                centerResult.setErrorMessage(errorInfo.getMessage());
                logger.error(LogUtils.create("ERROR_100_00101")//
                        .addLog("traceID", TraceUtil.getTraceID())//
                        .addLog("errorCode", centerResult.getErrorCode())//
                        .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                        .addLog("serviceID", serviceID)//
                        .toJson());
            } else {
                centerResult.setSuccess(true);
                centerResult.setErrorCode(ErrorCode.OK.getCodeType());
            }
        } catch (Throwable e) {
            centerResult.setSuccess(false);
            centerResult.setErrorCode(ErrorCode.Exception.getCodeType());
            centerResult.setErrorMessage(e.getMessage());
            logger.error(LogUtils.create("ERROR_100_00102")//
                    .addLog("traceID", TraceUtil.getTraceID())//
                    .addLog("errorCode", centerResult.getErrorCode())//
                    .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                    .addLog("serviceID", serviceID)//
                    .addLog("error", e.getMessage())//
                    .toJson(), e);
        }
        return centerResult;
    }
    //
    @Override
    public RsfCenterResult<List<String>> pullProviders(InstanceInfo instance, String serviceID) {
        RsfCenterResultDO<List<String>> centerResult = new RsfCenterResultDO<List<String>>();
        centerResult.setMessageID(this.rsfRequest.getRequestID());
        InterAddress remoteRsfAddress = this.rsfRequest.getRemoteAddress();
        try {
            // .判断异常
            Result<List<String>> result = this.serviceManager.queryProviders(serviceID, instance);
            if (!result.isSuccess()) {
                centerResult.setSuccess(false);
                ErrorCode errorInfo = result.getErrorInfo();
                centerResult.setErrorCode(errorInfo.getCodeType());
                centerResult.setErrorMessage(errorInfo.getMessage());
                logger.error(LogUtils.create("ERROR_100_00301")//
                        .addLog("traceID", TraceUtil.getTraceID())//
                        .addLog("errorCode", centerResult.getErrorCode())//
                        .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                        .addLog("serviceID", serviceID)//
                        .toJson());
            } else {
                centerResult.setSuccess(true);
                centerResult.setErrorCode(ErrorCode.OK.getCodeType());
                centerResult.setResult(result.getResult());
            }
        } catch (Throwable e) {
            centerResult.setSuccess(false);
            centerResult.setErrorCode(ErrorCode.Exception.getCodeType());
            centerResult.setErrorMessage(e.getMessage());
            logger.error(LogUtils.create("ERROR_100_00302")//
                    .addLog("traceID", TraceUtil.getTraceID())//
                    .addLog("errorCode", centerResult.getErrorCode())//
                    .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                    .addLog("serviceID", serviceID)//
                    .addLog("error", e.getMessage())//
                    .toJson(), e);
        }
        return centerResult;
    }
    @Override
    public RsfCenterResult<Boolean> requestPushProviders(InstanceInfo instance, String serviceID) {
        RsfCenterResultDO<Boolean> centerResult = new RsfCenterResultDO<Boolean>();
        centerResult.setMessageID(this.rsfRequest.getRequestID());
        InterAddress remoteRsfAddress = this.rsfRequest.getRemoteAddress();
        try {
            // .判断异常
            Result<Void> result = this.serviceManager.requestProviders(instance, serviceID);
            if (!result.isSuccess()) {
                centerResult.setSuccess(false);
                ErrorCode errorInfo = result.getErrorInfo();
                centerResult.setErrorCode(errorInfo.getCodeType());
                centerResult.setErrorMessage(errorInfo.getMessage());
                logger.error(LogUtils.create("ERROR_100_00401")//
                        .addLog("traceID", TraceUtil.getTraceID())//
                        .addLog("errorCode", centerResult.getErrorCode())//
                        .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                        .addLog("serviceID", serviceID)//
                        .toJson());
            } else {
                centerResult.setSuccess(true);
                centerResult.setErrorCode(ErrorCode.OK.getCodeType());
                centerResult.setResult(true);
            }
        } catch (Throwable e) {
            centerResult.setSuccess(false);
            centerResult.setErrorCode(ErrorCode.Exception.getCodeType());
            centerResult.setErrorMessage(e.getMessage());
            logger.error(LogUtils.create("ERROR_100_00402")//
                    .addLog("traceID", TraceUtil.getTraceID())//
                    .addLog("errorCode", centerResult.getErrorCode())//
                    .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                    .addLog("serviceID", serviceID)//
                    .addLog("error", e.getMessage())//
                    .toJson(), e);
        }
        return centerResult;
    }
}