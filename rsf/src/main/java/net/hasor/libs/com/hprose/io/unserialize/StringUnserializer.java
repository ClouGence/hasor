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
 * StringUnserializer.java                                *
 *                                                        *
 * String unserializer class for Java.                    *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.libs.com.hprose.io.HproseTags.*;
public final class StringUnserializer extends BaseUnserializer<String> {
    public final static StringUnserializer instance = new StringUnserializer();
    @Override
    public String unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case TagEmpty:
            return "";
        case TagString:
            return ReferenceReader.readString(reader);
        case TagUTF8Char:
            return ValueReader.readUTF8Char(reader);
        case TagInteger:
            return ValueReader.readUntil(reader, TagSemicolon).toString();
        case TagLong:
            return ValueReader.readUntil(reader, TagSemicolon).toString();
        case TagDouble:
            return ValueReader.readUntil(reader, TagSemicolon).toString();
        }
        if (tag >= '0' && tag <= '9')
            return String.valueOf((char) tag);
        switch (tag) {
        case TagTrue:
            return "true";
        case TagFalse:
            return "false";
        case TagNaN:
            return "NaN";
        case TagInfinity:
            return (reader.stream.read() == TagPos) ? "Infinity" : "-Infinity";
        case TagDate:
            return ReferenceReader.readDateTime(reader).toString();
        case TagTime:
            return ReferenceReader.readTime(reader).toString();
        case TagGuid:
            return ReferenceReader.readUUID(reader).toString();
        }
        return super.unserialize(reader, tag, type);
    }
    public String read(Reader reader) throws IOException {
        return read(reader, String.class);
    }
}
