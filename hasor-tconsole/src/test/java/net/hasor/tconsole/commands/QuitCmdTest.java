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
package net.hasor.tconsole.commands;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import net.hasor.tconsole.AbstractTelTest;
import net.hasor.tconsole.client.TelClient;
import net.hasor.tconsole.launcher.TelSessionObject;
import net.hasor.tconsole.launcher.telnet.TelnetTelService;
import net.hasor.test.beans.TestExecutor;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuitCmdTest extends AbstractTelTest {
    @Test
    public void close_test_1() {
        ByteBuf dataReader = PooledByteBufAllocator.DEFAULT.heapBuffer();
        dataReader.writeCharSequence("close -t-3 \n aaa", StandardCharsets.UTF_8); // bad
        //
        TelnetTelService telContext = mockTelContext(new QuitExecutor());
        //
        AtomicBoolean closeTag = new AtomicBoolean(false);
        Writer dataWriter = new StringWriter() {
            @Override
            public void close() throws IOException {
                super.close();
                closeTag.set(true);
            }
        };
        TelSessionObject sessionObject = new TelSessionObject(telContext, dataReader, dataWriter) {
            public boolean isClose() {
                return closeTag.get();
            }
        };
        //
        long start_t = System.currentTimeMillis();
        sessionObject.tryReceiveEvent();
        long end_t = System.currentTimeMillis();
        String toString = dataWriter.toString();
        //
        assert (end_t - start_t) < 3000;
        assert !toString.contains("exit after 3 seconds.");
        assert !toString.contains("exit after 2 seconds.");
        assert !toString.contains("exit after 1 seconds.");
        assert toString.contains("bye.\r\n");
        assert sessionObject.curentCounter() == 1;
    }

    @Test
    public void autoexit_test_1() throws Exception {
        try (TelnetTelService server = new TelnetTelService("127.0.0.1", 8082, s -> true)) {
            server.addCommand("test", new TestExecutor());
            //
            server.init();
            //
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.init();
            //
            client.sendCommand("set a=asd");
            client.sendCommand("exit -next");
            String vat_a = client.sendCommand("get a");
            assert vat_a.trim().equals("asd");
            assert !client.isInit(); // exit -next 生效
        }
    }

    @Test
    public void exit_n_test_1() throws Exception {
        try (TelnetTelService server = new TelnetTelService("127.0.0.1", 8082, s -> true)) {
            server.addCommand("test", new TestExecutor());
            server.init();
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.init();
            client.sendCommand("exit -n3");
            //
            String res1 = client.sendCommand("set a=asd");
            String res2 = client.sendCommand("set a=123");
            String res3 = client.sendCommand("get a");
            assert !client.isInit();
            assert res3.trim().equals("123");
        }
    }
}