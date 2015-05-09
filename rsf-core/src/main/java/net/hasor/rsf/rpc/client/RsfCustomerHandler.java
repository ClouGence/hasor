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
import io.netty.channel.ChannelHandlerContext;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.protocol.protocol.ResponseSocketBlock;
import net.hasor.rsf.rpc.BaseChannelInboundHandlerAdapter;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 负责处理 RSF 发出请求之后的所有响应（不区分连接）
 *  -- 根据 {@link ResponseMsg}中包含的 requestID 找到对应的{@link RsfFuture}。
 *  -- 通过{@link RsfFuture}发起响应。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfCustomerHandler extends BaseChannelInboundHandlerAdapter {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public RsfCustomerHandler(AbstractRsfContext rsfContext) {
        super(rsfContext);
    }
    //
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ResponseSocketBlock == false)
            return;
        ResponseSocketBlock block = (ResponseSocketBlock) msg;
        logger.debug("received response({}) full = {}", block.getRequestID(), block);
        //
        RsfClientRequestManager requestManager = this.rsfContext.getRequestManager();
        RsfFuture rsfFuture = requestManager.getRequest(block.getRequestID());
        if (rsfFuture == null) {
            logger.warn("give up the response,requestID({}) ,maybe because timeout! ", block.getRequestID());
            return;//或许它已经超时了。
        }
        logger.debug("doResponse.");
        new CustomerProcessing(block, requestManager, rsfFuture).run();
    }
}