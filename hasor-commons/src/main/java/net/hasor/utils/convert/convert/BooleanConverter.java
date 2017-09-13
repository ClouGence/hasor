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
package net.hasor.utils.convert.convert;
import net.hasor.utils.convert.ConversionException;
import net.hasor.utils.convert.Converter;
/**
 * {@link Converter} implementaion that handles conversion
 * to and from <b>Boolean</b> objects.
 * {@link Converter} implementaion that
 * handles conversion to and from <b>java.lang.Boolean</b> objects.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * <code>ConversionException</code> if a conversion error occurs.
 * <p>
 * By default any object whose string representation is one of the values
 * {"yes", "y", "true", "on", "1"} is converted to Boolean.TRUE, and 
 * string representations {"no", "n", "false", "off", "0"} are converted
 * to Boolean.FALSE. The recognised true/false strings can be changed by:
 * <pre>
 *  String[] trueStrings = {"oui", "o", "1"};
 *  String[] falseStrings = {"non", "n", "0"};
 *  Converter bc = new BooleanConverter(trueStrings, falseStrings);
 *  ConvertUtils.register(bc, Boolean.class);
 *  ConvertUtils.register(bc, Boolean.TYPE);
 * </pre>
 * In addition, it is recommended that the BooleanArrayConverter also be
 * modified to recognise the same set of values:
 * <pre>
 *   Converter bac = new BooleanArrayConverter(bc, BooleanArrayConverter.NO_DEFAULT);
 *   ConvertUtils.register(bac, bac.MODEL);
 * </pre>
 * </p>
 *
 * <p>Case is ignored when converting values to true or false.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 801644 $ $Date: 2009-08-06 14:38:56 +0100 (Thu, 06 Aug 2009) $
 * @since 1.3
 */
@SuppressWarnings("rawtypes")
public final class BooleanConverter extends AbstractConverter {
    // ----------------------------------------------------------- Constructors
    /**
     * Create a {@link Converter} that will throw a {@link ConversionException}
     * if a conversion error occurs, ie the string value being converted is
     * not one of the known true strings, nor one of the known false strings.
     */
    public BooleanConverter() {
        super();
    }
    /**
     * Create a {@link Converter} that will return the specified default value
     * if a conversion error occurs, ie the string value being converted is
     * not one of the known true strings, nor one of the known false strings.
     *
     * @param defaultValue The default value to be returned if the value
     *  being converted is not recognised. This value may be null, in which
     *  case null will be returned on conversion failure. When non-null, it is
     *  expected that this value will be either Boolean.TRUE or Boolean.FALSE.
     *  The special value BooleanConverter.NO_DEFAULT can also be passed here,
     *  in which case this constructor acts like the no-argument one.
     */
    public BooleanConverter(final Object defaultValue) {
        super();
        this.setDefaultValue(defaultValue);
    }
    /**
     * Create a {@link Converter} that will throw a {@link ConversionException}
     * if a conversion error occurs, ie the string value being converted is
     * not one of the known true strings, nor one of the known false strings.
     * <p>
     * The provided string arrays are copied, so that changes to the elements
     * of the array after this call is made do not affect this object.
     *
     * @param trueStrings is the set of strings which should convert to the
     *  value Boolean.TRUE. The value null must not be present. Case is
     *  ignored.
     *
     * @param falseStrings is the set of strings which should convert to the
     *  value Boolean.TRUE. The value null must not be present. Case is
     *  ignored.
     * @since 1.8.0
     */
    public BooleanConverter(final String[] trueStrings, final String[] falseStrings) {
        super();
        this.trueStrings = BooleanConverter.copyStrings(trueStrings);
        this.falseStrings = BooleanConverter.copyStrings(falseStrings);
    }
    /**
     * Create a {@link Converter} that will return
     * the specified default value if a conversion error occurs.
     * <p>
     * The provided string arrays are copied, so that changes to the elements
     * of the array after this call is made do not affect this object.
     *
     * @param trueStrings is the set of strings which should convert to the
     *  value Boolean.TRUE. The value null must not be present. Case is
     *  ignored.
     *
     * @param falseStrings is the set of strings which should convert to the
     *  value Boolean.TRUE. The value null must not be present. Case is
     *  ignored.
     *
     * @param defaultValue The default value to be returned if the value
     *  being converted is not recognised. This value may be null, in which
     *  case null will be returned on conversion failure. When non-null, it is
     *  expected that this value will be either Boolean.TRUE or Boolean.FALSE.
     *  The special value BooleanConverter.NO_DEFAULT can also be passed here,
     *  in which case an exception will be thrown on conversion failure.
     * @since 1.8.0
     */
    public BooleanConverter(final String[] trueStrings, final String[] falseStrings, final Object defaultValue) {
        super();
        this.trueStrings = BooleanConverter.copyStrings(trueStrings);
        this.falseStrings = BooleanConverter.copyStrings(falseStrings);
        this.setDefaultValue(defaultValue);
    }
    // ----------------------------------------------------- Instance Variables
    /**
     * The set of strings that are known to map to Boolean.TRUE.
     */
    private String[] trueStrings  = { "true", "yes", "y", "on", "1" };
    /**
     * The set of strings that are known to map to Boolean.FALSE.
     */
    private String[] falseStrings = { "false", "no", "n", "off", "0" };
    // --------------------------------------------------------- Protected Methods
    /**
     * Return the default type this <code>Converter</code> handles.
     *
     * @return The default type this <code>Converter</code> handles.
     * @since 1.8.0
     */
    @Override
    protected Class getDefaultType() {
        return Boolean.class;
    }
    /**
     * Convert the specified input object into an output object of the
     * specified type.
     *
     * @param type is the type to which this value should be converted. In the
     *  case of this BooleanConverter class, this value is ignored.
     *
     * @param value is the input value to be converted. The toString method
     *  shall be invoked on this object, and the result compared (ignoring
     *  case) against the known "true" and "false" string values.
     *
     * @return Boolean.TRUE if the value was a recognised "true" value, 
     *  Boolean.FALSE if the value was a recognised "false" value, or
     *  the default value if the value was not recognised and the constructor
     *  was provided with a default value.
     *
     * @throws Throwable if an error occurs converting to the specified type
     * @since 1.8.0
     */
    @Override
    protected Object convertToType(final Class type, final Object value) throws Throwable {
        // All the values in the trueStrings and falseStrings arrays are
        // guaranteed to be lower-case. By converting the input value
        // to lowercase too, we can use the efficient String.equals method
        // instead of the less-efficient String.equalsIgnoreCase method.
        String stringValue = value.toString().toLowerCase();
        for (String trueString : this.trueStrings) {
            if (trueString.equals(stringValue)) {
                return Boolean.TRUE;
            }
        }
        for (String falseString : this.falseStrings) {
            if (falseString.equals(stringValue)) {
                return Boolean.FALSE;
            }
        }
        throw new ConversionException("Can't convert value '" + value + "' to a Boolean");
    }
    /**
     * This method creates a copy of the provided array, and ensures that
     * all the strings in the newly created array contain only lower-case
     * letters.
     * <p>
     * Using this method to copy string arrays means that changes to the
     * src array do not modify the dst array.
     */
    private static String[] copyStrings(final String[] src) {
        String[] dst = new String[src.length];
        for (int i = 0; i < src.length; ++i) {
            dst[i] = src[i].toLowerCase();
        }
        return dst;
    }
}