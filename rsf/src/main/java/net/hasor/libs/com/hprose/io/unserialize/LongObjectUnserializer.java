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
 * LongObjectUnserializer.java                            *
 *                                                        *
 * Long unserializer class for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.libs.com.hprose.io.HproseTags.*;
public class LongObjectUnserializer extends BaseUnserializer<Long> {
    public final static LongObjectUnserializer instance = new LongObjectUnserializer();
    @Override
    public Long unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag >= '0' && tag <= '9')
            return (long) (tag - '0');
        if (tag == TagInteger || tag == TagLong)
            return ValueReader.readLong(reader, TagSemicolon);
        switch (tag) {
        case TagDouble:
            return Double.valueOf(ValueReader.readDouble(reader)).longValue();
        case TagEmpty:
            return 0l;
        case TagTrue:
            return 1l;
        case TagFalse:
            return 0l;
        case TagDate:
            return ReferenceReader.readDateTime(reader).toLong();
        case TagTime:
            return ReferenceReader.readTime(reader).toLong();
        case TagUTF8Char:
            return Long.parseLong(ValueReader.readUTF8Char(reader));
        case TagString:
            return Long.parseLong(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }
    public Long read(Reader reader) throws IOException {
        return read(reader, Long.class);
    }
}
