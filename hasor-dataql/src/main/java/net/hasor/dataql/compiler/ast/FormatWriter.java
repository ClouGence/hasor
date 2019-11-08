package net.hasor.dataql.compiler.ast;
import java.io.IOException;
import java.io.Writer;

public class FormatWriter extends Writer {
    private Writer writer;
    private int    lastBrLength;// 距离上一个 \n 之间输出了多少字符

    public FormatWriter(Writer writer) {
        this.writer = writer;
    }

    /** 距离上一个 \n 之间输出了多少字符 */
    public int getLastBrLength() {
        return lastBrLength;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = off; i < (off + len); i++) {
            if (cbuf[i] == '\n') {
                lastBrLength = 0;
            } else {
                lastBrLength++;
            }
        }
        this.writer.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}
