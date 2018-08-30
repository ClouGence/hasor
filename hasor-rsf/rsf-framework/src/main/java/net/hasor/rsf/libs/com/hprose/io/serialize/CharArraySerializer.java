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
 * CharArraySerializer.java                               *
 *                                                        *
 * char array serializer class for Java.                  *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.io.OutputStream;
public final class CharArraySerializer extends ReferenceSerializer<char[]> {
    public final static CharArraySerializer instance = new CharArraySerializer();
    @Override
    public final void serialize(Writer writer, char[] s) throws IOException {
        super.serialize(writer, s);
        OutputStream stream = writer.stream;
        stream.write(HproseTags.TagString);
        ValueWriter.write(stream, s);
    }
    @Override
    public final void write(Writer writer, char[] obj) throws IOException {
        OutputStream stream = writer.stream;
        switch (obj.length) {
        case 0:
            stream.write(HproseTags.TagEmpty);
            break;
        case 1:
            ValueWriter.write(stream, obj[0]);
            break;
        default:
            super.write(writer, obj);
            break;
        }
    }
}
