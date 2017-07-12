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
 * BigDecimalArrayUnserializer.java                       *
 *                                                        *
 * BigDecimal array unserializer class for Java.          *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagEmpty;
import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagList;
public final class BigDecimalArrayUnserializer extends BaseUnserializer<BigDecimal[]> {
    public final static BigDecimalArrayUnserializer instance = new BigDecimalArrayUnserializer();
    @Override
    public BigDecimal[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList)
            return ReferenceReader.readBigDecimalArray(reader);
        if (tag == TagEmpty)
            return new BigDecimal[0];
        return super.unserialize(reader, tag, type);
    }
    public BigDecimal[] read(Reader reader) throws IOException {
        return read(reader, BigDecimal[].class);
    }
}
