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
 * BigDecimalUnserializer.java                            *
 *                                                        *
 * BigDecimal unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 2, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class BigDecimalUnserializer extends BaseUnserializer<BigDecimal> {
    public final static BigDecimalUnserializer instance = new BigDecimalUnserializer();
    @Override
    public BigDecimal unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag >= '0' && tag <= '9')
            return BigDecimal.valueOf(tag - '0');
        switch (tag) {
        case TagInteger:
        case TagLong:
        case TagDouble:
            return new BigDecimal(ValueReader.readUntil(reader, TagSemicolon).toString());
        case TagEmpty:
            return BigDecimal.ZERO;
        case TagTrue:
            return BigDecimal.ONE;
        case TagFalse:
            return BigDecimal.ZERO;
        case TagUTF8Char:
            return new BigDecimal(ValueReader.readUTF8Char(reader));
        case TagString:
            return new BigDecimal(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }
    public BigDecimal read(Reader reader) throws IOException {
        return read(reader, BigDecimal.class);
    }
}
