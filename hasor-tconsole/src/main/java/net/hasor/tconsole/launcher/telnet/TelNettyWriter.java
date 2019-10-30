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
package net.hasor.tconsole.launcher.telnet;
import io.netty.channel.Channel;

import java.io.Writer;

/**
 * Handles writer
 * @version : 2016年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
class TelNettyWriter extends Writer {
    private Channel channel;

    TelNettyWriter(Channel channel) {
        this.channel = channel;
    }

    public boolean isClose() {
        return !this.channel.isActive();
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        this.channel.writeAndFlush(new String(cbuf, off, len));
    }

    @Override
    public void flush() {
        this.channel.flush();
    }

    @Override
    public void close() {
        this.channel.close();
    }
}
