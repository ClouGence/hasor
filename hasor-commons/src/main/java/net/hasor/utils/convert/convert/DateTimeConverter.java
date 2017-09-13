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

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
/**
 * {@link Converter} implementaion
 * that handles conversion to and from <b>date/time</b> objects.
 * <p>
 * This implementation handles conversion for the following
 * <i>date/time</i> types.
 * <ul>
 *     <li><code>java.util.Date</code></li>
 *     <li><code>java.util.Calendar</code></li>
 *     <li><code>java.sql.Date</code></li>
 *     <li><code>java.sql.Time</code></li>
 *     <li><code>java.sql.Timestamp</code></li>
 * </ul>
 *
 * <h3>String Conversions (to and from)</h3>
 * This class provides a number of ways in which date/time
 * conversions to/from Strings can be achieved:
 * <ul>
 *    <li>Using the SHORT date format for the default Locale, configure using:</li>
 *        <ul>
 *           <li><code>setUseLocaleFormat(true)</code></li>
 *        </ul>
 *    <li>Using the SHORT date format for a specified Locale, configure using:</li>
 *        <ul>
 *           <li><code>setLocale(Locale)</code></li>
 *        </ul>
 *    <li>Using the specified date pattern(s) for the default Locale, configure using:</li>
 *        <ul>
 *           <li>Either <code>setPattern(String)</code> or
 *                      <code>setPatterns(String[])</code></li>
 *        </ul>
 *    <li>Using the specified date pattern(s) for a specified Locale, configure using:</li>
 *        <ul>
 *           <li><code>setPattern(String)</code> or
 *                    <code>setPatterns(String[]) and...</code></li>
 *           <li><code>setLocale(Locale)</code></li>
 *        </ul>
 *    <li>If none of the above are configured the
 *        <code>toDate(String)</code> method is used to convert
 *        from String to Date and the Dates's
 *        <code>toString()</code> method used to convert from
 *        Date to String.</li>
 * </ul>
 *
 * <p>
 * The <b>Time Zone</b> to use with the date format can be specified
 * using the <code>setTimeZone()</code> method.
 *
 * @version $Revision: 640131 $ $Date: 2008-03-23 02:10:31 +0000 (Sun, 23 Mar 2008) $
 * @since 1.8.0
 */
@SuppressWarnings("rawtypes")
public abstract class DateTimeConverter extends AbstractConverter {
    private String[] patterns;
    private String   displayPatterns;
    private Locale   locale;
    private TimeZone timeZone;
    private boolean  useLocaleFormat;
    // ----------------------------------------------------------- Constructors
    /**
     * Construct a Date/Time <i>Converter</i> that throws a
     * <code>ConversionException</code> if an error occurs.
     */
    public DateTimeConverter() {
        super();
    }
    /**
     * Construct a Date/Time <i>Converter</i> that returns a default
     * value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public DateTimeConverter(final Object defaultValue) {
        super(defaultValue);
    }
    // --------------------------------------------------------- Public Methods
    /**
     * Indicate whether conversion should use a format/pattern or not.
     *
     * @param useLocaleFormat <code>true</code> if the format
     * for the locale should be used, otherwise <code>false</code>
     */
    public void setUseLocaleFormat(final boolean useLocaleFormat) {
        this.useLocaleFormat = useLocaleFormat;
    }
    /**
     * Return the Time Zone to use when converting dates
     * (or <code>null</code> if none specified.
     *
     * @return The Time Zone.
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }
    /**
     * Set the Time Zone to use when converting dates.
     *
     * @param timeZone The Time Zone.
     */
    public void setTimeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
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
     * @param locale The Locale.
     */
    public void setLocale(final Locale locale) {
        this.locale = locale;
        this.setUseLocaleFormat(true);
    }
    /**
     * Set a date format pattern to use to convert
     * dates to/from a <code>java.lang.String</code>.
     *
     * @see SimpleDateFormat
     * @param pattern The format pattern.
     */
    public void setPattern(final String pattern) {
        this.setPatterns(new String[] { pattern });
    }
    /**
     * Return the date format patterns used to convert
     * dates to/from a <code>java.lang.String</code>
     * (or <code>null</code> if none specified).
     *
     * @see SimpleDateFormat
     * @return Array of format patterns.
     */
    public String[] getPatterns() {
        return this.patterns;
    }
    /**
     * Set the date format patterns to use to convert
     * dates to/from a <code>java.lang.String</code>.
     *
     * @see SimpleDateFormat
     * @param patterns Array of format patterns.
     */
    public void setPatterns(final String[] patterns) {
        this.patterns = patterns;
        if (patterns != null && patterns.length > 1) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < patterns.length; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(patterns[i]);
            }
            this.displayPatterns = buffer.toString();
        }
        this.setUseLocaleFormat(true);
    }
    @Override
    public Object convert(final Class type, final Object value) {
        if (value == null) {
            return null;
        }
        return super.convert(type, value);
    }
    // ------------------------------------------------------ Protected Methods
    /**
     * Convert an input Date/Calendar object into a String.
     * <p>
     * <b>N.B.</b>If the converter has been configured to with
     * one or more patterns (using <code>setPatterns()</code>), then
     * the first pattern will be used to format the date into a String.
     * Otherwise the default <code>DateFormat</code> for the default locale
     * (and <i>style</i> if configured) will be used.
     *
     * @param value The input value to be converted
     * @return the converted String value.
     * @throws Throwable if an error occurs converting to a String
     */
    @Override
    protected String convertToString(final Object value) throws Throwable {
        Date date = null;
        if (value instanceof Date) {
            date = (Date) value;
        } else if (value instanceof Calendar) {
            date = ((Calendar) value).getTime();
        } else if (value instanceof Long) {
            date = new Date(((Long) value).longValue());
        }
        String result = null;
        if (this.useLocaleFormat && date != null) {
            DateFormat format = null;
            if (this.patterns != null && this.patterns.length > 0) {
                format = this.getFormat(this.patterns[0]);
            } else {
                format = this.getFormat(this.locale, this.timeZone);
            }
            result = format.format(date);
        } else {
            result = value.toString();
        }
        return result;
    }
    /**
     * Convert the input object into a Date object of the
     * specified type.
     * <p>
     * This method handles conversions between the following
     * types:
     * <ul>
     *     <li><code>java.util.Date</code></li>
     *     <li><code>java.util.Calendar</code></li>
     *     <li><code>java.sql.Date</code></li>
     *     <li><code>java.sql.Time</code></li>
     *     <li><code>java.sql.Timestamp</code></li>
     * </ul>
     *
     * It also handles conversion from a <code>String</code> to
     * any of the above types.
     * <p>
     *
     * For <code>String</code> conversion, if the converter has been configured
     * with one or more patterns (using <code>setPatterns()</code>), then
     * the conversion is attempted with each of the specified patterns.
     * Otherwise the default <code>DateFormat</code> for the default locale
     * (and <i>style</i> if configured) will be used.
     *
     * @param targetType Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Exception if conversion cannot be performed successfully
     */
    @Override
    protected Object convertToType(final Class targetType, final Object value) throws Exception {
        Class sourceType = value.getClass();
        // Handle java.sql.Timestamp
        if (value instanceof java.sql.Timestamp) {
            // ---------------------- JDK 1.3 Fix ----------------------
            // N.B. Prior to JDK 1.4 the Timestamp's getTime() method
            //      didn't include the milliseconds. The following code
            //      ensures it works consistently accross JDK versions
            java.sql.Timestamp timestamp = (java.sql.Timestamp) value;
            long timeInMillis = timestamp.getTime() / 1000 * 1000;
            timeInMillis += timestamp.getNanos() / 1000000;
            // ---------------------- JDK 1.3 Fix ----------------------
            return this.toDate(targetType, timeInMillis);
        }
        // Handle Date (includes java.sql.Date & java.sql.Time)
        if (value instanceof Date) {
            Date date = (Date) value;
            return this.toDate(targetType, date.getTime());
        }
        // Handle Calendar
        if (value instanceof Calendar) {
            Calendar calendar = (Calendar) value;
            return this.toDate(targetType, calendar.getTime().getTime());
        }
        // Handle Long
        if (value instanceof Long) {
            Long longObj = (Long) value;
            return this.toDate(targetType, longObj.longValue());
        }
        // Convert all other types to String & handle
        String stringValue = value.toString().trim();
        if (stringValue.length() == 0) {
            return this.handleMissing(targetType);
        }
        // Parse the Date/Time
        if (this.useLocaleFormat) {
            Calendar calendar = null;
            if (this.patterns != null && this.patterns.length > 0) {
                calendar = this.parse(sourceType, targetType, stringValue);
            } else {
                DateFormat format = this.getFormat(this.locale, this.timeZone);
                calendar = this.parse(sourceType, targetType, stringValue, format);
            }
            if (Calendar.class.isAssignableFrom(targetType)) {
                return calendar;
            } else {
                return this.toDate(targetType, calendar.getTime().getTime());
            }
        }
        // Default String conversion
        return this.toDate(targetType, stringValue);
    }
    /**
     * Convert a long value to the specified Date type for this
     * <i>Converter</i>.
     * <p>
     *
     * This method handles conversion to the following types:
     * <ul>
     *     <li><code>java.util.Date</code></li>
     *     <li><code>java.util.Calendar</code></li>
     *     <li><code>java.sql.Date</code></li>
     *     <li><code>java.sql.Time</code></li>
     *     <li><code>java.sql.Timestamp</code></li>
     * </ul>
     *
     * @param type The Date type to convert to
     * @param value The long value to convert.
     * @return The converted date value.
     */
    private Object toDate(final Class type, final long value) {
        // java.util.Date
        if (type.equals(Date.class)) {
            return new Date(value);
        }
        // java.sql.Date
        if (type.equals(java.sql.Date.class)) {
            return new java.sql.Date(value);
        }
        // java.sql.Time
        if (type.equals(java.sql.Time.class)) {
            return new java.sql.Time(value);
        }
        // java.sql.Timestamp
        if (type.equals(java.sql.Timestamp.class)) {
            return new java.sql.Timestamp(value);
        }
        // java.util.Calendar
        if (type.equals(Calendar.class)) {
            Calendar calendar = null;
            if (this.locale == null && this.timeZone == null) {
                calendar = Calendar.getInstance();
            } else if (this.locale == null) {
                calendar = Calendar.getInstance(this.timeZone);
            } else if (this.timeZone == null) {
                calendar = Calendar.getInstance(this.locale);
            } else {
                calendar = Calendar.getInstance(this.timeZone, this.locale);
            }
            calendar.setTime(new Date(value));
            calendar.setLenient(false);
            return calendar;
        }
        String msg = this.toString(this.getClass()) + " cannot handle conversion to '" + this.toString(type) + "'";
        throw new ConversionException(msg);
    }
    /**
     * Default String to Date conversion.
     * <p>
     * This method handles conversion from a String to the following types:
     * <ul>
     *     <li><code>java.sql.Date</code></li>
     *     <li><code>java.sql.Time</code></li>
     *     <li><code>java.sql.Timestamp</code></li>
     * </ul>
     * <p>
     * <strong>N.B.</strong> No default String conversion
     * mechanism is provided for <code>java.util.Date</code>
     * and <code>java.util.Calendar</code> type.
     *
     * @param type The Number type to convert to
     * @param value The String value to convert.
     * @return The converted Number value.
     */
    private Object toDate(final Class type, final String value) {
        // java.sql.Date
        if (type.equals(java.sql.Date.class)) {
            try {
                return java.sql.Date.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new ConversionException("String must be in JDBC format [yyyy-MM-dd] to create a java.sql.Date");
            }
        }
        // java.sql.Time
        if (type.equals(java.sql.Time.class)) {
            try {
                return java.sql.Time.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new ConversionException("String must be in JDBC format [HH:mm:ss] to create a java.sql.Time");
            }
        }
        // java.sql.Timestamp
        if (type.equals(java.sql.Timestamp.class)) {
            try {
                return java.sql.Timestamp.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new ConversionException("String must be in JDBC format [yyyy-MM-dd HH:mm:ss.fffffffff] " + "to create a java.sql.Timestamp");
            }
        }
        String msg = this.toString(this.getClass()) + " does not support default String to '" + this.toString(type) + "' conversion.";
        throw new ConversionException(msg);
    }
    /**
     * Return a <code>DateFormat<code> for the Locale.
     * @param locale The Locale to create the Format with (may be null)
     * @param timeZone The Time Zone create the Format with (may be null)
     *
     * @return A Date Format.
     */
    protected DateFormat getFormat(final Locale locale, final TimeZone timeZone) {
        DateFormat format = null;
        if (locale == null) {
            format = DateFormat.getDateInstance(DateFormat.SHORT);
        } else {
            format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        }
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        return format;
    }
    /**
     * Create a date format for the specified pattern.
     *
     * @param pattern The date pattern
     * @return The DateFormat
     */
    private DateFormat getFormat(final String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        if (this.timeZone != null) {
            format.setTimeZone(this.timeZone);
        }
        return format;
    }
    /**
     * Parse a String date value using the set of patterns.
     *
     * @param sourceType The type of the value being converted
     * @param targetType The type to convert the value to.
     * @param value The String date value.
     *
     * @return The converted Date object.
     * @throws Exception if an error occurs parsing the date.
     */
    private Calendar parse(final Class sourceType, final Class targetType, final String value) throws Exception {
        Exception firstEx = null;
        for (String pattern : this.patterns) {
            try {
                DateFormat format = this.getFormat(pattern);
                Calendar calendar = this.parse(sourceType, targetType, value, format);
                return calendar;
            } catch (Exception ex) {
                if (firstEx == null) {
                    firstEx = ex;
                }
            }
        }
        if (this.patterns.length > 1) {
            throw new ConversionException("Error converting '" + this.toString(sourceType) + "' to '" + this.toString(targetType) + "' using  patterns '" + this.displayPatterns + "'");
        } else {
            throw firstEx;
        }
    }
    /**
     * Parse a String into a <code>Calendar</code> object
     * using the specified <code>DateFormat</code>.
     *
     * @param sourceType The type of the value being converted
     * @param targetType The type to convert the value to
     * @param value The String date value.
     * @param format The DateFormat to parse the String value.
     *
     * @return The converted Calendar object.
     * @throws ConversionException if the String cannot be converted.
     */
    private Calendar parse(final Class sourceType, final Class targetType, final String value, final DateFormat format) {
        format.setLenient(false);
        ParsePosition pos = new ParsePosition(0);
        Date parsedDate = format.parse(value, pos); // ignore the result (use the Calendar)
        if (pos.getErrorIndex() >= 0 || pos.getIndex() != value.length() || parsedDate == null) {
            String msg = "Error converting '" + this.toString(sourceType) + "' to '" + this.toString(targetType) + "'";
            if (format instanceof SimpleDateFormat) {
                msg += " using pattern '" + ((SimpleDateFormat) format).toPattern() + "'";
            }
            throw new ConversionException(msg);
        }
        Calendar calendar = format.getCalendar();
        return calendar;
    }
    /**
     * Provide a String representation of this date/time converter.
     *
     * @return A String representation of this date/time converter
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.toString(this.getClass()));
        buffer.append("[UseDefault=");
        buffer.append(this.isUseDefault());
        buffer.append(", UseLocaleFormat=");
        buffer.append(this.useLocaleFormat);
        if (this.displayPatterns != null) {
            buffer.append(", Patterns={");
            buffer.append(this.displayPatterns);
            buffer.append('}');
        }
        if (this.locale != null) {
            buffer.append(", Locale=");
            buffer.append(this.locale);
        }
        if (this.timeZone != null) {
            buffer.append(", TimeZone=");
            buffer.append(this.timeZone);
        }
        buffer.append(']');
        return buffer.toString();
    }
}