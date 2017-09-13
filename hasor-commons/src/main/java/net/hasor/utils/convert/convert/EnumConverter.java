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
/**
 *
 * @version : 2013-8-13
 * @author 赵永春 (zyc@hasor.net)
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class EnumConverter extends AbstractConverter {
    public EnumConverter() {
        super();
    }
    public EnumConverter(final Object defaultValue) {
        super(defaultValue);
    }
    @Override
    protected Class getDefaultType() {
        return Enum.class;
    }
    @Override
    protected Object convertToType(final Class type, final Object value) throws Throwable {
        Class<Enum> forEnum = type;
        String strValue = value.toString();
        //
        for (Enum<?> item : forEnum.getEnumConstants()) {
            String enumValue = item.name().toLowerCase();
            if (enumValue.equals(strValue.toLowerCase()) == true) {
                return item;
            }
        }
        return null;
    }
}