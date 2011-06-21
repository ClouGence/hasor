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
package org.more.hypha.commons.engine;
import java.util.Map;
import java.util.Set;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.commons.logic.EngineLogic;
import org.more.hypha.commons.logic.IocEngine;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.InitEvent;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * “˝«Ê≥ı ºªØ
 * @version : 2011-4-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
class OnInit implements EventListener<InitEvent> {
    private static ILog log = LogFactory.getLog(OnInit.class);
    public void onEvent(InitEvent event, Sequence sequence) throws Throwable {
        AbstractApplicationContext context = (AbstractApplicationContext) event.toParams(sequence).applicationContext;
        ClassLoader loader = context.getBeanClassLoader();
        EngineLogic logic = context.getEngineLogic();
        Map<String, String> mapping = (Map<String, String>) context.getFlash().getAttribute(TagEngine_Engine.ConfigList);
        if (mapping != null) {
            Set<String> keys = mapping.keySet();
            int count = keys.size();
            int index = 0;
            for (String k : keys) {
                IocEngine ioc = (IocEngine) loader.loadClass(mapping.get(k)).newInstance();
                logic.addIocEngine(k, ioc);
                log.debug("add engine {%0} of {%1} OK!", index, count);
                index++;
            }
        } else
            log.debug("not load ioc engine!");
    }
}