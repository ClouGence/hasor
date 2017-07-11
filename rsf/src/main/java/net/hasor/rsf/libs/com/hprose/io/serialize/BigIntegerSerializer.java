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
 * BigIntegerSerializer.java                              *
 *                                                        *
 * BigInteger serializer class for Java.                  *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.math.BigInteger;
public final class BigIntegerSerializer implements Serializer<BigInteger> {
    public final static BigIntegerSerializer instance = new BigIntegerSerializer();
    public final void write(Writer writer, BigInteger obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}
