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
 * DateTimeConverter.java                                 *
 *                                                        *
 * DateTimeConverter class for Java.                      *
 *                                                        *
 * LastModified: Aug 21, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.lang.reflect.Type;
import java.util.Date;
public class DateTimeConverter implements Converter<Date> {
    public final static DateTimeConverter instance = new DateTimeConverter();
    @SuppressWarnings({ "deprecation" })
    public Date convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return ((DateTime) obj).toDateTime();
        } else if (obj instanceof String) {
            return new Date((String) obj);
        } else if (obj instanceof char[]) {
            return new Date(new String((char[]) obj));
        } else if (obj instanceof Long) {
            return new Date((Long) obj);
        } else if (obj instanceof Double) {
            return new Date(((Double) obj).longValue());
        }
        return (Date) obj;
    }
}
