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
 * ConverterFactory.java                                  *
 *                                                        *
 * Converter factory for Java.                            *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.convert;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;
import net.hasor.rsf.libs.com.hprose.utils.JdkVersion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
public final class ConverterFactory {
    private final static ConcurrentHashMap<Class<?>, Converter> converters = new ConcurrentHashMap<Class<?>, Converter>();

    static {
        converters.put(boolean.class, BooleanConverter.instance);
        converters.put(char.class, CharConverter.instance);
        converters.put(byte.class, ByteConverter.instance);
        converters.put(short.class, ShortConverter.instance);
        converters.put(int.class, IntConverter.instance);
        converters.put(long.class, LongConverter.instance);
        converters.put(float.class, FloatConverter.instance);
        converters.put(double.class, DoubleConverter.instance);
        converters.put(Boolean.class, BooleanConverter.instance);
        converters.put(Character.class, CharConverter.instance);
        converters.put(Byte.class, ByteConverter.instance);
        converters.put(Short.class, ShortConverter.instance);
        converters.put(Integer.class, IntConverter.instance);
        converters.put(Long.class, LongConverter.instance);
        converters.put(Float.class, FloatConverter.instance);
        converters.put(Double.class, DoubleConverter.instance);
        converters.put(String.class, StringConverter.instance);
        converters.put(BigInteger.class, BigIntegerConverter.instance);
        converters.put(Date.class, DateConverter.instance);
        converters.put(Time.class, TimeConverter.instance);
        converters.put(Timestamp.class, TimestampConverter.instance);
        converters.put(java.util.Date.class, DateTimeConverter.instance);
        converters.put(Calendar.class, CalendarConverter.instance);
        converters.put(BigDecimal.class, BigDecimalConverter.instance);
        converters.put(StringBuilder.class, StringBuilderConverter.instance);
        converters.put(StringBuffer.class, StringBufferConverter.instance);
        converters.put(UUID.class, UUIDConverter.instance);
        converters.put(URL.class, URLConverter.instance);
        converters.put(URI.class, URIConverter.instance);
        converters.put(Locale.class, LocaleConverter.instance);
        converters.put(Pattern.class, PatternConverter.instance);
        converters.put(TimeZone.class, TimeZoneConverter.instance);
        converters.put(DateTime.class, HproseDateTimeConverter.instance);
        if (JdkVersion.majorJavaVersion >= JdkVersion.JAVA_18) {
            try {
                Class.forName("hprose.io.convert.java8.ConverterLoader");
            } catch (ClassNotFoundException e) {
            }
        }
    }

    public final static Converter get(Class<?> type) {
        return converters.get(type);
    }
    public final static void register(Class<?> type, Converter converter) {
        converters.put(type, converter);
    }
}
