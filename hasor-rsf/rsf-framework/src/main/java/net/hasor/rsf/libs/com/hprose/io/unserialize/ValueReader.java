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
 * ValueReader.java                                       *
 *                                                        *
 * value reader class for Java.                           *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.UUID;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class ValueReader {
    public final static IOException badEncoding(int c) {
        return new IOException("bad utf-8 encoding at " + ((c < 0) ? "end of stream" : "0x" + Integer.toHexString(c & 255)));
    }
    public final static ClassCastException castError(String srctype, Type desttype) {
        return new ClassCastException(srctype + " can't change to " + desttype.toString());
    }
    public final static ClassCastException castError(Object obj, Type type) {
        return new ClassCastException(obj.getClass().toString() + " can't change to " + type.toString());
    }
    @SuppressWarnings({ "fallthrough" })
    public final static int readInt(Reader reader, int tag) throws IOException {
        InputStream stream = reader.stream;
        int result = 0;
        int i = stream.read();
        if (i == tag) {
            return result;
        }
        boolean neg = false;
        switch (i) {
        case '-':
            neg = true; // fallthrough
        case '+':
            i = stream.read();
            break;
        }
        if (neg) {
            while ((i != tag) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        } else {
            while ((i != tag) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }
    @SuppressWarnings({ "fallthrough" })
    public final static long readLong(Reader reader, int tag) throws IOException {
        InputStream stream = reader.stream;
        long result = 0;
        int i = stream.read();
        if (i == tag) {
            return result;
        }
        boolean neg = false;
        switch (i) {
        case '-':
            neg = true; // fallthrough
        case '+':
            i = stream.read();
            break;
        }
        if (neg) {
            while ((i != tag) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        } else {
            while ((i != tag) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }
    @SuppressWarnings({ "fallthrough" })
    public final static float readLongAsFloat(Reader reader) throws IOException {
        InputStream stream = reader.stream;
        float result = 0.0F;
        int i = stream.read();
        if (i == TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
        case '-':
            neg = true; // fallthrough
        case '+':
            i = stream.read();
            break;
        }
        if (neg) {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        } else {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }
    @SuppressWarnings({ "fallthrough" })
    public final static double readLongAsDouble(Reader reader) throws IOException {
        InputStream stream = reader.stream;
        double result = 0.0;
        int i = stream.read();
        if (i == TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
        case '-':
            neg = true; // fallthrough
        case '+':
            i = stream.read();
            break;
        }
        if (neg) {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        } else {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }
    public final static float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return Float.NaN;
        }
    }
    public final static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
    public final static float parseFloat(StringBuilder value) {
        try {
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException e) {
            return Float.NaN;
        }
    }
    public final static double parseDouble(StringBuilder value) {
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
    public final static int readLength(Reader reader) throws IOException {
        return readInt(reader, TagQuote);
    }
    public final static int readCount(Reader reader) throws IOException {
        return readInt(reader, TagOpenbrace);
    }
    public final static StringBuilder readUntil(Reader reader, int tag) throws IOException {
        InputStream stream = reader.stream;
        StringBuilder sb = new StringBuilder();
        int i = stream.read();
        while ((i != tag) && (i != -1)) {
            sb.append((char) i);
            i = stream.read();
        }
        return sb;
    }
    public final static char[] readChars(Reader reader) throws IOException {
        int len = readLength(reader);
        char[] buf = new char[len];
        int b1, b2, b3, b4;
        InputStream stream = reader.stream;
        for (int i = 0; i < len; ++i) {
            b1 = stream.read();
            switch (b1 >>> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                // 0xxx xxxx
                buf[i] = (char) b1;
                break;
            case 12:
            case 13:
                // 110x xxxx   10xx xxxx
                b2 = stream.read();
                buf[i] = (char) (((b1 & 0x1f) << 6) | (b2 & 0x3f));
                break;
            case 14:
                b2 = stream.read();
                b3 = stream.read();
                buf[i] = (char) (((b1 & 0x0f) << 12) | ((b2 & 0x3f) << 6) | (b3 & 0x3f));
                break;
            case 15:
                // 1111 0xxx  10xx xxxx  10xx xxxx  10xx xxxx
                if ((b1 & 0xf) <= 4) {
                    b2 = stream.read();
                    b3 = stream.read();
                    b4 = stream.read();
                    int s = (((b1 & 0x07) << 18) | ((b2 & 0x3f) << 12) | ((b3 & 0x3f) << 6) | (b4 & 0x3f)) - 0x10000;
                    if (0 <= s && s <= 0xfffff) {
                        buf[i] = (char) (((s >> 10) & 0x03ff) | 0xd800);
                        buf[++i] = (char) ((s & 0x03ff) | 0xdc00);
                        break;
                    }
                }
                // fallthrough
            default:
                throw badEncoding(b1);
            }
        }
        reader.skip(TagQuote);
        return buf;
    }
    public final static char readChar(Reader reader) throws IOException {
        InputStream stream = reader.stream;
        char u;
        int b1 = stream.read(), b2, b3;
        switch (b1 >>> 4) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
            u = (char) b1;
            break;
        case 12:
        case 13:
            b2 = stream.read();
            u = (char) (((b1 & 0x1f) << 6) | (b2 & 0x3f));
            break;
        case 14:
            b2 = stream.read();
            b3 = stream.read();
            u = (char) (((b1 & 0x0f) << 12) | ((b2 & 0x3f) << 6) | (b3 & 0x3f));
            break;
        default:
            throw badEncoding(b1);
        }
        return u;
    }
    public final static int readInt(Reader reader) throws IOException {
        return readInt(reader, TagSemicolon);
    }
    public final static long readLong(Reader reader) throws IOException {
        return readLong(reader, TagSemicolon);
    }
    public final static BigInteger readBigInteger(Reader reader) throws IOException {
        return new BigInteger(readUntil(reader, TagSemicolon).toString(), 10);
    }
    public final static float readFloat(Reader reader) throws IOException {
        return parseFloat(readUntil(reader, TagSemicolon));
    }
    public final static double readDouble(Reader reader) throws IOException {
        return parseDouble(readUntil(reader, TagSemicolon));
    }
    public final static float readFloatInfinity(Reader reader) throws IOException {
        return (reader.stream.read() == TagNeg) ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
    }
    public final static double readInfinity(Reader reader) throws IOException {
        return (reader.stream.read() == TagNeg) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    }
    public final static String readString(Reader reader) throws IOException {
        return new String(readChars(reader));
    }
    public final static String readUTF8Char(Reader reader) throws IOException {
        return new String(new char[] { readChar(reader) });
    }
    public final static byte[] readBytes(Reader reader) throws IOException {
        InputStream stream = reader.stream;
        int len = readLength(reader);
        int off = 0;
        byte[] b = new byte[len];
        while (len > 0) {
            int size = stream.read(b, off, len);
            off += size;
            len -= size;
        }
        reader.skip(TagQuote);
        return b;
    }
    private static int read4Digit(InputStream stream) throws IOException {
        int n = stream.read() - '0';
        n = n * 10 + stream.read() - '0';
        n = n * 10 + stream.read() - '0';
        return n * 10 + stream.read() - '0';
    }
    private static int read2Digit(InputStream stream) throws IOException {
        int n = stream.read() - '0';
        return n * 10 + stream.read() - '0';
    }
    public final static int readTime(Reader reader, DateTime dt) throws IOException {
        InputStream stream = reader.stream;
        dt.hour = read2Digit(stream);
        dt.minute = read2Digit(stream);
        dt.second = read2Digit(stream);
        int tag = stream.read();
        if (tag == TagPoint) {
            dt.nanosecond = stream.read() - '0';
            dt.nanosecond = dt.nanosecond * 10 + (stream.read() - '0');
            dt.nanosecond = dt.nanosecond * 10 + (stream.read() - '0');
            dt.nanosecond = dt.nanosecond * 1000000;
            tag = stream.read();
            if (tag >= '0' && tag <= '9') {
                dt.nanosecond += (tag - '0') * 100000;
                dt.nanosecond += (stream.read() - '0') * 10000;
                dt.nanosecond += (stream.read() - '0') * 1000;
                tag = stream.read();
                if (tag >= '0' && tag <= '9') {
                    dt.nanosecond += (tag - '0') * 100;
                    dt.nanosecond += (stream.read() - '0') * 10;
                    dt.nanosecond += stream.read() - '0';
                    tag = stream.read();
                }
            }
        }
        return tag;
    }
    public final static DateTime readDateTime(Reader reader) throws IOException {
        InputStream stream = reader.stream;
        DateTime dt = new DateTime();
        dt.year = read4Digit(stream);
        dt.month = read2Digit(stream);
        dt.day = read2Digit(stream);
        int tag = stream.read();
        if (tag == TagTime) {
            tag = readTime(reader, dt);
        }
        dt.utc = (tag == TagUTC);
        return dt;
    }
    public final static DateTime readTime(Reader reader) throws IOException {
        DateTime dt = new DateTime();
        dt.utc = (readTime(reader, dt) == TagUTC);
        return dt;
    }
    public final static UUID readUUID(Reader reader) throws IOException {
        InputStream stream = reader.stream;
        reader.skip(TagOpenbrace);
        char[] buf = new char[36];
        for (int i = 0; i < 36; ++i) {
            buf[i] = (char) stream.read();
        }
        reader.skip(TagClosebrace);
        UUID uuid = UUID.fromString(new String(buf));
        return uuid;
    }
}
