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
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
public final class TimeArraySerializer extends ReferenceSerializer<Time[]> {
    public final static TimeArraySerializer instance = new TimeArraySerializer();
    @Override
    public final void serialize(Writer writer, Time[] array) throws IOException {
        super.serialize(writer, array);
        OutputStream stream = writer.stream;
        stream.write(HproseTags.TagList);
        int length = array.length;
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            Time e = array[i];
            if (e == null) {
                stream.write(HproseTags.TagNull);
            } else {
                TimeSerializer.instance.write(writer, e);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }
}
