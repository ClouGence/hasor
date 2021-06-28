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
import net.hasor.db.mapping.resolve.MappingOptions;
import net.hasor.db.mapping.resolve.XmlResolveTableMapping;
import net.hasor.db.metadata.MetaDataService;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
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
    public final static MappingRegistry DEFAULT = new MappingRegistry();

    public static MappingRegistry newInstance() {
        return new MappingRegistry(TypeHandlerRegistry.DEFAULT, null, Thread.currentThread().getContextClassLoader());
    }

    public static MappingRegistry newInstance(MetaDataService metaService) {
        return new MappingRegistry(TypeHandlerRegistry.DEFAULT, metaService, Thread.currentThread().getContextClassLoader());
    }

    public static MappingRegistry newInstance(TypeHandlerRegistry typeRegistry) {
        return new MappingRegistry(typeRegistry, null, Thread.currentThread().getContextClassLoader());
    }

    public static MappingRegistry newInstance(ClassLoader classLoader, MetaDataService metaService, TypeHandlerRegistry typeRegistry) {
        return new MappingRegistry(typeRegistry, metaService, classLoader);
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
    public TableMapping getMapping(Class<?> entityType) {
        return getMapping(entityType.getName());
    }

    /** 从类型中解析 TableMapping */
    public TableMapping getMapping(String mappingId) {
        TableReader<?> tableReader = this.entityReaderMap.get(mappingId);
        return tableReader == null ? null : tableReader.getTableMapping();
    }

    /** 从类型中解析 TableReader */
    public <T> TableReader<T> getTableReader(Class<?> entityType) {
        return getTableReader(entityType.getName());
    }

    /** 从类型中解析 TableReader */
    public <T> TableReader<T> getTableReader(String mappingId) {
        TableReader<?> tableReader = this.entityReaderMap.get(mappingId);
        return tableReader == null ? null : (TableReader<T>) tableReader.getTableMapping();
    }

    /** 从类型中解析 TableMapping */
    public TableMapping loadMapping(Class<?> entityType, MappingOptions options) throws SQLException {
        TableReader<?> tableReader = loadReader(entityType, options);
        return (tableReader != null) ? tableReader.getTableMapping() : null;
    }

    /** 从类型中解析 TableMapping */
    public <T> TableReader<T> loadReader(Class<T> entityType, MappingOptions options) throws SQLException {
        options = (options == null) ? new MappingOptions() : options;
        boolean overwrite = options.isOverwrite();
        //
        TableReader<T> resultMapper = (TableReader<T>) this.entityReaderMap.get(entityType);
        if (resultMapper == null || overwrite) {
            synchronized (this) {
                resultMapper = (TableReader<T>) this.entityReaderMap.get(entityType);
                if (resultMapper != null && !overwrite) {
                    return resultMapper;
                }
                ClassLoader classLoader = entityType.getClassLoader();
                if (classLoader == null) {
                    classLoader = this.classLoader;
                }
                TableMapping tableMapping = new ClassResolveTableMapping().resolveTableMapping(entityType, classLoader, this.typeRegistry, this.metaService, options);
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
    public TableMapping loadMapping(String id, String mapperData) throws SQLException, IOException, ClassNotFoundException {
        TableReader<?> tableReader = loadReader(id, mapperData);
        return (tableReader != null) ? tableReader.getTableMapping() : null;
    }

    /** 从 Xml 中解析 TableMapping */
    public <T> TableReader<T> loadReader(String id, String mapperData) throws SQLException, ClassNotFoundException, IOException {
        Node refData = null;
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(mapperData)));
            refData = document.getDocumentElement();
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e);
        }
        return loadReader(id, refData);
    }

    /** 从 Xml 中解析 TableMapping */
    public <T> TableReader<T> loadReader(String id, Node refData) throws SQLException, ClassNotFoundException {
        MappingOptions options = MappingOptions.resolveOptions(refData);
        boolean overwrite = options.isOverwrite();
        //
        TableReader<T> resultMapper = (TableReader<T>) this.entityReaderMap.get(id);
        if (resultMapper == null || overwrite) {
            synchronized (this) {
                resultMapper = (TableReader<T>) this.entityReaderMap.get(id);
                if (resultMapper != null && !overwrite) {
                    return resultMapper;
                }
                //
                XmlResolveTableMapping resolveTableMapping = new XmlResolveTableMapping();
                TableMapping tableMapping = resolveTableMapping.resolveTableMapping(refData, this.classLoader, this.typeRegistry, this.metaService, options);
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

    private InputStream loadResource(String resource) throws IOException {
        if (this.classLoader != null) {
            return ResourcesUtils.getResourceAsStream(this.classLoader, resource);
        } else {
            return ResourcesUtils.getResourceAsStream(resource);
        }
    }

    /** 解析 mapper.xml 并载入各种 resultMap */
    public void loadMapper(String resource) throws SQLException, ClassNotFoundException, IOException {
        String namespace = "";
        Element root = null;
        try (InputStream asStream = loadResource(resource)) {
            if (asStream == null) {
                throw new IOException("mapper resource '" + resource + "' not exist.");
            }
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(asStream));
            root = document.getDocumentElement();
        } catch (ParserConfigurationException | SAXException e) {
            throw ExceptionUtils.toRuntime(e);
        }
        //
        NamedNodeMap rootAttributes = root.getAttributes();
        if (rootAttributes != null) {
            Node namespaceNode = rootAttributes.getNamedItem("namespace");
            if (namespaceNode != null && StringUtils.isBlank(namespace)) {
                namespace = namespaceNode.getNodeValue();
            }
        }
        //
        NodeList childNodes = root.getChildNodes();
        for (int i = 0, len = childNodes.getLength(); i < len; i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            NamedNodeMap nodeAttributes = node.getAttributes();
            Node idNode = nodeAttributes.getNamedItem("id");
            String idString = (idNode != null) ? idNode.getNodeValue() : null;
            if (StringUtils.isBlank(idString)) {
                throw new IOException("the <" + root.getNodeName() + "> tag is missing an ID.");
            }
            //
            if (StringUtils.isNotBlank(namespace)) {
                idString = namespace + "." + idString;
            }
            loadReader(idString, node);
        }
    }
}