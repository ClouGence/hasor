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
package test.net.hasor.rsf.protocol;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.protocol.protocol.ProtocolUtils;
import net.hasor.rsf.protocol.protocol.RequestSocketBlock;
import net.hasor.rsf.protocol.protocol.ResponseSocketBlock;
/**
 * response 200
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private ChannelFutureListener sendListener = null;
    //
    public ServerHandler(final Monitor monitor) {
        this.sendListener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess() == false)
                    return;
                monitor.monitorCount();
            }
        };
    }
    //
    //
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestSocketBlock req = (RequestSocketBlock) msg;
        //
        byte version = ProtocolUtils.getVersion(req.getVersion());
        ResponseSocketBlock response = ProtocolUtils.buildStatus(version, req.getRequestID(), ProtocolStatus.Accepted, "BlackHole", null);
        //
        ctx.channel().writeAndFlush(response).addListener(sendListener);
        //
    }
}