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
 * UUIDConverter.java                                     *
 *                                                        *
 * UUIDConverter interface for Java.                      *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import java.lang.reflect.Type;
import java.util.UUID;
public class UUIDConverter implements Converter<UUID> {
    public final static UUIDConverter instance = new UUIDConverter();
    public UUID convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return UUID.fromString((String) obj);
        } else if (obj instanceof char[]) {
            return UUID.fromString(new String((char[]) obj));
        } else if (obj instanceof byte[]) {
            return UUID.nameUUIDFromBytes((byte[]) obj);
        }
        return (UUID) obj;
    }
}
