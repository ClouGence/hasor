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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>Operations on arrays, primitive arrays (like <code>int[]</code>) and
 * primitive wrapper arrays (like <code>Integer[]</code>).</p>
 *
 * <p>This class tries to handle <code>null</code> input gracefully.
 * An exception will not be thrown for a <code>null</code>
 * array input. However, an Object array that contains a <code>null</code>
 * element may throw an exception. Each method documents its behaviour.</p>
 *
 * <p>#ThreadSafe#</p>
 * @author Apache Software Foundation
 * @author Moritz Petersen
 * @author <a href="mailto:fredrik@westermarck.com">Fredrik Westermarck</a>
 * @author Nikolay Metchev
 * @author Matthew Hawthorne
 * @author Tim O'Brien
 * @author Pete Gieser
 * @author Gary Gregory
 * @author <a href="mailto:equinus100@hotmail.com">Ashwin S</a>
 * @author Maarten Coene
 * @since 2.0
 * @version $Id: ArrayUtils.java 1056988 2011-01-09 17:58:53Z niallp $
 */
public class ArrayUtils {
    /** An empty immutable <code>Object</code> array. */
    public static final Object[]    EMPTY_OBJECT_ARRAY           = new Object[0];
    /** An empty immutable <code>Class</code> array. */
    public static final Class<?>[]  EMPTY_CLASS_ARRAY            = new Class[0];
    /** An empty immutable <code>String</code> array. */
    public static final String[]    EMPTY_STRING_ARRAY           = new String[0];
    /** An empty immutable <code>long</code> array. */
    public static final long[]      EMPTY_LONG_ARRAY             = new long[0];
    /** An empty immutable <code>Long</code> array. */
    public static final Long[]      EMPTY_LONG_OBJECT_ARRAY      = new Long[0];
    /** An empty immutable <code>int</code> array. */
    public static final int[]       EMPTY_INT_ARRAY              = new int[0];
    /** An empty immutable <code>Integer</code> array. */
    public static final Integer[]   EMPTY_INTEGER_OBJECT_ARRAY   = new Integer[0];
    /** An empty immutable <code>short</code> array. */
    public static final short[]     EMPTY_SHORT_ARRAY            = new short[0];
    /** An empty immutable <code>Short</code> array. */
    public static final Short[]     EMPTY_SHORT_OBJECT_ARRAY     = new Short[0];
    /** An empty immutable <code>byte</code> array. */
    public static final byte[]      EMPTY_BYTE_ARRAY             = new byte[0];
    /** An empty immutable <code>Byte</code> array. */
    public static final Byte[]      EMPTY_BYTE_OBJECT_ARRAY      = new Byte[0];
    /** An empty immutable <code>double</code> array. */
    public static final double[]    EMPTY_DOUBLE_ARRAY           = new double[0];
    /** An empty immutable <code>Double</code> array. */
    public static final Double[]    EMPTY_DOUBLE_OBJECT_ARRAY    = new Double[0];
    /** An empty immutable <code>float</code> array. */
    public static final float[]     EMPTY_FLOAT_ARRAY            = new float[0];
    /** An empty immutable <code>Float</code> array. */
    public static final Float[]     EMPTY_FLOAT_OBJECT_ARRAY     = new Float[0];
    /** An empty immutable <code>boolean</code> array. */
    public static final boolean[]   EMPTY_BOOLEAN_ARRAY          = new boolean[0];
    /** An empty immutable <code>Boolean</code> array. */
    public static final Boolean[]   EMPTY_BOOLEAN_OBJECT_ARRAY   = new Boolean[0];
    /** An empty immutable <code>char</code> array. */
    public static final char[]      EMPTY_CHAR_ARRAY             = new char[0];
    /** An empty immutable <code>Character</code> array. */
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
    /**
     * The index value when an element is not found in a list or array: <code>-1</code>.
     * This value is returned by methods in this class and can also be used in comparisons with values returned by
     * various method from {@link java.util.List}.
     */
    public static final int         INDEX_NOT_FOUND              = -1;

    /**
     * <p>ArrayUtils instances should NOT be constructed in standard programming.
     * Instead, the class should be used as <code>ArrayUtils.clone(new int[] {2})</code>.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean instance
     * to operate.</p>
     */
    public ArrayUtils() {
        super();
    }
    //-----------------------------------------------------------------------

    /**
     * <p>Converts the given array into a {@link Map}. Each element of the array
     * must be either a {@link Entry} or an Array, containing at least two
     * elements, where the first element is used as key and the second as
     * value.</p>
     *
     * <p>This method can be used to initialize:</p>
     * <pre>
     * // Create a Map mapping colors.
     * Map colorMap = ArrayUtils.toMap(new String[][] {{
     *     {"RED", "#FF0000"},
     *     {"GREEN", "#00FF00"},
     *     {"BLUE", "#0000FF"}});
     * </pre>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  an array whose elements are either a {@link Entry} or
     *  an Array containing at least two elements, may be <code>null</code>
     * @return a <code>Map</code> that was created from the array
     * @throws IllegalArgumentException  if one element of this Array is
     *  itself an Array containing less then two elements
     * @throws IllegalArgumentException  if the array contains elements other
     *  than {@link Entry} and an Array
     */
    public static Map toMap(final Object[] array) {
        if (array == null) {
            return null;
        }
        final Map map = new HashMap((int) (array.length * 1.5));
        for (int i = 0; i < array.length; i++) {
            Object object = array[i];
            if (object instanceof Entry) {
                Entry entry = (Entry) object;
                map.put(entry.getKey(), entry.getValue());
            } else if (object instanceof Object[]) {
                Object[] entry = (Object[]) object;
                if (entry.length < 2) {
                    throw new IllegalArgumentException("Array element " + i + ", '" + object + "', has a length less than 2");
                }
                map.put(entry[0], entry[1]);
            } else {
                throw new IllegalArgumentException("Array element " + i + ", '" + object + "', is neither of type Map.Entry nor an Array");
            }
        }
        return map;
    }
    // Clone
    //-----------------------------------------------------------------------

    /**
     * <p>Shallow clones an array returning a typecast result and handling
     * <code>null</code>.</p>
     *
     * <p>The objects in the array are not cloned, thus there is no special
     * handling for multi-dimensional arrays.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  the array to shallow clone, may be <code>null</code>
     * @return the cloned array, <code>null</code> if <code>null</code> input
     */
    public static Object[] clone(final Object[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * <p>Clones an array returning a typecast result and handling
     * <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  the array to clone, may be <code>null</code>
     * @return the cloned array, <code>null</code> if <code>null</code> input
     */
    public static long[] clone(final long[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * <p>Clones an array returning a typecast result and handling
     * <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  the array to clone, may be <code>null</code>
     * @return the cloned array, <code>null</code> if <code>null</code> input
     */
    public static int[] clone(final int[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * <p>Clones an array returning a typecast result and handling
     * <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  the array to clone, may be <code>null</code>
     * @return the cloned array, <code>null</code> if <code>null</code> input
     */
    public static short[] clone(final short[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * <p>Clones an array returning a typecast result and handling
     * <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  the array to clone, may be <code>null</code>
     * @return the cloned array, <code>null</code> if <code>null</code> input
     */
    public static char[] clone(final char[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * <p>Clones an array returning a typecast result and handling
     * <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  the array to clone, may be <code>null</code>
     * @return the cloned array, <code>null</code> if <code>null</code> input
     */
    public static byte[] clone(final byte[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * <p>Clones an array returning a typecast result and handling
     * <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  the array to clone, may be <code>null</code>
     * @return the cloned array, <code>null</code> if <code>null</code> input
     */
    public static double[] clone(final double[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * <p>Clones an array returning a typecast result and handling
     * <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  the array to clone, may be <code>null</code>
     * @return the cloned array, <code>null</code> if <code>null</code> input
     */
    public static float[] clone(final float[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * <p>Clones an array returning a typecast result and handling
     * <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  the array to clone, may be <code>null</code>
     * @return the cloned array, <code>null</code> if <code>null</code> input
     */
    public static boolean[] clone(final boolean[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
    // nullToEmpty
    //-----------------------------------------------------------------------

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static Object[] nullToEmpty(final Object[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static String[] nullToEmpty(final String[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static long[] nullToEmpty(final long[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_LONG_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static int[] nullToEmpty(final int[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static short[] nullToEmpty(final short[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_SHORT_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static char[] nullToEmpty(final char[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static byte[] nullToEmpty(final byte[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static double[] nullToEmpty(final double[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static float[] nullToEmpty(final float[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_FLOAT_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static boolean[] nullToEmpty(final boolean[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_BOOLEAN_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static Long[] nullToEmpty(final Long[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_LONG_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static Integer[] nullToEmpty(final Integer[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static Short[] nullToEmpty(final Short[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static Character[] nullToEmpty(final Character[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static Byte[] nullToEmpty(final Byte[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static Double[] nullToEmpty(final Double[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static Float[] nullToEmpty(final Float[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * <p>Defensive programming technique to change a <code>null</code>
     * reference to an empty one.</p>
     *
     * <p>This method returns an empty array for a <code>null</code> input array.</p>
     *
     * <p>As a memory optimizing technique an empty array passed in will be overridden with 
     * the empty <code>public static</code> references in this class.</p>
     *
     * @param array  the array to check for <code>null</code> or empty
     * @return the same array, <code>public static</code> empty array if <code>null</code> or empty input
     * @since 2.5
     */
    public static Boolean[] nullToEmpty(final Boolean[] array) {
        if (array == null || array.length == 0) {
            return ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY;
        }
        return array;
    }
    // Subarrays
    //-----------------------------------------------------------------------

    /**
     * <p>Produces a new array containing the elements between
     * the start and end indices.</p>
     *
     * <p>The start index is inclusive, the end index exclusive.
     * Null array input produces null output.</p>
     *
     * <p>The component type of the subarray is always the same as
     * that of the input array. Thus, if the input is an array of type
     * <code>Date</code>, the following usage is envisaged:</p>
     *
     * <pre>
     * Date[] someDates = (Date[])ArrayUtils.subarray(allDates, 2, 5);
     * </pre>
     *
     * @param array  the array
     * @param startIndexInclusive  the starting index. Undervalue (&lt;0)
     *      is promoted to 0, overvalue (&gt;array.length) results
     *      in an empty array.
     * @param endIndexExclusive  elements up to endIndex-1 are present in the
     *      returned subarray. Undervalue (&lt; startIndex) produces
     *      empty array, overvalue (&gt;array.length) is demoted to
     *      array length.
     * @return a new array containing the elements between
     *      the start and end indices.
     * @since 2.1
     */
    public static Object[] subarray(final Object[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        Class<?> type = array.getClass().getComponentType();
        if (newSize <= 0) {
            return (Object[]) Array.newInstance(type, 0);
        }
        Object[] subarray = (Object[]) Array.newInstance(type, newSize);
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * <p>Produces a new <code>long</code> array containing the elements
     * between the start and end indices.</p>
     *
     * <p>The start index is inclusive, the end index exclusive.
     * Null array input produces null output.</p>
     *
     * @param array  the array
     * @param startIndexInclusive  the starting index. Undervalue (&lt;0)
     *      is promoted to 0, overvalue (&gt;array.length) results
     *      in an empty array.
     * @param endIndexExclusive  elements up to endIndex-1 are present in the
     *      returned subarray. Undervalue (&lt; startIndex) produces
     *      empty array, overvalue (&gt;array.length) is demoted to
     *      array length.
     * @return a new array containing the elements between
     *      the start and end indices.
     * @since 2.1
     */
    public static long[] subarray(final long[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_LONG_ARRAY;
        }
        long[] subarray = new long[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * <p>Produces a new <code>int</code> array containing the elements
     * between the start and end indices.</p>
     *
     * <p>The start index is inclusive, the end index exclusive.
     * Null array input produces null output.</p>
     *
     * @param array  the array
     * @param startIndexInclusive  the starting index. Undervalue (&lt;0)
     *      is promoted to 0, overvalue (&gt;array.length) results
     *      in an empty array.
     * @param endIndexExclusive  elements up to endIndex-1 are present in the
     *      returned subarray. Undervalue (&lt; startIndex) produces
     *      empty array, overvalue (&gt;array.length) is demoted to
     *      array length.
     * @return a new array containing the elements between
     *      the start and end indices.
     * @since 2.1
     */
    public static int[] subarray(final int[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        int[] subarray = new int[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * <p>Produces a new <code>short</code> array containing the elements
     * between the start and end indices.</p>
     *
     * <p>The start index is inclusive, the end index exclusive.
     * Null array input produces null output.</p>
     *
     * @param array  the array
     * @param startIndexInclusive  the starting index. Undervalue (&lt;0)
     *      is promoted to 0, overvalue (&gt;array.length) results
     *      in an empty array.
     * @param endIndexExclusive  elements up to endIndex-1 are present in the
     *      returned subarray. Undervalue (&lt; startIndex) produces
     *      empty array, overvalue (&gt;array.length) is demoted to
     *      array length.
     * @return a new array containing the elements between
     *      the start and end indices.
     * @since 2.1
     */
    public static short[] subarray(final short[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_SHORT_ARRAY;
        }
        short[] subarray = new short[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * <p>Produces a new <code>char</code> array containing the elements
     * between the start and end indices.</p>
     *
     * <p>The start index is inclusive, the end index exclusive.
     * Null array input produces null output.</p>
     *
     * @param array  the array
     * @param startIndexInclusive  the starting index. Undervalue (&lt;0)
     *      is promoted to 0, overvalue (&gt;array.length) results
     *      in an empty array.
     * @param endIndexExclusive  elements up to endIndex-1 are present in the
     *      returned subarray. Undervalue (&lt; startIndex) produces
     *      empty array, overvalue (&gt;array.length) is demoted to
     *      array length.
     * @return a new array containing the elements between
     *      the start and end indices.
     * @since 2.1
     */
    public static char[] subarray(final char[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        char[] subarray = new char[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * <p>Produces a new <code>byte</code> array containing the elements
     * between the start and end indices.</p>
     *
     * <p>The start index is inclusive, the end index exclusive.
     * Null array input produces null output.</p>
     *
     * @param array  the array
     * @param startIndexInclusive  the starting index. Undervalue (&lt;0)
     *      is promoted to 0, overvalue (&gt;array.length) results
     *      in an empty array.
     * @param endIndexExclusive  elements up to endIndex-1 are present in the
     *      returned subarray. Undervalue (&lt; startIndex) produces
     *      empty array, overvalue (&gt;array.length) is demoted to
     *      array length.
     * @return a new array containing the elements between
     *      the start and end indices.
     * @since 2.1
     */
    public static byte[] subarray(final byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        byte[] subarray = new byte[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * <p>Produces a new <code>double</code> array containing the elements
     * between the start and end indices.</p>
     *
     * <p>The start index is inclusive, the end index exclusive.
     * Null array input produces null output.</p>
     *
     * @param array  the array
     * @param startIndexInclusive  the starting index. Undervalue (&lt;0)
     *      is promoted to 0, overvalue (&gt;array.length) results
     *      in an empty array.
     * @param endIndexExclusive  elements up to endIndex-1 are present in the
     *      returned subarray. Undervalue (&lt; startIndex) produces
     *      empty array, overvalue (&gt;array.length) is demoted to
     *      array length.
     * @return a new array containing the elements between
     *      the start and end indices.
     * @since 2.1
     */
    public static double[] subarray(final double[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        }
        double[] subarray = new double[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * <p>Produces a new <code>float</code> array containing the elements
     * between the start and end indices.</p>
     *
     * <p>The start index is inclusive, the end index exclusive.
     * Null array input produces null output.</p>
     *
     * @param array  the array
     * @param startIndexInclusive  the starting index. Undervalue (&lt;0)
     *      is promoted to 0, overvalue (&gt;array.length) results
     *      in an empty array.
     * @param endIndexExclusive  elements up to endIndex-1 are present in the
     *      returned subarray. Undervalue (&lt; startIndex) produces
     *      empty array, overvalue (&gt;array.length) is demoted to
     *      array length.
     * @return a new array containing the elements between
     *      the start and end indices.
     * @since 2.1
     */
    public static float[] subarray(final float[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_FLOAT_ARRAY;
        }
        float[] subarray = new float[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    /**
     * <p>Produces a new <code>boolean</code> array containing the elements
     * between the start and end indices.</p>
     *
     * <p>The start index is inclusive, the end index exclusive.
     * Null array input produces null output.</p>
     *
     * @param array  the array
     * @param startIndexInclusive  the starting index. Undervalue (&lt;0)
     *      is promoted to 0, overvalue (&gt;array.length) results
     *      in an empty array.
     * @param endIndexExclusive  elements up to endIndex-1 are present in the
     *      returned subarray. Undervalue (&lt; startIndex) produces
     *      empty array, overvalue (&gt;array.length) is demoted to
     *      array length.
     * @return a new array containing the elements between
     *      the start and end indices.
     * @since 2.1
     */
    public static boolean[] subarray(final boolean[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_BOOLEAN_ARRAY;
        }
        boolean[] subarray = new boolean[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
    // Is same length
    //-----------------------------------------------------------------------

    /**
     * <p>Checks whether two arrays are the same length, treating
     * <code>null</code> arrays as length <code>0</code>.
     *
     * <p>Any multi-dimensional aspects of the arrays are ignored.</p>
     *
     * @param array1 the first array, may be <code>null</code>
     * @param array2 the second array, may be <code>null</code>
     * @return <code>true</code> if length of arrays matches, treating
     *  <code>null</code> as an empty array
     */
    public static boolean isSameLength(final Object[] array1, final Object[] array2) {
        if (array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0 || array1 != null && array2 != null && array1.length != array2.length) {
            return false;
        }
        return true;
    }

    /**
     * <p>Checks whether two arrays are the same length, treating
     * <code>null</code> arrays as length <code>0</code>.</p>
     *
     * @param array1 the first array, may be <code>null</code>
     * @param array2 the second array, may be <code>null</code>
     * @return <code>true</code> if length of arrays matches, treating
     *  <code>null</code> as an empty array
     */
    public static boolean isSameLength(final long[] array1, final long[] array2) {
        if (array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0 || array1 != null && array2 != null && array1.length != array2.length) {
            return false;
        }
        return true;
    }

    /**
     * <p>Checks whether two arrays are the same length, treating
     * <code>null</code> arrays as length <code>0</code>.</p>
     *
     * @param array1 the first array, may be <code>null</code>
     * @param array2 the second array, may be <code>null</code>
     * @return <code>true</code> if length of arrays matches, treating
     *  <code>null</code> as an empty array
     */
    public static boolean isSameLength(final int[] array1, final int[] array2) {
        if (array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0 || array1 != null && array2 != null && array1.length != array2.length) {
            return false;
        }
        return true;
    }

    /**
     * <p>Checks whether two arrays are the same length, treating
     * <code>null</code> arrays as length <code>0</code>.</p>
     *
     * @param array1 the first array, may be <code>null</code>
     * @param array2 the second array, may be <code>null</code>
     * @return <code>true</code> if length of arrays matches, treating
     *  <code>null</code> as an empty array
     */
    public static boolean isSameLength(final short[] array1, final short[] array2) {
        if (array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0 || array1 != null && array2 != null && array1.length != array2.length) {
            return false;
        }
        return true;
    }

    /**
     * <p>Checks whether two arrays are the same length, treating
     * <code>null</code> arrays as length <code>0</code>.</p>
     *
     * @param array1 the first array, may be <code>null</code>
     * @param array2 the second array, may be <code>null</code>
     * @return <code>true</code> if length of arrays matches, treating
     *  <code>null</code> as an empty array
     */
    public static boolean isSameLength(final char[] array1, final char[] array2) {
        if (array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0 || array1 != null && array2 != null && array1.length != array2.length) {
            return false;
        }
        return true;
    }

    /**
     * <p>Checks whether two arrays are the same length, treating
     * <code>null</code> arrays as length <code>0</code>.</p>
     *
     * @param array1 the first array, may be <code>null</code>
     * @param array2 the second array, may be <code>null</code>
     * @return <code>true</code> if length of arrays matches, treating
     *  <code>null</code> as an empty array
     */
    public static boolean isSameLength(final byte[] array1, final byte[] array2) {
        if (array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0 || array1 != null && array2 != null && array1.length != array2.length) {
            return false;
        }
        return true;
    }

    /**
     * <p>Checks whether two arrays are the same length, treating
     * <code>null</code> arrays as length <code>0</code>.</p>
     *
     * @param array1 the first array, may be <code>null</code>
     * @param array2 the second array, may be <code>null</code>
     * @return <code>true</code> if length of arrays matches, treating
     *  <code>null</code> as an empty array
     */
    public static boolean isSameLength(final double[] array1, final double[] array2) {
        if (array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0 || array1 != null && array2 != null && array1.length != array2.length) {
            return false;
        }
        return true;
    }

    /**
     * <p>Checks whether two arrays are the same length, treating
     * <code>null</code> arrays as length <code>0</code>.</p>
     *
     * @param array1 the first array, may be <code>null</code>
     * @param array2 the second array, may be <code>null</code>
     * @return <code>true</code> if length of arrays matches, treating
     *  <code>null</code> as an empty array
     */
    public static boolean isSameLength(final float[] array1, final float[] array2) {
        if (array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0 || array1 != null && array2 != null && array1.length != array2.length) {
            return false;
        }
        return true;
    }

    /**
     * <p>Checks whether two arrays are the same length, treating
     * <code>null</code> arrays as length <code>0</code>.</p>
     *
     * @param array1 the first array, may be <code>null</code>
     * @param array2 the second array, may be <code>null</code>
     * @return <code>true</code> if length of arrays matches, treating
     *  <code>null</code> as an empty array
     */
    public static boolean isSameLength(final boolean[] array1, final boolean[] array2) {
        if (array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0 || array1 != null && array2 != null && array1.length != array2.length) {
            return false;
        }
        return true;
    }
    //-----------------------------------------------------------------------

    /**
     * <p>Returns the length of the specified array.
     * This method can deal with <code>Object</code> arrays and with primitive arrays.</p>
     *
     * <p>If the input array is <code>null</code>, <code>0</code> is returned.</p>
     *
     * <pre>
     * ArrayUtils.getLength(null)            = 0
     * ArrayUtils.getLength([])              = 0
     * ArrayUtils.getLength([null])          = 1
     * ArrayUtils.getLength([true, false])   = 2
     * ArrayUtils.getLength([1, 2, 3])       = 3
     * ArrayUtils.getLength(["a", "b", "c"]) = 3
     * </pre>
     *
     * @param array  the array to retrieve the length from, may be null
     * @return The length of the array, or <code>0</code> if the array is <code>null</code>
     * @throws IllegalArgumentException if the object arguement is not an array.
     * @since 2.1
     */
    public static int getLength(final Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }

    /**
     * <p>Checks whether two arrays are the same type taking into account
     * multi-dimensional arrays.</p>
     *
     * @param array1 the first array, must not be <code>null</code>
     * @param array2 the second array, must not be <code>null</code>
     * @return <code>true</code> if type of arrays matches
     * @throws IllegalArgumentException if either array is <code>null</code>
     */
    public static boolean isSameType(final Object array1, final Object array2) {
        if (array1 == null || array2 == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        return array1.getClass().getName().equals(array2.getClass().getName());
    }
    // Reverse
    //-----------------------------------------------------------------------

    /**
     * <p>Reverses the order of the given array.</p>
     *
     * <p>There is no special handling for multi-dimensional arrays.</p>
     *
     * <p>This method does nothing for a <code>null</code> input array.</p>
     *
     * @param array  the array to reverse, may be <code>null</code>
     */
    public static void reverse(final Object[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        Object tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * <p>Reverses the order of the given array.</p>
     *
     * <p>This method does nothing for a <code>null</code> input array.</p>
     *
     * @param array  the array to reverse, may be <code>null</code>
     */
    public static void reverse(final long[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        long tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * <p>Reverses the order of the given array.</p>
     *
     * <p>This method does nothing for a <code>null</code> input array.</p>
     *
     * @param array  the array to reverse, may be <code>null</code>
     */
    public static void reverse(final int[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        int tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * <p>Reverses the order of the given array.</p>
     *
     * <p>This method does nothing for a <code>null</code> input array.</p>
     *
     * @param array  the array to reverse, may be <code>null</code>
     */
    public static void reverse(final short[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        short tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * <p>Reverses the order of the given array.</p>
     *
     * <p>This method does nothing for a <code>null</code> input array.</p>
     *
     * @param array  the array to reverse, may be <code>null</code>
     */
    public static void reverse(final char[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        char tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * <p>Reverses the order of the given array.</p>
     *
     * <p>This method does nothing for a <code>null</code> input array.</p>
     *
     * @param array  the array to reverse, may be <code>null</code>
     */
    public static void reverse(final byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * <p>Reverses the order of the given array.</p>
     *
     * <p>This method does nothing for a <code>null</code> input array.</p>
     *
     * @param array  the array to reverse, may be <code>null</code>
     */
    public static void reverse(final double[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        double tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * <p>Reverses the order of the given array.</p>
     *
     * <p>This method does nothing for a <code>null</code> input array.</p>
     *
     * @param array  the array to reverse, may be <code>null</code>
     */
    public static void reverse(final float[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        float tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * <p>Reverses the order of the given array.</p>
     *
     * <p>This method does nothing for a <code>null</code> input array.</p>
     *
     * @param array  the array to reverse, may be <code>null</code>
     */
    public static void reverse(final boolean[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        boolean tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }
    // IndexOf search
    // ----------------------------------------------------------------------
    // Object IndexOf
    //-----------------------------------------------------------------------

    /**
     * <p>Finds the index of the given object in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param objectToFind  the object to find, may be <code>null</code>
     * @return the index of the object within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final Object[] array, final Object objectToFind) {
        return ArrayUtils.indexOf(array, objectToFind, 0);
    }

    /**
     * <p>Finds the index of the given object in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (<code>-1</code>).</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param objectToFind  the object to find, may be <code>null</code>
     * @param startIndex  the index to start searching at
     * @return the index of the object within the array starting at the index,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final Object[] array, final Object objectToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given object within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be <code>null</code>
     * @param objectToFind  the object to find, may be <code>null</code>
     * @return the last index of the object within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final Object[] array, final Object objectToFind) {
        return ArrayUtils.lastIndexOf(array, objectToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given object in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} (<code>-1</code>). A startIndex larger than
     * the array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be <code>null</code>
     * @param objectToFind  the object to find, may be <code>null</code>
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the object within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final Object[] array, final Object objectToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return ArrayUtils.INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i >= 0; i--) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i >= 0; i--) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the object is in the given array.</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array is passed in.</p>
     *
     * @param array  the array to search through
     * @param objectToFind  the object to find
     * @return <code>true</code> if the array contains the object
     */
    public static boolean contains(final Object[] array, final Object objectToFind) {
        return ArrayUtils.indexOf(array, objectToFind) != ArrayUtils.INDEX_NOT_FOUND;
    }
    // long IndexOf
    //-----------------------------------------------------------------------

    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final long[] array, final long valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (<code>-1</code>).</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final long[] array, final long valueToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be <code>null</code>
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final long[] array, final long valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} (<code>-1</code>). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final long[] array, final long valueToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return ArrayUtils.INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return <code>true</code> if the array contains the object
     */
    public static boolean contains(final long[] array, final long valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != ArrayUtils.INDEX_NOT_FOUND;
    }
    // int IndexOf
    //-----------------------------------------------------------------------

    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final int[] array, final int valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (<code>-1</code>).</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final int[] array, final int valueToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be <code>null</code>
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final int[] array, final int valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} (<code>-1</code>). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final int[] array, final int valueToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return ArrayUtils.INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return <code>true</code> if the array contains the object
     */
    public static boolean contains(final int[] array, final int valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != ArrayUtils.INDEX_NOT_FOUND;
    }
    // short IndexOf
    //-----------------------------------------------------------------------

    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final short[] array, final short valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (<code>-1</code>).</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final short[] array, final short valueToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be <code>null</code>
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final short[] array, final short valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} (<code>-1</code>). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final short[] array, final short valueToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return ArrayUtils.INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return <code>true</code> if the array contains the object
     */
    public static boolean contains(final short[] array, final short valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != ArrayUtils.INDEX_NOT_FOUND;
    }
    // char IndexOf
    //-----------------------------------------------------------------------

    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     * @since 2.1
     */
    public static int indexOf(final char[] array, final char valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (<code>-1</code>).</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     * @since 2.1
     */
    public static int indexOf(final char[] array, final char valueToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be <code>null</code>
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     * @since 2.1
     */
    public static int lastIndexOf(final char[] array, final char valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} (<code>-1</code>). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     * @since 2.1
     */
    public static int lastIndexOf(final char[] array, final char valueToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return ArrayUtils.INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return <code>true</code> if the array contains the object
     * @since 2.1
     */
    public static boolean contains(final char[] array, final char valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != ArrayUtils.INDEX_NOT_FOUND;
    }
    // byte IndexOf
    //-----------------------------------------------------------------------

    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final byte[] array, final byte valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (<code>-1</code>).</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final byte[] array, final byte valueToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be <code>null</code>
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final byte[] array, final byte valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} (<code>-1</code>). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final byte[] array, final byte valueToFind, int startIndex) {
        if (array == null) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return ArrayUtils.INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return <code>true</code> if the array contains the object
     */
    public static boolean contains(final byte[] array, final byte valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != ArrayUtils.INDEX_NOT_FOUND;
    }
    // double IndexOf
    //-----------------------------------------------------------------------

    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final double[] array, final double valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value within a given tolerance in the array.
     * This method will return the index of the first value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param tolerance tolerance of the search
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final double[] array, final double valueToFind, final double tolerance) {
        return ArrayUtils.indexOf(array, valueToFind, 0, tolerance);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (<code>-1</code>).</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final double[] array, final double valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.
     * This method will return the index of the first value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (<code>-1</code>).</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @param tolerance tolerance of the search
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final double[] array, final double valueToFind, int startIndex, final double tolerance) {
        if (ArrayUtils.isEmpty(array)) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i < array.length; i++) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be <code>null</code>
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final double[] array, final double valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value within a given tolerance in the array.
     * This method will return the index of the last value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param tolerance tolerance of the search
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final double[] array, final double valueToFind, final double tolerance) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} (<code>-1</code>). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final double[] array, final double valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return ArrayUtils.INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.
     * This method will return the index of the last value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} (<code>-1</code>). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @param tolerance  search for value within plus/minus this amount
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final double[] array, final double valueToFind, int startIndex, final double tolerance) {
        if (ArrayUtils.isEmpty(array)) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return ArrayUtils.INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i >= 0; i--) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return <code>true</code> if the array contains the object
     */
    public static boolean contains(final double[] array, final double valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if a value falling within the given tolerance is in the
     * given array.  If the array contains a value within the inclusive range
     * defined by (value - tolerance) to (value + tolerance).</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array
     * is passed in.</p>
     *
     * @param array  the array to search
     * @param valueToFind  the value to find
     * @param tolerance  the array contains the tolerance of the search
     * @return true if value falling within tolerance is in array
     */
    public static boolean contains(final double[] array, final double valueToFind, final double tolerance) {
        return ArrayUtils.indexOf(array, valueToFind, 0, tolerance) != ArrayUtils.INDEX_NOT_FOUND;
    }
    // float IndexOf
    //-----------------------------------------------------------------------

    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final float[] array, final float valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (<code>-1</code>).</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final float[] array, final float valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be <code>null</code>
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final float[] array, final float valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} (<code>-1</code>). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final float[] array, final float valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return ArrayUtils.INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return <code>true</code> if the array contains the object
     */
    public static boolean contains(final float[] array, final float valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != ArrayUtils.INDEX_NOT_FOUND;
    }
    // boolean IndexOf
    //-----------------------------------------------------------------------

    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int indexOf(final boolean[] array, final boolean valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} (<code>-1</code>).</p>
     *
     * @param array  the array to search through for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code>
     *  array input
     */
    public static int indexOf(final boolean[] array, final boolean valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) if
     * <code>null</code> array input.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be <code>null</code>
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final boolean[] array, final boolean valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} (<code>-1</code>) for a <code>null</code> input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} (<code>-1</code>). A startIndex larger than
     * the array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be <code>null</code>
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} (<code>-1</code>) if not found or <code>null</code> array input
     */
    public static int lastIndexOf(final boolean[] array, final boolean valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return ArrayUtils.INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return ArrayUtils.INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return ArrayUtils.INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return <code>true</code> if the array contains the object
     */
    public static boolean contains(final boolean[] array, final boolean valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != ArrayUtils.INDEX_NOT_FOUND;
    }
    // Primitive/Object array converters
    // ----------------------------------------------------------------------
    // Character array converters
    // ----------------------------------------------------------------------

    /**
     * <p>Converts an array of object Characters to primitives.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Character</code> array, may be <code>null</code>
     * @return a <code>char</code> array, <code>null</code> if null array input
     * @throws NullPointerException if array content is <code>null</code>
     */
    public static char[] toPrimitive(final Character[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        final char[] result = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].charValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of object Character to primitives handling <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Character</code> array, may be <code>null</code>
     * @param valueForNull  the value to insert if <code>null</code> found
     * @return a <code>char</code> array, <code>null</code> if null array input
     */
    public static char[] toPrimitive(final Character[] array, final char valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        final char[] result = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            Character b = array[i];
            result[i] = b == null ? valueForNull : b.charValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of primitive chars to objects.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array a <code>char</code> array
     * @return a <code>Character</code> array, <code>null</code> if null array input
     */
    public static Character[] toObject(final char[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY;
        }
        final Character[] result = new Character[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Character(array[i]);
        }
        return result;
    }
    // Long array converters
    // ----------------------------------------------------------------------

    /**
     * <p>Converts an array of object Longs to primitives.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Long</code> array, may be <code>null</code>
     * @return a <code>long</code> array, <code>null</code> if null array input
     * @throws NullPointerException if array content is <code>null</code>
     */
    public static long[] toPrimitive(final Long[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_LONG_ARRAY;
        }
        final long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].longValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of object Long to primitives handling <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Long</code> array, may be <code>null</code>
     * @param valueForNull  the value to insert if <code>null</code> found
     * @return a <code>long</code> array, <code>null</code> if null array input
     */
    public static long[] toPrimitive(final Long[] array, final long valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_LONG_ARRAY;
        }
        final long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            Long b = array[i];
            result[i] = b == null ? valueForNull : b.longValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of primitive longs to objects.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>long</code> array
     * @return a <code>Long</code> array, <code>null</code> if null array input
     */
    public static Long[] toObject(final long[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_LONG_OBJECT_ARRAY;
        }
        final Long[] result = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Long(array[i]);
        }
        return result;
    }
    // Int array converters
    // ----------------------------------------------------------------------

    /**
     * <p>Converts an array of object Integers to primitives.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Integer</code> array, may be <code>null</code>
     * @return an <code>int</code> array, <code>null</code> if null array input
     * @throws NullPointerException if array content is <code>null</code>
     */
    public static int[] toPrimitive(final Integer[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of object Integer to primitives handling <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Integer</code> array, may be <code>null</code>
     * @param valueForNull  the value to insert if <code>null</code> found
     * @return an <code>int</code> array, <code>null</code> if null array input
     */
    public static int[] toPrimitive(final Integer[] array, final int valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            Integer b = array[i];
            result[i] = b == null ? valueForNull : b.intValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of primitive ints to objects.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  an <code>int</code> array
     * @return an <code>Integer</code> array, <code>null</code> if null array input
     */
    public static Integer[] toObject(final int[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
        }
        final Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Integer(array[i]);
        }
        return result;
    }
    // Short array converters
    // ----------------------------------------------------------------------

    /**
     * <p>Converts an array of object Shorts to primitives.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Short</code> array, may be <code>null</code>
     * @return a <code>byte</code> array, <code>null</code> if null array input
     * @throws NullPointerException if array content is <code>null</code>
     */
    public static short[] toPrimitive(final Short[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_SHORT_ARRAY;
        }
        final short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].shortValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of object Short to primitives handling <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Short</code> array, may be <code>null</code>
     * @param valueForNull  the value to insert if <code>null</code> found
     * @return a <code>byte</code> array, <code>null</code> if null array input
     */
    public static short[] toPrimitive(final Short[] array, final short valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_SHORT_ARRAY;
        }
        final short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            Short b = array[i];
            result[i] = b == null ? valueForNull : b.shortValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of primitive shorts to objects.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>short</code> array
     * @return a <code>Short</code> array, <code>null</code> if null array input
     */
    public static Short[] toObject(final short[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY;
        }
        final Short[] result = new Short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Short(array[i]);
        }
        return result;
    }
    // Byte array converters
    // ----------------------------------------------------------------------

    /**
     * <p>Converts an array of object Bytes to primitives.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Byte</code> array, may be <code>null</code>
     * @return a <code>byte</code> array, <code>null</code> if null array input
     * @throws NullPointerException if array content is <code>null</code>
     */
    public static byte[] toPrimitive(final Byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of object Bytes to primitives handling <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Byte</code> array, may be <code>null</code>
     * @param valueForNull  the value to insert if <code>null</code> found
     * @return a <code>byte</code> array, <code>null</code> if null array input
     */
    public static byte[] toPrimitive(final Byte[] array, final byte valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            Byte b = array[i];
            result[i] = b == null ? valueForNull : b.byteValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of primitive bytes to objects.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>byte</code> array
     * @return a <code>Byte</code> array, <code>null</code> if null array input
     */
    public static Byte[] toObject(final byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY;
        }
        final Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Byte(array[i]);
        }
        return result;
    }
    // Double array converters
    // ----------------------------------------------------------------------

    /**
     * <p>Converts an array of object Doubles to primitives.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Double</code> array, may be <code>null</code>
     * @return a <code>double</code> array, <code>null</code> if null array input
     * @throws NullPointerException if array content is <code>null</code>
     */
    public static double[] toPrimitive(final Double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of object Doubles to primitives handling <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Double</code> array, may be <code>null</code>
     * @param valueForNull  the value to insert if <code>null</code> found
     * @return a <code>double</code> array, <code>null</code> if null array input
     */
    public static double[] toPrimitive(final Double[] array, final double valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            Double b = array[i];
            result[i] = b == null ? valueForNull : b.doubleValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of primitive doubles to objects.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>double</code> array
     * @return a <code>Double</code> array, <code>null</code> if null array input
     */
    public static Double[] toObject(final double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY;
        }
        final Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Double(array[i]);
        }
        return result;
    }
    //   Float array converters
    // ----------------------------------------------------------------------

    /**
     * <p>Converts an array of object Floats to primitives.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Float</code> array, may be <code>null</code>
     * @return a <code>float</code> array, <code>null</code> if null array input
     * @throws NullPointerException if array content is <code>null</code>
     */
    public static float[] toPrimitive(final Float[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_FLOAT_ARRAY;
        }
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of object Floats to primitives handling <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Float</code> array, may be <code>null</code>
     * @param valueForNull  the value to insert if <code>null</code> found
     * @return a <code>float</code> array, <code>null</code> if null array input
     */
    public static float[] toPrimitive(final Float[] array, final float valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_FLOAT_ARRAY;
        }
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            Float b = array[i];
            result[i] = b == null ? valueForNull : b.floatValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of primitive floats to objects.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>float</code> array
     * @return a <code>Float</code> array, <code>null</code> if null array input
     */
    public static Float[] toObject(final float[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY;
        }
        final Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Float(array[i]);
        }
        return result;
    }
    // Boolean array converters
    // ----------------------------------------------------------------------

    /**
     * <p>Converts an array of object Booleans to primitives.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Boolean</code> array, may be <code>null</code>
     * @return a <code>boolean</code> array, <code>null</code> if null array input
     * @throws NullPointerException if array content is <code>null</code>
     */
    public static boolean[] toPrimitive(final Boolean[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_BOOLEAN_ARRAY;
        }
        final boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].booleanValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of object Booleans to primitives handling <code>null</code>.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>Boolean</code> array, may be <code>null</code>
     * @param valueForNull  the value to insert if <code>null</code> found
     * @return a <code>boolean</code> array, <code>null</code> if null array input
     */
    public static boolean[] toPrimitive(final Boolean[] array, final boolean valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_BOOLEAN_ARRAY;
        }
        final boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            Boolean b = array[i];
            result[i] = b == null ? valueForNull : b.booleanValue();
        }
        return result;
    }

    /**
     * <p>Converts an array of primitive booleans to objects.</p>
     *
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     *
     * @param array  a <code>boolean</code> array
     * @return a <code>Boolean</code> array, <code>null</code> if null array input
     */
    public static Boolean[] toObject(final boolean[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY;
        }
        final Boolean[] result = new Boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i] ? Boolean.TRUE : Boolean.FALSE;
        }
        return result;
    }
    // ----------------------------------------------------------------------

    /**
     * <p>Checks if an array of Objects is empty or <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is empty or <code>null</code>
     * @since 2.1
     */
    public static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive longs is empty or <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is empty or <code>null</code>
     * @since 2.1
     */
    public static boolean isEmpty(final long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive ints is empty or <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is empty or <code>null</code>
     * @since 2.1
     */
    public static boolean isEmpty(final int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive shorts is empty or <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is empty or <code>null</code>
     * @since 2.1
     */
    public static boolean isEmpty(final short[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive chars is empty or <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is empty or <code>null</code>
     * @since 2.1
     */
    public static boolean isEmpty(final char[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive bytes is empty or <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is empty or <code>null</code>
     * @since 2.1
     */
    public static boolean isEmpty(final byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive doubles is empty or <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is empty or <code>null</code>
     * @since 2.1
     */
    public static boolean isEmpty(final double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive floats is empty or <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is empty or <code>null</code>
     * @since 2.1
     */
    public static boolean isEmpty(final float[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive booleans is empty or <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is empty or <code>null</code>
     * @since 2.1
     */
    public static boolean isEmpty(final boolean[] array) {
        return array == null || array.length == 0;
    }
    // ----------------------------------------------------------------------

    /**
     * <p>Checks if an array of Objects is not empty or not <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is not empty or not <code>null</code>
     * @since 2.5
     */
    public static boolean isNotEmpty(final Object[] array) {
        return array != null && array.length != 0;
    }

    /**
     * <p>Checks if an array of primitive longs is not empty or not <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is not empty or not <code>null</code>
     * @since 2.5
     */
    public static boolean isNotEmpty(final long[] array) {
        return array != null && array.length != 0;
    }

    /**
     * <p>Checks if an array of primitive ints is not empty or not <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is not empty or not <code>null</code>
     * @since 2.5
     */
    public static boolean isNotEmpty(final int[] array) {
        return array != null && array.length != 0;
    }

    /**
     * <p>Checks if an array of primitive shorts is not empty or not <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is not empty or not <code>null</code>
     * @since 2.5
     */
    public static boolean isNotEmpty(final short[] array) {
        return array != null && array.length != 0;
    }

    /**
     * <p>Checks if an array of primitive chars is not empty or not <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is not empty or not <code>null</code>
     * @since 2.5
     */
    public static boolean isNotEmpty(final char[] array) {
        return array != null && array.length != 0;
    }

    /**
     * <p>Checks if an array of primitive bytes is not empty or not <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is not empty or not <code>null</code>
     * @since 2.5
     */
    public static boolean isNotEmpty(final byte[] array) {
        return array != null && array.length != 0;
    }

    /**
     * <p>Checks if an array of primitive doubles is not empty or not <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is not empty or not <code>null</code>
     * @since 2.5
     */
    public static boolean isNotEmpty(final double[] array) {
        return array != null && array.length != 0;
    }

    /**
     * <p>Checks if an array of primitive floats is not empty or not <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is not empty or not <code>null</code>
     * @since 2.5
     */
    public static boolean isNotEmpty(final float[] array) {
        return array != null && array.length != 0;
    }

    /**
     * <p>Checks if an array of primitive booleans is not empty or not <code>null</code>.</p>
     *
     * @param array  the array to test
     * @return <code>true</code> if the array is not empty or not <code>null</code>
     * @since 2.5
     */
    public static boolean isNotEmpty(final boolean[] array) {
        return array != null && array.length != 0;
    }

    /**
     * <p>Adds all the elements of the given arrays into a new array.</p>
     * <p>The new array contains all of the element of <code>array1</code> followed
     * by all of the elements <code>array2</code>. When an array is returned, it is always
     * a new array.</p>
     *
     * <pre>
     * ArrayUtils.addAll(null, null)     = null
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * ArrayUtils.addAll([null], [null]) = [null, null]
     * ArrayUtils.addAll(["a", "b", "c"], ["1", "2", "3"]) = ["a", "b", "c", "1", "2", "3"]
     * </pre>
     *
     * @param array1  the first array whose elements are added to the new array, may be <code>null</code>
     * @param array2  the second array whose elements are added to the new array, may be <code>null</code>
     * @return The new array, <code>null</code> if both arrays are <code>null</code>.
     *      The type of the new array is the type of the first array,
     *      unless the first array is null, in which case the type is the same as the second array.
     * @since 2.1
     * @throws IllegalArgumentException if the array types are incompatible
     */
    public static Object[] addAll(final Object[] array1, final Object[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        } else if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        Object[] joinedArray = (Object[]) Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        try {
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        } catch (ArrayStoreException ase) {
            // Check if problem was due to incompatible types
            /*
             * We do this here, rather than before the copy because:
             * - it would be a wasted check most of the time
             * - safer, in case check turns out to be too strict
             */
            final Class<?> type1 = array1.getClass().getComponentType();
            final Class<?> type2 = array2.getClass().getComponentType();
            if (!type1.isAssignableFrom(type2)) {
                throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName());
            }
            throw ase; // No, so rethrow original
        }
        return joinedArray;
    }

    /**
     * <p>Adds all the elements of the given arrays into a new array.</p>
     * <p>The new array contains all of the element of <code>array1</code> followed
     * by all of the elements <code>array2</code>. When an array is returned, it is always
     * a new array.</p>
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1  the first array whose elements are added to the new array.
     * @param array2  the second array whose elements are added to the new array.
     * @return The new boolean[] array.
     * @since 2.1
     */
    public static boolean[] addAll(final boolean[] array1, final boolean[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        } else if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        boolean[] joinedArray = new boolean[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * <p>Adds all the elements of the given arrays into a new array.</p>
     * <p>The new array contains all of the element of <code>array1</code> followed
     * by all of the elements <code>array2</code>. When an array is returned, it is always
     * a new array.</p>
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1  the first array whose elements are added to the new array.
     * @param array2  the second array whose elements are added to the new array.
     * @return The new char[] array.
     * @since 2.1
     */
    public static char[] addAll(final char[] array1, final char[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        } else if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        char[] joinedArray = new char[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * <p>Adds all the elements of the given arrays into a new array.</p>
     * <p>The new array contains all of the element of <code>array1</code> followed
     * by all of the elements <code>array2</code>. When an array is returned, it is always
     * a new array.</p>
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1  the first array whose elements are added to the new array.
     * @param array2  the second array whose elements are added to the new array.
     * @return The new byte[] array.
     * @since 2.1
     */
    public static byte[] addAll(final byte[] array1, final byte[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        } else if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * <p>Adds all the elements of the given arrays into a new array.</p>
     * <p>The new array contains all of the element of <code>array1</code> followed
     * by all of the elements <code>array2</code>. When an array is returned, it is always
     * a new array.</p>
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1  the first array whose elements are added to the new array.
     * @param array2  the second array whose elements are added to the new array.
     * @return The new short[] array.
     * @since 2.1
     */
    public static short[] addAll(final short[] array1, final short[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        } else if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        short[] joinedArray = new short[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * <p>Adds all the elements of the given arrays into a new array.</p>
     * <p>The new array contains all of the element of <code>array1</code> followed
     * by all of the elements <code>array2</code>. When an array is returned, it is always
     * a new array.</p>
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1  the first array whose elements are added to the new array.
     * @param array2  the second array whose elements are added to the new array.
     * @return The new int[] array.
     * @since 2.1
     */
    public static int[] addAll(final int[] array1, final int[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        } else if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        int[] joinedArray = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * <p>Adds all the elements of the given arrays into a new array.</p>
     * <p>The new array contains all of the element of <code>array1</code> followed
     * by all of the elements <code>array2</code>. When an array is returned, it is always
     * a new array.</p>
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1  the first array whose elements are added to the new array.
     * @param array2  the second array whose elements are added to the new array.
     * @return The new long[] array.
     * @since 2.1
     */
    public static long[] addAll(final long[] array1, final long[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        } else if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        long[] joinedArray = new long[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * <p>Adds all the elements of the given arrays into a new array.</p>
     * <p>The new array contains all of the element of <code>array1</code> followed
     * by all of the elements <code>array2</code>. When an array is returned, it is always
     * a new array.</p>
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1  the first array whose elements are added to the new array.
     * @param array2  the second array whose elements are added to the new array.
     * @return The new float[] array.
     * @since 2.1
     */
    public static float[] addAll(final float[] array1, final float[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        } else if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        float[] joinedArray = new float[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * <p>Adds all the elements of the given arrays into a new array.</p>
     * <p>The new array contains all of the element of <code>array1</code> followed
     * by all of the elements <code>array2</code>. When an array is returned, it is always
     * a new array.</p>
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1  the first array whose elements are added to the new array.
     * @param array2  the second array whose elements are added to the new array.
     * @return The new double[] array.
     * @since 2.1
     */
    public static double[] addAll(final double[] array1, final double[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        } else if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        double[] joinedArray = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.</p>
     *
     * <p>The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element, unless the element itself is null,
     *  in which case the return type is Object[]</p>
     *
     * <pre>
     * ArrayUtils.add(null, null)      = [null]
     * ArrayUtils.add(null, "a")       = ["a"]
     * ArrayUtils.add(["a"], null)     = ["a", null]
     * ArrayUtils.add(["a"], "b")      = ["a", "b"]
     * ArrayUtils.add(["a", "b"], "c") = ["a", "b", "c"]
     * </pre>
     *
     * @param array  the array to "add" the element to, may be <code>null</code>
     * @param element  the object to add, may be <code>null</code>
     * @return A new array containing the existing elements plus the new element
     * The returned array type will be that of the input array (unless null),
     * in which case it will have the same type as the element.
     * @since 2.1
     */
    public static <T> T[] add(final T[] array, final T element) {
        Class<?> type;
        if (array != null) {
            type = array.getClass();
        } else if (element != null) {
            type = element.getClass();
        } else {
            type = Object.class;
        }
        T[] newArray = (T[]) ArrayUtils.copyArrayGrow1(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.</p>
     *
     * <p>The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add(null, true)          = [true]
     * ArrayUtils.add([true], false)       = [true, false]
     * ArrayUtils.add([true, false], true) = [true, false, true]
     * </pre>
     *
     * @param array  the array to copy and add the element to, may be <code>null</code>
     * @param element  the object to add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static boolean[] add(final boolean[] array, final boolean element) {
        boolean[] newArray = (boolean[]) ArrayUtils.copyArrayGrow1(array, Boolean.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.</p>
     *
     * <p>The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array  the array to copy and add the element to, may be <code>null</code>
     * @param element  the object to add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static byte[] add(final byte[] array, final byte element) {
        byte[] newArray = (byte[]) ArrayUtils.copyArrayGrow1(array, Byte.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.</p>
     *
     * <p>The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add(null, '0')       = ['0']
     * ArrayUtils.add(['1'], '0')      = ['1', '0']
     * ArrayUtils.add(['1', '0'], '1') = ['1', '0', '1']
     * </pre>
     *
     * @param array  the array to copy and add the element to, may be <code>null</code>
     * @param element  the object to add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static char[] add(final char[] array, final char element) {
        char[] newArray = (char[]) ArrayUtils.copyArrayGrow1(array, Character.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.</p>
     *
     * <p>The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array  the array to copy and add the element to, may be <code>null</code>
     * @param element  the object to add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static double[] add(final double[] array, final double element) {
        double[] newArray = (double[]) ArrayUtils.copyArrayGrow1(array, Double.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.</p>
     *
     * <p>The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array  the array to copy and add the element to, may be <code>null</code>
     * @param element  the object to add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static float[] add(final float[] array, final float element) {
        float[] newArray = (float[]) ArrayUtils.copyArrayGrow1(array, Float.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.</p>
     *
     * <p>The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array  the array to copy and add the element to, may be <code>null</code>
     * @param element  the object to add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static int[] add(final int[] array, final int element) {
        int[] newArray = (int[]) ArrayUtils.copyArrayGrow1(array, Integer.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.</p>
     *
     * <p>The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array  the array to copy and add the element to, may be <code>null</code>
     * @param element  the object to add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static long[] add(final long[] array, final long element) {
        long[] newArray = (long[]) ArrayUtils.copyArrayGrow1(array, Long.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.</p>
     *
     * <p>The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array  the array to copy and add the element to, may be <code>null</code>
     * @param element  the object to add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static short[] add(final short[] array, final short element) {
        short[] newArray = (short[]) ArrayUtils.copyArrayGrow1(array, Short.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * Returns a copy of the given array of size 1 greater than the argument.
     * The last value of the array is left to the default value.
     *
     * @param array The array to copy, must not be <code>null</code>.
     * @param newArrayComponentType If <code>array</code> is <code>null</code>, create a
     * size 1 array of this type.
     * @return A new copy of the array of size 1 greater than the input.
     */
    private static Object copyArrayGrow1(final Object array, final Class<?> newArrayComponentType) {
        if (array != null) {
            int arrayLength = Array.getLength(array);
            Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return Array.newInstance(newArrayComponentType, 1);
    }

    /**
     * <p>Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add(null, 0, null)      = [null]
     * ArrayUtils.add(null, 0, "a")       = ["a"]
     * ArrayUtils.add(["a"], 1, null)     = ["a", null]
     * ArrayUtils.add(["a"], 1, "b")      = ["a", "b"]
     * ArrayUtils.add(["a", "b"], 3, "c") = ["a", "b", "c"]
     * </pre>
     *
     * @param array  the array to add the element to, may be <code>null</code>
     * @param index  the position of the new object
     * @param element  the object to add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > array.length).
     */
    public static Object[] add(final Object[] array, final int index, final Object element) {
        Class<?> clss = null;
        if (array != null) {
            clss = array.getClass().getComponentType();
        } else if (element != null) {
            clss = element.getClass();
        } else {
            return new Object[] { null };
        }
        return (Object[]) ArrayUtils.add(array, index, element, clss);
    }

    /**
     * <p>Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add(null, 0, true)          = [true]
     * ArrayUtils.add([true], 0, false)       = [false, true]
     * ArrayUtils.add([false], 1, true)       = [false, true]
     * ArrayUtils.add([true, false], 1, true) = [true, true, false]
     * </pre>
     *
     * @param array  the array to add the element to, may be <code>null</code>
     * @param index  the position of the new object
     * @param element  the object to add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index > array.length).
     */
    public static boolean[] add(final boolean[] array, final int index, final boolean element) {
        Boolean booElement = element ? Boolean.TRUE : Boolean.FALSE;
        return (boolean[]) ArrayUtils.add(array, index, booElement, Boolean.TYPE);
    }

    /**
     * <p>Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add(null, 0, 'a')            = ['a']
     * ArrayUtils.add(['a'], 0, 'b')           = ['b', 'a']
     * ArrayUtils.add(['a', 'b'], 0, 'c')      = ['c', 'a', 'b']
     * ArrayUtils.add(['a', 'b'], 1, 'k')      = ['a', 'k', 'b']
     * ArrayUtils.add(['a', 'b', 'c'], 1, 't') = ['a', 't', 'b', 'c']
     * </pre>
     *
     * @param array  the array to add the element to, may be <code>null</code>
     * @param index  the position of the new object
     * @param element  the object to add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index > array.length).
     */
    public static char[] add(final char[] array, final int index, final char element) {
        return (char[]) ArrayUtils.add(array, index, new Character(element), Character.TYPE);
    }

    /**
     * <p>Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add([1], 0, 2)         = [2, 1]
     * ArrayUtils.add([2, 6], 2, 3)      = [2, 6, 3]
     * ArrayUtils.add([2, 6], 0, 1)      = [1, 2, 6]
     * ArrayUtils.add([2, 6, 3], 2, 1)   = [2, 6, 1, 3]
     * </pre>
     *
     * @param array  the array to add the element to, may be <code>null</code>
     * @param index  the position of the new object
     * @param element  the object to add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index > array.length).
     */
    public static byte[] add(final byte[] array, final int index, final byte element) {
        return (byte[]) ArrayUtils.add(array, index, new Byte(element), Byte.TYPE);
    }

    /**
     * <p>Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add([1], 0, 2)         = [2, 1]
     * ArrayUtils.add([2, 6], 2, 10)     = [2, 6, 10]
     * ArrayUtils.add([2, 6], 0, -4)     = [-4, 2, 6]
     * ArrayUtils.add([2, 6, 3], 2, 1)   = [2, 6, 1, 3]
     * </pre>
     *
     * @param array  the array to add the element to, may be <code>null</code>
     * @param index  the position of the new object
     * @param element  the object to add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index > array.length).
     */
    public static short[] add(final short[] array, final int index, final short element) {
        return (short[]) ArrayUtils.add(array, index, new Short(element), Short.TYPE);
    }

    /**
     * <p>Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add([1], 0, 2)         = [2, 1]
     * ArrayUtils.add([2, 6], 2, 10)     = [2, 6, 10]
     * ArrayUtils.add([2, 6], 0, -4)     = [-4, 2, 6]
     * ArrayUtils.add([2, 6, 3], 2, 1)   = [2, 6, 1, 3]
     * </pre>
     *
     * @param array  the array to add the element to, may be <code>null</code>
     * @param index  the position of the new object
     * @param element  the object to add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index > array.length).
     */
    public static int[] add(final int[] array, final int index, final int element) {
        return (int[]) ArrayUtils.add(array, index, new Integer(element), Integer.TYPE);
    }

    /**
     * <p>Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add([1L], 0, 2L)           = [2L, 1L]
     * ArrayUtils.add([2L, 6L], 2, 10L)      = [2L, 6L, 10L]
     * ArrayUtils.add([2L, 6L], 0, -4L)      = [-4L, 2L, 6L]
     * ArrayUtils.add([2L, 6L, 3L], 2, 1L)   = [2L, 6L, 1L, 3L]
     * </pre>
     *
     * @param array  the array to add the element to, may be <code>null</code>
     * @param index  the position of the new object
     * @param element  the object to add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index > array.length).
     */
    public static long[] add(final long[] array, final int index, final long element) {
        return (long[]) ArrayUtils.add(array, index, new Long(element), Long.TYPE);
    }

    /**
     * <p>Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add([1.1f], 0, 2.2f)               = [2.2f, 1.1f]
     * ArrayUtils.add([2.3f, 6.4f], 2, 10.5f)        = [2.3f, 6.4f, 10.5f]
     * ArrayUtils.add([2.6f, 6.7f], 0, -4.8f)        = [-4.8f, 2.6f, 6.7f]
     * ArrayUtils.add([2.9f, 6.0f, 0.3f], 2, 1.0f)   = [2.9f, 6.0f, 1.0f, 0.3f]
     * </pre>
     *
     * @param array  the array to add the element to, may be <code>null</code>
     * @param index  the position of the new object
     * @param element  the object to add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index > array.length).
     */
    public static float[] add(final float[] array, final int index, final float element) {
        return (float[]) ArrayUtils.add(array, index, new Float(element), Float.TYPE);
    }

    /**
     * <p>Inserts the specified element at the specified position in the array.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array plus the given element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, a new one element array is returned
     *  whose component type is the same as the element.</p>
     *
     * <pre>
     * ArrayUtils.add([1.1], 0, 2.2)              = [2.2, 1.1]
     * ArrayUtils.add([2.3, 6.4], 2, 10.5)        = [2.3, 6.4, 10.5]
     * ArrayUtils.add([2.6, 6.7], 0, -4.8)        = [-4.8, 2.6, 6.7]
     * ArrayUtils.add([2.9, 6.0, 0.3], 2, 1.0)    = [2.9, 6.0, 1.0, 0.3]
     * </pre>
     *
     * @param array  the array to add the element to, may be <code>null</code>
     * @param index  the position of the new object
     * @param element  the object to add
     * @return A new array containing the existing elements and the new element
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index > array.length).
     */
    public static double[] add(final double[] array, final int index, final double element) {
        return (double[]) ArrayUtils.add(array, index, new Double(element), Double.TYPE);
    }

    /**
     * Underlying implementation of add(array, index, element) methods.
     * The last parameter is the class, which may not equal element.getClass
     * for primitives.
     *
     * @param array  the array to add the element to, may be <code>null</code>
     * @param index  the position of the new object
     * @param element  the object to add
     * @param clss the type of the element being added
     * @return A new array containing the existing elements and the new element
     */
    private static Object add(final Object array, final int index, final Object element, final Class<?> clss) {
        if (array == null) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
            }
            Object joinedArray = Array.newInstance(clss, 1);
            Array.set(joinedArray, 0, element);
            return joinedArray;
        }
        int length = Array.getLength(array);
        if (index > length || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        Object result = Array.newInstance(clss, length + 1);
        System.arraycopy(array, 0, result, 0, index);
        Array.set(result, index, element);
        if (index < length) {
            System.arraycopy(array, index, result, index + 1, length - index);
        }
        return result;
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted to the left (substracts one from
     * their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.</p>
     *
     * <pre>
     * ArrayUtils.remove(["a"], 0)           = []
     * ArrayUtils.remove(["a", "b"], 0)      = ["b"]
     * ArrayUtils.remove(["a", "b"], 1)      = ["a"]
     * ArrayUtils.remove(["a", "b", "c"], 1) = ["a", "c"]
     * </pre>
     *
     * @param array  the array to remove the element from, may not be <code>null</code>
     * @param index  the position of the element to be removed
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index >= array.length), or if the array is <code>null</code>.
     * @since 2.1
     */
    public static Object[] remove(final Object[] array, final int index) {
        return (Object[]) ArrayUtils.remove((Object) array, index);
    }

    /**
     * <p>Removes the first occurrence of the specified element from the
     * specified array. All subsequent elements are shifted to the left
     * (substracts one from their indices). If the array doesn't contains
     * such an element, no elements are removed from the array.</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the first occurrence of the specified element. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <pre>
     * ArrayUtils.removeElement(null, "a")            = null
     * ArrayUtils.removeElement([], "a")              = []
     * ArrayUtils.removeElement(["a"], "b")           = ["a"]
     * ArrayUtils.removeElement(["a", "b"], "a")      = ["b"]
     * ArrayUtils.removeElement(["a", "b", "a"], "a") = ["b", "a"]
     * </pre>
     *
     * @param array  the array to remove the element from, may be <code>null</code>
     * @param element  the element to be removed
     * @return A new array containing the existing elements except the first
     *         occurrence of the specified element.
     * @since 2.1
     */
    public static Object[] removeElement(final Object[] array, final Object element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == ArrayUtils.INDEX_NOT_FOUND) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted to the left (substracts one from
     * their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.</p>
     *
     * <pre>
     * ArrayUtils.remove([true], 0)              = []
     * ArrayUtils.remove([true, false], 0)       = [false]
     * ArrayUtils.remove([true, false], 1)       = [true]
     * ArrayUtils.remove([true, true, false], 1) = [true, false]
     * </pre>
     *
     * @param array  the array to remove the element from, may not be <code>null</code>
     * @param index  the position of the element to be removed
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index >= array.length), or if the array is <code>null</code>.
     * @since 2.1
     */
    public static boolean[] remove(final boolean[] array, final int index) {
        return (boolean[]) ArrayUtils.remove((Object) array, index);
    }

    /**
     * <p>Removes the first occurrence of the specified element from the
     * specified array. All subsequent elements are shifted to the left
     * (substracts one from their indices). If the array doesn't contains
     * such an element, no elements are removed from the array.</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the first occurrence of the specified element. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <pre>
     * ArrayUtils.removeElement(null, true)                = null
     * ArrayUtils.removeElement([], true)                  = []
     * ArrayUtils.removeElement([true], false)             = [true]
     * ArrayUtils.removeElement([true, false], false)      = [true]
     * ArrayUtils.removeElement([true, false, true], true) = [false, true]
     * </pre>
     *
     * @param array  the array to remove the element from, may be <code>null</code>
     * @param element  the element to be removed
     * @return A new array containing the existing elements except the first
     *         occurrence of the specified element.
     * @since 2.1
     */
    public static boolean[] removeElement(final boolean[] array, final boolean element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == ArrayUtils.INDEX_NOT_FOUND) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted to the left (substracts one from
     * their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.</p>
     *
     * <pre>
     * ArrayUtils.remove([1], 0)          = []
     * ArrayUtils.remove([1, 0], 0)       = [0]
     * ArrayUtils.remove([1, 0], 1)       = [1]
     * ArrayUtils.remove([1, 0, 1], 1)    = [1, 1]
     * </pre>
     *
     * @param array  the array to remove the element from, may not be <code>null</code>
     * @param index  the position of the element to be removed
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index >= array.length), or if the array is <code>null</code>.
     * @since 2.1
     */
    public static byte[] remove(final byte[] array, final int index) {
        return (byte[]) ArrayUtils.remove((Object) array, index);
    }

    /**
     * <p>Removes the first occurrence of the specified element from the
     * specified array. All subsequent elements are shifted to the left
     * (substracts one from their indices). If the array doesn't contains
     * such an element, no elements are removed from the array.</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the first occurrence of the specified element. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1)        = null
     * ArrayUtils.removeElement([], 1)          = []
     * ArrayUtils.removeElement([1], 0)         = [1]
     * ArrayUtils.removeElement([1, 0], 0)      = [1]
     * ArrayUtils.removeElement([1, 0, 1], 1)   = [0, 1]
     * </pre>
     *
     * @param array  the array to remove the element from, may be <code>null</code>
     * @param element  the element to be removed
     * @return A new array containing the existing elements except the first
     *         occurrence of the specified element.
     * @since 2.1
     */
    public static byte[] removeElement(final byte[] array, final byte element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == ArrayUtils.INDEX_NOT_FOUND) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted to the left (substracts one from
     * their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.</p>
     *
     * <pre>
     * ArrayUtils.remove(['a'], 0)           = []
     * ArrayUtils.remove(['a', 'b'], 0)      = ['b']
     * ArrayUtils.remove(['a', 'b'], 1)      = ['a']
     * ArrayUtils.remove(['a', 'b', 'c'], 1) = ['a', 'c']
     * </pre>
     *
     * @param array  the array to remove the element from, may not be <code>null</code>
     * @param index  the position of the element to be removed
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index >= array.length), or if the array is <code>null</code>.
     * @since 2.1
     */
    public static char[] remove(final char[] array, final int index) {
        return (char[]) ArrayUtils.remove((Object) array, index);
    }

    /**
     * <p>Removes the first occurrence of the specified element from the
     * specified array. All subsequent elements are shifted to the left
     * (substracts one from their indices). If the array doesn't contains
     * such an element, no elements are removed from the array.</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the first occurrence of the specified element. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <pre>
     * ArrayUtils.removeElement(null, 'a')            = null
     * ArrayUtils.removeElement([], 'a')              = []
     * ArrayUtils.removeElement(['a'], 'b')           = ['a']
     * ArrayUtils.removeElement(['a', 'b'], 'a')      = ['b']
     * ArrayUtils.removeElement(['a', 'b', 'a'], 'a') = ['b', 'a']
     * </pre>
     *
     * @param array  the array to remove the element from, may be <code>null</code>
     * @param element  the element to be removed
     * @return A new array containing the existing elements except the first
     *         occurrence of the specified element.
     * @since 2.1
     */
    public static char[] removeElement(final char[] array, final char element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == ArrayUtils.INDEX_NOT_FOUND) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted to the left (substracts one from
     * their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.</p>
     *
     * <pre>
     * ArrayUtils.remove([1.1], 0)           = []
     * ArrayUtils.remove([2.5, 6.0], 0)      = [6.0]
     * ArrayUtils.remove([2.5, 6.0], 1)      = [2.5]
     * ArrayUtils.remove([2.5, 6.0, 3.8], 1) = [2.5, 3.8]
     * </pre>
     *
     * @param array  the array to remove the element from, may not be <code>null</code>
     * @param index  the position of the element to be removed
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index >= array.length), or if the array is <code>null</code>.
     * @since 2.1
     */
    public static double[] remove(final double[] array, final int index) {
        return (double[]) ArrayUtils.remove((Object) array, index);
    }

    /**
     * <p>Removes the first occurrence of the specified element from the
     * specified array. All subsequent elements are shifted to the left
     * (substracts one from their indices). If the array doesn't contains
     * such an element, no elements are removed from the array.</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the first occurrence of the specified element. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1.1)            = null
     * ArrayUtils.removeElement([], 1.1)              = []
     * ArrayUtils.removeElement([1.1], 1.2)           = [1.1]
     * ArrayUtils.removeElement([1.1, 2.3], 1.1)      = [2.3]
     * ArrayUtils.removeElement([1.1, 2.3, 1.1], 1.1) = [2.3, 1.1]
     * </pre>
     *
     * @param array  the array to remove the element from, may be <code>null</code>
     * @param element  the element to be removed
     * @return A new array containing the existing elements except the first
     *         occurrence of the specified element.
     * @since 2.1
     */
    public static double[] removeElement(final double[] array, final double element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == ArrayUtils.INDEX_NOT_FOUND) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted to the left (substracts one from
     * their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.</p>
     *
     * <pre>
     * ArrayUtils.remove([1.1], 0)           = []
     * ArrayUtils.remove([2.5, 6.0], 0)      = [6.0]
     * ArrayUtils.remove([2.5, 6.0], 1)      = [2.5]
     * ArrayUtils.remove([2.5, 6.0, 3.8], 1) = [2.5, 3.8]
     * </pre>
     *
     * @param array  the array to remove the element from, may not be <code>null</code>
     * @param index  the position of the element to be removed
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index >= array.length), or if the array is <code>null</code>.
     * @since 2.1
     */
    public static float[] remove(final float[] array, final int index) {
        return (float[]) ArrayUtils.remove((Object) array, index);
    }

    /**
     * <p>Removes the first occurrence of the specified element from the
     * specified array. All subsequent elements are shifted to the left
     * (substracts one from their indices). If the array doesn't contains
     * such an element, no elements are removed from the array.</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the first occurrence of the specified element. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1.1)            = null
     * ArrayUtils.removeElement([], 1.1)              = []
     * ArrayUtils.removeElement([1.1], 1.2)           = [1.1]
     * ArrayUtils.removeElement([1.1, 2.3], 1.1)      = [2.3]
     * ArrayUtils.removeElement([1.1, 2.3, 1.1], 1.1) = [2.3, 1.1]
     * </pre>
     *
     * @param array  the array to remove the element from, may be <code>null</code>
     * @param element  the element to be removed
     * @return A new array containing the existing elements except the first
     *         occurrence of the specified element.
     * @since 2.1
     */
    public static float[] removeElement(final float[] array, final float element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == ArrayUtils.INDEX_NOT_FOUND) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted to the left (substracts one from
     * their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.</p>
     *
     * <pre>
     * ArrayUtils.remove([1], 0)         = []
     * ArrayUtils.remove([2, 6], 0)      = [6]
     * ArrayUtils.remove([2, 6], 1)      = [2]
     * ArrayUtils.remove([2, 6, 3], 1)   = [2, 3]
     * </pre>
     *
     * @param array  the array to remove the element from, may not be <code>null</code>
     * @param index  the position of the element to be removed
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index >= array.length), or if the array is <code>null</code>.
     * @since 2.1
     */
    public static int[] remove(final int[] array, final int index) {
        return (int[]) ArrayUtils.remove((Object) array, index);
    }

    /**
     * <p>Removes the first occurrence of the specified element from the
     * specified array. All subsequent elements are shifted to the left
     * (substracts one from their indices). If the array doesn't contains
     * such an element, no elements are removed from the array.</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the first occurrence of the specified element. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1)      = null
     * ArrayUtils.removeElement([], 1)        = []
     * ArrayUtils.removeElement([1], 2)       = [1]
     * ArrayUtils.removeElement([1, 3], 1)    = [3]
     * ArrayUtils.removeElement([1, 3, 1], 1) = [3, 1]
     * </pre>
     *
     * @param array  the array to remove the element from, may be <code>null</code>
     * @param element  the element to be removed
     * @return A new array containing the existing elements except the first
     *         occurrence of the specified element.
     * @since 2.1
     */
    public static int[] removeElement(final int[] array, final int element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == ArrayUtils.INDEX_NOT_FOUND) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted to the left (substracts one from
     * their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.</p>
     *
     * <pre>
     * ArrayUtils.remove([1], 0)         = []
     * ArrayUtils.remove([2, 6], 0)      = [6]
     * ArrayUtils.remove([2, 6], 1)      = [2]
     * ArrayUtils.remove([2, 6, 3], 1)   = [2, 3]
     * </pre>
     *
     * @param array  the array to remove the element from, may not be <code>null</code>
     * @param index  the position of the element to be removed
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index >= array.length), or if the array is <code>null</code>.
     * @since 2.1
     */
    public static long[] remove(final long[] array, final int index) {
        return (long[]) ArrayUtils.remove((Object) array, index);
    }

    /**
     * <p>Removes the first occurrence of the specified element from the
     * specified array. All subsequent elements are shifted to the left
     * (substracts one from their indices). If the array doesn't contains
     * such an element, no elements are removed from the array.</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the first occurrence of the specified element. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1)      = null
     * ArrayUtils.removeElement([], 1)        = []
     * ArrayUtils.removeElement([1], 2)       = [1]
     * ArrayUtils.removeElement([1, 3], 1)    = [3]
     * ArrayUtils.removeElement([1, 3, 1], 1) = [3, 1]
     * </pre>
     *
     * @param array  the array to remove the element from, may be <code>null</code>
     * @param element  the element to be removed
     * @return A new array containing the existing elements except the first
     *         occurrence of the specified element.
     * @since 2.1
     */
    public static long[] removeElement(final long[] array, final long element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == ArrayUtils.INDEX_NOT_FOUND) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted to the left (substracts one from
     * their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.</p>
     *
     * <pre>
     * ArrayUtils.remove([1], 0)         = []
     * ArrayUtils.remove([2, 6], 0)      = [6]
     * ArrayUtils.remove([2, 6], 1)      = [2]
     * ArrayUtils.remove([2, 6, 3], 1)   = [2, 3]
     * </pre>
     *
     * @param array  the array to remove the element from, may not be <code>null</code>
     * @param index  the position of the element to be removed
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index >= array.length), or if the array is <code>null</code>.
     * @since 2.1
     */
    public static short[] remove(final short[] array, final int index) {
        return (short[]) ArrayUtils.remove((Object) array, index);
    }

    /**
     * <p>Removes the first occurrence of the specified element from the
     * specified array. All subsequent elements are shifted to the left
     * (substracts one from their indices). If the array doesn't contains
     * such an element, no elements are removed from the array.</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the first occurrence of the specified element. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1)      = null
     * ArrayUtils.removeElement([], 1)        = []
     * ArrayUtils.removeElement([1], 2)       = [1]
     * ArrayUtils.removeElement([1, 3], 1)    = [3]
     * ArrayUtils.removeElement([1, 3, 1], 1) = [3, 1]
     * </pre>
     *
     * @param array  the array to remove the element from, may be <code>null</code>
     * @param element  the element to be removed
     * @return A new array containing the existing elements except the first
     *         occurrence of the specified element.
     * @since 2.1
     */
    public static short[] removeElement(final short[] array, final short element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == ArrayUtils.INDEX_NOT_FOUND) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * All subsequent elements are shifted to the left (substracts one from
     * their indices).</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     *
     * <p>If the input array is <code>null</code>, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.</p>
     *
     * @param array  the array to remove the element from, may not be <code>null</code>
     * @param index  the position of the element to be removed
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index < 0 || index >= array.length), or if the array is <code>null</code>.
     * @since 2.1
     */
    private static Object remove(final Object array, final int index) {
        int length = ArrayUtils.getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }
        return result;
    }

    /**删除数组中空元素*/
    public static Object[] clearNull(final Object[] arr) {
        ArrayList<Object> list = new ArrayList<Object>();
        if (arr != null) {
            for (Object item : arr) {
                if (item != null) {
                    list.add(item);
                }
            }
        }
        return list.toArray();
    }
}