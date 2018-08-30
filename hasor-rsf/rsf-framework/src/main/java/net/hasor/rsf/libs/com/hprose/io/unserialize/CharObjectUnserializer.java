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
 * CharObjectUnserializer.java                            *
 *                                                        *
 * Character unserializer class for Java.                 *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public class CharObjectUnserializer extends BaseUnserializer<Character> {
    public final static CharObjectUnserializer instance = new CharObjectUnserializer();
    @Override
    public Character unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == HproseTags.TagUTF8Char)
            return ValueReader.readChar(reader);
        if (tag >= '0' && tag <= '9')
            return (char) tag;
        switch (tag) {
        case HproseTags.TagInteger:
            return (char) ValueReader.readInt(reader);
        case HproseTags.TagLong:
            return (char) ValueReader.readLong(reader);
        case HproseTags.TagDouble:
            return (char) Double.valueOf(ValueReader.readDouble(reader)).intValue();
        case HproseTags.TagString:
            return ReferenceReader.readString(reader).charAt(0);
        }
        return super.unserialize(reader, tag, type);
    }
    public Character read(Reader reader) throws IOException {
        return read(reader, Character.class);
    }
}