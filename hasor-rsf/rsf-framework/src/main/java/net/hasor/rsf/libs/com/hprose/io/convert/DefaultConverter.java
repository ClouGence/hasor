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
 * DefaultConverter.java                                  *
 *                                                        *
 * DefaultConverter class for Java.                       *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.utils.ClassUtil;

import java.lang.reflect.Type;
public class DefaultConverter implements Converter {
    public final static DefaultConverter instance = new DefaultConverter();
    public Object convertTo(Object obj, Type type) {
        if (type == null)
            return obj;
        Class<?> cls = ClassUtil.toClass(type);
        if (cls == null || cls.equals(Object.class) || cls.isInstance(obj)) {
            return obj;
        }
        Converter converter = ConverterFactory.get(cls);
        if (converter == null) {
            return cls.cast(obj);
        }
        return converter.convertTo(obj, type);
    }
}
