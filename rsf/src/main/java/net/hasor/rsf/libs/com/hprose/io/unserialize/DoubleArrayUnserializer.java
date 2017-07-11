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
 * DoubleArrayUnserializer.java                           *
 *                                                        *
 * double array unserializer class for Java.              *
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
public final class DoubleArrayUnserializer extends BaseUnserializer<double[]> {
    public final static DoubleArrayUnserializer instance = new DoubleArrayUnserializer();
    @Override
    public double[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList)
            return ReferenceReader.readDoubleArray(reader);
        if (tag == TagEmpty)
            return new double[0];
        return super.unserialize(reader, tag, type);
    }
    public double[] read(Reader reader) throws IOException {
        return read(reader, double[].class);
    }
}
