/*
 * Copyright 2008-2009 the original author or authors.
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
import net.hasor.db.dal.dynamic.nodes.*;
import net.hasor.utils.convert.ConverterUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * 解析动态 SQL 配置
 * @version : 2021-06-05
 * @author 赵永春 (zyc@byshell.org)
 */
public class DynamicParser {
    public DynamicSql parseDynamicSql(String sqlString) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(sqlString)));
        Element root = document.getDocumentElement();
        return parseDynamicSql(root);
    }

    public DynamicSql parseDynamicSql(Node configNode) {
        ArrayDynamicSql arraySqlNode = new ArrayDynamicSql();
        parseNodeList(arraySqlNode, configNode.getChildNodes());
        return arraySqlNode;
    }

    protected String getNodeAttributeValue(Node node, String attributeKey) {
        Node item = node.getAttributes().getNamedItem(attributeKey);
        return item != null ? item.getNodeValue() : null;
    }

    protected void parseNodeList(ArrayDynamicSql parentSqlNode, NodeList nodeList) {
        for (int i = 0, len = nodeList.getLength(); i < len; i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                parseTextSqlNode(parentSqlNode, node);
            } else if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = node.getNodeName();
                if ("foreach".equalsIgnoreCase(nodeName)) {
                    parseForeachSqlNode(parentSqlNode, node);
                } else if ("if".equalsIgnoreCase(nodeName)) {
                    parseIfSqlNode(parentSqlNode, node);
                } else if ("trim".equalsIgnoreCase(nodeName)) {
                    parseTrimSqlNode(parentSqlNode, node);
                } else if ("where".equalsIgnoreCase(nodeName)) {
                    parseWhereSqlNode(parentSqlNode, node);
                } else if ("set".equalsIgnoreCase(nodeName)) {
                    parseSetSqlNode(parentSqlNode, node);
                } else if ("bind".equalsIgnoreCase(nodeName)) {
                    parseBindSqlNode(parentSqlNode, node);
                } else if ("choose".equalsIgnoreCase(nodeName)) {
                    parseChooseSqlNode(parentSqlNode, node);
                } else if ("when".equalsIgnoreCase(nodeName)) {
                    parseWhenSqlNode(parentSqlNode, node);
                } else if ("otherwise".equalsIgnoreCase(nodeName)) {
                    parseOtherwiseSqlNode(parentSqlNode, node);
                } else if ("include".equalsIgnoreCase(nodeName)) {
                    parseIncludeSqlNode(parentSqlNode, node);
                } else {
                    throw new UnsupportedOperationException("Unsupported tags :" + nodeName);
                }
            } else if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
                parseTextSqlNode(parentSqlNode, node);
            }
        }
    }

    /** 工具节点 */
    protected void parseTextSqlNode(ArrayDynamicSql parentSqlNode, Node curXmlNode) {
        parentSqlNode.appendText(curXmlNode.getNodeValue());
    }

    /** 解析 <foreach> 节点 */
    protected void parseForeachSqlNode(ArrayDynamicSql parentSqlNode, Node curXmlNode) {
        String collection = getNodeAttributeValue(curXmlNode, "collection");
        String item = getNodeAttributeValue(curXmlNode, "item");
        String open = getNodeAttributeValue(curXmlNode, "open");
        String close = getNodeAttributeValue(curXmlNode, "close");
        String separator = getNodeAttributeValue(curXmlNode, "separator");
        //
        ArrayDynamicSql parent = new ForeachDynamicSql(collection, item, open, close, separator);
        parentSqlNode.addChildNode(parent);
        this.parseNodeList(parent, curXmlNode.getChildNodes());
    }

    /** 解析 <if> 节点 */
    protected void parseIfSqlNode(ArrayDynamicSql parentSqlNode, Node curXmlNode) {
        String test = getNodeAttributeValue(curXmlNode, "test");
        //
        ArrayDynamicSql parent = new IfDynamicSql(test);
        parentSqlNode.addChildNode(parent);
        this.parseNodeList(parent, curXmlNode.getChildNodes());
    }

    /** 解析 <trim> 节点 */
    protected void parseTrimSqlNode(ArrayDynamicSql parentSqlNode, Node curXmlNode) {
        String prefix = getNodeAttributeValue(curXmlNode, "prefix");
        String prefixOverrides = getNodeAttributeValue(curXmlNode, "prefixOverrides");
        String suffix = getNodeAttributeValue(curXmlNode, "suffix");
        String suffixOverrides = getNodeAttributeValue(curXmlNode, "suffixOverrides");
        boolean caseSensitive = (boolean) ConverterUtils.convert(getNodeAttributeValue(curXmlNode, "caseSensitive"), Boolean.TYPE);
        //
        ArrayDynamicSql parent = new TermDynamicSql(prefix, suffix, prefixOverrides, suffixOverrides, caseSensitive);
        parentSqlNode.addChildNode(parent);
        this.parseNodeList(parent, curXmlNode.getChildNodes());
    }

    /** 解析 <where> 节点 */
    protected void parseWhereSqlNode(ArrayDynamicSql parentSqlNode, Node curXmlNode) {
        //
        ArrayDynamicSql parent = new WhereDynamicSql();
        parentSqlNode.addChildNode(parent);
        this.parseNodeList(parent, curXmlNode.getChildNodes());
    }

    /** 解析 <set> 节点 */
    protected void parseSetSqlNode(ArrayDynamicSql parentSqlNode, Node curXmlNode) {
        //
        ArrayDynamicSql parent = new SetDynamicSql();
        parentSqlNode.addChildNode(parent);
        this.parseNodeList(parent, curXmlNode.getChildNodes());
    }

    /** 解析 <bind> 节点 */
    protected void parseBindSqlNode(ArrayDynamicSql parentSqlNode, Node curXmlNode) {
        String name = getNodeAttributeValue(curXmlNode, "name");
        String value = getNodeAttributeValue(curXmlNode, "value");
        boolean overwrite = (boolean) ConverterUtils.convert(getNodeAttributeValue(curXmlNode, "overwrite"), Boolean.TYPE);
        //
        parentSqlNode.addChildNode(new BindDynamicSql(name, value, overwrite));
    }

    /** 解析 <choose> 节点 */
    protected void parseChooseSqlNode(ArrayDynamicSql parentSqlNode, Node curXmlNode) {
        //
        ArrayDynamicSql parent = new ChooseDynamicSql();
        parentSqlNode.addChildNode(parent);
        this.parseNodeList(parent, curXmlNode.getChildNodes());
    }

    /** 解析 <when> 节点 */
    protected void parseWhenSqlNode(ArrayDynamicSql parentSqlNode, Node curXmlNode) {
        if (!(parentSqlNode instanceof ChooseDynamicSql)) {
            throw new UnsupportedOperationException("the tag `<when>` parent tag must be `<choose>`");
        }
        String test = getNodeAttributeValue(curXmlNode, "test");
        ChooseDynamicSql chooseSqlNode = (ChooseDynamicSql) parentSqlNode;
        //
        ArrayDynamicSql parent = new ArrayDynamicSql();
        chooseSqlNode.addWhen(test, parent);
        this.parseNodeList(parent, curXmlNode.getChildNodes());
    }

    /** 解析 <otherwise> 节点 */
    protected void parseOtherwiseSqlNode(ArrayDynamicSql parentSqlNode, Node curXmlNode) {
        if (!(parentSqlNode instanceof ChooseDynamicSql)) {
            throw new UnsupportedOperationException("the tag `<otherwise>` parent tag must be `<choose>`");
        }
        ChooseDynamicSql chooseSqlNode = (ChooseDynamicSql) parentSqlNode;
        //
        ArrayDynamicSql parent = new ArrayDynamicSql();
        chooseSqlNode.setDefaultNode(parent);
        this.parseNodeList(parent, curXmlNode.getChildNodes());
    }

    /** 解析 <include> 节点 */
    protected void parseIncludeSqlNode(ArrayDynamicSql parentSqlNode, Node curXmlNode) {
        String refId = getNodeAttributeValue(curXmlNode, "refid");
        //
        parentSqlNode.addChildNode(new IncludeDynamicSql(refId));
    }
}
