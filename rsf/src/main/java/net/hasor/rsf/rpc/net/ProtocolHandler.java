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
import io.netty.channel.ChannelHandler;
import net.hasor.core.AppContext;
/**
 * RSF协议扩展
 * @version : 2017年01月22日
 * @author 赵永春(zyc@hasor.net)
 */
public interface ProtocolHandler {
    /**
     * 是否允许这个新连接。IP黑白名单策略。请激活{@link RsfChannel#activeIn()}。
     * 如果没有激活这个连接，所有接收的数据包都会被忽略，同时当试图往这个连接中发数据时也会引发异常导致关闭这个连接。
     * tips：手动执行激活，可以让 开发者在 RPC 调用之前有机会实现握手协议。
     * @param connector 归属的连接器
     * @param rsfChannel 连接对象
     * @return 返回值决定了是否拒绝该连接。
     */
    public boolean acceptIn(Connector connector, RsfChannel rsfChannel) throws Exception;

    /** 编码器 & 解码器 */
    public ChannelHandler[] channelHandler(Connector connector, AppContext appContext);
}