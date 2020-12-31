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
import net.hasor.utils.BeanUtils;
import net.hasor.utils.convert.Converter;

/**
 * {@link Converter} implementaion that handles conversion
 * to and from <b>java.lang.Character</b> objects.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * <code>ConversionException</code> if a conversion error occurs.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 690380 $ $Date: 2008-08-29 21:04:38 +0100 (Fri, 29 Aug 2008) $
 * @since 1.3
 */
@SuppressWarnings("rawtypes")
public final class CharacterConverter extends AbstractConverter {
    /**
     * Construct a <b>java.lang.Character</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public CharacterConverter() {
        super();
    }

    /**
     * Construct a <b>java.lang.Character</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public CharacterConverter(final Object defaultValue) {
        super(defaultValue);
    }

    /**
     * Return the default type this <code>Converter</code> handles.
     *
     * @return The default type this <code>Converter</code> handles.
     * @since 1.8.0
     */
    @Override
    protected Class getDefaultType() {
        return Character.class;
    }

    /**
     * <p>Convert a java.lang.Class or object into a String.</p>
     *
     * @param value The input value to be converted
     * @return the converted String value.
     * @since 1.8.0
     */
    @Override
    protected String convertToString(final Object value) {
        String strValue = value.toString();
        return strValue.length() == 0 ? "" : strValue.substring(0, 1);
    }

    /**
     * <p>Convert the input object into a java.lang.Character.</p>
     *
     * @param type Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Exception if conversion cannot be performed successfully
     * @since 1.8.0
     */
    @Override
    protected Object convertToType(final Class type, final Object value) throws Exception {
        String string = value.toString();
        if (string.length() == 0) {
            return BeanUtils.getDefaultValue(type);
        } else {
            return string.charAt(0);
        }
    }
}