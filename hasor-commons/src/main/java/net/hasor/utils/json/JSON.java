// ========================================================================
// Copyright (c) 2004-2009 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses.
// ========================================================================
package net.hasor.utils.json;
import net.hasor.utils.StringUtils;

import java.io.Externalizable;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * JSON Parser and Generator.
 *
 * <p>
 * This class provides some static methods to convert POJOs to and from JSON
 * notation. The mapping from JSON to java is:
 *
 * <pre>
 *   object ==> Map
 *   array  ==> Object[]
 *   number ==> Double or Long
 *   string ==> String
 *   null   ==> null
 *   bool   ==> Boolean
 * </pre>
 *
 * </p>
 * <p>
 * The java to JSON mapping is:
 *
 * <pre>
 *   String --> string
 *   Number --> number
 *   Map    --> object
 *   List   --> array
 *   Array  --> array
 *   null   --> null
 *   Boolean--> boolean
 *   Object --> string (dubious!)
 * </pre>
 *
 * </p>
 * <p>
 * The interface {@link net.hasor.utils.json.JSON.Convertible} may be implemented by classes that
 * wish to externalize and initialize specific fields to and from JSON objects.
 * Only directed acyclic graphs of objects are supported.
 * </p>
 * <p>
 * The interface {@link net.hasor.utils.json.JSON.Generator} may be implemented by classes that know
 * how to render themselves as JSON and the {@link #toString(Object)} method
 * will use {@link net.hasor.utils.json.JSON.Generator#addJSON(Appendable)} to generate the JSON.
 * The class {@link net.hasor.utils.json.JSON.Literal} may be used to hold pre-generated JSON object.
 * <p>
 * The interface {@link net.hasor.utils.json.JSON.Convertor} may be implemented to provide static
 * convertors for objects that may be registered with
 * {@link #getConvertor(Class)}.
 * </p>
 *
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class JSON {
    protected final static Logger                 logger            = Logger.getLogger(JSON.class.getName());
    public final static    JSON                   DEFAULT           = new JSON();
    private                Map<String, Convertor> _convertors       = new ConcurrentHashMap<String, Convertor>();
    private                int                    _stringBufferSize = 1024;
    public JSON() {
    }
    /* ------------------------------------------------------------ */
    /** @return the initial stringBuffer size to use when creating JSON strings (default 1024) */
    public int getStringBufferSize() {
        return _stringBufferSize;
    }
    /* ------------------------------------------------------------ */
    /** @param stringBufferSize the initial stringBuffer size to use when creating JSON strings (default 1024) */
    public void setStringBufferSize(int stringBufferSize) {
        _stringBufferSize = stringBufferSize;
    }
    /* ------------------------------------------------------------ */
    /**
     * Register a {@link Convertor} for a class or interface.
     * @param forClass The class or interface that the convertor applies to
     * @param convertor the convertor
     */
    public static void registerConvertor(Class forClass, Convertor convertor) {
        DEFAULT.addConvertor(forClass, convertor);
    }
    /* ------------------------------------------------------------ */
    public static JSON getDefault() {
        return DEFAULT;
    }
    /* ------------------------------------------------------------ */
    public static String toString(Object object) {
        StringBuilder buffer = new StringBuilder(DEFAULT.getStringBufferSize());
        DEFAULT.append(buffer, object);
        return buffer.toString();
    }
    /* ------------------------------------------------------------ */
    public static String toString(Map object) {
        StringBuilder buffer = new StringBuilder(DEFAULT.getStringBufferSize());
        DEFAULT.appendMap(buffer, object);
        return buffer.toString();
    }
    /* ------------------------------------------------------------ */
    public static String toString(Object[] array) {
        StringBuilder buffer = new StringBuilder(DEFAULT.getStringBufferSize());
        DEFAULT.appendArray(buffer, array);
        return buffer.toString();
    }
    /* ------------------------------------------------------------ */
    /**
     * @param s String containing JSON object or array.
     * @return A Map, Object array or primitive array parsed from the JSON.
     */
    public static Object parse(String s) {
        return DEFAULT.parse(new StringSource(s), false);
    }
    /* ------------------------------------------------------------ */
    /**
     * @param s String containing JSON object or array.
     * @param stripOuterComment If true, an outer comment around the JSON is ignored.
     * @return A Map, Object array or primitive array parsed from the JSON.
     */
    public static Object parse(String s, boolean stripOuterComment) {
        return DEFAULT.parse(new StringSource(s), stripOuterComment);
    }
    /* ------------------------------------------------------------ */
    /**
     * @param in Reader containing JSON object or array.
     * @return A Map, Object array or primitive array parsed from the JSON.
     */
    public static Object parse(Reader in) throws IOException {
        return DEFAULT.parse(new ReaderSource(in), false);
    }
    /* ------------------------------------------------------------ */
    /**
     * @param in Reader containing JSON object or array.
     * @param stripOuterComment If true, an outer comment around the JSON is ignored.
     * @return A Map, Object array or primitive array parsed from the JSON.
     */
    public static Object parse(Reader in, boolean stripOuterComment) throws IOException {
        return DEFAULT.parse(new ReaderSource(in), stripOuterComment);
    }
    /* ------------------------------------------------------------ */
    /**
     * Convert Object to JSON
     * @param object The object to convert
     * @return The JSON String
     */
    public String toJSON(Object object) {
        StringBuilder buffer = new StringBuilder(getStringBufferSize());
        append(buffer, object);
        return buffer.toString();
    }
    /* ------------------------------------------------------------ */
    /**
     * Convert JSON to Object
     * @param json The json to convert
     * @return The object
     */
    public Object fromJSON(String json) {
        Source source = new StringSource(json);
        return parse(source);
    }
    /* ------------------------------------------------------------ */
    /**
     * Append object as JSON to string buffer.
     * @param buffer the buffer to append to
     * @param object the object to append
     */
    public void append(Appendable buffer, Object object) {
        try {
            if (object == null)
                buffer.append("null");
            else if (object instanceof Convertible)
                appendJSON(buffer, (Convertible) object);
            else if (object instanceof Generator)
                appendJSON(buffer, (Generator) object);
            else if (object instanceof Map)
                appendMap(buffer, (Map) object);
            else if (object instanceof Collection)
                appendArray(buffer, (Collection) object);
            else if (object.getClass().isArray())
                appendArray(buffer, object);
            else if (object instanceof Number)
                appendNumber(buffer, (Number) object);
            else if (object instanceof Boolean)
                appendBoolean(buffer, (Boolean) object);
            else if (object instanceof Character)
                appendString(buffer, object.toString());
            else if (object instanceof String)
                appendString(buffer, (String) object);
            else {
                Convertor convertor = getConvertor(object.getClass());
                if (convertor != null)
                    appendJSON(buffer, convertor, object);
                else
                    appendString(buffer, object.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /* ------------------------------------------------------------ */
    public void appendNull(Appendable buffer) {
        try {
            buffer.append("null");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /* ------------------------------------------------------------ */
    public void appendJSON(final Appendable buffer, final Convertor convertor, final Object object) {
        appendJSON(buffer, new Convertible() {
            public void fromJSON(Map object) {
            }
            public void toJSON(Output out) {
                convertor.toJSON(object, out);
            }
        });
    }
    /* ------------------------------------------------------------ */
    public void appendJSON(final Appendable buffer, Convertible converter) {
        ConvertableOutput out = new ConvertableOutput(buffer);
        converter.toJSON(out);
        out.complete();
    }
    /* ------------------------------------------------------------ */
    public void appendJSON(Appendable buffer, Generator generator) {
        generator.addJSON(buffer);
    }
    /* ------------------------------------------------------------ */
    public void appendMap(Appendable buffer, Map<?, ?> map) {
        try {
            if (map == null) {
                appendNull(buffer);
                return;
            }
            buffer.append('{');
            Iterator<?> iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
                StringUtils.quote(buffer, entry.getKey().toString());
                buffer.append(':');
                append(buffer, entry.getValue());
                if (iter.hasNext())
                    buffer.append(',');
            }
            buffer.append('}');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /* ------------------------------------------------------------ */
    public void appendArray(Appendable buffer, Collection collection) {
        try {
            if (collection == null) {
                appendNull(buffer);
                return;
            }
            buffer.append('[');
            Iterator iter = collection.iterator();
            boolean first = true;
            while (iter.hasNext()) {
                if (!first)
                    buffer.append(',');
                first = false;
                append(buffer, iter.next());
            }
            buffer.append(']');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /* ------------------------------------------------------------ */
    public void appendArray(Appendable buffer, Object array) {
        try {
            if (array == null) {
                appendNull(buffer);
                return;
            }
            buffer.append('[');
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                if (i != 0)
                    buffer.append(',');
                append(buffer, Array.get(array, i));
            }
            buffer.append(']');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /* ------------------------------------------------------------ */
    public void appendBoolean(Appendable buffer, Boolean b) {
        try {
            if (b == null) {
                appendNull(buffer);
                return;
            }
            buffer.append(b.booleanValue() ? "true" : "false");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /* ------------------------------------------------------------ */
    public void appendNumber(Appendable buffer, Number number) {
        try {
            if (number == null) {
                appendNull(buffer);
                return;
            }
            buffer.append(String.valueOf(number));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /* ------------------------------------------------------------ */
    public void appendString(Appendable buffer, String string) {
        if (string == null) {
            appendNull(buffer);
            return;
        }
        StringUtils.quote(buffer, string);
    }
    // Parsing utilities
    /* ------------------------------------------------------------ */
    protected String toString(char[] buffer, int offset, int length) {
        return new String(buffer, offset, length);
    }
    /* ------------------------------------------------------------ */
    protected Map<String, Object> newMap() {
        return new HashMap<String, Object>();
    }
    /* ------------------------------------------------------------ */
    protected Object[] newArray(int size) {
        return new Object[size];
    }
    /* ------------------------------------------------------------ */
    protected JSON contextForArray() {
        return this;
    }
    /* ------------------------------------------------------------ */
    protected JSON contextFor(String field) {
        return this;
    }
    /* ------------------------------------------------------------ */
    protected Object convertTo(Class type, Map map) {
        if (type == null) {
            throw new NullPointerException("type is null.");
        }
        if (type != null && Convertible.class.isAssignableFrom(type)) {
            try {
                Convertible conv = (Convertible) type.newInstance();
                conv.fromJSON(map);
                return conv;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Convertor convertor = getConvertor(type);
        if (convertor != null) {
            return convertor.fromJSON(map);
        }
        return map;
    }
    /* ------------------------------------------------------------ */
    /**
     * Register a {@link Convertor} for a class or interface.
     *
     * @param forClass The class or interface that the convertor applies to
     * @param convertor the convertor
     */
    public void addConvertor(Class forClass, Convertor convertor) {
        _convertors.put(forClass.getName(), convertor);
    }
    /* ------------------------------------------------------------ */
    /**
     * Lookup a convertor for a class.
     * <p>
     * If no match is found for the class, then the interfaces for the class are
     * tried. If still no match is found, then the super class and it's
     * interfaces are tried recursively.
     *
     * @param forClass The class
     * @return a {@link net.hasor.utils.json.JSON.Convertor} or null if none were found.
     */
    protected Convertor getConvertor(Class forClass) {
        if (forClass == null) {
            return null;
        }
        Class cls = forClass;
        Convertor convertor = _convertors.get(cls.getName());
        if (convertor == null && this != DEFAULT)
            convertor = DEFAULT.getConvertor(cls);
        while (convertor == null && cls != null && cls != Object.class) {
            Class[] ifs = cls.getInterfaces();
            int i = 0;
            while (convertor == null && ifs != null && i < ifs.length)
                convertor = _convertors.get(ifs[i++].getName());
            if (convertor == null) {
                cls = cls.getSuperclass();
                convertor = _convertors.get(cls.getName());
            }
        }
        if (convertor == null)
            return new JSONPojoConvertor(forClass);
        return convertor;
    }
    /* ------------------------------------------------------------ */
    /**
     * Register a {@link net.hasor.utils.json.JSON.Convertor} for a named class or interface.
     *
     * @param name name of a class or an interface that the convertor applies to
     * @param convertor the convertor
     */
    public void addConvertorFor(String name, Convertor convertor) {
        _convertors.put(name, convertor);
    }
    /* ------------------------------------------------------------ */
    /**
     * Lookup a convertor for a named class.
     *
     * @param name
     *            name of the class
     * @return a {@link net.hasor.utils.json.JSON.Convertor} or null if none were found.
     */
    public Convertor getConvertorFor(String name) {
        String clsName = name;
        Convertor convertor = _convertors.get(clsName);
        if (convertor == null && this != DEFAULT)
            convertor = DEFAULT.getConvertorFor(clsName);
        return convertor;
    }
    /* ------------------------------------------------------------ */
    public Object parse(Source source, boolean stripOuterComment) {
        int comment_state = 0; // 0=no comment, 1="/", 2="/*", 3="/* *" -1="//"
        if (!stripOuterComment)
            return parse(source);
        int strip_state = 1; // 0=no strip, 1=wait for /*, 2= wait for */
        Object o = null;
        while (source.hasNext()) {
            char c = source.peek();
            // handle // or /* comment
            if (comment_state == 1) {
                switch (c) {
                case '/':
                    comment_state = -1;
                    break;
                case '*':
                    comment_state = 2;
                    if (strip_state == 1) {
                        comment_state = 0;
                        strip_state = 2;
                    }
                }
            }
            // handle /* */ comment
            else if (comment_state > 1) {
                switch (c) {
                case '*':
                    comment_state = 3;
                    break;
                case '/':
                    if (comment_state == 3) {
                        comment_state = 0;
                        if (strip_state == 2)
                            return o;
                    } else
                        comment_state = 2;
                    break;
                default:
                    comment_state = 2;
                }
            }
            // handle // comment
            else if (comment_state < 0) {
                switch (c) {
                case '\r':
                case '\n':
                    comment_state = 0;
                default:
                    break;
                }
            }
            // handle unknown
            else {
                if (!Character.isWhitespace(c)) {
                    if (c == '/')
                        comment_state = 1;
                    else if (c == '*')
                        comment_state = 3;
                    else if (o == null) {
                        o = parse(source);
                        continue;
                    }
                }
            }
            source.next();
        }
        return o;
    }
    /* ------------------------------------------------------------ */
    public Object parse(Source source) {
        int comment_state = 0; // 0=no comment, 1="/", 2="/*", 3="/* *" -1="//"
        while (source.hasNext()) {
            char c = source.peek();
            // handle // or /* comment
            if (comment_state == 1) {
                switch (c) {
                case '/':
                    comment_state = -1;
                    break;
                case '*':
                    comment_state = 2;
                }
            }
            // handle /* */ comment
            else if (comment_state > 1) {
                switch (c) {
                case '*':
                    comment_state = 3;
                    break;
                case '/':
                    if (comment_state == 3)
                        comment_state = 0;
                    else
                        comment_state = 2;
                    break;
                default:
                    comment_state = 2;
                }
            }
            // handle // comment
            else if (comment_state < 0) {
                switch (c) {
                case '\r':
                case '\n':
                    comment_state = 0;
                    break;
                default:
                    break;
                }
            }
            // handle unknown
            else {
                switch (c) {
                case '{':
                    return parseObject(source);
                case '[':
                    return parseArray(source);
                case '"':
                    return parseString(source);
                case '-':
                    return parseNumber(source);
                case 'n':
                    complete("null", source);
                    return null;
                case 't':
                    complete("true", source);
                    return Boolean.TRUE;
                case 'f':
                    complete("false", source);
                    return Boolean.FALSE;
                case 'u':
                    complete("undefined", source);
                    return null;
                case 'N':
                    complete("NaN", source);
                    return null;
                case '/':
                    comment_state = 1;
                    break;
                default:
                    if (Character.isDigit(c))
                        return parseNumber(source);
                    else if (Character.isWhitespace(c))
                        break;
                    return handleUnknown(source, c);
                }
            }
            source.next();
        }
        return null;
    }
    /* ------------------------------------------------------------ */
    protected Object handleUnknown(Source source, char c) {
        throw new IllegalStateException("unknown char '" + c + "'(" + (int) c + ") in " + source);
    }
    /* ------------------------------------------------------------ */
    protected Object parseObject(Source source) {
        if (source.next() != '{')
            throw new IllegalStateException();
        Map<String, Object> map = newMap();
        char next = seekTo("\"}", source);
        while (source.hasNext()) {
            if (next == '}') {
                source.next();
                break;
            }
            String name = parseString(source);
            seekTo(':', source);
            source.next();
            Object value = contextFor(name).parse(source);
            map.put(name, value);
            seekTo(",}", source);
            next = source.next();
            if (next == '}')
                break;
            else
                next = seekTo("\"}", source);
        }
        String classname = (String) map.get("class");
        if (classname != null) {
            try {
                Class c = Loader.loadClass(JSON.class, classname);
                return convertTo(c, map);
            } catch (ClassNotFoundException e) {
                logger.warning(e.getMessage());
            }
        }
        return map;
    }
    /* ------------------------------------------------------------ */
    protected Object parseArray(Source source) {
        if (source.next() != '[')
            throw new IllegalStateException();
        int size = 0;
        ArrayList list = null;
        Object item = null;
        boolean coma = true;
        while (source.hasNext()) {
            char c = source.peek();
            switch (c) {
            case ']':
                source.next();
                switch (size) {
                case 0:
                    return newArray(0);
                case 1:
                    Object array = newArray(1);
                    Array.set(array, 0, item);
                    return array;
                default:
                    return list.toArray(newArray(list.size()));
                }
            case ',':
                if (coma)
                    throw new IllegalStateException();
                coma = true;
                source.next();
                break;
            default:
                if (Character.isWhitespace(c))
                    source.next();
                else {
                    coma = false;
                    if (size++ == 0)
                        item = contextForArray().parse(source);
                    else if (list == null) {
                        list = new ArrayList();
                        list.add(item);
                        item = contextForArray().parse(source);
                        list.add(item);
                        item = null;
                    } else {
                        item = contextForArray().parse(source);
                        list.add(item);
                        item = null;
                    }
                }
            }
        }
        throw new IllegalStateException("unexpected end of array");
    }
    /* ------------------------------------------------------------ */
    protected String parseString(Source source) {
        if (source.next() != '"')
            throw new IllegalStateException();
        boolean escape = false;
        StringBuilder b = null;
        final char[] scratch = source.scratchBuffer();
        if (scratch != null) {
            int i = 0;
            while (source.hasNext()) {
                if (i >= scratch.length) {
                    // we have filled the scratch buffer, so we must
                    // use the StringBuffer for a large string
                    b = new StringBuilder(scratch.length * 2);
                    b.append(scratch, 0, i);
                    break;
                }
                char c = source.next();
                if (escape) {
                    escape = false;
                    switch (c) {
                    case '"':
                        scratch[i++] = '"';
                        break;
                    case '\\':
                        scratch[i++] = '\\';
                        break;
                    case '/':
                        scratch[i++] = '/';
                        break;
                    case 'b':
                        scratch[i++] = '\b';
                        break;
                    case 'f':
                        scratch[i++] = '\f';
                        break;
                    case 'n':
                        scratch[i++] = '\n';
                        break;
                    case 'r':
                        scratch[i++] = '\r';
                        break;
                    case 't':
                        scratch[i++] = '\t';
                        break;
                    case 'u':
                        char uc = (char) ((TypeUtil.convertHexDigit((byte) source.next()) << 12) + (TypeUtil.convertHexDigit((byte) source.next()) << 8) + (TypeUtil.convertHexDigit((byte) source.next()) << 4) + (TypeUtil.convertHexDigit((byte) source.next())));
                        scratch[i++] = uc;
                        break;
                    default:
                        scratch[i++] = c;
                    }
                } else if (c == '\\') {
                    escape = true;
                    continue;
                } else if (c == '\"') {
                    // Return string that fits within scratch buffer
                    return toString(scratch, 0, i);
                } else
                    scratch[i++] = c;
            }
            // Missing end quote, but return string anyway ?
            if (b == null)
                return toString(scratch, 0, i);
        } else
            b = new StringBuilder(getStringBufferSize());
        // parse large string into string buffer
        final StringBuilder builder = b;
        while (source.hasNext()) {
            char c = source.next();
            if (escape) {
                escape = false;
                switch (c) {
                case '"':
                    builder.append('"');
                    break;
                case '\\':
                    builder.append('\\');
                    break;
                case '/':
                    builder.append('/');
                    break;
                case 'b':
                    builder.append('\b');
                    break;
                case 'f':
                    builder.append('\f');
                    break;
                case 'n':
                    builder.append('\n');
                    break;
                case 'r':
                    builder.append('\r');
                    break;
                case 't':
                    builder.append('\t');
                    break;
                case 'u':
                    char uc = (char) ((TypeUtil.convertHexDigit((byte) source.next()) << 12) + (TypeUtil.convertHexDigit((byte) source.next()) << 8) + (TypeUtil.convertHexDigit((byte) source.next()) << 4) + (TypeUtil.convertHexDigit((byte) source.next())));
                    builder.append(uc);
                    break;
                default:
                    builder.append(c);
                }
            } else if (c == '\\') {
                escape = true;
                continue;
            } else if (c == '\"')
                break;
            else
                builder.append(c);
        }
        return builder.toString();
    }
    /* ------------------------------------------------------------ */
    public Number parseNumber(Source source) {
        boolean minus = false;
        long number = 0;
        StringBuilder buffer = null;
        longLoop:
        while (source.hasNext()) {
            char c = source.peek();
            switch (c) {
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
                number = number * 10 + (c - '0');
                source.next();
                break;
            case '-':
            case '+':
                if (number != 0)
                    throw new IllegalStateException("bad number");
                minus = true;
                source.next();
                break;
            case '.':
            case 'e':
            case 'E':
                buffer = new StringBuilder(16);
                if (minus)
                    buffer.append('-');
                buffer.append(number);
                buffer.append(c);
                source.next();
                break longLoop;
            default:
                break longLoop;
            }
        }
        if (buffer == null)
            return minus ? -1 * number : number;
        doubleLoop:
        while (source.hasNext()) {
            char c = source.peek();
            switch (c) {
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
            case '-':
            case '.':
            case '+':
            case 'e':
            case 'E':
                buffer.append(c);
                source.next();
                break;
            default:
                break doubleLoop;
            }
        }
        return new Double(buffer.toString());
    }
    /* ------------------------------------------------------------ */
    protected void seekTo(char seek, Source source) {
        while (source.hasNext()) {
            char c = source.peek();
            if (c == seek)
                return;
            if (!Character.isWhitespace(c))
                throw new IllegalStateException("Unexpected '" + c + " while seeking '" + seek + "'");
            source.next();
        }
        throw new IllegalStateException("Expected '" + seek + "'");
    }
    /* ------------------------------------------------------------ */
    protected char seekTo(String seek, Source source) {
        while (source.hasNext()) {
            char c = source.peek();
            if (seek.indexOf(c) >= 0) {
                return c;
            }
            if (!Character.isWhitespace(c))
                throw new IllegalStateException("Unexpected '" + c + "' while seeking one of '" + seek + "'");
            source.next();
        }
        throw new IllegalStateException("Expected one of '" + seek + "'");
    }
    /* ------------------------------------------------------------ */
    protected static void complete(String seek, Source source) {
        int i = 0;
        while (source.hasNext() && i < seek.length()) {
            char c = source.next();
            if (c != seek.charAt(i++))
                throw new IllegalStateException("Unexpected '" + c + " while seeking  \"" + seek + "\"");
        }
        if (i < seek.length())
            throw new IllegalStateException("Expected \"" + seek + "\"");
    }
    private final class ConvertableOutput implements Output {
        private final Appendable _buffer;
        char c = '{';
        private ConvertableOutput(Appendable buffer) {
            _buffer = buffer;
        }
        public void complete() {
            try {
                if (c == '{')
                    _buffer.append("{}");
                else if (c != 0)
                    _buffer.append("}");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public void add(Object obj) {
            if (c == 0)
                throw new IllegalStateException();
            append(_buffer, obj);
            c = 0;
        }
        public void addClass(Class type) {
            try {
                if (c == 0)
                    throw new IllegalStateException();
                _buffer.append(c);
                _buffer.append("\"class\":");
                append(_buffer, type.getName());
                c = ',';
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public void add(String name, Object value) {
            try {
                if (c == 0)
                    throw new IllegalStateException();
                _buffer.append(c);
                StringUtils.quote(_buffer, name);
                _buffer.append(':');
                append(_buffer, value);
                c = ',';
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public void add(String name, double value) {
            try {
                if (c == 0)
                    throw new IllegalStateException();
                _buffer.append(c);
                StringUtils.quote(_buffer, name);
                _buffer.append(':');
                appendNumber(_buffer, new Double(value));
                c = ',';
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public void add(String name, long value) {
            try {
                if (c == 0)
                    throw new IllegalStateException();
                _buffer.append(c);
                StringUtils.quote(_buffer, name);
                _buffer.append(':');
                appendNumber(_buffer, value);
                c = ',';
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public void add(String name, boolean value) {
            try {
                if (c == 0)
                    throw new IllegalStateException();
                _buffer.append(c);
                StringUtils.quote(_buffer, name);
                _buffer.append(':');
                appendBoolean(_buffer, value ? Boolean.TRUE : Boolean.FALSE);
                c = ',';
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /* ------------------------------------------------------------ */
    public interface Source {
        boolean hasNext();

        char next();

        char peek();

        char[] scratchBuffer();
    }
    /* ------------------------------------------------------------ */
    public static class StringSource implements Source {
        private final String string;
        private       int    index;
        private       char[] scratch;
        public StringSource(String s) {
            string = s;
        }
        public boolean hasNext() {
            if (index < string.length())
                return true;
            scratch = null;
            return false;
        }
        public char next() {
            return string.charAt(index++);
        }
        public char peek() {
            return string.charAt(index);
        }
        public String toString() {
            return string.substring(0, index) + "|||" + string.substring(index);
        }
        public char[] scratchBuffer() {
            if (scratch == null)
                scratch = new char[string.length()];
            return scratch;
        }
    }
    /* ------------------------------------------------------------ */
    public static class ReaderSource implements Source {
        private Reader _reader;
        private int    _next = -1;
        private char[] scratch;
        public ReaderSource(Reader r) {
            _reader = r;
        }
        public void setReader(Reader reader) {
            _reader = reader;
            _next = -1;
        }
        public boolean hasNext() {
            getNext();
            if (_next < 0) {
                scratch = null;
                return false;
            }
            return true;
        }
        public char next() {
            getNext();
            char c = (char) _next;
            _next = -1;
            return c;
        }
        public char peek() {
            getNext();
            return (char) _next;
        }
        private void getNext() {
            if (_next < 0) {
                try {
                    _next = _reader.read();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        public char[] scratchBuffer() {
            if (scratch == null)
                scratch = new char[1024];
            return scratch;
        }
    }
    /* ------------------------------------------------------------ */
    /**
     * JSON Output class for use by {@link Convertible}.
     */
    public interface Output {
        public void addClass(Class c);

        public void add(Object obj);

        public void add(String name, Object value);

        public void add(String name, double value);

        public void add(String name, long value);

        public void add(String name, boolean value);
    }
    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /**
     * JSON Convertible object. Object can implement this interface in a similar
     * way to the {@link Externalizable} interface is used to allow classes to
     * provide their own serialization mechanism.
     * <p>
     * A JSON.Convertible object may be written to a JSONObject or initialized
     * from a Map of field names to values.
     * <p>
     * If the JSON is to be convertible back to an Object, then the method
     * {@link Output#addClass(Class)} must be called from within toJSON()
     *
     */
    public interface Convertible {
        public void toJSON(Output out);

        public void fromJSON(Map object);
    }
    /* ------------------------------------------------------------ */
    /**
     * Static JSON Convertor.
     * <p>
     * may be implemented to provide static convertors for objects that may be
     * registered with
     * {@link net.hasor.utils.json.JSON#registerConvertor(Class, net.hasor.utils.json.JSON.Convertor)}
     * . These convertors are looked up by class, interface and super class by
     * {@link net.hasor.utils.json.JSON#getConvertor(Class)}. Convertors should be used when the
     * classes to be converted cannot implement {@link Convertible} or
     * {@link Generator}.
     */
    public interface Convertor {
        public void toJSON(Object obj, Output out);

        public Object fromJSON(Map object);
    }
    /* ------------------------------------------------------------ */
    /**
     * JSON Generator. A class that can add it's JSON representation directly to
     * a StringBuffer. This is useful for object instances that are frequently
     * converted and wish to avoid multiple Conversions
     */
    public interface Generator {
        public void addJSON(Appendable buffer);
    }
    /* ------------------------------------------------------------ */
    /**
     * A Literal JSON generator A utility instance of {@link net.hasor.utils.json.JSON.Generator}
     * that holds a pre-generated string on JSON text.
     */
    public static class Literal implements Generator {
        private String _json;
        /* ------------------------------------------------------------ */
        /**
         * Construct a literal JSON instance for use by {@link JSON#toString(Object)}.
         * @param json A literal JSON string.
         */
        public Literal(String json) {
            if (logger.isLoggable(Level.FINE)) // TODO: Make this a configurable option on JSON instead!
                parse(json);
            _json = json;
        }
        public String toString() {
            return _json;
        }
        public void addJSON(Appendable buffer) {
            try {
                buffer.append(_json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
