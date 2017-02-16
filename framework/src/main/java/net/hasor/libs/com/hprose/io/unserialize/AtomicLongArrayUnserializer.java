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
 * AtomicLongArrayUnserializer.java                       *
 *                                                        *
 * AtomicLongArray unserializer class for Java.           *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLongArray;

import static net.hasor.libs.com.hprose.io.HproseTags.TagNull;
public final class AtomicLongArrayUnserializer implements Unserializer<AtomicLongArray> {
    public final static AtomicLongArrayUnserializer instance = new AtomicLongArrayUnserializer();
    public AtomicLongArray read(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagNull)
            return null;
        return new AtomicLongArray(LongArrayUnserializer.instance.read(reader, tag, long[].class));
    }
    public AtomicLongArray read(Reader reader, Type type) throws IOException {
        return read(reader, reader.stream.read(), type);
    }
    public AtomicLongArray read(Reader reader) throws IOException {
        return read(reader, AtomicLongArray.class);
    }
}
