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
import io.netty.channel.nio.NioEventLoopGroup;
import net.hasor.rsf.InterAddress;

import java.util.concurrent.Future;
/**
 * RPC协议连接器，负责创建某个特定RPC协议的网络事件。
 * @version : 2017年01月16日
 * @author 赵永春(zyc@hasor.net)
 */
public interface Connector {
    /** 连接器协议名称 */
    public String getProtocol();

    /** 监听的本地端口号 */
    public InterAddress getBindAddress();

    /** 如果工作在内网，这里返回配置的外网映射地址 */
    public InterAddress getGatewayAddress();

    /** 获取RSF运行的网关地址（如果有），或者本地绑定地址。 */
    public InterAddress getPublishAddress();

    /**
     * 启动本地监听器
     * @param listenLoopGroup 监听器线程组
     */
    public void startListener(NioEventLoopGroup listenLoopGroup);

    /**停止监听器*/
    public void shutdown();

    /** 建立或获取和远程的连接(异步+回调) */
    public Future<RsfChannel> getChannel(InterAddress target) throws InterruptedException;
    //    /** 设定别名 */
    //    public void mappingTo(RsfChannel rsfChannel, InterAddress interAddress);
}