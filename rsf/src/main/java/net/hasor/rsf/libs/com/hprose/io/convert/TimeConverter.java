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
 * TimeConverter.java                                     *
 *                                                        *
 * TimeConverter class for Java.                          *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.lang.reflect.Type;
import java.sql.Time;
public class TimeConverter implements Converter<Time> {
    public final static TimeConverter instance = new TimeConverter();
    public Time convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return ((DateTime) obj).toTime();
        } else if (obj instanceof String) {
            return Time.valueOf((String) obj);
        } else if (obj instanceof char[]) {
            return Time.valueOf(new String((char[]) obj));
        } else if (obj instanceof Long) {
            return new Time((Long) obj);
        } else if (obj instanceof Double) {
            return new Time(((Double) obj).longValue());
        }
        return (Time) obj;
    }
}
