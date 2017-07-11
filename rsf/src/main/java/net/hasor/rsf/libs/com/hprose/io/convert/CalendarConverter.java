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
 * CalendarConverter.java                                 *
 *                                                        *
 * CalendarConverter interface for Java.                  *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.lang.reflect.Type;
import java.util.Calendar;
public class CalendarConverter implements Converter<Calendar> {
    public final static CalendarConverter instance = new CalendarConverter();
    public Calendar convertTo(Long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar;
    }
    public Calendar convertTo(Double timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.longValue());
        return calendar;
    }
    public Calendar convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return ((DateTime) obj).toCalendar();
        } else if (obj instanceof Long) {
            return convertTo((Long) obj);
        } else if (obj instanceof Double) {
            return convertTo((Double) obj);
        }
        return (Calendar) obj;
    }
}
