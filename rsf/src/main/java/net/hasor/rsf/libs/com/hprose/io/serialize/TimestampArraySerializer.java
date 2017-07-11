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
 * TimestampArraySerializer.java                          *
 *                                                        *
 * Timestamp array serializer class for Java.             *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class TimestampArraySerializer extends ReferenceSerializer<Timestamp[]> {
    public final static TimestampArraySerializer instance = new TimestampArraySerializer();
    @Override
    public final void serialize(Writer writer, Timestamp[] array) throws IOException {
        super.serialize(writer, array);
        OutputStream stream = writer.stream;
        stream.write(TagList);
        int length = array.length;
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            Timestamp e = array[i];
            if (e == null) {
                stream.write(TagNull);
            } else {
                TimestampSerializer.instance.write(writer, e);
            }
        }
        stream.write(TagClosebrace);
    }
}
