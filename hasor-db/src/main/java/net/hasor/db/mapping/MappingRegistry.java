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
import net.hasor.db.mapping.resolve.XmlResolveTableMapping;
import net.hasor.db.metadata.MetaDataService;
import net.hasor.db.types.TypeHandlerRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
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
    public final static MappingRegistry DEFAULT = new MappingRegistry(TypeHandlerRegistry.DEFAULT, null, null);

    public static MappingRegistry newMappingRegistry(ClassLoader classLoader, MetaDataService metaDataService, TypeHandlerRegistry typeRegistry) {
        return new MappingRegistry(typeRegistry, metaDataService, classLoader);
    }

    private final ClassLoader                 classLoader;
    private final TypeHandlerRegistry         typeRegistry;
    private final MetaDataService             metaService;
    private final Map<String, TableReader<?>> entityReaderMap;

    public MappingRegistry() {
        this(TypeHandlerRegistry.DEFAULT, null, null);
    }

    public MappingRegistry(TypeHandlerRegistry typeRegistry, MetaDataService metaService, ClassLoader classLoader) {
        this.typeRegistry = Objects.requireNonNull(typeRegistry, "typeRegistry not null.");
        this.metaService = metaService;
        this.entityReaderMap = new ConcurrentHashMap<>();
        this.classLoader = (classLoader != null) ? classLoader : Thread.currentThread().getContextClassLoader();
    }

    public TypeHandlerRegistry getTypeRegistry() {
        return this.typeRegistry;
    }

    public MetaDataService getMetaService() {
        return this.metaService;
    }

    /** 从类型中解析 TableMapping */
    public <T> TableReader<T> resolveTableReader(Class<T> entityType) throws SQLException {
        TableReader<T> resultMapper = (TableReader<T>) this.entityReaderMap.get(entityType);
        if (resultMapper == null) {
            synchronized (this) {
                resultMapper = (TableReader<T>) this.entityReaderMap.get(entityType);
                if (resultMapper != null) {
                    return resultMapper;
                }
                ClassLoader classLoader = entityType.getClassLoader();
                if (classLoader == null) {
                    classLoader = Thread.currentThread().getContextClassLoader();
                }
                TableMapping tableMapping = new ClassResolveTableMapping().resolveTableMapping(entityType, classLoader, this.typeRegistry, this.metaService);
                if (tableMapping == null) {
                    return null;
                }
                resultMapper = new DefaultTableReader<>(entityType, tableMapping);
                //
                this.entityReaderMap.put(entityType.getName(), resultMapper);
            }
        }
        return resultMapper;
    }

    /** 从类型中解析 TableMapping */
    public TableMapping resolveTableMapping(Class<?> entityType) throws SQLException {
        TableReader<?> tableReader = resolveTableReader(entityType);
        if (tableReader != null) {
            return tableReader.getTableMapping();
        }
        return null;
    }

    public <T> TableReader<T> resolveTableReader(String id, String mapperData) throws SQLException, ClassNotFoundException, IOException {
        TableReader<T> resultMapper = (TableReader<T>) this.entityReaderMap.get(id);
        if (resultMapper == null) {
            synchronized (this) {
                resultMapper = (TableReader<T>) this.entityReaderMap.get(id);
                if (resultMapper != null) {
                    return resultMapper;
                }
                //
                Node refData = null;
                try {
                    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = documentBuilder.parse(new InputSource(new StringReader(mapperData)));
                    refData = document.getDocumentElement();
                } catch (SAXException | ParserConfigurationException e) {
                    throw new IOException(e);
                }
                //
                XmlResolveTableMapping resolveTableMapping = new XmlResolveTableMapping();
                TableMapping tableMapping = resolveTableMapping.resolveTableMapping(refData, this.classLoader, this.typeRegistry, this.metaService);
                if (tableMapping == null) {
                    return null;
                }
                //
                Class<T> entityType = (Class<T>) tableMapping.entityType();
                resultMapper = new DefaultTableReader<>(entityType, tableMapping);
                this.entityReaderMap.put(id, resultMapper);
            }
        }
        return resultMapper;
    }

    /** 从类型中解析 TableMapping */
    public TableMapping resolveTableMapping(String id, String mapperData) throws SQLException, IOException, ClassNotFoundException {
        TableReader<?> tableReader = resolveTableReader(id, mapperData);
        if (tableReader != null) {
            return tableReader.getTableMapping();
        }
        return null;
    }
}
