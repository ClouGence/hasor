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
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandler;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.rpc.caller.RsfRequestManager;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseBlock;
import net.hasor.rsf.transform.protocol.ResponseInfo;
/**
 * 
 * @version : 2015年12月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class RpcCodec extends ChannelInboundHandlerAdapter implements ChannelOutboundHandler {
    public RpcCodec(RpcEventListener listener) {
        // TODO Auto-generated constructor stub
    }
    //
    protected Logger           logger = LoggerFactory.getLogger(getClass());
    protected final NetChannel netChannel;
    public RpcCodecBaseHandler(NetChannel netChannel) {
        this.netChannel = netChannel;
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        netChannel.closeChannel(channel, cause);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        netChannel.closeChannel(channel, null);
    }
    //    protected ResponseInfo buildStatus(RequestInfo requestInfo, int status, String message) {
    //        ResponseInfo info = new ResponseInfo();
    //        RsfOptionSet optMap = this.get.rsfContext.getSettings().getServerOption();
    //        info.addOptionMap(optMap);
    //        info.setRequestID(requestInfo.getRequestID());
    //        info.setStatus(ProtocolStatus.Accepted);
    //        if (StringUtils.isNotBlank(message)) {
    //            info.addOption("message", message);
    //        }
    //        return info;
    //    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ResponseBlock == false)
            return;
        if (msg instanceof ResponseBlock == false)
            return;
    }
    //
    //
    private void readResponseInfo(ChannelHandlerContext ctx, ResponseInfo msg) throws Exception {
        if (msg instanceof ResponseBlock == false)
            return;
        ResponseBlock block = (ResponseBlock) msg;
        logger.debug("received response({}) full = {}", block.getRequestID(), block);
        //
        RsfRequestManager requestManager = this.rsfContext.getRequestManager();
        RsfFuture rsfFuture = requestManager.getRequest(block.getRequestID());
        if (rsfFuture == null) {
            logger.warn("give up the response,requestID({}) ,maybe because timeout! ", block.getRequestID());
            return;//或许它已经超时了。
        }
        logger.debug("doResponse.");
        new RsfClientProcessing(block, requestManager, rsfFuture).run();
    }
    private void readRequestInfo(ChannelHandlerContext ctx, RequestInfo info) {
        //
        //创建request、response
        ResponseInfo readyWrite = null;
        //
        try {
            logger.debug("received request({}) full = {}", info.getRequestID());
            String serviceUniqueName = info.getServiceName();
            Executor exe = this.rsfContext.getCallExecute(serviceUniqueName);
            Channel nettyChannel = ctx.channel();
            exe.execute(new RsfProviderProcessing(this.rsfContext, requestInfo, nettyChannel));//放入业务线程准备执行
            //
            readyWrite = buildStatus(requestInfo, ProtocolStatus.Accepted, null);
        } catch (RejectedExecutionException e) {
            logger.warn("task pool is full ->RejectedExecutionException.");
            readyWrite = buildStatus(requestInfo, ProtocolStatus.ChooseOther, e.getMessage());
        } catch (Throwable e) {
            logger.error("processing error ->" + e.getMessage(), e);
            readyWrite = buildStatus(requestInfo, ProtocolStatus.InvokeError, e.getMessage());
        }
        //
        ctx.pipeline().writeAndFlush(readyWrite);
    }
}