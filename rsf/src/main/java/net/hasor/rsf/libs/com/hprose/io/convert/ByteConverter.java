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
 * ByteConverter.java                                     *
 *                                                        *
 * ByteConverter interface for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import java.lang.reflect.Type;
public class ByteConverter implements Converter<Byte> {
    public final static ByteConverter instance = new ByteConverter();
    public Byte convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return Byte.parseByte((String) obj);
        } else if (obj instanceof char[]) {
            return Byte.parseByte(new String((char[]) obj));
        }
        return (Byte) obj;
    }
}
