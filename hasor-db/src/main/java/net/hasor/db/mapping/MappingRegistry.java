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
package net.hasor.db.mapping;
import net.hasor.db.mapping.reader.DefaultTableReader;
import net.hasor.db.mapping.reader.TableReader;
import net.hasor.db.mapping.resolve.ClassResolveTableMapping;
import net.hasor.db.metadata.MetaDataService;
import net.hasor.db.types.TypeHandlerRegistry;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 映射注册器。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingRegistry {
    public final static MappingRegistry               DEFAULT = new MappingRegistry(TypeHandlerRegistry.DEFAULT, null);
    protected final     TypeHandlerRegistry           typeRegistry;
    protected final     MetaDataService               metaService;
    protected final     Map<Class<?>, TableReader<?>> entityReaderMap;

    public MappingRegistry() {
        this(TypeHandlerRegistry.DEFAULT);
    }

    public MappingRegistry(TypeHandlerRegistry typeRegistry) {
        this(typeRegistry, null);
    }

    public MappingRegistry(MetaDataService metaDataService) {
        this.typeRegistry = TypeHandlerRegistry.DEFAULT;
        this.metaService = metaDataService;
        this.entityReaderMap = new ConcurrentHashMap<>();
    }

    public MappingRegistry(TypeHandlerRegistry typeRegistry, MetaDataService metaDataService) {
        this.typeRegistry = Objects.requireNonNull(typeRegistry, "typeRegistry not null.");
        this.metaService = metaDataService;
        this.entityReaderMap = new ConcurrentHashMap<>();
    }

    public TypeHandlerRegistry getTypeRegistry() {
        return this.typeRegistry;
    }

    /** 从类型中解析 TableReader */
    public <T> TableReader<T> resolveTableReader(Class<T> entityType) throws SQLException {
        return resolveTableReader(entityType, this.metaService);
    }

    /** 从类型中解析 TableMapping */
    public TableMapping resolveTableMapping(Class<?> entityType) throws SQLException {
        return resolveTableMapping(entityType, this.metaService);
    }

    /** 从类型中解析 TableReader */
    public TableMapping resolveTableMapping(Class<?> entityType, MetaDataService metaDataService) throws SQLException {
        TableReader<?> tableReader = resolveTableReader(entityType, metaDataService);
        if (tableReader != null) {
            return tableReader.getTableMapping();
        }
        return null;
    }

    /** 从类型中解析 TableMapping */
    public <T> TableReader<T> resolveTableReader(Class<T> entityType, MetaDataService metaDataService) throws SQLException {
        TableReader<T> resultMapper = (TableReader<T>) this.entityReaderMap.get(entityType);
        if (resultMapper == null) {
            synchronized (this) {
                resultMapper = (TableReader<T>) this.entityReaderMap.get(entityType);
                if (resultMapper != null) {
                    return resultMapper;
                }
                TableMapping tableMapping = new ClassResolveTableMapping().resolveTableMapping(entityType, this.typeRegistry, metaDataService);
                if (tableMapping == null) {
                    return null;
                }
                resultMapper = new DefaultTableReader<>(entityType, tableMapping);
                //
                this.entityReaderMap.put(entityType, resultMapper);
            }
        }
        return resultMapper;
    }
}
