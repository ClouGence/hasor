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
package net.hasor.core;
import java.lang.reflect.Constructor;
import java.util.List;
/**
 * Hasor的核心接口，它为应用程序提供了一个统一的配置界面和运行环境。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface AppContext {
    /** 容器事件，在所有模块 start 阶段之后引发。
     * @see net.hasor.core.context.TemplateAppContext*/
    public static final String ContextEvent_Started  = "ContextEvent_Started";
    /** 容器事件，在所有模块 start 阶段之后引发。
     * @see net.hasor.core.context.TemplateAppContext*/
    public static final String ContextEvent_Shutdown = "ContextEvent_Shutdown";

    /** @return 获取 {@link Environment} */
    public Environment getEnvironment();

    /** 获取当创建Bean时使用的{@link ClassLoader} */
    public ClassLoader getClassLoader();

    /**
     * 模块启动通知，如果在启动期间发生异常，将会抛出该异常。
     * @param modules 启动时使用的模块。
     * @throws Throwable 启动过程中引发的异常。
     */
    public void start(Module... modules) throws Throwable;

    /**
     * 确定 AppContext 目前状态是否处于启动状态。
     * @return 返回 true 表示已经完成初始化并且启动完成。false表示尚未完成启动过程。
     */
    public boolean isStart();

    /** 发送停止通知 */
    public void shutdown();
    //
    /*---------------------------------------------------------------------------------------Bean*/

    /** 通过 bindID 获取 Bean 的类型 */
    public Class<?> getBeanType(String bindID);

    /** @return 获取当前所有 bindID */
    public String[] getBindIDs();

    /** @return 如果存在目标类型的 Bean 则返回 Bean 的名称 */
    public String[] getNames(Class<?> targetClass);

    /** @return 判断是否存在某个 bindID */
    public boolean containsBindID(String bindID);

    /** 根据 bindID 获取{@link BindInfo} */
    public <T> BindInfo<T> getBindInfo(String bindID);

    /** 根据类型获取{@link BindInfo}，该方法相当于 findBindingRegister(null,bindType) */
    public <T> BindInfo<T> getBindInfo(Class<T> bindType);

    /** 根据构造方法获取{@link BindInfo}，该方法相当于 findBindingRegister(null,bindConstructor) */
    public <T> BindInfo<T> getBindInfo(Constructor<T> bindConstructor);

    /** 根据 bindID 创建Bean */
    public <T> T getInstance(String bindID);

    /** 根据类型创建 Bean */
    public <T> T getInstance(Class<T> targetClass);

    /** 根据构造方法创建 Bean */
    public <T> T getInstance(Constructor<T> targetConstructor);

    /** 根据 BindInfo 创建 Bean */
    public <T> T getInstance(BindInfo<T> info);

    /** 根据 bindID 创建 Bean 的 Provider */
    public <T> Provider<T> getProvider(String bindID);

    /** 根据类型创建创建 Bean 的 Provider */
    public <T> Provider<T> getProvider(Class<T> targetClass);

    /** 根据构造方法创建 Bean 的 Provider */
    public <T> Provider<T> getProvider(Constructor<T> targetConstructor);

    /** 根据 BindInfo 创建 Bean 的 Provider */
    public <T> Provider<T> getProvider(BindInfo<T> info);

    /** 对 object 对象仅执行依赖注入，要注入的属性等信息参照：findBindingRegister(null,object.getClass())。
     * 如果参照信息为空，那么将直接 return object。 */
    public <T> T justInject(T object);

    /** 对 object 对象仅执行依赖注入，要注入的属性等信息参照：findBindingRegister(null,bindType)。
     * 如果参照信息为空，那么将直接 return object。 */
    public <T> T justInject(T object, Class<?> beanType);

    /** 对 object 对象仅执行依赖注入，要注入的属性等信息参照：bindInfo。
     * 如果参照信息为空，那么将直接 return object。 */
    public <T> T justInject(T object, BindInfo<?> bindInfo);


    /*-------------------------------------------------------------------------------------Binder*/

    /**
     * 获取可以构建出 bindType 的所有 BindInfo，最后创建这些 Bean 对象。
     * @param bindType bean type
     * @return 返回符合条件的绑定对象。
     */
    public <T> List<T> findBindingBean(Class<T> bindType);

    /**
     * 获取可以构建出 bindType 的所有 BindInfo，最后创建这些 Bean 对象
     * （Provider形式返回，真正创建 Bean 的时机是当调用 Provider.get 方法时，相当于Lazy）
     * @param bindType bean type
     * @return 返回符合条件的绑定对象。
     */
    public <T> List<Provider<T>> findBindingProvider(Class<T> bindType);

    /**
     * 根据名字和类型查找对应的 BindInfo 然后创建这个 Bean。
     * @param withName name
     * @param bindType bean type
     * @return 返回符合条件的绑定对象。
     */
    public <T> T findBindingBean(String withName, Class<T> bindType);

    /**
     * 根据名字和类型查找对应的 BindInfo 然后创建这个 Bean。
     * （Provider形式返回，真正创建 Bean 的时机是当调用 Provider.get 方法时，相当于Lazy）
     * @param withName 绑定名称。
     * @param bindType bean type
     * @return 返回{@link Provider}形式对象。
     */
    public <T> Provider<T> findBindingProvider(String withName, Class<T> bindType);

    /**
     * 获取可以构建出 bindType 的所有 BindInfo。
     * @param bindType bean type
     * @return 返回所有符合条件的绑定信息。
     */
    public <T> List<BindInfo<T>> findBindingRegister(Class<T> bindType);

    /**
     * 根据名字和类型查找对应的 BindInfo。
     * @param withName 绑定名
     * @param bindType bean type
     * @return 返回所有符合条件的绑定信息。
     */
    public <T> BindInfo<T> findBindingRegister(String withName, Class<T> bindType);

    /**
     * 根据名字和构造方法查找对应的 BindInfo。
     * @param withName 绑定名
     * @param bindConstructor bean Constructor
     * @return 返回所有符合条件的绑定信息。
     */
    public <T> BindInfo<T> findBindingRegister(String withName, Constructor<T> bindConstructor);
}