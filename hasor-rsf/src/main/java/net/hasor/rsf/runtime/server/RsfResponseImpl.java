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
package net.hasor.rsf.runtime.server;
import io.netty.channel.ChannelHandlerContext;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.runtime.RsfResponse;
import net.hasor.rsf.runtime.RsfContext;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfResponseImpl implements RsfResponse {
    public RsfResponseImpl(RequestMsg requestMessage, ChannelHandlerContext channelContext, RsfContext rsfServer) {
        // TODO Auto-generated constructor stub
    }
    @Override
    public void send(Object returnObject) {
        // TODO Auto-generated method stub
    }
    @Override
    public void sendMessage(ProtocolStatus status) {
        // TODO Auto-generated method stub
    }
    @Override
    public void sendMessage(ProtocolStatus status, Object messageBody) {
        // TODO Auto-generated method stub
    }
}