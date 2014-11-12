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
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfRequestImpl implements RsfRequest {
    //    private RsfRequest formatRequest(RequestMsg requestMessage2, ChannelHandlerContext channelContext2) {
    //        // TODO Auto-generated method stub
    //        SocketAddress remoteAddress = channelContext.channel().remoteAddress();//InetSocketAddress
    //        long requestID = requestMessage.getRequestID();
    //        return null;
    //    }
    public RsfRequestImpl(RequestMsg requestMessage, ChannelHandlerContext channelContext, RsfContext rsfServer) {
        // TODO Auto-generated constructor stub
    }
    public void unSerialize() throws Throwable {
        // TODO Auto-generated method stub
        String serializeType = this.requestMessage.getSerializeType();
        SerializeFactory serializeFactory = this.rsfServer.getSerializeFactory();
    }
    @Override
    public String getRequestID() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getRemotHost() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int getTimeout() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public String getServiceName() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getServiceMethod() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Class<?>[] getParameterTypes() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Object[] getParameterObject() {
        // TODO Auto-generated method stub
        return null;
    }
}