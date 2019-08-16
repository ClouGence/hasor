/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.rsf.protocol.rsf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.rpc.net.Connector;
import net.hasor.rsf.utils.ProtocolUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RSF 握手协议实现
 * @version : 2017年11月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ShakeHands extends ChannelInboundHandlerAdapter {
    protected     Logger         logger = LoggerFactory.getLogger(getClass());
    private final AtomicBoolean  atomicBoolean;
    private final InterAddress   bindAddress;
    private final RsfEnvironment rsfEnvironment;

    public ShakeHands(Connector connector) {
        this.atomicBoolean = new AtomicBoolean(false);
        this.rsfEnvironment = connector.getRsfEnvironment();
        this.bindAddress = connector.getBindAddress();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        this.atomicBoolean.set(false);
        this.rsfEnvironment.atTime(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                if (atomicBoolean.get()) {
                    return;
                }
                ProtocolUtils.buildResponseStatus(rsfEnvironment, -1, ProtocolStatus.Timeout, "shake hands with timeout.");
                ResponseInfo options = new ResponseInfo();
                options.setRequestID(-1);
                options.setStatus(ProtocolStatus.OK);
                ctx.close();
                logger.error("shake hands with timeout. ->" + ctx.channel().remoteAddress());
            }
        }, 3000);
        //
        // .发送RSF实例信息
        ResponseInfo options = new ResponseInfo();
        options.setRequestID(-1);
        options.setStatus(ProtocolStatus.OK);
        options.addOption("SERVER_INFO", this.bindAddress.toHostSchema());
        ctx.writeAndFlush(options);
        //
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!this.atomicBoolean.get()) {
            if (msg instanceof ResponseInfo) {
                ResponseInfo responseInfo = (ResponseInfo) msg;
                String serverInfo = responseInfo.getOption("SERVER_INFO");
                if (StringUtils.isNotBlank(serverInfo)) {
                    logger.info("shake hands successful. ->" + ctx.channel().remoteAddress());
                    this.atomicBoolean.set(true);
                }
            }
            return;
        }
        super.channelRead(ctx, msg);
    }
}