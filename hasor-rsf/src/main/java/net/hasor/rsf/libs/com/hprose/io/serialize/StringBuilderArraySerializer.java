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
 * StringBuilderArraySerializer.java                      *
 *                                                        *
 * StringBuilder array serializer class for Java.         *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.io.OutputStream;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class StringBuilderArraySerializer extends ReferenceSerializer<StringBuilder[]> {
    public final static StringBuilderArraySerializer instance = new StringBuilderArraySerializer();
    @Override
    public final void serialize(Writer writer, StringBuilder[] array) throws IOException {
        super.serialize(writer, array);
        OutputStream stream = writer.stream;
        int length = array.length;
        stream.write(TagList);
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            StringBuilder e = array[i];
            if (e == null) {
                stream.write(TagNull);
            } else {
                StringBuilderSerializer.instance.write(writer, e);
            }
        }
        stream.write(TagClosebrace);
    }
}
