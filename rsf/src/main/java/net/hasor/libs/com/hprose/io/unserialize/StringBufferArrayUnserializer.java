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
 * StringBufferArrayUnserializer.java                     *
 *                                                        *
 * StringBuffer array unserializer class for Java.        *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.libs.com.hprose.io.HproseTags.TagEmpty;
import static net.hasor.libs.com.hprose.io.HproseTags.TagList;
public final class StringBufferArrayUnserializer extends BaseUnserializer<StringBuffer[]> {
    public final static StringBufferArrayUnserializer instance = new StringBufferArrayUnserializer();
    @Override
    public StringBuffer[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList)
            return ReferenceReader.readStringBufferArray(reader);
        if (tag == TagEmpty)
            return new StringBuffer[0];
        return super.unserialize(reader, tag, type);
    }
    public StringBuffer[] read(Reader reader) throws IOException {
        return read(reader, StringBuffer[].class);
    }
}
