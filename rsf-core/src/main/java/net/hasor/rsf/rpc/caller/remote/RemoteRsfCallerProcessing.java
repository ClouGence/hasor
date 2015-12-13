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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RSFConstants;
import net.hasor.rsf.rpc.caller.RsfFilterHandler;
import net.hasor.rsf.rpc.caller.RsfResponseFormLocal;
import net.hasor.rsf.transform.codec.ProtocolUtils;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseBlock;
/**
 * 负责处理 Request 调用逻辑和response写入逻辑。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class RemoteRsfCallerProcessing implements Runnable {
    protected Logger              logger = LoggerFactory.getLogger(getClass());
    private final InterAddress    target;
    private final RemoteRsfCaller rsfCaller;
    private final RequestInfo     requestInfo;
    //
    public RemoteRsfCallerProcessing(InterAddress target, RemoteRsfCaller rsfCaller, RequestInfo requestInfo) {
        this.target = target;
        this.rsfCaller = rsfCaller;
        this.requestInfo = requestInfo;
    }
    public void run() {
        /*正确性检验。*/
        long requestID = this.requestInfo.getRequestID();
        String group = this.requestInfo.getServiceGroup();
        String name = this.requestInfo.getServiceName();
        String version = this.requestInfo.getServiceVersion();
        RsfBindInfo<?> bindInfo = this.rsfCaller.getContainer().getRsfBindInfo(group, name, version);
        if (bindInfo == null) {
            String msgLog = "do request(" + requestID + ") failed -> service " + bindInfo.getBindID() + " not exist.";
            logger.error(msgLog);
            ResponseBlock block = ProtocolUtils.buildStatus(RSFConstants.RSF_Response, requestID, ProtocolStatus.Forbidden, msgLog);
            this.rsfCaller.getSenderListener().receiveResponse(this.target, block);
            return;
        }
        /*检查timeout。*/
        long lostTime = System.currentTimeMillis() - this.requestInfo.getReceiveTime();
        int clientTimeout = this.requestInfo.getClientTimeout();
        int timeout = this.validateTimeout(clientTimeout, bindInfo);
        if (lostTime > timeout) {
            String errorInfo = "request(" + requestID + ") timeout for server.";
            logger.error(errorInfo);
            ResponseBlock block = ProtocolUtils.buildStatus(RSFConstants.RSF_Response, requestID, ProtocolStatus.Timeout, errorInfo);
            this.rsfCaller.getSenderListener().receiveResponse(this.target, block);
            return;
        }
        /*执行调用*/
        String serviceID = bindInfo.getBindID();
        try {
            RsfRequestFormLocal rsfRequest = new RsfRequestFormLocal(rsfRequest);
            RsfResponseFormLocal rsfResponse = new RsfResponseFormLocal(rsfRequest);
            Provider<RsfFilter>[] rsfFilters = this.rsfCaller.getContainer().getFilterProviders(serviceID);
            new RsfFilterHandler(rsfFilters, RsfInvokeFilterChain.Default).doFilter(rsfRequest, rsfResponse);
        } catch (Throwable e) {
            try {
                rsfFuture.failed(e);
            } catch (Throwable e2) {
                logger.error("do callback for failed error->" + e.getMessage(), e);
            }
        } finally {
            String errorMessage = "invoke fail, requestID:" + requestInfo.getRequestID() + " , error=" + e.getMessage();
            logger.error(errorMessage);
            rsfResponse.sendStatus(ProtocolStatus.InvokeError, errorMessage);
            return rsfResponse;
        }
    }
    private int validateTimeout(int timeout, RsfBindInfo<?> bindInfo) {
        if (timeout <= 0)
            timeout = this.rsfCaller.getContext().getSettings().getDefaultTimeout();
        if (timeout > bindInfo.getClientTimeout())
            timeout = bindInfo.getClientTimeout();
        return timeout;
    }
}