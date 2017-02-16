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
 * StringBufferUnserializer.java                          *
 *                                                        *
 * StringBuffer unserializer class for Java.              *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.unserialize;
import net.hasor.libs.com.hprose.io.convert.StringBufferConverter;

import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.libs.com.hprose.io.HproseTags.*;
public final class StringBufferUnserializer extends BaseUnserializer<StringBuffer> {
    public final static StringBufferUnserializer instance = new StringBufferUnserializer();
    @Override
    public StringBuffer unserialize(Reader reader, int tag, Type type) throws IOException {
        StringBufferConverter converter = StringBufferConverter.instance;
        switch (tag) {
        case TagEmpty:
            return new StringBuffer();
        case TagString:
            return converter.convertTo(ReferenceReader.readChars(reader));
        case TagUTF8Char:
            return new StringBuffer().append(ValueReader.readChar(reader));
        case TagInteger:
            return new StringBuffer(ValueReader.readUntil(reader, TagSemicolon));
        case TagLong:
            return new StringBuffer(ValueReader.readUntil(reader, TagSemicolon));
        case TagDouble:
            return new StringBuffer(ValueReader.readUntil(reader, TagSemicolon));
        }
        if (tag >= '0' && tag <= '9')
            return new StringBuffer().append((char) tag);
        switch (tag) {
        case TagTrue:
            return new StringBuffer("true");
        case TagFalse:
            return new StringBuffer("false");
        case TagNaN:
            return new StringBuffer("NaN");
        case TagInfinity:
            return new StringBuffer((reader.stream.read() == TagPos) ? "Infinity" : "-Infinity");
        case TagDate:
            return ReferenceReader.readDateTime(reader).toStringBuffer();
        case TagTime:
            return ReferenceReader.readTime(reader).toStringBuffer();
        case TagGuid:
            return new StringBuffer(ReferenceReader.readUUID(reader).toString());
        }
        return super.unserialize(reader, tag, type);
    }
    public StringBuffer read(Reader reader) throws IOException {
        return read(reader, StringBuffer.class);
    }
}
