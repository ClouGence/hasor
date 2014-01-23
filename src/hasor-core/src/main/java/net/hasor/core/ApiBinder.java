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
import java.util.Set;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
/**
 * Hasor的核心接口，主要用于收集绑定配置信息。<p>
 * 
 * Hasor 在初始化模块时会为每个模块独立分配一个 ApiBinder 接口实例。
 * <p>方法 {@link ApiBinder#configModule()} ,会返回一个接口用于配置当前模块依赖情况。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ApiBinder {
    /**获取环境接口。*/
    public Environment getEnvironment();
    /**获取事件管理器*/
    public EventManager getEventManager();
    /**获取 Guice 的 Binder。*/
    public Binder getGuiceBinder();
    /**
     * See the EDSL examples at {@link Binder}.<p>
     * 将后面的对象绑定前一个类型上，可以通过 {@link AppContext} 使用绑定的类型重新获取绑定的对象。
     * 使用下面的方法即可重新获取绑定的类型。
     * <pre>
     * AppContext :
     *   appContext.findBindingsByType(BeanInfo.class);
     * Guice :
     *   TypeLiteral INFO_DEFS = TypeLiteral.get(BeanInfo.class);
     *   appContext.getGuice().findBindingsByType(INFO_DEFS);
     * </pre>
     * */
    public <T> LinkedBindingBuilder<T> bindingType(Class<T> type);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see ApiBinder#bindingType(Class) */
    public <T> void bindingType(Class<T> type, T instance);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see ApiBinder#bindingType(Class) */
    public <T> ScopedBindingBuilder bindingType(Class<T> type, Class<? extends T> implementation);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see ApiBinder#bindingType(Class) */
    public <T> ScopedBindingBuilder bindingType(Class<T> type, Provider<? extends T> provider);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see ApiBinder#bindingType(Class) */
    public <T> ScopedBindingBuilder bindingType(Class<T> type, Key<? extends T> targetKey);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see net.hasor.core.ApiBinder#bindingType(Class)*/
    public <T> LinkedBindingBuilder<T> bindingType(String withName, Class<T> type);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see net.hasor.core.ApiBinder#bindingType(String, Class)*/
    public <T> void bindingType(String withName, Class<T> type, T instance);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see net.hasor.core.ApiBinder#bindingType(String, Class)*/
    public <T> ScopedBindingBuilder bindingType(String withName, Class<T> type, Class<? extends T> implementation);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see net.hasor.core.ApiBinder#bindingType(String, Class)*/
    public <T> ScopedBindingBuilder bindingType(String withName, Class<T> type, Provider<? extends T> provider);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see net.hasor.core.ApiBinder#bindingType(String, Class)*/
    public <T> ScopedBindingBuilder bindingType(String withName, Class<T> type, Key<? extends T> targetKey);
    //
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> findClass(Class<?> featureType);
    /**配置模块依赖关系。*/
    public ModuleSettings configModule();
    /**注册一个bean。*/
    public BeanBindingBuilder defineBean(String beanName);
    /**负责注册Bean*/
    public static interface BeanBindingBuilder {
        /**别名*/
        public BeanBindingBuilder aliasName(String aliasName);
        /**bean绑定的类型。*/
        public <T> LinkedBindingBuilder<T> bindType(Class<T> beanType);
    }
    /**该接口可以配置模块信息。*/
    public interface ModuleSettings extends ModuleInfo {
        /**依赖反制：强制目标模块依赖当前模块(弱依赖)。*/
        public void reverse(Class<? extends Module> targetModule);
        /**强制依赖：跟随目标模块启动而启动。如果依赖的模块没有成功启动，则该模块不会启动。<br/> 
         * 注意：该方法要求在目标模块启动之后在启动。*/
        public void forced(Class<? extends Module> targetModule);
        /**弱依赖：要求目标模块的启动在当前模块之前进行启动。<br/>
         * 注意：该方法仅仅要求在目标模块之后启动。但目标模块是否启动并无强制要求。*/
        public void weak(Class<? extends Module> targetModule);
    }
    /**注册一个需要 AppContextAware 的类。该接口会在 AppContext 启动后第一时间注入 AppContext。*/
    public void registerAware(AppContextAware aware);
}