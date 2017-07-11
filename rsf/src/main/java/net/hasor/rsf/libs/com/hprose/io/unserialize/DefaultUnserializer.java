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
 * DefaultUnserializer.java                               *
 *                                                        *
 * default unserializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public final class DefaultUnserializer extends BaseUnserializer {
    public final static DefaultUnserializer instance = new DefaultUnserializer();
    @Override
    public Object unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case '0':
            return 0;
        case '1':
            return 1;
        case '2':
            return 2;
        case '3':
            return 3;
        case '4':
            return 4;
        case '5':
            return 5;
        case '6':
            return 6;
        case '7':
            return 7;
        case '8':
            return 8;
        case '9':
            return 9;
        case HproseTags.TagEmpty:
            return "";
        case HproseTags.TagTrue:
            return true;
        case HproseTags.TagFalse:
            return false;
        case HproseTags.TagNaN:
            return Double.NaN;
        case HproseTags.TagInteger:
            return ValueReader.readInt(reader);
        case HproseTags.TagLong:
            return ValueReader.readBigInteger(reader);
        case HproseTags.TagDouble:
            return ValueReader.readDouble(reader);
        case HproseTags.TagInfinity:
            return ValueReader.readInfinity(reader);
        case HproseTags.TagUTF8Char:
            return ValueReader.readUTF8Char(reader);
        case HproseTags.TagString:
            return ReferenceReader.readString(reader);
        case HproseTags.TagBytes:
            return ReferenceReader.readBytes(reader);
        case HproseTags.TagDate:
            return ReferenceReader.readDateTime(reader).toCalendar();
        case HproseTags.TagTime:
            return ReferenceReader.readTime(reader).toCalendar();
        case HproseTags.TagGuid:
            return ReferenceReader.readUUID(reader);
        case HproseTags.TagList:
            return ReferenceReader.readArrayList(reader);
        case HproseTags.TagMap:
            return ReferenceReader.readHashMap(reader);
        case HproseTags.TagObject:
            return ReferenceReader.readObject(reader);
        }
        return super.unserialize(reader, tag, type);
    }
    public Object read(Reader reader) throws IOException {
        return read(reader, Object.class);
    }
}
