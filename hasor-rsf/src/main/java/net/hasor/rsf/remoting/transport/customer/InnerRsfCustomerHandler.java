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
package net.hasor.rsf.remoting.transport.customer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.adapter.AbstractRequestManager;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.remoting.transport.protocol.message.ResponseMsg;
import net.hasor.rsf.utils.RuntimeUtils;
/**
 * 负责处理 RSF 发出请求之后的所有响应（不区分连接）
 *  -- 根据 {@link ResponseMsg}中包含的 requestID 找到对应的{@link RsfFuture}。
 *  -- 通过{@link RsfFuture}发起响应。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerRsfCustomerHandler extends ChannelInboundHandlerAdapter {
    private AbstractRequestManager requestManager = null;
    public InnerRsfCustomerHandler(AbstractRequestManager requestManager) {
        this.requestManager = requestManager;
    }
    //
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ResponseMsg == false)
            return;
        ResponseMsg responseMsg = (ResponseMsg) msg;
        //
        RsfFuture rsfFuture = this.requestManager.getRequest(responseMsg.getRequestID());
        if (rsfFuture == null) {
            Hasor.logWarn(" give up the response,requestID:" + responseMsg.getRequestID() + " ,maybe because timeout! ");
            return;//或许它已经超时了。
        }
        new ResponseHandler(responseMsg, requestManager, rsfFuture).run();
    }
}
/**负责处理客户端 Response 回应逻辑。*/
class ResponseHandler implements Runnable {
    private ResponseMsg            responseMsg;
    private AbstractRequestManager requestManager;
    private RsfFuture              rsfFuture;
    //
    public ResponseHandler(ResponseMsg responseMsg, AbstractRequestManager requestManager, RsfFuture rsfFuture) {
        this.responseMsg = responseMsg;
        this.requestManager = requestManager;
        this.rsfFuture = rsfFuture;
    }
    public void run() {
        //状态判断
        short resStatus = responseMsg.getStatus();
        if (resStatus == ProtocolStatus.Accepted) {
            //
            return;
        } else if (resStatus == ProtocolStatus.ChooseOther) {
            //
            this.requestManager.tryAgain(responseMsg.getRequestID());
            return;
        }
        //恢复response
        RsfResponse response = null;
        try {
            response = RuntimeUtils.recoverResponse(responseMsg, rsfFuture.getRequest(), requestManager.getRsfContext());
            if (resStatus == ProtocolStatus.OK) {
                requestManager.putResponse(responseMsg.getRequestID(), response);
            } else {
                String errorMessage = (String) response.getResponseData();
                requestManager.putResponse(responseMsg.getRequestID(), new RsfException(resStatus, errorMessage));
            }
        } catch (Throwable e) {
            requestManager.putResponse(responseMsg.getRequestID(), e);
            return;
        }
    }
}