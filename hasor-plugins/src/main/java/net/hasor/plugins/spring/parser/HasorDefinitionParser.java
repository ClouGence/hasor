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
import net.hasor.plugins.spring.SpringModule;
import net.hasor.plugins.spring.factory.SpringFactoryBean;
import net.hasor.plugins.spring.rsf.RsfAddressPropertyEditor;
import net.hasor.rsf.InterAddress;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.SpringVersion;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * h:hasor 标签
 * @version : 2016年2月16日
 * @author 赵永春(zyc@hasor.net)
 */
class HasorDefinitionParser extends AbstractHasorDefinitionParser {
    @Override
    protected String beanID(Element element, NamedNodeMap attributes) {
        String beanID = revertProperty(attributes, "factoryID");
        if (StringUtils.isBlank(beanID)) {
            beanID = SpringModule.DefaultHasorBeanName;
        }
        return beanID;
    }
    //
    @Override
    protected AbstractBeanDefinition parse(Element element, NamedNodeMap attributes, ParserContext parserContext) {
        try {
            this.parsePropertyEditor(parserContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        builder.getRawBeanDefinition().setBeanClass(SpringFactoryBean.class);
        builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT);
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);//单例
        builder.setLazyInit(true);
        //
        String shareEvent = revertProperty(attributes, "shareEvent");
        if (StringUtils.isNotBlank(shareEvent)) {
            builder.addPropertyValue("shareEvent", Boolean.parseBoolean(shareEvent));
        }
        //
        String startWith = revertProperty(attributes, "startWith");
        String startWithRef = revertProperty(attributes, "startWithRef");
        if (StringUtils.isNotBlank(startWith) || StringUtils.isNotBlank(startWithRef)) {
            ManagedList list = new ManagedList();
            list.setSource(ArrayList.class);
            list.setMergeEnabled(false);
            builder.addPropertyValue("modules", list);
            if (StringUtils.isNotBlank(startWithRef)) {
                //-startWithRef
                String[] refs = startWithRef.split(",");
                for (String refName : refs) {
                    list.add(new RuntimeBeanReference(refName));
                }
            } else {
                //-startWith
                BeanDefinitionBuilder startWithBuilder = BeanDefinitionBuilder.genericBeanDefinition(startWith);
                startWithBuilder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT);
                AbstractBeanDefinition startWithDefine = startWithBuilder.getBeanDefinition();
                String beanName = new DefaultBeanNameGenerator().generateBeanName(startWithDefine, parserContext.getRegistry());
                list.add(new BeanDefinitionHolder(startWithDefine, beanName));
            }
        }
        String refPropertiesBean = revertProperty(attributes, "refProperties");
        builder.addPropertyValue("refProperties", refPropertiesBean);
        HashMap<String, String> envMap = new HashMap<String, String>();
        builder.addPropertyValue("envConfig", envMap);
        //
        String configFile = null;
        Node node = element.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element entry = (Element) node;
                if (entry.getLocalName().equals("configFile")) {
                    configFile = entry.getFirstChild().getNodeValue();
                    if (StringUtils.isBlank(configFile)) {
                        configFile = node.getNodeValue();
                    }
                } else if (entry.getLocalName().equals("property")) {
                    String key = entry.getAttribute("name");
                    if (StringUtils.isBlank(key)) {
                        continue;
                    }
                    envMap.put(key, entry.getTextContent());
                }
            }
            node = node.getNextSibling();
        }
        if (StringUtils.isNotBlank(configFile)) {
            builder.addPropertyValue("config", configFile);
        }
        //
        return builder.getBeanDefinition();
    }
    //
    private void parsePropertyEditor(ParserContext parserContext) throws IOException {
        if (ResourcesUtils.getResourceAsStream("net.hasor.rsf.InterAddress".replace('.', '/') + ".class") == null) {
            return;
        }
        //
        // Spring 版本兼容
        String version = SpringVersion.getVersion();
        version = net.hasor.rsf.utils.StringUtils.isBlank(version) ? "?" : version;
        Map customEditors = null;
        if (version.charAt(0) == '4' || version.charAt(0) == '5') {
            customEditors = new HashMap<Class<?>, Class<? extends PropertyEditor>>();
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
    }
}