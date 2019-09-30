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
import io.netty.buffer.ByteBuf;
import net.hasor.tconsole.TelContext;
import net.hasor.tconsole.TelExecutor;
import net.hasor.tconsole.TelPhase;
import net.hasor.tconsole.TelSession;
import net.hasor.tconsole.spi.CloseListener;
import net.hasor.tconsole.spi.ExecutorListener;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static net.hasor.tconsole.TelOptions.*;
import static net.hasor.tconsole.launcher.TelUtils.aBoolean;
import static net.hasor.tconsole.launcher.TelUtils.aInteger;

/**
 *
 * @version : 2016年4月3日
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class TelSessionObject extends AttributeObject implements TelSession {
    private static Logger           logger = LoggerFactory.getLogger(TelSessionObject.class);
    private        String           sessionID;     //
    private        TelReaderObject  dataReader;    // 输入流
    private        Writer           dataWriter;    // 输出流
    //
    private        TelConsoleServer telContext;    //
    private        TelCommandObject curentCommand; // 当前命令
    private        AtomicInteger    atomicInteger; // 指令计数器

    TelSessionObject(TelConsoleServer telContext, ByteBuf dataReader, Writer dataWriter) {
        this.sessionID = UUID.randomUUID().toString().replace("-", "");
        this.telContext = telContext;
        this.dataReader = new TelReaderObject(telContext.getByteBufAllocator(), dataReader);
        this.dataWriter = dataWriter;
        this.atomicInteger = new AtomicInteger(0);
    }

    @Override
    public String getSessionID() {
        return this.sessionID;
    }

    @Override
    public int curentCounter() {
        return this.atomicInteger.get();
    }

    /** 当前预缓冲区的大小 */
    public int buffSize() {
        return this.dataReader.getBuffSize();
    }

    @Override
    public TelContext getTelContext() {
        return this.telContext;
    }

    @Override
    public void writeMessage(String message) {
        if (message == null) {
            return;
        }
        if (!this.isClose()) {
            try {
                this.dataWriter.write(message);
                this.dataWriter.flush();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public boolean tryReceiveEvent() {
        // .更新读取区
        this.dataReader.update();
        //
        // .是否有跳出动作 ^C 字符
        if (this.dataReader.expectChar(65533)) {
            this.close(); // 清掉缓冲区，重新接收
            return true;
        } else {
            this.dataReader.reset(); // 重置读取索引
        }
        // .创造命令
        if (this.curentCommand == null) {
            boolean blankLine = this.dataReader.expectBlankLine();
            if (blankLine) {
                String readData = this.dataReader.removeReadData();
                try {
                    this.curentCommand = createTelCommand(readData);
                    if (this.curentCommand == null) {
                        return !this.dataReader.isEof();
                    }
                    this.curentCommand.curentPhase(TelPhase.Prepare);
                } catch (Exception e) {
                    this.dataReader.clear(); // 清掉缓冲区，重新接收
                    writeMessageLine(e.getMessage());
                    return true;
                }
            }
            if (this.curentCommand == null) {
                return true;
            }
        }
        //
        // .命令如果还未结束那么继续等待输入
        if (!this.curentCommand.testReadly(this.dataReader)) {
            return false;
        }
        // .设置Body
        String readData = this.dataReader.removeReadData();
        this.curentCommand.setCommandBody(readData);
        this.curentCommand.curentPhase(TelPhase.StandBy);
        //
        // .执行命令
        try {
            this.telContext.getSpiTrigger().callSpi(ExecutorListener.class, listener -> {
                listener.beforeExecCommand(this.curentCommand);
            });
            this.execCommand(this.curentCommand);
        } finally {
            this.telContext.getSpiTrigger().callSpi(ExecutorListener.class, listener -> {
                listener.afterExecCommand(this.curentCommand);
            });
        }
        this.curentCommand = null;
        return true;
    }

    private void execCommand(TelCommandObject curentCommand) {
        long doStartTime = System.currentTimeMillis();
        String result = null;
        try {
            curentCommand.curentPhase(TelPhase.Running);
            result = curentCommand.doCommand();
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            result = sw.toString();
        }
        // .输出成本
        boolean silent = aBoolean(this, SILENT);    // 静默
        boolean cost = aBoolean(this, COST);        // 成本
        if (!silent && cost) {
            result = result + "\r\n--------------\r\n";
            result = result + ("cost time: " + (System.currentTimeMillis() - doStartTime) + "ms.");
        }
        //
        if (StringUtils.isNotBlank(result)) {
            writeMessageLine(result);
        }
        if (aBoolean(this, CLOSE_SESSION)) {
            if (!silent) {
                writeMessageLine("bye.");
            }
            IOUtils.closeQuietly(this.dataWriter);
        }
        curentCommand.curentPhase(TelPhase.Complete);
        this.atomicInteger.incrementAndGet(); // 计数器 ++
        //
        // .达到最大命令执行数，自动关闭session
        int executorNum = aInteger(this, MAX_EXECUTOR_NUM);
        if (executorNum > 0 && this.atomicInteger.get() >= executorNum) {
            this.close();
        }
    }

    private TelCommandObject createTelCommand(String inputString) {
        if (StringUtils.isBlank(inputString)) {
            return null;
        }
        String requestCMD = inputString;
        String requestArgs = "";
        int cmdIndex = inputString.indexOf(" ");
        if (inputString.indexOf(" ") > 0) {
            requestCMD = inputString.substring(0, cmdIndex);
            requestArgs = inputString.substring(cmdIndex + 1);
        }
        TelExecutor executor = this.telContext.findCommand(requestCMD);
        if (executor == null) {
            throw new UnsupportedOperationException("'" + requestCMD + "' is bad command.");
        }
        if (StringUtils.isBlank(requestArgs)) {
            return new TelCommandObject(this, executor, requestCMD, new String[0]);
        } else {
            return new TelCommandObject(this, executor, requestCMD, requestArgs.split(" "));
        }
    }

    @Override
    public void close(int afterSeconds, boolean countdown) {
        // .设置关闭状态
        this.setAttribute(CLOSE_SESSION, "true");
        // .触发SPI
        this.telContext.getSpiTrigger().callSpi(CloseListener.class, listener -> {
            listener.onClose(curentCommand, afterSeconds);
        });
        // .倒计时
        if (afterSeconds > 0) {
            try {
                for (int i = afterSeconds; i > 0; i--) {
                    if (countdown) {
                        this.writeMessageLine("exit after " + i + " seconds.");
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) { /**/ }
        }
    }
}