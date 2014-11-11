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
package net.hasor.rsf.server.handler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.hasor.rsf.executes.ExecutesManager;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.protocol.block.ResponseSocketBlock;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.protocol.message.ResponseMsg;
import net.hasor.rsf.protocol.toos.TransferUtils;
/**
 * 负责将 Netty 事件放入 ExecutesManager 队列中。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceHandler extends ChannelInboundHandlerAdapter {
    private ExecutesManager executesManager = null;
    //
    public ServiceHandler(ExecutesManager executesManager) {
        this.executesManager = executesManager;
    }
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte version = 0;
        long requestID = 0;
        boolean pushOK = false;
        //
        if (msg instanceof RequestMsg) {
            RequestMsg request = (RequestMsg) msg;
            version = request.getVersion();
            requestID = request.getRequestID();
            pushOK = this.executesManager.pushMessage(msg);//Request
        } else if (msg instanceof ResponseMsg) {
            ResponseMsg response = (ResponseMsg) msg;
            version = response.getVersion();
            requestID = response.getRequestID();
            pushOK = this.executesManager.pushMessage(msg);//Response
        }
        //
        if (pushOK == false) {
            this.fireChooseOther(ctx, version, requestID);
        }
    }
    private void fireChooseOther(ChannelHandlerContext ctx, byte version, long requestID) {
        //1.创建ChooseOther包
        ResponseSocketBlock ack = TransferUtils.buildStatus(//
                version, requestID, ProtocolStatus.ChooseOther);
        //2.发送ChooseOther包
        ctx.pipeline().writeAndFlush(ack);
    }
}