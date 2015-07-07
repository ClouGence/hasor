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
package net.test.hasor.core._08_event.custom;
import java.util.Set;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Environment;
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import net.hasor.core.StartModule;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 提供 <code>@Listener</code>注解 功能支持。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class ListenerPlugin implements StartModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        final Environment env = apiBinder.getEnvironment();
        final Set<Class<?>> eventSet = env.findClass(Listener.class);
        if (eventSet == null || eventSet.isEmpty()) {
            return;
        }
        for (final Class<?> eventClass : eventSet) {
            if (eventClass == Listener.class || EventListener.class.isAssignableFrom(eventClass) == false) {
                continue;
            }
            Listener eventAnno = eventClass.getAnnotation(Listener.class);
            String[] eventVar = eventAnno.value();
            for (String eventName : eventVar) {
                if (StringUtils.isBlank(eventName)) {
                    continue;
                }
                BindInfo<?> eventInfo = apiBinder.bindType(eventClass).uniqueName().toInfo();
                EventContext ec = apiBinder.getEnvironment().getEventContext();
                ec.addListener(eventName, new EventListenerPropxy(eventInfo));
                logger.info("event ‘{}’ binding to ‘{}’", eventName, eventClass);
            }
            //当ContextEvent_Start事件到来时注册所有配置文件监听器。
            logger.info("event binding finish.");
        }
    }
    private AppContext appContext = null;
    public void onStart(AppContext appContext) throws Throwable {
        this.appContext = appContext;
    }
    private class EventListenerPropxy implements EventListener {
        private EventListener targetListener = null;
        private BindInfo<?>   targetInfo     = null;
        //
        public EventListenerPropxy(BindInfo<?> targetInfo) {
            this.targetInfo = targetInfo;
        }
        public void onEvent(String event, Object[] params) throws Throwable {
            if (this.targetListener == null) {
                this.targetListener = (EventListener) appContext.getInstance(this.targetInfo);
            }
            this.targetListener.onEvent(event, params);
        }
    }
}