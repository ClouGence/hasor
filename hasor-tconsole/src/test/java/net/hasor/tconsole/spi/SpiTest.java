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
package net.hasor.tconsole.spi;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.tconsole.AbstractTelTest;
import net.hasor.tconsole.client.TelClient;
import net.hasor.tconsole.launcher.hosts.PipedHostTelService;
import net.hasor.tconsole.launcher.telnet.TelnetTelService;
import net.hasor.test.beans.*;
import org.junit.Test;

import java.io.StringWriter;
import java.net.InetSocketAddress;

public class SpiTest extends AbstractTelTest {
    @Test
    public void executor_listener_1() throws Exception {
        ExecutorListenerBean executorListener = new ExecutorListenerBean();
        InBoundMatcherBean inBoundMatcher = new InBoundMatcherBean();
        //
        try (TelnetTelService server = new TelnetTelService("127.0.0.1", 8082, inBoundMatcher)) {
            server.addListener(TelExecutorListener.class, executorListener);
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
    public void close_listener_1() throws Exception {
        CloseListenerBean closeListener = new CloseListenerBean();
        try (TelnetTelService server = new TelnetTelService("127.0.0.1", 8082, s -> true)) {
            server.addCommand("test", new TestExecutor());
            server.addListener(TelCloseEventListener.class, closeListener);
            server.init();
            //
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.init();
            assert client.remoteAddress().getPort() == 8082;
            client.sendCommand("quit -t3");
        }
        //
        assert closeListener.getTrigger() != null;
        assert closeListener.getAfterSeconds() == 3;
    }

    @Test
    public void context_listener_1() throws Exception {
        TelContextListenerBean contextListener = new TelContextListenerBean();
        AppContext appContext = Hasor.create().asCore().build(apiBinder -> {
            apiBinder.bindSpiListener(TelContextListener.class, contextListener);
        });
        //
        try (TelnetTelService server = new TelnetTelService("127.0.0.1", 8082, s -> true, appContext)) {
            assert contextListener.getContextListener() == null;
            //
            server.init();
            assert contextListener.getContextListener();
        }
        assert !contextListener.getContextListener();
    }

    @Test
    public void context_listener_2() throws Exception {
        TelContextListenerBean contextListener = new TelContextListenerBean();
        AppContext appContext = Hasor.create().asCore().build(apiBinder -> {
            apiBinder.bindSpiListener(TelContextListener.class, contextListener);
        });
        //
        StringWriter stringWriter = new StringWriter();
        PipedHostTelService pipedHostTelService = new PipedHostTelService(appContext, stringWriter);
        //
        assert contextListener.getContextListener() == null;
        pipedHostTelService.init();
        assert contextListener.getContextListener();
        pipedHostTelService.close();
        assert !contextListener.getContextListener();
    }

    @Test
    public void session_listener_1() throws Exception {
        TelSessionListenerBean listenerBean = new TelSessionListenerBean();
        AppContext appContext = Hasor.create().asCore().build(apiBinder -> {
            apiBinder.bindSpiListener(TelSessionListener.class, listenerBean);
        });
        //
        try (TelnetTelService server = new TelnetTelService("127.0.0.1", 8082, s -> true, appContext)) {
            server.addCommand("test", new TestExecutor());
            server.init();
            //
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.init();
            assert listenerBean.size() == 1;
            //
            client.sendCommand("quit -t3");
            assert !client.isInit();
        }
        //
        assert listenerBean.size() == 0;
    }

    @Test
    public void session_listener_2() throws Exception {
        TelSessionListenerBean listenerBean = new TelSessionListenerBean();
        AppContext appContext = Hasor.create().asCore().build(apiBinder -> {
            apiBinder.bindSpiListener(TelSessionListener.class, listenerBean);
        });
        //
        StringWriter stringWriter = new StringWriter();
        PipedHostTelService pipedHostTelService = new PipedHostTelService(appContext, stringWriter);
        //
        assert listenerBean.size() == 0;
        pipedHostTelService.init();
        assert listenerBean.size() == 1;
        pipedHostTelService.close();
        assert listenerBean.size() == 0;
    }
}