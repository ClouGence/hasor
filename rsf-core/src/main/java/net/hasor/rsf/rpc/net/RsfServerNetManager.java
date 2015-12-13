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
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.transform.netty.RSFCodec;
import net.hasor.rsf.utils.NameThreadFactory;
import net.hasor.rsf.utils.NetworkUtils;
/**
 * 维护RSF同其它RSF的连接，并提供数据投递和接收服务。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfServerNetManager extends RsfNetManager {
    public RsfServerNetManager(RsfEnvironment rsfEnvironment, ReceivedListener listener) {
        super(rsfEnvironment, listener);
    }
    /**启动服务器。*/
    public void start(InetAddress localAddress, int bindSocket) throws UnknownHostException, URISyntaxException {
        RsfSettings rsfSettings = this.getRsfEnvironment().getSettings();
        if (localAddress == null) {
            localAddress = NetworkUtils.finalBindAddress(rsfSettings.getBindAddress());
        }
        final InterAddress hostAddress = new InterAddress(localAddress.getHostAddress(), bindSocket, rsfSettings.getUnitName());
        int listenerThread = this.getRsfEnvironment().getSettings().getNetworkListener();
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup(listenerThread, new NameThreadFactory("RSF-Listen-%s"));
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(bossGroup, this.getWorkLoopGroup());
        boot.channel(NioServerSocketChannel.class);
        boot.childHandler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                logger.info("initConnection connect {}.", hostAddress);
                ch.pipeline().addLast(new RSFCodec(), new RpcCodec(getListener()));
            }
        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
        //
        ChannelFuture future = boot.bind(localAddress, bindSocket);
        final Channel serverChannel = future.channel();
        logger.info("rsf Server started at :{}:{}", localAddress, bindSocket);
    }
}