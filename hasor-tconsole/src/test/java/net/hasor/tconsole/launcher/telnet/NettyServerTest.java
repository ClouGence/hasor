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
package net.hasor.tconsole.launcher.telnet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.tconsole.AbstractTelTest;
import net.hasor.tconsole.client.TelClient;
import net.hasor.tconsole.client.TelClientEventListener;
import net.hasor.tconsole.client.TelClientHandler;
import net.hasor.tconsole.spi.TelCloseEventListener;
import net.hasor.tconsole.spi.TelExecutorListener;
import net.hasor.tconsole.spi.TelSessionListener;
import net.hasor.test.beans.*;
import net.hasor.utils.future.BasicFuture;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

public class NettyServerTest extends AbstractTelTest {
    @Test
    public void serverTest_1() throws Exception {
        try (TellnetTelService server = new TellnetTelService("127.0.0.1", 8082, s -> true)) {
            server.addCommand("test", new TestExecutor());
            server.init();
            assert server.isInit();
            //
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("127.0.0.1", 8082), 1000);
            //
            assert true;
            //
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.init();
            Thread.sleep(500);
            //
            String help = client.sendCommand("help");
            assert help.contains("- exit  out of console.");
            assert help.contains("- set   set/get environment variables of console.");
            assert help.contains("- test  hello help.");
            //
            String exit = client.sendCommand("exit");
            assert exit.equals("");
            assert !client.isInit();
        }
    }

    @Test
    public void clientCoverage_1() {
        ChannelHandlerContext context = PowerMockito.mock(ChannelHandlerContext.class);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        TelClientEventListener closeFuture = () -> atomicBoolean.set(true);
        BasicFuture<Object> future = new BasicFuture<>();
        TelClientHandler handler = new TelClientHandler("aaa", future, closeFuture, PowerMockito.mock(ByteBuf.class));
        //
        Exception exception = new Exception("sss");
        handler.exceptionCaught(context, exception);
        assert atomicBoolean.get();
        try {
            future.get();
            assert false;
        } catch (Exception e) {
            assert e.getCause() == exception;
        }
    }

    @Test
    public void spiCoverage_1() throws UnknownHostException, InterruptedException {
        ExecutorListenerBean executorListener = new ExecutorListenerBean();
        InBoundMatcherBean inBoundMatcher = new InBoundMatcherBean();
        AppContext appContext = Hasor.create().asCore().build(apiBinder -> {
            apiBinder.bindSpiListener(TelExecutorListener.class, executorListener);
        });
        //
        try (TellnetTelService server = new TellnetTelService("127.0.0.1", 8082, inBoundMatcher, appContext)) {
            server.addCommand("test", new TestExecutor());
            server.init();
            //
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.init();
            //
            assert inBoundMatcher.contains("127.0.0.1");
            assert executorListener.getBeforeExecCommand().size() == 2; // client 链接上的时候会发送两个 set 指令
            assert executorListener.getAfterExecCommand().size() == 2;  // client 链接上的时候会发送两个 set 指令
            //
            client.sendCommand("set a=abc");
            assert executorListener.getBeforeExecCommand().size() == 3;
            assert executorListener.getAfterExecCommand().size() == 3;
            assert executorListener.getBeforeExecCommand().get(2).getCommandName().equals("set");
            assert executorListener.getAfterExecCommand().get(2).getCommandName().equals("set");
            //
            String get = client.sendCommand("get a");
            assert get.equals("abc");
            assert executorListener.getBeforeExecCommand().size() == 4;
            assert executorListener.getAfterExecCommand().size() == 4;
            assert executorListener.getBeforeExecCommand().get(3).getCommandName().equals("get");
            assert executorListener.getAfterExecCommand().get(3).getCommandName().equals("get");
            //
            client.sendCommand("quit");
            assert !client.isInit();
        }
    }

    @Test
    public void spiCoverage_2() throws UnknownHostException, InterruptedException {
        CloseListenerBean closeListener = new CloseListenerBean();
        TelSessionListenerBean listenerBean = new TelSessionListenerBean();
        AppContext appContext = Hasor.create().asCore().build(apiBinder -> {
            apiBinder.bindSpiListener(TelCloseEventListener.class, closeListener);
            apiBinder.bindSpiListener(TelSessionListener.class, listenerBean);
        });
        //
        try (TellnetTelService server = new TellnetTelService("127.0.0.1", 8082, s -> true, appContext)) {
            server.addCommand("test", new TestExecutor());
            server.init();
            //
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.init();
            //
            assert listenerBean.size() == 1;
            //
            client.sendCommand("quit -t3");
        }
        //
        assert listenerBean.size() == 0;
        assert closeListener.getTrigger() != null;
        assert closeListener.getAfterSeconds() == 3;
    }
}