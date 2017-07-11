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
 * ByteUnserializer.java                                  *
 *                                                        *
 * byte unserializer class for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagNull;
public final class ByteUnserializer extends ByteObjectUnserializer {
    public final static ByteUnserializer instance = new ByteUnserializer();
    @Override
    public Byte read(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagNull)
            return 0;
        return super.read(reader, tag, type);
    }
    @Override
    public Byte read(Reader reader) throws IOException {
        return read(reader, byte.class);
    }
}