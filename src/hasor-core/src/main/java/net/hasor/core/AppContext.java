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
import net.hasor.core.register.ServicesRegisterHandler;
import org.more.UndefinedException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 应用程序上下文
 * @version : 2013-3-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface AppContext {
    //----------------------------------------------------------------------------------Event
    /**容器事件，在所有模块初始化之前引发。
     * @see net.hasor.core.context.AbstractAppContext*/
    public static final String ContextEvent_Initialize  = "ContextEvent_Initialize";
    /**容器事件，在所有模块初始化之后引发。
     * @see net.hasor.core.context.AbstractAppContext*/
    public static final String ContextEvent_Initialized = "ContextEvent_Initialized";
    /**容器事件，在所有模块 start 阶段之前引发。
     * @see net.hasor.core.context.AbstractAppContext*/
    public static final String ContextEvent_Start       = "ContextEvent_Start";
    /**容器事件，在所有模块处理完 stop 阶段之后引发。
     * @see net.hasor.core.context.AbstractAppContext*/
    public static final String ContextEvent_Stoped      = "ContextEvent_Stoped";
    /**模块事件。当模块收到 start 调用信号之后引发。
     * @see net.hasor.core.module.ModulePropxy*/
    public static final String ModuleEvent_Start        = "ModuleEvent_Start";
    /**模块事件。当模块处理完 stop 调用信号之后引发。
     * @see net.hasor.core.module.ModulePropxy*/
    public static final String ModuleEvent_Stoped       = "ModuleEvent_Stoped";
    //
    //----------------------------------------------------------------------------------ServicesRegister
    /**注册服务。
     * @see net.hasor.core.register.ServicesRegisterHandler*/
    public <T> void registerService(Class<T> type, T serviceBean, Object... objects);
    /**注册服务。
     * @see net.hasor.core.register.ServicesRegisterHandler*/
    public <T> void registerService(Class<T> type, Class<? extends T> serviceType, Object... objects);
    /**注册服务。
     * @see net.hasor.core.register.ServicesRegisterHandler*/
    public <T> void registerService(Class<T> type, Key<? extends T> serviceKey, Object... objects);
    /**解除注册服务。
     * @see net.hasor.core.register.ServicesRegisterHandler*/
    public <T> void unRegisterService(Class<T> type, T serviceBean);
    /**解除注册服务。
     * @see net.hasor.core.register.ServicesRegisterHandler*/
    public <T> void unRegisterService(Class<T> type, Class<? extends T> serviceType);
    /**解除注册服务。
     * @see net.hasor.core.register.ServicesRegisterHandler*/
    public <T> void unRegisterService(Class<T> type, Key<? extends T> serviceKey);
    /**解除注册服务。
     * @see net.hasor.core.register.ServicesRegisterHandler*/
    public ServicesRegisterHandler lookUpRegisterService(Class<?> type);
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
    public <T> T getInstance(Class<T> beanType);
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<T> findBeanByType(Class<T> bindingType);
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<Provider<T>> findProviderByType(Class<T> bindingType);
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> T findBeanByType(String withName, Class<T> bindingType);
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> Provider<T> findProviderByType(String withName, Class<T> bindingType);
    //
    //----------------------------------------------------------------------------------Context
    /**获得Guice环境。*/
    public Injector getGuice();
    /**获取上下文*/
    public Object getContext();
    /**获取系统启动时间*/
    public long getStartTime();
    /**表示AppContext是否准备好。*/
    public boolean isReady();
    /**获取应用程序配置。*/
    public Settings getSettings();
    /**获取环境接口。*/
    public Environment getEnvironment();
    /**获取事件操作接口。*/
    public EventManager getEventManager();
    /**获得所有模块*/
    public ModuleInfo[] getModules();
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> getClassSet(Class<?> featureType);
    //
    //----------------------------------------------------------------------------------Life
    /**启动。向所有模块发送启动信号，并将容器的状态置为Start。（该方法会尝试init所有模块）*/
    public void start();
    /**停止。向所有模块发送停止信号，并将容器的状态置为Stop。*/
    public void stop();
    /**判断容器是否处于运行状态*/
    public boolean isStart();
}