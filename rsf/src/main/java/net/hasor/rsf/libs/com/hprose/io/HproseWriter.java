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
 * HproseWriter.java                                      *
 *                                                        *
 * hprose writer class for Java.                          *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io;
import net.hasor.rsf.libs.com.hprose.io.serialize.Writer;

import java.io.OutputStream;
public final class HproseWriter extends Writer {
    public HproseWriter(OutputStream stream) {
        super(stream);
    }
    public HproseWriter(OutputStream stream, boolean simple) {
        super(stream, simple);
    }
    public HproseWriter(OutputStream stream, HproseMode mode) {
        super(stream, mode);
    }
    public HproseWriter(OutputStream stream, HproseMode mode, boolean simple) {
        super(stream, mode, simple);
    }
}
