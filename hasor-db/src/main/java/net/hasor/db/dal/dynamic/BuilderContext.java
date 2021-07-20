/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.dal.dynamic;
import net.hasor.db.dal.dynamic.rule.RuleRegistry;
import net.hasor.db.dal.repository.MapperRegistry;
import net.hasor.db.mapping.MappingRegistry;
import net.hasor.db.mapping.reader.TableReader;
import net.hasor.db.types.TypeHandlerRegistry;

import java.util.Map;

/**
 * 生成动态 SQL 的 Build 环境
 * @version : 2021-06-05
 * @author 赵永春 (zyc@byshell.org)
 */
public class BuilderContext {
    private final String              namespace;
    private final Map<String, Object> context;
    private final TypeHandlerRegistry handlerRegistry;
    private final RuleRegistry        ruleRegistry;
    private final ClassLoader         classLoader;
    private final MapperRegistry      mapperRegistry;

    public BuilderContext(Map<String, Object> context) {
        this("", context, RuleRegistry.DEFAULT, MapperRegistry.DEFAULT, null);
    }

    public BuilderContext(String namespace, Map<String, Object> context) {
        this(namespace, context, RuleRegistry.DEFAULT, MapperRegistry.DEFAULT, null);
    }

    public BuilderContext(String namespace, Map<String, Object> context, RuleRegistry ruleRegistry, MapperRegistry mapperRegistry, ClassLoader classLoader) {
        this.namespace = namespace;
        this.context = context;
        this.ruleRegistry = (ruleRegistry == null) ? RuleRegistry.DEFAULT : ruleRegistry;
        this.mapperRegistry = (mapperRegistry == null) ? MapperRegistry.DEFAULT : mapperRegistry;
        this.classLoader = (classLoader == null) ? Thread.currentThread().getContextClassLoader() : classLoader;
        this.handlerRegistry = this.mapperRegistry.getMappingRegistry().getTypeRegistry();
    }

    public Class<?> loadClass(String typeName) throws ClassNotFoundException {
        return this.classLoader.loadClass(typeName);
    }

    public DynamicSql findDynamicSqlById(String dynamicId) {
        if (this.mapperRegistry != null) {
            return mapperRegistry.findDynamicSql(this.namespace, dynamicId);
        }
        return null;
    }

    public TableReader<?> findTableReaderById(String mapperId) {
        if (this.mapperRegistry != null) {
            return mapperRegistry.getMappingRegistry().getTableReader(this.namespace, mapperId);
        }
        return null;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }

    public TypeHandlerRegistry getHandlerRegistry() {
        return this.handlerRegistry;
    }

    public RuleRegistry getRuleRegistry() {
        return this.ruleRegistry;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public MapperRegistry getMapperRegistry() {
        return this.mapperRegistry;
    }

    public MappingRegistry getMappingRegistry() {
        if (this.mapperRegistry != null) {
            return this.mapperRegistry.getMappingRegistry();
        }
        return null;
    }
}
