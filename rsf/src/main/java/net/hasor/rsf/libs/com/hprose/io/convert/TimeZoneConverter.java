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
 * TimeZoneConverter.java                                 *
 *                                                        *
 * TimeZoneConverter class for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import java.lang.reflect.Type;
import java.util.TimeZone;
public class TimeZoneConverter implements Converter<TimeZone> {
    public final static TimeZoneConverter instance = new TimeZoneConverter();
    public TimeZone convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return TimeZone.getTimeZone((String) obj);
        } else if (obj instanceof char[]) {
            return TimeZone.getTimeZone(new String((char[]) obj));
        }
        return (TimeZone) obj;
    }
}
