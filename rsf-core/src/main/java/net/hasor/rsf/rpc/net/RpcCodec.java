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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
/**
 * 基类
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class RpcCodec extends ChannelInboundHandlerAdapter {
    protected Logger               logger = LoggerFactory.getLogger(getClass());
    private final ReceivedListener rpcEventListener;
    //
    public RpcCodec(ReceivedListener rpcEventListener) {
        this.rpcEventListener = rpcEventListener;
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("exception close channel.");
        ctx.pipeline().channel().close();
        super.exceptionCaught(ctx, cause);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("in active close channel.");
        ctx.pipeline().channel().close();
        super.channelInactive(ctx);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RequestInfo) {
            this.rpcEventListener.receivedMessage((RequestInfo) msg);
        }
        if (msg instanceof ResponseInfo) {
            this.rpcEventListener.receivedMessage((ResponseInfo) msg);
        }
    }
}