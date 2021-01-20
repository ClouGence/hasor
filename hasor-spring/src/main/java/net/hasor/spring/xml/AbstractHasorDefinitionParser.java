/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.spring.xml;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.function.Consumer;

/**
 * Spring Xml 解析器基类
 * @version : 2016年2月16日
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractHasorDefinitionParser implements BeanDefinitionParser {
    /** 属性解析 */
    protected final String revertProperty(NamedNodeMap attributes, String attName) {
        Node attNode = attributes.getNamedItem(attName);
        return (attNode != null) ? attNode.getNodeValue() : null;
    }

    /** BeanID */
    protected abstract String beanID(Element element, NamedNodeMap attributes);

    /** 配置Bean */
    protected abstract AbstractBeanDefinition parse(Element element, NamedNodeMap attributes, ParserContext parserContext);

    /** 解析Xml 文件 */
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        NamedNodeMap attributes = element.getAttributes();
        //-解析
        AbstractBeanDefinition definition = parse(element, attributes, parserContext);
        if (definition == null) {
            return null;
        }
        //-将Bean注册到容器中
        return registerBean(element, parserContext, attributes, definition);
    }

    /**  摘抄 Spring 源码，将Bean注册到容器中*/
    private BeanDefinition registerBean(Element element, ParserContext parserContext, NamedNodeMap attributes, AbstractBeanDefinition definition) {
        if (!parserContext.isNested()) {
            try {
                String id = beanID(element, attributes);
                if (!StringUtils.hasText(id)) {
                    parserContext.getReaderContext().error(beanID(element, attributes) + " is undefined. for element '" + element.getLocalName(), element);
                }
                BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id);
                BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());
                parserContext.registerComponent(new BeanComponentDefinition(holder));
            } catch (BeanDefinitionStoreException ex) {
                parserContext.getReaderContext().error(ex.getMessage(), element);
                return null;
            }
        }
        return definition;
    }

    protected void exploreElement(Element element, String elementName, Consumer<Element> consumer) {
        Node node = element.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element entry = (Element) node;
                if (entry.getLocalName().equals(elementName)) {
                    consumer.accept(entry);
                }
            }
            node = node.getNextSibling();
        }
    }

    protected BeanDefinitionHolder createBeanHolder(String beanType, ParserContext parserContext) {
        return createBeanHolder(beanType, parserContext, beanDefinitionBuilder -> {
            //
        });
    }

    protected BeanDefinitionHolder createBeanHolder(String beanType, ParserContext parserContext, Consumer<BeanDefinitionBuilder> buildBean) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanType);
        builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT);
        buildBean.accept(builder);
        //
        AbstractBeanDefinition startWithDefine = builder.getBeanDefinition();
        String beanName = new DefaultBeanNameGenerator().generateBeanName(startWithDefine, parserContext.getRegistry());
        return new BeanDefinitionHolder(startWithDefine, beanName);
    }
}
