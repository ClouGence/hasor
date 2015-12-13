/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RSFConstants;
import net.hasor.rsf.rpc.caller.RsfCaller;
import net.hasor.rsf.transform.codec.ProtocolUtils;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseBlock;
import net.hasor.rsf.utils.ExecutesManager;
/**
 * 扩展{@link RsfCaller}，用来支持远程机器发来的调用请求。
 * 
 * @version : 2015年12月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class RemoteRsfCaller extends RsfCaller {
    private final ExecutesManager      executesManager;
    private final RemoteSenderListener senderListener;
    //
    public RemoteRsfCaller(AppContext appContext, RemoteSenderListener senderListener) {
        super(appContext, senderListener);
        //
        this.senderListener = senderListener;
        RsfSettings rsfSettings = this.getContext().getSettings();
        int queueSize = rsfSettings.getQueueMaxSize();
        int minCorePoolSize = rsfSettings.getQueueMinPoolSize();
        int maxCorePoolSize = rsfSettings.getQueueMaxPoolSize();
        long keepAliveTime = rsfSettings.getQueueKeepAliveTime();
        this.executesManager = new ExecutesManager(minCorePoolSize, maxCorePoolSize, queueSize, keepAliveTime);
        Hasor.pushShutdownListener(appContext.getEnvironment(), new EventListener() {
            public void onEvent(String event, Object[] params) throws Throwable {
                logger.info("rsfCaller -> shutdown.");
                executesManager.shutdown();
            }
        });
    }
    /**获取消息监听器。*/
    RemoteSenderListener getSenderListener() {
        return this.senderListener;
    }
    /**
     * 收到Request请求，并将该请求安排进队列，由队列安排方法调用。
     * @param target 目标调用地址。
     * @param info 请求消息。
     */
    public void receivedRequest(InterAddress target, RequestInfo info) {
        try {
            logger.debug("received request({}) full = {}", info.getRequestID());
            String serviceUniqueName = "[" + info.getServiceGroup() + "]" + info.getServiceName() + "-" + info.getServiceVersion();
            Executor executor = executesManager.getExecute(serviceUniqueName);
            executor.execute(new RemoteRsfCallerProcessing(target, this, info));//放入业务线程准备执行
        } catch (RejectedExecutionException e) {
            String msgLog = "rejected request, queue is full." + e.getMessage();
            logger.warn(msgLog, e);
            ResponseBlock block = ProtocolUtils.buildStatus(RSFConstants.RSF_Response, info.getRequestID(), ProtocolStatus.QueueFull, msgLog);
            this.senderListener.receiveResponse(target, block);
        } catch (Throwable e) {
            String msgLog = "put calling task error ->" + e.getMessage();
            logger.error(msgLog, e);
            ResponseBlock block = ProtocolUtils.buildStatus(RSFConstants.RSF_Response, info.getRequestID(), ProtocolStatus.ServerError, msgLog);
            this.senderListener.receiveResponse(target, block);
        }
    }
}