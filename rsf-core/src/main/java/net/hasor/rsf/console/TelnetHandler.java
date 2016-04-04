/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package net.hasor.rsf.console;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.more.util.ArrayUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.utils.NameThreadFactory;
/**
 * Handles a server-side channel.
 */
@Sharable
public class TelnetHandler extends SimpleChannelInboundHandler<String> {
    protected static Logger                              logger = LoggerFactory.getLogger(ConsoleRsfPlugin.class);
    private static final AttributeKey<RsfCommandRequest> KEY    = AttributeKey.newInstance("CommandRequest");
    private static final String                          CMD    = "rsf>";
    private RsfContext                                   rsfContext;
    private ScheduledExecutorService                     executor;
    private String[]                                     inBoundAddress;
    //
    public TelnetHandler(RsfContext rsfContext) {
        this.rsfContext = rsfContext;
        int workSize = 1;
        this.executor = Executors.newScheduledThreadPool(workSize, new NameThreadFactory("RSF-Console-Work"));
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) this.executor;
        threadPool.setCorePoolSize(workSize);
        threadPool.setMaximumPoolSize(workSize);
        //
        this.inBoundAddress = rsfContext.getSettings().getConsoleInBoundAddress();
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String remoteAddress = inetAddress.getAddress().getHostAddress();
        if (ArrayUtils.contains(this.inBoundAddress, remoteAddress) == false) {
            ctx.write("--------------------------------------------\r\n\r\n");
            ctx.write("I'm sorry you are not allowed to connect RSF Console.\r\n\r\n");
            ctx.write("--------------------------------------------\r\n");
            ctx.flush();
            ctx.close();
            return;
        }
        //
        RsfSettings settings = this.rsfContext.getSettings();
        InterAddress addr = new InterAddress(settings.getBindAddress(), settings.getBindPort(), settings.getUnitName());
        // Send greeting for a new connection.
        ctx.write("--------------------------------------------\r\n\r\n");
        ctx.write("Welcome to Remote Service Framework Console!\r\n");
        ctx.write("\r\n");
        ctx.write("login      : " + new Date() + " now. form " + ctx.channel().remoteAddress() + "\r\n");
        ctx.write("workAt     : " + ctx.channel().localAddress() + "\r\n");
        ctx.write("rsfAddress : " + addr.toHostSchema() + "\r\n\r\n");
        ctx.write("Tips: You can enter a 'help' for more information.\r\n");
        ctx.write("use the 'exit' or 'quit' out of the console.\r\n");
        ctx.write("--------------------------------------------\r\n");
        ctx.write(CMD);
        ctx.flush();
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        request = request.trim();
        Attribute<RsfCommandRequest> attr = ctx.attr(KEY);
        boolean close = false;
        String result = "";
        //
        boolean doRequest = false;
        if (StringUtils.isBlank(request)) {
            if (attr != null && attr.get() != null) {
                doRequest = true;
            }
        } else {
            doRequest = true;
        }
        if (doRequest) {
            RsfCommandResponse response = this.doRequest(attr, ctx, request);
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
        cause.printStackTrace();
        ctx.close();
    }
    private RsfCommandResponse doRequest(final Attribute<RsfCommandRequest> cmdAttr, final ChannelHandlerContext ctx, final String inputString) {
        //1.准备环境
        RsfCommandRequest requestCmd = cmdAttr.get();
        if (requestCmd == null) {
            String requestCMD = inputString;
            String requestArgs = "";
            int cmdIndex = inputString.indexOf(" ");
            if (inputString.indexOf(" ") > 0) {
                requestCMD = inputString.substring(0, cmdIndex);
                requestArgs = inputString.substring(cmdIndex + 1);
            }
            RsfCommand rsfCommand = this.rsfContext.getAppContext().findBindingBean(requestCMD, RsfCommand.class);
            if (rsfCommand == null) {
                return new RsfCommandResponse("'" + requestCMD + "' is bad command.", true, false);
            }
            requestCmd = new RsfCommandRequest(this.rsfContext, ctx, rsfCommand, requestArgs);
            cmdAttr.set(requestCmd);
        }
        //
        //2.不同模式命令的处理
        if (requestCmd.getCommand().inputMultiLine()) {
            /*多行模式*/
            if (requestCmd.getStatus() == CommandRequestStatus.Prepare) {
                requestCmd.appendRequestBody(inputString);
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
            requestCmd.doCommand(executor, new Runnable() {
                public void run() {
                    cmdAttr.remove();
                    ctx.writeAndFlush("\r\n" + CMD);
                }
            });//命令进入就绪状态
            return requestCmd.getResponse();
        } else if (requestCmd.getStatus() == CommandRequestStatus.Running) {
            return new RsfCommandResponse("command is running, please wait a moment.", false, false);
        }
        return null;
    }
}
