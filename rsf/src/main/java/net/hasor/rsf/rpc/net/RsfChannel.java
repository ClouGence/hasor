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
package net.hasor.rsf.rpc.net;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 封装网络连接，并且提供网络数据收发统计。
 * @version : 2015年12月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfChannel {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private          String                 protocol;
    private final    InterAddress           target;
    private          InterAddress           inverseMappingTo;
    private final    Channel                channel;
    private final    AtomicBoolean          shakeHands;
    private final    LinkType               linkType;
    private volatile long                   lastSendTime;   //最后数据发送时间
    private volatile long                   sendPackets;    //发送的数据包总数
    private          List<ReceivedListener> listenerList;
    //
    RsfChannel(String protocol, InterAddress target, Channel channel, LinkType linkType) {
        this.protocol = protocol;
        this.target = target;
        this.channel = channel;
        this.shakeHands = new AtomicBoolean(false);
        this.linkType = linkType;
        this.listenerList = new CopyOnWriteArrayList<ReceivedListener>();
        //
        if (!LinkType.In.equals(linkType)) {
            this.shakeHands.set(true);//连入的连接，需要进行握手之后才能使用
        }
    }
    @Override
    public String toString() {
        return "RsfChannel{" + "protocol=" + protocol +//
                ", linkType=" + linkType.name() + //
                ", shakeHands=" + shakeHands + //
                ", channel=" + channel +//
                '}';
    }
    //
    //
    /**接收到数据*/
    public void addListener(ReceivedListener receivedListener) {
        if (!this.listenerList.contains(receivedListener)) {
            this.listenerList.add(receivedListener);
        }
    }
    /**接收到数据*/
    void receivedData(OptionInfo object) throws IOException {
        for (ReceivedListener listener : this.listenerList) {
            listener.receivedMessage(this, object);
        }
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
        this.sendPackets++;
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
                lastSendTime = System.currentTimeMillis();
                RsfException e = null;
                if (future.isCancelled()) {
                    //用户取消
                    String errorMsg = "send request(" + requestID + ") to cancelled by user.";
                    e = new RsfException(ProtocolStatus.NetworkError, errorMsg);
                    logger.error(e.getMessage(), e);
                    //回应Response
                    if (callBack != null) {
                        callBack.failed(requestID, e);
                    }
                } else if (!future.isSuccess()) {
                    //异常状况
                    Throwable ex = future.cause();
                    String errorMsg = "send request(" + requestID + ") an error ->" + ex.getMessage();
                    e = new RsfException(ProtocolStatus.NetworkError, errorMsg, ex);
                    logger.error(e.getMessage(), e);
                    //回应Response
                    if (callBack != null) {
                        callBack.failed(requestID, e);
                    }
                }
            }
        });
    }
    //
    //
    /**运行的协议*/
    public String getProtocol() {
        return this.protocol;
    }
    /**连接方向*/
    public LinkType getLinkType() {
        return this.linkType;
    }
    /**最后发送数据时间*/
    public long getLastSendTime() {
        return this.lastSendTime;
    }
    /**发送的数据包总数。*/
    public long getSendPackets() {
        return this.sendPackets;
    }
    /**测定连接是否处于激活的。*/
    public boolean isActive() {
        return this.channel.isActive() && this.shakeHands.get();
    }
    /**获取远程连接的地址*/
    public InterAddress getTarget() {
        if (this.target != null) {
            return this.target;
        }
        return this.target;
    }
    //
    //
    /**激活这个连接服务*/
    public boolean activeIn() {
        this.shakeHands.set(true);
        return this.shakeHands.get();
    }
    /**关闭连接。*/
    void close() {
        if (this.channel != null && this.channel.isActive()) {
            this.channel.close();
        }
    }
    void inverseMappingTo(InterAddress interAddress) {
        this.inverseMappingTo = interAddress;
    }
}