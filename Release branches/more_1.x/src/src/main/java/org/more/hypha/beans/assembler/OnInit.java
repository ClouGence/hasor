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
package org.more.hypha.beans.assembler;
import java.util.List;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.beans.xml.BeansConfig_BeanTypeConfig;
import org.more.hypha.beans.xml.BeansConfig_MDParserConfig;
import org.more.hypha.commons.logic.AbstractBeanBuilder;
import org.more.hypha.commons.logic.EngineLogic;
import org.more.hypha.commons.logic.ValueMetaDataParser;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.InitEvent;
/**
 * beans的初始化EventException
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class OnInit implements EventListener<InitEvent> {
    private static Log log = LogFactory.getLog(OnInit.class);
    public void onEvent(InitEvent event, Sequence sequence) throws Throwable {
        AbstractApplicationContext context = (AbstractApplicationContext) event.toParams(sequence).applicationContext;
        ClassLoader loader = context.getClassLoader();
        //1.获取引擎
        EngineLogic engine = context.getEngineLogic();
        //2.注册Bean类型
        List<B_BeanType> btList = (List<B_BeanType>) context.getFlash().getAttribute(BeansConfig_BeanTypeConfig.BTConfigList);
        if (btList != null)
            for (B_BeanType bt : btList) {
                AbstractBeanBuilder builder = (AbstractBeanBuilder) loader.loadClass(bt.getClassName()).newInstance();
                engine.regeditBeanBuilder(bt.gettName(), builder);
            }
        //3.注册元信息解析器
        List<B_MDParser> mdList = (List<B_MDParser>) context.getFlash().getAttribute(BeansConfig_MDParserConfig.MDParserConfigList);
        if (mdList != null)
            for (B_MDParser mdp : mdList) {
                ValueMetaDataParser parser = (ValueMetaDataParser) loader.loadClass(mdp.getClassName()).newInstance();
                engine.regeditValueMetaDataParser(mdp.getMdType(), parser);
            }
        log.info("hypha.beans init OK!");
    };
}