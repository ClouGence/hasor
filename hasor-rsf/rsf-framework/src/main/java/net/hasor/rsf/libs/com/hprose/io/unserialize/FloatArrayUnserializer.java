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
 * FloatArrayUnserializer.java                            *
 *                                                        *
 * float array unserializer class for Java.               *
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
public final class FloatArrayUnserializer extends BaseUnserializer<float[]> {
    public final static FloatArrayUnserializer instance = new FloatArrayUnserializer();
    @Override
    public float[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList)
            return ReferenceReader.readFloatArray(reader);
        if (tag == TagEmpty)
            return new float[0];
        return super.unserialize(reader, tag, type);
    }
    public float[] read(Reader reader) throws IOException {
        return read(reader, float[].class);
    }
}
