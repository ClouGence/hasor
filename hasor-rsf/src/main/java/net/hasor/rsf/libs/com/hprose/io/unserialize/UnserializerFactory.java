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
 * UnserializerFactory.java                               *
 *                                                        *
 * hprose unserializer factory for Java.                  *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.utils.CaseInsensitiveMap;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;
import net.hasor.rsf.libs.com.hprose.utils.JdkVersion;
import net.hasor.rsf.libs.com.hprose.utils.LinkedCaseInsensitiveMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.*;
import java.util.regex.Pattern;
public final class UnserializerFactory {
    private final static ConcurrentHashMap<Class<?>, Unserializer> unserializers = new ConcurrentHashMap<Class<?>, Unserializer>();

    static {
        unserializers.put(void.class, DefaultUnserializer.instance);
        unserializers.put(boolean.class, BooleanUnserializer.instance);
        unserializers.put(char.class, CharUnserializer.instance);
        unserializers.put(byte.class, ByteUnserializer.instance);
        unserializers.put(short.class, ShortUnserializer.instance);
        unserializers.put(int.class, IntUnserializer.instance);
        unserializers.put(long.class, LongUnserializer.instance);
        unserializers.put(float.class, FloatUnserializer.instance);
        unserializers.put(double.class, DoubleUnserializer.instance);
        unserializers.put(Object.class, DefaultUnserializer.instance);
        unserializers.put(Void.class, DefaultUnserializer.instance);
        unserializers.put(Boolean.class, BooleanObjectUnserializer.instance);
        unserializers.put(Character.class, CharObjectUnserializer.instance);
        unserializers.put(Byte.class, ByteObjectUnserializer.instance);
        unserializers.put(Short.class, ShortObjectUnserializer.instance);
        unserializers.put(Integer.class, IntObjectUnserializer.instance);
        unserializers.put(Long.class, LongObjectUnserializer.instance);
        unserializers.put(Float.class, FloatObjectUnserializer.instance);
        unserializers.put(Double.class, DoubleObjectUnserializer.instance);
        unserializers.put(String.class, StringUnserializer.instance);
        unserializers.put(BigInteger.class, BigIntegerUnserializer.instance);
        unserializers.put(Date.class, DateUnserializer.instance);
        unserializers.put(Time.class, TimeUnserializer.instance);
        unserializers.put(Timestamp.class, TimestampUnserializer.instance);
        unserializers.put(java.util.Date.class, DateTimeUnserializer.instance);
        unserializers.put(Calendar.class, CalendarUnserializer.instance);
        unserializers.put(BigDecimal.class, BigDecimalUnserializer.instance);
        unserializers.put(StringBuilder.class, StringBuilderUnserializer.instance);
        unserializers.put(StringBuffer.class, StringBufferUnserializer.instance);
        unserializers.put(UUID.class, UUIDUnserializer.instance);
        unserializers.put(boolean[].class, BooleanArrayUnserializer.instance);
        unserializers.put(char[].class, CharArrayUnserializer.instance);
        unserializers.put(byte[].class, ByteArrayUnserializer.instance);
        unserializers.put(short[].class, ShortArrayUnserializer.instance);
        unserializers.put(int[].class, IntArrayUnserializer.instance);
        unserializers.put(long[].class, LongArrayUnserializer.instance);
        unserializers.put(float[].class, FloatArrayUnserializer.instance);
        unserializers.put(double[].class, DoubleArrayUnserializer.instance);
        unserializers.put(String[].class, StringArrayUnserializer.instance);
        unserializers.put(BigInteger[].class, BigIntegerArrayUnserializer.instance);
        unserializers.put(Date[].class, DateArrayUnserializer.instance);
        unserializers.put(Time[].class, TimeArrayUnserializer.instance);
        unserializers.put(Timestamp[].class, TimestampArrayUnserializer.instance);
        unserializers.put(java.util.Date[].class, DateTimeArrayUnserializer.instance);
        unserializers.put(Calendar[].class, CalendarArrayUnserializer.instance);
        unserializers.put(BigDecimal[].class, BigDecimalArrayUnserializer.instance);
        unserializers.put(StringBuilder[].class, StringBuilderArrayUnserializer.instance);
        unserializers.put(StringBuffer[].class, StringBufferArrayUnserializer.instance);
        unserializers.put(UUID[].class, UUIDArrayUnserializer.instance);
        unserializers.put(char[][].class, CharsArrayUnserializer.instance);
        unserializers.put(byte[][].class, BytesArrayUnserializer.instance);
        unserializers.put(ArrayList.class, ArrayListUnserializer.instance);
        unserializers.put(AbstractList.class, ArrayListUnserializer.instance);
        unserializers.put(AbstractCollection.class, ArrayListUnserializer.instance);
        unserializers.put(List.class, ArrayListUnserializer.instance);
        unserializers.put(Collection.class, ArrayListUnserializer.instance);
        unserializers.put(LinkedList.class, LinkedListUnserializer.instance);
        unserializers.put(AbstractSequentialList.class, LinkedListUnserializer.instance);
        unserializers.put(HashSet.class, HashSetUnserializer.instance);
        unserializers.put(AbstractSet.class, HashSetUnserializer.instance);
        unserializers.put(Set.class, HashSetUnserializer.instance);
        unserializers.put(TreeSet.class, TreeSetUnserializer.instance);
        unserializers.put(SortedSet.class, TreeSetUnserializer.instance);
        unserializers.put(LinkedCaseInsensitiveMap.class, LinkedCaseInsensitiveMapUnserializer.instance);
        unserializers.put(LinkedHashMap.class, LinkedHashMapUnserializer.instance);
        unserializers.put(CaseInsensitiveMap.class, CaseInsensitiveMapUnserializer.instance);
        unserializers.put(HashMap.class, HashMapUnserializer.instance);
        unserializers.put(AbstractMap.class, HashMapUnserializer.instance);
        unserializers.put(Map.class, LinkedHashMapUnserializer.instance);
        unserializers.put(TreeMap.class, TreeMapUnserializer.instance);
        unserializers.put(SortedMap.class, TreeMapUnserializer.instance);
        unserializers.put(AtomicBoolean.class, AtomicBooleanUnserializer.instance);
        unserializers.put(AtomicInteger.class, AtomicIntegerUnserializer.instance);
        unserializers.put(AtomicLong.class, AtomicLongUnserializer.instance);
        unserializers.put(AtomicReference.class, AtomicReferenceUnserializer.instance);
        unserializers.put(AtomicIntegerArray.class, AtomicIntegerArrayUnserializer.instance);
        unserializers.put(AtomicLongArray.class, AtomicLongArrayUnserializer.instance);
        unserializers.put(AtomicReferenceArray.class, AtomicReferenceArrayUnserializer.instance);
        unserializers.put(URL.class, URLUnserializer.instance);
        unserializers.put(URI.class, URIUnserializer.instance);
        unserializers.put(Locale.class, LocaleUnserializer.instance);
        unserializers.put(Pattern.class, PatternUnserializer.instance);
        unserializers.put(TimeZone.class, TimeZoneUnserializer.instance);
        unserializers.put(DateTime.class, HproseDateTimeUnserializer.instance);
        if (JdkVersion.majorJavaVersion >= JdkVersion.JAVA_18) {
            try {
                Class.forName("hprose.io.unserialize.java8.UnserializerLoader");
            } catch (ClassNotFoundException e) {
            }
        }
    }

    public final static Unserializer get(Class<?> type) {
        Unserializer unserializer = unserializers.get(type);
        if (unserializer == null) {
            if (type.isEnum()) {
                unserializer = EnumUnserializer.instance;
            } else if (type.isArray()) {
                unserializer = ArrayUnserializer.instance;
            } else if (Map.class.isAssignableFrom(type)) {
                unserializer = MapUnserializer.instance;
            } else if (Collection.class.isAssignableFrom(type)) {
                unserializer = CollectionUnserializer.instance;
            } else if (TimeZone.class.isAssignableFrom(type)) {
                unserializer = TimeZoneUnserializer.instance;
            } else if (Calendar.class.isAssignableFrom(type)) {
                unserializer = CalendarUnserializer.instance;
            } else {
                unserializer = ObjectUnserializer.instance;
            }
            unserializers.put(type, unserializer);
        }
        return unserializer;
    }
    public final static void register(Class<?> type, Unserializer unserializer) {
        unserializers.put(type, unserializer);
    }
}
