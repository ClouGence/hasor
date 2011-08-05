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
package org.more.hypha.commons.engine.xml;
import java.util.Map;
import java.util.Set;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.commons.logic.EngineLogic;
import org.more.hypha.commons.logic.IocEngine;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.InitEvent;
/**
 * “˝«Ê≥ı ºªØ
 * @version : 2011-4-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class OnInit implements EventListener<InitEvent> {
    private static Log          log       = LogFactory.getLog(OnInit.class);
    private Map<String, String> xmlConfig = null;
    public OnInit(Map<String, String> xmlConfig) {
        this.xmlConfig = xmlConfig;
    }
    public void onEvent(InitEvent event, Sequence sequence) throws Throwable {
        AbstractApplicationContext context = (AbstractApplicationContext) event.toParams(sequence).applicationContext;
        ClassLoader loader = context.getClassLoader();
        EngineLogic logic = context.getEngineLogic();
        if (this.xmlConfig != null) {
            Set<String> keys = this.xmlConfig.keySet();
            int count = keys.size();
            int index = 0;
            for (String k : keys) {
                IocEngine ioc = (IocEngine) loader.loadClass(this.xmlConfig.get(k)).newInstance();
                logic.addIocEngine(k, ioc);
                log.debug("add engine {%0} of {%1} OK!", index, count);
                index++;
            }
        } else
            log.debug("not load ioc engine!");
    }
}