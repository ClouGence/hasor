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
 * StringBuilderSerializer.java                           *
 *                                                        *
 * StringBuilder serializer class for Java.               *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.io.OutputStream;
public final class StringBuilderSerializer extends ReferenceSerializer<StringBuilder> {
    public final static StringBuilderSerializer instance = new StringBuilderSerializer();
    @Override
    public final void serialize(Writer writer, StringBuilder s) throws IOException {
        super.serialize(writer, s);
        OutputStream stream = writer.stream;
        stream.write(HproseTags.TagString);
        ValueWriter.write(stream, s.toString());
    }
    @Override
    public final void write(Writer writer, StringBuilder obj) throws IOException {
        OutputStream stream = writer.stream;
        switch (obj.length()) {
        case 0:
            stream.write(HproseTags.TagEmpty);
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
