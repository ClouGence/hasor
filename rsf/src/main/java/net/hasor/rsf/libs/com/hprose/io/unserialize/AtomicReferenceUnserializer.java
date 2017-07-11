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
 * AtomicReferenceUnserializer.java                       *
 *                                                        *
 * AtomicReference unserializer class for Java.           *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;
import net.hasor.rsf.libs.com.hprose.utils.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;
public final class AtomicReferenceUnserializer implements Unserializer<AtomicReference> {
    public final static AtomicReferenceUnserializer instance = new AtomicReferenceUnserializer();
    @SuppressWarnings({ "unchecked" })
    public AtomicReference read(Reader reader, int tag, Type type) throws IOException {
        if (tag == HproseTags.TagNull)
            return null;
        type = ClassUtil.getComponentType(type);
        return new AtomicReference(reader.unserialize(type));
    }
    public AtomicReference read(Reader reader, Type type) throws IOException {
        return read(reader, reader.stream.read(), type);
    }
    public AtomicReference read(Reader reader) throws IOException {
        return read(reader, AtomicReference.class);
    }
}
