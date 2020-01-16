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
import net.hasor.tconsole.ConsoleApiBinder;
import net.hasor.tconsole.launcher.hosts.HostServerTest;
import net.hasor.test.tconsole.TestExecutor;
import org.junit.Test;

import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.StringWriter;

public class BasicBinderTest extends HostServerTest {
    @Test
    public void hasor_core_1() {
        AppContext appContext = Hasor.create().asCore().build(apiBinder -> {
            assert apiBinder.tryCast(ConsoleApiBinder.class) == null;
        });
        assert appContext.getBindInfo(InnerExecutorManager.class) == null;
    }

    @Test
    public void hasor_core_2() {
        AppContext appContext = Hasor.create().asCore().build(apiBinder -> {
            apiBinder.installModule(new ConsoleModule());
            assert apiBinder.tryCast(ConsoleApiBinder.class) == null;
        });
        assert appContext.getBindInfo(InnerExecutorManager.class) == null;
    }

    @Test
    public void hasor_core_3() {
        AppContext appContext = Hasor.create().asCore().build(apiBinder -> {
            apiBinder.installModule(new ConsoleModule());
            assert apiBinder.tryCast(ConsoleApiBinder.class) == null;
        });
        assert appContext.getBindInfo(InnerExecutorManager.class) == null;
    }

    @Test
    public void hasor_host_toConstructor_1() {
        // .输入输出流通道
        StringWriter stringWriter = new StringWriter();
        PipedWriter inDataWriter = new PipedWriter();
        //
        // .启动 tConsole 服务
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.tryCast(ConsoleApiBinder.class)                           //
                    .asHost(new PipedReader(inDataWriter), stringWriter)        //
                    .answerExit()                                               //
                    .addExecutor("test").toConstructor(TestExecutor.class.getConstructor());
        });
        //
        // .执行3条命令(每秒钟产生一条命令到 piped)
        Thread copyThread = this.createCopyThread(inDataWriter);
        copyThread.start();
        appContext.join();
        //
        String string = stringWriter.toString();
        assert string.contains("{\"args\":\"\",\"name\":\"test\"}");            // test 命令
    }

    @Test
    public void hasor_host_toInstance_1() {
        // .输入输出流通道
        StringWriter stringWriter = new StringWriter();
        PipedWriter inDataWriter = new PipedWriter();
        //
        // .启动 tConsole 服务
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.tryCast(ConsoleApiBinder.class)                           //
                    .asHost(new PipedReader(inDataWriter), stringWriter)        //
                    .answerExit()                                               //
                    .addExecutor("test").toInstance(new TestExecutor());
        });
        //
        // .执行3条命令(每秒钟产生一条命令到 piped)
        Thread copyThread = this.createCopyThread(inDataWriter);
        copyThread.start();
        appContext.join();
        //
        String string = stringWriter.toString();
        assert string.contains("{\"args\":\"\",\"name\":\"test\"}");            // test 命令
    }
}