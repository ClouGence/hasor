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
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.utils.NameThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Netty 线程组。
 * @version : 2017年01月16日
 * @author 赵永春(zyc@hasor.net)
 */
class NettyThreadGroup {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private EventLoopGroup    workLoopGroup;    // I/O线程
    private NioEventLoopGroup listenLoopGroup;  // 监听线程
    //
    NettyThreadGroup(String protocol, RsfEnvironment rsfEnvironment) {
        RsfSettings rsfSettings = rsfEnvironment.getSettings();
        String configKey = rsfSettings.getProtocolConfigKey(protocol);
        int workerThread = rsfSettings.getInteger(configKey + ".workerThread", 8);
        int listenerThread = rsfSettings.getInteger(configKey + ".listenThread", 1);
        //
        this.logger.info("nioEventLoopGroup, workerThread = {} , listenerThread = {}", workerThread, listenerThread);
        this.workLoopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s", rsfEnvironment.getClassLoader()));
        this.listenLoopGroup = new NioEventLoopGroup(listenerThread, new NameThreadFactory("RSF-Listen-%s", rsfEnvironment.getClassLoader()));
        //
    }
    public void shutdownGracefully() {
        logger.info("shutdownGracefully -> nioEventLoopGroup (listenLoopGroup and workLoopGroup).");
        listenLoopGroup.shutdownGracefully();
        workLoopGroup.shutdownGracefully();
    }
    //
    public EventLoopGroup getWorkLoopGroup() {
        return this.workLoopGroup;
    }
    public NioEventLoopGroup getListenLoopGroup() {
        return this.listenLoopGroup;
    }
}