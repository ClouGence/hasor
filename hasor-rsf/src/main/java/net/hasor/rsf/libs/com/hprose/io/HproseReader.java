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
 * HproseReader.java                                      *
 *                                                        *
 * hprose reader class for Java.                          *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io;
import net.hasor.rsf.libs.com.hprose.io.unserialize.Reader;

import java.io.InputStream;
import java.nio.ByteBuffer;
public final class HproseReader extends Reader {
    public HproseReader(InputStream stream) {
        super(stream);
    }
    public HproseReader(InputStream stream, boolean simple) {
        super(stream, simple);
    }
    public HproseReader(InputStream stream, HproseMode mode) {
        super(stream, mode);
    }
    public HproseReader(InputStream stream, HproseMode mode, boolean simple) {
        super(stream, mode, simple);
    }
    public HproseReader(ByteBuffer buffer) {
        super(buffer);
    }
    public HproseReader(ByteBuffer buffer, boolean simple) {
        super(buffer, simple);
    }
    public HproseReader(ByteBuffer buffer, HproseMode mode) {
        super(buffer, mode);
    }
    public HproseReader(ByteBuffer buffer, HproseMode mode, boolean simple) {
        super(buffer, mode, simple);
    }
    public HproseReader(byte[] bytes) {
        super(bytes);
    }
    public HproseReader(byte[] bytes, boolean simple) {
        super(bytes, simple);
    }
    public HproseReader(byte[] bytes, HproseMode mode) {
        super(bytes, mode);
    }
    public HproseReader(byte[] bytes, HproseMode mode, boolean simple) {
        super(bytes, mode, simple);
    }
}