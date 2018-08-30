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
 * ByteObjectUnserializer.java                            *
 *                                                        *
 * Byte unserializer class for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public class ByteObjectUnserializer extends BaseUnserializer<Byte> {
    public final static ByteObjectUnserializer instance = new ByteObjectUnserializer();
    @Override
    public Byte unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag >= '0' && tag <= '9')
            return (byte) (tag - '0');
        if (tag == HproseTags.TagInteger)
            return (byte) ValueReader.readInt(reader, HproseTags.TagSemicolon);
        switch (tag) {
        case HproseTags.TagLong:
            return (byte) ValueReader.readLong(reader, HproseTags.TagSemicolon);
        case HproseTags.TagDouble:
            return Double.valueOf(ValueReader.readDouble(reader)).byteValue();
        case HproseTags.TagEmpty:
            return 0;
        case HproseTags.TagTrue:
            return 1;
        case HproseTags.TagFalse:
            return 0;
        case HproseTags.TagUTF8Char:
            return Byte.parseByte(ValueReader.readUTF8Char(reader));
        case HproseTags.TagString:
            return Byte.parseByte(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }
    public Byte read(Reader reader) throws IOException {
        return read(reader, Byte.class);
    }
}
