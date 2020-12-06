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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
/* ------------------------------------------------------------ */
/**
 * TYPE Utilities.
 * Provides various static utiltiy methods for manipulating types and their
 * string representations.
 *
 * @since Jetty 4.1
 */
class TypeUtil {
    protected final static Logger                    logger     = Logger.getLogger(TypeUtil.class.getName());
    public static          int                       CR         = '\015';
    public static          int                       LF         = '\012';
    /* ------------------------------------------------------------ */
    private static final   HashMap<String, Class<?>> name2Class = new HashMap<String, Class<?>>();

    static {
        name2Class.put("boolean", Boolean.TYPE);
        name2Class.put("byte", Byte.TYPE);
        name2Class.put("char", Character.TYPE);
        name2Class.put("double", Double.TYPE);
        name2Class.put("float", Float.TYPE);
        name2Class.put("int", Integer.TYPE);
        name2Class.put("long", Long.TYPE);
        name2Class.put("short", Short.TYPE);
        name2Class.put("void", Void.TYPE);
        name2Class.put("java.lang.Boolean.TYPE", Boolean.TYPE);
        name2Class.put("java.lang.Byte.TYPE", Byte.TYPE);
        name2Class.put("java.lang.Character.TYPE", Character.TYPE);
        name2Class.put("java.lang.Double.TYPE", Double.TYPE);
        name2Class.put("java.lang.Float.TYPE", Float.TYPE);
        name2Class.put("java.lang.Integer.TYPE", Integer.TYPE);
        name2Class.put("java.lang.Long.TYPE", Long.TYPE);
        name2Class.put("java.lang.Short.TYPE", Short.TYPE);
        name2Class.put("java.lang.Void.TYPE", Void.TYPE);
        name2Class.put("java.lang.Boolean", Boolean.class);
        name2Class.put("java.lang.Byte", Byte.class);
        name2Class.put("java.lang.Character", Character.class);
        name2Class.put("java.lang.Double", Double.class);
        name2Class.put("java.lang.Float", Float.class);
        name2Class.put("java.lang.Integer", Integer.class);
        name2Class.put("java.lang.Long", Long.class);
        name2Class.put("java.lang.Short", Short.class);
        name2Class.put("Boolean", Boolean.class);
        name2Class.put("Byte", Byte.class);
        name2Class.put("Character", Character.class);
        name2Class.put("Double", Double.class);
        name2Class.put("Float", Float.class);
        name2Class.put("Integer", Integer.class);
        name2Class.put("Long", Long.class);
        name2Class.put("Short", Short.class);
        name2Class.put(null, Void.TYPE);
        name2Class.put("string", String.class);
        name2Class.put("String", String.class);
        name2Class.put("java.lang.String", String.class);
    }

    /* ------------------------------------------------------------ */
    private static final HashMap<Class<?>, String> class2Name = new HashMap<Class<?>, String>();

    static {
        class2Name.put(Boolean.TYPE, "boolean");
        class2Name.put(Byte.TYPE, "byte");
        class2Name.put(Character.TYPE, "char");
        class2Name.put(Double.TYPE, "double");
        class2Name.put(Float.TYPE, "float");
        class2Name.put(Integer.TYPE, "int");
        class2Name.put(Long.TYPE, "long");
        class2Name.put(Short.TYPE, "short");
        class2Name.put(Void.TYPE, "void");
        class2Name.put(Boolean.class, "java.lang.Boolean");
        class2Name.put(Byte.class, "java.lang.Byte");
        class2Name.put(Character.class, "java.lang.Character");
        class2Name.put(Double.class, "java.lang.Double");
        class2Name.put(Float.class, "java.lang.Float");
        class2Name.put(Integer.class, "java.lang.Integer");
        class2Name.put(Long.class, "java.lang.Long");
        class2Name.put(Short.class, "java.lang.Short");
        class2Name.put(null, "void");
        class2Name.put(String.class, "java.lang.String");
    }

    /* ------------------------------------------------------------ */
    private static final HashMap<Class<?>, Method> class2Value = new HashMap<Class<?>, Method>();

    static {
        try {
            Class<?>[] s = { String.class };
            class2Value.put(Boolean.TYPE, Boolean.class.getMethod("valueOf", s));
            class2Value.put(Byte.TYPE, Byte.class.getMethod("valueOf", s));
            class2Value.put(Double.TYPE, Double.class.getMethod("valueOf", s));
            class2Value.put(Float.TYPE, Float.class.getMethod("valueOf", s));
            class2Value.put(Integer.TYPE, Integer.class.getMethod("valueOf", s));
            class2Value.put(Long.TYPE, Long.class.getMethod("valueOf", s));
            class2Value.put(Short.TYPE, Short.class.getMethod("valueOf", s));
            class2Value.put(Boolean.class, Boolean.class.getMethod("valueOf", s));
            class2Value.put(Byte.class, Byte.class.getMethod("valueOf", s));
            class2Value.put(Double.class, Double.class.getMethod("valueOf", s));
            class2Value.put(Float.class, Float.class.getMethod("valueOf", s));
            class2Value.put(Integer.class, Integer.class.getMethod("valueOf", s));
            class2Value.put(Long.class, Long.class.getMethod("valueOf", s));
            class2Value.put(Short.class, Short.class.getMethod("valueOf", s));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    /* ------------------------------------------------------------ */
    /** Array to List.
     * <p>
     * Works like {@link Arrays#asList(Object...)}, but handles null arrays.
     * @return a list backed by the array.
     */
    public static <T> List<T> asList(T[] a) {
        if (a == null)
            return Collections.emptyList();
        return Arrays.asList(a);
    }
    /* ------------------------------------------------------------ */
    /** Class from a canonical name for a type.
     * @param name A class or type name.
     * @return A class , which may be a primitive TYPE field..
     */
    public static Class<?> fromName(String name) {
        return name2Class.get(name);
    }
    /* ------------------------------------------------------------ */
    /** Canonical name for a type.
     * @param type A class , which may be a primitive TYPE field.
     * @return Canonical name.
     */
    public static String toName(Class<?> type) {
        return class2Name.get(type);
    }
    /* ------------------------------------------------------------ */
    /** Convert String value to instance.
     * @param type The class of the instance, which may be a primitive TYPE field.
     * @param value The value as a string.
     * @return The value as an Object.
     */
    public static Object valueOf(Class<?> type, String value) {
        try {
            if (type.equals(String.class))
                return value;
            Method m = class2Value.get(type);
            if (m != null)
                return m.invoke(null, value);
            if (type.equals(Character.TYPE) || type.equals(Character.class))
                return new Character(value.charAt(0));
            Constructor<?> c = type.getConstructor(String.class);
            return c.newInstance(value);
        } catch (NoSuchMethodException e) {
            // LogSupport.ignore(log,e);
        } catch (IllegalAccessException e) {
            // LogSupport.ignore(log,e);
        } catch (InstantiationException e) {
            // LogSupport.ignore(log,e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof Error)
                throw (Error) (e.getTargetException());
            // LogSupport.ignore(log,e);
        }
        return null;
    }
    /* ------------------------------------------------------------ */
    /** Convert String value to instance.
     * @param type classname or type (eg int)
     * @param value The value as a string.
     * @return The value as an Object.
     */
    public static Object valueOf(String type, String value) {
        return valueOf(fromName(type), value);
    }
    /* ------------------------------------------------------------ */
    /** Parse an int from a substring.
     * Negative numbers are not handled.
     * @param s String
     * @param offset Offset within string
     * @param length Length of integer or -1 for remainder of string
     * @param base base of the integer
     * @return the parsed integer
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static int parseInt(String s, int offset, int length, int base) throws NumberFormatException {
        int value = 0;
        if (length < 0)
            length = s.length() - offset;
        for (int i = 0; i < length; i++) {
            char c = s.charAt(offset + i);
            int digit = c - '0';
            if (digit < 0 || digit >= base || digit >= 10) {
                digit = 10 + c - 'A';
                if (digit < 10 || digit >= base)
                    digit = 10 + c - 'a';
            }
            if (digit < 0 || digit >= base)
                throw new NumberFormatException(s.substring(offset, offset + length));
            value = value * base + digit;
        }
        return value;
    }
    /* ------------------------------------------------------------ */
    /** Parse an int from a byte array of ascii characters.
     * Negative numbers are not handled.
     * @param b byte array
     * @param offset Offset within string
     * @param length Length of integer or -1 for remainder of string
     * @param base base of the integer
     * @return the parsed integer
     * @throws NumberFormatException if the array cannot be parsed into an integer
     */
    public static int parseInt(byte[] b, int offset, int length, int base) throws NumberFormatException {
        int value = 0;
        if (length < 0)
            length = b.length - offset;
        for (int i = 0; i < length; i++) {
            char c = (char) (0xff & b[offset + i]);
            int digit = c - '0';
            if (digit < 0 || digit >= base || digit >= 10) {
                digit = 10 + c - 'A';
                if (digit < 10 || digit >= base)
                    digit = 10 + c - 'a';
            }
            if (digit < 0 || digit >= base)
                throw new NumberFormatException(new String(b, offset, length));
            value = value * base + digit;
        }
        return value;
    }
    /* ------------------------------------------------------------ */
    public static byte[] parseBytes(String s, int base) {
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2)
            bytes[i / 2] = (byte) TypeUtil.parseInt(s, i, 2, base);
        return bytes;
    }
    /* ------------------------------------------------------------ */
    public static String toString(byte[] bytes, int base) {
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            int bi = 0xff & b;
            int c = '0' + (bi / base) % base;
            if (c > '9')
                c = 'a' + (c - '0' - 10);
            buf.append((char) c);
            c = '0' + bi % base;
            if (c > '9')
                c = 'a' + (c - '0' - 10);
            buf.append((char) c);
        }
        return buf.toString();
    }
    /* ------------------------------------------------------------ */
    /**
     * @param b An ASCII encoded character 0-9 a-f A-F
     * @return The byte value of the character 0-16.
     */
    public static byte convertHexDigit(byte b) {
        if ((b >= '0') && (b <= '9'))
            return (byte) (b - '0');
        if ((b >= 'a') && (b <= 'f'))
            return (byte) (b - 'a' + 10);
        if ((b >= 'A') && (b <= 'F'))
            return (byte) (b - 'A' + 10);
        throw new IllegalArgumentException("!hex:" + Integer.toHexString(0xff & b));
    }
    /* ------------------------------------------------------------ */
    public static void toHex(byte b, Appendable buf) {
        try {
            int bi = 0xff & b;
            int c = '0' + (bi / 16) % 16;
            if (c > '9')
                c = 'A' + (c - '0' - 10);
            buf.append((char) c);
            c = '0' + bi % 16;
            if (c > '9')
                c = 'A' + (c - '0' - 10);
            buf.append((char) c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /* ------------------------------------------------------------ */
    public static String toHexString(byte b) {
        return toHexString(new byte[] { b }, 0, 1);
    }
    /* ------------------------------------------------------------ */
    public static String toHexString(byte[] b) {
        return toHexString(b, 0, b.length);
    }
    /* ------------------------------------------------------------ */
    public static String toHexString(byte[] b, int offset, int length) {
        StringBuilder buf = new StringBuilder();
        for (int i = offset; i < offset + length; i++) {
            int bi = 0xff & b[i];
            int c = '0' + (bi / 16) % 16;
            if (c > '9')
                c = 'A' + (c - '0' - 10);
            buf.append((char) c);
            c = '0' + bi % 16;
            if (c > '9')
                c = 'a' + (c - '0' - 10);
            buf.append((char) c);
        }
        return buf.toString();
    }
    /* ------------------------------------------------------------ */
    public static byte[] fromHexString(String s) {
        if (s.length() % 2 != 0)
            throw new IllegalArgumentException(s);
        byte[] array = new byte[s.length() / 2];
        for (int i = 0; i < array.length; i++) {
            int b = Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
            array[i] = (byte) (0xff & b);
        }
        return array;
    }
    public static void dump(Class<?> c) {
        System.err.println("Dump: " + c);
        dump(c.getClassLoader());
    }
    public static void dump(ClassLoader cl) {
        System.err.println("Dump Loaders:");
        while (cl != null) {
            System.err.println("  loader " + cl);
            cl = cl.getParent();
        }
    }
    /* ------------------------------------------------------------ */
    public static byte[] readLine(InputStream in) throws IOException {
        byte[] buf = new byte[256];
        int i = 0;
        int loops = 0;
        int ch = 0;
        while (true) {
            ch = in.read();
            if (ch < 0)
                break;
            loops++;
            // skip a leading LF's
            if (loops == 1 && ch == LF)
                continue;
            if (ch == CR || ch == LF)
                break;
            if (i >= buf.length) {
                byte[] old_buf = buf;
                buf = new byte[old_buf.length + 256];
                System.arraycopy(old_buf, 0, buf, 0, old_buf.length);
            }
            buf[i++] = (byte) ch;
        }
        if (ch == -1 && i == 0)
            return null;
        // skip a trailing LF if it exists
        if (ch == CR && in.available() >= 1 && in.markSupported()) {
            in.mark(1);
            ch = in.read();
            if (ch != LF)
                in.reset();
        }
        byte[] old_buf = buf;
        buf = new byte[i];
        System.arraycopy(old_buf, 0, buf, 0, i);
        return buf;
    }
    public static URL jarFor(String className) {
        try {
            className = className.replace('.', '/') + ".class";
            // hack to discover jstl libraries
            URL url = Loader.getResource(null, className, false);
            String s = url.toString();
            if (s.startsWith("jar:file:"))
                return new URL(s.substring(4, s.indexOf("!/")));
        } catch (Exception e) {
            logger.fine(e.getMessage());
        }
        return null;
    }
    public static Object call(Class<?> oClass, String method, Object obj, Object[] arg) throws InvocationTargetException, NoSuchMethodException {
        // Lets just try all methods for now
        Method[] methods = oClass.getMethods();
        for (int c = 0; methods != null && c < methods.length; c++) {
            if (!methods[c].getName().equals(method))
                continue;
            if (methods[c].getParameterTypes().length != arg.length)
                continue;
            if (Modifier.isStatic(methods[c].getModifiers()) != (obj == null))
                continue;
            if ((obj == null) && methods[c].getDeclaringClass() != oClass)
                continue;
            try {
                return methods[c].invoke(obj, arg);
            } catch (IllegalAccessException e) {
                logger.fine(e.getMessage());
            } catch (IllegalArgumentException e) {
                logger.fine(e.getMessage());
            }
        }
        throw new NoSuchMethodException(method);
    }
}
