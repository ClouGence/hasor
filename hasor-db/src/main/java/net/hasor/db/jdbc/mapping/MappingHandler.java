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
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.db.types.UnknownTypeHandler;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ExceptionUtils;

import java.sql.JDBCType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 映射到数据库的列信息。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingHandler {
    public final static MappingHandler                  DEFAULT = new MappingHandler(TypeHandlerRegistry.DEFAULT);
    private final       TypeHandlerRegistry             typeRegistry;
    private final       Map<Class<?>, BeanRowMapper<?>> resultMapperMap;

    public MappingHandler() {
        this(TypeHandlerRegistry.DEFAULT);
    }

    public MappingHandler(TypeHandlerRegistry typeRegistry) {
        this.typeRegistry = Objects.requireNonNull(typeRegistry, "typeRegistry not null.");
        this.resultMapperMap = new ConcurrentHashMap<>();
    }

    public TypeHandlerRegistry getTypeRegistry() {
        return this.typeRegistry;
    }

    public <T> BeanRowMapper<T> resolveMapper(Class<T> dtoClass) {
        BeanRowMapper<T> resultMapper = (BeanRowMapper<T>) this.resultMapperMap.get(dtoClass);
        if (resultMapper == null) {
            synchronized (this) {
                resultMapper = (BeanRowMapper<T>) this.resultMapperMap.get(dtoClass);
                if (resultMapper != null) {
                    return resultMapper;
                }
                resultMapper = new BeanRowMapper<>(dtoClass);
                Table defTable = defTable(dtoClass);
                resultMapper.setupTable(defTable);
                boolean autoConfigField = defTable.autoFiled();
                List<java.lang.reflect.Field> allFields = BeanUtils.findALLFields(dtoClass);
                for (java.lang.reflect.Field field : allFields) {
                    Field defField = defField(field, autoConfigField);
                    if (defField == null) {
                        continue;
                    }
                    //
                    TypeHandler<?> typeHandler = null;
                    if (defField.typeHandler() == UnknownTypeHandler.class) {
                        typeHandler = this.typeRegistry.getTypeHandler(field.getType(), defField.jdbcType());
                    } else {
                        try {
                            typeHandler = defField.typeHandler().newInstance();
                        } catch (Exception e) {
                            throw ExceptionUtils.toRuntimeException(e);
                        }
                    }
                    resultMapper.setupField(field, defField, typeHandler);
                }
                //
                this.resultMapperMap.put(dtoClass, resultMapper);
            }
        }
        return resultMapper;
    }

    private Table defTable(Class<?> dtoClass) {
        if (dtoClass.isAnnotationPresent(Table.class)) {
            return dtoClass.getAnnotation(Table.class);
        } else {
            return new TableImpl(dtoClass.getSimpleName());
        }
    }

    private Field defField(java.lang.reflect.Field dtoField, boolean autoConfigField) {
        if (dtoField.isAnnotationPresent(Field.class)) {
            return dtoField.getAnnotation(Field.class);
        } else if (autoConfigField) {
            Class<?> fieldType = dtoField.getType();
            JDBCType jdbcType = this.typeRegistry.toSqlType(fieldType);
            return new FieldImpl(dtoField.getName(), jdbcType);
        } else {
            return null;
        }
    }
}
