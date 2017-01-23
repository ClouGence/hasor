///*
// * Copyright 2008-2009 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package net.hasor.rsf.protocol.rsf;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.util.Timeout;
//import io.netty.util.TimerTask;
//import net.hasor.rsf.InterAddress;
//import net.hasor.rsf.RsfEnvironment;
//import net.hasor.rsf.domain.ProtocolStatus;
//import net.hasor.rsf.domain.RequestInfo;
//import net.hasor.rsf.domain.ResponseInfo;
//import net.hasor.rsf.domain.RsfConstants;
//import org.more.util.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.InetSocketAddress;
///**
// * RSF协议之间的握手。
// * @version : 2014年11月4日
// * @author 赵永春(zyc@hasor.net)
// */
//public class RpcShakeHands extends ChannelInboundHandlerAdapter {
//    protected Logger logger = LoggerFactory.getLogger(getClass());
//    private       InterAddress   gatewayAddress;
//    private final RsfEnvironment rsfEnvironment;
//    //
//    public RpcShakeHands(InterAddress gatewayAddress, RsfEnvironment rsfEnvironment) {
//        this.gatewayAddress = gatewayAddress;
//        this.rsfEnvironment = rsfEnvironment;
//    }
//    @Override
//    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
//        this.rsfEnvironment.atTime(new TimerTask() {
//            public void run(Timeout timeout) throws Exception {
//                if (!shakeHands.get()) {
//                    ctx.close();
//                }
//            }
//        });
//        super.handlerAdded(ctx);
//    }
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        //
//        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.pipeline().channel().remoteAddress();
//        if (msg instanceof RequestInfo) {
//            RequestInfo request = (RequestInfo) msg;
//            String cmdType = request.getTargetMethod();
//            if (request.getRequestID() == -1 && StringUtils.equals(cmdType, "ASK_HOST_INFO")) {
//                ResponseInfo response = new ResponseInfo(RsfConstants.Version_1);
//                response.setRequestID(-1);
//                response.setStatus(ProtocolStatus.OK);
//                if (this.gatewayAddress != null) {
//                    response.addOption("SERVER_INFO", this.gatewayAddress.toHostSchema());//RSF实例信息。
//                } else {
//                    response.addOption("SERVER_INFO", this.bindAddress.toHostSchema());//RSF实例信息。
//                }
//                logger.info("handshake -> send ack to {}.", remoteAddress);
//                ctx.pipeline().writeAndFlush(response);//发送握手数据包
//                return;
//            }
//        }
//        if (msg instanceof ResponseInfo) {
//            ResponseInfo response = (ResponseInfo) msg;
//            String serverInfo = response.getOption("SERVER_INFO");
//            Channel channel = ctx.pipeline().channel();
//            this.targetAddress = new InterAddress(serverInfo);
//            this.shakeHands.set(true);
//            RsfChannel netChannel = new RsfChannel(this.targetAddress, channel, this.shakeHands);
//            this.channelRegister.completed(this.targetAddress, netChannel);
//            logger.info("handshake -> ready for {}", this.targetAddress);
//        }
//    }
//}