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
package net.hasor.tconsole.binder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.tconsole.client.TelClient;
import net.hasor.tconsole.launcher.hosts.HostServerTest;
import net.hasor.test.beans.TestExecutor;
import org.junit.Test;

import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;

public class FunctionBinderTest extends HostServerTest {
    @Test
    public void hasor_host_test_1() {
        // .输入输出流通道
        StringWriter stringWriter = new StringWriter();
        PipedWriter inDataWriter = new PipedWriter();
        //
        // .启动 tConsole 服务
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.tryCast(ConsoleApiBinder.class)                           //
                    .asHost(new PipedReader(inDataWriter), stringWriter)        //
                    .answerExit()                                               //
                    .addExecutor("test").to(TestExecutor.class);
        });
        //
        // .执行3条命令(每秒钟产生一条命令到 piped)
        Thread copyThread = this.createCopyThread(inDataWriter);
        copyThread.start();
        appContext.join();
        //
        String string = stringWriter.toString();
        assert string.contains("use the 'exit' or 'quit' out of the console."); // 非静默模式，所以有欢迎信息
        assert string.contains(" - test  hello help.");                         // help 命令
        assert string.contains("{\"args\":\"\",\"name\":\"test\"}");            // test 命令
        assert string.contains("bye.");                                         // exit 命令
    }

    @Test
    public void hasor_host_test_2() {
        // .输入输出流通道
        StringWriter stringWriter = new StringWriter();
        PipedWriter inDataWriter = new PipedWriter();
        //
        // .启动 tConsole 服务
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.tryCast(ConsoleApiBinder.class)//
                    .asHost(new PipedReader(inDataWriter), stringWriter)//
                    .preCommand("exit -next", "test")// 执行两个命令后自动结束
                    .answerExit()// 响应 exit 指令自动退出容器
                    .addExecutor("test").to(TestExecutor.class);
        });
        while (appContext.isStart()) {
            try {
                Thread.sleep(100); //等待结束
            } catch (Exception e) {/**/}
        }
        //
        assert !appContext.isStart();
        //
        String string = stringWriter.toString();
        assert string.contains("use the 'exit' or 'quit' out of the console."); // 非静默模式，所以有欢迎信息
        assert string.contains("{\"args\":\"\",\"name\":\"test\"}");            // test 命令
        assert string.contains("bye.");                                         // exit 命令
    }

    @Test
    public void hasor_host_test_3() {
        // .输入输出流通道
        StringWriter stringWriter = new StringWriter();
        PipedWriter inDataWriter = new PipedWriter();
        //
        // .启动 tConsole 服务
        AppContext appContext = Hasor.create().build(apiBinder -> {
            ConsoleApiBinder.HostBuilder asHost = apiBinder.tryCast(ConsoleApiBinder.class)//
                    .asHost(new PipedReader(inDataWriter), stringWriter);
            //
            asHost.setAttribute("abc", "aaa");
            asHost.setAttribute("bcd", "bbb");
            assert asHost.getAttribute("bcd").equals("bbb");
            assert asHost.getAttributeNames().size() == 2;
            asHost.preCommand("exit -next", "get abc")// 执行两个命令后自动结束
                    .answerExit()// 响应 exit 指令自动退出容器
                    .addExecutor("test").to(TestExecutor.class);
        });
        while (appContext.isStart()) {
            try {
                Thread.sleep(100); //等待结束
            } catch (Exception e) {/**/}
        }
        //
        assert !appContext.isStart();
        //
        String string = stringWriter.toString();
        assert string.contains("use the 'exit' or 'quit' out of the console."); // 非静默模式，所以有欢迎信息
        assert string.contains("aaa");                                          // get abc 命令
        assert string.contains("bye.");                                         // exit 命令
    }

    @Test
    public void hasor_host_silent_3() throws InterruptedException {
        // .输入输出流通道
        StringWriter stringWriter = new StringWriter();
        PipedWriter inDataWriter = new PipedWriter();
        //
        // .启动 tConsole 服务
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.tryCast(ConsoleApiBinder.class)//
                    .asHost(new PipedReader(inDataWriter), stringWriter)//
                    .silent()//
                    .preCommand("exit -next", "set abc = 123", "get abc")// 执行两个命令后 不会自动结束
                    .addExecutor("test").to(TestExecutor.class);
        });
        //
        Thread.sleep(500); // 等待 100ms 让preCommand 开始执行
        //
        String string = stringWriter.toString();
        assert string.trim().equals("123");
        assert appContext.isStart();
        appContext.shutdown();
    }

    @Test
    public void hasor_telnet_test_1() {
        // .启动 tConsole 服务
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.tryCast(ConsoleApiBinder.class)//
                    .asTelnet("127.0.0.1", 8082)//
                    .addExecutor("test").to(TestExecutor.class);
        });
        //
        TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
        client.init();
        assert client.remoteAddress().getPort() == 8082;
        //
        String help = client.sendCommand("help");
        assert help.contains("- exit  out of console.");
        assert help.contains("- set   set/get environment variables of console.");
        assert help.contains("- test  hello help.");
        //
        String exit = client.sendCommand("exit");
        assert exit.equals("");
        assert !client.isInit();
        //
        appContext.shutdown();
    }
}