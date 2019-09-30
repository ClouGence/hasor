package net.hasor.tconsole.launcher;
import io.netty.channel.Channel;

import java.io.Writer;

/**
 * Handles a server-side channel.
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
