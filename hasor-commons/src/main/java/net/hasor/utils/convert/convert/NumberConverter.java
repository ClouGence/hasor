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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * {@link Converter} implementaion that handles conversion
 * to and from <b>java.lang.Number</b> objects.
 * <p>
 * This implementation handles conversion for the following
 * <code>java.lang.Number</code> types.
 * <ul>
 *     <li><code>java.lang.Byte</code></li>
 *     <li><code>java.lang.Short</code></li>
 *     <li><code>java.lang.Integer</code></li>
 *     <li><code>java.lang.Long</code></li>
 *     <li><code>java.lang.Float</code></li>
 *     <li><code>java.lang.Double</code></li>
 *     <li><code>java.math.BigDecimal</code></li>
 *     <li><code>java.math.BigInteger</code></li>
 * </ul>
 *
 * <h3>String Conversions (to and from)</h3>
 * This class provides a number of ways in which number
 * conversions to/from Strings can be achieved:
 * <ul>
 *    <li>Using the default format for the default Locale, configure using:</li>
 *        <ul>
 *           <li><code>setUseLocaleFormat(true)</code></li>
 *        </ul>
 *    <li>Using the default format for a specified Locale, configure using:</li>
 *        <ul>
 *           <li><code>setLocale(Locale)</code></li>
 *        </ul>
 *    <li>Using a specified pattern for the default Locale, configure using:</li>
 *        <ul>
 *           <li><code>setPattern(String)</code></li>
 *        </ul>
 *    <li>Using a specified pattern for a specified Locale, configure using:</li>
 *        <ul>
 *           <li><code>setPattern(String)</code></li>
 *           <li><code>setLocale(Locale)</code></li>
 *        </ul>
 *    <li>If none of the above are configured the
 *        <code>toNumber(String)</code> method is used to convert
 *        from String to Number and the Number's
 *        <code>toString()</code> method used to convert from
 *        Number to String.</li>
 * </ul>
 *
 * <p>
 * <strong>N.B.</strong>Patterns can only be specified used the <i>standard</i>
 * pattern characters and NOT in <i>localized</i> form (see <code>java.text.SimpleDateFormat</code>).
 * For example to cater for number styles used in Germany such as <code>0.000,00</code> the pattern
 * is specified in the normal form <code>0,000.00</code> and the locale set to <code>Locale.GERMANY</code>.
 *
 * @version $Revision: 745081 $ $Date: 2009-02-17 14:05:20 +0000 (Tue, 17 Feb 2009) $
 * @since 1.8.0
 */
@SuppressWarnings("rawtypes")
public abstract class NumberConverter extends AbstractConverter {
    private static final Integer ZERO = new Integer(0);
    private static final Integer ONE  = new Integer(1);
    private String  pattern;
    private boolean allowDecimals;
    private boolean useLocaleFormat;
    private Locale  locale;
    // ----------------------------------------------------------- Constructors
    /**
     * Construct a <b>java.lang.Number</b> <i>Converter</i>
     * that throws a <code>ConversionException</code> if a error occurs.
     *
     * @param allowDecimals Indicates whether decimals are allowed
     */
    public NumberConverter(final boolean allowDecimals) {
        super();
        this.allowDecimals = allowDecimals;
    }
    /**
     * Construct a <code>java.lang.Number</code> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param allowDecimals Indicates whether decimals are allowed
     * @param defaultValue The default value to be returned
     */
    public NumberConverter(final boolean allowDecimals, final Object defaultValue) {
        super();
        this.allowDecimals = allowDecimals;
        this.setDefaultValue(defaultValue);
    }
    // --------------------------------------------------------- Public Methods
    /**
     * Return whether decimals are allowed in the number.
     *
     * @return Whether decimals are allowed in the number
     */
    public boolean isAllowDecimals() {
        return this.allowDecimals;
    }
    /**
     * Set whether a format should be used to convert
     * the Number.
     *
     * @param useLocaleFormat <code>true</code> if a number format
     * should be used.
     */
    public void setUseLocaleFormat(final boolean useLocaleFormat) {
        this.useLocaleFormat = useLocaleFormat;
    }
    /**
     * Return the number format pattern used to convert
     * Numbers to/from a <code>java.lang.String</code>
     * (or <code>null</code> if none specified).
     * <p>
     * See <code>java.text.SimpleDateFormat</code> for details
     * of how to specify the pattern.
     *
     * @return The format pattern.
     */
    public String getPattern() {
        return this.pattern;
    }
    /**
     * Set a number format pattern to use to convert
     * Numbers to/from a <code>java.lang.String</code>.
     * <p>
     * See <code>java.text.SimpleDateFormat</code> for details
     * of how to specify the pattern.
     *
     * @param pattern The format pattern.
     */
    public void setPattern(final String pattern) {
        this.pattern = pattern;
        this.setUseLocaleFormat(true);
    }
    /**
     * Return the Locale for the <i>Converter</i>
     * (or <code>null</code> if none specified).
     *
     * @return The locale to use for conversion
     */
    public Locale getLocale() {
        return this.locale;
    }
    /**
     * Set the Locale for the <i>Converter</i>.
     *
     * @param locale The locale to use for conversion
     */
    public void setLocale(final Locale locale) {
        this.locale = locale;
        this.setUseLocaleFormat(true);
    }
    // ------------------------------------------------------ Protected Methods
    /**
     * Convert an input Number object into a String.
     *
     * @param value The input value to be converted
     * @return the converted String value.
     * @throws Throwable if an error occurs converting to a String
     */
    @Override
    protected String convertToString(final Object value) throws Throwable {
        if (this.useLocaleFormat && value instanceof Number) {
            NumberFormat format = this.getFormat();
            format.setGroupingUsed(false);
            return format.format(value);
        } else {
            return value.toString();
        }
    }
    /**
     * Convert the input object into a Number object of the
     * specified type.
     *
     * @param targetType Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     */
    @Override
    protected Object convertToType(final Class targetType, final Object value) throws Throwable {
        Class sourceType = value.getClass();
        // Handle Number
        if (value instanceof Number) {
            return this.toNumber(sourceType, targetType, (Number) value);
        }
        // Handle Boolean
        if (value instanceof Boolean) {
            return this.toNumber(sourceType, targetType, ((Boolean) value).booleanValue() ? NumberConverter.ONE : NumberConverter.ZERO);
        }
        // Handle Date --> Long
        if (value instanceof Date && Long.class.equals(targetType)) {
            return new Long(((Date) value).getTime());
        }
        // Handle Calendar --> Long
        if (value instanceof Calendar && Long.class.equals(targetType)) {
            return new Long(((Calendar) value).getTime().getTime());
        }
        // Convert all other types to String & handle
        String stringValue = value.toString().trim();
        if (stringValue.length() == 0) {
            return this.handleMissing(targetType);
        }
        // Convert/Parse a String
        Number number = null;
        if (this.useLocaleFormat) {
            NumberFormat format = this.getFormat();
            number = this.parse(sourceType, targetType, stringValue, format);
        } else {
            number = this.toNumber(sourceType, targetType, stringValue);
        }
        // Ensure the correct number type is returned
        return this.toNumber(sourceType, targetType, number);
    }
    /**
     * Convert any Number object to the specified type for this
     * <i>Converter</i>.
     * <p>
     * This method handles conversion to the following types:
     * <ul>
     *     <li><code>java.lang.Byte</code></li>
     *     <li><code>java.lang.Short</code></li>
     *     <li><code>java.lang.Integer</code></li>
     *     <li><code>java.lang.Long</code></li>
     *     <li><code>java.lang.Float</code></li>
     *     <li><code>java.lang.Double</code></li>
     *     <li><code>java.math.BigDecimal</code></li>
     *     <li><code>java.math.BigInteger</code></li>
     * </ul>
     * @param sourceType The type being converted from
     * @param targetType The Number type to convert to
     * @param value The Number to convert.
     *
     * @return The converted value.
     */
    private Number toNumber(final Class sourceType, final Class targetType, final Number value) {
        // Correct Number type already
        if (targetType.equals(value.getClass())) {
            return value;
        }
        // Byte
        if (targetType.equals(Byte.class)) {
            long longValue = value.longValue();
            if (longValue > Byte.MAX_VALUE) {
                throw new ConversionException(this.toString(sourceType) + " value '" + value + "' is too large for " + this.toString(targetType));
            }
            if (longValue < Byte.MIN_VALUE) {
                throw new ConversionException(this.toString(sourceType) + " value '" + value + "' is too small " + this.toString(targetType));
            }
            return new Byte(value.byteValue());
        }
        // Short
        if (targetType.equals(Short.class)) {
            long longValue = value.longValue();
            if (longValue > Short.MAX_VALUE) {
                throw new ConversionException(this.toString(sourceType) + " value '" + value + "' is too large for " + this.toString(targetType));
            }
            if (longValue < Short.MIN_VALUE) {
                throw new ConversionException(this.toString(sourceType) + " value '" + value + "' is too small " + this.toString(targetType));
            }
            return new Short(value.shortValue());
        }
        // Integer
        if (targetType.equals(Integer.class)) {
            long longValue = value.longValue();
            if (longValue > Integer.MAX_VALUE) {
                throw new ConversionException(this.toString(sourceType) + " value '" + value + "' is too large for " + this.toString(targetType));
            }
            if (longValue < Integer.MIN_VALUE) {
                throw new ConversionException(this.toString(sourceType) + " value '" + value + "' is too small " + this.toString(targetType));
            }
            return new Integer(value.intValue());
        }
        // Long
        if (targetType.equals(Long.class)) {
            return new Long(value.longValue());
        }
        // Float
        if (targetType.equals(Float.class)) {
            if (value.doubleValue() > Float.MAX_VALUE) {
                throw new ConversionException(this.toString(sourceType) + " value '" + value + "' is too large for " + this.toString(targetType));
            }
            return new Float(value.floatValue());
        }
        // Double
        if (targetType.equals(Double.class)) {
            return new Double(value.doubleValue());
        }
        // BigDecimal
        if (targetType.equals(BigDecimal.class)) {
            if (value instanceof Float || value instanceof Double) {
                return new BigDecimal(value.toString());
            } else if (value instanceof BigInteger) {
                return new BigDecimal((BigInteger) value);
            } else {
                return BigDecimal.valueOf(value.longValue());
            }
        }
        // BigInteger
        if (targetType.equals(BigInteger.class)) {
            if (value instanceof BigDecimal) {
                return ((BigDecimal) value).toBigInteger();
            } else {
                return BigInteger.valueOf(value.longValue());
            }
        }
        String msg = this.toString(this.getClass()) + " cannot handle conversion to '" + this.toString(targetType) + "'";
        throw new ConversionException(msg);
    }
    /**
     * Default String to Number conversion.
     * <p>
     * This method handles conversion from a String to the following types:
     * <ul>
     *     <li><code>java.lang.Byte</code></li>
     *     <li><code>java.lang.Short</code></li>
     *     <li><code>java.lang.Integer</code></li>
     *     <li><code>java.lang.Long</code></li>
     *     <li><code>java.lang.Float</code></li>
     *     <li><code>java.lang.Double</code></li>
     *     <li><code>java.math.BigDecimal</code></li>
     *     <li><code>java.math.BigInteger</code></li>
     * </ul>
     * @param sourceType The type being converted from
     * @param targetType The Number type to convert to
     * @param value The String value to convert.
     *
     * @return The converted Number value.
     */
    private Number toNumber(final Class sourceType, final Class targetType, final String value) {
        // Byte
        if (targetType.equals(Byte.class)) {
            return new Byte(value);
        }
        // Short
        if (targetType.equals(Short.class)) {
            return new Short(value);
        }
        // Integer
        if (targetType.equals(Integer.class)) {
            return new Integer(value);
        }
        // Long
        if (targetType.equals(Long.class)) {
            return new Long(value);
        }
        // Float
        if (targetType.equals(Float.class)) {
            return new Float(value);
        }
        // Double
        if (targetType.equals(Double.class)) {
            return new Double(value);
        }
        // BigDecimal
        if (targetType.equals(BigDecimal.class)) {
            return new BigDecimal(value);
        }
        // BigInteger
        if (targetType.equals(BigInteger.class)) {
            return new BigInteger(value);
        }
        String msg = this.toString(this.getClass()) + " cannot handle conversion from '" + this.toString(sourceType) + "' to '" + this.toString(targetType) + "'";
        throw new ConversionException(msg);
    }
    /**
     * Provide a String representation of this number converter.
     *
     * @return A String representation of this number converter
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.toString(this.getClass()));
        buffer.append("[UseDefault=");
        buffer.append(this.isUseDefault());
        buffer.append(", UseLocaleFormat=");
        buffer.append(this.useLocaleFormat);
        if (this.pattern != null) {
            buffer.append(", Pattern=");
            buffer.append(this.pattern);
        }
        if (this.locale != null) {
            buffer.append(", Locale=");
            buffer.append(this.locale);
        }
        buffer.append(']');
        return buffer.toString();
    }
    /**
     * Return a NumberFormat to use for Conversion.
     *
     * @return The NumberFormat.
     */
    private NumberFormat getFormat() {
        NumberFormat format = null;
        if (this.pattern != null) {
            if (this.locale == null) {
                format = new DecimalFormat(this.pattern);
            } else {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(this.locale);
                format = new DecimalFormat(this.pattern, symbols);
            }
        } else {
            if (this.locale == null) {
                format = NumberFormat.getInstance();
            } else {
                format = NumberFormat.getInstance(this.locale);
            }
        }
        if (!this.allowDecimals) {
            format.setParseIntegerOnly(true);
        }
        return format;
    }
    /**
     * Convert a String into a <code>Number</code> object.
     * @param sourceType
     * @param targetType The type to convert the value to
     * @param value The String date value.
     * @param format The NumberFormat to parse the String value.
     *
     * @return The converted Number object.
     * @throws ConversionException if the String cannot be converted.
     */
    private Number parse(final Class sourceType, final Class targetType, final String value, final NumberFormat format) {
        ParsePosition pos = new ParsePosition(0);
        Number parsedNumber = format.parse(value, pos);
        if (pos.getErrorIndex() >= 0 || pos.getIndex() != value.length() || parsedNumber == null) {
            String msg = "Error converting from '" + this.toString(sourceType) + "' to '" + this.toString(targetType) + "'";
            if (format instanceof DecimalFormat) {
                msg += " using pattern '" + ((DecimalFormat) format).toPattern() + "'";
            }
            if (this.locale != null) {
                msg += " for locale=[" + this.locale + "]";
            }
            throw new ConversionException(msg);
        }
        return parsedNumber;
    }
}