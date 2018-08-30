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
package net.hasor.registry.server;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.client.RsfCenterRegister;
import net.hasor.registry.client.RsfCenterResult;
import net.hasor.registry.client.domain.ConsumerPublishInfo;
import net.hasor.registry.client.domain.ProviderPublishInfo;
import net.hasor.registry.client.domain.ServiceID;
import net.hasor.registry.common.InstanceInfo;
import net.hasor.registry.server.domain.*;
import net.hasor.registry.server.manager.AuthQuery;
import net.hasor.registry.server.manager.PublishManager;
import net.hasor.registry.server.manager.QueryManager;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.domain.RsfServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * 客户端注册中心接口{@link RsfCenterRegister}实现类，负责接收来自客户端的请求调用。
 * (这个类做的最多的是输入输出校验)
 * @version : 2015年6月8日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class RsfCenterRegisterProvider implements RsfCenterRegister {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private AuthQuery      authQuery;
    @Inject
    private RsfRequest     rsfRequest;
    @Inject
    private QueryManager   queryManager;    // 服务查询
    @Inject
    private PublishManager publishManager;  // 服务注册
    //
    //
    /**发布服务*/
    @Override
    public RsfCenterResult<Void> registerProvider(InstanceInfo instance, ServiceID serviceID, ProviderPublishInfo info) {
        return this.register(instance, serviceID, RsfServiceType.Provider, info);
    }
    /**订阅服务*/
    @Override
    public RsfCenterResult<Void> registerConsumer(InstanceInfo instance, ServiceID serviceID, ConsumerPublishInfo info) {
        return this.register(instance, serviceID, RsfServiceType.Consumer, info);
    }
    //
    //
    private RsfCenterResult<Void> register(InstanceInfo instance, ServiceID serviceID, RsfServiceType type, Object info) {
        if (instance == null || serviceID == null || info == null) {
            RsfCenterResultDO<Void> centerResult = new RsfCenterResultDO<Void>();
            centerResult.setSuccess(false);
            centerResult.setErrorCode(ErrorCode.ParamError.getCodeType());
            centerResult.setErrorMessage(ErrorCode.ParamError.getMessage());
            return centerResult;
        }
        // .检查权限
        AuthBean authInfo = (AuthBean) this.rsfRequest.getAttribute(RsfCenterConstants.Center_Request_AuthInfo);
        Result<Boolean> checkResult = this.authQuery.checkPublish(authInfo, serviceID, type);
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
            if (RsfServiceType.Provider == type && info instanceof ProviderPublishInfo) {
                result = this.publishManager.publishProvider(instance, serviceID, (ProviderPublishInfo) info);
            } else if (RsfServiceType.Consumer == type && info instanceof ConsumerPublishInfo) {
                result = this.publishManager.publishConsumer(instance, serviceID, (ConsumerPublishInfo) info);
            } else {
                centerResult.setErrorCode(ErrorCode.EmptyResult.getCodeType());
                centerResult.setErrorMessage("registerType mast in Provider or Consumer.");
                logger.error(LogUtils.create("ERROR_100_00001")//
                        .addLog("errorCode", centerResult.getErrorCode())//
                        .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                        .addLog("serviceID", serviceID)//
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
                        .addLog("errorCode", centerResult.getErrorCode())//
                        .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                        .addLog("serviceID", serviceID)//
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
                    .addLog("errorCode", centerResult.getErrorCode())//
                    .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                    .addLog("serviceID", serviceID)//
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
    public RsfCenterResult<Void> unRegister(InstanceInfo instance, ServiceID serviceID) {
        RsfCenterResultDO<Void> centerResult = new RsfCenterResultDO<Void>();
        centerResult.setMessageID(this.rsfRequest.getRequestID());
        InterAddress remoteRsfAddress = this.rsfRequest.getRemoteAddress();
        String instanceID = instance.getInstanceID();
        try {
            // .判断异常
            Result<Void> result = this.publishManager.removeRegister(instance, serviceID);
            if (!result.isSuccess()) {
                centerResult.setSuccess(false);
                ErrorCode errorInfo = result.getErrorInfo();
                centerResult.setErrorCode(errorInfo.getCodeType());
                centerResult.setErrorMessage(errorInfo.getMessage());
                logger.error(LogUtils.create("ERROR_100_00101")//
                        .addLog("errorCode", centerResult.getErrorCode())//
                        .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                        .addLog("instanceID", instanceID)//
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
                    .addLog("errorCode", centerResult.getErrorCode())//
                    .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                    .addLog("instanceID", instanceID)//
                    .addLog("serviceID", serviceID)//
                    .addLog("error", e.getMessage())//
                    .toJson(), e);
        }
        return centerResult;
    }
    //
    @Override
    public RsfCenterResult<List<String>> pullProviders(InstanceInfo instance, ServiceID serviceID, List<String> runProtocol) {
        RsfCenterResultDO<List<String>> centerResult = new RsfCenterResultDO<List<String>>();
        centerResult.setMessageID(this.rsfRequest.getRequestID());
        InterAddress remoteRsfAddress = this.rsfRequest.getRemoteAddress();
        try {
            // .判断异常
            List<String> result = this.queryManager.queryProviderList(runProtocol, serviceID);
            centerResult.setSuccess(true);
            centerResult.setErrorCode(ErrorCode.OK.getCodeType());
            centerResult.setResult(result);
        } catch (Throwable e) {
            centerResult.setSuccess(false);
            centerResult.setErrorCode(ErrorCode.Exception.getCodeType());
            centerResult.setErrorMessage(e.getMessage());
            logger.error(LogUtils.create("ERROR_100_00302")//
                    .addLog("errorCode", centerResult.getErrorCode())//
                    .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                    .addLog("instanceID", instance.getInstanceID())//
                    .addLog("serviceID", serviceID)//
                    .addLog("error", e.getMessage())//
                    .toJson(), e);
        }
        return centerResult;
    }
    //
    @Override
    public RsfCenterResult<Boolean> requestPushProviders(InstanceInfo instance, ServiceID serviceID, List<String> runProtocol) {
        RsfCenterResultDO<Boolean> centerResult = new RsfCenterResultDO<Boolean>();
        centerResult.setMessageID(this.rsfRequest.getRequestID());
        InterAddress remoteRsfAddress = this.rsfRequest.getRemoteAddress();
        try {
            //
            // .异步推送
            Result<Void> result = this.publishManager.requestPushProviders(instance, serviceID, runProtocol);
            if (!result.isSuccess()) {
                centerResult.setSuccess(false);
                ErrorCode errorInfo = result.getErrorInfo();
                centerResult.setErrorCode(errorInfo.getCodeType());
                centerResult.setErrorMessage(errorInfo.getMessage());
                logger.error(LogUtils.create("ERROR_100_00401")//
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
                    .addLog("errorCode", centerResult.getErrorCode())//
                    .addLog("remoteAddress", remoteRsfAddress.toHostSchema())//
                    .addLog("serviceID", serviceID)//
                    .addLog("error", e.getMessage())//
                    .toJson(), e);
        }
        return centerResult;
    }
}