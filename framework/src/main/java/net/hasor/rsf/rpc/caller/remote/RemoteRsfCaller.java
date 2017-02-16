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
package net.hasor.rsf.rpc.caller.remote;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.rpc.caller.RsfCaller;
import net.hasor.rsf.utils.ExecutesManager;
import net.hasor.rsf.utils.ProtocolUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
/**
 * 扩展{@link RsfCaller}，用来支持远程机器发来的调用请求。
 * @version : 2015年12月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class RemoteRsfCaller extends RsfCaller {
    private final ExecutesManager      executesManager;
    private final RemoteSenderListener senderListener;
    // 
    public RemoteRsfCaller(RsfContext rsfContext, RsfBeanContainer rsfBeanContainer, RemoteSenderListener senderListener) {
        super(rsfContext, rsfBeanContainer, senderListener);
        //
        this.senderListener = senderListener;
        RsfSettings rsfSettings = rsfContext.getSettings();
        int queueSize = rsfSettings.getQueueMaxSize();
        int minCorePoolSize = rsfSettings.getQueueMinPoolSize();
        int maxCorePoolSize = rsfSettings.getQueueMaxPoolSize();
        long keepAliveTime = rsfSettings.getQueueKeepAliveTime();
        this.executesManager = new ExecutesManager(minCorePoolSize, maxCorePoolSize, queueSize, keepAliveTime, rsfContext.getClassLoader());
    }
    /**销毁。*/
    public void shutdown() {
        logger.info("rsfCaller -> shutdown.");
        this.executesManager.shutdown();
    }
    /**
     * 收到Request请求，并将该请求安排进队列，由队列安排方法调用。
     * @param target 目标调用地址。
     * @param info 请求消息。
     */
    public void onRequest(InterAddress target, RequestInfo info) {
        RsfEnvironment rsfEnv = this.getContext().getEnvironment();
        String serviceUniqueName = "[" + info.getServiceGroup() + "]" + info.getServiceName() + "-" + info.getServiceVersion();
        try {
            invLogger.info("request({}) -> received, bindID ={}, targetMethod ={}, remoteAddress ={}.", //
                    info.getRequestID(), serviceUniqueName, info.getTargetMethod(), target);
            //
            Executor executor = executesManager.getExecute(serviceUniqueName);
            executor.execute(new RemoteRsfCallerProcessing(target, this, info));//放入业务线程准备执行
            ResponseInfo resp = ProtocolUtils.buildResponseStatus(rsfEnv, info.getRequestID(), ProtocolStatus.Accept, null);
            this.senderListener.sendResponse(target, resp);
        } catch (RejectedExecutionException e) {
            invLogger.info("request({}) -> rejected request, queue is full. -> bindID ={}, targetMethod ={}, remoteAddress ={}.", //
                    info.getRequestID(), serviceUniqueName, info.getTargetMethod(), target);
            //
            String errorMessage = "(" + e.getClass().getName() + ")" + e.getMessage();
            String msgLog = "rejected request, queue is full." + errorMessage;
            logger.warn(msgLog, e);
            ResponseInfo resp = ProtocolUtils.buildResponseStatus(rsfEnv, info.getRequestID(), ProtocolStatus.QueueFull, msgLog);
            this.senderListener.sendResponse(target, resp);
        }
    }
    //
    /**获取消息监听器。*/
    RemoteSenderListener getSenderListener() {
        return this.senderListener;
    }
}