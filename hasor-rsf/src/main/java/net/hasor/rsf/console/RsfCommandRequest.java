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
package net.hasor.rsf.console;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.utils.StringUtils;
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
 * @author 赵永春(zyc@hasor.net)
 */
public final class RsfCommandRequest {
    protected static    Logger logger                      = LoggerFactory.getLogger(RsfConstants.LoggerName_Console);
    public static final String WITHOUT_AFTER_CLOSE_SESSION = "WithoutAfterCloseSession";
    private RsfCommandSession    rsfSession;                                              //Rsf环境
    private RsfInstruct          rsfInstruct;                                              //命令执行体
    private String               command;
    private String[]             requestArgs;                                             //请求参数
    private CommandRequestStatus status;
    private boolean              inputMultiLine;
    private StringBuffer         bodyBuffer;                                              //多行输入下内容缓冲区
    private String               result;                                                  //执行结果
    private long                 doStartTime;                                             //命令执行的开始时间
    private boolean              doClose;
    private Map<String, Object>  attr;
    //
    RsfCommandRequest(String command, RsfCommandSession rsfSession, //
            RsfInstruct rsfInstruct, String requestArgs) {
        this.rsfSession = rsfSession;
        this.rsfInstruct = rsfInstruct;
        this.command = command;
        this.requestArgs = StringUtils.isBlank(requestArgs) ? new String[0] : requestArgs.split(" ");
        this.bodyBuffer = new StringBuffer("");
        this.doClose = false;
        this.attr = new HashMap<String, Object>();
        this.inputMultiLine = rsfInstruct.inputMultiLine(this);
        this.status = this.inputMultiLine ? CommandRequestStatus.Prepare : CommandRequestStatus.Ready;
    }
    void appendRequestBody(String requestBody) {
        if (this.inputMultiLine) {
            this.bodyBuffer.append(requestBody);
            this.bodyBuffer.append("\n");
            this.status = CommandRequestStatus.Prepare;
        }
    }
    void inReady() {
        if (this.status == CommandRequestStatus.Prepare) {
            this.status = CommandRequestStatus.Ready;
        }
    }
    void inStandBy() {
        if (this.status == CommandRequestStatus.Ready) {
            this.status = CommandRequestStatus.StandBy;
        }
    }
    RsfInstruct getCommand() {
        return this.rsfInstruct;
    }
    CommandRequestStatus getStatus() {
        return this.status;
    }
    boolean inputMultiLine() {
        return this.inputMultiLine;
    }
    void doCommand(final Executor executor, Runnable callBack) {
        status = CommandRequestStatus.Running;
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
                result = rsfInstruct.doCommand(RsfCommandRequest.this);
            } catch (Throwable e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                result = "execute the command error :" + sw.toString();
            } finally {
                //
                Object withoutAfterClose = getAttr(WITHOUT_AFTER_CLOSE_SESSION);
                if (withoutAfterClose == null) {
                    withoutAfterClose = false;
                }
                boolean withoutAfterCloseBoolean = (Boolean) ConverterUtils.convert(Boolean.TYPE, withoutAfterClose);
                if (!withoutAfterCloseBoolean) {
                    Object afterClose = getSessionAttr(RsfInstruct.AFTER_CLOSE_SESSION);
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
            status = CommandRequestStatus.Complete;
            this.callBack.run();
            if (doClose) {
                writeMessageLine("bye.");
                rsfSession.close();
            }
        }
    }
    RsfCommandResponse getResponse() {
        if (this.status != CommandRequestStatus.Complete) {
            if (this.inputMultiLine) {
                return new RsfCommandResponse("commit the command.", false, false);
            } else {
                return new RsfCommandResponse("", false, false);
            }
        }
        return new RsfCommandResponse(this.result, true, false);
    }
    //
    //
    /**获取会话属性。*/
    public Object getSessionAttr(String key) {
        return this.rsfSession.getSessionAttr(key);
    }
    /**设置会话属性。*/
    public void setSessionAttr(String key, Object value) {
        this.rsfSession.setSessionAttr(key, value);
    }
    /**获取会话属性。*/
    public Object getAttr(String key) {
        return this.attr.get(key.toLowerCase());
    }
    /**设置请求属性。*/
    public void setAttr(String key, Object value) {
        this.attr.put(key.toLowerCase(), value);
    }
    public String getCommandString() {
        return this.command;
    }
    /**获取RSF环境{@link RsfContext}*/
    public RsfContext getRsfContext() {
        return this.rsfSession.getRsfContext();
    }
    /**获取request*/
    public String[] getRequestArgs() {
        return this.requestArgs;
    }
    public String getRequestBody() {
        return this.bodyBuffer.toString();
    }
    /**关闭Telnet连接。*/
    public void closeSession() {
        this.doClose = true;
    }
    public boolean isSessionActive() {
        return this.rsfSession.isActive();
    }
    /**输出状态（带有换行）。*/
    public void writeMessageLine(String message) {
        try {
            this.rsfSession.writeMessageLine(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    /**输出状态（不带换行）。*/
    public void writeMessage(String message) {
        try {
            this.rsfSession.writeMessage(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}