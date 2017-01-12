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
 * StringArraySerializer.java                             *
 *                                                        *
 * String array serializer class for Java.                *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.io.OutputStream;

import static net.hasor.libs.com.hprose.io.HproseTags.*;
public final class StringArraySerializer extends ReferenceSerializer<String[]> {
    public final static StringArraySerializer instance = new StringArraySerializer();
    @Override
    public final void serialize(Writer writer, String[] array) throws IOException {
        super.serialize(writer, array);
        OutputStream stream = writer.stream;
        stream.write(TagList);
        int length = array.length;
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            String e = array[i];
            if (e == null) {
                stream.write(TagNull);
            } else {
                StringSerializer.instance.write(writer, e);
            }
        }
        stream.write(TagClosebrace);
    }
}
