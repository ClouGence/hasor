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
 * ByteArraySerializer.java                               *
 *                                                        *
 * byte array serializer class for Java.                  *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.io.OutputStream;
public final class ByteArraySerializer extends ReferenceSerializer<byte[]> {
    public final static ByteArraySerializer instance = new ByteArraySerializer();
    @Override
    public final void serialize(Writer writer, byte[] bytes) throws IOException {
        super.serialize(writer, bytes);
        OutputStream stream = writer.stream;
        stream.write(HproseTags.TagBytes);
        int length = bytes.length;
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(HproseTags.TagQuote);
        stream.write(bytes);
        stream.write(HproseTags.TagQuote);
    }
}
