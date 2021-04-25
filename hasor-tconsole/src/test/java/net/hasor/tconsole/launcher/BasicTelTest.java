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
package net.hasor.tconsole.launcher;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import net.hasor.tconsole.AbstractTelTest;
import net.hasor.tconsole.commands.QuitExecutor;
import net.hasor.tconsole.launcher.telnet.TelnetTelService;
import net.hasor.test.tconsole.EchoSessionIDExecutor;
import net.hasor.test.tconsole.ErrorExecutor;
import net.hasor.test.tconsole.TestExecutor;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class BasicTelTest extends AbstractTelTest {
    @Test
    public void basic_test_1() {
        ByteBuf dataReader = PooledByteBufAllocator.DEFAULT.heapBuffer();
        dataReader.writeCharSequence("set name=abc age=13 \n aaa", StandardCharsets.UTF_8);
        //
        TestExecutor testExecutor = new TestExecutor();
        TelnetTelService telContext = mockTelContext(testExecutor);
        //
        Writer dataWriter = new StringWriter();
        TelSessionObject sessionObject = new TelSessionObject(telContext, dataReader, dataWriter) {
            @Override
            public boolean isClose() {
                return false;
            }
        };
        //
        while (!testExecutor.isDoCommand()) {
            sessionObject.tryReceiveEvent();
        }
        //
        JSONObject jsonObject = JSONObject.parseObject(dataWriter.toString().split("\r\n--------------\r\n")[0]);
        assert jsonObject.getString("name").equals("set");
        assert jsonObject.getString("args").equals("name=abc,age=13");
        assert jsonObject.getString("body") == null;
    }

    @Test
    public void echo_sessionID_test_1() {
        ByteBuf dataReader = PooledByteBufAllocator.DEFAULT.heapBuffer();
        dataReader.writeCharSequence("set name=abc age=13 \n aaa", StandardCharsets.UTF_8);
        //
        TelnetTelService telContext = mockTelContext(new EchoSessionIDExecutor());
        //
        Writer dataWriter = new StringWriter();
        TelSessionObject sessionObject = new TelSessionObject(telContext, dataReader, dataWriter) {
            @Override
            public boolean isClose() {
                return false;
            }
        };
        //
        sessionObject.tryReceiveEvent();
        //
        assert dataWriter.toString().contains(sessionObject.getSessionID());
    }

    @Test
    public void bad_test_1() {
        ByteBuf dataReader = PooledByteBufAllocator.DEFAULT.heapBuffer();
        dataReader.writeCharSequence("set name=abc age=13 \n aaa", StandardCharsets.UTF_8);
        //
        TelnetTelService telContext = mockTelContext(null);
        //
        Writer dataWriter = new StringWriter();
        TelSessionObject sessionObject = new TelSessionObject(telContext, dataReader, dataWriter) {
            @Override
            public boolean isClose() {
                return false;
            }
        };
        //
        sessionObject.tryReceiveEvent();
        //
        assert dataWriter.toString().contains("'set' is bad command.");
    }

    @Test
    public void empty_test_1() {
        ByteBuf dataReader = PooledByteBufAllocator.DEFAULT.heapBuffer();
        dataReader.writeCharSequence("\n\n\n\n", StandardCharsets.UTF_8);
        //
        TelnetTelService telContext = mockTelContext(null);
        //
        Writer dataWriter = new StringWriter();
        TelSessionObject sessionObject = new TelSessionObject(telContext, dataReader, dataWriter) {
            @Override
            public boolean isClose() {
                return false;
            }
        };
        //
        while (sessionObject.tryReceiveEvent()) {
        }
        assert dataWriter.toString().equals("");
    }

    @Test
    public void error_test_1() {
        ByteBuf dataReader = PooledByteBufAllocator.DEFAULT.heapBuffer();
        dataReader.writeCharSequence("set name=abc age=13 \n aaa", StandardCharsets.UTF_8);
        //
        TelnetTelService telContext = mockTelContext(new ErrorExecutor());
        //
        Writer dataWriter = new StringWriter();
        TelSessionObject sessionObject = new TelSessionObject(telContext, dataReader, dataWriter) {
            @Override
            public boolean isClose() {
                return false;
            }
        };
        //
        sessionObject.tryReceiveEvent();
        //
        assert dataWriter.toString().startsWith("java.lang.RuntimeException: error message form test");
        assert sessionObject.currentCounter() == 1;
    }

    @Test
    public void close_test_1() {
        ByteBuf dataReader = PooledByteBufAllocator.DEFAULT.heapBuffer();
        dataReader.writeCharSequence("close -t3 \n aaa", StandardCharsets.UTF_8);
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
        assert (end_t - start_t) >= 3000;
        assert toString.contains("exit after 3 seconds.");
        assert toString.contains("exit after 2 seconds.");
        assert toString.contains("exit after 1 seconds.");
        assert toString.contains("bye.\r\n");
        assert sessionObject.currentCounter() == 1;
    }
}