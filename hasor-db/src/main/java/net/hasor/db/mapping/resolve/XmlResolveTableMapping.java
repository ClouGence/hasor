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
package net.hasor.db.mapping.resolve;
import net.hasor.db.mapping.ColumnMapping;
import net.hasor.db.mapping.ColumnMappingDef;
import net.hasor.db.mapping.TableMapping;
import net.hasor.db.mapping.TableMappingDef;
import net.hasor.db.metadata.CaseSensitivityType;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.MetaDataService;
import net.hasor.db.metadata.domain.SimpleColumnDef;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过 Xml 来解析 TableMapping
 * @version : 2021-06-23
 * @author 赵永春 (zyc@hasor.net)
 */
public class XmlResolveTableMapping extends AbstractResolveTableMapping implements ResolveTableMapping<Node> {
    private final ClassResolveTableMapping classResolveTableMapping = new ClassResolveTableMapping();

    @Override
    public TableMapping resolveTableMapping(Node refData, ClassLoader classLoader, TypeHandlerRegistry typeRegistry, MetaDataService metaDataService, MappingOptions options) throws SQLException, ClassNotFoundException {
        NamedNodeMap nodeAttributes = refData.getAttributes();
        Node typeNode = nodeAttributes.getNamedItem("type");
        String type = (typeNode != null) ? typeNode.getNodeValue() : null;
        //
        Class<?> tableType = ClassUtils.getClass(classLoader, type);
        //
        if (options.isAutoMapping()) {
            return this.classResolveTableMapping.resolveTableMapping(tableType, classLoader, typeRegistry, metaDataService, options);
        } else {
            TableMappingDef mappingByType = this.classResolveTableMapping.parserTable(tableType, metaDataService, options);
            return loadTableMappingByConfig(mappingByType, refData, classLoader, typeRegistry, metaDataService);
        }
    }

    private TableMappingDef loadTableMappingByConfig(TableMappingDef mappingByType, Node refData, ClassLoader classLoader, TypeHandlerRegistry typeRegistry, MetaDataService metaDataService) throws SQLException, ClassNotFoundException {
        Map<String, ColumnDef> columnMetaData = new HashMap<>();
        if (metaDataService != null) {
            columnMetaData = metaDataService.getColumnMap(mappingByType.getCatalog(), mappingByType.getSchema(), mappingByType.getTable());
            if (mappingByType.getCaseSensitivity() == CaseSensitivityType.Fuzzy) {
                columnMetaData = new LinkedCaseInsensitiveMap<>(columnMetaData);
            }
        }
        //
        NodeList childNodes = refData.getChildNodes();
        for (int i = 0, len = childNodes.getLength(); i < len; i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String elementName = node.getNodeName().toLowerCase().trim();
            if (StringUtils.isBlank(elementName)) {
                throw new UnsupportedOperationException("tag name is Empty.");
            }
            //
            ColumnMapping columnMapping = null;
            if ("id".equalsIgnoreCase(elementName)) {
                columnMapping = this.loadColumnMapping(true, node, mappingByType, columnMetaData, classLoader, typeRegistry);
            } else if ("result".equalsIgnoreCase(elementName)) {
                columnMapping = this.loadColumnMapping(false, node, mappingByType, columnMetaData, classLoader, typeRegistry);
            } else {
                throw new UnsupportedOperationException("tag <" + elementName + "> Unsupported.");
            }
            //
            mappingByType.addMapping(columnMapping);
        }
        return mappingByType;
    }

    private ColumnMapping loadColumnMapping(boolean asPrimaryKey, Node xmlNode, TableMappingDef mappingByType, Map<String, ColumnDef> columnMetaData, ClassLoader classLoader, TypeHandlerRegistry typeRegistry) throws ClassNotFoundException {
        NamedNodeMap nodeAttributes = xmlNode.getAttributes();
        Node columnNode = nodeAttributes.getNamedItem("column");
        Node propertyNode = nodeAttributes.getNamedItem("property");
        Node javaTypeNode = nodeAttributes.getNamedItem("javaType");
        Node jdbcTypeNode = nodeAttributes.getNamedItem("jdbcType");
        Node typeHandlerNode = nodeAttributes.getNamedItem("typeHandler");
        String column = (columnNode != null) ? columnNode.getNodeValue() : null;
        String property = (propertyNode != null) ? propertyNode.getNodeValue() : null;
        String javaType = (javaTypeNode != null) ? javaTypeNode.getNodeValue() : null;
        String jdbcType = (jdbcTypeNode != null) ? jdbcTypeNode.getNodeValue() : null;
        String typeHandler = (typeHandlerNode != null) ? typeHandlerNode.getNodeValue() : null;
        //
        if (!BeanUtils.hasPropertyOrField(property, mappingByType.entityType())) {
            throw new IllegalStateException("property '" + property + "' undefined.");
        }
        ColumnDef columnMeta = columnMetaData.get(column);
        //
        Class<?> columnJavaType = BeanUtils.getPropertyOrFieldType(mappingByType.entityType(), property);
        if (StringUtils.isNotBlank(javaType)) {
            Class<?> configColumnJavaType = ClassUtils.getClass(classLoader, javaType);
            if (configColumnJavaType.isAssignableFrom(columnJavaType)) {
                columnJavaType = configColumnJavaType;
            } else {
                throw new ClassCastException(configColumnJavaType.getName() + " is not a subclass of " + columnJavaType.getName());
            }
        }
        //
        JDBCType columnJdbcType = null;
        if (StringUtils.isNotBlank(jdbcType)) {
            columnJdbcType = (JDBCType) ConverterUtils.convert(jdbcType, JDBCType.class);
        } else {
            if (columnMeta != null) {
                columnJdbcType = columnMeta.getJdbcType();
            } else {
                columnJdbcType = TypeHandlerRegistry.toSqlType(columnJavaType);
            }
        }
        //
        TypeHandler<?> columnTypeHandler = null;
        if (StringUtils.isNotBlank(typeHandler)) {
            Class<?> configTypeHandlerType = ClassUtils.getClass(classLoader, typeHandler);
            if (TypeHandler.class.isAssignableFrom(configTypeHandlerType)) {
                columnTypeHandler = ClassUtils.newInstance(configTypeHandlerType);
            } else {
                throw new ClassCastException(configTypeHandlerType.getName() + " is not a subclass of " + TypeHandler.class.getName());
            }
        } else {
            columnTypeHandler = typeRegistry.getTypeHandler(columnJavaType, columnJdbcType);
        }
        //
        SimpleColumnDef columnDef = new SimpleColumnDef();
        columnDef.setName(column);
        columnDef.setColumnType(null);
        columnDef.setJdbcType(columnJdbcType);
        columnDef.setJavaType(columnJavaType);
        columnDef.setPrimaryKey(asPrimaryKey);
        if (columnMeta != null) {
            columnDef.setName(columnMeta.getName());
            columnDef.setColumnType(columnMeta.getColumnType());
            columnDef.setPrimaryKey(columnMeta.isPrimaryKey());
        }
        //
        ColumnMappingDef mappingDef = new ColumnMappingDef(property, columnJavaType, columnDef);
        mappingDef.setJdbcType(columnDef.getJdbcType());
        mappingDef.setTypeHandler(columnTypeHandler);
        mappingDef.setInsert(true);
        mappingDef.setUpdate(true);
        return mappingDef;
    }
}
