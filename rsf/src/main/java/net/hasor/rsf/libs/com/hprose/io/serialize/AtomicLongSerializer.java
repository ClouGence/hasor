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
 * AtomicLongSerializer.java                              *
 *                                                        *
 * AtomicLong serializer class for Java.                  *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
public final class AtomicLongSerializer implements Serializer<AtomicLong> {
    public final static AtomicLongSerializer instance = new AtomicLongSerializer();
    public final void write(Writer writer, AtomicLong obj) throws IOException {
        ValueWriter.write(writer.stream, obj.get());
    }
}
