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
 * Writer.java                                            *
 *                                                        *
 * hprose writer class for Java.                          *
 *                                                        *
 * LastModified: Jul 31, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.io.HproseMode;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public class Writer {
    public final OutputStream stream;
    final        WriterRefer  refer;
    final        HproseMode   mode;
    final HashMap<Class<?>, Integer> classref = new HashMap<Class<?>, Integer>();
    int lastclassref = 0;
    public Writer(OutputStream stream) {
        this(stream, HproseMode.MemberMode, false);
    }
    public Writer(OutputStream stream, boolean simple) {
        this(stream, HproseMode.MemberMode, simple);
    }
    public Writer(OutputStream stream, HproseMode mode) {
        this(stream, mode, false);
    }
    public Writer(OutputStream stream, HproseMode mode, boolean simple) {
        this.stream = stream;
        this.mode = mode;
        this.refer = simple ? null : new WriterRefer();
    }
    @SuppressWarnings({ "unchecked" })
    public final void serialize(Object obj) throws IOException {
        if (obj == null) {
            stream.write(TagNull);
        } else {
            SerializerFactory.get(obj.getClass()).write(this, obj);
        }
    }
    public final void writeInteger(int i) throws IOException {
        ValueWriter.write(stream, i);
    }
    public final void writeLong(long l) throws IOException {
        ValueWriter.write(stream, l);
    }
    public final void writeBigInteger(BigInteger bi) throws IOException {
        ValueWriter.write(stream, bi);
    }
    public final void writeFloat(float f) throws IOException {
        ValueWriter.write(stream, f);
    }
    public final void writeDouble(double d) throws IOException {
        ValueWriter.write(stream, d);
    }
    public final void writeBigDecimal(BigDecimal bd) throws IOException {
        ValueWriter.write(stream, bd);
    }
    public final void writeNaN() throws IOException {
        stream.write(TagNaN);
    }
    public final void writeInfinity(boolean positive) throws IOException {
        stream.write(TagInfinity);
        stream.write(positive ? TagPos : TagNeg);
    }
    public final void writeNull() throws IOException {
        stream.write(TagNull);
    }
    public final void writeEmpty() throws IOException {
        stream.write(TagEmpty);
    }
    public final void writeBoolean(boolean b) throws IOException {
        stream.write(b ? TagTrue : TagFalse);
    }
    public final void writeDate(Date date) throws IOException {
        DateSerializer.instance.serialize(this, date);
    }
    public final void writeDateWithRef(Date date) throws IOException {
        DateSerializer.instance.write(this, date);
    }
    public final void writeDate(Time time) throws IOException {
        TimeSerializer.instance.serialize(this, time);
    }
    public final void writeDateWithRef(Time time) throws IOException {
        TimeSerializer.instance.write(this, time);
    }
    public final void writeDate(Timestamp time) throws IOException {
        TimestampSerializer.instance.serialize(this, time);
    }
    public final void writeDateWithRef(Timestamp time) throws IOException {
        TimestampSerializer.instance.write(this, time);
    }
    public final void writeDate(java.util.Date date) throws IOException {
        DateTimeSerializer.instance.serialize(this, date);
    }
    public final void writeDateWithRef(java.util.Date date) throws IOException {
        DateTimeSerializer.instance.write(this, date);
    }
    public final void writeDate(Calendar calendar) throws IOException {
        CalendarSerializer.instance.serialize(this, calendar);
    }
    public final void writeDateWithRef(Calendar calendar) throws IOException {
        CalendarSerializer.instance.write(this, calendar);
    }
    public final void writeTime(Time time) throws IOException {
        writeDate(time);
    }
    public final void writeTimeWithRef(Time time) throws IOException {
        writeDateWithRef(time);
    }
    public final void writeBytes(byte[] bytes) throws IOException {
        ByteArraySerializer.instance.serialize(this, bytes);
    }
    public final void writeBytesWithRef(byte[] bytes) throws IOException {
        ByteArraySerializer.instance.write(this, bytes);
    }
    public final void writeUTF8Char(char c) throws IOException {
        ValueWriter.write(stream, c);
    }
    public final void writeString(String s) throws IOException {
        StringSerializer.instance.serialize(this, s);
    }
    public final void writeStringWithRef(String s) throws IOException {
        StringSerializer.instance.write(this, s);
    }
    public final void writeString(StringBuilder s) throws IOException {
        StringBuilderSerializer.instance.serialize(this, s);
    }
    public final void writeStringWithRef(StringBuilder s) throws IOException {
        StringBuilderSerializer.instance.write(this, s);
    }
    public final void writeString(StringBuffer s) throws IOException {
        StringBufferSerializer.instance.serialize(this, s);
    }
    public final void writeStringWithRef(StringBuffer s) throws IOException {
        StringBufferSerializer.instance.write(this, s);
    }
    public final void writeString(char[] s) throws IOException {
        CharArraySerializer.instance.serialize(this, s);
    }
    public final void writeStringWithRef(char[] s) throws IOException {
        CharArraySerializer.instance.write(this, s);
    }
    public final void writeUUID(UUID uuid) throws IOException {
        UUIDSerializer.instance.serialize(this, uuid);
    }
    public final void writeUUIDWithRef(UUID uuid) throws IOException {
        UUIDSerializer.instance.write(this, uuid);
    }
    public final void writeArray(short[] array) throws IOException {
        ShortArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(short[] array) throws IOException {
        ShortArraySerializer.instance.write(this, array);
    }
    public final void writeArray(int[] array) throws IOException {
        IntArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(int[] array) throws IOException {
        IntArraySerializer.instance.write(this, array);
    }
    public final void writeArray(long[] array) throws IOException {
        LongArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(long[] array) throws IOException {
        LongArraySerializer.instance.write(this, array);
    }
    public final void writeArray(float[] array) throws IOException {
        FloatArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(float[] array) throws IOException {
        FloatArraySerializer.instance.write(this, array);
    }
    public final void writeArray(double[] array) throws IOException {
        DoubleArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(double[] array) throws IOException {
        DoubleArraySerializer.instance.write(this, array);
    }
    public final void writeArray(boolean[] array) throws IOException {
        BooleanArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(boolean[] array) throws IOException {
        BooleanArraySerializer.instance.write(this, array);
    }
    public final void writeArray(Date[] array) throws IOException {
        DateArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(Date[] array) throws IOException {
        DateArraySerializer.instance.write(this, array);
    }
    public final void writeArray(Time[] array) throws IOException {
        TimeArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(Time[] array) throws IOException {
        TimeArraySerializer.instance.write(this, array);
    }
    public final void writeArray(Timestamp[] array) throws IOException {
        TimestampArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(Timestamp[] array) throws IOException {
        TimestampArraySerializer.instance.write(this, array);
    }
    public final void writeArray(java.util.Date[] array) throws IOException {
        DateTimeArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(java.util.Date[] array) throws IOException {
        DateTimeArraySerializer.instance.write(this, array);
    }
    public final void writeArray(Calendar[] array) throws IOException {
        CalendarArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(Calendar[] array) throws IOException {
        CalendarArraySerializer.instance.write(this, array);
    }
    public final void writeArray(String[] array) throws IOException {
        StringArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(String[] array) throws IOException {
        StringArraySerializer.instance.write(this, array);
    }
    public final void writeArray(StringBuilder[] array) throws IOException {
        StringBuilderArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(StringBuilder[] array) throws IOException {
        StringBuilderArraySerializer.instance.write(this, array);
    }
    public final void writeArray(StringBuffer[] array) throws IOException {
        StringBufferArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(StringBuffer[] array) throws IOException {
        StringBufferArraySerializer.instance.write(this, array);
    }
    public final void writeArray(UUID[] array) throws IOException {
        UUIDArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(UUID[] array) throws IOException {
        UUIDArraySerializer.instance.write(this, array);
    }
    public final void writeArray(char[][] array) throws IOException {
        CharsArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(char[][] array) throws IOException {
        CharsArraySerializer.instance.write(this, array);
    }
    public final void writeArray(byte[][] array) throws IOException {
        BytesArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(byte[][] array) throws IOException {
        BytesArraySerializer.instance.write(this, array);
    }
    public final void writeArray(BigInteger[] array) throws IOException {
        BigIntegerArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(BigInteger[] array) throws IOException {
        BigIntegerArraySerializer.instance.write(this, array);
    }
    public final void writeArray(BigDecimal[] array) throws IOException {
        BigDecimalArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(BigDecimal[] array) throws IOException {
        BigDecimalArraySerializer.instance.write(this, array);
    }
    public final void writeArray(Object[] array) throws IOException {
        ObjectArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(Object[] array) throws IOException {
        ObjectArraySerializer.instance.write(this, array);
    }
    public final void writeArray(AtomicIntegerArray array) throws IOException {
        AtomicIntegerArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(AtomicIntegerArray array) throws IOException {
        AtomicIntegerArraySerializer.instance.write(this, array);
    }
    public final void writeArray(AtomicLongArray array) throws IOException {
        AtomicLongArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(AtomicLongArray array) throws IOException {
        AtomicLongArraySerializer.instance.write(this, array);
    }
    public final void writeArray(AtomicReferenceArray array) throws IOException {
        AtomicReferenceArraySerializer.instance.serialize(this, array);
    }
    public final void writeArrayWithRef(AtomicReferenceArray array) throws IOException {
        AtomicReferenceArraySerializer.instance.write(this, array);
    }
    public final void writeArray(Object array) throws IOException {
        OtherTypeArraySerializer.instance.serialize(this, array);
    }
    @SuppressWarnings({ "unchecked" })
    public final void writeArrayWithRef(Object array) throws IOException {
        OtherTypeArraySerializer.instance.write(this, array);
    }
    @SuppressWarnings({ "unchecked" })
    public final void writeCollection(Collection<?> collection) throws IOException {
        CollectionSerializer.instance.serialize(this, collection);
    }
    @SuppressWarnings({ "unchecked" })
    public final void writeCollectionWithRef(Collection<?> collection) throws IOException {
        CollectionSerializer.instance.write(this, collection);
    }
    @SuppressWarnings({ "unchecked" })
    public final void writeList(List<?> list) throws IOException {
        ListSerializer.instance.serialize(this, list);
    }
    @SuppressWarnings({ "unchecked" })
    public final void writeListWithRef(List<?> list) throws IOException {
        ListSerializer.instance.write(this, list);
    }
    @SuppressWarnings({ "unchecked" })
    public final void writeMap(Map<?, ?> map) throws IOException {
        MapSerializer.instance.write(this, map);
    }
    @SuppressWarnings({ "unchecked" })
    public final void writeMapWithRef(Map<?, ?> map) throws IOException {
        MapSerializer.instance.write(this, map);
    }
    public final void writeObject(Object object) throws IOException {
        OtherTypeSerializer.instance.serialize(this, object);
    }
    @SuppressWarnings({ "unchecked" })
    public final void writeObjectWithRef(Object object) throws IOException {
        OtherTypeSerializer.instance.write(this, object);
    }
    final boolean writeRef(Object object) throws IOException {
        return refer != null && refer.write(stream, object);
    }
    final void setRef(Object object) {
        if (refer != null)
            refer.set(object);
    }
    public final void reset() {
        if (refer != null) {
            refer.reset();
        }
        classref.clear();
        lastclassref = 0;
    }
}
