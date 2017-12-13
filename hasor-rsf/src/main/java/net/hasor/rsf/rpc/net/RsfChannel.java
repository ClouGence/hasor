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
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * 封装网络连接，并且提供网络数据收发统计。
 * @version : 2015年12月8日
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class RsfChannel {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final    String                 protocol;       // 使用的协议
    private final    InterAddress           target;
    private final    LinkType               linkType;
    private volatile long                   lastSendTime;   //最后数据发送时间
    private volatile long                   sendPackets;    //发送的数据包总数
    private volatile long                   sendPacketsOk;  //发送的数据包总数
    private volatile long                   sendPacketsErr; //发送的数据包总数
    private          List<ReceivedListener> listenerList;   //
    private          CloseListener          closeListener;  //当关闭时
    //
    public RsfChannel(InterAddress target, LinkType linkType) {
        this.protocol = target.getSechma();
        this.target = target;
        this.linkType = linkType;
        this.listenerList = new CopyOnWriteArrayList<ReceivedListener>();
    }
    @Override
    public String toString() {
        return "RsfChannel{" + "protocol=" + this.getProtocol() +//
                ", linkType=" + linkType.name() + '}';
    }
    /**运行的协议*/
    public String getProtocol() {
        return this.protocol;
    }
    //
    //
    /**将数据写入 Netty。*/
    public final void sendData(final RequestInfo info, final SendCallBack callBack) {
        this.sendData(info.getRequestID(), info, callBack);
    }
    /**将数据写入 Netty。*/
    public final void sendData(final ResponseInfo info, final SendCallBack callBack) {
        this.sendData(info.getRequestID(), info, callBack);
    }
    //
    private void sendData(final long requestID, OptionInfo sendData, final SendCallBack callBack) {
        if (!this.isActive()) {
            RsfException e = new RsfException(ProtocolStatus.NetworkError, "send (" + requestID + ") an error, channel is not ready.");
            if (callBack != null) {
                callBack.failed(requestID, e);
            }
            return;
        }
        /*发送数据*/
        this.sendPackets++;
        this.lastSendTime = System.currentTimeMillis();
        this.sendData(sendData, new SendCallBack() {
            private boolean asked = false;
            @Override
            public void failed(long requestID, Throwable ex) {
                if (asked) {
                    return;
                }
                sendPacketsErr++;
                if (callBack != null) {
                    callBack.failed(requestID, new RsfException(ProtocolStatus.NetworkError, ex.getMessage(), ex));
                }
                this.asked = true;
            }
            @Override
            public void complete(long requestID) {
                if (asked) {
                    return;
                }
                sendPacketsOk++;
                if (callBack != null) {
                    callBack.complete(requestID);
                }
                this.asked = true;
            }
        });
    }
    /**接收到数据（受保护的，只有包内可见）*/
    final void receivedData(OptionInfo object) {
        if (!isActive()) {
            return;
        }
        for (ReceivedListener listener : this.listenerList) {
            listener.receivedMessage(this, object);
        }
    }
    /**添加数据接收监听器（受保护的，只有包内可见）*/
    final void addListener(ReceivedListener receivedListener) {
        if (!this.listenerList.contains(receivedListener)) {
            this.listenerList.add(receivedListener);
        }
    }
    //
    //
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
    /**发送的数据包成功数。*/
    public long getSendPacketsOk() {
        return this.sendPacketsOk;
    }
    /**发送的数据包失败数。*/
    public long getSendPacketsErr() {
        return this.sendPacketsErr;
    }
    /**测定连接是否处于激活的。*/
    public abstract boolean isActive();
    /**获取远程连接的地址*/
    public InterAddress getTarget() {
        if (this.target != null) {
            return this.target;
        }
        return null;
    }
    /**关闭连接。*/
    public void close() {
        if (this.isActive()) {
            if (this.closeListener != null) {
                this.closeListener.doClose(this);
            }
            this.closeChannel();
        }
    }
    void onClose(CloseListener closeListener) {
        this.closeListener = closeListener;
    }
    //
    //
    //
    /**判断两个数据通道是相同的*/
    protected boolean equalsSameAs(RsfChannel rsfChannel) {
        return this.target.getHostPort().equals(rsfChannel.target.getHostPort());
    }
    /**关闭网络连接*/
    protected abstract void closeChannel();

    /**发送数据*/
    protected abstract void sendData(OptionInfo sendData, SendCallBack sendCallBack);
}