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
 * DoubleFieldAccessor.java                               *
 *                                                        *
 * DoubleFieldAccessor class for Java.                    *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.access;
import net.hasor.rsf.libs.com.hprose.io.HproseException;
import net.hasor.rsf.libs.com.hprose.io.serialize.ValueWriter;
import net.hasor.rsf.libs.com.hprose.io.serialize.Writer;
import net.hasor.rsf.libs.com.hprose.io.unserialize.DoubleUnserializer;
import net.hasor.rsf.libs.com.hprose.io.unserialize.Reader;

import java.io.IOException;
import java.lang.reflect.Field;
public final class DoubleFieldAccessor implements MemberAccessor {
    private final long offset;
    public DoubleFieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        offset = Accessors.unsafe.objectFieldOffset(accessor);
    }
    @Override
    public void serialize(Writer writer, Object obj) throws IOException {
        double value;
        try {
            value = Accessors.unsafe.getDouble(obj, offset);
        } catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
        ValueWriter.write(writer.stream, value);
    }
    @Override
    public void unserialize(Reader reader, Object obj) throws IOException {
        double value = DoubleUnserializer.instance.read(reader);
        try {
            Accessors.unsafe.putDouble(obj, offset, value);
        } catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }
}