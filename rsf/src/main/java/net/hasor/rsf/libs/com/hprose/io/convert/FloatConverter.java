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
 * FloatConverter.java                                    *
 *                                                        *
 * FloatConverter interface for Java.                     *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.io.unserialize.ValueReader;

import java.lang.reflect.Type;
public class FloatConverter implements Converter<Float> {
    public final static FloatConverter instance = new FloatConverter();
    public Float convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return ValueReader.parseFloat((String) obj);
        } else if (obj instanceof char[]) {
            return ValueReader.parseFloat(new String((char[]) obj));
        }
        return (Float) obj;
    }
}
