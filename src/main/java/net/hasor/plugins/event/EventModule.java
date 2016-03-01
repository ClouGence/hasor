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
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.more.future.BasicFuture;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.core.Environment;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
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
        final Set<Class<?>> eventSet = env.findClass(Event.class);
        if (eventSet == null || eventSet.isEmpty()) {
            return;
        }
        for (final Class<?> eventClass : eventSet) {
            if (eventClass == Event.class || EventListener.class.isAssignableFrom(eventClass) == false) {
                continue;
            }
            Event eventAnno = eventClass.getAnnotation(Event.class);
            String[] eventVar = eventAnno.value();
            EventType eventType = eventAnno.type();
            for (String eventName : eventVar) {
                if (StringUtils.isBlank(eventName)) {
                    continue;
                }
                BindInfo<?> eventInfo = apiBinder.bindType(eventClass).uniqueName().toInfo();
                EventListenerPropxy eventListener = Hasor.autoAware(env, new EventListenerPropxy(eventInfo));
                /*   */if (EventType.Once == eventType) {
                    apiBinder.getEnvironment().getEventContext().pushListener(eventName, eventListener);
                } else if (EventType.Listener == eventType) {
                    apiBinder.getEnvironment().getEventContext().addListener(eventName, eventListener);
                }
                logger.info("event ‘{}’ binding to ‘{}’", eventName, eventClass);
            }
            // 当ContextEvent_Start事件到来时注册所有配置文件监听器。
            logger.info("event binding finish.");
        }
    }
    //
    private class EventListenerPropxy implements EventListener<Object>, AppContextAware {
        private BasicFuture<AppContext> appContextFuture;
        private BindInfo<?>             targetInfo     = null;
        private EventListener<Object>   targetListener = null;
        //
        public EventListenerPropxy(BindInfo<?> targetInfo) {
            this.targetInfo = targetInfo;
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
                this.targetListener = (EventListener<Object>) app.getInstance(this.targetInfo);
            }
            this.targetListener.onEvent(event, eventData);
        }
    }
}