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
 * EnumSerializer.java                                    *
 *                                                        *
 * enum serializer class for Java.                        *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
public final class EnumSerializer implements Serializer<Enum> {
    public final static EnumSerializer instance = new EnumSerializer();
    public final void write(Writer writer, Enum obj) throws IOException {
        ValueWriter.write(writer.stream, obj.ordinal());
    }
}
