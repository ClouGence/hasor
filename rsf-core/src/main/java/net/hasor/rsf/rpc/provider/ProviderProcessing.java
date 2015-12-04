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
package net.hasor.rsf.rpc.provider;
import io.netty.channel.Channel;
import java.util.List;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.domain.ServiceDefine;
import net.hasor.rsf.protocol.protocol.RequestBlock;
import net.hasor.rsf.protocol.protocol.ResponseBlock;
import net.hasor.rsf.rpc.RsfFilterHandler;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.objects.local.RsfResponseFormLocal;
import net.hasor.rsf.rpc.objects.socket.RsfRequestFormSocket;
import net.hasor.rsf.utils.ProtocolUtils;
import org.more.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 负责处理 Request 调用逻辑，和response写入逻辑。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class ProviderProcessing implements Runnable {
    protected Logger                 logger = LoggerFactory.getLogger(getClass());
    private final AbstractRsfContext rsfContext;
    private final RequestBlock requestBlock;
    private final Channel            nettyChannel;
    //
    public ProviderProcessing(AbstractRsfContext rsfContext, RequestBlock requestBlock, Channel nettyChannel) {
        this.rsfContext = rsfContext;
        this.requestBlock = requestBlock;
        this.nettyChannel = nettyChannel;
    }
    public void run() {
        try {
            RsfResponseFormLocal response = this.doRequest();
            sendResponse(response);
        } catch (Throwable e) {
            sendError(e);
        }
    }
    private RsfResponseFormLocal doRequest() {
        RsfRequestFormSocket rsfRequest = null;
        RsfResponseFormLocal rsfResponse = null;
        //
        //1.构建Response.
        try {
            rsfRequest = new RsfRequestFormSocket(rsfContext, requestBlock);
            rsfResponse = rsfRequest.buildResponse();
        } catch (Throwable e) {
            String errorMessage = "buildResponse fail, requestID:" + requestBlock.getRequestID() + " , error=" + e.getMessage();
            logger.error(errorMessage);
            throw new RsfException(ProtocolStatus.BuildResponse, errorMessage);
        }
        //
        //1.检查timeout
        long lostTime = System.currentTimeMillis() - requestBlock.getReceiveTime();
        int timeout = validateTimeout(requestBlock.getClientTimeout(), rsfRequest.getBindInfo());
        if (lostTime > timeout) {
            logger.error("request timeout. (client parameter)., requestID:" + requestBlock.getRequestID());
            rsfResponse.sendStatus(ProtocolStatus.RequestTimeout, "request timeout. (client parameter).");
            return rsfResponse;
        }
        //
        //2.执行调用
        try {
            String binderID = rsfRequest.getBindInfo().getBindID();
            ServiceDefine<?> define = this.rsfContext.getBindCenter().getService(binderID);
            List<RsfFilter> rsfFilters = define.getFilters();
            new RsfFilterHandler(rsfFilters, InvokeRsfFilterChain.Default).doFilter(rsfRequest, rsfResponse);
        } catch (Throwable e) {
            String errorMessage = "invoke fail, requestID:" + requestBlock.getRequestID() + " , error=" + e.getMessage();
            logger.error(errorMessage);
            rsfResponse.sendStatus(ProtocolStatus.InvokeError, errorMessage);
            return rsfResponse;
        }
        return rsfResponse;
    }
    //
    private void sendResponse(RsfResponseFormLocal rsfResponse) {
        ResponseBlock socketBlock = null;
        RsfOptionSet optMap = this.rsfContext.getSettings().getServerOption();
        //
        if (rsfResponse != null) {
            //1.默认值
            if (rsfResponse.isResponse() == false) {
                Object defaultValue = BeanUtils.getDefaultValue(rsfResponse.getResponseType());
                rsfResponse.sendData(defaultValue);
            }
            //2.buildResponseSocketBlock
            try {
                socketBlock = rsfResponse.buildSocketBlock(this.rsfContext.getSerializeFactory());
            } catch (Throwable e) {
                String errorMessage = "buildResponseSocketBlock fail, requestID:" + requestBlock.getRequestID() + " , error=" + e.getMessage();
                logger.error(errorMessage);
                socketBlock = ProtocolUtils.buildStatus(requestBlock, ProtocolStatus.BuildSocketBlock, optMap);
            }
        } else {
            logger.error("response is null.");
            socketBlock = ProtocolUtils.buildStatus(requestBlock, ProtocolStatus.ResponseNullError, optMap);
        }
        this.nettyChannel.writeAndFlush(socketBlock);
    }
    //
    private void sendError(Throwable exception) {
        RsfOptionSet optMap = this.rsfContext.getSettings().getServerOption();
        ResponseBlock socketBlock = ProtocolUtils.buildStatus(requestBlock, ProtocolStatus.BuildSocketBlock, optMap);
        if (exception instanceof RsfException) {
            socketBlock.setStatus(((RsfException) exception).getStatus());
        }
        this.nettyChannel.writeAndFlush(socketBlock);
    }
    private int validateTimeout(int timeout, RsfBindInfo<?> bindInfo) {
        if (timeout <= 0)
            timeout = this.rsfContext.getSettings().getDefaultTimeout();
        if (timeout > bindInfo.getClientTimeout())
            timeout = bindInfo.getClientTimeout();
        return timeout;
    }
}