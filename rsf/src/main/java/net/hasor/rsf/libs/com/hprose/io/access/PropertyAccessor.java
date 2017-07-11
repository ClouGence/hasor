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
 * PropertyAccessor.java                                  *
 *                                                        *
 * PropertyAccessor class for Java.                       *
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
import java.lang.reflect.Method;
import java.lang.reflect.Type;
public final class PropertyAccessor implements MemberAccessor {
    private final static Object[] nullArgs = new Object[0];
    private final Method       getter;
    private final Method       setter;
    private final Type         propType;
    private final Serializer   serializer;
    private final Unserializer unserializer;
    public PropertyAccessor(Type type, Method getter, Method setter) {
        getter.setAccessible(true);
        setter.setAccessible(true);
        this.getter = getter;
        this.setter = setter;
        propType = ClassUtil.getActualType(type, getter.getGenericReturnType());
        Class<?> cls = ClassUtil.toClass(propType);
        serializer = SerializerFactory.get(cls);
        unserializer = UnserializerFactory.get(cls);
    }
    @Override
    @SuppressWarnings({ "unchecked" })
    public void serialize(Writer writer, Object obj) throws IOException {
        Object value;
        try {
            value = getter.invoke(obj, nullArgs);
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
        Object value = unserializer.read(reader, reader.stream.read(), propType);
        try {
            setter.invoke(obj, new Object[] { value });
        } catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }
}