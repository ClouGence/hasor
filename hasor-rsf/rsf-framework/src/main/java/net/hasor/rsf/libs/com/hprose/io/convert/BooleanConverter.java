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
 * BooleanConverter.java                                  *
 *                                                        *
 * BooleanConverter interface for Java.                   *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import java.lang.reflect.Type;
public class BooleanConverter implements Converter<Boolean> {
    public final static BooleanConverter instance = new BooleanConverter();
    public Boolean convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return Boolean.parseBoolean((String) obj);
        } else if (obj instanceof char[]) {
            return Boolean.parseBoolean(new String((char[]) obj));
        }
        return (Boolean) obj;
    }
}
