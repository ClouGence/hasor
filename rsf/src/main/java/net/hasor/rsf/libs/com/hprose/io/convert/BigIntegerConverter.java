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
 * BigIntegerConverter.java                               *
 *                                                        *
 * BigIntegerConverter interface for Java.                *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.lang.reflect.Type;
import java.math.BigInteger;
public class BigIntegerConverter implements Converter<BigInteger> {
    public final static BigIntegerConverter instance = new BigIntegerConverter();
    public BigInteger convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return new BigInteger((String) obj);
        } else if (obj instanceof char[]) {
            return new BigInteger(new String((char[]) obj));
        } else if (obj instanceof DateTime) {
            return ((DateTime) obj).toBigInteger();
        }
        return (BigInteger) obj;
    }
}
