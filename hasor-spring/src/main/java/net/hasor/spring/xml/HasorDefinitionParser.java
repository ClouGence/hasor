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
import net.hasor.rsf.InterAddress;
import net.hasor.spring.SpringModule;
import net.hasor.spring.beans.AutoScanPackagesModule;
import net.hasor.spring.beans.ContextFactoryBean;
import net.hasor.spring.rsf.RsfAddressPropertyEditor;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
            beanID = SpringModule.DefaultHasorBeanName;
        }
        return beanID.trim();
    }

    private void parsePropertyEditor(ParserContext parserContext) throws IOException {
        if (ResourcesUtils.getResourceAsStream("net.hasor.rsf.InterAddress".replace('.', '/') + ".class") == null) {
            return;
        }
        //
        // Spring 版本兼容
        String version = SpringVersion.getVersion();
        version = StringUtils.isBlank(version) ? "?" : version;
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

    @Override
    protected AbstractBeanDefinition parse(Element element, NamedNodeMap attributes, ParserContext parserContext) {
        try {
            this.parsePropertyEditor(parserContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        // 加载模块
        ManagedList<Object> loadModules = new ManagedList<>();
        loadModules.setSource(ArrayList.class);
        loadModules.setMergeEnabled(false);
        builder.addPropertyValue("loadModules", loadModules);
        String startWith = revertProperty(attributes, "startWith");
        String startWithRef = revertProperty(attributes, "startWithRef");
        if (StringUtils.isNotBlank(startWith) || StringUtils.isNotBlank(startWithRef)) {
            if (StringUtils.isNotBlank(startWithRef)) {
                //-startWithRef
                loadModules.add(new RuntimeBeanReference(startWithRef));
            } else {
                //-startWith
                loadModules.add(createBeanHolder(startWith, parserContext));
            }
        }
        exploreElement(element, "loadModule", node -> {
            // @DimModule 的处理
            String autoScan = node.getAttribute("autoScan");        // 是否启用对 @DimModule 注解的自动扫描
            String scanPackages = node.getAttribute("scanPackages");// 扫描时使用的扫描路径
            if ((Boolean) ConverterUtils.convert(autoScan, Boolean.TYPE) && StringUtils.isNotBlank(scanPackages)) {
                String[] packages = Arrays.stream(scanPackages.split(","))//
                        .filter(StringUtils::isNotBlank)//
                        .toArray(String[]::new);
                loadModules.add(createBeanHolder(AutoScanPackagesModule.class.getName(), parserContext, beanBuilder -> {
                    beanBuilder.addConstructorArgValue(packages);
                }));
            }
            // module 元素
            exploreElement(node, "module", eleMode -> {
                String refBean = eleMode.getAttribute("refBean");// 来自一个Spring Bean
                String classType = eleMode.getAttribute("class");// 来自一个类型
                if (StringUtils.isNotBlank(refBean)) {
                    loadModules.add(new RuntimeBeanReference(refBean));
                } else if (StringUtils.isNotBlank(classType)) {
                    loadModules.add(createBeanHolder(classType, parserContext));
                }
            });
        });
        //
        // 结束
        return builder.getBeanDefinition();
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
