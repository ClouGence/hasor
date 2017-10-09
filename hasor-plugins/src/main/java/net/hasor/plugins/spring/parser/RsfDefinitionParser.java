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
package net.hasor.plugins.spring.parser;
import net.hasor.plugins.spring.rsf.RsfAddressPropertyEditor;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.utils.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.SpringVersion;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
/**
 *
 * @version : 2016-11-08
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfDefinitionParser implements BeanDefinitionParser {
    /** 属性解析 */
    protected String revertProperty(NamedNodeMap attributes, String attName) {
        Node attNode = attributes.getNamedItem(attName);
        return (attNode != null) ? attNode.getNodeValue() : null;
    }
    /** 解析Xml 文件 */
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        // Spring 版本兼容
        String version = SpringVersion.getVersion();
        version = StringUtils.isBlank(version) ? "?" : version;
        Map customEditors = null;
        if (version.charAt(0) == '4' || version.charAt(0) == '5') {
            customEditors = new HashMap<Class<?>, Class<? extends java.beans.PropertyEditor>>();
            customEditors.put(InterAddress.class, RsfAddressPropertyEditor.class);
        } else {
            customEditors = new HashMap();
            customEditors.put("net.hasor.rsf.InterAddress", new RsfAddressPropertyEditor());
        }
        //
        // .属性编辑器 Bean 定义
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        builder.getRawBeanDefinition().setBeanClass(CustomEditorConfigurer.class);
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);//单例
        builder.addPropertyValue("customEditors", customEditors);
        //
        //  .注册这个属性编辑器,BeanID 为：net.hasor.rsf.spring.RsfAddressPropertyEditor
        AbstractBeanDefinition propEditors = builder.getBeanDefinition();
        String beanID = RsfAddressPropertyEditor.class.getName();
        BeanDefinitionHolder holder = new BeanDefinitionHolder(propEditors, beanID);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());
        parserContext.registerComponent(new BeanComponentDefinition(holder));
        //
        //
        NamedNodeMap attributes = element.getAttributes();
        //
        return null;
    }
}