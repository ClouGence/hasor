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
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import net.hasor.core.AppContext;
/**
 * RSF协议扩展
 * @version : 2017年01月22日
 * @author 赵永春(zyc@hasor.net)
 */
public interface ProtocolHandler {
    /**
     * 是否允许这个新连接。IP黑白名单策略。
     * @param connector 归属的连接器
     * @param channel 连接对象
     * @return 返回值决定了是否拒绝该连接。
     */
    public boolean acceptIn(Connector connector, Channel channel);

    public void active(RsfChannel rsfChannel);

    /** 解码器 */
    public ChannelInboundHandler decoder(AppContext appContext);

    /** 编码器 */
    public ChannelOutboundHandler encoder(AppContext appContext);
}