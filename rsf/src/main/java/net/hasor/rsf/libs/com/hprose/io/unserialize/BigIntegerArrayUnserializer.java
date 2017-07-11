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
 * BigIntegerArrayUnserializer.java                       *
 *                                                        *
 * BigInteger array unserializer class for Java.          *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagEmpty;
import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagList;
public final class BigIntegerArrayUnserializer extends BaseUnserializer<BigInteger[]> {
    public final static BigIntegerArrayUnserializer instance = new BigIntegerArrayUnserializer();
    @Override
    public BigInteger[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList)
            return ReferenceReader.readBigIntegerArray(reader);
        if (tag == TagEmpty)
            return new BigInteger[0];
        return super.unserialize(reader, tag, type);
    }
    public BigInteger[] read(Reader reader) throws IOException {
        return read(reader, BigInteger[].class);
    }
}
