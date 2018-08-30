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
 * IntConverter.java                                      *
 *                                                        *
 * IntConverter interface for Java.                       *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import java.lang.reflect.Type;
public class IntConverter implements Converter<Integer> {
    public final static IntConverter instance = new IntConverter();
    public Integer convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        } else if (obj instanceof char[]) {
            return Integer.parseInt(new String((char[]) obj));
        }
        return (Integer) obj;
    }
}
