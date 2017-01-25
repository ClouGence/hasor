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
package net.hasor.plugins.event;
import net.hasor.core.*;
import org.more.future.BasicFuture;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
/**
 * 提供 <code>@Event</code>注解 功能支持。
 *
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class EventModule implements Module {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        final Environment env = apiBinder.getEnvironment();
        final EventContext eventContext = env.getEventContext();
        final Set<Class<?>> eventSet = new HashSet<Class<?>>(env.findClass(Event.class));
        final Class<?> listenerClass = EventListener.class;
        eventSet.remove(Event.class);
        //
        if (eventSet.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("event -> init failed , not found any @Event.");
            }
            return;
        }
        int count = 0;
        for (final Class<?> eventClass : eventSet) {
            if (eventClass == Event.class || !listenerClass.isAssignableFrom(eventClass)) {
                continue;
            }
            Event eventAnno = eventClass.getAnnotation(Event.class);
            String[] eventVar = eventAnno.value();
            EventType eventType = eventAnno.type();
            for (String eventName : eventVar) {
                if (StringUtils.isBlank(eventName)) {
                    continue;
                }
                /*   */
                if (EventType.Once == eventType) {
                    if (logger.isInfoEnabled()) {
                        logger.info("event -> ‘{}’ binding[OnceListener] to ‘{}’", eventName, eventClass);
                    }
                    EventListenerPropxy eventListener = Hasor.autoAware(env, new EventListenerPropxy(eventClass));
                    eventContext.pushListener(eventName, eventListener);
                } else if (EventType.Listener == eventType) {
                    if (logger.isInfoEnabled()) {
                        logger.info("event -> ‘{}’ binding[Listener] to ‘{}’", eventName, eventClass);
                    }
                    EventListenerPropxy eventListener = Hasor.autoAware(env, new EventListenerPropxy(eventClass));
                    eventContext.addListener(eventName, eventListener);
                } else {
                    logger.error("event -> ‘{}’ binding[{}] to ‘{}’ , event type not supported.", eventName, eventType.name(), eventClass);
                    throw new java.lang.UnsupportedOperationException(eventType.name() + " event type not supported");
                }
                count++;
            }
        }
        // 当ContextEvent_Start事件到来时注册所有配置文件监听器。
        if (logger.isInfoEnabled()) {
            logger.info("event -> finish , count ={}.", count);
        }
    }
    //
    private class EventListenerPropxy implements EventListener<Object>, AppContextAware {
        private BasicFuture<AppContext> appContextFuture;
        private Class<?>              eventClass     = null;
        private EventListener<Object> targetListener = null;
        //
        public EventListenerPropxy(Class<?> eventClass) {
            this.eventClass = eventClass;
            this.appContextFuture = new BasicFuture<AppContext>();
        }
        @Override
        public void setAppContext(AppContext appContext) {
            this.appContextFuture.completed(appContext);
        }
        @Override
        public void onEvent(String event, Object eventData) throws Throwable {
            if (eventData == null) {
                return;
            }
            if (this.targetListener == null) {
                AppContext app = this.appContextFuture.get();//无限制等待
                //AppContext app = this.appContextFuture.get(10, TimeUnit.SECONDS);//最大等待时间10秒
                this.targetListener = (EventListener<Object>) app.getInstance(this.eventClass);
            }
            this.targetListener.onEvent(event, eventData);
        }
    }
}