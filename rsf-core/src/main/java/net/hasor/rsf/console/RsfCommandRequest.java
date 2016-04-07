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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Executor;
import org.more.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import net.hasor.rsf.RsfContext;
/**
 * 
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
public final class RsfCommandRequest {
    private RsfContext            rsfContext;   //Rsf环境
    private RsfCommand            rsfCommand;   //命令执行体
    private ChannelHandlerContext nettyContext; //网络套接字
    private String[]              requestArgs;  //请求参数
    private CommandRequestStatus  status;
    private StringBuffer          bodyBuffer;   //多行输入下内容缓冲区
    private String                result;       //执行结果
    private long                  doStartTime;  //命令执行的开始时间
    private boolean               doClose;
    //
    RsfCommandRequest(RsfContext rsfContext, ChannelHandlerContext nettyContext, //
            RsfCommand rsfCommand, String requestArgs) {
        this.rsfContext = rsfContext;
        this.rsfCommand = rsfCommand;
        this.nettyContext = nettyContext;
        this.requestArgs = StringUtils.isBlank(requestArgs) ? new String[0] : requestArgs.split(" ");
        this.bodyBuffer = new StringBuffer("");
        this.status = rsfCommand.inputMultiLine() ? CommandRequestStatus.Prepare : CommandRequestStatus.Ready;
        this.doClose = false;
    }
    void appendRequestBody(String requestBody) {
        if (this.rsfCommand.inputMultiLine()) {
            this.bodyBuffer.append(requestBody);
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
    RsfCommand getCommand() {
        return this.rsfCommand;
    }
    CommandRequestStatus getStatus() {
        return this.status;
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
                result = rsfCommand.doCommand(rsfContext, RsfCommandRequest.this);
            } catch (Throwable e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                result = "execute the command error :" + sw.toString();
            } finally {
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
                nettyContext.close();
            }
        }
    }
    RsfCommandResponse getResponse() {
        if (this.status != CommandRequestStatus.Complete) {
            return new RsfCommandResponse("commit the command.", false, false);
        }
        return new RsfCommandResponse(this.result, true, false);
    }
    //
    //
    /**获取RSF环境{@link RsfContext}*/
    public RsfContext getRsfContext() {
        return this.rsfContext;
    }
    /**获取request*/
    public String[] getRequestArgs() {
        return this.requestArgs;
    }
    public String getRequestBody() {
        return this.bodyBuffer.toString();
    }
    /**输出状态（带有换行）。*/
    public void writeMessageLine(String message) {
        if (StringUtils.isBlank(message)) {
            message = "";
        }
        this.nettyContext.writeAndFlush(message + "\r\n");
    }
    /**输出状态（不带换行）。*/
    public void writeMessage(String message) {
        if (StringUtils.isBlank(message)) {
            message = "";
        }
        this.nettyContext.writeAndFlush(message);
    }
    /**关闭Telnet连接。*/
    public void closeSession() {
        this.doClose = true;
    }
}