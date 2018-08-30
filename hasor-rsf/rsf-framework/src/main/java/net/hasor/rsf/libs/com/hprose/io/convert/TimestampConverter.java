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
 * TimestampConverter.java                                *
 *                                                        *
 * TimestampConverter class for Java.                     *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.lang.reflect.Type;
import java.sql.Timestamp;
public class TimestampConverter implements Converter<Timestamp> {
    public final static TimestampConverter instance = new TimestampConverter();
    public Timestamp convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return ((DateTime) obj).toTimestamp();
        } else if (obj instanceof String) {
            return Timestamp.valueOf((String) obj);
        } else if (obj instanceof char[]) {
            return Timestamp.valueOf(new String((char[]) obj));
        } else if (obj instanceof Long) {
            return new Timestamp((Long) obj);
        } else if (obj instanceof Double) {
            return new Timestamp(((Double) obj).longValue());
        }
        return (Timestamp) obj;
    }
}
