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
 * CharsArrayUnserializer.java                            *
 *                                                        *
 * chars array unserializer class for Java.               *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public final class CharsArrayUnserializer extends BaseUnserializer<char[][]> {
    public final static CharsArrayUnserializer instance = new CharsArrayUnserializer();
    @Override
    public char[][] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == HproseTags.TagList)
            return ReferenceReader.readCharsArray(reader);
        if (tag == HproseTags.TagEmpty)
            return new char[0][];
        return super.unserialize(reader, tag, type);
    }
    public char[][] read(Reader reader) throws IOException {
        return read(reader, char[][].class);
    }
}
