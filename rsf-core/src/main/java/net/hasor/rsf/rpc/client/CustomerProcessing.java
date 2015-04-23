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
package net.hasor.rsf.rpc.client;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.protocol.protocol.ResponseSocketBlock;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.objects.socket.RsfResponseFormSocket;
import org.more.logger.LoggerHelper;
/**
 * 负责处理客户端 Response 回应逻辑。
 * @version : 2015年4月23日
 * @author 赵永春(zyc@hasor.net)
 */
class CustomerProcessing implements Runnable {
    private RsfFuture               rsfFuture;
    private ResponseSocketBlock     responseBlock;
    private RsfClientRequestManager requestManager;
    //
    public CustomerProcessing(ResponseSocketBlock responseBlock, RsfClientRequestManager requestManager, RsfFuture rsfFuture) {
        this.responseBlock = responseBlock;
        this.requestManager = requestManager;
        this.rsfFuture = rsfFuture;
    }
    public void run() {
        //状态判断
        long requestID = responseBlock.getRequestID();
        short resStatus = responseBlock.getStatus();
        if (resStatus == ProtocolStatus.Accepted) {
            //
            LoggerHelper.logFine("requestID:%s , received Accepted.", requestID);
            return;
        } else if (resStatus == ProtocolStatus.ChooseOther) {
            //
            LoggerHelper.logInfo("requestID:%s , received ChooseOther -> do tryAgain.", requestID);
            this.requestManager.tryAgain(requestID);
            return;
        }
        //恢复response
        try {
            RsfBindInfo<?> bindInfo = rsfFuture.getRequest().getBindInfo();
            AbstractRsfContext rsfContext = this.requestManager.getRsfContext();
            RsfResponse response = new RsfResponseFormSocket(rsfContext, bindInfo, this.responseBlock);
            LoggerHelper.logInfo("requestID:%s , received protocolStatus=%s.", requestID, resStatus);
            if (resStatus == ProtocolStatus.OK) {
                requestManager.putResponse(requestID, response);
            } else {
                String errorMessage = (String) response.getResponseData();
                requestManager.putResponse(requestID, new RsfException(resStatus, errorMessage));
            }
        } catch (Throwable e) {
            LoggerHelper.logSevere("requestID:%s , recovery response ERROR, -> %s", requestID, e.getMessage());
            requestManager.putResponse(requestID, e);
            return;
        }
    }
}