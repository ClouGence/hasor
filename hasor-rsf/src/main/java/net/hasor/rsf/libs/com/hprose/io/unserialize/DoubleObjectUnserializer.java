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
 * DoubleObjectUnserializer.java                          *
 *                                                        *
 * Double unserializer class for Java.                    *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public class DoubleObjectUnserializer extends BaseUnserializer<Double> {
    public final static DoubleObjectUnserializer instance = new DoubleObjectUnserializer();
    @Override
    public Double unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == HproseTags.TagDouble)
            return ValueReader.readDouble(reader);
        if (tag >= '0' && tag <= '9')
            return (double) (tag - '0');
        if (tag == HproseTags.TagInteger)
            return (double) ValueReader.readInt(reader, HproseTags.TagSemicolon);
        switch (tag) {
        case HproseTags.TagLong:
            return ValueReader.readLongAsDouble(reader);
        case HproseTags.TagEmpty:
            return 0.0;
        case HproseTags.TagTrue:
            return 1.0;
        case HproseTags.TagFalse:
            return 0.0;
        case HproseTags.TagNaN:
            return Double.NaN;
        case HproseTags.TagInfinity:
            return ValueReader.readInfinity(reader);
        case HproseTags.TagUTF8Char:
            return ValueReader.parseDouble(ValueReader.readUTF8Char(reader));
        case HproseTags.TagString:
            return ValueReader.parseDouble(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }
    public Double read(Reader reader) throws IOException {
        return read(reader, Double.class);
    }
}
