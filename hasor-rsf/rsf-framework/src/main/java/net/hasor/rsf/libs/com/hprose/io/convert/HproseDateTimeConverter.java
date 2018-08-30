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
 * HproseDateTimeConverter.java                           *
 *                                                        *
 * HproseDateTimeConverter class for Java.                *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.lang.reflect.Type;
import java.util.Date;
public class HproseDateTimeConverter implements Converter<DateTime> {
    public final static HproseDateTimeConverter instance = new HproseDateTimeConverter();
    @SuppressWarnings({ "deprecation" })
    public DateTime convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return (DateTime) obj;
        } else if (obj instanceof String) {
            return new DateTime(new Date((String) obj));
        } else if (obj instanceof char[]) {
            return new DateTime(new Date(new String((char[]) obj)));
        } else if (obj instanceof Long) {
            return new DateTime(new Date((Long) obj));
        } else if (obj instanceof Double) {
            return new DateTime(new Date(((Double) obj).longValue()));
        }
        return (DateTime) obj;
    }
}
