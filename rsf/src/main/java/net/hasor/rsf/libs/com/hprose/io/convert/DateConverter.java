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
 * DateConverter.java                                     *
 *                                                        *
 * DateConverter class for Java.                          *
 *                                                        *
 * LastModified: Aug 2, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.lang.reflect.Type;
import java.sql.Date;
public class DateConverter implements Converter<Date> {
    public final static DateConverter instance = new DateConverter();
    public Date convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return ((DateTime) obj).toDate();
        } else if (obj instanceof String) {
            return Date.valueOf((String) obj);
        } else if (obj instanceof char[]) {
            return Date.valueOf(new String((char[]) obj));
        } else if (obj instanceof Long) {
            return new Date((Long) obj);
        } else if (obj instanceof Double) {
            return new Date(((Double) obj).longValue());
        }
        return (Date) obj;
    }
}
