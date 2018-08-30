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
 * ShortArrayUnserializer.java                            *
 *                                                        *
 * short array unserializer class for Java.               *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public final class ShortArrayUnserializer extends BaseUnserializer<short[]> {
    public final static ShortArrayUnserializer instance = new ShortArrayUnserializer();
    @Override
    public short[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == HproseTags.TagList)
            return ReferenceReader.readShortArray(reader);
        if (tag == HproseTags.TagEmpty)
            return new short[0];
        return super.unserialize(reader, tag, type);
    }
    public short[] read(Reader reader) throws IOException {
        return read(reader, short[].class);
    }
}
