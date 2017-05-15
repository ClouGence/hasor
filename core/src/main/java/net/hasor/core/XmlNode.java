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
package net.hasor.core;
import java.util.List;
import java.util.Map;
/**
 * Xml属性节点。
 * @version : 2013-4-22
 * @author 赵永春 (zyc@hasor.net)
 */
public interface XmlNode {
    /** @return 获取Xml节点元素名称。*/
    public String getName();

    /** @return 获取Xml节点文本值。*/
    public String getText();

    /** @return 获取Xml节点Xml文本值。*/
    public String getXmlText();

    /** @return 获取属性集合*/
    public Map<String, String> getAttributeMap();

    /** @return 获取Xml子节点。*/
    public List<XmlNode> getChildren();
    //

    /**
     * 获取Xml子节点。
     * @param elementName 子节点名称。
     * @return 返回子节点集合
     */
    public List<XmlNode> getChildren(String elementName);

    /**
     * 获取Xml子节点，如果有多个返回第一条。
     * @param elementName 子节点名称。
     * @return 返回子节点
     */
    public XmlNode getOneChildren(String elementName);

    /**
     * 获取Xml节点上的属性。
     * @param attName 属性名
     * @return 返回属性值
     */
    public String getAttribute(String attName);
}