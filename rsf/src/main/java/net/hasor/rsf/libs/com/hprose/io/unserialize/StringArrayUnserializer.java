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
 * StringArrayUnserializer.java                           *
 *                                                        *
 * String array unserializer class for Java.              *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public final class StringArrayUnserializer extends BaseUnserializer<String[]> {
    public final static StringArrayUnserializer instance = new StringArrayUnserializer();
    @Override
    public String[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == HproseTags.TagList)
            return ReferenceReader.readStringArray(reader);
        if (tag == HproseTags.TagEmpty)
            return new String[0];
        return super.unserialize(reader, tag, type);
    }
    public String[] read(Reader reader) throws IOException {
        return read(reader, String[].class);
    }
}
