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
package net.hasor.spring.factory;
import java.util.ArrayList;
import org.more.util.ExceptionUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.util.SystemPropertyUtils;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.EventContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.context.StatusAppContext;
import net.hasor.core.context.TemplateAppContext;
import net.hasor.core.event.EventObject;
import net.hasor.spring.event.AsyncHasorEvent;
import net.hasor.spring.event.EventType;
import net.hasor.spring.event.HasorEvent;
import net.hasor.spring.event.SyncHasorEvent;
/**
 * 
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class SpringFactoryBean implements FactoryBean, InitializingBean, //
        ApplicationContextAware, //
        ShareEventListener, ApplicationListener, ApplicationEventPublisherAware, //
        Module {
    //
    //
    protected static Logger    logger     = LoggerFactory.getLogger(Hasor.class);
    private AppContext         appContext;
    private ApplicationContext applicationContext;
    private String             config;
    private ArrayList<Module>  modules;
    private boolean            shareEvent = false;
    //
    public String getConfig() {
        return config;
    }
    public void setConfig(String config) {
        this.config = config;
    }
    public ArrayList<Module> getModules() {
        return modules;
    }
    public void setModules(ArrayList<Module> modules) {
        this.modules = modules;
    }
    public boolean isShareEvent() {
        return shareEvent;
    }
    public void setShareEvent(boolean shareEvent) {
        this.shareEvent = shareEvent;
    }
    //
    //
    //
    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    @Override
    public final Object getObject() throws Exception {
        if (this.appContext == null) {
            throw new IllegalStateException("has not been initialized");
        }
        return this.appContext;
    }
    @Override
    public final Class<?> getObjectType() {
        return AppContext.class;
    }
    @Override
    public final boolean isSingleton() {
        return true;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        ArrayList<Module> moduleList = this.getModules();
        String config = this.getConfig();
        // - initData
        if (StringUtils.isBlank(config) == false) {
            config = SystemPropertyUtils.resolvePlaceholders(config);
        }
        if (StringUtils.isBlank(config)) {
            config = TemplateAppContext.DefaultSettings;
        }
        if (moduleList == null) {
            moduleList = new ArrayList<Module>();
        }
        // - AppContext
        try {
            moduleList.add(this);
            Module[] moduleArrays = moduleList.toArray(new Module[moduleList.size()]);
            this.appContext = createAppContext(this.applicationContext, config, moduleArrays);
        } catch (Throwable e) {
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                ExceptionUtils.toRuntimeException(e);
            }
        }
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    protected AppContext createAppContext(ApplicationContext context, final String config, final Module... modules) throws Throwable {
        logger.info("create AppContext ,mainSettings = {} , modules = {}", config, modules);
        ShareEventStandardEnvironment dev = new ShareEventStandardEnvironment(context, config, this);
        BeanContainer container = new BeanContainer(context.getClassLoader());
        AppContext appContext = new StatusAppContext<BeanContainer>(dev, container);
        appContext.start(modules);
        return appContext;
    }
    //
    //
    //
    //- 事件的发布和接收
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public final void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    /*负责将Spring的事件转发到Hasor*/
    @Override
    public final void onApplicationEvent(ApplicationEvent event) {
        if (this.appContext == null || this.shareEvent == false) {
            return;
        }
        //
        if (event instanceof InternalHasorEvent) {
            return;/*如果是由 onEvent 转发过来的事件则忽略*/
        }
        //
        String eventType = null;
        if (event instanceof EventType) {
            eventType = ((EventType) event).getEventType();
        } else {
            eventType = event.getClass().getSimpleName();
        }
        //
        EventContext eventContext = this.appContext.getEnvironment().getEventContext();
        /*   */if (event instanceof SyncHasorEvent) {
            eventContext.fireSyncEvent(eventType, event);
        } else if (event instanceof AsyncHasorEvent) {
            eventContext.fireAsyncEvent(eventType, event);
        } else {
            eventContext.fireSyncEvent(eventType, event);
        }
    }
    /*负责将Hasor的事件转发到Spring*/
    @Override
    public void fireEvent(EventObject<?> eventObj) {
        this.applicationEventPublisher.publishEvent(new InternalHasorEvent(eventObj.getEventType(), eventObj.getEventData()));
    }
    private static class InternalHasorEvent extends HasorEvent {
        private static final long serialVersionUID = 4716275791429045894L;
        public InternalHasorEvent(String event, Object source) {
            super(event, source);
        }
    }
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // TODO Auto-generated method stub
    }
}