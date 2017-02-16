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
 * TimeArraySerializer.java                               *
 *                                                        *
 * Time array serializer class for Java.                  *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;

import static net.hasor.libs.com.hprose.io.HproseTags.*;
public final class TimeArraySerializer extends ReferenceSerializer<Time[]> {
    public final static TimeArraySerializer instance = new TimeArraySerializer();
    @Override
    public final void serialize(Writer writer, Time[] array) throws IOException {
        super.serialize(writer, array);
        OutputStream stream = writer.stream;
        stream.write(TagList);
        int length = array.length;
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            Time e = array[i];
            if (e == null) {
                stream.write(TagNull);
            } else {
                TimeSerializer.instance.write(writer, e);
            }
        }
        stream.write(TagClosebrace);
    }
}
