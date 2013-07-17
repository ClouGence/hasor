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
package org.hasor.context;
import java.util.List;
import java.util.Map;
/**
 * Xml属性节点。
 * @version : 2013-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
public interface XmlProperty {
    /**获取Xml节点元素名称。*/
    public String getName();
    /**获取Xml节点文本值。*/
    public String getText();
    /**获取Xml节点Xml文本值。*/
    public String getXmlText();
    /**获取属性集合*/
    public Map<String, String> getAttributeMap();
    /**获取Xml子节点。*/
    public List<XmlProperty> getChildren();
    /**获取父节点*/
    public XmlProperty getParent();
    /**克隆一个XmlProperty*/
    public XmlProperty clone();
}