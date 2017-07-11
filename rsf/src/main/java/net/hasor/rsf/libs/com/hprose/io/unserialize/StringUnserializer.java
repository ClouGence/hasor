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
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public final class StringUnserializer extends BaseUnserializer<String> {
    public final static StringUnserializer instance = new StringUnserializer();
    @Override
    public String unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case HproseTags.TagEmpty:
            return "";
        case HproseTags.TagString:
            return ReferenceReader.readString(reader);
        case HproseTags.TagUTF8Char:
            return ValueReader.readUTF8Char(reader);
        case HproseTags.TagInteger:
            return ValueReader.readUntil(reader, HproseTags.TagSemicolon).toString();
        case HproseTags.TagLong:
            return ValueReader.readUntil(reader, HproseTags.TagSemicolon).toString();
        case HproseTags.TagDouble:
            return ValueReader.readUntil(reader, HproseTags.TagSemicolon).toString();
        }
        if (tag >= '0' && tag <= '9')
            return String.valueOf((char) tag);
        switch (tag) {
        case HproseTags.TagTrue:
            return "true";
        case HproseTags.TagFalse:
            return "false";
        case HproseTags.TagNaN:
            return "NaN";
        case HproseTags.TagInfinity:
            return (reader.stream.read() == HproseTags.TagPos) ? "Infinity" : "-Infinity";
        case HproseTags.TagDate:
            return ReferenceReader.readDateTime(reader).toString();
        case HproseTags.TagTime:
            return ReferenceReader.readTime(reader).toString();
        case HproseTags.TagGuid:
            return ReferenceReader.readUUID(reader).toString();
        }
        return super.unserialize(reader, tag, type);
    }
    public String read(Reader reader) throws IOException {
        return read(reader, String.class);
    }
}
