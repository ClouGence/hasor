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
package org.more.util;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.more.NullArgumentException;
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
    /**
     * <p>The package separator character: <code>'&#x2e;' == {@value}</code>.</p>
     */
    public static final char   PACKAGE_SEPARATOR_CHAR     = '.';
    /**
     * <p>The package separator String: <code>"&#x2e;"</code>.</p>
     */
    public static final String PACKAGE_SEPARATOR          = String.valueOf(ClassUtils.PACKAGE_SEPARATOR_CHAR);
    /**
     * <p>The inner class separator character: <code>'$' == {@value}</code>.</p>
     */
    public static final char   INNER_CLASS_SEPARATOR_CHAR = '$';
    /**
     * <p>The inner class separator String: <code>"$"</code>.</p>
     */
    public static final String INNER_CLASS_SEPARATOR      = String.valueOf(ClassUtils.INNER_CLASS_SEPARATOR_CHAR);
    /**
     * Maps primitive <code>Class</code>es to their corresponding wrapper <code>Class</code>.
     */
    private static final Map   primitiveWrapperMap        = new HashMap();
    static {
        ClassUtils.primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        ClassUtils.primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        ClassUtils.primitiveWrapperMap.put(Character.TYPE, Character.class);
        ClassUtils.primitiveWrapperMap.put(Short.TYPE, Short.class);
        ClassUtils.primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        ClassUtils.primitiveWrapperMap.put(Long.TYPE, Long.class);
        ClassUtils.primitiveWrapperMap.put(Double.TYPE, Double.class);
        ClassUtils.primitiveWrapperMap.put(Float.TYPE, Float.class);
        ClassUtils.primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }
    /**
     * Maps wrapper <code>Class</code>es to their corresponding primitive types.
     */
    private static final Map   wrapperPrimitiveMap        = new HashMap();
    static {
        for (Iterator it = ClassUtils.primitiveWrapperMap.keySet().iterator(); it.hasNext();) {
            Class primitiveClass = (Class) it.next();
            Class wrapperClass = (Class) ClassUtils.primitiveWrapperMap.get(primitiveClass);
            if (!primitiveClass.equals(wrapperClass)) {
                ClassUtils.wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
    }
    /**
     * Maps a primitive class name to its corresponding abbreviation used in array class names.
     */
    private static final Map   abbreviationMap            = new HashMap();
    /**
     * Maps an abbreviation used in array class names to corresponding primitive class name.
     */
    private static final Map   reverseAbbreviationMap     = new HashMap();
    /**
     * Add primitive type abbreviation to maps of abbreviations.
     *
     * @param primitive Canonical name of primitive type
     * @param abbreviation Corresponding abbreviation of primitive type
     */
    private static void addAbbreviation(final String primitive, final String abbreviation) {
        ClassUtils.abbreviationMap.put(primitive, abbreviation);
        ClassUtils.reverseAbbreviationMap.put(abbreviation, primitive);
    }
    /**
     * Feed abbreviation maps
     */
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
     * <p>Gets the class name minus the package name for an <code>Object</code>.</p>
     *
     * @param object  the class to get the short name for, may be null
     * @param valueIfNull  the value to return if null
     * @return the class name of the object without the package name, or the null value
     */
    public static String getShortClassName(final Object object, final String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return ClassUtils.getShortClassName(object.getClass());
    }
    /**
     * <p>Gets the class name minus the package name from a <code>Class</code>.</p>
     *
     * @param cls  the class to get the short name for.
     * @return the class name without the package name or an empty string
     */
    public static String getShortClassName(final Class cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return ClassUtils.getShortClassName(cls.getName());
    }
    /**
     * <p>Gets the class name minus the package name from a String.</p>
     *
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
            className = (String) ClassUtils.reverseAbbreviationMap.get(className);
        }
        int lastDotIdx = className.lastIndexOf(ClassUtils.PACKAGE_SEPARATOR_CHAR);
        int innerIdx = className.indexOf(ClassUtils.INNER_CLASS_SEPARATOR_CHAR, lastDotIdx == -1 ? 0 : lastDotIdx + 1);
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1) {
            out = out.replace(ClassUtils.INNER_CLASS_SEPARATOR_CHAR, ClassUtils.PACKAGE_SEPARATOR_CHAR);
        }
        return out + arrayPrefix;
    }
    // Package name
    // ----------------------------------------------------------------------
    /**
     * <p>Gets the package name of an <code>Object</code>.</p>
     *
     * @param object  the class to get the package name for, may be null
     * @param valueIfNull  the value to return if null
     * @return the package name of the object, or the null value
     */
    public static String getPackageName(final Object object, final String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return ClassUtils.getPackageName(object.getClass());
    }
    /**
     * <p>Gets the package name of a <code>Class</code>.</p>
     *
     * @param cls  the class to get the package name for, may be <code>null</code>.
     * @return the package name or an empty string
     */
    public static String getPackageName(final Class cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return ClassUtils.getPackageName(cls.getName());
    }
    /**
     * <p>Gets the package name from a <code>String</code>.</p>
     *
     * <p>The string passed in is assumed to be a class name - it is not checked.</p>
     * <p>If the class is unpackaged, return an empty string.</p>
     *
     * @param className  the className to get the package name for, may be <code>null</code>
     * @return the package name or an empty string
     */
    public static String getPackageName(String className) {
        if (className == null || className.length() == 0) {
            return StringUtils.EMPTY;
        }
        // Strip array encoding
        while (className.charAt(0) == '[') {
            className = className.substring(1);
        }
        // Strip Object type encoding
        if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
            className = className.substring(1);
        }
        int i = className.lastIndexOf(ClassUtils.PACKAGE_SEPARATOR_CHAR);
        if (i == -1) {
            return StringUtils.EMPTY;
        }
        return className.substring(0, i);
    }
    // Superclasses/Superinterfaces
    // ----------------------------------------------------------------------
    /**
     * <p>Gets a <code>List</code> of superclasses for the given class.</p>
     *
     * @param cls  the class to look up, may be <code>null</code>
     * @return the <code>List</code> of superclasses in order going up from this one
     *  <code>null</code> if null input
     */
    public static List getAllSuperclasses(final Class cls) {
        if (cls == null) {
            return null;
        }
        List classes = new ArrayList();
        Class superclass = cls.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }
    /**
     * <p>Gets a <code>List</code> of all interfaces implemented by the given
     * class and its superclasses.</p>
     *
     * <p>The order is determined by looking through each interface in turn as
     * declared in the source file and following its hierarchy up. Then each
     * superclass is considered in the same way. Later duplicates are ignored,
     * so the order is maintained.</p>
     *
     * @param cls  the class to look up, may be <code>null</code>
     * @return the <code>List</code> of interfaces in order,
     *  <code>null</code> if null input
     */
    public static List getAllInterfaces(final Class cls) {
        if (cls == null) {
            return null;
        }
        List interfacesFound = new ArrayList();
        ClassUtils.getAllInterfaces(cls, interfacesFound);
        return interfacesFound;
    }
    /**
     * Get the interfaces for the specified class.
     *
     * @param cls  the class to look up, may be <code>null</code>
     * @param interfacesFound the <code>Set</code> of interfaces for the class
     */
    private static void getAllInterfaces(Class cls, final List interfacesFound) {
        while (cls != null) {
            Class[] interfaces = cls.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (!interfacesFound.contains(interfaces[i])) {
                    interfacesFound.add(interfaces[i]);
                    ClassUtils.getAllInterfaces(interfaces[i], interfacesFound);
                }
            }
            cls = cls.getSuperclass();
        }
    }
    // Convert list
    // ----------------------------------------------------------------------
    /**
     * <p>Given a <code>List</code> of class names, this method converts them into classes.</p>
     *
     * <p>A new <code>List</code> is returned. If the class name cannot be found, <code>null</code>
     * is stored in the <code>List</code>. If the class name in the <code>List</code> is
     * <code>null</code>, <code>null</code> is stored in the output <code>List</code>.</p>
     *
     * @param classNames  the classNames to change
     * @return a <code>List</code> of Class objects corresponding to the class names,
     *  <code>null</code> if null input
     * @throws ClassCastException if classNames contains a non String entry
     */
    public static List convertClassNamesToClasses(final List classNames) {
        if (classNames == null) {
            return null;
        }
        List classes = new ArrayList(classNames.size());
        for (Iterator it = classNames.iterator(); it.hasNext();) {
            String className = (String) it.next();
            try {
                classes.add(Class.forName(className));
            } catch (Exception ex) {
                classes.add(null);
            }
        }
        return classes;
    }
    /**
     * <p>Given a <code>List</code> of <code>Class</code> objects, this method converts
     * them into class names.</p>
     *
     * <p>A new <code>List</code> is returned. <code>null</code> objects will be copied into
     * the returned list as <code>null</code>.</p>
     *
     * @param classes  the classes to change
     * @return a <code>List</code> of class names corresponding to the Class objects,
     *  <code>null</code> if null input
     * @throws ClassCastException if <code>classes</code> contains a non-<code>Class</code> entry
     */
    public static List convertClassesToClassNames(final List classes) {
        if (classes == null) {
            return null;
        }
        List classNames = new ArrayList(classes.size());
        for (Iterator it = classes.iterator(); it.hasNext();) {
            Class cls = (Class) it.next();
            if (cls == null) {
                classNames.add(null);
            } else {
                classNames.add(cls.getName());
            }
        }
        return classNames;
    }
    // Is assignable
    // ----------------------------------------------------------------------
    /**
     * <p>Checks if an array of Classes can be assigned to another array of Classes.</p>
     *
     * <p>This method calls {@link #isAssignable(Class, Class) isAssignable} for each
     * Class pair in the input arrays. It can be used to check if a set of arguments
     * (the first parameter) are suitably compatible with a set of method parameter types
     * (the second parameter).</p>
     *
     * <p>Unlike the {@link Class#isAssignableFrom(java.lang.Class)} method, this
     * method takes into account widenings of primitive classes and
     * <code>null</code>s.</p>
     *
     * <p>Primitive widenings allow an int to be assigned to a <code>long</code>,
     * <code>float</code> or <code>double</code>. This method returns the correct
     * result for these cases.</p>
     *
     * <p><code>Null</code> may be assigned to any reference type. This method will
     * return <code>true</code> if <code>null</code> is passed in and the toClass is
     * non-primitive.</p>
     *
     * <p>Specifically, this method tests whether the type represented by the
     * specified <code>Class</code> parameter can be converted to the type
     * represented by this <code>Class</code> object via an identity conversion
     * widening primitive or widening reference conversion. See
     * <em><a href="http://java.sun.com/docs/books/jls/">The Java Language Specification</a></em>,
     * sections 5.1.1, 5.1.2 and 5.1.4 for details.</p>
     *
     * @param classArray  the array of Classes to check, may be <code>null</code>
     * @param toClassArray  the array of Classes to try to assign into, may be <code>null</code>
     * @return <code>true</code> if assignment possible
     */
    public static boolean isAssignable(final Class[] classArray, final Class[] toClassArray) {
        return ClassUtils.isAssignable(classArray, toClassArray, false);
    }
    /**
     * <p>Checks if an array of Classes can be assigned to another array of Classes.</p>
     *
     * <p>This method calls {@link #isAssignable(Class, Class) isAssignable} for each
     * Class pair in the input arrays. It can be used to check if a set of arguments
     * (the first parameter) are suitably compatible with a set of method parameter types
     * (the second parameter).</p>
     *
     * <p>Unlike the {@link Class#isAssignableFrom(java.lang.Class)} method, this
     * method takes into account widenings of primitive classes and
     * <code>null</code>s.</p>
     *
     * <p>Primitive widenings allow an int to be assigned to a <code>long</code>,
     * <code>float</code> or <code>double</code>. This method returns the correct
     * result for these cases.</p>
     *
     * <p><code>Null</code> may be assigned to any reference type. This method will
     * return <code>true</code> if <code>null</code> is passed in and the toClass is
     * non-primitive.</p>
     *
     * <p>Specifically, this method tests whether the type represented by the
     * specified <code>Class</code> parameter can be converted to the type
     * represented by this <code>Class</code> object via an identity conversion
     * widening primitive or widening reference conversion. See
     * <em><a href="http://java.sun.com/docs/books/jls/">The Java Language Specification</a></em>,
     * sections 5.1.1, 5.1.2 and 5.1.4 for details.</p>
     *
     * @param classArray  the array of Classes to check, may be <code>null</code>
     * @param toClassArray  the array of Classes to try to assign into, may be <code>null</code>
     * @param autoboxing  whether to use implicit autoboxing/unboxing between primitives and wrappers
     * @return <code>true</code> if assignment possible
     * @since 2.5
     */
    public static boolean isAssignable(Class[] classArray, Class[] toClassArray, final boolean autoboxing) {
        if (ArrayUtils.isSameLength(classArray, toClassArray) == false) {
            return false;
        }
        if (classArray == null) {
            classArray = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        if (toClassArray == null) {
            toClassArray = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        for (int i = 0; i < classArray.length; i++) {
            if (ClassUtils.isAssignable(classArray[i], toClassArray[i], autoboxing) == false) {
                return false;
            }
        }
        return true;
    }
    /**
     * <p>Checks if one <code>Class</code> can be assigned to a variable of
     * another <code>Class</code>.</p>
     *
     * <p>Unlike the {@link Class#isAssignableFrom(java.lang.Class)} method,
     * this method takes into account widenings of primitive classes and
     * <code>null</code>s.</p>
     *
     * <p>Primitive widenings allow an int to be assigned to a long, float or
     * double. This method returns the correct result for these cases.</p>
     *
     * <p><code>Null</code> may be assigned to any reference type. This method
     * will return <code>true</code> if <code>null</code> is passed in and the
     * toClass is non-primitive.</p>
     *
     * <p>Specifically, this method tests whether the type represented by the
     * specified <code>Class</code> parameter can be converted to the type
     * represented by this <code>Class</code> object via an identity conversion
     * widening primitive or widening reference conversion. See
     * <em><a href="http://java.sun.com/docs/books/jls/">The Java Language Specification</a></em>,
     * sections 5.1.1, 5.1.2 and 5.1.4 for details.</p>
     *
     * @param cls  the Class to check, may be null
     * @param toClass  the Class to try to assign into, returns false if null
     * @return <code>true</code> if assignment possible
     */
    public static boolean isAssignable(final Class cls, final Class toClass) {
        return ClassUtils.isAssignable(cls, toClass, false);
    }
    /**
     * <p>Checks if one <code>Class</code> can be assigned to a variable of
     * another <code>Class</code>.</p>
     *
     * <p>Unlike the {@link Class#isAssignableFrom(java.lang.Class)} method,
     * this method takes into account widenings of primitive classes and
     * <code>null</code>s.</p>
     *
     * <p>Primitive widenings allow an int to be assigned to a long, float or
     * double. This method returns the correct result for these cases.</p>
     *
     * <p><code>Null</code> may be assigned to any reference type. This method
     * will return <code>true</code> if <code>null</code> is passed in and the
     * toClass is non-primitive.</p>
     *
     * <p>Specifically, this method tests whether the type represented by the
     * specified <code>Class</code> parameter can be converted to the type
     * represented by this <code>Class</code> object via an identity conversion
     * widening primitive or widening reference conversion. See
     * <em><a href="http://java.sun.com/docs/books/jls/">The Java Language Specification</a></em>,
     * sections 5.1.1, 5.1.2 and 5.1.4 for details.</p>
     *
     * @param cls  the Class to check, may be null
     * @param toClass  the Class to try to assign into, returns false if null
     * @param autoboxing  whether to use implicit autoboxing/unboxing between primitives and wrappers
     * @return <code>true</code> if assignment possible
     * @since 2.5
     */
    public static boolean isAssignable(Class cls, final Class toClass, final boolean autoboxing) {
        if (toClass == null) {
            return false;
        }
        // have to check for null, as isAssignableFrom doesn't
        if (cls == null) {
            return !toClass.isPrimitive();
        }
        //autoboxing:
        if (autoboxing) {
            if (cls.isPrimitive() && !toClass.isPrimitive()) {
                cls = ClassUtils.primitiveToWrapper(cls);
                if (cls == null) {
                    return false;
                }
            }
            if (toClass.isPrimitive() && !cls.isPrimitive()) {
                cls = ClassUtils.wrapperToPrimitive(cls);
                if (cls == null) {
                    return false;
                }
            }
        }
        if (cls.equals(toClass)) {
            return true;
        }
        if (cls.isPrimitive()) {
            if (toClass.isPrimitive() == false) {
                return false;
            }
            if (Integer.TYPE.equals(cls)) {
                return Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Long.TYPE.equals(cls)) {
                return Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Boolean.TYPE.equals(cls)) {
                return false;
            }
            if (Double.TYPE.equals(cls)) {
                return false;
            }
            if (Float.TYPE.equals(cls)) {
                return Double.TYPE.equals(toClass);
            }
            if (Character.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Short.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Byte.TYPE.equals(cls)) {
                return Short.TYPE.equals(toClass) || Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            // should never get here
            return false;
        }
        return toClass.isAssignableFrom(cls);
    }
    /**
     * <p>Converts the specified primitive Class object to its corresponding
     * wrapper Class object.</p>
     *
     * <p>NOTE: From v2.2, this method handles <code>Void.TYPE</code>,
     * returning <code>Void.TYPE</code>.</p>
     *
     * @param cls  the class to convert, may be null
     * @return the wrapper class for <code>cls</code> or <code>cls</code> if
     * <code>cls</code> is not a primitive. <code>null</code> if null input.
     * @since 2.1
     */
    public static Class primitiveToWrapper(final Class cls) {
        Class convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = (Class) ClassUtils.primitiveWrapperMap.get(cls);
        }
        return convertedClass;
    }
    /**
     * <p>Converts the specified array of primitive Class objects to an array of
     * its corresponding wrapper Class objects.</p>
     *
     * @param classes  the class array to convert, may be null or empty
     * @return an array which contains for each given class, the wrapper class or
     * the original class if class is not a primitive. <code>null</code> if null input.
     * Empty array if an empty array passed in.
     * @since 2.1
     */
    public static Class[] primitivesToWrappers(final Class[] classes) {
        if (classes == null) {
            return null;
        }
        if (classes.length == 0) {
            return classes;
        }
        Class[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; i++) {
            convertedClasses[i] = ClassUtils.primitiveToWrapper(classes[i]);
        }
        return convertedClasses;
    }
    /**
     * <p>Converts the specified wrapper class to its corresponding primitive
     * class.</p>
     *
     * <p>This method is the counter part of <code>primitiveToWrapper()</code>.
     * If the passed in class is a wrapper class for a primitive type, this
     * primitive type will be returned (e.g. <code>Integer.TYPE</code> for
     * <code>Integer.class</code>). For other classes, or if the parameter is
     * <b>null</b>, the return value is <b>null</b>.</p>
     *
     * @param cls the class to convert, may be <b>null</b>
     * @return the corresponding primitive type if <code>cls</code> is a
     * wrapper class, <b>null</b> otherwise
     * @see #primitiveToWrapper(Class)
     * @since 2.4
     */
    public static Class wrapperToPrimitive(final Class cls) {
        return (Class) ClassUtils.wrapperPrimitiveMap.get(cls);
    }
    /**
     * <p>Converts the specified array of wrapper Class objects to an array of
     * its corresponding primitive Class objects.</p>
     *
     * <p>This method invokes <code>wrapperToPrimitive()</code> for each element
     * of the passed in array.</p>
     *
     * @param classes  the class array to convert, may be null or empty
     * @return an array which contains for each given class, the primitive class or
     * <b>null</b> if the original class is not a wrapper class. <code>null</code> if null input.
     * Empty array if an empty array passed in.
     * @see #wrapperToPrimitive(Class)
     * @since 2.4
     */
    public static Class[] wrappersToPrimitives(final Class[] classes) {
        if (classes == null) {
            return null;
        }
        if (classes.length == 0) {
            return classes;
        }
        Class[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; i++) {
            convertedClasses[i] = ClassUtils.wrapperToPrimitive(classes[i]);
        }
        return convertedClasses;
    }
    // Inner class
    // ----------------------------------------------------------------------
    /**
     * <p>Is the specified class an inner class or static nested class.</p>
     *
     * @param cls  the class to check, may be null
     * @return <code>true</code> if the class is an inner or static nested class,
     *  false if not or <code>null</code>
     */
    public static boolean isInnerClass(final Class cls) {
        if (cls == null) {
            return false;
        }
        return cls.getName().indexOf(ClassUtils.INNER_CLASS_SEPARATOR_CHAR) >= 0;
    }
    // Class loading
    // ----------------------------------------------------------------------
    /**
     * Returns the class represented by <code>className</code> using the
     * <code>classLoader</code>.  This implementation supports the syntaxes
     * "<code>java.util.Map.Entry[]</code>", "<code>java.util.Map$Entry[]</code>",
     * "<code>[Ljava.util.Map.Entry;</code>", and "<code>[Ljava.util.Map$Entry;</code>".
     *
     * @param classLoader  the class loader to use to load the class
     * @param className  the class name
     * @param initialize  whether the class must be initialized
     * @return the class represented by <code>className</code> using the <code>classLoader</code>
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class getClass(final ClassLoader classLoader, final String className, final boolean initialize) throws ClassNotFoundException {
        try {
            Class clazz;
            if (ClassUtils.abbreviationMap.containsKey(className)) {
                String clsName = "[" + ClassUtils.abbreviationMap.get(className);
                clazz = Class.forName(clsName, initialize, classLoader).getComponentType();
            } else {
                clazz = Class.forName(ClassUtils.toCanonicalName(className), initialize, classLoader);
            }
            return clazz;
        } catch (ClassNotFoundException ex) {
            // allow path separators (.) as inner class name separators
            int lastDotIndex = className.lastIndexOf(ClassUtils.PACKAGE_SEPARATOR_CHAR);
            if (lastDotIndex != -1) {
                try {
                    return ClassUtils.getClass(classLoader, className.substring(0, lastDotIndex) + ClassUtils.INNER_CLASS_SEPARATOR_CHAR + className.substring(lastDotIndex + 1), initialize);
                } catch (ClassNotFoundException ex2) {}
            }
            throw ex;
        }
    }
    /**
     * Returns the (initialized) class represented by <code>className</code>
     * using the <code>classLoader</code>.  This implementation supports
     * the syntaxes "<code>java.util.Map.Entry[]</code>",
     * "<code>java.util.Map$Entry[]</code>", "<code>[Ljava.util.Map.Entry;</code>",
     * and "<code>[Ljava.util.Map$Entry;</code>".
     *
     * @param classLoader  the class loader to use to load the class
     * @param className  the class name
     * @return the class represented by <code>className</code> using the <code>classLoader</code>
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class getClass(final ClassLoader classLoader, final String className) throws ClassNotFoundException {
        return ClassUtils.getClass(classLoader, className, true);
    }
    /**
     * Returns the (initialized) class represented by <code>className</code>
     * using the current thread's context class loader. This implementation
     * supports the syntaxes "<code>java.util.Map.Entry[]</code>",
     * "<code>java.util.Map$Entry[]</code>", "<code>[Ljava.util.Map.Entry;</code>",
     * and "<code>[Ljava.util.Map$Entry;</code>".
     *
     * @param className  the class name
     * @return the class represented by <code>className</code> using the current thread's context class loader
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class getClass(final String className) throws ClassNotFoundException {
        return ClassUtils.getClass(className, true);
    }
    /**
     * Returns the class represented by <code>className</code> using the
     * current thread's context class loader. This implementation supports the
     * syntaxes "<code>java.util.Map.Entry[]</code>", "<code>java.util.Map$Entry[]</code>",
     * "<code>[Ljava.util.Map.Entry;</code>", and "<code>[Ljava.util.Map$Entry;</code>".
     *
     * @param className  the class name
     * @param initialize  whether the class must be initialized
     * @return the class represented by <code>className</code> using the current thread's context class loader
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class getClass(final String className, final boolean initialize) throws ClassNotFoundException {
        ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
        ClassLoader loader = contextCL == null ? ClassUtils.class.getClassLoader() : contextCL;
        return ClassUtils.getClass(loader, className, initialize);
    }
    // Public method
    // ----------------------------------------------------------------------
    /**
     * <p>Returns the desired Method much like <code>Class.getMethod</code>, however
     * it ensures that the returned Method is from a public class or interface and not
     * from an anonymous inner class. This means that the Method is invokable and
     * doesn't fall foul of Java bug
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4071957">4071957</a>).
     *
     *  <code><pre>Set set = Collections.unmodifiableSet(...);
     *  Method method = ClassUtils.getPublicMethod(set.getClass(), "isEmpty",  new Class[0]);
     *  Object result = method.invoke(set, new Object[]);</pre></code>
     * </p>
     *
     * @param cls  the class to check, not null
     * @param methodName  the name of the method
     * @param parameterTypes  the list of parameters
     * @return the method
     * @throws NullPointerException if the class is null
     * @throws SecurityException if a a security violation occured
     * @throws NoSuchMethodException if the method is not found in the given class
     *  or if the metothod doen't conform with the requirements
     */
    public static Method getPublicMethod(final Class cls, final String methodName, final Class parameterTypes[]) throws SecurityException, NoSuchMethodException {
        Method declaredMethod = cls.getMethod(methodName, parameterTypes);
        if (Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers())) {
            return declaredMethod;
        }
        List candidateClasses = new ArrayList();
        candidateClasses.addAll(ClassUtils.getAllInterfaces(cls));
        candidateClasses.addAll(ClassUtils.getAllSuperclasses(cls));
        for (Iterator it = candidateClasses.iterator(); it.hasNext();) {
            Class candidateClass = (Class) it.next();
            if (!Modifier.isPublic(candidateClass.getModifiers())) {
                continue;
            }
            Method candidateMethod;
            try {
                candidateMethod = candidateClass.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ex) {
                continue;
            }
            if (Modifier.isPublic(candidateMethod.getDeclaringClass().getModifiers())) {
                return candidateMethod;
            }
        }
        StringBuffer sb = new StringBuffer();
        if (parameterTypes != null) {
            for (Class pt : parameterTypes) {
                sb.append(pt.getName() + ",");
            }
        }
        throw new NoSuchMethodException("Can't find a public method for " + methodName + " " + sb.toString());
    }
    // ----------------------------------------------------------------------
    /**
     * Converts a class name to a JLS style class name.
     *
     * @param className  the class name
     * @return the converted name
     */
    private static String toCanonicalName(String className) {
        className = StringUtils.deleteWhitespace(className);
        if (className == null) {
            throw new NullArgumentException("className");
        } else if (className.endsWith("[]")) {
            StringBuilder classNameBuffer = new StringBuilder();
            while (className.endsWith("[]")) {
                className = className.substring(0, className.length() - 2);
                classNameBuffer.append("[");
            }
            String abbreviation = (String) ClassUtils.abbreviationMap.get(className);
            if (abbreviation != null) {
                classNameBuffer.append(abbreviation);
            } else {
                classNameBuffer.append("L").append(className).append(";");
            }
            className = classNameBuffer.toString();
        }
        return className;
    }
    /**
     * <p>Converts an array of <code>Object</code> in to an array of <code>Class</code> objects.
     * If any of these objects is null, a null element will be inserted into the array.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array an <code>Object</code> array
     * @return a <code>Class</code> array, <code>null</code> if null array input
     * @since 2.4
     */
    public static Class[] toClass(final Object[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        Class[] classes = new Class[array.length];
        for (int i = 0; i < array.length; i++) {
            classes[i] = array[i] == null ? null : array[i].getClass();
        }
        return classes;
    }
    // Short canonical name
    // ----------------------------------------------------------------------
    /**
     * <p>Gets the canonical name minus the package name for an <code>Object</code>.</p>
     *
     * @param object  the class to get the short name for, may be null
     * @param valueIfNull  the value to return if null
     * @return the canonical name of the object without the package name, or the null value
     * @since 2.4
     */
    public static String getShortCanonicalName(final Object object, final String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return ClassUtils.getShortCanonicalName(object.getClass().getName());
    }
    /**
     * <p>Gets the canonical name minus the package name from a <code>Class</code>.</p>
     *
     * @param cls  the class to get the short name for.
     * @return the canonical name without the package name or an empty string
     * @since 2.4
     */
    public static String getShortCanonicalName(final Class cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return ClassUtils.getShortCanonicalName(cls.getName());
    }
    /**
     * <p>Gets the canonical name minus the package name from a String.</p>
     *
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
     * <p>Gets the package name from the canonical name of an <code>Object</code>.</p>
     *
     * @param object  the class to get the package name for, may be null
     * @param valueIfNull  the value to return if null
     * @return the package name of the object, or the null value
     * @since 2.4
     */
    public static String getPackageCanonicalName(final Object object, final String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return ClassUtils.getPackageCanonicalName(object.getClass().getName());
    }
    /**
     * <p>Gets the package name from the canonical name of a <code>Class</code>.</p>
     *
     * @param cls  the class to get the package name for, may be <code>null</code>.
     * @return the package name or an empty string
     * @since 2.4
     */
    public static String getPackageCanonicalName(final Class cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return ClassUtils.getPackageCanonicalName(cls.getName());
    }
    /**
     * <p>Gets the package name from the canonical name. </p>
     *
     * <p>The string passed in is assumed to be a canonical name - it is not checked.</p>
     * <p>If the class is unpackaged, return an empty string.</p>
     *
     * @param canonicalName  the canonical name to get the package name for, may be <code>null</code>
     * @return the package name or an empty string
     * @since 2.4
     */
    public static String getPackageCanonicalName(final String canonicalName) {
        return ClassUtils.getPackageName(ClassUtils.getCanonicalName(canonicalName));
    }
    /**
     * <p>Converts a given name of class into canonical format.
     * If name of class is not a name of array class it returns
     * unchanged name.</p>
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
        className = StringUtils.deleteWhitespace(className);
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
                        className = (String) ClassUtils.reverseAbbreviationMap.get(className.substring(0, 1));
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
    /**判断某个类是否为一个lang包的类。*/
    public static boolean isLangClass(final Class<?> target) {
        return target.getName().startsWith("java.lang.");
    };
    /**获取方法的标识代码，在不考虑其所属类的情况下。*/
    public static String getDescName(final Class<?> type) {
        if (type == Void.class) {
            return "void ";
        } else if (type.isPrimitive()) {
            return ClassUtils.getShortCanonicalName(type);
        } else {
            return type.getName();
        }
    }
    /**获取方法的标识代码，在不考虑其所属类的情况下。
     * 格式为：<code>&lt;修饰符&gt;&nbsp;&lt;返回值&gt;&nbsp;&lt;类名&gt;.&lt;方法名&gt;(&lt;参数签名列表&gt;)</code>*/
    public static String getDescName(final Method method) {
        //public void addChild(org.noe.safety.services.SYS_TB_MenuTree)
        StringBuffer str = new StringBuffer("");
        //1.访问修饰符
        int modifiers = method.getModifiers();
        if (Modifier.isPublic(modifiers)) {
            str.append("public ");
        } else if (Modifier.isPrivate(modifiers)) {
            str.append("private ");
        } else if (Modifier.isProtected(modifiers)) {
            str.append("protected ");
        } else {
            str.append("friendly ");
        }
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