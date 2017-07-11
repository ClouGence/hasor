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
 * EnumUnserializer.java                                  *
 *                                                        *
 * Enum unserializer class for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.utils.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Type;
public final class EnumUnserializer implements Unserializer {
    public final static EnumUnserializer instance = new EnumUnserializer();
    public Object read(Reader reader, int tag, Type type) throws IOException {
        int index = IntUnserializer.instance.read(reader, tag, int.class);
        return ClassUtil.toClass(type).getEnumConstants()[index];
    }
    public Object read(Reader reader, Type type) throws IOException {
        return read(reader, reader.stream.read(), type);
    }
}
