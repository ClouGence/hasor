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
 * DoubleConverter.java                                   *
 *                                                        *
 * DoubleConverter interface for Java.                    *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.io.unserialize.ValueReader;

import java.lang.reflect.Type;
public class DoubleConverter implements Converter<Double> {
    public final static DoubleConverter instance = new DoubleConverter();
    public Double convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return ValueReader.parseDouble((String) obj);
        } else if (obj instanceof char[]) {
            return ValueReader.parseDouble(new String((char[]) obj));
        }
        return (Double) obj;
    }
}
