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
 * IntObjectUnserializer.java                             *
 *                                                        *
 * Integer unserializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public class IntObjectUnserializer extends BaseUnserializer<Integer> {
    public final static IntObjectUnserializer instance = new IntObjectUnserializer();
    @Override
    public Integer unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag >= '0' && tag <= '9')
            return (tag - '0');
        if (tag == HproseTags.TagInteger)
            return ValueReader.readInt(reader, HproseTags.TagSemicolon);
        switch (tag) {
        case HproseTags.TagLong:
            return ValueReader.readInt(reader, HproseTags.TagSemicolon);
        case HproseTags.TagDouble:
            return Double.valueOf(ValueReader.readDouble(reader)).intValue();
        case HproseTags.TagEmpty:
            return 0;
        case HproseTags.TagTrue:
            return 1;
        case HproseTags.TagFalse:
            return 0;
        case HproseTags.TagUTF8Char:
            return Integer.parseInt(ValueReader.readUTF8Char(reader));
        case HproseTags.TagString:
            return Integer.parseInt(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }
    public Integer read(Reader reader) throws IOException {
        return read(reader, Integer.class);
    }
}
