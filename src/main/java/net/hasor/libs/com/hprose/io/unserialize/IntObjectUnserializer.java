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
package net.hasor.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.libs.com.hprose.io.HproseTags.*;
public class IntObjectUnserializer extends BaseUnserializer<Integer> {
    public final static IntObjectUnserializer instance = new IntObjectUnserializer();
    @Override
    public Integer unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag >= '0' && tag <= '9')
            return (tag - '0');
        if (tag == TagInteger)
            return ValueReader.readInt(reader, TagSemicolon);
        switch (tag) {
        case TagLong:
            return ValueReader.readInt(reader, TagSemicolon);
        case TagDouble:
            return Double.valueOf(ValueReader.readDouble(reader)).intValue();
        case TagEmpty:
            return 0;
        case TagTrue:
            return 1;
        case TagFalse:
            return 0;
        case TagUTF8Char:
            return Integer.parseInt(ValueReader.readUTF8Char(reader));
        case TagString:
            return Integer.parseInt(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }
    public Integer read(Reader reader) throws IOException {
        return read(reader, Integer.class);
    }
}
