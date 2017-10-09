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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.utils.StringUtils;
import net.hasor.utils.NameThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
/**
 * Handles a server-side channel.
 */
@Sharable
public class TelnetHandler extends SimpleChannelInboundHandler<String> {
    protected static     Logger                          logger     = LoggerFactory.getLogger(RsfConstants.LoggerName_Console);
    protected static     Logger                          rxdLogger  = LoggerFactory.getLogger(RsfConstants.LoggerName_ConsoleRXD);
    private static final AttributeKey<RsfCommandRequest> RequestKEY = AttributeKey.newInstance("CommandRequest");
    private static final AttributeKey<RsfCommandSession> SessionKEY = AttributeKey.newInstance("CommandSession");
    private static final String                          CMD        = "rsf>";
    private RsfContext               rsfContext;
    private CommandManager           commandManager;
    private ScheduledExecutorService executor;
    private String[]                 inBoundAddress;
    //
    public TelnetHandler(RsfContext rsfContext) {
        this.rsfContext = rsfContext;
        int workSize = 1;
        String shortName = "RSF-Console-Work";
        this.executor = Executors.newScheduledThreadPool(workSize, new NameThreadFactory(shortName, rsfContext.getClassLoader()));
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) this.executor;
        threadPool.setCorePoolSize(workSize);
        threadPool.setMaximumPoolSize(workSize);
        logger.info("rsfConsole -> create TelnetHandler , threadShortName={} , workThreadSize = {}.", shortName, workSize);
        //
        this.inBoundAddress = rsfContext.getSettings().getConsoleInBoundAddress();
        this.commandManager = rsfContext.getAppContext().getInstance(CommandManager.class);
        logger.info("rsfConsole -> inBoundAddress is :{}.", StringUtils.join(this.inBoundAddress, ","));
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String remoteAddress = inetAddress.getAddress().getHostAddress();
        //
        boolean contains = false;
        for (String addr : this.inBoundAddress) {
            if (addr.equals(remoteAddress))
                contains = true;
        }
        //
        if (!contains) {
            logger.warn("rsfConsole -> reject inBound socket ,remoteAddress = {}.", remoteAddress);
            ctx.write("--------------------------------------------\r\n\r\n");
            ctx.write("I'm sorry you are not allowed to connect RSF Console.\r\n\r\n");
            ctx.write(" your address is :" + remoteAddress + "\r\n");
            ctx.write("--------------------------------------------\r\n");
            ctx.flush();
            ctx.close();
            return;
        } else {
            logger.info("rsfConsole -> accept inBound socket ,remoteAddress = {}.", remoteAddress);
        }
        //
        RsfSettings settings = this.rsfContext.getSettings();
        List<String> rsfAddressList = getStrings(settings.getBindAddressSet());
        //
        Attribute<RsfCommandSession> attr = ctx.attr(SessionKEY);
        if (attr.get() == null) {
            logger.info("rsfConsole -> new  RsfCommandSession.");
            attr.set(new RsfCommandSession(this.rsfContext, ctx));
        }
        logger.info("rsfConsole -> send Welcome info.");
        // Send greeting for a new connection.
        ctx.write("--------------------------------------------\r\n\r\n");
        ctx.write("Welcome to RSF Console!\r\n");
        ctx.write("\r\n");
        ctx.write("     login : " + new Date() + " now. form " + ctx.channel().remoteAddress() + "\r\n");
        ctx.write("    workAt : " + ctx.channel().localAddress() + "\r\n");
        for (int i = 0; i < rsfAddressList.size(); i++) {
            if (i == 0) {
                ctx.write("rsfAddress : " + rsfAddressList.get(i) + "\r\n");
            } else {
                ctx.write("           : " + rsfAddressList.get(i) + "\r\n");
            }
        }
        ctx.write("  unitName : " + this.rsfContext.getSettings().getUnitName() + "\r\n\r\n");
        ctx.write("Tips: You can enter a 'help' or 'help -a' for more information.\r\n");
        ctx.write("use the 'exit' or 'quit' out of the console.\r\n");
        ctx.write("--------------------------------------------\r\n");
        ctx.write(CMD);
        ctx.flush();
    }
    private List<String> getStrings(Map<String, InterAddress> bindPortMap) throws UnknownHostException {
        List<String> hostSchema = new ArrayList<String>();
        List<String> portType = new ArrayList<String>(bindPortMap.keySet());
        Collections.sort(portType);
        //
        for (String key : portType) {
            try {
                InterAddress addr = bindPortMap.get(key);
                hostSchema.add(addr.toHostSchema());
            } catch (Exception e) {
                hostSchema.add(InetAddress.getLocalHost().getHostAddress());
            }
        }
        return hostSchema;
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        request = request.trim();
        rxdLogger.info("RXD({})-> {}", ctx.channel().remoteAddress(), request);
        //
        Attribute<RsfCommandRequest> attr = ctx.attr(RequestKEY);
        boolean close = false;
        String result = "";
        boolean doRequest = false;
        if (StringUtils.isBlank(request)) {
            if (attr != null && attr.get() != null) {
                doRequest = true;
            }
        } else {
            doRequest = true;
        }
        //
        if (!doRequest) {
            logger.info("rsfConsole -> receive RXD :" + request);
        }
        //
        if (doRequest) {
            RsfCommandResponse response = this.doRequest(attr, ctx, request);
            if (response != null) {
                close = response.isCloseConnection();
                logger.info("rsfConsole -> receive RXD, response isComplete = {}, isCloseConnection = {}", response.isComplete(), response.isCloseConnection());
                if (response.isComplete()) {
                    result = response.getResult() + "\r\n" + CMD;
                } else {
                    result = response.getResult() + "\r\n";
                }
            }
        } else {
            result = CMD;
        }
        //
        if (StringUtils.isNotBlank(result)) {
            rxdLogger.info("TXD({})-> {}", ctx.channel().remoteAddress(), result);
            ChannelFuture future = ctx.writeAndFlush(result);
            if (close) {
                logger.info("rsfConsole -> close connection.");
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LoggerFactory.getLogger(TelnetHandler.class).error("rsfConsole error->" + cause.getMessage(), cause);
        clearAttr(ctx);
        ctx.close();
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("rsfConsole -> channelInactive.");
        clearAttr(ctx);
        super.channelInactive(ctx);
    }
    private void clearAttr(ChannelHandlerContext ctx) {
        Attribute<RsfCommandSession> sessionAttr = ctx.attr(SessionKEY);
        Attribute<RsfCommandRequest> attr = ctx.attr(RequestKEY);
        if (sessionAttr != null) {
            logger.info("rsfConsole -> clearAttr ,remove sessionAttr.");
            sessionAttr.remove();
        }
        if (attr != null) {
            logger.info("rsfConsole -> clearAttr ,remove requestAttr.");
            attr.remove();
        }
    }
    private RsfCommandResponse doRequest(final Attribute<RsfCommandRequest> cmdAttr, final ChannelHandlerContext ctx, final String inputString) {
        // .准备环境
        logger.info("rsfConsole -> doRequest, pre environment.");
        RsfCommandRequest requestCmd = cmdAttr.get();
        Attribute<RsfCommandSession> sessionAttr = ctx.attr(SessionKEY);
        boolean newCommand = (requestCmd == null);
        //
        // .确定是否将本次输入追加到上一个命令中
        if (requestCmd == null) {
            String requestCMD = inputString;
            String requestArgs = "";
            int cmdIndex = inputString.indexOf(" ");
            if (inputString.indexOf(" ") > 0) {
                requestCMD = inputString.substring(0, cmdIndex);
                requestArgs = inputString.substring(cmdIndex + 1);
            }
            RsfInstruct rsfInstruct = this.commandManager.findCommand(requestCMD);
            if (rsfInstruct == null) {
                String msgStr = "'" + requestCMD + "' is bad command.";
                logger.info("rsfConsole -> " + msgStr);
                return new RsfCommandResponse(msgStr, true, false);
            }
            //
            logger.info("rsfConsole -> doRequest, RsfCommandRequest in ctx exist. -> command = {} , args = {}", requestCMD, requestArgs);
            requestCmd = new RsfCommandRequest(requestCMD, sessionAttr.get(), rsfInstruct, requestArgs);
            cmdAttr.set(requestCmd);
        }
        //
        // .不同模式命令的处理
        if (requestCmd.inputMultiLine()) {
            /*多行模式*/
            if (requestCmd.getStatus() == CommandRequestStatus.Prepare) {
                if (!newCommand) {
                    requestCmd.appendRequestBody(inputString);
                }
                if (StringUtils.isBlank(inputString)) {
                    requestCmd.inReady();//命令准备执行
                }
            } else if (requestCmd.getStatus() == CommandRequestStatus.Ready) {
                if (StringUtils.isBlank(inputString)) {
                    requestCmd.inStandBy();//命令准备执行
                } else {
                    requestCmd.appendRequestBody(inputString);
                }
            }
        } else {
            /*单行模式*/
            requestCmd.inStandBy();
        }
        //
        //3.执行命令
        if (requestCmd.getStatus() == CommandRequestStatus.StandBy) {
            logger.info("rsfConsole -> doRequest, doRunning.");
            requestCmd.doCommand(executor, new Runnable() {
                public void run() {
                    cmdAttr.remove();
                    ctx.writeAndFlush("\r\n" + CMD);
                }
            });
            //命令进入就绪状态
            return requestCmd.getResponse();
        } else if (requestCmd.getStatus() == CommandRequestStatus.Running) {
            return new RsfCommandResponse("command is running, please wait a moment.", false, false);
        }
        //
        return null;
    }
}
