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
 * BytesArrayUnserializer.java                            *
 *                                                        *
 * bytes array unserializer class for Java.               *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagEmpty;
import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagList;
public final class BytesArrayUnserializer extends BaseUnserializer<byte[][]> {
    public final static BytesArrayUnserializer instance = new BytesArrayUnserializer();
    @Override
    public byte[][] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList)
            return ReferenceReader.readBytesArray(reader);
        if (tag == TagEmpty)
            return new byte[0][];
        return super.unserialize(reader, tag, type);
    }
    public byte[][] read(Reader reader) throws IOException {
        return read(reader, byte[][].class);
    }
}
