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
package net.hasor.tconsole;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.tconsole.launcher.TelConsoleServer;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.Writer;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class AbstractTelTest {
    public TelConsoleServer mockTelContext() {
        return mockTelContext(null, false);
    }

    public TelConsoleServer mockTelContext(TelExecutor executor) {
        return mockTelContext(executor, true);
    }

    private TelConsoleServer mockTelContext(TelExecutor executor, boolean hasExecutor) {
        AppContext appContext = Hasor.create().asCore().build();
        //
        TelConsoleServer telContext = PowerMockito.mock(TelConsoleServer.class);
        PowerMockito.when(telContext.getByteBufAllocator()).thenReturn(PooledByteBufAllocator.DEFAULT);
        PowerMockito.when(telContext.getSpiTrigger()).thenReturn(appContext.getInstance(SpiTrigger.class));
        PowerMockito.doAnswer(invocation -> {
            appContext.getEnvironment().getEventContext().asyncTask((Runnable) invocation.getArguments()[0]);
            return null;
        }).when(telContext).asyncExecute(any());
        //
        if (hasExecutor) {
            PowerMockito.when(telContext.findCommand(anyString())).thenReturn(executor);
        }
        return telContext;
    }

    protected ChannelHandlerContext mockNetty(Writer dataWriter) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        //
        Channel channel = PowerMockito.mock(Channel.class);
        PowerMockito.when(channel.localAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 12345));
        PowerMockito.when(channel.remoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 54321));
        PowerMockito.when(channel.write(anyString())).then(invocation -> {
            dataWriter.write(invocation.getArguments()[0].toString());
            return null;
        });
        PowerMockito.when(channel.writeAndFlush(anyString())).then(invocation -> {
            dataWriter.write(invocation.getArguments()[0].toString());
            return null;
        });
        PowerMockito.doAnswer(invocation -> {
            dataWriter.flush();
            return null;
        }).when(channel).flush();
        PowerMockito.doAnswer(invocation -> {
            dataWriter.close();
            atomicBoolean.set(false);
            return null;
        }).when(channel).close();
        //
        PowerMockito.when(channel.isActive()).then((Answer<Boolean>) invocation -> atomicBoolean.get());
        //
        ChannelHandlerContext context = PowerMockito.mock(ChannelHandlerContext.class);
        PowerMockito.when(context.channel()).thenReturn(channel);
        return context;
    }
}