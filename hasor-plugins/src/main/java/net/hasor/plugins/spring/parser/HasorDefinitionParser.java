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
import net.hasor.core.AppContext;
import net.hasor.plugins.spring.factory.SpringFactoryBean;
import net.hasor.utils.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
/**
 * h:hasor 标签
 * @version : 2016年2月16日
 * @author 赵永春(zyc@hasor.net)
 */
class HasorDefinitionParser extends AbstractHasorDefinitionParser {
    @Override
    protected String beanID() {
        return "factoryID";
    }
    @Override
    protected String revertProperty(NamedNodeMap attributes, String attName) {
        String val = super.revertProperty(attributes, attName);
        if (StringUtils.isNotBlank(val)) {
            return val;
        }
        // 是否为 factoryID 属性（默认值处理）
        if (attName.equalsIgnoreCase(this.beanID())) {
            val = this.defaultHasorContextBeanName();
        }
        return val;
    }
    @Override
    protected AbstractBeanDefinition parse(Element element, NamedNodeMap attributes, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        builder.getRawBeanDefinition().setBeanClass(SpringFactoryBean.class);
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);//单例
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
                AbstractBeanDefinition startWithDefine = startWithBuilder.getBeanDefinition();
                String beanName = new DefaultBeanNameGenerator().generateBeanName(startWithDefine, parserContext.getRegistry());
                list.add(new BeanDefinitionHolder(startWithDefine, beanName));
            }
        }
        String refPropertiesBean = revertProperty(attributes, "refProperties");
        builder.addPropertyValue("refProperties", refPropertiesBean);
        //
        String configFile = null;
        String factoryID = revertProperty(attributes, beanID());
        if (StringUtils.isBlank(factoryID)) {
            factoryID = AppContext.class.getName();
        }
        BeanDefinitionParser parser = new BeanDefinitionParser(factoryID);
        //
        Node node = element.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element entry = (Element) node;
                if (entry.getLocalName().equals("configFile")) {
                    configFile = entry.getFirstChild().getNodeValue();
                    if (StringUtils.isBlank(configFile)) {
                        configFile = node.getNodeValue();
                    }
                } else if (entry.getLocalName().equals("bean")) {
                    parser.parse((Element) node, parserContext);
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
}