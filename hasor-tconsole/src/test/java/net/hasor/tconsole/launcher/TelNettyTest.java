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
import io.netty.channel.ChannelHandlerContext;
import net.hasor.tconsole.AbstractTelTest;
import net.hasor.tconsole.TelContext;
import net.hasor.tconsole.commands.QuitExecutor;
import net.hasor.test.beans.TestExecutor;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

public class TelNettyTest extends AbstractTelTest {
    @Test
    public void allowTest_1() throws Exception {
        TestExecutor testExecutor = new TestExecutor();
        AtomicBoolean close = new AtomicBoolean(false);
        Writer dataWriter = new StringWriter() {
            @Override
            public void close() throws IOException {
                super.close();
                close.set(true);
            }
        };
        //
        TelContext telContext = mockTelContext(testExecutor);
        ChannelHandlerContext context = mockNetty(dataWriter);
        //
        //
        TelNettyHandler handler = new TelNettyHandler(telContext, null);
        handler.channelActive(context);
        handler.channelRead(context, "set name=abc age=13 \n aaa");
        //
        assert !dataWriter.toString().contains("Welcome to tConsole!");
        Thread.sleep(1000);
        assert dataWriter.toString().contains("Welcome to tConsole!");
        assert !close.get();
    }

    @Test
    public void allowTest_2() throws Exception {
        AtomicBoolean close = new AtomicBoolean(false);
        Writer dataWriter = new StringWriter() {
            @Override
            public void close() throws IOException {
                super.close();
                close.set(true);
            }
        };
        //
        TelContext telContext = mockTelContext(new QuitExecutor());
        ChannelHandlerContext context = mockNetty(dataWriter);
        //
        //
        TelNettyHandler handler = new TelNettyHandler(telContext, null);
        handler.channelActive(context);
        handler.channelRead(context, "set name=abc age=13 \n aaa");
        //
        assert !dataWriter.toString().contains("Welcome to tConsole!");
        Thread.sleep(1000);
        assert dataWriter.toString().contains("Welcome to tConsole!");
        assert close.get();
    }

    @Test
    public void rejectTest_1() throws Exception {
        TestExecutor testExecutor = new TestExecutor();
        AtomicBoolean close = new AtomicBoolean(false);
        Writer dataWriter = new StringWriter() {
            @Override
            public void close() throws IOException {
                super.close();
                close.set(true);
            }
        };
        //
        TelContext telContext = mockTelContext(testExecutor);
        ChannelHandlerContext context = mockNetty(dataWriter);
        //
        //
        TelNettyHandler handler = new TelNettyHandler(telContext, "192.168.8.8"::equals);
        handler.channelActive(context);
        handler.channelRead(context, "set name=abc age=13 \n aaa");
        //
        assert dataWriter.toString().contains("I'm sorry you are not allowed to connect tConsole.");
        assert close.get();
    }
}