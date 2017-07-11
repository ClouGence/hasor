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
 * PatternConverter.java                                  *
 *                                                        *
 * PatternConverter class for Java.                       *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import java.lang.reflect.Type;
import java.util.regex.Pattern;
public class PatternConverter implements Converter<Pattern> {
    public final static PatternConverter instance = new PatternConverter();
    public Pattern convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return Pattern.compile((String) obj);
        } else if (obj instanceof char[]) {
            return Pattern.compile(new String((char[]) obj));
        }
        return (Pattern) obj;
    }
}
