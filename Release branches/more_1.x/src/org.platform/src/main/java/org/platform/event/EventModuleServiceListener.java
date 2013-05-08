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
package org.platform.event;
import java.util.Set;
import org.platform.Platform;
import org.platform.binder.ApiBinder;
import org.platform.context.AbstractModuleListener;
import org.platform.context.AppContext;
import org.platform.context.InitListener;
/**
 * 事件服务。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@InitListener(displayName = "EventModuleServiceListener", description = "org.platform.event软件包功能支持。", startIndex = -100)
public class EventModuleServiceListener extends AbstractModuleListener {
    private EventManager eventManager = null;
    /**初始化.*/
    @Override
    public void initialize(ApiBinder event) {
        event.getGuiceBinder().bind(EventManager.class).to(DefaultEventManager.class).asEagerSingleton();
    }
    //
    /*装载Listener*/
    protected void loadListener(AppContext appContext) {
        Platform.info("begin loadListener...");
        //1.获取
        Set<Class<?>> listenerSet = appContext.getInitContext().getClassSet(Listener.class);
        for (Class<?> cls : listenerSet) {
            if (EventListener.class.isAssignableFrom(cls) == false) {
                Platform.warning("loadListener : not implemented EventListener of type %s.", cls);
            } else {
                try {
                    Listener annoListener = cls.getAnnotation(Listener.class);
                    EventListener eventListener = (EventListener) appContext.getInstance(cls);
                    for (String eventType : annoListener.value())
                        this.eventManager.addEventListener(eventType, eventListener);
                } catch (Exception e) {
                    Platform.warning("addEventListener error%s.", e);
                }
            }
        }
    }
    @Override
    public void initialized(AppContext appContext) {
        this.eventManager = appContext.getInstance(EventManager.class);
        if (this.eventManager instanceof ManagerLife)
            ((ManagerLife) this.eventManager).initLife(appContext);
        this.loadListener(appContext);
        this.eventManager.throwEvent(EventManager.EventManager_Start_Event);
        Platform.info("EventManager is started.");
    }
    @Override
    public void destroy(AppContext appContext) {
        this.eventManager.throwEvent(EventManager.EventManager_Destroy_Event);
        if (this.eventManager instanceof ManagerLife)
            ((ManagerLife) this.eventManager).destroyLife(appContext);
        Platform.info("EventManager is destroy.");
    }
}