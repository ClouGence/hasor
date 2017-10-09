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
import io.netty.channel.ChannelHandler;
import net.hasor.core.AppContext;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.domain.OptionInfo;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.protocol.rsf.v1.PoolBlock;
import net.hasor.rsf.rpc.net.*;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
/**
 * RSF 解码器
 * @version : 2014年10月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfProtocolHandler implements ProtocolHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public boolean acceptIn(final Connector connector, RsfChannel rsfChannel) throws Exception {
        //
        // .添加数据接收监听器，获取握手数据
        final BasicFuture<Boolean> future = new BasicFuture<Boolean>();
        rsfChannel.addListener(new ReceivedListener() {
            @Override
            public void receivedMessage(RsfChannel rsfChannel, OptionInfo info) throws IOException {
                if (future.isDone()) {
                    return;
                }
                //
                if (info instanceof ResponseInfo) {
                    ResponseInfo response = (ResponseInfo) info;
                    String serverInfo = response.getOption("SERVER_INFO");
                    try {
                        if (LinkType.In == rsfChannel.getLinkType()) {
                            connector.mappingTo(rsfChannel, new InterAddress(serverInfo));
                        }
                    } catch (Exception e) { /**/ }
                    future.completed(true);
                    logger.info("handshake -> ready for {}", serverInfo);
                }
            }
        });
        //
        // .发送RSF实例信息
        InterAddress interAddress = connector.getGatewayAddress();
        if (interAddress == null) {
            interAddress = connector.getBindAddress();
        }
        InterAddress publishAddress = interAddress;
        ResponseInfo options = new ResponseInfo();
        options.setRequestID(-1);
        options.setStatus(ProtocolStatus.OK);
        options.addOption("SERVER_INFO", publishAddress.toHostSchema());
        rsfChannel.sendData(options, null);
        //
        return rsfChannel.activeIn();
    }
    @Override
    public ChannelHandler[] channelHandler(Connector connector, AppContext appContext) {
        RsfEnvironment env = appContext.getInstance(RsfEnvironment.class);
        RsfDuplexHandler duplexHandler = new RsfDuplexHandler(  //
                new RsfDecoder(env, PoolBlock.DataMaxSize),     //
                new RsfEncoder(env)                             //
        );
        return new ChannelHandler[] {                           //
                duplexHandler                                   //
        };
    }
}