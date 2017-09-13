/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.utils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/**
 * <p>Operates on classes without using reflection.</p>
 *
 * <p>This class handles invalid <code>null</code> inputs as best it can.
 * Each method documents its behaviour in more detail.</p>
 *
 * <p>The notion of a <code>canonical name</code> includes the human
 * readable name for the type, for example <code>int[]</code>. The
 * non-canonical method variants work with the JVM names, such as
 * <code>[I</code>. </p>
 *
 * @author 赵永春
 * @author Apache Software Foundation
 * @author Gary Gregory
 * @author Norm Deane
 * @author Alban Peignier
 * @author Tomasz Blachowicz
 * @since 2.0
 * @version $Id: ClassUtils.java 1057072 2011-01-10 01:55:57Z niallp $
 */
public class ClassUtils {
    /** <p>The package separator character: <code>'&#x2e;' == {@value}</code>.</p> */
    public static final  char                PACKAGE_SEPARATOR_CHAR     = '.';
    /** <p>The inner class separator character: <code>'$' == {@value}</code>.</p> */
    public static final  char                INNER_CLASS_SEPARATOR_CHAR = '$';
    /** Maps a primitive class name to its corresponding abbreviation used in array class names. */
    private static final Map<String, String> abbreviationMap            = new HashMap<String, String>();
    /** Maps an abbreviation used in array class names to corresponding primitive class name. */
    private static final Map<String, String> reverseAbbreviationMap     = new HashMap<String, String>();
    /**
     * Add primitive type abbreviation to maps of abbreviations.
     * @param primitive Canonical name of primitive type
     * @param abbreviation Corresponding abbreviation of primitive type
     */
    private static void addAbbreviation(final String primitive, final String abbreviation) {
        ClassUtils.abbreviationMap.put(primitive, abbreviation);
        ClassUtils.reverseAbbreviationMap.put(abbreviation, primitive);
    }
    /** Feed abbreviation maps. */
    static {
        ClassUtils.addAbbreviation("int", "I");
        ClassUtils.addAbbreviation("boolean", "Z");
        ClassUtils.addAbbreviation("float", "F");
        ClassUtils.addAbbreviation("long", "J");
        ClassUtils.addAbbreviation("short", "S");
        ClassUtils.addAbbreviation("byte", "B");
        ClassUtils.addAbbreviation("double", "D");
        ClassUtils.addAbbreviation("char", "C");
    }

    /**
     * <p>ClassUtils instances should NOT be constructed in standard programming.
     * Instead, the class should be used as
     * <code>ClassUtils.getShortClassName(cls)</code>.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public ClassUtils() {
        super();
    }
    // Short class name
    // ----------------------------------------------------------------------
    /**
     * <p>Gets the class name minus the package name from a String.</p>
     * <p>The string passed in is assumed to be a class name - it is not checked.</p>
     *
     * @param className  the className to get the short name for
     * @return the class name of the class without the package name or an empty string
     */
    public static String getShortClassName(String className) {
        if (className == null) {
            return StringUtils.EMPTY;
        }
        if (className.length() == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder arrayPrefix = new StringBuilder();
        // Handle array encoding
        if (className.startsWith("[")) {
            while (className.charAt(0) == '[') {
                className = className.substring(1);
                arrayPrefix.append("[]");
            }
            // Strip Object type encoding
            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
                className = className.substring(1, className.length() - 1);
            }
        }
        if (ClassUtils.reverseAbbreviationMap.containsKey(className)) {
            className = ClassUtils.reverseAbbreviationMap.get(className);
        }
        int lastDotIdx = className.lastIndexOf(ClassUtils.PACKAGE_SEPARATOR_CHAR);
        int innerIdx = className.indexOf(ClassUtils.INNER_CLASS_SEPARATOR_CHAR, lastDotIdx == -1 ? 0 : lastDotIdx + 1);
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1) {
            out = out.replace(ClassUtils.INNER_CLASS_SEPARATOR_CHAR, ClassUtils.PACKAGE_SEPARATOR_CHAR);
        }
        return out + arrayPrefix;
    }
    // ----------------------------------------------------------------------
    /**
     * <p>Gets the canonical name minus the package name from a <code>Class</code>.</p>
     *
     * @param cls  the class to get the short name for.
     * @return the canonical name without the package name or an empty string
     * @since 2.4
     */
    public static String getShortCanonicalName(final Class<?> cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return ClassUtils.getShortCanonicalName(cls.getName());
    }
    /**
     * <p>Gets the canonical name minus the package name from a String.</p>
     * <p>The string passed in is assumed to be a canonical name - it is not checked.</p>
     *
     * @param canonicalName  the class name to get the short name for
     * @return the canonical name of the class without the package name or an empty string
     * @since 2.4
     */
    public static String getShortCanonicalName(final String canonicalName) {
        return ClassUtils.getShortClassName(ClassUtils.getCanonicalName(canonicalName));
    }
    // Package name
    // ----------------------------------------------------------------------
    /**
     * <p>Converts a given name of class into canonical format.
     * If name of class is not a name of array class it returns unchanged name.</p>
     * <p>Example:
     * <ul>
     * <li><code>getCanonicalName("[I") = "int[]"</code></li>
     * <li><code>getCanonicalName("[Ljava.lang.String;") = "java.lang.String[]"</code></li>
     * <li><code>getCanonicalName("java.lang.String") = "java.lang.String"</code></li>
     * </ul>
     * </p>
     *
     * @param className the name of class
     * @return canonical form of class name
     * @since 2.4
     */
    private static String getCanonicalName(String className) {
        //
        if (!StringUtils.isEmpty(className)) {
            int sz = className.length();
            char[] chs = new char[sz];
            int count = 0;
            for (int i = 0; i < sz; i++) {
                if (!Character.isWhitespace(className.charAt(i))) {
                    chs[count++] = className.charAt(i);
                }
            }
            if (count != sz) {
                className = new String(chs, 0, count);
            }
        }
        //
        if (className == null) {
            return null;
        } else {
            int dim = 0;
            while (className.startsWith("[")) {
                dim++;
                className = className.substring(1);
            }
            if (dim < 1) {
                return className;
            } else {
                if (className.startsWith("L")) {
                    className = className.substring(1, className.endsWith(";") ? className.length() - 1 : className.length());
                } else {
                    if (className.length() > 0) {
                        className = ClassUtils.reverseAbbreviationMap.get(className.substring(0, 1));
                    }
                }
                StringBuilder canonicalClassNameBuffer = new StringBuilder(className);
                for (int i = 0; i < dim; i++) {
                    canonicalClassNameBuffer.append("[]");
                }
                return canonicalClassNameBuffer.toString();
            }
        }
    }
    /**获取方法的标识代码，在不考虑其所属类的情况下。*/
    public static String getDescName(final Class<?> type) {
        if (type == Void.class) {
            return "void ";
        } else if (type.isPrimitive()) {
            return ClassUtils.getShortCanonicalName(type);
        } else if (type.isArray()) {
            return type.getComponentType().getName() + "[]";
        } else {
            return type.getName();
        }
    }
    /**获取方法的标识代码，在不考虑其所属类的情况下。
     * 格式为：<code>&lt;修饰符&gt;&nbsp;&lt;返回值&gt;&nbsp;&lt;类名&gt;.&lt;方法名&gt;(&lt;参数签名列表&gt;)</code>*/
    public static String getDescNameWithOutModifiers(final Method method) {
        //public void addChild(org.noe.safety.services.SYS_TB_MenuTree)
        StringBuffer str = new StringBuffer("");
        //2.返回值
        Class<?> returnType = method.getReturnType();
        str.append(ClassUtils.getDescName(returnType) + " ");
        //3.方法名
        Class<?> decType = method.getDeclaringClass();
        str.append(decType.getName());
        str.append(".");
        str.append(method.getName());
        //4.方法签名
        Class<?>[] paramTypes = method.getParameterTypes();
        str.append("(");
        if (paramTypes != null) {
            for (int j = 0; j < paramTypes.length; j++) {
                str.append(ClassUtils.getDescName(paramTypes[j]));
                if (j < paramTypes.length - 1) {
                    str.append(",");
                }
            }
        }
        str.append(")");
        //
        return str.toString();
    }
}