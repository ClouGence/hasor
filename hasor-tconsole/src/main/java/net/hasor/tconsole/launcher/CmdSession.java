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
import net.hasor.tconsole.CommandFinder;
import net.hasor.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @version : 2016年4月3日
 * @author 赵永春 (zyc@hasor.net)
 */
public final class CmdSession {
    private CommandFinder         commandFinder;//环境
    private ChannelHandlerContext nettyContext; //网络套接字
    private Map<String, Object>   attr;

    CmdSession(CommandFinder commandFinder, ChannelHandlerContext nettyContext) {
        this.commandFinder = commandFinder;
        this.nettyContext = nettyContext;
        this.attr = new HashMap<>();
    }

    void close() {
        this.nettyContext.close();
    }

    /**输出状态（带有换行）。*/
    void writeMessageLine(String message) throws InterruptedException {
        if (StringUtils.isBlank(message)) {
            message = "";
        }
        if (this.nettyContext.channel().isActive()) {
            String outStr = message + "\r\n";
            this.nettyContext.writeAndFlush(outStr).sync();
        }
    }

    /**输出状态（不带换行）。*/
    void writeMessage(String message) throws InterruptedException {
        if (StringUtils.isBlank(message)) {
            message = "";
        }
        if (this.nettyContext.channel().isActive()) {
            this.nettyContext.writeAndFlush(message).sync();
        }
    }

    public boolean isActive() {
        return this.nettyContext.channel().isActive();
    }

    public CommandFinder getFinder() {
        return this.commandFinder;
    }

    public Object getSessionAttr(String key) {
        return this.attr.get(key.toLowerCase());
    }

    public void setSessionAttr(String key, Object value) {
        this.attr.put(key.toLowerCase(), value);
    }
}