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
 * FloatSerializer.java                                   *
 *                                                        *
 * float serializer class for Java.                       *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
public final class FloatSerializer implements Serializer<Float> {
    public final static FloatSerializer instance = new FloatSerializer();
    public final void write(Writer writer, Float obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}
