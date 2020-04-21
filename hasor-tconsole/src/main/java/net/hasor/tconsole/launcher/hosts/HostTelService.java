/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.tconsole.launcher.hosts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.hasor.core.AppContext;
import net.hasor.tconsole.TelAttribute;
import net.hasor.tconsole.TelOptions;
import net.hasor.tconsole.launcher.AbstractTelService;
import net.hasor.tconsole.launcher.TelSessionObject;
import net.hasor.tconsole.launcher.TelUtils;
import net.hasor.tconsole.spi.TelSessionCreateListener;
import net.hasor.tconsole.spi.TelSessionDestroyListener;
import net.hasor.tconsole.spi.TelStopContextListener;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 提供一个可以基于本地模式使用的 Tel 命令工具。
 * HostTelService 无需监听任何 Socket 端口， Tel 的命令交互是通过 Reader、Writer 来实现的。
 * @version : 2019年10月23日
 * @author 赵永春 (zyc@hasor.net)
 */
public class HostTelService extends AbstractTelService implements TelOptions, TelAttribute {
    protected static Logger           logger       = LoggerFactory.getLogger(HostTelService.class);
    private          TelSessionObject telSession   = null; // 会话
    //
    private          Thread           ioCopyThread = null; // IO拷贝线程
    private          BufferedReader   sourceReader = null; // 源头
    private          ByteBuf          dataReader   = null; // 读取缓冲,把源头数据丢入这个 Reader

    /** 内部构造方法，给予子类扩展使用不对外 */
    HostTelService(AppContext appContext) {
        super(appContext);
    }

    /** 创建 Host 模式的 Tel 命令服务 */
    public HostTelService(Reader reader, Writer writer) {
        this(reader, writer, null);
    }

    /** 创建 Host 模式的 Tel 命令服务 */
    public HostTelService(Reader reader, Writer writer, AppContext appContext) {
        super(appContext);
        this.initConstructor(reader, writer);
    }

    void initConstructor(Reader reader, Writer writer) {
        Writer newWriter = new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                writer.write(cbuf, off, len);
            }

            @Override
            public void flush() throws IOException {
                writer.flush();
            }

            @Override
            public void close() {
                HostTelService.this.close();
            }
        };
        this.sourceReader = new BufferedReader(reader);
        this.dataReader = this.getByteBufAllocator().heapBuffer();
        this.telSession = new TelSessionObject(this, this.dataReader, newWriter) {
            public boolean isClose() {
                return !isInit();
            }
        };
    }

    private void printWelcome() {
        if (TelUtils.aBoolean(this.telSession, SILENT)) {
            logger.info("tConsole -> silent, ignore Welcome info.");
            return;
        }
        logger.info("tConsole -> send Welcome info.");
        // Send greeting for a new connection.
        this.telSession.writeMessage("--------------------------------------------\r\n\r\n");
        this.telSession.writeMessage("Welcome to tConsole!\r\n");
        this.telSession.writeMessage("\r\n");
        this.telSession.writeMessage("     login : " + new Date() + " now. form Session\r\n");
        this.telSession.writeMessage("Tips: You can enter a 'help' or 'help -a' for more information.\r\n");
        this.telSession.writeMessage("use the 'exit' or 'quit' out of the console.\r\n");
        this.telSession.writeMessage("--------------------------------------------\r\n");
        this.telSession.writeMessage(CMD);
    }

    public ByteBufAllocator getByteBufAllocator() {
        return ByteBufAllocator.DEFAULT;
    }

    @Override
    protected void doInitialize() {
        super.doInitialize();
        //
        this.ioCopyThread = new Thread(this::doIoCopy);
        this.ioCopyThread.setDaemon(true);
        this.ioCopyThread.setName("tConsole-IoCopy-Thread");
        this.ioCopyThread.start();
        //
        printWelcome();
        //
        // .创建Session
        logger.info("tConsole -> trigger TelSessionListener.sessionCreated");
        this.getSpiTrigger().notifySpiWithoutResult(TelSessionCreateListener.class, listener -> {
            listener.sessionCreated(this.telSession);
        });
    }

    @Override
    protected void doClose() {
        // .销毁Session
        logger.info("tConsole -> trigger TelSessionDestroyListener.sessionDestroyed");
        this.getSpiTrigger().notifySpiWithoutResult(TelSessionDestroyListener.class, listener -> {
            listener.sessionDestroyed(this.telSession);
        });
        super.doClose();
        // .等待线程退出
        logger.info("tConsole -> wait HostTelService exit.");
        while (this.ioCopyThread.getState() != Thread.State.TERMINATED) {
            if (this.ioCopyThread.getState() == Thread.State.TIMED_WAITING) {
                this.ioCopyThread.interrupt();
            }
            try {
                Thread.sleep(50);
            } catch (Exception e) { /**/ }
        }
        logger.info("tConsole -> HostTelService exit.");
    }

    private void doIoCopy() {
        while (isInit()) {
            try {
                // .数据丢入缓冲区缓冲区，然后尝试执行一次。
                dataReader.writeCharSequence(sourceReader.readLine() + "\n", StandardCharsets.UTF_8);
                this.doWork();
            } catch (Exception e) {
                if (!this.isInit() && e instanceof InterruptedIOException) {
                    return;/* readLine阻塞情况下被中断 */
                }
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void doWork() {
        int lastBufferSize = this.telSession.buffSize();
        while (this.telSession.tryReceiveEvent()) {
            if (lastBufferSize == this.telSession.buffSize()) {
                break;
            }
            lastBufferSize = this.telSession.buffSize();
        }
        //
        if (this.telSession.buffSize() == 0) {
            boolean noSilent = !TelUtils.aBoolean(this.telSession, SILENT);
            if (noSilent) {
                this.telSession.writeMessage(CMD);
            } else {
                String codeOfSilent = TelUtils.aString(this.telSession, ENDCODE_OF_SILENT);
                if (StringUtils.isNotBlank(codeOfSilent)) {
                    this.telSession.writeMessageLine(codeOfSilent);
                }
            }
        }
    }

    @Override
    public Object getAttribute(String key) {
        return this.telSession.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        this.telSession.setAttribute(key, value);
    }

    @Override
    public Set<String> getAttributeNames() {
        return this.telSession.getAttributeNames();
    }

    public void silent() {
        this.setAttribute(TelOptions.SILENT, true);//静默输出
    }

    public boolean isSilent() {
        return TelUtils.aBoolean(this, TelOptions.SILENT);//静默输出
    }

    public void endcodeOfSilent(String endcode) {
        this.setAttribute(TelOptions.ENDCODE_OF_SILENT, endcode);//结束符
    }

    public String endcodeOfSilent() {
        return TelUtils.aString(this, TelOptions.ENDCODE_OF_SILENT);//结束符
    }

    public void sendCommand(String message) throws IOException {
        tryShutdown();
        if (StringUtils.isNotBlank(message)) {
            this.dataReader.writeCharSequence(message + "\n", StandardCharsets.UTF_8);
            this.doWork();
        }
    }

    private void tryShutdown() {
        if (!this.isInit()) {
            throw new IllegalStateException("the container is not started yet.");
        }
    }

    public void join() {
        this.join(0, null);
    }

    public void join(long timeout, TimeUnit unit) {
        tryShutdown();
        // .当收到 Shutdown 事件时退出 join
        BasicFuture<Object> future = new BasicFuture<>();
        this.addListener(TelStopContextListener.class, telSession -> future.completed(new Object()));
        //
        // .阻塞进程
        joinAt(future, timeout, unit);
    }

    // .阻塞进程
    private void joinAt(BasicFuture<Object> future, long timeout, TimeUnit unit) {
        try {
            if (unit == null) {
                logger.debug("tConsole -> joinAt none.");
                future.get();
            } else {
                logger.debug("tConsole -> joinAt unit=" + unit.name() + " ,timeout=" + timeout);
                future.get(timeout, unit);
            }
        } catch (ExecutionException e) {
            throw ExceptionUtils.toRuntimeException(e.getCause());
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    @Override
    public boolean isHost() {
        return true;
    }
}