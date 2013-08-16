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
package org.more.convert;
/**
 * <p>Utility methods for converting String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class.</p>
 *
 * <p>For more details, see <code>ConvertUtilsBean</code> which provides the
 * implementations for these methods.</p>
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @version $Revision: 556229 $ $Date: 2007-07-14 07:11:19 +0100 (Sat, 14 Jul 2007) $
 * @see ConvertUtilsBean
 */
@SuppressWarnings("rawtypes")
public class ConverterUtils {
    // --------------------------------------------------------- Public Classes
    /**
     * <p>Convert the specified value into a String.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param value Value to be converted (may be null)
     * @return The converted String value
     *
     * @see ConvertUtilsBean#convert(Object)
     */
    public static String convert(Object value) {
        return ConverterBean.getInstance().convert(value);
    }
    /**
     * <p>Convert the specified value to an object of the specified class (if
     * possible).  Otherwise, return a String representation of the value.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param value Value to be converted (may be null)
     * @param clazz Java class to be converted to
     * @return The converted value
     *
     * @see ConvertUtilsBean#convert(String, Class)
     */
    public static Object convert(String value, Class clazz) {
        return ConverterBean.getInstance().convert(value, clazz);
    }
    /**
     * <p>Convert an array of specified values to an array of objects of the
     * specified class (if possible).</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param values Array of values to be converted
     * @param clazz Java array or element class to be converted to
     * @return The converted value
     *
     * @see ConvertUtilsBean#convert(String[], Class)
     */
    public static Object convert(String[] values, Class clazz) {
        return ConverterBean.getInstance().convert(values, clazz);
    }
    /**
     * <p>Convert the value to an object of the specified class (if
     * possible).</p>
     *
     * @param value Value to be converted (may be null)
     * @param targetType Class of the value to be converted to
     * @return The converted value
     *
     * @exception ConversionException if thrown by an underlying Converter
     */
    public static Object convert(Class<?> targetType, Object value) {
        return ConverterBean.getInstance().convert(value, targetType);
    }
    /**
     * <p>Remove all registered {@link Converter}s, and re-establish the
     * standard Converters.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @see ConvertUtilsBean#deregister()
     */
    public static void deregister() {
        ConverterBean.getInstance().deregister();
    }
    /**
     * <p>Remove any registered {@link Converter} for the specified destination
     * <code>Class</code>.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param clazz Class for which to remove a registered Converter
     * @see ConvertUtilsBean#deregister(Class)
     */
    public static void deregister(Class clazz) {
        ConverterBean.getInstance().deregister(clazz);
    }
    /**
     * <p>Look up and return any registered {@link Converter} for the specified
     * destination class; if there is no registered Converter, return
     * <code>null</code>.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param clazz Class for which to return a registered Converter
     * @return The registered {@link Converter} or <code>null</code> if not found
     * @see ConvertUtilsBean#lookup(Class)
     */
    public static Converter lookup(Class clazz) {
        return ConverterBean.getInstance().lookup(clazz);
    }
    /**
     * Look up and return any registered {@link Converter} for the specified
     * source and destination class; if there is no registered Converter,
     * return <code>null</code>.
     *
     * @param sourceType Class of the value being converted
     * @param targetType Class of the value to be converted to
     * @return The registered {@link Converter} or <code>null</code> if not found
     */
    public static Converter lookup(Class sourceType, Class targetType) {
        return ConverterBean.getInstance().lookup(sourceType, targetType);
    }
    /**
     * <p>Register a custom {@link Converter} for the specified destination
     * <code>Class</code>, replacing any previously registered Converter.</p>
     *
     * <p>For more details see <code>ConvertUtilsBean</code>.</p>
     *
     * @param converter Converter to be registered
     * @param clazz Destination class for conversions performed by this
     *  Converter
     * @see ConvertUtilsBean#register(Converter, Class)
     */
    public static void register(Converter converter, Class clazz) {
        ConverterBean.getInstance().register(converter, clazz);
    }
}