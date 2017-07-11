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
 * ReferenceReader.java                                   *
 *                                                        *
 * reference reader class for Java.                       *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseException;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;
import net.hasor.rsf.libs.com.hprose.io.access.Accessors;
import net.hasor.rsf.libs.com.hprose.io.access.ConstructorAccessor;
import net.hasor.rsf.libs.com.hprose.io.access.MemberAccessor;
import net.hasor.rsf.libs.com.hprose.io.convert.Converter;
import net.hasor.rsf.libs.com.hprose.io.convert.ConverterFactory;
import net.hasor.rsf.libs.com.hprose.utils.CaseInsensitiveMap;
import net.hasor.rsf.libs.com.hprose.utils.ClassUtil;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;
import net.hasor.rsf.libs.com.hprose.utils.LinkedCaseInsensitiveMap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
public final class ReferenceReader {
    public final static DateTime readDateTime(Reader reader) throws IOException {
        DateTime datetime = ValueReader.readDateTime(reader);
        reader.setRef(datetime);
        return datetime;
    }
    public final static DateTime readTime(Reader reader) throws IOException {
        DateTime datetime = ValueReader.readTime(reader);
        reader.setRef(datetime);
        return datetime;
    }
    public final static UUID readUUID(Reader reader) throws IOException {
        UUID uuid = ValueReader.readUUID(reader);
        reader.setRef(uuid);
        return uuid;
    }
    public final static String readString(Reader reader) throws IOException {
        String str = ValueReader.readString(reader);
        reader.setRef(str);
        return str;
    }
    public final static char[] readChars(Reader reader) throws IOException {
        char[] chars = ValueReader.readChars(reader);
        reader.setRef(chars);
        return chars;
    }
    public final static char[] readCharArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        char[] a = new char[count];
        reader.setRef(a);
        CharUnserializer unserializer = CharUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static char[][] readCharsArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        char[][] a = new char[count][];
        reader.setRef(a);
        CharArrayUnserializer unserializer = CharArrayUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static byte[] readBytes(Reader reader) throws IOException {
        byte[] bytes = ValueReader.readBytes(reader);
        reader.setRef(bytes);
        return bytes;
    }
    public final static byte[] readByteArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        byte[] a = new byte[count];
        reader.setRef(a);
        ByteUnserializer unserializer = ByteUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static byte[][] readBytesArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        byte[][] a = new byte[count][];
        reader.setRef(a);
        ByteArrayUnserializer unserializer = ByteArrayUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static short[] readShortArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        short[] a = new short[count];
        reader.setRef(a);
        ShortUnserializer unserializer = ShortUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static int[] readIntArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        int[] a = new int[count];
        reader.setRef(a);
        IntUnserializer unserializer = IntUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static long[] readLongArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        long[] a = new long[count];
        reader.setRef(a);
        LongUnserializer unserializer = LongUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static boolean[] readBooleanArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        boolean[] a = new boolean[count];
        reader.setRef(a);
        BooleanUnserializer unserializer = BooleanUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static float[] readFloatArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        float[] a = new float[count];
        reader.setRef(a);
        FloatUnserializer unserializer = FloatUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static double[] readDoubleArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        double[] a = new double[count];
        reader.setRef(a);
        DoubleUnserializer unserializer = DoubleUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static BigDecimal[] readBigDecimalArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        BigDecimal[] a = new BigDecimal[count];
        reader.setRef(a);
        BigDecimalUnserializer unserializer = BigDecimalUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static BigInteger[] readBigIntegerArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        BigInteger[] a = new BigInteger[count];
        reader.setRef(a);
        BigIntegerUnserializer unserializer = BigIntegerUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static String[] readStringArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        String[] a = new String[count];
        reader.setRef(a);
        StringUnserializer unserializer = StringUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static StringBuilder[] readStringBuilderArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        StringBuilder[] a = new StringBuilder[count];
        reader.setRef(a);
        StringBuilderUnserializer unserializer = StringBuilderUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static StringBuffer[] readStringBufferArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        StringBuffer[] a = new StringBuffer[count];
        reader.setRef(a);
        StringBufferUnserializer unserializer = StringBufferUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static Calendar[] readCalendarArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        Calendar[] a = new Calendar[count];
        reader.setRef(a);
        CalendarUnserializer unserializer = CalendarUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static Date[] readDateArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        Date[] a = new Date[count];
        reader.setRef(a);
        DateUnserializer unserializer = DateUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static Time[] readTimeArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        Time[] a = new Time[count];
        reader.setRef(a);
        TimeUnserializer unserializer = TimeUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static java.util.Date[] readDateTimeArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        java.util.Date[] a = new java.util.Date[count];
        reader.setRef(a);
        DateTimeUnserializer unserializer = DateTimeUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static Timestamp[] readTimestampArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        Timestamp[] a = new Timestamp[count];
        reader.setRef(a);
        TimestampUnserializer unserializer = TimestampUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static UUID[] readUUIDArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        UUID[] a = new UUID[count];
        reader.setRef(a);
        UUIDUnserializer unserializer = UUIDUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            a[i] = unserializer.read(reader);
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    @SuppressWarnings({ "unchecked" })
    public final static <T> T[] readArray(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        Type componentType = ClassUtil.getComponentType(type);
        Class compontentClass = ClassUtil.toClass(componentType);
        T[] a = (T[]) Array.newInstance(compontentClass, count);
        reader.setRef(a);
        if (count > 0) {
            InputStream stream = reader.stream;
            Unserializer<T> unserializer = UnserializerFactory.get(compontentClass);
            for (int i = 0; i < count; ++i) {
                a[i] = unserializer.read(reader, stream.read(), componentType);
            }
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static Object[] readArray(Reader reader, int count) throws IOException {
        Object[] a = new Object[count];
        reader.setRef(a);
        for (int i = 0; i < count; ++i) {
            a[i] = reader.unserialize();
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    public final static Object[] readArray(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        return readArray(reader, count);
    }
    public final static void readArray(Reader reader, Type[] types, Object[] a, int count) throws IOException {
        reader.setRef(a);
        for (int i = 0; i < count; ++i) {
            a[i] = reader.unserialize(types[i]);
        }
        reader.skip(HproseTags.TagClosebrace);
    }
    @SuppressWarnings({ "unchecked" })
    public final static ArrayList readArrayList(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        ArrayList a = new ArrayList(count);
        reader.setRef(a);
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                a.add(reader.unserialize());
            }
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    @SuppressWarnings({ "unchecked" })
    public final static Collection readCollection(Reader reader, Class<? extends Collection> type, Type componentType) throws IOException {
        int count = ValueReader.readCount(reader);
        Collection a = (Collection) ConstructorAccessor.newInstance(type);
        reader.setRef(a);
        if (count > 0) {
            InputStream stream = reader.stream;
            Class compontentClass = ClassUtil.toClass(componentType);
            Unserializer unserializer = UnserializerFactory.get(compontentClass);
            for (int i = 0; i < count; ++i) {
                a.add(unserializer.read(reader, stream.read(), componentType));
            }
        }
        reader.skip(HproseTags.TagClosebrace);
        return a;
    }
    @SuppressWarnings({ "unchecked" })
    public static void readCollection(Reader reader, Type type, Collection a, int count) throws IOException {
        reader.setRef(a);
        if (count > 0) {
            InputStream stream = reader.stream;
            Type componentType = ClassUtil.getComponentType(type);
            Class compontentClass = ClassUtil.toClass(componentType);
            Unserializer unserializer = UnserializerFactory.get(compontentClass);
            for (int i = 0; i < count; ++i) {
                a.add(unserializer.read(reader, stream.read(), componentType));
            }
        }
        reader.skip(HproseTags.TagClosebrace);
    }
    public final static ArrayList readArrayList(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        ArrayList a = new ArrayList(count);
        readCollection(reader, type, a, count);
        return a;
    }
    public final static LinkedList readLinkedList(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        LinkedList a = new LinkedList();
        readCollection(reader, type, a, count);
        return a;
    }
    public final static HashSet readHashSet(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        HashSet a = new HashSet(count);
        readCollection(reader, type, a, count);
        return a;
    }
    public final static TreeSet readTreeSet(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        TreeSet a = new TreeSet();
        readCollection(reader, type, a, count);
        return a;
    }
    @SuppressWarnings({ "unchecked" })
    public final static Collection readCollection(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        Class<?> cls = ClassUtil.toClass(type);
        Collection a = (Collection) ConstructorAccessor.newInstance(cls);
        readCollection(reader, type, a, count);
        return a;
    }
    @SuppressWarnings({ "unchecked" })
    public final static HashMap readHashMap(Reader reader) throws IOException {
        int count = ValueReader.readCount(reader);
        HashMap m = new HashMap(count);
        reader.setRef(m);
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                Object key = reader.unserialize();
                Object value = reader.unserialize();
                m.put(key, value);
            }
        }
        reader.skip(HproseTags.TagClosebrace);
        return m;
    }
    @SuppressWarnings({ "unchecked" })
    public static void readListAsMap(Reader reader, Map m, Type keyType, Type valueType, int count) throws IOException {
        reader.setRef(m);
        if (count > 0) {
            InputStream stream = reader.stream;
            Class<?> keyClass = ClassUtil.toClass(keyType);
            Class<?> valueClass = ClassUtil.toClass(valueType);
            Converter converter = ConverterFactory.get(keyClass);
            Unserializer unserializer = UnserializerFactory.get(valueClass);
            for (int i = 0; i < count; ++i) {
                m.put(converter.convertTo(i, keyType), unserializer.read(reader, stream.read(), valueType));
            }
        }
        reader.skip(HproseTags.TagClosebrace);
    }
    @SuppressWarnings({ "unchecked" })
    public static void readListAsMap(Reader reader, Type type, Map m, int count) throws IOException {
        readListAsMap(reader, m, ClassUtil.getKeyType(type), ClassUtil.getValueType(type), count);
    }
    @SuppressWarnings({ "unchecked" })
    public static Map readListAsMap(Reader reader, Class<? extends Map> type, Type keyType, Type valueType) throws IOException {
        int count = ValueReader.readCount(reader);
        Map m = (Map) ConstructorAccessor.newInstance(type);
        readListAsMap(reader, m, keyType, valueType, count);
        return m;
    }
    public final static HashMap readListAsHashMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        HashMap m = new HashMap(count);
        readListAsMap(reader, type, m, count);
        return m;
    }
    public final static LinkedHashMap readListAsLinkedHashMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        LinkedHashMap m = new LinkedHashMap(count);
        readListAsMap(reader, type, m, count);
        return m;
    }
    public final static LinkedCaseInsensitiveMap readListAsLinkedCaseInsensitiveMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        LinkedCaseInsensitiveMap m = new LinkedCaseInsensitiveMap(count);
        readListAsMap(reader, type, m, count);
        return m;
    }
    public final static CaseInsensitiveMap readListAsCaseInsensitiveMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        CaseInsensitiveMap m = new CaseInsensitiveMap(count);
        readListAsMap(reader, type, m, count);
        return m;
    }
    public final static TreeMap readListAsTreeMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        TreeMap m = new TreeMap();
        readListAsMap(reader, type, m, count);
        return m;
    }
    @SuppressWarnings({ "unchecked" })
    public final static Map readListAsMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        Class<?> cls = ClassUtil.toClass(type);
        Map m = (Map) ConstructorAccessor.newInstance(cls);
        readListAsMap(reader, type, m, count);
        return m;
    }
    public static void readObjectAsMap(Reader reader, Map<String, Object> m, Map<String, Type> typeMap, String[] memberNames, int count) throws IOException {
        reader.setRef(m);
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                String key = memberNames[i];
                if (typeMap.containsKey(key)) {
                    Type valueType = typeMap.get(key);
                    m.put(key, reader.unserialize(valueType));
                } else {
                    m.put(key, reader.unserialize());
                }
            }
        }
        reader.skip(HproseTags.TagClosebrace);
    }
    @SuppressWarnings({ "unchecked" })
    public final static Map<String, Object> readObjectAsMap(Reader reader, Class<? extends Map> type, Map<String, Type> typeMap) throws IOException {
        String[] memberNames = reader.readMemberNames();
        int count = memberNames.length;
        Map<String, Object> m = (Map<String, Object>) ConstructorAccessor.newInstance(type);
        readObjectAsMap(reader, m, typeMap, memberNames, count);
        return m;
    }
    @SuppressWarnings({ "unchecked" })
    public static void readObjectAsMap(Reader reader, Map m, Type keyType, Type valueType, int count, String[] memberNames) throws IOException {
        reader.setRef(m);
        if (count > 0) {
            InputStream stream = reader.stream;
            Class<?> keyClass = ClassUtil.toClass(keyType);
            Class<?> valueClass = ClassUtil.toClass(valueType);
            Converter converter = ConverterFactory.get(keyClass);
            Unserializer unserializer = UnserializerFactory.get(valueClass);
            for (int i = 0; i < count; ++i) {
                m.put(converter.convertTo(memberNames[i], keyType), unserializer.read(reader, stream.read(), valueType));
            }
        }
        reader.skip(HproseTags.TagClosebrace);
    }
    @SuppressWarnings({ "unchecked" })
    public final static Map readObjectAsMap(Reader reader, Class<? extends Map> type, Type keyType, Type valueType) throws IOException {
        String[] memberNames = reader.readMemberNames();
        int count = memberNames.length;
        Map m = (Map) ConstructorAccessor.newInstance(type);
        readObjectAsMap(reader, m, keyType, valueType, count, memberNames);
        return m;
    }
    @SuppressWarnings({ "unchecked" })
    public static void readObjectAsMap(Reader reader, Type type, Map m, int count, String[] memberNames) throws IOException {
        readObjectAsMap(reader, m, ClassUtil.getKeyType(type), ClassUtil.getValueType(type), count, memberNames);
    }
    public final static HashMap readObjectAsHashMap(Reader reader, Type type) throws IOException {
        String[] memberNames = reader.readMemberNames();
        int count = memberNames.length;
        HashMap m = new HashMap(count);
        readObjectAsMap(reader, type, m, count, memberNames);
        return m;
    }
    public final static LinkedHashMap readObjectAsLinkedHashMap(Reader reader, Type type) throws IOException {
        String[] memberNames = reader.readMemberNames();
        int count = memberNames.length;
        LinkedHashMap m = new LinkedHashMap(count);
        readObjectAsMap(reader, type, m, count, memberNames);
        return m;
    }
    public final static LinkedCaseInsensitiveMap readObjectAsLinkedCaseInsensitiveMap(Reader reader, Type type) throws IOException {
        String[] memberNames = reader.readMemberNames();
        int count = memberNames.length;
        LinkedCaseInsensitiveMap m = new LinkedCaseInsensitiveMap(count);
        readObjectAsMap(reader, type, m, count, memberNames);
        return m;
    }
    public final static CaseInsensitiveMap readObjectAsCaseInsensitiveMap(Reader reader, Type type) throws IOException {
        String[] memberNames = reader.readMemberNames();
        int count = memberNames.length;
        CaseInsensitiveMap m = new CaseInsensitiveMap(count);
        readObjectAsMap(reader, type, m, count, memberNames);
        return m;
    }
    public final static TreeMap readObjectAsTreeMap(Reader reader, Type type) throws IOException {
        String[] memberNames = reader.readMemberNames();
        int count = memberNames.length;
        TreeMap m = new TreeMap();
        readObjectAsMap(reader, type, m, count, memberNames);
        return m;
    }
    @SuppressWarnings({ "unchecked" })
    public final static Map readObjectAsMap(Reader reader, Type type) throws IOException {
        String[] memberNames = reader.readMemberNames();
        int count = memberNames.length;
        Class<?> cls = ClassUtil.toClass(type);
        Map m = (Map) ConstructorAccessor.newInstance(cls);
        readObjectAsMap(reader, type, m, count, memberNames);
        return m;
    }
    public static void readMap(Reader reader, Map<String, Object> m, Map<String, Type> typeMap, int count) throws IOException {
        reader.setRef(m);
        if (count > 0) {
            StringUnserializer keyUnserializer = StringUnserializer.instance;
            for (int i = 0; i < count; ++i) {
                String key = keyUnserializer.read(reader);
                if (typeMap.containsKey(key)) {
                    Type valueType = typeMap.get(key);
                    m.put(key, reader.unserialize(valueType));
                } else {
                    m.put(key, reader.unserialize());
                }
            }
        }
        reader.skip(HproseTags.TagClosebrace);
    }
    @SuppressWarnings({ "unchecked" })
    public final static Map<String, Object> readMap(Reader reader, Class<? extends Map> type, Map<String, Type> typeMap) throws IOException {
        int count = ValueReader.readCount(reader);
        Map<String, Object> m = (Map<String, Object>) ConstructorAccessor.newInstance(type);
        readMap(reader, m, typeMap, count);
        return m;
    }
    @SuppressWarnings({ "unchecked" })
    public static void readMap(Reader reader, Map m, Type keyType, Type valueType, int count) throws IOException {
        reader.setRef(m);
        if (count > 0) {
            InputStream stream = reader.stream;
            Class<?> keyClass = ClassUtil.toClass(keyType);
            Class<?> valueClass = ClassUtil.toClass(valueType);
            Unserializer keyUnserializer = UnserializerFactory.get(keyClass);
            Unserializer valueUnserializer = UnserializerFactory.get(valueClass);
            for (int i = 0; i < count; ++i) {
                Object key = keyUnserializer.read(reader, stream.read(), keyType);
                Object value = valueUnserializer.read(reader, stream.read(), valueType);
                m.put(key, value);
            }
        }
        reader.skip(HproseTags.TagClosebrace);
    }
    @SuppressWarnings({ "unchecked" })
    public final static Map readMap(Reader reader, Class<? extends Map> type, Type keyType, Type valueType) throws IOException {
        int count = ValueReader.readCount(reader);
        Map m = (Map) ConstructorAccessor.newInstance(type);
        readMap(reader, m, keyType, valueType, count);
        return m;
    }
    @SuppressWarnings({ "unchecked" })
    private static void readMap(Reader reader, Type type, Map m, int count) throws IOException {
        readMap(reader, m, ClassUtil.getKeyType(type), ClassUtil.getValueType(type), count);
    }
    public final static HashMap readHashMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        HashMap m = new HashMap(count);
        readMap(reader, type, m, count);
        return m;
    }
    public final static LinkedHashMap readLinkedHashMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        LinkedHashMap m = new LinkedHashMap(count);
        readMap(reader, type, m, count);
        return m;
    }
    public final static LinkedCaseInsensitiveMap readLinkedCaseInsensitiveMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        LinkedCaseInsensitiveMap m = new LinkedCaseInsensitiveMap(count);
        readMap(reader, type, m, count);
        return m;
    }
    public final static CaseInsensitiveMap readCaseInsensitiveMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        CaseInsensitiveMap m = new CaseInsensitiveMap(count);
        readMap(reader, type, m, count);
        return m;
    }
    public final static TreeMap readTreeMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        TreeMap m = new TreeMap();
        readMap(reader, type, m, count);
        return m;
    }
    @SuppressWarnings({ "unchecked" })
    public final static Map readMap(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        Class<?> cls = ClassUtil.toClass(type);
        Map m = (Map) ConstructorAccessor.newInstance(cls);
        readMap(reader, type, m, count);
        return m;
    }
    public final static Object readMapAsObject(Reader reader, Type type) throws IOException {
        Class<?> cls = ClassUtil.toClass(type);
        Object obj = ConstructorAccessor.newInstance(cls);
        reader.setRef(obj);
        Map<String, MemberAccessor> members = Accessors.getMembers(type, reader.mode);
        int count = ValueReader.readCount(reader);
        StringUnserializer unserializer = StringUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            String memberName = unserializer.read(reader);
            MemberAccessor member = members.get(memberName);
            if (member != null) {
                member.unserialize(reader, obj);
            } else {
                reader.unserialize();
            }
        }
        reader.skip(HproseTags.TagClosebrace);
        return obj;
    }
    private static Object readObject(Reader reader, String[] memberNames, Type type) throws HproseException, IOException {
        Class<?> cls = ClassUtil.toClass(type);
        Object obj = ConstructorAccessor.newInstance(cls);
        reader.setRef(obj);
        Map<String, MemberAccessor> members = Accessors.getMembers(type, reader.mode);
        int count = memberNames.length;
        for (int i = 0; i < count; ++i) {
            MemberAccessor member = members.get(memberNames[i]);
            if (member != null) {
                member.unserialize(reader, obj);
            } else {
                reader.unserialize();
            }
        }
        reader.skip(HproseTags.TagClosebrace);
        return obj;
    }
    private static Object readObjectAsMap(Reader reader, String[] memberNames) throws IOException {
        int count = memberNames.length;
        LinkedCaseInsensitiveMap<String, Object> map = new LinkedCaseInsensitiveMap<String, Object>(count);
        reader.setRef(map);
        for (int i = 0; i < count; ++i) {
            map.put(memberNames[i], reader.unserialize());
        }
        reader.skip(HproseTags.TagClosebrace);
        return map;
    }
    public final static Object readObject(Reader reader, Type type) throws IOException {
        Object cr = reader.readClassRef();
        String[] memberNames = reader.getMemberNames(cr);
        if (Class.class.equals(cr.getClass())) {
            Class<?> c = (Class<?>) cr;
            if ((type == null) || ((type instanceof Class<?>) && ((Class<?>) type).isAssignableFrom(c))) {
                type = c;
            }
        }
        if (type == null || Object.class.equals(type)) {
            return readObjectAsMap(reader, memberNames);
        }
        return readObject(reader, memberNames, type);
    }
    public final static Object readObject(Reader reader) throws IOException {
        Object cr = reader.readClassRef();
        String[] memberNames = reader.getMemberNames(cr);
        if (Class.class.equals(cr.getClass())) {
            return readObject(reader, memberNames, (Class<?>) cr);
        }
        return readObjectAsMap(reader, memberNames);
    }
}
