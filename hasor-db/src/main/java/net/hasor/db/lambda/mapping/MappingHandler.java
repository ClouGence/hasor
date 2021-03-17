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
package net.hasor.db.lambda.mapping;
import net.hasor.db.types.TypeHandlerRegistry;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 映射到数据库的列信息。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingHandler {
    public final static MappingHandler                     DEFAULT = new MappingHandler(TypeHandlerRegistry.DEFAULT);
    protected final     TypeHandlerRegistry                typeRegistry;
    protected final     Map<Class<?>, MappingRowMapper<?>> resultMapperMap;

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

    public <T> MappingRowMapper<T> resolveMapper(Class<T> dtoClass) {
        MappingRowMapper<T> resultMapper = (MappingRowMapper<T>) this.resultMapperMap.get(dtoClass);
        if (resultMapper == null) {
            synchronized (this) {
                resultMapper = (MappingRowMapper<T>) this.resultMapperMap.get(dtoClass);
                if (resultMapper != null) {
                    return resultMapper;
                }
                resultMapper = new MappingRowMapper<>(dtoClass);
                //
                this.resultMapperMap.put(dtoClass, resultMapper);
            }
        }
        return resultMapper;
    }
}
