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
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.spi.AppContextAware;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Hasor的核心接口，主要用于收集Bean绑定信息。<p>
 * Bind 参考了 Google Guice 的 Binder 接口设计，功能上大体相似。目的是提供一种不同于配置文件、注解方式的配置方法。
 * 这样一种设计并不是指 Hasor 抛弃配置文件和注解的优势，开发者可以根据项目的特征自行选择。
 * Hasor 的开发者可以将某一个类使用 ApiBinder 接口的 bindType 方法注册到容器中。这个工作与 Spring 配置文件中 Bean 配置的作用并无不同。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ApiBinder {
    /**
     * 获取 {@link Environment}
     * @return return {@link Environment}
     */
    public Environment getEnvironment();

    /**
     * 在框架扫描包的范围内查找具有特征类集合（特征可以是继承的类、标记的注解）。<br>
     *  -- 该方法会放弃在匹配的过程中如果类无法被ClassLoader所加载的类。
     * @param featureType 特征类型
     * @return 返回匹配的类集合。
     */
    public Set<Class<?>> findClass(Class<?> featureType);

    /**
     * 在框架扫描包的范围内查找具有特征类集合（特征可以是继承的类、标记的注解）。<br>
     *  -- 该方法会放弃在匹配的过程中如果类无法被ClassLoader所加载的类。
     * @param featureType 特征类型
     * @param scanPackages 扫描的包范围
     * @return 返回匹配的类集合。
     */
    public Set<Class<?>> findClass(Class<?> featureType, String... scanPackages);

    /**
     * 尝试把 ApiBinder 转换为另一个 ApiBinder（如果它支持）<br/>
     * tips： 如果不支持转换则返回空。
     */
    public <T extends ApiBinder> T tryCast(Class<T> castApiBinder);

    /**
     * 安装其它插件。
     * @param module 新安装的插件
     * @throws Throwable 在执行loadModule方法期间发生异常的。
     * @see net.hasor.core.Module#loadModule(ApiBinder)
     */
    public void installModule(Module... module) throws Throwable;

    /** 是否为单例 */
    public boolean isSingleton(BindInfo<?> bindInfo);

    /** 是否为单例 */
    public boolean isSingleton(Class<?> targetType);
    /*----------------------------------------------------------------------------------------Aop*/

    /**
     * 使用表达式配置Aop。
     * <p>例：<pre>格式：&lt;返回值&gt;&nbsp;&lt;类名&gt;.&lt;方法名&gt;(&lt;参数签名列表&gt;)
     *  * *.*()                  匹配：任意无参方法
     *  * *.*(*)                 匹配：任意方法
     *  * *.add*(*)              匹配：任意add开头的方法
     *  * *.add*(*,*)            匹配：任意add开头并且具有两个参数的方法。
     *  * net.test.hasor.*(*)    匹配：包“net.test.hasor”下的任意类，任意方法。
     *  * net.test.hasor.add*(*) 匹配：包“net.test.hasor”下的任意类，任意add开头的方法。
     *  java.lang.String *.*(*)  匹配：任意返回值为String类型的方法。
     * </pre>
     * @param matcherExpression 格式为“<code>&lt;返回值&gt;&nbsp;&lt;类名&gt;.&lt;方法名&gt;(&lt;参数签名列表&gt;)</code>”
     * @param interceptor 拦截器对象
     */
    public void bindInterceptor(String matcherExpression, MethodInterceptor interceptor);

    /**
     * 使用匹配器配置Aop。
     * @param matcherClass 类型匹配器
     * @param matcherMethod 方法匹配器
     * @param interceptor 拦截器对象
     */
    public void bindInterceptor(Predicate<Class<?>> matcherClass, Predicate<Method> matcherMethod, MethodInterceptor interceptor);

    /*--------------------------------------------------------------------------------------Finds*/

    /** 根据ID获取{@link BindInfo} */
    public <T> BindInfo<T> getBindInfo(String bindID);

    /** 根据ID获取{@link BindInfo} */
    public <T> BindInfo<T> getBindInfo(Class<T> bindType);

    /**
     * 通过一个类型获取所有绑定该类型下的绑定信息。
     * @param withName 绑定名
     * @param bindType bean type
     * @return 返回所有符合条件的绑定信息。
     */
    public <T> BindInfo<T> findBindingRegister(String withName, Class<T> bindType);

    /**
     * 通过一个类型获取所有绑定该类型下的绑定信息。
     * @param bindType bean type
     * @return 返回所有符合条件的绑定信息。
     */
    public <T> List<BindInfo<T>> findBindingRegister(Class<T> bindType);

    /*------------------------------------------------------------------------------------Binding*/

    /**
     * bind type to context , 通过返回的 Builder 可以对绑定进行后续更加细粒度的配置。<p>
     *  -- {@link NamedBindingBuilder}类型，为绑定起名字。继承自：{@link LinkedBindingBuilder}<br>
     *  -- {@link LinkedBindingBuilder}类型，为绑定设置实现方式。继承自：{@link InjectPropertyBindingBuilder}<br>
     *  -- {@link InjectPropertyBindingBuilder}类型，为绑定设置注入属性。继承自：{@link LifeBindingBuilder}<br>
     *  -- {@link LifeBindingBuilder}类型，为绑定设置生命周期方法配置。继承自：{@link ScopedBindingBuilder}<br>
     *  -- {@link ScopedBindingBuilder}类型，为绑定设置作用域。继承自：{@link OptionPropertyBindingBuilder}<br>
     *  -- {@link OptionPropertyBindingBuilder}类型，为绑定设置作用域。继承自：{@link MetaDataBindingBuilder}<br>
     *  -- {@link MetaDataBindingBuilder}类型，绑定元信息配置。<br>
     * @param type bean type。
     * @return 返回 - {@link NamedBindingBuilder}。
     */
    public <T> NamedBindingBuilder<T> bindType(Class<T> type);

    /**
     * 绑定一个类型并且为这个类型指定一个实例。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).toInstance(instance);</code>”
     * @param type bean type。
     * @param instance 类型的实例
     * @return 返回 - {@link OptionPropertyBindingBuilder}。
     * @see #bindType(Class)
     */
    public default <T> OptionPropertyBindingBuilder<T> bindType(final Class<T> type, final T instance) {
        return this.bindType(type).toInstance(instance);
    }

    /**
     * 绑定一个类型并且为这个类型指定一个实现类。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).to(implementation);</code>”
     * @param type bean type。
     * @param implementation 绑定的类型的实现类
     * @return 返回 - {@link InjectPropertyBindingBuilder}。
     * @see #bindType(Class)
     */
    public default <T> InjectPropertyBindingBuilder<T> bindType(final Class<T> type, final Class<? extends T> implementation) {
        return this.bindType(type).to(implementation);
    }

    /**
     * 绑定一个类型并且为这个类型指定一个{@link Supplier}，开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).to(implementation);</code>”
     * @param type bean type。
     * @param supplier supplier 可以用来封装类型实例创建的细节。
     * @return 返回 - {@link ScopedBindingBuilder}。
     * @see #bindType(Class)
     */
    public default <T> ScopedBindingBuilder<T> bindType(final Class<T> type, final Supplier<T> supplier) {
        return this.bindType(type).toProvider(supplier);
    }

    /**
     * 为绑定类型配置一个名称，进而基于同一个类型下不同名称的绑定进行差异化配置。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).nameWith(withName).to(type);</code>”
     * @param withName bean名称。
     * @param type bean type。
     * @return 返回 - {@link InjectPropertyBindingBuilder}。
     * @see #bindType(Class)
     */
    public default <T> InjectPropertyBindingBuilder<T> bindType(final String withName, final Class<T> type) {
        return this.bindType(type).nameWith(withName).to(type);
    }

    /**
     * 为绑定类型配置一个名称，进而基于同一个类型下不同名称的绑定进行差异化配置。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).nameWith(withName).toInstance(instance);</code>”
     * @param withName 要绑定的类型。
     * @param type bean type。
     * @param instance 同时指定实例对象
     * @return 返回 - {@link OptionPropertyBindingBuilder}。
     * @see #bindType(String, Class)
     * @see #bindType(Class)
     */
    public default <T> OptionPropertyBindingBuilder<T> bindType(final String withName, final Class<T> type, final T instance) {
        return this.bindType(type).nameWith(withName).toInstance(instance);
    }

    /**
     * 为绑定类型配置一个名称，进而基于同一个类型下不同名称的绑定进行差异化配置。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).nameWith(withName).to(implementation);</code>”
     * @param withName 要绑定的类型。
     * @param type bean type。
     * @param implementation 同时指定实现类
     * @return 返回 - {@link InjectPropertyBindingBuilder}。
     * @see #bindType(String, Class)
     * @see #bindType(Class)
     */
    public default <T> InjectPropertyBindingBuilder<T> bindType(final String withName, final Class<T> type, final Class<? extends T> implementation) {
        return this.bindType(type).nameWith(withName).to(implementation);
    }

    /**
     * 为绑定类型配置一个名称，进而基于同一个类型下不同名称的绑定进行差异化配置。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).nameWith(withName).to(implementation);</code>”
     * @param withName 要绑定的类型。
     * @param type bean type。
     * @param supplier supplier 可以用来封装类型实例创建的细节。
     * @return 返回 - {@link LifeBindingBuilder}。
     * @see #bindType(String, Class)
     * @see #bindType(Class)
     */
    public default <T> LifeBindingBuilder<T> bindType(final String withName, final Class<T> type, final Supplier<T> supplier) {
        return this.bindType(type).nameWith(withName).toProvider(supplier);
    }

    /** 添加SPI监听器 */
    public <T extends EventListener> void bindSpiListener(Class<T> spiType, T listener);

    /**
     * 注册作用域。
     * @param scopeType 作用域名称
     * @param scope 作用域
     * @return 成功注册之后返回它自身, 如果存在同名的scope那么会返回第一次注册那个 scope。
     * @see javax.inject.Scope
     */
    public default Supplier<Scope> bindScope(Class<? extends Annotation> scopeType, Scope scope) {
        Objects.requireNonNull(scopeType, "scopeType is null.");
        javax.inject.Scope scopeTypeAnno = scopeType.getAnnotation(javax.inject.Scope.class);
        if (scopeTypeAnno == null) {
            throw new IllegalStateException("Annotation Type " + scopeType.getName() + " is not javax.inject.Scope");
        }
        return this.bindScope(scopeType.getName(), InstanceProvider.of(scope));
    }

    /**
     * 注册作用域。
     * @param scopeName 作用域名称
     * @param scope 作用域
     * @return 成功注册之后返回它自身, 如果存在同名的scope那么会返回第一次注册那个 scope。
     */
    public default Supplier<Scope> bindScope(String scopeName, Scope scope) {
        return this.bindScope(scopeName, InstanceProvider.of(scope));
    }

    /**
     * 注册作用域。
     * @param scopeName 作用域名称
     * @param scopeSupplier 作用域
     * @return 成功注册之后返回它自身, 如果存在同名的scope那么会返回第一次注册那个 scope。
     */
    public <T extends Scope> Supplier<T> bindScope(String scopeName, Supplier<T> scopeSupplier);

    public default <T> Supplier<T> getProvider(Class<T> targetType) {
        class TargetSupplierByClass implements AppContextAware, Supplier<T> {
            private AppContext appContext = null;

            @Override
            public void setAppContext(AppContext appContext) {
                this.appContext = appContext;
            }

            @Override
            public T get() {
                if (this.appContext == null) {
                    throw new IllegalStateException("the current state is not ready.");
                }
                return appContext.getInstance(targetType);
            }
        }
        return HasorUtils.autoAware(getEnvironment(), new TargetSupplierByClass());
    }

    public default <T> Supplier<T> getProvider(BindInfo<T> targetType) {
        class TargetSupplierByInfo implements AppContextAware, Supplier<T> {
            private AppContext appContext = null;

            @Override
            public void setAppContext(AppContext appContext) {
                this.appContext = appContext;
            }

            @Override
            public T get() {
                if (this.appContext == null) {
                    throw new IllegalStateException("the current state is not ready.");
                }
                return appContext.getInstance(targetType);
            }
        }
        return HasorUtils.autoAware(getEnvironment(), new TargetSupplierByInfo());
    }

    /*--------------------------------------------------------------------------------------Faces*/

    /**给绑定起个名字。*/
    public interface NamedBindingBuilder<T> extends LinkedBindingBuilder<T> {
        /**
         * 绑定一个名称(并同时设置ID,为随机ID)。
         * @param name 名称
         * @return 返回 - {@link LinkedBindingBuilder}。
         */
        public LinkedBindingBuilder<T> nameWith(String name);
        //        {
        //            return this.annotationWith(new Named() {
        //                @Override
        //                public Class<? extends Annotation> annotationType() {
        //                    return Named.class;
        //                }
        //                @Override
        //                public String value() {
        //                    return name;
        //                }
        //            });
        //        }
        //
        //        /**
        //         * 绑定到一个注解上(并同时设置ID,为随机ID)。
        //         * @param annotation 注解
        //         * @return 返回 - {@link LinkedBindingBuilder}。
        //         */
        //        public LinkedBindingBuilder<T> annotationWith(Annotation annotation);

        /**
         * 随机取一个不重复的名字(并同时设置ID,为随机ID)。
         * @return 返回 - {@link LinkedBindingBuilder}。
         */
        public LinkedBindingBuilder<T> uniqueName();

        /**
         * 设置一个ID标识符。
         * @param idString id标识符.
         * @return 返回 - {@link NamedBindingBuilder}。
         */
        public NamedBindingBuilder<T> idWith(String idString);

        /**
         * 设置一个ID标识符，同时设置 name。
         * @param nameString id标识符.
         * @return 返回 - {@link NamedBindingBuilder}。
         */
        public NamedBindingBuilder<T> bothWith(String nameString);
    }

    /**处理类型和实现的绑定。*/
    public interface LinkedBindingBuilder<T> extends InjectPropertyBindingBuilder<T> {
        /**
         * 为绑定设置一个实现类。
         * @param implementation 实现类型
         * @return 返回 - {@link InjectPropertyBindingBuilder}。
         */
        public InjectPropertyBindingBuilder<T> to(Class<? extends T> implementation);

        /**
         * 为绑定设置一个实例
         * @param instance 实例对象
         * @return 返回 - {@link OptionPropertyBindingBuilder}。
         */
        public default OptionPropertyBindingBuilder<T> toInstance(final T instance) {
            return this.toProvider(InstanceProvider.of(instance));
        }

        /**
         * 为绑定设置一个 {@link Supplier}。
         * @param supplier supplier 可以用来封装类型实例创建的细节。
         * @return 返回 - {@link LifeBindingBuilder}。
         */
        public LifeBindingBuilder<T> toProvider(Supplier<? extends T> supplier);

        /**
         * 为绑定设置一个构造方法。
         * @param constructor 使用的构造方法。
         * @return 返回 - {@link InjectConstructorBindingBuilder}。
         */
        public InjectConstructorBindingBuilder<T> toConstructor(Constructor<? extends T> constructor);
    }

    /**构造方法依赖注入，该接口的配置会覆盖注解  {@link ConstructorBy}。*/
    public interface InjectConstructorBindingBuilder<T> extends LifeBindingBuilder<T> {
        /**
         * 设置构造方法注入属性。
         * @param index 构造方法参数索引位置。
         * @param value 构造方法参数值。
         * @return 返回 - {@link InjectConstructorBindingBuilder}。
         */
        public InjectConstructorBindingBuilder<T> injectValue(int index, Object value);

        /**
         * 设置构造方法注入属性。
         * @param index 构造方法参数索引位置。
         * @param valueInfo 要注入的参数来自于其它绑定。
         * @return 返回 - {@link InjectConstructorBindingBuilder}。
         */
        public InjectConstructorBindingBuilder<T> inject(int index, BindInfo<?> valueInfo);

        /**
         * 设置构造方法注入属性。
         * @param index 构造方法参数索引位置。
         * @param valueSupplier supplier 可以用来封装类型实例创建的细节。
         * @return 返回 - {@link InjectConstructorBindingBuilder}。
         */
        public InjectConstructorBindingBuilder<T> inject(int index, Supplier<?> valueSupplier);

        /**
         * 设置构造方法注入属性。
         * @param index 构造方法参数索引位置。
         * @param valueType 要注入的参数来自于其它绑定。
         * @return 返回 - {@link InjectConstructorBindingBuilder}。
         */
        public InjectConstructorBindingBuilder<T> inject(int index, Class<?> valueType);
    }

    /**属性依赖注入*/
    public interface InjectPropertyBindingBuilder<T> extends LifeBindingBuilder<T> {
        /**
         * 值类型的属性注入。
         * @param property 被注入Bean的属性名
         * @param value 属性值。
         * @return 返回属性注入接口，以继续其它属性注入。 - {@link InjectPropertyBindingBuilder}。
         */
        public InjectPropertyBindingBuilder<T> injectValue(String property, Object value);

        /**
         * 注入另一个Bean对象。
         * @param property 被注入Bean的属性名
         * @param valueInfo Bean配置信息。
         * @return 返回属性注入接口，以继续其它属性注入。 - {@link InjectPropertyBindingBuilder}。
         */
        public InjectPropertyBindingBuilder<T> inject(String property, BindInfo<?> valueInfo);

        /**
         * 工厂方式注入Bean。
         * @param property 被注入Bean的属性名
         * @param valueSupplier 属性值提供者。
         * @return 返回属性注入接口，以继续其它属性注入。 - {@link InjectPropertyBindingBuilder}。
         */
        public InjectPropertyBindingBuilder<T> inject(String property, Supplier<?> valueSupplier);

        /**
         * 工厂方式注入Bean。
         * @param property 被注入Bean的属性名
         * @param valueType 属性值提供者。
         * @return 返回属性注入接口，以继续其它属性注入。 - {@link InjectPropertyBindingBuilder}。
         */
        public InjectPropertyBindingBuilder<T> inject(String property, Class<?> valueType);

        /**
         * 是否强制覆盖类本身注解配置。
         * @return 返回属性注入接口，以继续其它属性注入。 - {@link InjectPropertyBindingBuilder}。
         */
        public InjectPropertyBindingBuilder<T> overwriteAnnotation();
    }

    /**负责启动之后的生命周期方法映射。*/
    public interface LifeBindingBuilder<T> extends ScopedBindingBuilder<T> {
        /**配置当对象被创建时调用的方法，如果{@link Init @Init()}注解也定义了一个初始化方法则，注解方式优先于配置。
         * @see net.hasor.core.Init*/
        public LifeBindingBuilder<T> initMethod(String methodName);

        /**配置当容器销毁时调用的方法，如果{@link Destroy @Destroy()}注解也定义了一个初始化方法则，注解方式优先于配置。
         * @see net.hasor.core.Destroy*/
        public LifeBindingBuilder<T> destroyMethod(String methodName);
    }

    /**Bean存在的作用域*/
    public interface ScopedBindingBuilder<T> extends OptionPropertyBindingBuilder<T> {
        /**
         * 注册为原型模式(会覆盖类型上本身的作用域注释配置)。<p>
         * 原型模式：当类型被多个对象注入时，每个注入的类型实例都是全新的对象。
         * @return 返回 - {@link OptionPropertyBindingBuilder}。
         */
        public default OptionPropertyBindingBuilder<T> asEagerPrototype() {
            return this.toScope(Prototype.class);
        }

        /**
         * 注册为单例模式(会覆盖类型上本身的作用域注释配置)。<p>
         * 单列模式：当类型被多个对象注入时，每个注入的类型实例都是同一个对象。
         * @return 返回 - {@link OptionPropertyBindingBuilder}。
         */
        public default OptionPropertyBindingBuilder<T> asEagerSingleton() {
            return this.toScope(Singleton.class);
        }

        /**
         * 将Bean应用到一个用户指定的Scope上。
         * @param scope 作用域
         * @return 返回 - {@link OptionPropertyBindingBuilder}。
         */
        public default OptionPropertyBindingBuilder<T> toScope(Scope... scope) {
            Supplier[] supplierArray = Arrays.stream(scope).map(InstanceProvider::of).toArray(Supplier[]::new);
            return this.toScope(supplierArray);
        }

        /**
         * 将Bean应用到一个用户指定的Scope上。
         * 如果配置了{@link #toScope(Supplier...)}或者{@link #toScope(Scope...)}，那么该方法将会使它们失效。
         * @return 返回 - {@link OptionPropertyBindingBuilder}。
         */
        public default OptionPropertyBindingBuilder<T> toScope(Class<?>... scopeAnno) {
            return this.toScope(Arrays.stream(scopeAnno)//
                    .filter(aClass -> {
                        if (!aClass.isAnnotation()) {
                            throw new IllegalArgumentException(aClass.getName() + " must be Annotation.");
                        }
                        return true;
                    })//
                    .map(aClass -> {
                        if (aClass.getAnnotation(javax.inject.Scope.class) == null) {
                            throw new IllegalArgumentException(aClass.getName() + " is not javax.inject.Scope");
                        }
                        return aClass.getName();
                    }).toArray(String[]::new)//
            );
        }

        /**
         * 设置Scope。
         * @param scope 作用域
         * @return 返回 - {@link OptionPropertyBindingBuilder}。
         */
        public OptionPropertyBindingBuilder<T> toScope(Supplier<Scope>... scope);

        /**
         * 设置Scope。
         * @param scopeName 作用域
         * @return 返回 - {@link OptionPropertyBindingBuilder}。
         */
        public OptionPropertyBindingBuilder<T> toScope(String... scopeName);
    }

    /** 选项和属性的配置。*/
    public interface OptionPropertyBindingBuilder<T> extends MetaDataBindingBuilder<T> {
    }

    /**绑定元信息*/
    public interface MetaDataBindingBuilder<T> {
        /**
         * 设置元信息。
         * @param key 元信息 key
         * @param value 元信息 value
         * @return 返回 - {@link MetaDataBindingBuilder}。
         */
        public MetaDataBindingBuilder<T> metaData(String key, Object value);

        /**
         * 转换为 {@link BindInfo} 接口对象。
         * @return 返回{@link BindInfo}。
         */
        public BindInfo<T> toInfo();
    }
}