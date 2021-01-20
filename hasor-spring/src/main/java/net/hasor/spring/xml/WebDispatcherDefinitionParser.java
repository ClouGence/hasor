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
import net.hasor.spring.beans.SpringHasorDispatcher;
import net.hasor.utils.StringUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;

/**
 * 负责解析 web-dispatcher 标签
 * @version : 2016年2月16日
 * @author 赵永春 (zyc@hasor.net)
 */
class WebDispatcherDefinitionParser extends AbstractHasorDefinitionParser {
    @Override
    protected String beanID(Element element, NamedNodeMap attributes) {
        String beanID = revertProperty(attributes, "id");
        if (StringUtils.isBlank(beanID)) {
            beanID = SpringHasorDispatcher.class.getName();
        }
        return beanID;
    }

    @Override
    protected AbstractBeanDefinition parse(Element element, NamedNodeMap attributes, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        builder.getRawBeanDefinition().setBeanClass(SpringHasorDispatcher.class);
        builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT);
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);//单例
        //builder.addDependsOn("requestMappingHandlerMapping");
        //
        List<String> mappingPath = new ArrayList<>();
        builder.addPropertyValue("mappingPath", mappingPath);
        exploreElement(element, "mapping", node -> {
            String path = node.getAttribute("path");
            if (StringUtils.isNotBlank(path)) {
                mappingPath.add(path);
            }
        });
        //
        return builder.getBeanDefinition();
    }
}
