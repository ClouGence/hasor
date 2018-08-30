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
 * BigDecimalSerializer.java                              *
 *                                                        *
 * BigDecimal serializer class for Java.                  *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.math.BigDecimal;
public final class BigDecimalSerializer implements Serializer<BigDecimal> {
    public final static BigDecimalSerializer instance = new BigDecimalSerializer();
    public final void write(Writer writer, BigDecimal obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}
