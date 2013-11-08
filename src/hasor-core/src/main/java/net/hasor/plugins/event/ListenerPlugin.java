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
import static net.hasor.core.AppContext.ContextEvent_Start;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.EventListener;
import net.hasor.core.EventManager;
import net.hasor.core.plugin.AbstractPluginFace;
import net.hasor.core.plugin.Plugin;
import org.more.util.ArrayUtils;
import org.more.util.StringUtils;
/**
 * 提供 <code>@Listener</code>注解 功能支持。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Plugin
public class ListenerPlugin extends AbstractPluginFace {
    public void loadPlugin(ApiBinder apiBinder) {
        final Environment env = apiBinder.getEnvironment();
        final EventManager eventManager = env.getEventManager();
        Set<Class<?>> eventSet = env.getClassSet(Listener.class);
        if (eventSet == null || eventSet.isEmpty())
            return;
        for (final Class<?> eventClass : eventSet) {
            if (EventListener.class.isAssignableFrom(eventClass) == false) {
                Hasor.logWarn("not implemented EventListener :%s", eventClass);
                continue;
            }
            //当ContextEvent_Start事件到来时注册所有配置文件监听器。
            eventManager.pushEventListener(ContextEvent_Start, new EventListener() {
                public void onEvent(String event, Object[] params) {
                    //
                    AppContext appContext = (AppContext) params[0];
                    Listener eventType = eventClass.getAnnotation(Listener.class);
                    String[] var = eventType.value();
                    if (ArrayUtils.isEmpty(var))
                        return;
                    //
                    EventListener e = (EventListener) appContext.getInstance(eventClass);
                    for (String v : var)
                        if (!StringUtils.isBlank(v)) {
                            eventManager.addEventListener(v, e);
                            Hasor.logInfo("event ‘%s’ binding to ‘%s’", v, e);
                        }
                }
            });
            Hasor.logInfo("event binding finish.");
        }
    }
}