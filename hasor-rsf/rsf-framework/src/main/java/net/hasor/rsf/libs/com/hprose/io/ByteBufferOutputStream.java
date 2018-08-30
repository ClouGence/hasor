/**********************************************************\
 |                                                          |
 |                          hprose                          |
 |                                                          |
 | Official WebSite: http://www.hprose.com/                 |
 |                   http://www.hprose.org/                 |
 |                                                          |
 \**********************************************************/
/**********************************************************\
 *                                                        *
 * ByteBufferOutputStream.java                            *
 *                                                        *
 * ByteBuffer OutputStream for Java.                      *
 *                                                        *
 * LastModified: Apr 21, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io;
import java.io.IOException;
import java.io.OutputStream;
public final class ByteBufferOutputStream extends OutputStream {
    public final ByteBufferStream stream;
    ByteBufferOutputStream(ByteBufferStream stream) {
        this.stream = stream;
    }
    @Override
    public final void write(int b) throws IOException {
        stream.write(b);
    }
    @Override
    public final void write(byte b[]) throws IOException {
        stream.write(b);
    }
    @Override
    public final void write(byte b[], int off, int len) throws IOException {
        stream.write(b, off, len);
    }
    @Override
    public final void close() throws IOException {
        stream.close();
    }
}
