/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.db.jdbc.mapping;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 元信息分析器。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MetaManager {
    private static final Map<String, FieldMeta> COLUMN_CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    public static TableMeta loadTableMeta(Class<?> declaringClass) {
        return null;
    }

    public static FieldMeta[] loadColumnMeta(Class<?> declaringClass) {
        return null;
    }

    public static FieldMeta loadColumnMeta(Field declaringClass) {
        return null;
    }

    public static Object toColumnMeta(Method columnMethod) {
        return new FieldMeta(columnMethod.getName(), columnMethod.getReturnType());
    }

    public static FieldMeta toColumnMeta(String columnName, Class<?> javaType) {
        return new FieldMeta(columnName, javaType);
    }

    public static TableMeta toTableMeta(String tableName) {
        return new TableMeta(tableName);
    }
}