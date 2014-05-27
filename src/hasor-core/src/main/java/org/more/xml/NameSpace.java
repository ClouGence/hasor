/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.xml;
/**
 * 该类在{@link XmlParserKitManager}类，用于标记命名空间和xpath的对应关系。
 * @version 2010-9-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class NameSpace {
    private String       uri   = null; //命名空间
    private StringBuffer xpath = null; //xpath
    /***/
    public NameSpace(String uri, String xpath) {
        this.uri = uri;
        this.xpath = new StringBuffer(xpath);
    }
    /**获取命名空间。*/
    public String getUri() {
        return uri;
    }
    /**获取xpath。*/
    public String getXpath() {
        return this.xpath.toString();
    }
    /**追加xpath一个节点。*/
    void appendXPath(String name, boolean isAttribute) {
        if (this.xpath.indexOf("/") != this.xpath.length() - 1)
            this.xpath.append("/");
        if (isAttribute == true)
            this.xpath.append("@");
        this.xpath.append(name);
    }
    /**删除xpath的最后一个节点。*/
    void removeXPath() {
        int index = this.xpath.lastIndexOf("/");
        index = (index == 0) ? 1 : index;
        this.xpath = this.xpath.delete(index, this.xpath.length());
    }
}