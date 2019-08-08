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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.hasor.tconsole.CommandExecutor;
import net.hasor.tconsole.CommandFinder;
import net.hasor.utils.NameThreadFactory;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Handles a server-side channel.
 */
@Sharable
class TelnetHandler extends SimpleChannelInboundHandler<String> {
    protected static     Logger                   logger     = LoggerFactory.getLogger(TelnetHandler.class);
    private static final AttributeKey<CmdRequest> RequestKEY = AttributeKey.newInstance("CommandRequest");
    private static final AttributeKey<CmdSession> SessionKEY = AttributeKey.newInstance("CommandSession");
    public static final  String                   CMD        = "tConsole>";
    private              CommandFinder            commandFinder;
    private              ScheduledExecutorService executor;
    private              String[]                 consoleInBound;

    public TelnetHandler(CommandFinder finder, String[] consoleInBound) {
        this.commandFinder = finder;
        this.consoleInBound = consoleInBound;
        int workSize = 1;
        String shortName = "tConsole-Work";
        this.executor = Executors.newScheduledThreadPool(workSize, new NameThreadFactory(shortName, finder.getAppContext().getClassLoader()));
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) this.executor;
        threadPool.setCorePoolSize(workSize);
        threadPool.setMaximumPoolSize(workSize);
        logger.info("tConsole -> create TelnetHandler , threadShortName={} , workThreadSize = {}.", shortName, workSize);
        //
        logger.info("tConsole -> inBoundAddress is :{}.", StringUtils.join(consoleInBound, ","));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String remoteAddress = inetAddress.getAddress().getHostAddress();
        //
        boolean contains = false;
        for (String addr : this.consoleInBound) {
            if (addr.equals(remoteAddress))
                contains = true;
        }
        //
        if (!contains) {
            logger.warn("tConsole -> reject inBound socket ,remoteAddress = {}.", remoteAddress);
            ctx.write("--------------------------------------------\r\n\r\n");
            ctx.write("I'm sorry you are not allowed to connect RSF Console.\r\n\r\n");
            ctx.write(" your address is :" + remoteAddress + "\r\n");
            ctx.write("--------------------------------------------\r\n");
            ctx.flush();
            ctx.close();
            return;
        } else {
            logger.info("tConsole -> accept inBound socket ,remoteAddress = {}.", remoteAddress);
        }
        //
        Attribute<CmdSession> attr = ctx.attr(SessionKEY);
        if (attr.get() == null) {
            logger.info("tConsole -> new  RsfCommandSession.");
            attr.set(new CmdSession(this.commandFinder, ctx));
        }
        logger.info("tConsole -> send Welcome info.");
        // Send greeting for a new connection.
        ctx.write("--------------------------------------------\r\n\r\n");
        ctx.write("Welcome to tConsole!\r\n");
        ctx.write("\r\n");
        ctx.write("     login : " + new Date() + " now. form " + ctx.channel().remoteAddress() + "\r\n");
        ctx.write("    workAt : " + ctx.channel().localAddress() + "\r\n");
        ctx.write("Tips: You can enter a 'help' or 'help -a' for more information.\r\n");
        ctx.write("use the 'exit' or 'quit' out of the console.\r\n");
        ctx.write("--------------------------------------------\r\n");
        ctx.write(CMD);
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        request = request.trim();
        Attribute<CmdRequest> attr = ctx.attr(RequestKEY);
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
        if (doRequest) {
            CmdResponse response = this.doRequest(attr, ctx, request);
            if (response != null) {
                close = response.isCloseConnection();
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
            ChannelFuture future = ctx.writeAndFlush(result);
            if (close) {
                logger.info("tConsole -> close connection.");
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
        LoggerFactory.getLogger(TelnetHandler.class).error("tConsole error->" + cause.getMessage(), cause);
        clearAttr(ctx);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("tConsole -> channelInactive.");
        clearAttr(ctx);
        super.channelInactive(ctx);
    }

    private void clearAttr(ChannelHandlerContext ctx) {
        Attribute<CmdSession> sessionAttr = ctx.attr(SessionKEY);
        Attribute<CmdRequest> attr = ctx.attr(RequestKEY);
        if (sessionAttr != null) {
            logger.info("tConsole -> clearAttr ,remove sessionAttr.");
            sessionAttr.remove();
        }
        if (attr != null) {
            logger.info("tConsole -> clearAttr ,remove requestAttr.");
            attr.remove();
        }
    }

    private CmdResponse doRequest(final Attribute<CmdRequest> cmdAttr, final ChannelHandlerContext ctx, final String inputString) {
        // .准备环境
        CmdRequest requestCmd = cmdAttr.get();
        Attribute<CmdSession> sessionAttr = ctx.attr(SessionKEY);
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
            CommandExecutor executor = this.commandFinder.findCommand(requestCMD);
            if (executor == null) {
                String msgStr = "'" + requestCMD + "' is bad command.";
                return new CmdResponse(msgStr, true, false);
            }
            //
            logger.info("tConsole -> doRequest, CommandRequest in ctx exist. -> command = {} , args = {}", requestCMD, requestArgs);
            requestCmd = new CmdRequest(requestCMD, sessionAttr.get(), executor, requestArgs);
            cmdAttr.set(requestCmd);
        }
        //
        // .不同模式命令的处理
        if (requestCmd.inputMultiLine()) {
            /*多行模式*/
            if (requestCmd.getStatus() == RequestStatus.Prepare) {
                if (!newCommand) {
                    requestCmd.appendRequestBody(inputString);
                }
                if (StringUtils.isBlank(inputString)) {
                    requestCmd.inReady();//命令准备执行
                }
            } else if (requestCmd.getStatus() == RequestStatus.Ready) {
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
        if (requestCmd.getStatus() == RequestStatus.StandBy) {
            logger.info("tConsole -> doRequest, doRunning.");
            requestCmd.doCommand(executor, () -> {
                cmdAttr.remove();
                ctx.writeAndFlush("\r\n" + CMD);
            });
            //命令进入就绪状态
            return requestCmd.getResponse();
        } else if (requestCmd.getStatus() == RequestStatus.Running) {
            return new CmdResponse("command is running, please wait a moment.", false, false);
        }
        //
        return null;
    }
}
