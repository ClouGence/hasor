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
 * StringBufferSerializer.java                            *
 *                                                        *
 * StringBuffer serializer class for Java.                *
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
public final class StringBufferSerializer extends ReferenceSerializer<StringBuffer> {
    public final static StringBufferSerializer instance = new StringBufferSerializer();
    @Override
    public final void serialize(Writer writer, StringBuffer s) throws IOException {
        super.serialize(writer, s);
        OutputStream stream = writer.stream;
        stream.write(TagString);
        ValueWriter.write(stream, s.toString());
    }
    @Override
    public final void write(Writer writer, StringBuffer obj) throws IOException {
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
