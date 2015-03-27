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
package net.hasor.rsf.remoting.transport.provider;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.remoting.transport.component.RsfFilterHandler;
import net.hasor.rsf.remoting.transport.component.RsfRequestImpl;
import net.hasor.rsf.remoting.transport.component.RsfResponseImpl;
import net.hasor.rsf.remoting.transport.connection.NetworkConnection;
import net.hasor.rsf.remoting.transport.protocol.message.RequestMsg;
import net.hasor.rsf.remoting.transport.protocol.message.ResponseMsg;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.utils.RuntimeUtils;
import net.hasor.rsf.utils.TransferUtils;
import org.more.logger.LoggerHelper;
import org.more.util.BeanUtils;
/**
 * 负责处理 Request 调用逻辑，和response写入逻辑。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerRequestHandler implements Runnable {
    private AbstractRsfContext rsfContext;
    private RequestMsg         requestMsg;
    private NetworkConnection  connection;
    //
    public InnerRequestHandler(AbstractRsfContext rsfContext, RequestMsg requestMsg, NetworkConnection connection) {
        this.rsfContext = rsfContext;
        this.requestMsg = requestMsg;
        this.connection = connection;
    }
    public void run() {
        RsfResponseImpl response = this.doRequest();
        sendResponse(response);
    }
    private RsfResponseImpl doRequest() {
        RsfRequestImpl request = null;
        RsfResponseImpl response = null;
        try {
            request = RuntimeUtils.recoverRequest(//
                    requestMsg, connection, this.rsfContext);
            response = request.buildResponse();
            //
        } catch (RsfException e) {
            String logMsg = String.format("recoverRequest fail, requestID(%s) , %s.", requestMsg.getRequestID(), e.getMessage());
            LoggerHelper.logSevere(logMsg);
            //
            ResponseMsg pack = TransferUtils.buildStatus(//
                    requestMsg.getVersion(), //协议版本
                    requestMsg.getRequestID(),//请求ID
                    e.getStatus(),//响应状态
                    requestMsg.getSerializeType(),//序列化类型
                    this.rsfContext.getSettings().getServerOption());//选项参数
            try {
                pack.setReturnData(logMsg, rsfContext.getSerializeFactory());
            } catch (Throwable e1) { /**/}
            this.connection.getChannel().writeAndFlush(pack);
            return null;
        }
        //1.检查timeout
        long lostTime = System.currentTimeMillis() - requestMsg.getReceiveTime();
        int timeout = validateTimeout(requestMsg.getClientTimeout(), request.getBindInfo());
        if (lostTime > timeout) {
            response.sendStatus(ProtocolStatus.RequestTimeout, "request timeout. (client parameter).");
            return response;
        }
        //2.执行调用
        try {
            Provider<RsfFilter>[] rsfFilters = this.rsfContext.getFilters(request.getBindInfo());
            new RsfFilterHandler(rsfFilters, InnerInvokeHandler.Default).doFilter(request, response);
        } catch (Throwable e) {
            //500 InternalServerError
            response.sendStatus(ProtocolStatus.InternalServerError, e.getMessage());
            return response;
        }
        return response;
    }
    private void sendResponse(RsfResponseImpl response) {
        if (response == null)
            return;
        //给予默认值
        if (response.isResponse() == false) {
            Object defaultValue = BeanUtils.getDefaultValue(response.getResponseType());
            response.sendData(defaultValue);
        }
        //回写Socket
        ResponseMsg responseMsg = response.getMsg();
        try {
            Object responseData = response.getResponseData();
            if (responseData != null) {
                SerializeFactory serializeFactory = this.rsfContext.getSerializeFactory();
                responseMsg.setReturnData(responseData, serializeFactory);
            }
        } catch (Throwable e) {
            String msg = e.getClass().getName() + ":" + e.getMessage();
            responseMsg.setStatus(ProtocolStatus.SerializeError);;
            responseMsg.setReturnData(msg.getBytes());;
            responseMsg.setReturnType(String.class.getName());
        }
        this.connection.getChannel().writeAndFlush(responseMsg);
    }
    private int validateTimeout(int timeout, RsfBindInfo<?> bindInfo) {
        if (timeout <= 0)
            timeout = this.rsfContext.getSettings().getDefaultTimeout();
        if (timeout > bindInfo.getClientTimeout())
            timeout = bindInfo.getClientTimeout();
        return timeout;
    }
}