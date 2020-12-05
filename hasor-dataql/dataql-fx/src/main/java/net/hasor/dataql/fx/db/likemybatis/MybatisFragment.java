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
package net.hasor.dataql.fx.db.likemybatis;
import net.hasor.dataql.Hints;
import net.hasor.dataql.fx.db.fxquery.FxQuery;
import net.hasor.dataql.fx.db.runsql.SqlFragment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * 支持 Mybatis 的代码片段执行器。整合了分页、批处理能力。
 * 已支持的标签有：select/insert/delete/update/if/foreach
 *
 * @author jmxd
 * @version : 2020-05-18
 */
@Singleton
public class MybatisFragment extends SqlFragment {
    @Override
    public Object runFragment(Hints hint, Map<String, Object> paramMap, String fragmentString) throws Throwable {
        SqlNode sqlNode = parseSqlNode(fragmentString.trim());
        FxQuery fxSql = new MybatisSqlQuery(sqlNode);
        if (usePage(hint)) {
            return this.usePageFragment(fxSql, hint, paramMap);
        } else {
            return this.noPageFragment(fxSql, hint, paramMap);
        }
    }

    /**
     * 枷锁防止多线程事件
     * @param fragmentString
     * @return
     * @throws Exception
     */
    private synchronized SqlNode parseSqlNode(String fragmentString) throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(fragmentString.getBytes()));
        Element root = document.getDocumentElement();
        String tagName = root.getTagName();
        SqlNode sqlNode = new TextSqlNode("");
        if ("select".equalsIgnoreCase(tagName)) {
            sqlNode.setSqlNode(SqlMode.Query);
        } else if ("update".equalsIgnoreCase(tagName)) {
            sqlNode.setSqlNode(SqlMode.Update);
        } else if ("insert".equalsIgnoreCase(tagName)) {
            sqlNode.setSqlNode(SqlMode.Insert);
        } else if ("delete".equalsIgnoreCase(tagName)) {
            sqlNode.setSqlNode(SqlMode.Delete);
        } else {
            return sqlNode;
        }
        parseNodeList(sqlNode, root.getChildNodes());
        return sqlNode;
    }

    private void parseNodeList(SqlNode sqlNode, NodeList nodeList) {
        for (int i = 0, len = nodeList.getLength(); i < len; i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                sqlNode.addChildNode(new TextSqlNode(node.getNodeValue().trim()));
            } else if (node.getNodeType() != Node.COMMENT_NODE) {
                String nodeName = node.getNodeName();
                SqlNode childNode;
                if ("foreach".equalsIgnoreCase(nodeName)) {
                    childNode = parseForeachSqlNode(node);
                } else if ("if".equalsIgnoreCase(nodeName)) {
                    childNode = new IfSqlNode(getNodeAttributeValue(node, "test"));
                // 增加@@mybatis对<trim>、<set>、<where>标签的支持 
                // 添加 by zhangxu begin
                } else if ("trim".equalsIgnoreCase(nodeName)) {
                    childNode = parseTrimSqlNode(node);
                } else if ("set".equalsIgnoreCase(nodeName)) {
                    childNode = parseSetSqlNode(node);
                } else if ("where".equalsIgnoreCase(nodeName)) {
                    childNode = parseWhereSqlNode(node);
                //添加 by zhangxu end
                } else {
                    throw new UnsupportedOperationException("Unsupported tags :" + nodeName);
                }
                sqlNode.addChildNode(childNode);
                if (node.hasChildNodes()) {
                    parseNodeList(childNode, node.getChildNodes());
                }
            }
        }
    }

    /** 解析foreach节点 */
    private ForeachSqlNode parseForeachSqlNode(Node node) {
        ForeachSqlNode foreachSqlNode = new ForeachSqlNode();
        foreachSqlNode.setCollection(getNodeAttributeValue(node, "collection"));
        foreachSqlNode.setSeparator(getNodeAttributeValue(node, "separator"));
        foreachSqlNode.setClose(getNodeAttributeValue(node, "close"));
        foreachSqlNode.setOpen(getNodeAttributeValue(node, "open"));
        foreachSqlNode.setItem(getNodeAttributeValue(node, "item"));
        return foreachSqlNode;
    }
    
    // 增加@@mybatis对<trim>、<set>、<where>标签的支持
    // 添加 by zhangxu begin
    /** 解析trim节点 */
    private TrimSqlNode parseTrimSqlNode(Node node) {
        TrimSqlNode trimSqlNode = new TrimSqlNode();
        trimSqlNode.setPrefix(getNodeAttributeValue(node, "prefix"));
        trimSqlNode.setPrefixOverrides(getNodeAttributeValue(node, "prefixOverrides"));
        trimSqlNode.setSuffix(getNodeAttributeValue(node, "suffix"));
        trimSqlNode.setSuffixOverrides(getNodeAttributeValue(node, "suffixOverrides"));
        return trimSqlNode;
    }
    
    /** 解析set节点 */
    private SetSqlNode parseSetSqlNode(Node node) {
        SetSqlNode setSqlNode = new SetSqlNode();
        return setSqlNode;
    }
    
    /** 解析where节点 */
    private WhereSqlNode parseWhereSqlNode(Node node) {
        WhereSqlNode whereSqlNode = new WhereSqlNode();
        return whereSqlNode;
    }
    //添加 by zhangxu end

    private String getNodeAttributeValue(Node node, String attributeKey) {
        Node item = node.getAttributes().getNamedItem(attributeKey);
        return item != null ? item.getNodeValue() : null;
    }
}