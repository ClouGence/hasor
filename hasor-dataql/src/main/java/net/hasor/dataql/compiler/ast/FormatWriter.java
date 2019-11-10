package net.hasor.dataql.compiler.ast;
import java.io.IOException;
import java.io.Writer;

public class FormatWriter extends Writer {
    private Writer writer;

    public FormatWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
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
