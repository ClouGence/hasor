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
 * AtomicIntegerSerializer.java                           *
 *                                                        *
 * AtomicInteger serializer class for Java.               *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
public final class AtomicIntegerSerializer implements Serializer<AtomicInteger> {
    public final static AtomicIntegerSerializer instance = new AtomicIntegerSerializer();
    public final void write(Writer writer, AtomicInteger obj) throws IOException {
        ValueWriter.write(writer.stream, obj.get());
    }
}
