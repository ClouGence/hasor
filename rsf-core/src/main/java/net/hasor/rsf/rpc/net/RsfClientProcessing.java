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
package net.hasor.rsf.rpc.net;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.rpc.caller.RsfRequestManager;
import net.hasor.rsf.rpc.caller.RsfResponseFormLocal;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.transform.protocol.ResponseInfo;
/**
 * 负责处理客户端 Response 回应逻辑。
 * @version : 2015年4月23日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfClientProcessing implements Runnable {
    protected Logger                logger = LoggerFactory.getLogger(getClass());
    private RsfFuture               rsfFuture;
    private ResponseInfo            responseInfo;
    private RsfRequestManager requestManager;
    //
    public RsfClientProcessing(ResponseInfo responseInfo, RsfRequestManager requestManager, RsfFuture rsfFuture) {
        this.requestManager = requestManager;
        this.rsfFuture = rsfFuture;
        this.responseInfo = responseInfo;
    }
    public void run() {
        //状态判断
        long requestID = responseInfo.getRequestID();
        ProtocolStatus resStatus = responseInfo.getStatus();
        if (ProtocolStatus.Accepted.equals(resStatus)) {
            //
            logger.debug("requestID:{} , received Accepted.", requestID);
            return;
        }
        //恢复response
        try {
            RsfBindInfo<?> bindInfo = rsfFuture.getRequest().getBindInfo();
            AbstractRsfContext rsfContext = this.requestManager.getRsfContext();
            RsfResponse response = new RsfResponseFormLocal(rsfContext, bindInfo, this.responseInfo);
            logger.info("requestID:{} , received protocolStatus={}.", requestID, resStatus);
            if (ProtocolStatus.OK.equals(resStatus)) {
                requestManager.putResponse(requestID, response);
            } else {
                String errorMessage = (String) response.getResponseData();
                requestManager.putResponse(requestID, new RsfException(resStatus, errorMessage));
            }
        } catch (Throwable e) {
            logger.error("requestID:{} , recovery response ERROR, -> {}", requestID, e.getMessage());
            requestManager.putResponse(requestID, e);
            return;
        }
    }
}