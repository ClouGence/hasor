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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 映射注册器。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingRegistry {
    public final static MappingRegistry                          DEFAULT       = new MappingRegistry();
    private final       ClassLoader                              classLoader;
    private final       TypeHandlerRegistry                      typeRegistry;
    private final       MetaDataService                          metaService;
    private final       Map<String, Map<String, TableReader<?>>> spaceSqlMap   = new ConcurrentHashMap<>();
    private final       Map<String, TableReader<?>>              defaultSqlMap = new ConcurrentHashMap<>();

    public MappingRegistry() {
        this(TypeHandlerRegistry.DEFAULT, null, null);
    }

    public MappingRegistry(MetaDataService metaService) {
        this(TypeHandlerRegistry.DEFAULT, metaService, Thread.currentThread().getContextClassLoader());
    }

    public MappingRegistry(TypeHandlerRegistry typeRegistry) {
        this(typeRegistry, null, Thread.currentThread().getContextClassLoader());
    }

    public MappingRegistry(TypeHandlerRegistry typeRegistry, MetaDataService metaService, ClassLoader classLoader) {
        this.typeRegistry = Objects.requireNonNull(typeRegistry, "typeRegistry not null.");
        this.metaService = metaService;
        this.classLoader = (classLoader != null) ? classLoader : Thread.currentThread().getContextClassLoader();
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public TypeHandlerRegistry getTypeRegistry() {
        return this.typeRegistry;
    }

    public MetaDataService getMetaService() {
        return this.metaService;
    }

    /** 从类型中解析 TableMapping */
    public <T> TableReader<T> getTableReader(Class<?> entityType) {
        return getTableReader("", entityType.getName());
    }

    /** 从类型中解析 TableMapping */
    public <T> TableReader<T> getTableReader(String namespace, Class<?> entityType) {
        return getTableReader(namespace, entityType.getName());
    }

    /** 从类型中解析 TableMapping */
    public <T> TableReader<T> getTableReader(String mappingId) {
        return getTableReader("", mappingId);
    }

    /** 从类型中解析 TableMapping */
    public <T> TableReader<T> getTableReader(String namespace, String mappingId) {
        if (StringUtils.isBlank(namespace)) {
            return (TableReader<T>) this.defaultSqlMap.get(mappingId);
        }
        Map<String, TableReader<?>> tableReaderMap = this.spaceSqlMap.get(namespace);
        if (tableReaderMap == null) {
            return null;
        }
        return (TableReader<T>) tableReaderMap.get(mappingId);
    }

    public <T> TableReader<T> loadReader(Class<T> entityType, MappingOptions defaultOptions) throws SQLException {
        return this.loadReader("", entityType.getName(), entityType, defaultOptions);
    }

    public <T> TableReader<T> loadReader(String mapperData, MappingOptions defaultOptions) throws SQLException, IOException, ClassNotFoundException {
        Element root = null;
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(mapperData)));
            root = document.getDocumentElement();
        } catch (ParserConfigurationException | SAXException e) {
            throw ExceptionUtils.toRuntime(e);
        }
        //
        NamedNodeMap nodeAttributes = root.getAttributes();
        Node idNode = nodeAttributes.getNamedItem("id");
        String idString = (idNode != null) ? idNode.getNodeValue() : null;
        if (StringUtils.isBlank(idString)) {
            throw new SQLException("the <" + root.getNodeName() + "> tag is missing an ID.");
        }
        //
        defaultOptions = MappingOptions.resolveOptions(root, defaultOptions);
        return this.loadReader("", idString, root, defaultOptions, false);
    }

    public <T> TableReader<T> loadReader(String id, Class<T> entityType, MappingOptions defaultOptions) throws SQLException, IOException {
        if (StringUtils.isBlank(id)) {
            throw new SQLException("id is null");
        }
        return this.loadReader("", id, entityType, defaultOptions);
    }

    public <T> TableReader<T> loadReader(String id, String mapperData, MappingOptions defaultOptions) throws SQLException, IOException, ClassNotFoundException {
        if (StringUtils.isBlank(id)) {
            throw new SQLException("id is null");
        }
        return this.loadReader("", id, mapperData, defaultOptions);
    }

    /** 从类型中解析 TableMapping */
    public <T> TableReader<T> loadReader(String namespace, String id, Class<T> entityType, MappingOptions defaultOptions) throws SQLException {
        if (StringUtils.isBlank(id)) {
            throw new SQLException("id is null");
        }
        defaultOptions = (defaultOptions == null) ? new MappingOptions() : defaultOptions;
        boolean overwrite = defaultOptions.getOverwrite() != null && defaultOptions.getOverwrite();
        TableReader<T> tableReader = this.getTableReader(namespace, id);
        if (tableReader != null && !overwrite) {
            return tableReader;
        }
        //
        ClassLoader classLoader = entityType.getClassLoader();
        if (classLoader == null) {
            classLoader = this.classLoader;
        }
        TableMapping tableMapping = new ClassResolveTableMapping().resolveTableMapping(entityType, classLoader, this.typeRegistry, this.metaService, defaultOptions);
        if (tableMapping == null) {
            return null;
        }
        tableReader = new DefaultTableReader<>(entityType, tableMapping);
        this.saveTableReader(namespace, id, tableReader, overwrite);
        return tableReader;
    }

    /** 从 Xml 中解析 TableMapping */
    public <T> TableReader<T> loadReader(String namespace, String id, String mapperData, MappingOptions defaultOptions) throws SQLException, ClassNotFoundException, IOException {
        if (StringUtils.isBlank(id)) {
            throw new SQLException("id is null");
        }
        Node refData = null;
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(mapperData)));
            refData = document.getDocumentElement();
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e);
        }
        //
        defaultOptions = MappingOptions.resolveOptions(refData, defaultOptions);
        boolean overwrite = defaultOptions.getOverwrite() != null && defaultOptions.getOverwrite();
        //
        TableReader<T> tableReader = this.getTableReader(namespace, id);
        if (tableReader != null && !overwrite) {
            return tableReader;
        }
        return loadReader(namespace, id, refData, MappingOptions.resolveOptions(refData, defaultOptions), false);
    }

    /** 从 Xml 中解析 TableMapping */
    public <T> TableReader<T> loadReader(String namespace, String id, Node refData, MappingOptions defaultOptions, boolean isDataQL) throws SQLException, ClassNotFoundException {
        if (StringUtils.isBlank(id)) {
            throw new SQLException("id is null");
        }
        defaultOptions = MappingOptions.resolveOptions(refData, defaultOptions);
        boolean overwrite = defaultOptions.getOverwrite() != null && defaultOptions.getOverwrite();
        TableReader<T> tableReader = this.getTableReader(namespace, id);
        if (tableReader != null && !overwrite) {
            return tableReader;
        }
        //
        if (StringUtils.isBlank(id)) {
            throw new SQLException("id is null");
        }
        XmlResolveTableMapping resolveTableMapping = new XmlResolveTableMapping();
        TableMapping tableMapping = resolveTableMapping.resolveTableMapping(refData, this.classLoader, this.typeRegistry, this.metaService, defaultOptions);
        if (tableMapping == null) {
            return null;
        }
        //
        Class<T> entityType = (Class<T>) tableMapping.entityType();
        tableReader = new DefaultTableReader<>(entityType, tableMapping);
        this.saveTableReader(namespace, id, tableReader, overwrite);
        return tableReader;
    }

    /** 解析 mapper.xml 并载入各种 resultMap */
    public void loadMapper(String resource, MappingOptions defaultOptions) throws SQLException, ClassNotFoundException, IOException {
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
        loadMapper("", root, defaultOptions);
    }

    public void loadMapper(String namespace, Node root, MappingOptions defaultOptions) throws SQLException, ClassNotFoundException, IOException {
        NamedNodeMap rootAttributes = root.getAttributes();
        if (StringUtils.isBlank(namespace) && rootAttributes != null) {
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
            boolean isResultMap = "resultMap".equalsIgnoreCase(node.getNodeName());
            boolean isDataQlMap = "dataqlMap".equalsIgnoreCase(node.getNodeName());
            if (!(isResultMap || isDataQlMap)) {
                continue;
            }
            //
            NamedNodeMap nodeAttributes = node.getAttributes();
            Node idNode = nodeAttributes.getNamedItem("id");
            String idString = (idNode != null) ? idNode.getNodeValue() : null;
            if (StringUtils.isBlank(idString)) {
                throw new IOException("the <" + node.getNodeName() + "> tag is missing an ID.");
            }
            //
            if (isResultMap) {
                loadReader(namespace, idString, node, MappingOptions.resolveOptions(node, defaultOptions), false);
            }
            if (isDataQlMap) {
                loadReader(namespace, idString, node, MappingOptions.resolveOptions(node, defaultOptions), true);
            }
        }
    }

    private void saveTableReader(String namespace, String idString, TableReader<?> tableReader, boolean overwrite) throws SQLException {
        if (StringUtils.isBlank(idString)) {
            throw new SQLException("id is null");
        }
        //
        Map<String, TableReader<?>> sqlMap = null;
        if (StringUtils.isBlank(namespace)) {
            sqlMap = this.defaultSqlMap;
        } else {
            if (!this.spaceSqlMap.containsKey(namespace)) {
                this.spaceSqlMap.put(namespace, new HashMap<>());
            }
            sqlMap = this.spaceSqlMap.get(namespace);
        }
        //
        if (sqlMap.containsKey(idString) && !overwrite) {
            String msg = StringUtils.isBlank(namespace) ? "default namespace" : ("'" + namespace + "' namespace.");
            throw new SQLException("repeat '" + idString + "' in " + msg);
        } else {
            sqlMap.put(idString, tableReader);
        }
    }

    private InputStream loadResource(String resource) throws IOException {
        if (this.classLoader != null) {
            return ResourcesUtils.getResourceAsStream(this.classLoader, resource);
        } else {
            return ResourcesUtils.getResourceAsStream(resource);
        }
    }
}