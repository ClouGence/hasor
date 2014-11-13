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
import net.hasor.rsf.context.RsfContext;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.protocol.message.ResponseMsg;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.server.RsfRequest;
import net.hasor.rsf.server.RsfResponse;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfResponseImpl implements RsfResponse {
    private RsfRequest            rsfRequest     = null;
    private ChannelHandlerContext channelContext = null;
    private RsfContext            rsfContext     = null;
    private boolean               committed      = false;
    //
    public RsfResponseImpl(RsfRequest rsfRequest, ChannelHandlerContext channelContext, RsfContext rsfContext) {
        this.rsfRequest = rsfRequest;
        this.channelContext = channelContext;
        this.rsfContext = rsfContext;
    }
    //
    private void check() {
        if (this.committed == true)
            throw new IllegalStateException("is committed.");
        if (this.channelContext.channel().isActive() == false)
            throw new IllegalStateException("connection is closed.");
    }
    //
    public boolean isCommitted() {
        return this.committed;
    }
    public void onMessage(Object messageBody) {
        sendStatus(ProtocolStatus.Message, messageBody);
    }
    public void sendData(Object returnObject) {
        sendStatus(ProtocolStatus.OK, returnObject);
    }
    public void sendStatus(ProtocolStatus status) {
        sendStatus(status, null);
    }
    public void sendStatus(ProtocolStatus status, Object messageBody) {
        check();
        //1.基本信息
        ResponseMsg response = new ResponseMsg();
        response.setVersion(this.rsfRequest.getProtocolVersion());
        response.setRequestID(this.rsfRequest.getRequestID());
        response.setStatus(status);
        response.setSerializeType(rsfRequest.getSerializeType());
        //2.序列化
        try {
            if (messageBody != null) {
                SerializeFactory serializeFactory = this.rsfContext.getSerializeFactory();
                response.setReturnData(messageBody, serializeFactory);
                response.setReturnType(messageBody.getClass().getName());
            } else {
                response.setReturnData(null);
                response.setReturnType(null);
            }
        } catch (Throwable e) {
            response.setStatus(ProtocolStatus.SerializeError);;
            response.setReturnData(e.getMessage().getBytes());;
            response.setReturnType(String.class.getName());
        }
        //3.写内容
        this.channelContext.channel().writeAndFlush(response);
        this.committed = true;
    }
}