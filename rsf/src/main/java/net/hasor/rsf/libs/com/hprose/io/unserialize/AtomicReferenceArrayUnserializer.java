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
 * AtomicReferenceArrayUnserializer.java                  *
 *                                                        *
 * AtomicReferenceArray unserializer class for Java.      *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.utils.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagNull;
public final class AtomicReferenceArrayUnserializer implements Unserializer<AtomicReferenceArray> {
    public final static AtomicReferenceArrayUnserializer instance = new AtomicReferenceArrayUnserializer();
    @SuppressWarnings({ "unchecked" })
    public AtomicReferenceArray read(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagNull)
            return null;
        type = ClassUtil.getComponentType(type);
        Object[] array = (Object[]) ArrayUnserializer.instance.read(reader, tag, type);
        return new AtomicReferenceArray(array);
    }
}
