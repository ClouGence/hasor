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
 * ByteArrayUnserializer.java                             *
 *                                                        *
 * byte array unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.libs.com.hprose.io.HproseTags.*;
public final class ByteArrayUnserializer extends BaseUnserializer<byte[]> {
    public final static ByteArrayUnserializer instance = new ByteArrayUnserializer();
    @Override
    public byte[] unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case TagEmpty:
            return new byte[0];
        case TagBytes:
            return ReferenceReader.readBytes(reader);
        case TagList:
            return ReferenceReader.readByteArray(reader);
        case TagUTF8Char:
            return ValueReader.readUTF8Char(reader).getBytes("UTF-8");
        case TagString:
            return ReferenceReader.readString(reader).getBytes("UTF-8");
        }
        return super.unserialize(reader, tag, type);
    }
    public byte[] read(Reader reader) throws IOException {
        return read(reader, byte[].class);
    }
}
