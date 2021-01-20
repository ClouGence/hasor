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
import net.hasor.spring.SpringModule;
import net.hasor.spring.beans.ContextFactoryBean;
import net.hasor.utils.StringUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 负责解析 h:hasor 标签
 * @version : 2016年2月16日
 * @author 赵永春 (zyc@hasor.net)
 */
class HasorDefinitionParser extends AbstractHasorDefinitionParser {
    @Override
    protected String beanID(Element element, NamedNodeMap attributes) {
        String beanID = revertProperty(attributes, "id");
        if (StringUtils.isBlank(beanID)) {
            beanID = SpringModule.DEFAULT_HASOR_BEAN_NAME;
        }
        return beanID.trim();
    }

    @Override
    protected AbstractBeanDefinition parse(Element element, NamedNodeMap attributes, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        builder.getRawBeanDefinition().setBeanClass(ContextFactoryBean.class);
        builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT);
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);//单例
        builder.setLazyInit(false);
        //
        // 引用一个属性配置，并导入到Hasor环境变量中
        String refProperties = revertProperty(attributes, "refProperties");
        if (StringUtils.isNotBlank(refProperties)) {
            builder.addPropertyValue("refProperties", new RuntimeBeanReference(refProperties));
        } else {
            builder.addPropertyValue("refProperties", null);
        }
        // 表示 Hasor 的环境属性变量是否作为配置，默认是 true
        String importPropertiesToSettings = revertProperty(attributes, "useProperties");
        if (StringUtils.isNotBlank(importPropertiesToSettings)) {
            builder.addPropertyValue("useProperties", importPropertiesToSettings);
        }
        //
        // 主配置
        exploreElement(element, "mainConfig", node -> {
            String mainConfig = node.getFirstChild().getNodeValue();
            if (StringUtils.isBlank(mainConfig)) {
                mainConfig = node.getNodeValue();
            }
            if (StringUtils.isNotBlank(mainConfig)) {
                builder.addPropertyValue("mainConfig", mainConfig);
            }
        });
        //
        // 扩展属性
        Map<String, String> customProperties = new HashMap<>();
        builder.addPropertyValue("customProperties", customProperties);
        exploreElement(element, "property", node -> {
            String key = node.getAttribute("name");
            String value = node.getAttribute("value");
            if (StringUtils.isBlank(key)) {
                return;
            }
            if (StringUtils.isBlank(value)) {
                customProperties.put(key, node.getTextContent());
            } else {
                customProperties.put(key, value);
            }
        });
        //
        //
        // 加载模块
        ArrayList<String> loadModules = new ArrayList<>();
        builder.addPropertyValue("loadModules", loadModules);
        String startWith = revertProperty(attributes, "startWith");
        String startWithRef = revertProperty(attributes, "startWithRef");
        if (StringUtils.isNotBlank(startWith) || StringUtils.isNotBlank(startWithRef)) {
            if (StringUtils.isNotBlank(startWithRef)) {
                //-startWithRef
                loadModules.add(startWithRef);
            } else {
                //-startWith
                BeanDefinitionHolder beanHolder = createBeanHolder(startWith, parserContext);
                parserContext.getRegistry().registerBeanDefinition(beanHolder.getBeanName(), beanHolder.getBeanDefinition());
                loadModules.add(beanHolder.getBeanName());
            }
        }
        exploreElement(element, "loadModule", node -> {
            // scanPackages 的处理
            String scanPackages = node.getAttribute("scanPackages");// 扫描时使用的扫描路径
            if (StringUtils.isNotBlank(scanPackages)) {
                String[] packages = Arrays.stream(scanPackages.split(","))//
                        .filter(StringUtils::isNotBlank)//
                        .toArray(String[]::new);
                if (packages.length > 0) {
                    builder.addPropertyValue("scanPackages", packages);
                }
            }
            // module 元素
            exploreElement(node, "module", eleMode -> {
                String refBean = eleMode.getAttribute("refBean");// 来自一个Spring Bean
                String classType = eleMode.getAttribute("class");// 来自一个类型
                if (StringUtils.isNotBlank(refBean)) {
                    loadModules.add(refBean);
                } else if (StringUtils.isNotBlank(classType)) {
                    BeanDefinitionHolder beanHolder = createBeanHolder(classType, parserContext);
                    parserContext.getRegistry().registerBeanDefinition(beanHolder.getBeanName(), beanHolder.getBeanDefinition());
                    loadModules.add(beanHolder.getBeanName());
                }
            });
        });
        //
        // 结束
        return builder.getBeanDefinition();
    }
}
