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
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.Environment;
import net.hasor.core.EventListener;
import net.hasor.core.EventManager;
import net.hasor.core.Hasor;
import net.hasor.core.plugin.AbstractHasorPlugin;
import net.hasor.quick.plugin.Plugin;
import org.more.util.StringUtils;
/**
 * 提供 <code>@Listener</code>注解 功能支持。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Plugin
public class ListenerPlugin extends AbstractHasorPlugin {
    public void loadPlugin(ApiBinder apiBinder) {
        final Environment env = apiBinder.getEnvironment();
        final EventManager eventManager = env.getEventManager();
        final Set<Class<?>> eventSet = env.findClass(Listener.class);
        if (eventSet == null || eventSet.isEmpty())
            return;
        for (final Class<?> eventClass : eventSet) {
            /*排除没有实现 EventListener 接口的类。*/
            if (EventListener.class.isAssignableFrom(eventClass) == false) {
                Hasor.logWarn("not implemented EventListener :%s", eventClass);
                continue;
            }
            Listener eventAnno = eventClass.getAnnotation(Listener.class);
            String[] eventVar = eventAnno.value();
            for (String eventName : eventVar) {
                if (StringUtils.isBlank(eventName))
                    continue;
                //
                EventListenerPropxy listener = new EventListenerPropxy(eventClass);
                apiBinder.registerAware(listener);/*注册AppContextAware*/
                eventManager.addListener(eventName, listener);
                Hasor.logInfo("event ‘%s’ binding to ‘%s’", eventName, eventClass);
            }
            //当ContextEvent_Start事件到来时注册所有配置文件监听器。
            Hasor.logInfo("event binding finish.");
        }
    }
    //
    //
    //
    private static class EventListenerPropxy implements EventListener, AppContextAware {
        private AppContext    appContext  = null;
        private Class<?>      eventClass  = null;
        private EventListener eventTarget = null;
        //
        public EventListenerPropxy(Class<?> eventClass) {
            this.eventClass = eventClass;
        }
        public void setAppContext(AppContext appContext) {
            this.appContext = appContext;
        }
        //
        public void onEvent(String event, Object[] params) throws Throwable {
            if (this.eventTarget == null) {
                if (appContext != null)
                    this.eventTarget = (EventListener) this.appContext.getInstance(this.eventClass);
                else
                    this.eventTarget = (EventListener) this.eventClass.newInstance();
            }
            this.eventTarget.onEvent(event, params);
        }
    }
}