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
 * BigDecimalConverter.java                               *
 *                                                        *
 * BigDecimalConverter interface for Java.                *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import java.lang.reflect.Type;
import java.math.BigDecimal;
public class BigDecimalConverter implements Converter<BigDecimal> {
    public final static BigDecimalConverter instance = new BigDecimalConverter();
    public BigDecimal convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return new BigDecimal((String) obj);
        } else if (obj instanceof char[]) {
            return new BigDecimal(new String((char[]) obj));
        }
        return (BigDecimal) obj;
    }
}
