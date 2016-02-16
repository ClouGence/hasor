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
package net.hasor.spring.factory.xml;
import java.util.ArrayList;
import org.more.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.hasor.spring.factory.SpringFactoryBean;
/**
 * 
 * @version : 2016年2月16日
 * @author 赵永春(zyc@hasor.net)
 */
public class HasorNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("hasor", new FactoryHasorDefinitionParser());
    }
}
class FactoryHasorDefinitionParser extends HasorDefinitionParser {
    @Override
    protected AbstractBeanDefinition parse(Element element, NamedNodeMap attributes, BeanDefinitionBuilder builder, ParserContext parserContext) {
        builder.getRawBeanDefinition().setBeanClass(SpringFactoryBean.class);
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);//单例
        //
        String shareEvent = revertProperty(attributes, "shareEvent");
        if (org.more.util.StringUtils.isNotBlank(shareEvent)) {
            builder.addPropertyValue("shareEvent", Boolean.parseBoolean(shareEvent));
        }
        //
        String startWith = revertProperty(attributes, "startWith");
        String startWithRef = revertProperty(attributes, "startWithRef");
        if (org.more.util.StringUtils.isNotBlank(startWith) || org.more.util.StringUtils.isNotBlank(startWithRef)) {
            ManagedList list = new ManagedList();
            list.setSource(ArrayList.class);
            list.setMergeEnabled(false);
            builder.addPropertyValue("modules", list);
            if (org.more.util.StringUtils.isNotBlank(startWithRef)) {
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
        //
        NodeList nodeList = element.getChildNodes();
        String configFile = null;
        if (nodeList != null) {
            int length = nodeList.getLength();
            for (int i = 0; i < length; i++) {
                //
                Node node = nodeList.item(i);
                if (node.getNodeName().equalsIgnoreCase("configFile")) {
                    configFile = this.revertProperty(node.getAttributes(), "resource");
                    if (StringUtils.isBlank(configFile)) {
                        configFile = node.getNodeValue();
                    }
                    if (StringUtils.isNotBlank(configFile)) {
                        break;
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(configFile)) {
            builder.addPropertyValue("config", configFile);
        }
        //
        return builder.getBeanDefinition();
    }
}