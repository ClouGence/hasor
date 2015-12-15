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
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
/**
 * 
 * @version : 2015年12月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfNetChannel {
    protected Logger           logger = LoggerFactory.getLogger(getClass());
    private final InterAddress target;
    private final Channel      channel;
    //
    RsfNetChannel(InterAddress target, Channel channel) {
        this.target = target;
        this.channel = channel;
    }
    public InterAddress getTarget() {
        return target;
    }
    /**将数据写入 Netty。*/
    public void sendData(final RequestInfo info, final SendCallBack callBack) {
        this.sendData(info.getRequestID(), info, callBack);
    }
    /**将数据写入 Netty。*/
    public void sendData(final ResponseInfo info, final SendCallBack callBack) {
        this.sendData(info.getRequestID(), info, callBack);
    }
    /**将数据写入 Netty。*/
    private void sendData(final long requestID, Object sendData, final SendCallBack callBack) {
        if (!this.channel.isActive()) {
            RsfException e = new RsfException(ProtocolStatus.NetworkError, "send (" + requestID + ") an error, socket Channel is close.");
            if (callBack != null) {
                callBack.failed(requestID, e);
            }
            return;
        }
        /*发送数据*/
        ChannelFuture future = this.channel.writeAndFlush(sendData);
        /*为sendData添加侦听器，负责处理意外情况。*/
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    if (callBack != null) {
                        callBack.complete(requestID);
                    }
                    return;
                }
                RsfException e = null;
                if (future.isCancelled()) {
                    //用户取消
                    String errorMsg = "send request(" + requestID + ") to cancelled by user.";
                    e = new RsfException(ProtocolStatus.NetworkError, errorMsg);
                } else if (!future.isSuccess()) {
                    //异常状况
                    Throwable ex = future.cause();
                    String errorMsg = "send request(" + requestID + ") an error ->" + ex.getMessage();
                    e = new RsfException(ProtocolStatus.NetworkError, errorMsg, ex);
                }
                //回应Response
                logger.error(e.getMessage(), e);
                if (callBack != null) {
                    callBack.failed(requestID, e);
                }
            }
        });
    }
    //
    /**测定连接是否处于激活的。*/
    public boolean isActive() {
        return this.channel.isActive();
    }
    /**关闭连接。*/
    void close() {
        if (this.channel != null && this.channel.isActive()) {
            this.channel.close();
        }
    }
}