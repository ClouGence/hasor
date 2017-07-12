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
 * ShortUnserializer.java                                 *
 *                                                        *
 * short unserializer class for Java.                     *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public final class ShortUnserializer extends ShortObjectUnserializer {
    public final static ShortUnserializer instance = new ShortUnserializer();
    @Override
    public Short read(Reader reader, int tag, Type type) throws IOException {
        if (tag == HproseTags.TagNull)
            return 0;
        return super.read(reader, tag, type);
    }
    @Override
    public Short read(Reader reader) throws IOException {
        return read(reader, short.class);
    }
}
