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
 * CharArrayUnserializer.java                             *
 *                                                        *
 * char array unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.libs.com.hprose.io.HproseTags.*;
public final class CharArrayUnserializer extends BaseUnserializer<char[]> {
    public final static CharArrayUnserializer instance = new CharArrayUnserializer();
    @Override
    public char[] unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case TagEmpty:
            return new char[0];
        case TagUTF8Char:
            return new char[] { ValueReader.readChar(reader) };
        case TagList:
            return ReferenceReader.readCharArray(reader);
        case TagString:
            return ReferenceReader.readChars(reader);
        }
        return super.unserialize(reader, tag, type);
    }
    public char[] read(Reader reader) throws IOException {
        return read(reader, char[].class);
    }
}
