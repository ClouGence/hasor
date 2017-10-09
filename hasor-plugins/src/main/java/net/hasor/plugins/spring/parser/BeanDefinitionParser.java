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
import net.hasor.plugins.spring.factory.HasorBean;
import net.hasor.utils.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
/**
 *
 * @version : 2016年2月16日
 * @author 赵永春(zyc@hasor.net)
 */
class BeanDefinitionParser extends AbstractHasorDefinitionParser {
    public BeanDefinitionParser() {
    }
    public BeanDefinitionParser(String factoryID) {
        this.factoryID = factoryID;
    }
    private String factoryID;
    //
    @Override
    protected String beanID() {
        return "id";
    }
    @Override
    protected AbstractBeanDefinition parse(Element element, NamedNodeMap attributes, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        builder.getRawBeanDefinition().setBeanClass(HasorBean.class);
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);//单例
        //
        String factoryID = revertProperty(attributes, "hasorID");
        String refID = revertProperty(attributes, "refID");
        String refType = revertProperty(attributes, "refType");
        String refName = revertProperty(attributes, "refName");
        //
        if (StringUtils.isBlank(factoryID)) {
            factoryID = StringUtils.isBlank(this.factoryID) ? defaultHasorContextBeanName() : this.factoryID;
        }
        if (StringUtils.isNotBlank(refID) || StringUtils.isNotBlank(refType)) {
            builder.addPropertyReference("factory", factoryID);
            builder.addPropertyValue("refID", refID);
            //
            builder.addPropertyValue("refName", refName);
            if (StringUtils.isNotBlank(refType)) {
                try {
                    ClassLoader classLoader = parserContext.getReaderContext().getBeanClassLoader();
                    Class<?> refTypeClass = ClassUtils.forName(refType, classLoader);
                    builder.addPropertyValue("refType", refTypeClass);
                } catch (ClassNotFoundException ex) {
                    parserContext.getReaderContext().error("Bean class [" + refType + "] not found", element, ex);
                }
            }
        } else {
            parserContext.getReaderContext().error("Bean class [" + refType + "] refID and refType ,both undefined.", element);
        }
        return builder.getBeanDefinition();
    }
}