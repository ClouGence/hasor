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
 * LongConverter.java                                     *
 *                                                        *
 * LongConverter interface for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.lang.reflect.Type;
public class LongConverter implements Converter<Long> {
    public final static LongConverter instance = new LongConverter();
    public Long convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return Long.parseLong((String) obj);
        } else if (obj instanceof char[]) {
            return Long.parseLong(new String((char[]) obj));
        } else if (obj instanceof DateTime) {
            return ((DateTime) obj).toLong();
        }
        return (Long) obj;
    }
}
