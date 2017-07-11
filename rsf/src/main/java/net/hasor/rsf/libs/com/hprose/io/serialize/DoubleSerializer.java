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
 * DoubleSerializer.java                                  *
 *                                                        *
 * double serializer class for Java.                      *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
public final class DoubleSerializer implements Serializer<Double> {
    public final static DoubleSerializer instance = new DoubleSerializer();
    public final void write(Writer writer, Double obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}
