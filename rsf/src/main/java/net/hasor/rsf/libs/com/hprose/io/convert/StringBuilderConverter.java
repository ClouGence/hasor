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
 * StringBuilderConverter.java                            *
 *                                                        *
 * StringBuilderConverter class for Java.                 *
 *                                                        *
 * LastModified: Aug 2, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.lang.reflect.Type;
public class StringBuilderConverter implements Converter<StringBuilder> {
    public final static StringBuilderConverter instance = new StringBuilderConverter();
    public StringBuilder convertTo(char[] chars) {
        return new StringBuilder(chars.length + 16).append(chars);
    }
    public StringBuilder convertTo(Object obj, Type type) {
        if (obj instanceof char[]) {
            return convertTo((char[]) obj);
        } else if (obj instanceof String) {
            return new StringBuilder((String) obj);
        } else if (obj instanceof DateTime) {
            return ((DateTime) obj).toStringBuilder();
        }
        return new StringBuilder(obj.toString());
    }
}
