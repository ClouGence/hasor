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
 * BooleanObjectUnserializer.java                         *
 *                                                        *
 * Boolean unserializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public class BooleanObjectUnserializer extends BaseUnserializer<Boolean> {
    public final static BooleanObjectUnserializer instance = new BooleanObjectUnserializer();
    @Override
    public Boolean unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagTrue)
            return true;
        if (tag == TagFalse)
            return false;
        if (tag == '0')
            return false;
        if (tag >= '1' && tag <= '9')
            return true;
        switch (tag) {
        case TagInteger:
            return ValueReader.readInt(reader) != 0;
        case TagLong:
            return !(BigInteger.ZERO.equals(ValueReader.readBigInteger(reader)));
        case TagDouble:
            return ValueReader.readDouble(reader) != 0.0;
        case TagEmpty:
            return false;
        case TagNaN:
            return true;
        case TagInfinity:
            reader.stream.read();
            return true;
        case TagUTF8Char:
            return "\00".indexOf(ValueReader.readChar(reader)) == -1;
        case TagString:
            return Boolean.parseBoolean(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }
    public Boolean read(Reader reader) throws IOException {
        return read(reader, Boolean.class);
    }
}