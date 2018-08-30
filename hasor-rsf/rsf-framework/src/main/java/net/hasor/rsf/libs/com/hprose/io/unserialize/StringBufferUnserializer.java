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
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;
import net.hasor.rsf.libs.com.hprose.io.convert.StringBufferConverter;

import java.io.IOException;
import java.lang.reflect.Type;
public final class StringBufferUnserializer extends BaseUnserializer<StringBuffer> {
    public final static StringBufferUnserializer instance = new StringBufferUnserializer();
    @Override
    public StringBuffer unserialize(Reader reader, int tag, Type type) throws IOException {
        StringBufferConverter converter = StringBufferConverter.instance;
        switch (tag) {
        case HproseTags.TagEmpty:
            return new StringBuffer();
        case HproseTags.TagString:
            return converter.convertTo(ReferenceReader.readChars(reader));
        case HproseTags.TagUTF8Char:
            return new StringBuffer().append(ValueReader.readChar(reader));
        case HproseTags.TagInteger:
            return new StringBuffer(ValueReader.readUntil(reader, HproseTags.TagSemicolon));
        case HproseTags.TagLong:
            return new StringBuffer(ValueReader.readUntil(reader, HproseTags.TagSemicolon));
        case HproseTags.TagDouble:
            return new StringBuffer(ValueReader.readUntil(reader, HproseTags.TagSemicolon));
        }
        if (tag >= '0' && tag <= '9')
            return new StringBuffer().append((char) tag);
        switch (tag) {
        case HproseTags.TagTrue:
            return new StringBuffer("true");
        case HproseTags.TagFalse:
            return new StringBuffer("false");
        case HproseTags.TagNaN:
            return new StringBuffer("NaN");
        case HproseTags.TagInfinity:
            return new StringBuffer((reader.stream.read() == HproseTags.TagPos) ? "Infinity" : "-Infinity");
        case HproseTags.TagDate:
            return ReferenceReader.readDateTime(reader).toStringBuffer();
        case HproseTags.TagTime:
            return ReferenceReader.readTime(reader).toStringBuffer();
        case HproseTags.TagGuid:
            return new StringBuffer(ReferenceReader.readUUID(reader).toString());
        }
        return super.unserialize(reader, tag, type);
    }
    public StringBuffer read(Reader reader) throws IOException {
        return read(reader, StringBuffer.class);
    }
}
