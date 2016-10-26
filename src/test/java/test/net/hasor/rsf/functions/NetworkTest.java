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
package test.net.hasor.rsf.functions;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.hasor.core.Hasor;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.transform.netty.RSFCodec;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
import net.hasor.rsf.utils.NameThreadFactory;
import net.hasor.rsf.utils.NetworkUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class NetworkTest extends ChannelInboundHandlerAdapter {
    //
    private <T extends AbstractBootstrap<?, ?>> T configBoot(T boot) {
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        // boot.option(ChannelOption.SO_BACKLOG, 128);
        // boot.option(ChannelOption.SO_BACKLOG, 1024);
        // boot.option(ChannelOption.SO_RCVBUF, 1024 * 256);
        // boot.option(ChannelOption.SO_SNDBUF, 1024 * 256);
        boot.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return boot;
    }
    protected Channel genChannel() throws IOException, InterruptedException {
        final DefaultRsfEnvironment rsfEnv = new DefaultRsfEnvironment(Hasor.createAppContext().getEnvironment());
        //
        int workerThread = rsfEnv.getSettings().getNetworkWorker();
        InetAddress local = NetworkUtils.finalBindAddress("local");
        NioEventLoopGroup workLoopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s"));
        //
        Bootstrap clientBoot = new Bootstrap();
        clientBoot.group(workLoopGroup);
        clientBoot.channel(NioSocketChannel.class);
        clientBoot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RSFCodec(rsfEnv), new TestChannelInboundHandlerAdapter("Client"));
            }
        });
        return configBoot(clientBoot).connect(new InetSocketAddress(local, 8000)).await().channel();
    }
    //
    public void receivePack() throws IOException, InterruptedException {
        final DefaultRsfEnvironment rsfEnv = new DefaultRsfEnvironment(Hasor.createAppContext().getEnvironment());
        //
        int workerThread = rsfEnv.getSettings().getNetworkWorker();
        int listenerThread = rsfEnv.getSettings().getNetworkListener();
        InetAddress local = NetworkUtils.finalBindAddress("local");
        NioEventLoopGroup workLoopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s"));
        NioEventLoopGroup listenLoopGroup = new NioEventLoopGroup(listenerThread, new NameThreadFactory("RSF-Listen-%s"));
        ServerBootstrap serverBoot = new ServerBootstrap();
        serverBoot.group(listenLoopGroup, workLoopGroup);
        serverBoot.channel(NioServerSocketChannel.class);
        serverBoot.childHandler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RSFCodec(rsfEnv), new TestChannelInboundHandlerAdapter("Server"));
            }
        });
        serverBoot.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        serverBoot.childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = configBoot(serverBoot).bind(local, 8000);
        future.sync();
    }
    @Test
    public void sendPack() throws IOException, InterruptedException {
        //
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Thread thread = new Thread() {
            public void run() {
                try {
                    receivePack();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    atomicBoolean.set(true);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        while (!atomicBoolean.get()) {
            Thread.sleep(10);
        }
        //
        //
        System.out.println(">>>>>>>>> server started. <<<<<<<<<<");
        Channel channel = genChannel();
        //
        for (int i = 0; i <= 10; i++) {
            RequestInfo outRequest = new RequestInfo(RsfConstants.Version_1);
            outRequest.setMessage(i % 2 == 0);
            outRequest.setClientTimeout(1000);
            outRequest.setReceiveTime(System.nanoTime());
            outRequest.setRequestID(System.currentTimeMillis());
            outRequest.setSerializeType("json");
            outRequest.setServiceGroup("Test");
            outRequest.setServiceName("java.util.List");
            outRequest.setServiceVersion("1.0.0");
            outRequest.setTargetMethod("add");
            outRequest.addParameter("java.lang.Object", "aaaa".getBytes());
            channel.write(outRequest);
            channel.flush();
        }
        //
        //
        for (int i = 0; i <= 10; i++) {
            ResponseInfo outResponse = new ResponseInfo(RsfConstants.Version_1);
            outResponse.setSerializeType("json");
            outResponse.setRequestID(System.currentTimeMillis());
            outResponse.setReceiveTime(System.currentTimeMillis());
            outResponse.setReturnData("ok".getBytes());
            outResponse.setStatus((short) 200);
            channel.write(outResponse);
            channel.flush();
        }
        //
        //
        Thread.sleep(5000);
        thread.stop();
    }
}