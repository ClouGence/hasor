/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.core;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;
/**
 * Hasor的核心接口，它为应用程序提供了一个统一的配置界面和运行环境。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface AppContext {
    /**获取父亲*/
    public AppContext getParent();
    /**获取上下文*/
    public Object getContext();
    /**获取应用程序配置。*/
    public Settings getSettings();
    /**获取环境接口。*/
    public Environment getEnvironment();
    /**获得所有模块*/
    public ModuleInfo[] getModules();
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> findClass(Class<?> featureType);
    /**表示AppContext是否准备好。*/
    public boolean isReady();
    /**启动。向所有模块发送启动信号，并将容器的状态置为Start。（该方法会尝试init所有模块）*/
    public void start();
    /**判断容器是否处于运行状态*/
    public boolean isStart();
    //
    /*--------------------------------------------------------------------------------------Event*/
    /**容器事件，在所有模块初始化之后引发。
     * @see net.hasor.core.context.AbstractAppContext*/
    public static final String ContextEvent_Initialized = "ContextEvent_Initialized";
    /**容器事件，在所有模块 start 阶段之后引发。
     * @see net.hasor.core.context.AbstractAppContext*/
    public static final String ContextEvent_Started     = "ContextEvent_Started";
    /**模块事件。当模块收到 start 调用信号之后引发。
     * @see net.hasor.core.module.ModuleProxy*/
    public static final String ModuleEvent_Started      = "ModuleEvent_Started";
    //
    /**pushPhaseEvent方法注册的时间监听器当收到一次事件之后会被自动删除。*/
    public void pushListener(String eventType, EventListener eventListener);
    /**添加一种类型事件的事件监听器。*/
    public void addListener(String eventType, EventListener eventListener);
    /**删除某个监听器的注册。*/
    public void removeListener(String eventType, EventListener eventListener);
    /**同步方式抛出事件。当方法返回时已经全部处理完成事件分发。<p>
     * 注意：当某个时间监听器抛出异常时将中断事件分发抛出监听器异常。*/
    public void fireSyncEvent(String eventType, Object... objects);
    /**同步方式抛出事件。当方法返回时已经全部处理完成事件分发。<p>
     * 注意：当某个时间监听器抛出异常时该方法会吞掉异常，继续分发事件。被吞掉的异常会以一条警告的方式出现。*/
    public void fireSyncEvent(String eventType, EventCallBackHook callBack, Object... objects);
    /**异步方式抛出事件。fireAsyncEvent方法的调用不会决定何时开始执行事件，而这一切由事件管理器决定。<p>
     * 注意：当某个时间监听器抛出异常时该方法会吞掉异常，继续分发事件。被吞掉的异常会以一条警告的方式出现。*/
    public void fireAsyncEvent(String eventType, Object... objects);
    /**异步方式抛出事件。fireAsyncEvent方法的调用不会决定何时开始执行事件，而这一切由事件管理器决定。<p>
     * 注意：当某个时间监听器抛出异常时将中断事件分发，并将程序执行权交给异常处理接口。*/
    public void fireAsyncEvent(String eventType, EventCallBackHook callBack, Object... objects);
    //
    /*---------------------------------------------------------------------------------------Bean*/
    /**通过名获取Bean的类型。*/
    public <T> Class<T> getBeanType(String name);
    /**如果存在目标类型的Bean则返回Bean的名称。*/
    public String getBeanName(Class<?> targetClass);
    /**获取已经注册的Bean名称。*/
    public String[] getBeanNames();
    /**创建Bean。*/
    public <T> T getBean(String name);
    /**创建Bean。*/
    public <T> T getInstance(Class<T> targetClass);
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<T> findBindingBean(Class<T> bindingType);
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<Provider<T>> findBindingProvider(Class<T> bindingType);
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> T findBindingBean(String withName, Class<T> bindingType);
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> Provider<T> findBindingProvider(String withName, Class<T> bindingType);
}