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
import org.more.hypha.EventException;
import org.more.hypha.EventListener;
import org.more.hypha.beans.config.B_BeanType;
import org.more.hypha.beans.config.B_MDParser;
import org.more.hypha.beans.config.BeansConfig_BeanTypeConfig;
import org.more.hypha.beans.config.BeansConfig_MDParserConfig;
import org.more.hypha.commons.engine.AbstractBeanBuilder;
import org.more.hypha.commons.engine.EngineLogic;
import org.more.hypha.commons.engine.ValueMetaDataParser;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.InitEvent;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class OnInit implements EventListener<InitEvent> {
    private static ILog log = LogFactory.getLog(OnInit.class);
    private <T> T newInstance(String classname, Sequence eventSequence) throws EventException {
        try {
            return (T) Class.forName(classname).newInstance();
        } catch (InstantiationException e) {
            throw new EventException(eventSequence, "newInstance InstantiationException!", e);
        } catch (IllegalAccessException e) {
            throw new EventException(eventSequence, "newInstance IllegalAccessException!", e);
        } catch (ClassNotFoundException e) {
            throw new EventException(eventSequence, "newInstance ClassNotFoundException!", e);
        }
    };
    public void onEvent(InitEvent event, Sequence sequence) throws EventException {
        AbstractApplicationContext context = (AbstractApplicationContext) event.toParams(sequence).applicationContext;
        //1.获取引擎
        EngineLogic engine = context.getEngineLogic();
        //2.注册Bean类型
        List<B_BeanType> btList = (List<B_BeanType>) context.getFlash().getAttribute(BeansConfig_BeanTypeConfig.BTConfigList);
        if (btList != null)
            for (B_BeanType bt : btList) {
                AbstractBeanBuilder builder = this.newInstance(bt.getClassName(), sequence);
                engine.regeditBeanBuilder(bt.gettName(), builder);
            }
        //3.注册元信息解析器
        List<B_MDParser> mdList = (List<B_MDParser>) context.getFlash().getAttribute(BeansConfig_MDParserConfig.MDParserConfigList);
        if (mdList != null)
            for (B_MDParser mdp : mdList) {
                ValueMetaDataParser parser = this.newInstance(mdp.getClassName(), sequence);
                engine.regeditValueMetaDataParser(mdp.getMdType(), parser);
            }
        //String beanBuilderClass = prop.getProperty(k);
        //Object builder = Class.forName(beanBuilderClass).getConstructor().newInstance();
        //context.getELContext().addELObject(name, elObject);
        //1.初始化bean
        List<String> ns = context.getBeanDefinitionIDs();
        log.info("loadding init bean names = [{%0}].", ns);
        for (String id : ns) {
            AbstractBeanDefine define = context.getBeanDefinition(id);
            if (define.isLazyInit() == false)
                try {
                    context.getBean(id);
                } catch (Throwable e) {
                    log.warning("load Bean {%0} error! ,message = {%1}", id, e);
                }
        }
    }
}