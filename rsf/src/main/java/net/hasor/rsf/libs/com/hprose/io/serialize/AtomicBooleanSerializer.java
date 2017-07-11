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
 * AtomicBooleanSerializer.java                           *
 *                                                        *
 * AtomicBoolean serializer class for Java.               *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
public final class AtomicBooleanSerializer implements Serializer<AtomicBoolean> {
    public final static AtomicBooleanSerializer instance = new AtomicBooleanSerializer();
    public final void write(Writer writer, AtomicBoolean obj) throws IOException {
        ValueWriter.write(writer.stream, obj.get());
    }
}
