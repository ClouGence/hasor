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
 * AtomicLongArraySerializer.java                         *
 *                                                        *
 * AtomicLongArray serializer class for Java.             *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLongArray;
public final class AtomicLongArraySerializer extends ReferenceSerializer<AtomicLongArray> {
    public final static AtomicLongArraySerializer instance = new AtomicLongArraySerializer();
    @Override
    public final void serialize(Writer writer, AtomicLongArray array) throws IOException {
        super.serialize(writer, array);
        OutputStream stream = writer.stream;
        stream.write(HproseTags.TagList);
        int length = array.length();
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            ValueWriter.write(stream, array.get(i));
        }
        stream.write(HproseTags.TagClosebrace);
    }
}
