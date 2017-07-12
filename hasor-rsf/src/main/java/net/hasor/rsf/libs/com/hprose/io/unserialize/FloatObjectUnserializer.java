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
 * FloatObjectUnserializer.java                           *
 *                                                        *
 * Float unserializer class for Java.                     *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public class FloatObjectUnserializer extends BaseUnserializer<Float> {
    public final static FloatObjectUnserializer instance = new FloatObjectUnserializer();
    @Override
    public Float unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == HproseTags.TagDouble)
            return ValueReader.readFloat(reader);
        if (tag >= '0' && tag <= '9')
            return (float) (tag - '0');
        if (tag == HproseTags.TagInteger)
            return (float) ValueReader.readInt(reader, HproseTags.TagSemicolon);
        switch (tag) {
        case HproseTags.TagLong:
            return ValueReader.readLongAsFloat(reader);
        case HproseTags.TagEmpty:
            return 0.0f;
        case HproseTags.TagTrue:
            return 1.0f;
        case HproseTags.TagFalse:
            return 0.0f;
        case HproseTags.TagNaN:
            return Float.NaN;
        case HproseTags.TagInfinity:
            return ValueReader.readFloatInfinity(reader);
        case HproseTags.TagUTF8Char:
            return ValueReader.parseFloat(ValueReader.readUTF8Char(reader));
        case HproseTags.TagString:
            return ValueReader.parseFloat(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }
    public Float read(Reader reader) throws IOException {
        return read(reader, Float.class);
    }
}
