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
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.StartedServicesEvent;
/**
 * beans的初始化EventException
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
class OnStarted implements EventListener<StartedServicesEvent> {
    private static Log log = LogFactory.getLog(OnStarted.class);
    public void onEvent(StartedServicesEvent event, Sequence sequence) throws Throwable {
        AbstractApplicationContext context = (AbstractApplicationContext) event.toParams(sequence).applicationContext;
        //1.初始化bean
        List<String> ns = context.getBeanDefinitionIDs();
        log.info("loadding init bean names = [{%0}].", ns);
        if (ns != null)
            for (String id : ns) {
                AbstractBeanDefine define = context.getBeanDefinition(id);
                if (define.isLazyInit() == false)
                    try {
                        context.getBean(id);
                    } catch (Throwable e) {
                        log.warning("load Bean {%0} error! ,message = {%1}", id, e);
                        throw e;
                    }
            }
    };
}