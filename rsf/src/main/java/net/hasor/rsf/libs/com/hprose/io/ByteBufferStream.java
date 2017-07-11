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
 * ByteBufferStream.java                                  *
 *                                                        *
 * ByteBuffer Stream for Java.                            *
 *                                                        *
 * LastModified: Jul 15, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
public final class ByteBufferStream {
    final static class ByteBufferPool {
        private final        int            POOLNUM  = 12;
        private final        int            POOLSIZE = 32;
        private final        ByteBuffer[][] pool     = new ByteBuffer[POOLNUM][];
        private final        int[]          position = new int[POOLNUM];
        private final static int[]          debruijn = new int[] { 0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9 };
        private static int log2(int x) {
            return debruijn[(x & -x) * 0x077CB531 >>> 27];
        }
        private ByteBufferPool() {
            for (int i = 0; i < POOLNUM; ++i) {
                pool[i] = new ByteBuffer[POOLSIZE];
                position[i] = -1;
            }
        }
        private ByteBuffer allocate(int capacity) {
            capacity = pow2roundup(capacity);
            if (capacity < 1024) {
                return ByteBuffer.allocate(capacity);
            }
            int index = log2(capacity) - 10;
            if (index < POOLNUM && position[index] >= 0) {
                int pos = position[index];
                ByteBuffer byteBuffer = pool[index][pos];
                pool[index][pos] = null;
                position[index] = pos - 1;
                if (byteBuffer != null)
                    return byteBuffer;
            }
            return ByteBuffer.allocateDirect(capacity);
        }
        private void free(ByteBuffer buffer) {
            if (buffer.isDirect()) {
                int capacity = buffer.capacity();
                if (capacity == pow2roundup(capacity)) {
                    buffer.clear();
                    int index = log2(capacity) - 10;
                    if (index >= 0 && index < POOLNUM) {
                        int pos = position[index];
                        if (pos < POOLSIZE - 1) {
                            pool[index][++pos] = buffer;
                            position[index] = pos;
                        }
                    }
                }
            }
        }
    }
    private final static ThreadLocal<ByteBufferPool> byteBufferPool = new ThreadLocal<ByteBufferPool>() {
        @Override
        protected ByteBufferPool initialValue() {
            return new ByteBufferPool();
        }
    };
    public ByteBuffer buffer;
    InputStream  istream;
    OutputStream ostream;
    private static int pow2roundup(int x) {
        --x;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }
    public final static ByteBuffer allocate(int capacity) {
        return byteBufferPool.get().allocate(capacity);
    }
    public final static void free(ByteBuffer buffer) {
        byteBufferPool.get().free(buffer);
    }
    public ByteBufferStream() {
        this(1024);
    }
    public ByteBufferStream(int capacity) {
        buffer = allocate(capacity);
    }
    public ByteBufferStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }
    public final static ByteBufferStream wrap(byte[] array, int offset, int length) {
        return new ByteBufferStream(ByteBuffer.wrap(array, offset, length));
    }
    public final static ByteBufferStream wrap(byte[] array) {
        return new ByteBufferStream(ByteBuffer.wrap(array));
    }
    public final void close() {
        if (buffer != null) {
            free(buffer);
            buffer = null;
        }
    }
    public final InputStream getInputStream() {
        if (istream == null) {
            istream = new ByteBufferInputStream(this);
        }
        return istream;
    }
    public final OutputStream getOutputStream() {
        if (ostream == null) {
            ostream = new ByteBufferOutputStream(this);
        }
        return ostream;
    }
    public final int read() {
        if (buffer.hasRemaining()) {
            return buffer.get() & 0xff;
        } else {
            return -1;
        }
    }
    public final int read(byte b[]) {
        return read(b, 0, b.length);
    }
    public final int read(byte b[], int off, int len) {
        if (len <= 0) {
            return 0;
        }
        int remain = buffer.remaining();
        if (remain <= 0) {
            return -1;
        }
        if (len >= remain) {
            buffer.get(b, off, remain);
            return remain;
        }
        buffer.get(b, off, len);
        return len;
    }
    public final int read(ByteBuffer b) {
        int len = b.remaining();
        if (len <= 0) {
            return 0;
        }
        int remain = buffer.remaining();
        if (remain <= 0) {
            return -1;
        }
        if (len >= remain) {
            b.put(buffer);
            return remain;
        }
        int oldlimit = buffer.limit();
        buffer.limit(buffer.position() + len);
        b.put(buffer);
        buffer.limit(oldlimit);
        return len;
    }
    public final long skip(long n) {
        if (n <= 0) {
            return 0;
        }
        int remain = buffer.remaining();
        if (remain <= 0) {
            return 0;
        }
        if (n > remain) {
            buffer.position(buffer.limit());
            return remain;
        }
        buffer.position(buffer.position() + (int) n);
        return n;
    }
    public final int available() {
        return buffer.remaining();
    }
    public final boolean markSupported() {
        return true;
    }
    public final void mark(int readlimit) {
        buffer.mark();
    }
    public final void reset() {
        buffer.reset();
    }
    private void grow(int n) {
        if (buffer.remaining() < n) {
            int required = buffer.position() + n;
            if (required > buffer.capacity()) {
                int size = pow2roundup(required);
                ByteBuffer buf = allocate(size);
                buffer.flip();
                buf.put(buffer);
                free(buffer);
                buffer = buf;
            } else {
                buffer.limit(required);
            }
        }
    }
    public final void write(int b) {
        grow(1);
        buffer.put((byte) b);
    }
    public final void write(byte b[]) {
        write(b, 0, b.length);
    }
    public final void write(byte b[], int off, int len) {
        grow(len);
        buffer.put(b, off, len);
    }
    public final void write(ByteBuffer b) {
        grow(b.remaining());
        buffer.put(b);
    }
    public final void flip() {
        if (buffer.position() != 0) {
            buffer.flip();
        }
    }
    public final void rewind() {
        buffer.rewind();
    }
    public final byte[] toArray() {
        flip();
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        return data;
    }
    public final void readFrom(InputStream istream) throws IOException {
        byte[] b = new byte[8192];
        for (; ; ) {
            int n = istream.read(b);
            if (n == -1) {
                break;
            }
            write(b, 0, n);
        }
    }
    public final void writeTo(OutputStream ostream) throws IOException {
        if (buffer.hasArray()) {
            ostream.write(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
        } else {
            byte[] b = new byte[8192];
            for (; ; ) {
                int n = read(b);
                if (n == -1) {
                    break;
                }
                ostream.write(b, 0, n);
            }
        }
    }
    public final void readFrom(ByteChannel channel, int length) throws IOException {
        int n = 0;
        grow(length);
        buffer.limit(buffer.position() + length);
        while (n < length) {
            int nn = channel.read(buffer);
            if (nn == -1) {
                break;
            }
            n += nn;
        }
        if (n < length) {
            throw new HproseException("Unexpected EOF");
        }
    }
    public final void writeTo(ByteChannel channel) throws IOException {
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}
