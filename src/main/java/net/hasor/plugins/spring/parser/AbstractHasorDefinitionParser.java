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
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
/**
 *
 * @version : 2016年2月16日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractHasorDefinitionParser implements BeanDefinitionParser {
    /** BeanID 属性名 */
    protected abstract String beanID();

    /** 配置Bean */
    protected abstract AbstractBeanDefinition parse(Element element, NamedNodeMap attributes, ParserContext parserContext);
    //
    /** 属性解析 */
    protected String revertProperty(NamedNodeMap attributes, String attName) {
        Node attNode = attributes.getNamedItem(attName);
        return (attNode != null) ? attNode.getNodeValue() : null;
    }
    protected String defaultHasorContextBeanName() {
        return SpringModule.DefaultHasorBeanName;
    }
    //
    //
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
    /**摘抄 Spring 源码，将Bean注册到容器中*/
    private BeanDefinition registerBean(Element element, ParserContext parserContext, NamedNodeMap attributes, AbstractBeanDefinition definition) {
        if (!parserContext.isNested()) {
            try {
                String id = revertProperty(attributes, beanID());
                if (!StringUtils.hasText(id)) {
                    parserContext.getReaderContext().error(beanID() + " is undefined. for element '" + element.getLocalName(), element);
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
}