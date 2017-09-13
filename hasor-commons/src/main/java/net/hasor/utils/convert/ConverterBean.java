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
package net.hasor.utils.convert;
import net.hasor.utils.ContextClassLoaderLocal;
import net.hasor.utils.convert.convert.*;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
/**
 * <p>Utility methods for converting String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class.  The
 * actual {@link Converter} instance to be used can be registered for each
 * possible destination Class.  Unless you override them, standard
 * {@link Converter} instances are provided for all of the following
 * destination Classes:</p>
 * <ul>
 * <li>java.lang.BigDecimal (no default value)</li>
 * <li>java.lang.BigInteger (no default value)</li>
 * <li>boolean and java.lang.Boolean (default to false)</li>
 * <li>byte and java.lang.Byte (default to zero)</li>
 * <li>char and java.lang.Character (default to a space)</li>
 * <li>java.lang.Class (no default value)</li>
 * <li>double and java.lang.Double (default to zero)</li>
 * <li>float and java.lang.Float (default to zero)</li>
 * <li>int and java.lang.Integer (default to zero)</li>
 * <li>long and java.lang.Long (default to zero)</li>
 * <li>short and java.lang.Short (default to zero)</li>
 * <li>java.lang.String (default to null)</li>
 * <li>java.io.File (no default value)</li>
 * <li>java.net.URL (no default value)</li>
 * <li>java.sql.Date (no default value)</li>
 * <li>java.sql.Time (no default value)</li>
 * <li>java.sql.Timestamp (no default value)</li>
 * </ul>
 *
 * <p>For backwards compatibility, the standard Converters for primitive
 * types (and the corresponding wrapper classes) return a defined
 * default value when a conversion error occurs.  If you prefer to have a
 * {@link ConversionException} thrown instead, replace the standard Converter
 * instances with instances created with the zero-arguments constructor.  For
 * example, to cause the Converters for integers to throw an exception on
 * conversion errors, you could do this:</p>
 * <pre>
 *   // No-args constructor gets the version that throws exceptions
 *   Converter myConverter =
 *    new org.more.convert.convert.IntegerConverter();
 *   ConverterUtils.register(myConverter, Integer.TYPE);    // Native type
 *   ConverterUtils.register(myConverter, Integer.class);   // Wrapper class
 * </pre>
 *
 * <p>
 * Converters generally treat null input as if it were invalid
 * input, ie they return their default value if one was specified when the
 * converter was constructed, and throw an exception otherwise. If you prefer
 * nulls to be preserved for converters that are converting to objects (not
 * primitives) then register a converter as above, passing a default value of
 * null to the converter constructor (and of course registering that converter
 * only for the .class target).
 * </p>
 *
 * <p>
 * When a converter is listed above as having no default value, then that
 * converter will throw an exception when passed null or an invalid value
 * as its input. In particular, by default the BigInteger and BigDecimal
 * converters have no default (and are therefore somewhat inconsistent
 * with the other numerical converters which all have zero as their default).
 * </p>
 *
 * <p>
 * Converters that generate <i>arrays</i> of each of the primitive types are
 * also automatically configured (including String[]). When passed null
 * or invalid input, these return an empty array (not null). See class
 * AbstractArrayConverter for the supported input formats for these converters.
 * </p>
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author James Strachan
 * @version $Revision: 745079 $ $Date: 2009-02-17 14:04:10 +0000 (Tue, 17 Feb 2009) $
 * @since 1.7
 */
public class ConverterBean {
    private static final Integer                              ZERO                 = new Integer(0);
    private static final Character                            SPACE                = new Character(' ');
    /** Contains <code>ConverterBean</code> instances indexed by context classloader. */
    private static final ConverterBeanContextClassLoaderLocal BEANS_BY_CLASSLOADER = new ConverterBeanContextClassLoaderLocal();
    private static class ConverterBeanContextClassLoaderLocal extends ContextClassLoaderLocal<ConverterBean> {
        @Override
        protected ConverterBean initialValue() {
            return new ConverterBean();
        }
    }
    // ------------------------------------------------------- Class Methods
    /**
     * Get singleton instance
     * @return The singleton instance
     */
    protected static ConverterBean getInstance() {
        return ConverterBean.BEANS_BY_CLASSLOADER.get();
    }
    // ------------------------------------------------------- Variables
    /**
     * The set of {@link Converter}s that can be used to convert Strings
     * into objects of a specified Class, keyed by the destination Class.
     */
    private WeakFastHashMap converters = new WeakFastHashMap();
    // ------------------------------------------------------- Constructors
    /** Construct a bean with standard converters registered */
    public ConverterBean() {
        this.converters.setFast(false);
        this.deregister();
        this.converters.setFast(true);
    }
    // --------------------------------------------------------- Public Methods
    /**
     * Convert the specified value into a String.  If the specified value
     * is an array, the first element (converted to a String) will be
     * returned.  The registered {@link Converter} for the
     * <code>java.lang.String</code> class will be used, which allows
     * applications to customize Object->String conversions (the default
     * implementation simply uses toString()).
     *
     * @param value Value to be converted (may be null)
     * @return The converted String value
     */
    public String convert(Object value) {
        if (value == null) {
            return null;
        } else if (value.getClass().isArray()) {
            if (Array.getLength(value) < 1) {
                return null;
            }
            value = Array.get(value, 0);
            if (value == null) {
                return null;
            } else {
                Converter converter = this.lookup(String.class);
                return (String) converter.convert(String.class, value);
            }
        } else {
            Converter converter = this.lookup(String.class);
            return (String) converter.convert(String.class, value);
        }
    }
    /**
     * Convert the specified value to an object of the specified class (if
     * possible).  Otherwise, return a String representation of the value.
     *
     * @param value Value to be converted (may be null)
     * @param clazz Java class to be converted to
     * @return The converted value
     *
     * @exception ConversionException if thrown by an underlying Converter
     */
    public Object convert(final String value, final Class<?> clazz) {
        Converter converter = this.lookup(clazz);
        if (converter == null) {
            converter = this.lookup(String.class);
        }
        return converter.convert(clazz, value);
    }
    /**
     * Convert an array of specified values to an array of objects of the
     * specified class (if possible).  If the specified Java class is itself
     * an array class, this class will be the type of the returned value.
     * Otherwise, an array will be constructed whose component type is the
     * specified class.
     *
     * @param values Array of values to be converted
     * @param clazz Java array or element class to be converted to
     * @return The converted value
     *
     * @exception ConversionException if thrown by an underlying Converter
     */
    public Object convert(final String[] values, final Class<?> clazz) {
        Class<?> type = clazz;
        if (clazz.isArray()) {
            type = clazz.getComponentType();
        }
        Converter converter = this.lookup(type);
        if (converter == null) {
            converter = this.lookup(String.class);
        }
        Object array = Array.newInstance(type, values.length);
        for (int i = 0; i < values.length; i++) {
            Array.set(array, i, converter.convert(type, values[i]));
        }
        return array;
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
    public Object convert(final Object value, final Class<?> targetType) {
        Class<?> sourceType = value == null ? null : value.getClass();
        Object converted = value;
        Converter converter = this.lookup(sourceType, targetType);
        if (converter != null) {
            converted = converter.convert(targetType, value);
        }
        if (targetType == String.class && converted != null && !(converted instanceof String)) {
            // NOTE: For backwards compatibility, if the Converter
            //       doesn't handle  conversion-->String then
            //       use the registered String Converter
            converter = this.lookup(String.class);
            if (converter != null) {
                converted = converter.convert(String.class, converted);
            }
            // If the object still isn't a String, use toString() method
            if (converted != null && !(converted instanceof String)) {
                converted = converted.toString();
            }
        }
        return converted;
    }
    /**
     * Remove all registered {@link Converter}s, and re-establish the
     * standard Converters.
     */
    public void deregister() {
        this.converters.clear();
        this.registerPrimitives(false);
        this.registerStandard(false, false);
        this.registerOther(true);
        this.registerArrays(false, 0);
        this.register(BigDecimal.class, new BigDecimalConverter());
        this.register(BigInteger.class, new BigIntegerConverter());
    }
    /**
     * Register the provided converters with the specified defaults.
     *
     * @param throwException <code>true</code> if the converters should
     * throw an exception when a conversion error occurs, otherwise <code>
     * <code>false</code> if a default value should be used.
     * @param defaultNull <code>true</code>if the <i>standard</i> converters
     * (see {@link ConverterBean#registerStandard(boolean, boolean)})
     * should use a default value of <code>null</code>, otherwise <code>false</code>.
     * N.B. This values is ignored if <code>throwException</code> is <code>true</code>
     * @param defaultArraySize The size of the default array value for array converters
     * (N.B. This values is ignored if <code>throwException</code> is <code>true</code>).
     * Specifying a value less than zero causes a <code>null<code> value to be used for
     * the default.
     */
    public void register(final boolean throwException, final boolean defaultNull, final int defaultArraySize) {
        this.registerPrimitives(throwException);
        this.registerStandard(throwException, defaultNull);
        this.registerOther(throwException);
        this.registerArrays(throwException, defaultArraySize);
    }
    /**
     * Register the converters for primitive types.
     * </p>
     * This method registers the following converters:
     * <ul>
     *     <li><code>Boolean.TYPE</code> - {@link BooleanConverter}</li>
     *     <li><code>Byte.TYPE</code> - {@link ByteConverter}</li>
     *     <li><code>Character.TYPE</code> - {@link CharacterConverter}</li>
     *     <li><code>Double.TYPE</code> - {@link DoubleConverter}</li>
     *     <li><code>Float.TYPE</code> - {@link FloatConverter}</li>
     *     <li><code>Integer.TYPE</code> - {@link IntegerConverter}</li>
     *     <li><code>Long.TYPE</code> - {@link LongConverter}</li>
     *     <li><code>Short.TYPE</code> - {@link ShortConverter}</li>
     * </ul>
     * @param throwException <code>true</code> if the converters should
     * throw an exception when a conversion error occurs, otherwise <code>
     * <code>false</code> if a default value should be used.
     */
    private void registerPrimitives(final boolean throwException) {
        this.register(Boolean.TYPE, throwException ? new BooleanConverter() : new BooleanConverter(Boolean.FALSE));
        this.register(Byte.TYPE, throwException ? new ByteConverter() : new ByteConverter(ConverterBean.ZERO));
        this.register(Character.TYPE, throwException ? new CharacterConverter() : new CharacterConverter(ConverterBean.SPACE));
        this.register(Double.TYPE, throwException ? new DoubleConverter() : new DoubleConverter(ConverterBean.ZERO));
        this.register(Float.TYPE, throwException ? new FloatConverter() : new FloatConverter(ConverterBean.ZERO));
        this.register(Integer.TYPE, throwException ? new IntegerConverter() : new IntegerConverter(ConverterBean.ZERO));
        this.register(Long.TYPE, throwException ? new LongConverter() : new LongConverter(ConverterBean.ZERO));
        this.register(Short.TYPE, throwException ? new ShortConverter() : new ShortConverter(ConverterBean.ZERO));
    }
    /**
     * Register the converters for standard types.
     * </p>
     * This method registers the following converters:
     * <ul>
     *     <li><code>BigDecimal.class</code> - {@link BigDecimalConverter}</li>
     *     <li><code>BigInteger.class</code> - {@link BigIntegerConverter}</li>
     *     <li><code>Boolean.class</code> - {@link BooleanConverter}</li>
     *     <li><code>Byte.class</code> - {@link ByteConverter}</li>
     *     <li><code>Character.class</code> - {@link CharacterConverter}</li>
     *     <li><code>Double.class</code> - {@link DoubleConverter}</li>
     *     <li><code>Float.class</code> - {@link FloatConverter}</li>
     *     <li><code>Integer.class</code> - {@link IntegerConverter}</li>
     *     <li><code>Long.class</code> - {@link LongConverter}</li>
     *     <li><code>Short.class</code> - {@link ShortConverter}</li>
     *     <li><code>String.class</code> - {@link StringConverter}</li>
     * </ul>
     * @param throwException <code>true</code> if the converters should
     * throw an exception when a conversion error occurs, otherwise <code>
     * <code>false</code> if a default value should be used.
     * @param defaultNull <code>true</code>if the <i>standard</i> converters
     * (see {@link ConverterBean#registerStandard(boolean, boolean)})
     * should use a default value of <code>null</code>, otherwise <code>false</code>.
     * N.B. This values is ignored if <code>throwException</code> is <code>true</code>
     */
    private void registerStandard(final boolean throwException, final boolean defaultNull) {
        Number defaultNumber = defaultNull ? null : ConverterBean.ZERO;
        BigDecimal bigDecDeflt = defaultNull ? null : new BigDecimal("0.0");
        BigInteger bigIntDeflt = defaultNull ? null : new BigInteger("0");
        Boolean booleanDefault = defaultNull ? null : Boolean.FALSE;
        Character charDefault = defaultNull ? null : ConverterBean.SPACE;
        String stringDefault = defaultNull ? null : "";
        this.register(BigDecimal.class, throwException ? new BigDecimalConverter() : new BigDecimalConverter(bigDecDeflt));
        this.register(BigInteger.class, throwException ? new BigIntegerConverter() : new BigIntegerConverter(bigIntDeflt));
        this.register(Boolean.class, throwException ? new BooleanConverter() : new BooleanConverter(booleanDefault));
        this.register(Byte.class, throwException ? new ByteConverter() : new ByteConverter(defaultNumber));
        this.register(Character.class, throwException ? new CharacterConverter() : new CharacterConverter(charDefault));
        this.register(Double.class, throwException ? new DoubleConverter() : new DoubleConverter(defaultNumber));
        this.register(Float.class, throwException ? new FloatConverter() : new FloatConverter(defaultNumber));
        this.register(Integer.class, throwException ? new IntegerConverter() : new IntegerConverter(defaultNumber));
        this.register(Long.class, throwException ? new LongConverter() : new LongConverter(defaultNumber));
        this.register(Short.class, throwException ? new ShortConverter() : new ShortConverter(defaultNumber));
        this.register(String.class, throwException ? new StringConverter() : new StringConverter(stringDefault));
    }
    /**
     * Register the converters for other types.
     * </p>
     * This method registers the following converters:
     * <ul>
     *     <li><code>Class.class</code> - {@link ClassConverter}</li>
     *     <li><code>java.util.Date.class</code> - {@link DateConverter}</li>
     *     <li><code>java.util.Calendar.class</code> - {@link CalendarConverter}</li>
     *     <li><code>File.class</code> - {@link FileConverter}</li>
     *     <li><code>java.sql.Date.class</code> - {@link SqlDateConverter}</li>
     *     <li><code>java.sql.Time.class</code> - {@link SqlTimeConverter}</li>
     *     <li><code>java.sql.Timestamp.class</code> - {@link SqlTimestampConverter}</li>
     *     <li><code>URL.class</code> - {@link URLConverter}</li>
     * </ul>
     * @param throwException <code>true</code> if the converters should
     * throw an exception when a conversion error occurs, otherwise <code>
     * <code>false</code> if a default value should be used.
     */
    private void registerOther(final boolean throwException) {
        this.register(Class.class, throwException ? new ClassConverter() : new ClassConverter(null));
        this.register(java.util.Date.class, throwException ? new DateConverter() : new DateConverter(null));
        this.register(Calendar.class, throwException ? new CalendarConverter() : new CalendarConverter(null));
        this.register(File.class, throwException ? new FileConverter() : new FileConverter(null));
        this.register(java.sql.Date.class, throwException ? new SqlDateConverter() : new SqlDateConverter(null));
        this.register(java.sql.Time.class, throwException ? new SqlTimeConverter() : new SqlTimeConverter(null));
        this.register(Timestamp.class, throwException ? new SqlTimestampConverter() : new SqlTimestampConverter(null));
        this.register(URL.class, throwException ? new URLConverter() : new URLConverter(null));
        this.register(URI.class, throwException ? new URIConverter() : new URIConverter(null));
        this.register(Enum.class, throwException ? new EnumConverter() : new EnumConverter(null));
    }
    /**
     * Register array converters.
     *
     * @param throwException <code>true</code> if the converters should
     * throw an exception when a conversion error occurs, otherwise <code>
     * <code>false</code> if a default value should be used.
     * @param defaultArraySize The size of the default array value for array converters
     * (N.B. This values is ignored if <code>throwException</code> is <code>true</code>).
     * Specifying a value less than zero causes a <code>null<code> value to be used for
     * the default.
     */
    private void registerArrays(final boolean throwException, final int defaultArraySize) {
        // Primitives
        this.registerArrayConverter(Boolean.TYPE, new BooleanConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Byte.TYPE, new ByteConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Character.TYPE, new CharacterConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Double.TYPE, new DoubleConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Float.TYPE, new FloatConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Integer.TYPE, new IntegerConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Long.TYPE, new LongConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Short.TYPE, new ShortConverter(), throwException, defaultArraySize);
        // Standard
        this.registerArrayConverter(BigDecimal.class, new BigDecimalConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(BigInteger.class, new BigIntegerConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Boolean.class, new BooleanConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Byte.class, new ByteConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Character.class, new CharacterConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Double.class, new DoubleConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Float.class, new FloatConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Integer.class, new IntegerConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Long.class, new LongConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Short.class, new ShortConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(String.class, new StringConverter(), throwException, defaultArraySize);
        // Other
        this.registerArrayConverter(Class.class, new ClassConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(java.util.Date.class, new DateConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Calendar.class, new DateConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(File.class, new FileConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(java.sql.Date.class, new SqlDateConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(java.sql.Time.class, new SqlTimeConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(Timestamp.class, new SqlTimestampConverter(), throwException, defaultArraySize);
        this.registerArrayConverter(URL.class, new URLConverter(), throwException, defaultArraySize);
    }
    /**
     * Register a new ArrayConverter with the specified element delegate converter
     * that returns a default array of the specified size in the event of conversion errors.
     *
     * @param componentType The component type of the array
     * @param componentConverter The converter to delegate to for the array elements
     * @param throwException Whether a conversion exception should be thrown or a default
     * value used in the event of a conversion error
     * @param defaultArraySize The size of the default array
     */
    private void registerArrayConverter(final Class<?> componentType, final Converter componentConverter, final boolean throwException, final int defaultArraySize) {
        Class<?> arrayType = Array.newInstance(componentType, 0).getClass();
        Converter arrayConverter = null;
        if (throwException) {
            arrayConverter = new ArrayConverter(arrayType, componentConverter);
        } else {
            arrayConverter = new ArrayConverter(arrayType, componentConverter, defaultArraySize);
        }
        this.register(arrayType, arrayConverter);
    }
    /** strictly for convenience since it has same parameter order as Map.put */
    private void register(final Class<?> clazz, final Converter converter) {
        this.register(new ConverterFacade(converter), clazz);
    }
    /**
     * Remove any registered {@link Converter} for the specified destination
     * <code>Class</code>.
     *
     * @param clazz Class for which to remove a registered Converter
     */
    public void deregister(final Class<?> clazz) {
        this.converters.remove(clazz);
    }
    /**
     * Look up and return any registered {@link Converter} for the specified
     * destination class; if there is no registered Converter, return
     * <code>null</code>.
     *
     * @param clazz Class for which to return a registered Converter
     * @return The registered {@link Converter} or <code>null</code> if not found
     */
    public Converter lookup(final Class<?> clazz) {
        Converter conv = (Converter) this.converters.get(clazz);
        if (conv != null) {
            return conv;
        }
        for (Object regType : this.converters.keySet()) {
            if (((Class<?>) regType).isAssignableFrom(clazz)) {
                return (Converter) this.converters.get(regType);
            }
        }
        return null;
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
    public Converter lookup(final Class<?> sourceType, final Class<?> targetType) {
        if (targetType == null) {
            throw new IllegalArgumentException("Target type is missing");
        }
        if (sourceType == null) {
            return this.lookup(targetType);
        }
        Converter converter = null;
        // Convert --> String 
        if (targetType == String.class) {
            converter = this.lookup(sourceType);
            if (converter == null && (sourceType.isArray() || Collection.class.isAssignableFrom(sourceType))) {
                converter = this.lookup(String[].class);
            }
            if (converter == null) {
                converter = this.lookup(String.class);
            }
            return converter;
        }
        // Convert --> String array 
        if (targetType == String[].class) {
            if (sourceType.isArray() || Collection.class.isAssignableFrom(sourceType)) {
                converter = this.lookup(sourceType);
            }
            if (converter == null) {
                converter = this.lookup(String[].class);
            }
            return converter;
        }
        return this.lookup(targetType);
    }
    /**
     * Register a custom {@link Converter} for the specified destination
     * <code>Class</code>, replacing any previously registered Converter.
     *
     * @param converter Converter to be registered
     * @param clazz Destination class for conversions performed by this
     *  Converter
     */
    public void register(final Converter converter, final Class<?> clazz) {
        this.converters.put(clazz, converter);
    }
}