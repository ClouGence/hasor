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
 * AtomicIntegerArraySerializer.java                      *
 *                                                        *
 * AtomicIntegerArray serializer class for Java.          *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class AtomicIntegerArraySerializer extends ReferenceSerializer<AtomicIntegerArray> {
    public final static AtomicIntegerArraySerializer instance = new AtomicIntegerArraySerializer();
    @Override
    public final void serialize(Writer writer, AtomicIntegerArray array) throws IOException {
        super.serialize(writer, array);
        OutputStream stream = writer.stream;
        stream.write(TagList);
        int length = array.length();
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            ValueWriter.write(stream, array.get(i));
        }
        stream.write(TagClosebrace);
    }
}
