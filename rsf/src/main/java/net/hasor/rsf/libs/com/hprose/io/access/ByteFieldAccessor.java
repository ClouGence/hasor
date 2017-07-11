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
 * ByteFieldAccessor.java                                 *
 *                                                        *
 * ByteFieldAccessor class for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.access;
import net.hasor.rsf.libs.com.hprose.io.HproseException;
import net.hasor.rsf.libs.com.hprose.io.serialize.ValueWriter;
import net.hasor.rsf.libs.com.hprose.io.serialize.Writer;
import net.hasor.rsf.libs.com.hprose.io.unserialize.ByteUnserializer;
import net.hasor.rsf.libs.com.hprose.io.unserialize.Reader;

import java.io.IOException;
import java.lang.reflect.Field;
public final class ByteFieldAccessor implements MemberAccessor {
    private final long offset;
    public ByteFieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        offset = Accessors.unsafe.objectFieldOffset(accessor);
    }
    @Override
    public void serialize(Writer writer, Object obj) throws IOException {
        int value;
        try {
            value = Accessors.unsafe.getByte(obj, offset);
        } catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
        ValueWriter.write(writer.stream, value);
    }
    @Override
    public void unserialize(Reader reader, Object obj) throws IOException {
        byte value = ByteUnserializer.instance.read(reader);
        try {
            Accessors.unsafe.putInt(obj, offset, value);
        } catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }
}