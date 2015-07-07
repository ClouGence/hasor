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
 * Hasor的核心接口，主要用于收集Bean绑定信息。<p>
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
     * 安装其它插件。
     * @param module 新安装的插件
     * @throws Throwable 在执行loadModule方法期间发生异常的。
     * @see net.hasor.core.Module#loadModule(ApiBinder)
     */
    public void installModule(Module module) throws Throwable;
    //
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
    public void bindInterceptor(Matcher<Class<?>> matcherClass, Matcher<Method> matcherMethod, MethodInterceptor interceptor);
    //
    /**Aop匹配器*/
    public static interface Matcher<T> {
        /**Returns {@code true} if this matches {@code T}, {@code false} otherwise.*/
        public boolean matches(T target);
    }
    //
    /*------------------------------------------------------------------------------------Binding*/
    /**
     * bind type to context , 通过返回的 Builder 可以对绑定进行后续更加细粒度的配置。<p>
     *  -- {@link NamedBindingBuilder}类型，为绑定起名字。继承自：{@link LinkedBindingBuilder}<br>
     *  -- {@link LinkedBindingBuilder}类型，为绑定设置实现方式。继承自：{@link InjectPropertyBindingBuilder}<br>
     *  -- {@link InjectPropertyBindingBuilder}类型，为绑定设置注入属性。继承自：{@link LifeBindingBuilder}<br>
     *  -- {@link LifeBindingBuilder}类型，为绑定设置生命周期方法配置。继承自：{@link ScopedBindingBuilder}<br>
     *  -- {@link ScopedBindingBuilder}类型，为绑定设置作用域。继承自：{@link MetaDataBindingBuilder}<br>
     *  -- {@link MetaDataBindingBuilder}类型，绑定元信息配置。<br>
     * @param type bean type。
     * @return 返回细粒度绑定接口 - {@link NamedBindingBuilder}。
     */
    public <T> NamedBindingBuilder<T> bindType(Class<T> type);
    /**
     * 绑定一个类型并且为这个类型指定一个实例。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).toInstance(instance);</code>”
     * @param type bean type。
     * @param instance 类型的实例
     * @return 返回细粒度绑定接口 - {@link MetaDataBindingBuilder}。
     * @see #bindType(Class)
     */
    public <T> MetaDataBindingBuilder<T> bindType(Class<T> type, T instance);
    /**
     * 绑定一个类型并且为这个类型指定一个实现类。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).to(implementation);</code>”
     * @param type bean type。
     * @param implementation 绑定的类型的实现类
     * @return 返回细粒度绑定接口 - {@link InjectPropertyBindingBuilder}。
     * @see #bindType(Class)
     */
    public <T> InjectPropertyBindingBuilder<T> bindType(Class<T> type, Class<? extends T> implementation);
    /**
     * 绑定一个类型并且为这个类型指定一个{@link Provider}，开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).to(implementation);</code>”
     * @param type bean type。
     * @param provider provider 可以用来封装类型实例创建的细节。
     * @return 返回细粒度绑定接口 - {@link ScopedBindingBuilder}。
     * @see #bindType(Class)
     */
    public <T> ScopedBindingBuilder<T> bindType(Class<T> type, Provider<T> provider);
    /**
     * 为绑定类型配置一个名称，进而基于同一个类型下不同名称的绑定进行差异化配置。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).nameWith(withName).to(type);</code>”
     * @param withName bean名称。
     * @param type bean type。
     * @return 返回细粒度绑定接口 - {@link InjectPropertyBindingBuilder}。
     * @see #bindType(Class)
     */
    public <T> InjectPropertyBindingBuilder<T> bindType(String withName, Class<T> type);
    /**
     * 为绑定类型配置一个名称，进而基于同一个类型下不同名称的绑定进行差异化配置。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).nameWith(withName).toInstance(instance);</code>”
     * @param withName 要绑定的类型。
     * @param type bean type。
     * @param instance 同时指定实例对象
     * @return 返回细粒度绑定接口 - {@link MetaDataBindingBuilder}。
     * @see #bindType(String, Class)
     * @see #bindType(Class)
     */
    public <T> MetaDataBindingBuilder<T> bindType(String withName, Class<T> type, T instance);
    /**
     * 为绑定类型配置一个名称，进而基于同一个类型下不同名称的绑定进行差异化配置。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).nameWith(withName).to(implementation);</code>”
     * @param withName 要绑定的类型。
     * @param type bean type。
     * @param implementation 同时指定实现类
     * @return 返回细粒度绑定接口 - {@link InjectPropertyBindingBuilder}。
     * @see #bindType(String, Class)
     * @see #bindType(Class)
     */
    public <T> InjectPropertyBindingBuilder<T> bindType(String withName, Class<T> type, Class<? extends T> implementation);
    /**
     * 为绑定类型配置一个名称，进而基于同一个类型下不同名称的绑定进行差异化配置。开发者可以通过返回的 Builder 可以对绑定进行后续更加细粒度的绑定。<p>
     * 该方法相当于“<code>apiBinder.bindType(type).nameWith(withName).to(implementation);</code>”
     * @param withName 要绑定的类型。
     * @param type bean type。
     * @param provider provider 可以用来封装类型实例创建的细节。
     * @return 返回细粒度绑定接口 - {@link LifeBindingBuilder}。
     * @see #bindType(String, Class)
     * @see #bindType(Class)
     */
    public <T> LifeBindingBuilder<T> bindType(String withName, Class<T> type, Provider<T> provider);
    //
    /*--------------------------------------------------------------------------------------Faces*/
    /**给绑定起个名字。*/
    public interface NamedBindingBuilder<T> extends LinkedBindingBuilder<T> {
        /**
         * 绑定一个名称(并同时设置ID,为随机ID)。
         * @param name 名称
         * @return 返回细粒度绑定接口 - {@link LinkedBindingBuilder}。
         */
        public LinkedBindingBuilder<T> nameWith(String name);
        /**
         * 随机取一个不重复的名字(并同时设置ID,为随机ID)。
         * @return 返回细粒度绑定接口 - {@link LinkedBindingBuilder}。
         */
        public LinkedBindingBuilder<T> uniqueName();
        /**
         * 设置一个ID标识符。
         * @param idString id标识符.
         * @return 返回细粒度绑定接口 - {@link NamedBindingBuilder}。
         */
        public NamedBindingBuilder<T> idWith(String idString);
    }
    /**处理类型和实现的绑定。*/
    public interface LinkedBindingBuilder<T> extends InjectPropertyBindingBuilder<T> {
        /**
         * 为绑定设置一个实现类。
         * @param implementation 实现类型
         * @return 返回细粒度绑定接口 - {@link InjectPropertyBindingBuilder}。
         */
        public InjectPropertyBindingBuilder<T> to(Class<? extends T> implementation);
        /**
         * 为绑定设置一个实例
         * @param instance 实例对象
         * @return 返回细粒度绑定接口 - {@link MetaDataBindingBuilder}。
         */
        public MetaDataBindingBuilder<T> toInstance(T instance);
        /**
         * 为绑定设置一个 {@link Provider}。
         * @param provider provider 可以用来封装类型实例创建的细节。
         * @return 返回细粒度绑定接口 - {@link LifeBindingBuilder}。
         */
        public LifeBindingBuilder<T> toProvider(Provider<T> provider);
        /**
         * 为绑定设置一个构造方法。
         * @param constructor 使用的构造方法。
         * @return 返回细粒度绑定接口 - {@link InjectConstructorBindingBuilder}。
         */
        public InjectConstructorBindingBuilder<T> toConstructor(Constructor<? extends T> constructor);
    }
    /**构造方法依赖注入。*/
    public interface InjectConstructorBindingBuilder<T> extends LifeBindingBuilder<T> {
        /**
         * 设置构造方法注入属性。
         * @param index 构造方法参数索引位置。
         * @param value 构造方法参数值。
         * @return 返回细粒度绑定接口 - {@link InjectConstructorBindingBuilder}。
         */
        public InjectConstructorBindingBuilder<T> injectValue(int index, Object value);
        /**
         * 设置构造方法注入属性。
         * @param index 构造方法参数索引位置。
         * @param valueInfo 要注入的参数来自于其它绑定。
         * @return 返回细粒度绑定接口 - {@link InjectConstructorBindingBuilder}。
         */
        public InjectConstructorBindingBuilder<T> inject(int index, BindInfo<?> valueInfo);
        /**
         * 设置构造方法注入属性。
         * @param index 构造方法参数索引位置。
         * @param valueProvider provider 可以用来封装类型实例创建的细节。
         * @return 返回细粒度绑定接口 - {@link InjectConstructorBindingBuilder}。
         */
        public InjectConstructorBindingBuilder<T> inject(int index, Provider<?> valueProvider);
    }
    /**属性依赖注入*/
    public interface InjectPropertyBindingBuilder<T> extends LifeBindingBuilder<T> {
        /* *启用自动装配*/
        /* public LifeBindingBuilder autoWire();*/
        /**
         * 配置一个属性注入。
         * @param property 属性名
         * @param value 属性值。
         * @return 返回细粒度绑定接口 - {@link InjectPropertyBindingBuilder}。
         */
        public InjectPropertyBindingBuilder<T> injectValue(String property, Object value);
        /**
         * 配置一个属性注入。
         * @param property 属性名
         * @param valueInfo 属性值来源于另外一个绑定。
         * @return 返回细粒度绑定接口 - {@link InjectPropertyBindingBuilder}。
         */
        public InjectPropertyBindingBuilder<T> inject(String property, BindInfo<?> valueInfo);
        /**
         * 配置一个属性注入。
         * @param property 属性名
         * @param valueProvider provider 可以用来封装类型实例创建的细节。
         * @return 返回细粒度绑定接口 - {@link InjectPropertyBindingBuilder}。
         */
        public InjectPropertyBindingBuilder<T> inject(String property, Provider<?> valueProvider);
    }
    /**负责启动之后的生命周期方法映射。*/
    public interface LifeBindingBuilder<T> extends ScopedBindingBuilder<T> {
        /* *当容器启动时调用的方法*/
        /*public LifeBindingBuilder initMethod(String methodName);*/
    }
    /**Bean存在的作用域*/
    public interface ScopedBindingBuilder<T> extends MetaDataBindingBuilder<T> {
        /**
         * 注册为单例，该方法会覆盖toScope方法的设置。
         * @return 返回细粒度绑定接口 - {@link MetaDataBindingBuilder}。
         */
        public MetaDataBindingBuilder<T> asEagerSingleton();
        /**
         * 设置Scope。
         * @param scope 作用域
         * @return 返回细粒度绑定接口 - {@link MetaDataBindingBuilder}。
         */
        public MetaDataBindingBuilder<T> toScope(Scope scope);
        /**
         * 设置Scope。
         * @param scope 作用域
         * @return 返回细粒度绑定接口 - {@link MetaDataBindingBuilder}。
         */
        public MetaDataBindingBuilder<T> toScope(Provider<Scope> scope);
    }
    /**绑定元信息*/
    public interface MetaDataBindingBuilder<T> {
        /**
         * 设置元信息。
         * @param key 元信息 key
         * @param value 元信息 value
         * @return 返回细粒度绑定接口 - {@link MetaDataBindingBuilder}。
         */
        public MetaDataBindingBuilder<T> metaData(String key, Object value);
        /**
         * 转换为 {@link BindInfo} 接口对象。
         * @return 返回{@link BindInfo}。
         */
        public BindInfo<T> toInfo();
    }
}