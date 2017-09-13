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

import java.lang.reflect.Array;
import java.util.Collection;
/**
 * Base {@link Converter} implementation that provides the structure
 * for handling conversion <b>to</b> and <b>from</b> a specified type.
 * <p>
 * This implementation provides the basic structure for
 * converting to/from a specified type optionally using a default
 * value or throwing a {@link ConversionException} if a
 * conversion error occurs.
 * <p>
 * Implementations should provide conversion to the specified
 * type and from the specified type to a <code>String</code> value
 * by implementing the following methods:
 * <ul>
 *     <li><code>convertToString(value)</code> - convert to a String
 *        (default implementation uses the objects <code>toString()</code>
 *        method).</li>
 *     <li><code>convertToType(Class, value)</code> - convert
 *         to the specified type</li>
 * </ul>
 *
 * @version $Revision: 640131 $ $Date: 2008-03-23 02:10:31 +0000 (Sun, 23 Mar 2008) $
 * @since 1.8.0
 */
@SuppressWarnings({ "rawtypes" })
public abstract class AbstractConverter implements Converter {
    /** 当转换出错时是否返回默认值。*/
    private boolean useDefault   = false;
    /**默认值*/
    private Object  defaultValue = null;
    // ----------------------------------------------------------- Constructors
    /**创建创造<i>Converter</i>转换器，可能会引发<code>ConversionException</code>异常。*/
    public AbstractConverter() {
    }
    /**创建创造<i>Converter</i>转换器，可能会引发<code>ConversionException</code>异常。*/
    public AbstractConverter(final Object defaultValue) {
        this.setDefaultValue(defaultValue);
    }
    // --------------------------------------------------------- Public Methods
    /**
     * 当转换期间发生异常时是否使用默认值。
     * @return 如果<code>true</code>则表示当遇到错误时设置的默认值会被返回。如果<code>false</code>会引发{@link ConversionException}异常。
     */
    public boolean isUseDefault() {
        return this.useDefault;
    }
    /**
     * Convert the input object into an output object of the
     * specified type.
     *
     * @param type Data type to which this value should be converted
     * @param value The input value to be converted
     * @return The converted value.
     * @throws ConversionException if conversion cannot be performed
     * successfully and no default is specified.
     */
    @Override
    public Object convert(final Class type, Object value) {
        Class sourceType = value == null ? null : value.getClass();
        Class targetType = this.primitive(type == null ? this.getDefaultType() : type);
        value = this.convertArray(value);//如果数据源是一个Array 或 集合 那么取得第一个元素。
        //Missing Value
        if (value == null) {
            return this.handleMissing(targetType);
        }
        //
        sourceType = value.getClass();
        try {
            /*Convert --> String*/
            if (targetType.equals(String.class)) {
                return this.convertToString(value);
            } else if (targetType.equals(sourceType)) {
                return value;
                /*Convert --> Type*/
            } else {
                return this.convertToType(targetType, value);
            }
        } catch (Throwable t) {
            return this.handleError(targetType, value, t);
        }
    }
    /**
     * 处理转换错误。<p>
     * 如果设置了default属性则当遇到错误时返回默认值。否则引发{@link ConversionException}异常。
     */
    protected Object handleError(final Class type, final Object value, final Throwable cause) {
        if (this.useDefault) {
            return this.handleMissing(type);
        }
        if (cause instanceof ConversionException) {
            throw (ConversionException) cause;
        } else {
            String msg = "Error converting from '" + value.getClass() + "' to '" + type + "' " + cause.getMessage();
            throw new ConversionException(msg, cause);
        }
    }
    /**
     * 转换对象成为String格式。<p>
     * <b>注意：</b>这个方法简单使用<code>toString()</code>实现该功能，子类应当重写该方法以完成特殊的转换过程。
     */
    protected String convertToString(final Object value) throws Throwable {
        return value.toString();
    }
    /**执行类型转换代码。*/
    protected abstract Object convertToType(Class type, Object value) throws Throwable;
    /**
     * Return the first element from an Array (or Collection)
     * or the value unchanged if not an Array (or Collection).
     *
     * N.B. This needs to be overriden for array/Collection converters.
     *
     * @param value The value to convert
     * @return The first element in an Array (or Collection)
     * or the value unchanged if not an Array (or Collection)
     */
    protected Object convertArray(final Object value) {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            if (Array.getLength(value) > 0) {
                return Array.get(value, 0);
            } else {
                return null;
            }
        }
        if (value instanceof Collection) {
            Collection collection = (Collection) value;
            if (collection.size() > 0) {
                return collection.iterator().next();
            } else {
                return null;
            }
        }
        return value;
    }
    /**设置默认值 */
    protected void setDefaultValue(final Object defaultValue) {
        this.useDefault = false;
        if (defaultValue == null) {
            this.defaultValue = null;
        } else {
            this.defaultValue = this.convert(this.getDefaultType(), defaultValue);
        }
        this.useDefault = true;
    }
    /**获取默认值*/
    protected abstract Class getDefaultType();
    /**返回指定类型的默认值.*/
    protected Object getDefault(final Class type) {
        if (type.equals(String.class)) {
            return null;
        } else {
            return this.defaultValue;
        }
    }
    /**
     * Provide a String representation of this converter.
     * @return A String representation of this converter
     */
    @Override
    public String toString() {
        return this.toString(this.getClass()) + "[UseDefault=" + this.useDefault + "]";
    }
    /**当遇到空值传入或者返回值为空的时候*/
    protected Object handleMissing(final Class type) {
        if (this.useDefault || type.equals(String.class)) {
            Object value = this.getDefault(type);
            if (this.useDefault && value != null && !type.equals(value.getClass())) {
                try {
                    value = this.convertToType(type, this.defaultValue);
                } catch (Throwable t) {
                    //log().error("    Default conversion to " + toString(type) + "failed: " + t);// TODO Log
                }
            }
            return value;
        }
        throw new ConversionException("No value specified for '" + this.toString(type) + "'");
    }
    // ----------------------------------------------------------- Package Methods
    /** 转换基本类型到包装类型. */
    private Class primitive(final Class type) {
        if (type == null || !type.isPrimitive()) {
            return type;
        }
        if (type == Integer.TYPE) {
            return Integer.class;
        } else if (type == Double.TYPE) {
            return Double.class;
        } else if (type == Long.TYPE) {
            return Long.class;
        } else if (type == Boolean.TYPE) {
            return Boolean.class;
        } else if (type == Float.TYPE) {
            return Float.class;
        } else if (type == Short.TYPE) {
            return Short.class;
        } else if (type == Byte.TYPE) {
            return Byte.class;
        } else if (type == Character.TYPE) {
            return Character.class;
        } else {
            return type;
        }
    }
    //
    //
    /**
     * Provide a String representation of a <code>java.lang.Class</code>.
     * @param type The <code>java.lang.Class</code>.
     * @return The String representation.
     */
    public String toString(final Class type) {
        String typeName = null;
        if (type == null) {
            typeName = "null";
        } else if (type.isArray()) {
            Class elementType = type.getComponentType();
            int count = 1;
            while (elementType.isArray()) {
                elementType = elementType.getComponentType();
                count++;
            }
            typeName = elementType.getName();
            for (int i = 0; i < count; i++) {
                typeName += "[]";
            }
        } else {
            typeName = type.getName();
        }
        /* org.more.convert.convert. */
        final String PACKAGE = AbstractConverter.class.getPackage().getName() + ".";
        if (typeName.startsWith("java.lang.") || typeName.startsWith("java.util.") || typeName.startsWith("java.math.")) {
            typeName = typeName.substring("java.lang.".length());
        } else if (typeName.startsWith(PACKAGE)) {
            typeName = typeName.substring(PACKAGE.length());
        }
        return typeName;
    }
}