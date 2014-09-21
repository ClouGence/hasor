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
    public <T extends AppContextAware> T autoAware(T aware);
    /**安装其它插件*/
    public void installModule(Module module) throws Throwable;
    //
    /*----------------------------------------------------------------------------------------Aop*/
    /**配置Aop，表达式格式为*/
    public void bindInterceptor(String matcherExpression, MethodInterceptor interceptor);
    /**配置Aop*/
    public void bindInterceptor(Matcher<Class<?>> matcherClass, Matcher<Method> matcherMethod, MethodInterceptor interceptor);
    /***/
    public static interface Matcher<T> {
        /**Returns {@code true} if this matches {@code t}, {@code false} otherwise.*/
        public boolean matches(T target);
    }
    //
    /*------------------------------------------------------------------------------------Binding*/
    /** */
    public <T> NamedBindingBuilder<T> bindType(Class<T> type);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see #bindingType(Class) */
    public <T> MetaDataBindingBuilder<T> bindType(Class<T> type, T instance);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see #bindingType(Class) */
    public <T> InjectPropertyBindingBuilder<T> bindType(Class<T> type, Class<? extends T> implementation);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see #bindingType(Class) */
    public <T> ScopedBindingBuilder<T> bindType(Class<T> type, Provider<T> provider);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #bindingType(Class)*/
    public <T> InjectPropertyBindingBuilder<T> bindType(String withName, Class<T> type);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #bindingType(String, Class)*/
    public <T> MetaDataBindingBuilder<T> bindType(String withName, Class<T> type, T instance);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #bindingType(String, Class)*/
    public <T> InjectPropertyBindingBuilder<T> bindType(String withName, Class<T> type, Class<? extends T> implementation);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #bindingType(String, Class)*/
    public <T> LifeBindingBuilder<T> bindType(String withName, Class<T> type, Provider<T> provider);
    //
    /*--------------------------------------------------------------------------------------Faces*/
    /**给绑定起个名字*/
    public interface NamedBindingBuilder<T> extends LinkedBindingBuilder<T> {
        /**绑定一个名称*/
        public LinkedBindingBuilder<T> nameWith(String name);
        /**随机取一个不重复的名字*/
        public LinkedBindingBuilder<T> uniqueName();
        /**设置一个ID标识符*/
        public NamedBindingBuilder<T> idWith(String idString);
    }
    /**处理类型和实现的绑定*/
    public interface LinkedBindingBuilder<T> extends InjectPropertyBindingBuilder<T> {
        /**为绑定设置一个实现类*/
        public InjectPropertyBindingBuilder<T> to(Class<? extends T> implementation);
        /**为绑定设置一个实例*/
        public MetaDataBindingBuilder<T> toInstance(T instance);
        /**为绑定设置一个Provider*/
        public LifeBindingBuilder<T> toProvider(Provider<T> provider);
        /**为绑定设置一个构造方法*/
        public InjectConstructorBindingBuilder<T> toConstructor(Constructor<? extends T> constructor);
    }
    /**构造方法依赖注入*/
    public interface InjectConstructorBindingBuilder<T> extends LifeBindingBuilder<T> {
        /**启用自动装配*/
        public InjectConstructorBindingBuilder<T> injectValue(int index, Object value);
        /**启用自动装配*/
        public InjectConstructorBindingBuilder<T> inject(int index, BindInfo<?> valueInfo);
        /**启用自动装配*/
        public InjectConstructorBindingBuilder<T> inject(int index, Provider<?> valueProvider);
    }
    /**属性依赖注入*/
    public interface InjectPropertyBindingBuilder<T> extends LifeBindingBuilder<T> {
        /* *启用自动装配*/
        /* public LifeBindingBuilder autoWire();*/
        /**启用自动装配*/
        public InjectPropertyBindingBuilder<T> injectValue(String property, Object value);
        /**启用自动装配*/
        public InjectPropertyBindingBuilder<T> inject(String property, BindInfo<?> valueInfo);
        /**启用自动装配*/
        public InjectPropertyBindingBuilder<T> inject(String property, Provider<?> valueProvider);
    }
    /**负责启动之后的生命周期方法映射。*/
    public interface LifeBindingBuilder<T> extends ScopedBindingBuilder<T> {
        /* *当容器启动时调用的方法*/
        /*public LifeBindingBuilder initMethod(String methodName);*/
    }
    /**Bean存在的作用域*/
    public interface ScopedBindingBuilder<T> extends MetaDataBindingBuilder<T> {
        /**注册为单例*/
        public MetaDataBindingBuilder<T> asEagerSingleton();
        /**设施Scope*/
        public MetaDataBindingBuilder<T> toScope(Scope scope);
        /**设施Scope*/
        public MetaDataBindingBuilder<T> toScope(Provider<Scope> scope);
    }
    /**绑定元信息*/
    public interface MetaDataBindingBuilder<T> {
        /**获取元信息。*/
        public MetaDataBindingBuilder<T> metaData(String key, Object value);
        /***/
        public BindInfo<T> toInfo();
    }
}