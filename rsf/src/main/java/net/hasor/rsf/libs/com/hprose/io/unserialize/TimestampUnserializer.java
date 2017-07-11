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
 * TimestampUnserializer.java                             *
 *                                                        *
 * Timestamp unserializer class for Java.                 *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
public final class TimestampUnserializer extends BaseUnserializer<Timestamp> {
    public final static TimestampUnserializer instance = new TimestampUnserializer();
    @Override
    public Timestamp unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case HproseTags.TagDate:
            return ReferenceReader.readDateTime(reader).toTimestamp();
        case HproseTags.TagTime:
            return ReferenceReader.readTime(reader).toTimestamp();
        case HproseTags.TagEmpty:
            return null;
        case HproseTags.TagString:
            return Timestamp.valueOf(ReferenceReader.readString(reader));
        case HproseTags.TagInteger:
        case HproseTags.TagLong:
            return new Timestamp(ValueReader.readLong(reader));
        case HproseTags.TagDouble:
            return new Timestamp((long) ValueReader.readDouble(reader));
        }
        if (tag >= '0' && tag <= '9')
            return new Timestamp(tag - '0');
        return super.unserialize(reader, tag, type);
    }
    public Timestamp read(Reader reader) throws IOException {
        return read(reader, Timestamp.class);
    }
}
