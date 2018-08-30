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
 * SafeFieldAccessor.java                                 *
 *                                                        *
 * SafeFieldAccessor class for Java.                      *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.access;
import net.hasor.rsf.libs.com.hprose.io.HproseException;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;
import net.hasor.rsf.libs.com.hprose.io.serialize.Serializer;
import net.hasor.rsf.libs.com.hprose.io.serialize.SerializerFactory;
import net.hasor.rsf.libs.com.hprose.io.serialize.Writer;
import net.hasor.rsf.libs.com.hprose.io.unserialize.Reader;
import net.hasor.rsf.libs.com.hprose.io.unserialize.Unserializer;
import net.hasor.rsf.libs.com.hprose.io.unserialize.UnserializerFactory;
import net.hasor.rsf.libs.com.hprose.utils.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
public final class SafeFieldAccessor implements MemberAccessor {
    private final Field        field;
    private final Type         fieldType;
    private final Serializer   serializer;
    private final Unserializer unserializer;
    public SafeFieldAccessor(Type type, Field field) {
        field.setAccessible(true);
        this.field = field;
        fieldType = ClassUtil.getActualType(type, field.getGenericType());
        Class<?> cls = ClassUtil.toClass(fieldType);
        serializer = SerializerFactory.get(cls);
        unserializer = UnserializerFactory.get(cls);
    }
    @Override
    @SuppressWarnings({ "unchecked" })
    public void serialize(Writer writer, Object obj) throws IOException {
        Object value;
        try {
            value = field.get(obj);
        } catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
        if (value == null) {
            writer.stream.write(HproseTags.TagNull);
        } else {
            serializer.write(writer, value);
        }
    }
    @Override
    public void unserialize(Reader reader, Object obj) throws IOException {
        Object value = unserializer.read(reader, reader.stream.read(), fieldType);
        try {
            field.set(obj, value);
        } catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }
}