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
import org.more.UndefinedException;
import com.google.inject.Injector;
import com.google.inject.Provider;
/**
 * Hasor的核心接口，它为应用程序提供了一个统一的配置界面和运行环境。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface AppContext {
    //
    //----------------------------------------------------------------------------------Event
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
    //----------------------------------------------------------------------------------Bean
    /**通过名获取Bean的类型。*/
    public <T> Class<T> getBeanType(String name);
    /**如果存在目标类型的Bean则返回Bean的名称。*/
    public String getBeanName(Class<?> targetClass);
    /**获取已经注册的Bean名称。*/
    public String[] getBeanNames();
    /**通过名称创建bean实例，使用guice，如果获取的bean不存在则会引发{@link UndefinedException}类型异常。*/
    public <T> T getBean(String name);
    /**通过类型创建该类实例，使用guice*/
    public <T> T getInstance(Class<T> targetClass);
    //
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<T> findBindingBean(Class<T> bindingType);
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<Provider<T>> findBindingProvider(Class<T> bindingType);
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> T findBindingBean(String withName, Class<T> bindingType);
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> Provider<T> findBindingProvider(String withName, Class<T> bindingType);
    //
    //----------------------------------------------------------------------------------Context
    /**获得Guice环境。*/
    public Injector getGuice();
    /**获取上下文*/
    public Object getContext();
    /**获取系统启动时间*/
    public long getStartTime();
    /**获取应用程序配置。*/
    public Settings getSettings();
    /**获取环境接口。*/
    public Environment getEnvironment();
    /**获取事件管理器*/
    public EventManager getEventManager();
    /**获得所有模块*/
    public ModuleInfo[] getModules();
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> findClass(Class<?> featureType);
    //
    //----------------------------------------------------------------------------------Life
    /**表示AppContext是否准备好。*/
    public boolean isReady();
    /**启动。向所有模块发送启动信号，并将容器的状态置为Start。（该方法会尝试init所有模块）*/
    public void start();
    /**判断容器是否处于运行状态*/
    public boolean isStart();
}