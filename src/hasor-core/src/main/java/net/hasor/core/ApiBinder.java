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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;
import org.aopalliance.intercept.MethodInterceptor;
/**
 * Hasor的核心接口，主要用于收集绑定配置信息。<p>
 * 
 * Hasor 在初始化模块时会为每个模块独立分配一个 ApiBinder 接口实例。
 * <p>方法 {@link ApiBinder#configModule()} ,会返回一个接口用于配置当前模块依赖情况。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ApiBinder extends EventContext {
    /**获取应用程序配置。*/
    public Settings getSettings();
    /**获取环境接口。*/
    public Environment getEnvironment();
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> findClass(Class<?> featureType);
    /**注册一个需要 AppContextAware 的类。该接口会在 AppContext 启动后第一时间注入 AppContext。*/
    public void registerAware(AppContextAware aware);
    //
    /*----------------------------------------------------------------------------------------Aop*/
    /**配置Aop，表达式格式为*/
    public void bindInterceptor(String matcherExpression, MethodInterceptor interceptor);
    /**配置Aop*/
    public void bindInterceptor(Matcher<Class<?>> matcherClass, Matcher<Method> matcherMethod, MethodInterceptor interceptor);
    /***/
    public static interface Matcher<T> {
        /**Returns {@code true} if this matches {@code t}, {@code false} otherwise.*/
        public boolean matches(T t);
    }
    //
    /*-------------------------------------------------------------------------------------Module*/
    /**配置模块依赖关系。*/
    public ModuleSettings configModule();
    /**该接口可以配置模块信息。*/
    public interface ModuleSettings extends ModuleInfo {
        /**设置模块ID*/
        public void setModuleID(String moduleID);
        /**依赖反转：强制目标模块依赖当前模块(弱依赖)。*/
        public void reverse(Class<? extends Module> targetModule);
        /**强制依赖：跟随目标模块启动而启动。如果依赖的模块没有成功启动，则该模块不会启动。<br/> 
         * 注意：该方法要求在目标模块启动之后在启动。*/
        public void forced(Class<? extends Module> targetModule);
        /**弱依赖：要求目标模块的启动在当前模块之前进行启动。<br/>
         * 注意：该方法仅仅要求在目标模块之后启动。但目标模块是否启动并无强制要求。*/
        public void weak(Class<? extends Module> targetModule);
    }
    //
    /*------------------------------------------------------------------------------------Binding*/
    /** */
    public <T> NamedBindingBuilder<T> bindingType(Class<T> type);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see #bindingType(Class) */
    public <T> void bindingType(Class<T> type, T instance);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see #bindingType(Class) */
    public <T> ScopedBindingBuilder bindingType(Class<T> type, Class<? extends T> implementation);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see #bindingType(Class) */
    public <T> ScopedBindingBuilder bindingType(Class<T> type, Provider<T> provider);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #bindingType(Class)*/
    public <T> LinkedBindingBuilder<T> bindingType(String withName, Class<T> type);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #bindingType(String, Class)*/
    public <T> void bindingType(String withName, Class<T> type, T instance);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #bindingType(String, Class)*/
    public <T> ScopedBindingBuilder bindingType(String withName, Class<T> type, Class<? extends T> implementation);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #bindingType(String, Class)*/
    public <T> ScopedBindingBuilder bindingType(String withName, Class<T> type, Provider<T> provider);
    //
    /*---------------------------------------------------------------------------------------Bean*/
    /**注册一个bean。*/
    public BeanBindingBuilder defineBean(String beanName);
    //
    public interface BeanBindingBuilder {
        /**别名*/
        public BeanBindingBuilder aliasName(String aliasName);
        /**设置属性*/
        public BeanBindingBuilder setProperty(String attName, Object attValue);
        /**bean绑定的类型。*/
        public <T> LinkedBindingBuilder<T> bindType(Class<T> beanType);
    }
    public interface NamedBindingBuilder<T> extends LinkedBindingBuilder<T> {
        public LinkedBindingBuilder<T> nameWith(String name);
        public LinkedBindingBuilder<T> uniqueName();
    }
    public interface LinkedBindingBuilder<T> extends ScopedBindingBuilder {
        /**为绑定设置一个实现类*/
        public ScopedBindingBuilder to(Class<? extends T> implementation);
        /**为绑定设置一个实例*/
        public MetaDataBindingBuilder toInstance(T instance);
        /**为绑定设置一个Provider*/
        public ScopedBindingBuilder toProvider(Provider<T> provider);
        /**为绑定设置一个构造方法*/
        public ScopedBindingBuilder toConstructor(Constructor<? extends T> constructor);
    }
    public interface ScopedBindingBuilder extends MetaDataBindingBuilder {
        /**注册为单例*/
        public MetaDataBindingBuilder asEagerSingleton();
        /**在容器上公开这个绑定*/
        public MetaDataBindingBuilder toScope(Scope scope);
    }
    public interface MetaDataBindingBuilder {
        /**获取元信息。*/
        public MetaDataBindingBuilder metaData(String key, Object value);
    }
}