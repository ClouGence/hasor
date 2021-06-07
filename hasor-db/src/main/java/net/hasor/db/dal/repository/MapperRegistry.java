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
import net.hasor.db.dal.dynamic.DynamicSql;
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

    public DynamicSql findDynamicSql(String dynamicId) {
        return this.defaultSqlMap.get(dynamicId);
    }

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

    protected InputStream loadResource(String resource) throws IOException {
        if (this.resourceLoader != null) {
            return this.resourceLoader.getResourceAsStream(resource);
        } else {
            return ResourcesUtils.getResourceAsStream(resource);
        }
    }

    protected SqlConfigParser getRepositoryDynamicParser() {
        return new SqlConfigParser();
    }

    public void loadMapper(String resource) throws IOException {
        SqlConfigParser dynamicParser = getRepositoryDynamicParser();
        try (InputStream asStream = loadResource(resource)) {
            if (asStream == null) {
                throw new IOException("mapper resource not exist.");
            }
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(asStream));
            Element root = document.getDocumentElement();
            NamedNodeMap rootAttributes = root.getAttributes();
            String mapperSpace = "";
            if (rootAttributes != null) {
                Node namespaceNode = rootAttributes.getNamedItem("namespace");
                mapperSpace = (namespaceNode != null) ? namespaceNode.getNodeValue() : null;
                if (!this.spaceSqlMap.containsKey(mapperSpace)) {
                    this.spaceSqlMap.put(mapperSpace, new HashMap<>());
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
                    throw new IOException("the ID attribute of the <" + root.getNodeName() + "> tag is empty.");
                }
                DynamicSql dynamicSql = dynamicParser.parseSqlConfig(node);
                if (dynamicSql != null) {
                    this.defaultSqlMap.put(idString, dynamicSql);
                    this.spaceSqlMap.get(mapperSpace).put(idString, dynamicSql);
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }
}
