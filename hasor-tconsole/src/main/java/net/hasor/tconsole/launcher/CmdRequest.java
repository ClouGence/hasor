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
import net.hasor.tconsole.CommandExecutor;
import net.hasor.tconsole.CommandFinder;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
/**
 *
 * @version : 2016年4月3日
 * @author 赵永春 (zyc@hasor.net)
 */
public final class CmdRequest implements net.hasor.tconsole.CommandRequest {
    protected static Logger              logger = LoggerFactory.getLogger(CmdRequest.class);
    private          CmdSession          telnetSession;    //Rsf环境
    private          CommandExecutor     commandExecutor;  //命令执行体
    private          String              commandString;
    private          String[]            requestArgs;      //请求参数
    private          RequestStatus       status;
    private          boolean             inputMultiLine;
    private          StringBuffer        bodyBuffer;       //多行输入下内容缓冲区
    private          String              result;           //执行结果
    private          long                doStartTime;      //命令执行的开始时间
    private          boolean             doClose;
    private          Map<String, Object> attr;
    //
    CmdRequest(String commandString, CmdSession telnetSession, CommandExecutor commandExecutor, String requestArgs) {
        this.commandString = commandString;
        this.telnetSession = telnetSession;
        this.commandExecutor = commandExecutor;
        this.requestArgs = StringUtils.isBlank(requestArgs) ? new String[0] : requestArgs.split(" ");
        this.bodyBuffer = new StringBuffer();
        this.doClose = false;
        this.attr = new HashMap<>();
        this.inputMultiLine = commandExecutor.inputMultiLine(this);
        this.status = this.inputMultiLine ? RequestStatus.Prepare : RequestStatus.Ready;
    }
    void appendRequestBody(String requestBody) {
        if (this.inputMultiLine) {
            this.bodyBuffer.append(requestBody);
            this.bodyBuffer.append("\n");
            this.status = RequestStatus.Prepare;
        }
    }
    void inReady() {
        if (this.status == RequestStatus.Prepare) {
            this.status = RequestStatus.Ready;
        }
    }
    void inStandBy() {
        if (this.status == RequestStatus.Ready) {
            this.status = RequestStatus.StandBy;
        }
    }
    RequestStatus getStatus() {
        return this.status;
    }
    boolean inputMultiLine() {
        return this.inputMultiLine;
    }
    void doCommand(Executor executor, Runnable callBack) {
        status = RequestStatus.Running;
        executor.execute(new CommandRun(callBack));
    }
    private class CommandRun implements Runnable {
        private Runnable callBack;
        public CommandRun(Runnable callBack) {
            this.callBack = callBack;
        }
        public void run() {
            try {
                doStartTime = System.currentTimeMillis();
                result = commandExecutor.doCommand(CmdRequest.this);
            } catch (Throwable e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                result = sw.toString();
            } finally {
                //
                Object withoutAfterClose = getCommandAttr(WITHOUT_AFTER_CLOSE_SESSION);
                if (withoutAfterClose == null) {
                    withoutAfterClose = false;
                }
                boolean withoutAfterCloseBoolean = (Boolean) ConverterUtils.convert(Boolean.TYPE, withoutAfterClose);
                if (!withoutAfterCloseBoolean) {
                    Object afterClose = getSessionAttr(CommandExecutor.AFTER_CLOSE_SESSION);
                    if (afterClose != null) {
                        doClose = (Boolean) ConverterUtils.convert(Boolean.TYPE, afterClose);
                    }
                }
                //
                if (!doClose) {
                    result = result + "\r\n--------------\r\n";
                    result = result + ("pass time: " + (System.currentTimeMillis() - doStartTime) + "ms.");
                }
            }
            writeMessageLine(result);
            status = RequestStatus.Complete;
            this.callBack.run();
            if (doClose) {
                writeMessageLine("bye.");
                telnetSession.close();
            }
        }
    }
    CmdResponse getResponse() {
        if (this.status != RequestStatus.Complete) {
            if (this.inputMultiLine) {
                return new CmdResponse("commit the command.", false, false);
            } else {
                return new CmdResponse("", false, false);
            }
        }
        return new CmdResponse(this.result, true, false);
    }
    //
    //
    /**获取会话属性。*/
    public Object getSessionAttr(String key) {
        return this.telnetSession.getSessionAttr(key);
    }
    /**设置会话属性。*/
    public void setSessionAttr(String key, Object value) {
        this.telnetSession.setSessionAttr(key, value);
    }
    /**获取 Request 属性。*/
    public Object getCommandAttr(String key) {
        return this.attr.get(key.toLowerCase());
    }
    /**设置 Request 属性。*/
    public void setCommandAttr(String key, Object value) {
        this.attr.put(key.toLowerCase(), value);
    }
    /**获取命令行输入*/
    public String getCommandString() {
        return this.commandString;
    }
    /**获取App环境{@link CommandFinder}*/
    public CommandFinder getFinder() {
        return this.telnetSession.getFinder();
    }
    /**获取request*/
    public String[] getRequestArgs() {
        return this.requestArgs;
    }
    /**获取命令的内容部分。*/
    public String getRequestBody() {
        return this.bodyBuffer.toString();
    }
    /**关闭Telnet连接。*/
    public void closeSession() {
        this.doClose = true;
    }
    /**判断会话是否已经被关闭*/
    public boolean isSessionActive() {
        return this.telnetSession.isActive();
    }
    /**输出状态（带有换行）。*/
    public void writeMessageLine(String message) {
        try {
            this.telnetSession.writeMessageLine(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    /**输出状态（不带换行）。*/
    public void writeMessage(String message) {
        try {
            this.telnetSession.writeMessage(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}