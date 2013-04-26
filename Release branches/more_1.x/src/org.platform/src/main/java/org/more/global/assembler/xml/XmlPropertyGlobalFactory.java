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
package org.more.global.assembler.xml;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.more.core.error.FormatException;
import org.more.global.GlobalFactory;
import org.more.xml.XmlParserKit;
import org.more.xml.register.XmlRegister;
import org.more.xml.stream.XmlReader;
/**
* 解析Xml
* @version : 2011-9-3
* @author 赵永春 (zyc@byshell.org)
*/
public class XmlPropertyGlobalFactory extends GlobalFactory {
    /**默认使用的命名空间。*/
    public static final String DefaultNameSpace    = "http://project.byshell.org/more/schema/global";
    private HashSet<String>    loadNameSpace       = new HashSet<String>();
    private boolean            isIgnoreRootElement = false;
    private XmlProperty        xmlTreeRoot         = null;
    //
    public Map<String, Object> loadConfig(InputStream stream, String encoding, boolean isIgnoreRootElement) throws IOException {
        try {
            if (loadNameSpace.contains(loadNameSpace) == false)
                loadNameSpace.add(DefaultNameSpace);
            if (this.xmlTreeRoot == null)
                this.xmlTreeRoot = new XmlPropertyImpl("");
            XmlParserKit kit = new XmlParserKit();
            kit.regeditHook("/*", new XmlProperty_ElementHook());
            XmlRegister xmlRegister = new XmlRegister(this.xmlTreeRoot);
            for (String ns : loadNameSpace)
                xmlRegister.regeditKit(ns, kit);
            new XmlReader(stream).reader(xmlRegister, encoding, null);
            //2.转换结果
            HashMap<String, Object> returnData = new HashMap<String, Object>();
            this.convertType(returnData, this.xmlTreeRoot.getChildren(), "");
            //3.依照isIgnoreRootElement配置去掉前缀
            if (isIgnoreRootElement == true) {
                HashMap<String, Object> finalReturnData = new HashMap<String, Object>();
                for (Entry<String, Object> ent : returnData.entrySet()) {
                    String keyStr = ent.getKey();
                    keyStr = keyStr.substring(keyStr.indexOf(".") + 1);
                    finalReturnData.put(keyStr, ent.getValue());
                }
                returnData = finalReturnData;
            }
            //
            return returnData;
        } catch (Exception e) {
            if (e instanceof IOException == false)
                throw new FormatException(e);
            else
                throw (IOException) e;
        }
    };
    public Map<String, Object> loadConfig(InputStream stream, String encoding) throws IOException {
        return this.loadConfig(stream, encoding, this.isIgnoreRootElement);
    };
    /**转换成Key Value形式*/
    protected void convertType(Map<String, Object> returnData, List<XmlProperty> xmlPropertyList, String parentAttName) {
        if (xmlPropertyList != null)
            for (XmlProperty xmlProperty : xmlPropertyList) {
                XmlPropertyImpl impl = (XmlPropertyImpl) xmlProperty;
                //1.put本级
                String key = ("".equals(parentAttName)) ? impl.getName() : (parentAttName + "." + impl.getName());
                returnData.put(key, impl);
                //2.put属性
                for (Entry<String, String> ent : impl.getAttributeMap().entrySet())
                    returnData.put(key + "." + ent.getKey(), ent.getValue());
                //3.put孩子
                this.convertType(returnData, xmlProperty.getChildren(), key);
            }
    }
    /**获取要装载的命名空间集合。*/
    public HashSet<String> getLoadNameSpace() {
        return this.loadNameSpace;
    };
    /**判断在解析xml的时候是否放弃根节点。*/
    public boolean isIgnoreRootElement() {
        return isIgnoreRootElement;
    };
    /**设置在解析xml的时候是否放弃根节点。*/
    public void setIgnoreRootElement(boolean isIgnoreRootElement) {
        this.isIgnoreRootElement = isIgnoreRootElement;
    }
    /**设置用于Xml解析时使用的根节点*/
    public XmlProperty getXmlTreeRoot() {
        return xmlTreeRoot;
    }
    /**设置用于Xml解析时使用的根节点*/
    public void setXmlTreeRoot(XmlProperty xmlTreeRoot) {
        this.xmlTreeRoot = xmlTreeRoot;
    };
}