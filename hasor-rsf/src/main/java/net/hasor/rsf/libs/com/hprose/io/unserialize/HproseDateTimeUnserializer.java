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
 * HproseDateTimeUnserializer.java                        *
 *                                                        *
 * Hprose DateTime unserializer class for Java.           *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class HproseDateTimeUnserializer extends BaseUnserializer<DateTime> {
    public final static HproseDateTimeUnserializer instance = new HproseDateTimeUnserializer();
    @Override
    @SuppressWarnings({ "deprecation" })
    public DateTime unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case TagDate:
            return ReferenceReader.readDateTime(reader);
        case TagTime:
            return ReferenceReader.readTime(reader);
        case TagEmpty:
            return null;
        case TagString:
            return new DateTime(new Date(ReferenceReader.readString(reader)));
        case TagInteger:
        case TagLong:
            return new DateTime(new Date(ValueReader.readLong(reader)));
        case TagDouble:
            return new DateTime(new Date((long) ValueReader.readDouble(reader)));
        }
        if (tag >= '0' && tag <= '9')
            return new DateTime(new Date(tag - '0'));
        return super.unserialize(reader, tag, type);
    }
    public DateTime read(Reader reader) throws IOException {
        return read(reader, DateTime.class);
    }
}
