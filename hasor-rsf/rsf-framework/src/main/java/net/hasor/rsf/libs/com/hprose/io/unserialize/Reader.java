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
 * HproseReader.java                                      *
 *                                                        *
 * hprose reader class for Java.                          *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.ByteBufferStream;
import net.hasor.rsf.libs.com.hprose.io.HproseException;
import net.hasor.rsf.libs.com.hprose.io.HproseMode;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;
import net.hasor.rsf.libs.com.hprose.io.convert.DefaultConverter;
import net.hasor.rsf.libs.com.hprose.utils.ClassUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
interface ReaderRefer {
    void set(Object obj);

    Object read(int index);

    void reset();
}
final class FakeReaderRefer implements ReaderRefer {
    public final void set(Object obj) {
    }
    public final Object read(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public final void reset() {
    }
}
final class RealReaderRefer implements ReaderRefer {
    private final ArrayList<Object> ref = new ArrayList<Object>();
    public final void set(Object obj) {
        ref.add(obj);
    }
    public final Object read(int index) {
        return ref.get(index);
    }
    public final void reset() {
        ref.clear();
    }
}
public class Reader {
    public final InputStream stream;
    final        HproseMode  mode;
    final ArrayList<Object>                 classref   = new ArrayList<Object>();
    final IdentityHashMap<Object, String[]> membersref = new IdentityHashMap<Object, String[]>();
    final ReaderRefer refer;
    public Reader(InputStream stream) {
        this(stream, HproseMode.MemberMode, false);
    }
    public Reader(InputStream stream, boolean simple) {
        this(stream, HproseMode.MemberMode, simple);
    }
    public Reader(InputStream stream, HproseMode mode) {
        this(stream, mode, false);
    }
    public Reader(InputStream stream, HproseMode mode, boolean simple) {
        this.stream = stream;
        this.mode = mode;
        this.refer = simple ? new FakeReaderRefer() : new RealReaderRefer();
    }
    public Reader(ByteBuffer buffer) {
        this(buffer, HproseMode.MemberMode, false);
    }
    public Reader(ByteBuffer buffer, boolean simple) {
        this(buffer, HproseMode.MemberMode, simple);
    }
    public Reader(ByteBuffer buffer, HproseMode mode) {
        this(buffer, mode, false);
    }
    public Reader(ByteBuffer buffer, HproseMode mode, boolean simple) {
        this(new ByteBufferStream(buffer).getInputStream(), mode, simple);
    }
    public Reader(byte[] bytes) {
        this(bytes, HproseMode.MemberMode, false);
    }
    public Reader(byte[] bytes, boolean simple) {
        this(bytes, HproseMode.MemberMode, simple);
    }
    public Reader(byte[] bytes, HproseMode mode) {
        this(bytes, mode, false);
    }
    public Reader(byte[] bytes, HproseMode mode, boolean simple) {
        this(new ByteArrayInputStream(bytes), mode, simple);
    }
    public final HproseException unexpectedTag(int tag) {
        return unexpectedTag(tag, null);
    }
    public final HproseException unexpectedTag(int tag, String expectTags) {
        if (tag == -1) {
            return new HproseException("No byte found in stream");
        } else if (expectTags == null) {
            return new HproseException("Unexpected serialize tag '" + (char) tag + "' in stream");
        } else {
            return new HproseException("Tag '" + expectTags + "' expected, but '" + (char) tag + "' found in stream");
        }
    }
    public final void checkTag(int tag, int expectTag) throws HproseException {
        if (tag != expectTag) {
            throw unexpectedTag(tag, new String(new char[] { (char) expectTag }));
        }
    }
    public final void checkTag(int expectTag) throws IOException {
        checkTag(stream.read(), expectTag);
    }
    public final int checkTags(int tag, String expectTags) throws IOException {
        if (expectTags.indexOf(tag) == -1) {
            throw unexpectedTag(tag, expectTags);
        }
        return tag;
    }
    public final int checkTags(String expectTags) throws IOException {
        return checkTags(stream.read(), expectTags);
    }
    public final void skip(int tag) throws IOException {
        int c = stream.read();
        assert c == tag : "Tag '" + (char) tag + "' expected, but '" + (char) c + "' found in stream";
    }
    public final byte readByte(int tag) throws IOException {
        return (byte) ValueReader.readInt(this, tag);
    }
    public final short readShort(int tag) throws IOException {
        return (short) ValueReader.readInt(this, tag);
    }
    public final int readInt(int tag) throws IOException {
        return ValueReader.readInt(this, tag);
    }
    public final long readLong(int tag) throws IOException {
        return ValueReader.readLong(this, tag);
    }
    public final int readIntWithoutTag() throws IOException {
        return ValueReader.readInt(this);
    }
    public final BigInteger readBigIntegerWithoutTag() throws IOException {
        return ValueReader.readBigInteger(this);
    }
    public final long readLongWithoutTag() throws IOException {
        return ValueReader.readInt(this);
    }
    public final double readDoubleWithoutTag() throws IOException {
        return ValueReader.readDouble(this);
    }
    public final double readInfinityWithoutTag() throws IOException {
        return ValueReader.readInfinity(this);
    }
    public final Calendar readDateWithoutTag() throws IOException {
        return ReferenceReader.readDateTime(this).toCalendar();
    }
    public final Calendar readTimeWithoutTag() throws IOException {
        return ReferenceReader.readTime(this).toCalendar();
    }
    public final byte[] readBytesWithoutTag() throws IOException {
        return ReferenceReader.readBytes(this);
    }
    public final String readUTF8CharWithoutTag() throws IOException {
        return ValueReader.readUTF8Char(this);
    }
    public final String readStringWithoutTag() throws IOException {
        return ReferenceReader.readString(this);
    }
    public final char[] readCharsWithoutTag() throws IOException {
        return ReferenceReader.readChars(this);
    }
    public final UUID readUUIDWithoutTag() throws IOException {
        return ReferenceReader.readUUID(this);
    }
    public final ArrayList readListWithoutTag() throws IOException {
        return ReferenceReader.readArrayList(this);
    }
    public final HashMap readMapWithoutTag() throws IOException {
        return ReferenceReader.readHashMap(this);
    }
    public final Object readObjectWithoutTag(Type type) throws IOException {
        return ReferenceReader.readObject(this, type);
    }
    public final Object unserialize() throws IOException {
        return DefaultUnserializer.instance.read(this);
    }
    public final boolean readBoolean() throws IOException {
        return BooleanUnserializer.instance.read(this);
    }
    public final Boolean readBooleanObject() throws IOException {
        return BooleanObjectUnserializer.instance.read(this);
    }
    public final char readChar() throws IOException {
        return CharUnserializer.instance.read(this);
    }
    public final Character readCharObject() throws IOException {
        return CharObjectUnserializer.instance.read(this);
    }
    public final byte readByte() throws IOException {
        return ByteUnserializer.instance.read(this);
    }
    public final Byte readByteObject() throws IOException {
        return ByteObjectUnserializer.instance.read(this);
    }
    public final short readShort() throws IOException {
        return ShortUnserializer.instance.read(this);
    }
    public final Short readShortObject() throws IOException {
        return ShortObjectUnserializer.instance.read(this);
    }
    public final int readInt() throws IOException {
        return IntUnserializer.instance.read(this);
    }
    public final Integer readIntObject() throws IOException {
        return IntObjectUnserializer.instance.read(this);
    }
    public final long readLong() throws IOException {
        return LongUnserializer.instance.read(this);
    }
    public final Long readLongObject() throws IOException {
        return LongObjectUnserializer.instance.read(this);
    }
    public final float readFloat() throws IOException {
        return FloatUnserializer.instance.read(this);
    }
    public final Float readFloatObject() throws IOException {
        return FloatObjectUnserializer.instance.read(this);
    }
    public final double readDouble() throws IOException {
        return DoubleUnserializer.instance.read(this);
    }
    public final Double readDoubleObject() throws IOException {
        return DoubleObjectUnserializer.instance.read(this);
    }
    @SuppressWarnings({ "unchecked" })
    public final <T extends Enum<T>> T readEnum(Class<T> type) throws IOException {
        return (T) EnumUnserializer.instance.read(this, type);
    }
    public final String readString() throws IOException {
        return StringUnserializer.instance.read(this);
    }
    public final BigInteger readBigInteger() throws IOException {
        return BigIntegerUnserializer.instance.read(this);
    }
    public final Date readDate() throws IOException {
        return DateUnserializer.instance.read(this);
    }
    public final Time readTime() throws IOException {
        return TimeUnserializer.instance.read(this);
    }
    public final java.util.Date readDateTime() throws IOException {
        return DateTimeUnserializer.instance.read(this);
    }
    public final Timestamp readTimestamp() throws IOException {
        return TimestampUnserializer.instance.read(this);
    }
    public final Calendar readCalendar() throws IOException {
        return CalendarUnserializer.instance.read(this);
    }
    public final BigDecimal readBigDecimal() throws IOException {
        return BigDecimalUnserializer.instance.read(this);
    }
    public final StringBuilder readStringBuilder() throws IOException {
        return StringBuilderUnserializer.instance.read(this);
    }
    public final StringBuffer readStringBuffer() throws IOException {
        return StringBufferUnserializer.instance.read(this);
    }
    public final UUID readUUID() throws IOException {
        return UUIDUnserializer.instance.read(this);
    }
    public final void readArray(Type[] types, Object[] a, int count) throws IOException {
        ReferenceReader.readArray(this, types, a, count);
    }
    public final Object[] readArray(int count) throws IOException {
        return ReferenceReader.readArray(this, count);
    }
    public final Object[] readObjectArray() throws IOException {
        return ObjectArrayUnserializer.instance.read(this);
    }
    public final boolean[] readBooleanArray() throws IOException {
        return BooleanArrayUnserializer.instance.read(this);
    }
    public final char[] readCharArray() throws IOException {
        return CharArrayUnserializer.instance.read(this);
    }
    public final byte[] readByteArray() throws IOException {
        return ByteArrayUnserializer.instance.read(this);
    }
    public final short[] readShortArray() throws IOException {
        return ShortArrayUnserializer.instance.read(this);
    }
    public final int[] readIntArray() throws IOException {
        return IntArrayUnserializer.instance.read(this);
    }
    public final long[] readLongArray() throws IOException {
        return LongArrayUnserializer.instance.read(this);
    }
    public final float[] readFloatArray() throws IOException {
        return FloatArrayUnserializer.instance.read(this);
    }
    public final double[] readDoubleArray() throws IOException {
        return DoubleArrayUnserializer.instance.read(this);
    }
    public final String[] readStringArray() throws IOException {
        return StringArrayUnserializer.instance.read(this);
    }
    public final BigInteger[] readBigIntegerArray() throws IOException {
        return BigIntegerArrayUnserializer.instance.read(this);
    }
    public final Date[] readDateArray() throws IOException {
        return DateArrayUnserializer.instance.read(this);
    }
    public final Time[] readTimeArray() throws IOException {
        return TimeArrayUnserializer.instance.read(this);
    }
    public final Timestamp[] readTimestampArray() throws IOException {
        return TimestampArrayUnserializer.instance.read(this);
    }
    public final java.util.Date[] readDateTimeArray() throws IOException {
        return DateTimeArrayUnserializer.instance.read(this);
    }
    public final Calendar[] readCalendarArray() throws IOException {
        return CalendarArrayUnserializer.instance.read(this);
    }
    public final BigDecimal[] readBigDecimalArray() throws IOException {
        return BigDecimalArrayUnserializer.instance.read(this);
    }
    public final StringBuilder[] readStringBuilderArray() throws IOException {
        return StringBuilderArrayUnserializer.instance.read(this);
    }
    public final StringBuffer[] readStringBufferArray() throws IOException {
        return StringBufferArrayUnserializer.instance.read(this);
    }
    public final UUID[] readUUIDArray() throws IOException {
        return UUIDArrayUnserializer.instance.read(this);
    }
    public final char[][] readCharsArray() throws IOException {
        return CharsArrayUnserializer.instance.read(this);
    }
    public final byte[][] readBytesArray() throws IOException {
        return BytesArrayUnserializer.instance.read(this);
    }
    public final Object unserialize(Type type) throws IOException {
        if (type == null) {
            return DefaultUnserializer.instance.read(this);
        }
        Class<?> cls = ClassUtil.toClass(type);
        return UnserializerFactory.get(cls).read(this, stream.read(), type);
    }
    @SuppressWarnings({ "unchecked" })
    public final <T> T unserialize(Class<T> type) throws IOException {
        return (T) unserialize((Type) type);
    }
    public final Object readRef() throws IOException {
        return refer.read(ValueReader.readInt(this));
    }
    @SuppressWarnings({ "unchecked" })
    public final <T> T readRef(Type type) throws IOException {
        return (T) DefaultConverter.instance.convertTo(readRef(), type);
    }
    public void setRef(Object obj) {
        refer.set(obj);
    }
    public void readClass() throws IOException {
        String className = ValueReader.readString(this);
        int count = ValueReader.readCount(this);
        String[] memberNames = new String[count];
        StringUnserializer unserialize = StringUnserializer.instance;
        for (int i = 0; i < count; ++i) {
            memberNames[i] = unserialize.read(this, stream.read(), String.class);
        }
        stream.read();
        Class<?> cls = ClassUtil.getClass(className);
        Object key = (cls.equals(void.class)) ? new Object() : cls;
        classref.add(key);
        membersref.put(key, memberNames);
    }
    public Object readClassRef() throws IOException {
        return classref.get(ValueReader.readInt(this, HproseTags.TagOpenbrace));
    }
    public String[] getMemberNames(Object cr) {
        return membersref.get(cr);
    }
    public String[] readMemberNames() throws IOException {
        return getMemberNames(readClassRef());
    }
    public final String tagToString(int tag) throws IOException {
        switch (tag) {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case HproseTags.TagInteger:
            return "Integer";
        case HproseTags.TagLong:
            return "BigInteger";
        case HproseTags.TagDouble:
            return "Double";
        case HproseTags.TagNull:
            return "Null";
        case HproseTags.TagEmpty:
            return "Empty String";
        case HproseTags.TagTrue:
            return "Boolean True";
        case HproseTags.TagFalse:
            return "Boolean False";
        case HproseTags.TagNaN:
            return "NaN";
        case HproseTags.TagInfinity:
            return "Infinity";
        case HproseTags.TagDate:
            return "DateTime";
        case HproseTags.TagTime:
            return "DateTime";
        case HproseTags.TagBytes:
            return "Byte[]";
        case HproseTags.TagUTF8Char:
            return "Char";
        case HproseTags.TagString:
            return "String";
        case HproseTags.TagGuid:
            return "Guid";
        case HproseTags.TagList:
            return "IList";
        case HproseTags.TagMap:
            return "IDictionary";
        case HproseTags.TagClass:
            return "Class";
        case HproseTags.TagObject:
            return "Object";
        case HproseTags.TagRef:
            return "Object Reference";
        case HproseTags.TagError:
            throw new HproseException(readString());
        default:
            throw unexpectedTag(tag);
        }
    }
    public final ByteBufferStream readRaw() throws IOException {
        ByteBufferStream rawstream = new ByteBufferStream();
        readRaw(rawstream.getOutputStream());
        rawstream.flip();
        return rawstream;
    }
    public final void readRaw(OutputStream ostream) throws IOException {
        RawReader.readRaw(stream, ostream, stream.read());
    }
    public final void reset() {
        refer.reset();
        classref.clear();
        membersref.clear();
    }
}