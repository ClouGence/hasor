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
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public class LongObjectUnserializer extends BaseUnserializer<Long> {
    public final static LongObjectUnserializer instance = new LongObjectUnserializer();
    @Override
    public Long unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag >= '0' && tag <= '9')
            return (long) (tag - '0');
        if (tag == HproseTags.TagInteger || tag == HproseTags.TagLong)
            return ValueReader.readLong(reader, HproseTags.TagSemicolon);
        switch (tag) {
        case HproseTags.TagDouble:
            return Double.valueOf(ValueReader.readDouble(reader)).longValue();
        case HproseTags.TagEmpty:
            return 0l;
        case HproseTags.TagTrue:
            return 1l;
        case HproseTags.TagFalse:
            return 0l;
        case HproseTags.TagDate:
            return ReferenceReader.readDateTime(reader).toLong();
        case HproseTags.TagTime:
            return ReferenceReader.readTime(reader).toLong();
        case HproseTags.TagUTF8Char:
            return Long.parseLong(ValueReader.readUTF8Char(reader));
        case HproseTags.TagString:
            return Long.parseLong(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }
    public Long read(Reader reader) throws IOException {
        return read(reader, Long.class);
    }
}
