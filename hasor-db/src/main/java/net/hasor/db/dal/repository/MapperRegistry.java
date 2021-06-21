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
package net.hasor.db.dal.repository;
import net.hasor.db.dal.RefMapper;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.repository.parser.MethodRepositoryItemParser;
import net.hasor.db.dal.repository.parser.XmlNodeRepositoryItemParser;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.resource.ResourceLoader;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper 配置中心
 * @version : 2021-06-05
 * @author 赵永春 (zyc@byshell.org)
 */
public class MapperRegistry {
    public static final MapperRegistry                       DEFAULT       = new MapperRegistry();
    private final       Map<String, Map<String, DynamicSql>> spaceSqlMap   = new HashMap<>();
    private final       Map<String, DynamicSql>              defaultSqlMap = new HashMap<>();
    private             ResourceLoader                       resourceLoader;

    /** 根据配置 ID 查找 DynamicSql */
    public DynamicSql findDynamicSql(String dynamicId) {
        return this.defaultSqlMap.get(dynamicId);
    }

    /** 根据 namespace 和 ID 查找 DynamicSql */
    public DynamicSql findDynamicSql(Class<?> namespace, String dynamicId) {
        if (namespace == null) {
            return findDynamicSql(dynamicId);
        } else {
            return findDynamicSql(namespace.getName(), dynamicId);
        }
    }

    /** 根据 namespace 和 ID 查找 DynamicSql */
    public DynamicSql findDynamicSql(String namespace, String dynamicId) {
        if (StringUtils.isBlank(namespace)) {
            return findDynamicSql(dynamicId);
        }
        Map<String, DynamicSql> dynamicSqlMap = spaceSqlMap.get(namespace);
        if (dynamicSqlMap == null) {
            return null;
        } else {
            return dynamicSqlMap.get(dynamicId);
        }
    }

    protected void saveDynamicSql(String mapperSpace, String idString, DynamicSql dynamicSql, boolean overwrite) throws IOException {
        Map<String, DynamicSql> sqlMap = null;
        if (StringUtils.isBlank(mapperSpace)) {
            sqlMap = this.defaultSqlMap;
        } else {
            if (!this.spaceSqlMap.containsKey(mapperSpace)) {
                this.spaceSqlMap.put(mapperSpace, new HashMap<>());
            }
            sqlMap = this.spaceSqlMap.get(mapperSpace);
        }
        //
        if (sqlMap.containsKey(idString) && !overwrite) {
            String msg = StringUtils.isBlank(mapperSpace) ? "default namespace" : ("'" + mapperSpace + "' namespace.");
            throw new IOException("repeat '" + idString + "' in " + msg);
        } else {
            sqlMap.put(idString, dynamicSql);
        }
    }

    //------------------------------------------------------------------------------------------------------------- Xml
    protected InputStream loadResource(String resource) throws IOException {
        if (this.resourceLoader != null) {
            return this.resourceLoader.getResourceAsStream(resource);
        } else {
            return ResourcesUtils.getResourceAsStream(resource);
        }
    }

    protected XmlNodeRepositoryItemParser getXmlRepositoryParser() {
        return new XmlNodeRepositoryItemParser();
    }

    protected MethodRepositoryItemParser getMethodRepositoryParser() {
        return new MethodRepositoryItemParser();
    }

    /** 解析并载入 mapper.xml（支持 MyBatis 大部分能力） */
    public void loadMapper(String resource) throws IOException {
        this.loadMapper(resource, null, false);
    }

    /** 解析并载入 mapper.xml（支持 MyBatis 大部分能力） */
    public void loadMapper(String resource, Class<?> refRepository) throws IOException {
        this.loadMapper(resource, refRepository, false);
    }

    /** 解析并载入 mapper.xml（支持 MyBatis 大部分能力） */
    public void loadMapper(String resource, Class<?> refRepository, boolean overwrite) throws IOException {
        String mapperSpace = "";
        if (refRepository != null) {
            mapperSpace = refRepository.getName();
        }
        //
        XmlNodeRepositoryItemParser dynamicParser = getXmlRepositoryParser();
        try (InputStream asStream = loadResource(resource)) {
            if (asStream == null) {
                throw new IOException("mapper resource '" + resource + "' not exist.");
            }
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(asStream));
            Element root = document.getDocumentElement();
            NamedNodeMap rootAttributes = root.getAttributes();
            if (rootAttributes != null) {
                Node namespaceNode = rootAttributes.getNamedItem("namespace");
                if (namespaceNode != null && StringUtils.isBlank(mapperSpace)) {
                    mapperSpace = namespaceNode.getNodeValue();
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
                DynamicSql dynamicSql = dynamicParser.parseSqlConfig(node);
                if (dynamicSql != null) {
                    saveDynamicSql(mapperSpace, idString, dynamicSql, overwrite);
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }
    //---------------------------------------------------------------------------------------------------------- Method

    /** 解析并载入类型 */
    public void loadMapper(Class<?> dalType) throws IOException {
        loadMapper(dalType, false);
    }

    /** 解析并载入类型 */
    public void loadMapper(Class<?> dalType, boolean overwrite) throws IOException {
        if (!dalType.isInterface()) {
            throw new UnsupportedOperationException("the '" + dalType.getName() + "' must interface.");
        }
        //
        if (!dalType.isAnnotationPresent(RefMapper.class) && !MethodRepositoryItemParser.matchType(dalType)) {
            throw new UnsupportedOperationException("type '" + dalType.getName() + "' is not Repository.");
        }
        //
        RefMapper refMapper = dalType.getAnnotation(RefMapper.class);
        if (refMapper != null) {
            loadMapper(refMapper.value(), dalType, overwrite);
            return;
        }
        //
        String mapperSpace = dalType.getName();
        Method[] dalTypeMethods = dalType.getMethods();
        MethodRepositoryItemParser dynamicParser = getMethodRepositoryParser();
        for (Method method : dalTypeMethods) {
            if (!MethodRepositoryItemParser.matchMethod(method)) {
                continue;
            }
            //
            String idString = method.getName();
            DynamicSql dynamicSql = dynamicParser.parseSqlConfig(method);
            this.saveDynamicSql(mapperSpace, idString, dynamicSql, overwrite);
        }
    }
    //---------------------------------------------------------------------------------------------------------- String

    /** 解析并载入 mapper.xml（支持 MyBatis 大部分能力） */
    public void loadXmlString(String mapperSpace, String idString, String xmlString) throws IOException {
        this.loadXmlString(mapperSpace, idString, xmlString, false);
    }

    /** 解析并载入 mapper.xml（支持 MyBatis 大部分能力） */
    public void loadXmlString(String mapperSpace, String idString, String xmlString, boolean overwrite) throws IOException {
        if (StringUtils.isBlank(xmlString)) {
            return;
        }
        //
        xmlString = xmlString.trim();
        boolean isXml = xmlString.startsWith("<") && xmlString.endsWith(">");
        if (!isXml) {
            xmlString = "<select>" + xmlString + "</select>";
        }
        //
        try {
            DynamicSql dynamicSql = this.getXmlRepositoryParser().parseDynamicSql(xmlString);
            if (dynamicSql != null) {
                saveDynamicSql(mapperSpace, idString, dynamicSql, overwrite);
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }
}
