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
 * StringBuilderUnserializer.java                         *
 *                                                        *
 * StringBuilder unserializer class for Java.             *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.convert.StringBuilderConverter;

import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class StringBuilderUnserializer extends BaseUnserializer<StringBuilder> {
    public final static StringBuilderUnserializer instance = new StringBuilderUnserializer();
    @Override
    public StringBuilder unserialize(Reader reader, int tag, Type type) throws IOException {
        StringBuilderConverter converter = StringBuilderConverter.instance;
        switch (tag) {
        case TagEmpty:
            return new StringBuilder();
        case TagString:
            return converter.convertTo(ReferenceReader.readChars(reader));
        case TagUTF8Char:
            return new StringBuilder().append(ValueReader.readChar(reader));
        case TagInteger:
            return ValueReader.readUntil(reader, TagSemicolon);
        case TagLong:
            return ValueReader.readUntil(reader, TagSemicolon);
        case TagDouble:
            return ValueReader.readUntil(reader, TagSemicolon);
        }
        if (tag >= '0' && tag <= '9')
            return new StringBuilder().append((char) tag);
        switch (tag) {
        case TagTrue:
            return new StringBuilder("true");
        case TagFalse:
            return new StringBuilder("false");
        case TagNaN:
            return new StringBuilder("NaN");
        case TagInfinity:
            return new StringBuilder((reader.stream.read() == TagPos) ? "Infinity" : "-Infinity");
        case TagDate:
            return ReferenceReader.readDateTime(reader).toStringBuilder();
        case TagTime:
            return ReferenceReader.readTime(reader).toStringBuilder();
        case TagGuid:
            return new StringBuilder(ReferenceReader.readUUID(reader).toString());
        }
        return super.unserialize(reader, tag, type);
    }
    public StringBuilder read(Reader reader) throws IOException {
        return read(reader, StringBuilder.class);
    }
}
