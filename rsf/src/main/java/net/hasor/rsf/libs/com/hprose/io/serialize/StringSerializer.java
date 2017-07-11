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
 * StringSerializer.java                                  *
 *                                                        *
 * String serializer class for Java.                      *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.io.OutputStream;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagEmpty;
import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagString;
public final class StringSerializer extends ReferenceSerializer<String> {
    public final static StringSerializer instance = new StringSerializer();
    @Override
    public final void serialize(Writer writer, String s) throws IOException {
        super.serialize(writer, s);
        OutputStream stream = writer.stream;
        stream.write(TagString);
        ValueWriter.write(stream, s);
    }
    @Override
    public final void write(Writer writer, String obj) throws IOException {
        OutputStream stream = writer.stream;
        switch (obj.length()) {
        case 0:
            stream.write(TagEmpty);
            break;
        case 1:
            ValueWriter.write(stream, obj.charAt(0));
            break;
        default:
            super.write(writer, obj);
            break;
        }
    }
}
