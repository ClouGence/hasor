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
 * LocaleConverter.java                                   *
 *                                                        *
 * LocaleConverter class for Java.                        *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import java.lang.reflect.Type;
import java.util.Locale;
public class LocaleConverter implements Converter<Locale> {
    public final static LocaleConverter instance = new LocaleConverter();
    public Locale convertTo(String str) {
        String[] items = str.split("_");
        if (items.length == 1) {
            return new Locale(items[0]);
        }
        if (items.length == 2) {
            return new Locale(items[0], items[1]);
        }
        return new Locale(items[0], items[1], items[2]);
    }
    public Locale convertTo(char[] chars) {
        return convertTo(new String(chars));
    }
    public Locale convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return convertTo((String) obj);
        } else if (obj instanceof char[]) {
            return convertTo(new String((char[]) obj));
        }
        return (Locale) obj;
    }
}
