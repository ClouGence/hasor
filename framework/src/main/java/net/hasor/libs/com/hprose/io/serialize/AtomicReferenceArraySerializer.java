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
 * AtomicReferenceArraySerializer.java                    *
 *                                                        *
 * AtomicReferenceArray serializer class for Java.        *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static net.hasor.libs.com.hprose.io.HproseTags.*;
public final class AtomicReferenceArraySerializer extends ReferenceSerializer<AtomicReferenceArray> {
    public final static AtomicReferenceArraySerializer instance = new AtomicReferenceArraySerializer();
    @Override
    public final void serialize(Writer writer, AtomicReferenceArray array) throws IOException {
        super.serialize(writer, array);
        OutputStream stream = writer.stream;
        stream.write(TagList);
        int length = array.length();
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            writer.serialize(array.get(i));
        }
        stream.write(TagClosebrace);
    }
}
