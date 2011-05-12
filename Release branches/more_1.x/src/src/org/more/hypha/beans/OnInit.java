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
package org.more.hypha.beans;
import java.util.List;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.beans.config.B_BeanType;
import org.more.hypha.beans.config.B_MDParser;
import org.more.hypha.beans.config.BeansConfig_BeanTypeConfig;
import org.more.hypha.beans.config.BeansConfig_MDParserConfig;
import org.more.hypha.commons.engine.AbstractBeanBuilder;
import org.more.hypha.commons.engine.ValueMetaDataParser;
import org.more.hypha.commons.engine.ioc.Ioc_BeanEngine;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.app.InitEvent;
/**
 * 
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
class OnInit implements EventListener<InitEvent> {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void onEvent(InitEvent event, Sequence sequence) {
        AbstractApplicationContext context = (AbstractApplicationContext) event.toParams(sequence).applicationContext;
        //1.注册引擎到
        Ioc_BeanEngine engine = new Ioc_BeanEngine(context);
        context.addBeanEngine(Ioc_BeanEngine.EngineName, engine);
        //2.注册Bean类型
        List<B_BeanType> btList = (List<B_BeanType>) context.getFlash().getAttribute(BeansConfig_BeanTypeConfig.BTConfigList);
        for (B_BeanType bt : btList) {
            AbstractBeanBuilder builder = (AbstractBeanBuilder) Class.forName(bt.getClassName()).newInstance();
            engine.regeditBeanBuilder(bt.gettName(), builder);
        }
        //3.注册元信息解析器
        List<B_MDParser> mdList = (List<B_MDParser>) context.getFlash().getAttribute(BeansConfig_MDParserConfig.MDParserConfigList);
        for (B_MDParser mdp : mdList) {
            ValueMetaDataParser parser = (ValueMetaDataParser) Class.forName(mdp.getClassName()).newInstance();
            engine.regeditValueMetaDataParser(mdp.getMdType(), parser);
        }
        //String beanBuilderClass = prop.getProperty(k);
        //Object builder = Class.forName(beanBuilderClass).getConstructor().newInstance();
        //context.getELContext().addELObject(name, elObject);
        //1.初始化bean
        for (String id : context.getBeanDefinitionIDs()) {
            AbstractBeanDefine define = context.getBeanDefinition(id);
            if (define.isLazyInit() == false)
                context.getBean(id);
        }
    }
}