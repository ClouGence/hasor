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
 * AtomicReferenceSerializer.java                         *
 *                                                        *
 * AtomicReference serializer class for Java.             *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
public final class AtomicReferenceSerializer implements Serializer<AtomicReference> {
    public final static AtomicReferenceSerializer instance = new AtomicReferenceSerializer();
    public final void write(Writer writer, AtomicReference obj) throws IOException {
        writer.serialize(obj.get());
    }
}
