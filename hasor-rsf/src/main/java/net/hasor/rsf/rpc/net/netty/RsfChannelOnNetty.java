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
package net.hasor.rsf.rpc.net.netty;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.domain.OptionInfo;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.rpc.net.LinkType;
import net.hasor.rsf.rpc.net.RsfChannel;
import net.hasor.utils.future.FutureCallback;

import java.io.IOException;
/**
 * 封装Netty网络连接。
 * @version : 2015年12月8日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfChannelOnNetty extends RsfChannel {
    private final Channel channel;
    //
    RsfChannelOnNetty(InterAddress target, Channel channel, LinkType linkType) {
        super(target, linkType);
        this.channel = channel;
    }
    //
    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }
    @Override
    protected boolean equalsSameAs(RsfChannel rsfChannel) {
        if (rsfChannel instanceof RsfChannelOnNetty) {
            return this.channel.id().asShortText().equals(((RsfChannelOnNetty) rsfChannel).channel.id().asShortText());
        }
        return false;
    }
    @Override
    protected void closeChannel() {
        this.channel.close();
    }
    @Override
    protected void sendData(OptionInfo sendData, final FutureCallback<?> sendCallBack) {
        final ChannelFuture future = this.channel.writeAndFlush(sendData);
        /*为sendData添加侦听器，负责处理意外情况。*/
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    if (sendCallBack != null) {
                        sendCallBack.completed(null);
                    }
                    return;
                }
                RsfException e = null;
                if (future.isCancelled()) {
                    //用户取消
                    if (sendCallBack != null) {
                        sendCallBack.cancelled();
                    }
                } else if (!future.isSuccess()) {
                    //异常状况
                    if (sendCallBack != null) {
                        sendCallBack.failed(future.cause());
                    }
                }
            }
        });
    }
    /**接收到数据*/
    public void receivedData(OptionInfo object) throws IOException {
        super.receivedData(object);
    }
}