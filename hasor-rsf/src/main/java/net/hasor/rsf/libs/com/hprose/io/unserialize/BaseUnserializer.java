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
 * BaseUnserializer.java                                  *
 *                                                        *
 * hprose base unserializer class for Java.               *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.utils.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public abstract class BaseUnserializer<T> implements Unserializer<T> {
    public T unserialize(Reader reader, int tag, Type type) throws IOException {
        throw ValueReader.castError(reader.tagToString(tag), ClassUtil.toClass(type));
    }
    @SuppressWarnings({ "unchecked" })
    public T read(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagNull)
            return null;
        if (tag == TagRef)
            return (T) reader.readRef(type);
        if (tag == TagClass) {
            reader.readClass();
            return read(reader, type);
        }
        return unserialize(reader, tag, type);
    }
    public T read(Reader reader, Type type) throws IOException {
        return read(reader, reader.stream.read(), type);
    }
}
