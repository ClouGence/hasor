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
package net.hasor.core.binder;
import net.hasor.core.*;
import net.hasor.core.classcode.matcher.AopMatchers;
import net.hasor.core.container.BeanBuilder;
import net.hasor.core.container.ScopManager;
import net.hasor.core.info.AopBindInfoAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;
/**
 * 标准的 {@link ApiBinder} 接口实现，Hasor 在初始化模块时会为每个模块独立分配一个 ApiBinder 接口实例。
 * <p>抽象方法 {@link #getBeanBuilder()} ,会返回一个类( {@link BeanBuilder} )用于配置Bean信息。
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractBinder implements ApiBinder {
    private Environment environment;
    public AbstractBinder(Environment environment) {
        this.environment = Hasor.assertIsNotNull(environment, "environment is null.");
    }
    //
    @Override
    public Environment getEnvironment() {
        return this.environment;
    }
    @Override
    public Set<Class<?>> findClass(final Class<?> featureType) {
        String[] spanPackage = this.getEnvironment().getSpanPackage();
        return this.getEnvironment().findClass(featureType, spanPackage);
    }
    @Override
    public Set<Class<?>> findClass(Class<?> featureType, String... scanPackages) {
        if (featureType == null || scanPackages == null || scanPackages.length == 0) {
            return null;
        }
        return this.getEnvironment().findClass(featureType, scanPackages);
    }
    @Override
    public void installModule(final Module module) throws Throwable {
        //see : net.hasor.core.binder.ApiBinderInvocationHandler.invoke()
        throw new IllegalStateException("current state is not allowed.");
    }
    @Override
    public <T extends ApiBinder> T tryCast(Class<T> castApiBinder) {
        //see : net.hasor.core.binder.ApiBinderInvocationHandler.invoke()
        throw new IllegalStateException("current state is not allowed.");
    }
    //
    /*------------------------------------------------------------------------------------Binding*/
    protected Class<?> getBinderSource() {
        return ApiBinder.class;
    }
    /**注册一个类型*/
    protected abstract BeanBuilder getBeanBuilder();

    /**注册一个类型*/
    protected abstract ScopManager getScopManager();
    //
    @Override
    public <T> NamedBindingBuilder<T> bindType(final Class<T> type) {
        BeanBuilder builder = this.getBeanBuilder();
        BindInfoBuilder<T> typeBuilder = builder.createInfoAdapter(type, this.getBinderSource());
        return new BindingBuilderImpl<T>(typeBuilder);
    }
    @Override
    public <T> MetaDataBindingBuilder<T> bindType(final Class<T> type, final T instance) {
        return this.bindType(type).toInstance(instance);
    }
    @Override
    public <T> InjectPropertyBindingBuilder<T> bindType(final Class<T> type, final Class<? extends T> implementation) {
        return this.bindType(type).to(implementation);
    }
    @Override
    public <T> ScopedBindingBuilder<T> bindType(final Class<T> type, final Provider<T> provider) {
        return this.bindType(type).toProvider(provider);
    }
    @Override
    public <T> InjectPropertyBindingBuilder<T> bindType(final String withName, final Class<T> type) {
        return this.bindType(type).nameWith(withName).to(type);
    }
    @Override
    public <T> MetaDataBindingBuilder<T> bindType(final String withName, final Class<T> type, final T instance) {
        return this.bindType(type).nameWith(withName).toInstance(instance);
    }
    @Override
    public <T> InjectPropertyBindingBuilder<T> bindType(final String withName, final Class<T> type, final Class<? extends T> implementation) {
        return this.bindType(type).nameWith(withName).to(implementation);
    }
    @Override
    public <T> LifeBindingBuilder<T> bindType(final String withName, final Class<T> type, final Provider<T> provider) {
        return this.bindType(type).nameWith(withName).toProvider(provider);
    }
    //
    @Override
    public Provider<Scope> registerScope(String scopeName, Provider<Scope> scope) {
        return this.getScopManager().registerScope(scopeName, scope);
    }
    @Override
    public Provider<Scope> registerScope(String scopeName, Scope scope) {
        return this.registerScope(scopeName, new InstanceProvider<Scope>(scope));
    }
    /*----------------------------------------------------------------------------------------Aop*/
    //    static {
    //        final String tmpWordReg = "[^\\s,]+";
    //        final String tmpParamsReg = "(?:(?:W *, *){0,}W)?";
    //        final String tmpMethodReg = "(W) (W.W)\\((P)\\)";
    //        String methodReg = tmpMethodReg.replace("P", tmpParamsReg).replace("W", tmpWordReg);
    //        InterceptorPattern = java.util.regex.Pattern.compile(methodReg);
    //    }
    //    private static final Pattern InterceptorPattern;
    @Override
    public void bindInterceptor(final String matcherExpression, final MethodInterceptor interceptor) {
        //
        Matcher<Class<?>> matcherClass = AopMatchers.expressionClass(matcherExpression);
        Matcher<Method> matcherMethod = AopMatchers.expressionMethod(matcherExpression);
        this.bindInterceptor(matcherClass, matcherMethod, interceptor);
    }
    @Override
    public void bindInterceptor(final Matcher<Class<?>> matcherClass, final Matcher<Method> matcherMethod, final MethodInterceptor interceptor) {
        Hasor.assertIsNotNull(matcherClass, "matcherClass is null.");
        Hasor.assertIsNotNull(matcherMethod, "matcherMethod is null.");
        Hasor.assertIsNotNull(interceptor, "interceptor is null.");
        //
        AopBindInfoAdapter aopAdapter = new AopBindInfoAdapter(matcherClass, matcherMethod, interceptor);
        aopAdapter = Hasor.autoAware(this.getEnvironment(), aopAdapter);
        this.bindType(AopBindInfoAdapter.class).uniqueName().toInstance(aopAdapter);
    }
    @Override
    public <T> List<BindInfo<T>> findBindingRegister(Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        return getBeanBuilder().findBindInfoList(bindType);
    }
    @Override
    public <T> BindInfo<T> findBindingRegister(String withName, Class<T> bindType) {
        Hasor.assertIsNotNull(withName, "withName is null.");
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        return getBeanBuilder().findBindInfo(withName, bindType);
    }
    @Override
    public <T> BindInfo<T> getBindInfo(String bindID) {
        Hasor.assertIsNotNull(bindID, "bindID is null.");
        return getBeanBuilder().findBindInfoByID(bindID);
    }
    @Override
    public <T> BindInfo<T> getBindInfo(Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        return getBeanBuilder().findBindInfoByType(bindType);
    }
    //
    /*------------------------------------------------------------------------------------Binding*/
    /** 一堆接口的实现 */
    private class BindingBuilderImpl<T> implements //
            InjectConstructorBindingBuilder<T>, InjectPropertyBindingBuilder<T>, //
            NamedBindingBuilder<T>, LinkedBindingBuilder<T>, LifeBindingBuilder<T>, ScopedBindingBuilder<T>, MetaDataBindingBuilder<T> {
        private BindInfoBuilder<T> typeBuilder = null;
        private Class<?>[]         initParams  = new Class<?>[0];
        //
        public BindingBuilderImpl(final BindInfoBuilder<T> typeBuilder) {
            this.typeBuilder = typeBuilder;
        }
        @Override
        public LifeBindingBuilder<T> initMethod(String methodName) {
            this.typeBuilder.initMethod(methodName);
            return this;
        }
        @Override
        public MetaDataBindingBuilder<T> metaData(final String key, final Object value) {
            this.typeBuilder.setMetaData(key, value);
            return this;
        }
        @Override
        public MetaDataBindingBuilder<T> asEagerPrototype() {
            this.typeBuilder.setSingleton(false);
            return this;
        }
        @Override
        public MetaDataBindingBuilder<T> asEagerSingleton() {
            this.typeBuilder.setSingleton(true);
            return this;
        }
        @Override
        public NamedBindingBuilder<T> idWith(String newID) {
            if (!StringUtils.isBlank(newID)) {
                this.typeBuilder.setBindID(newID);
                this.typeBuilder.setBindName(newID);
            }
            return this;
        }
        @Override
        public LinkedBindingBuilder<T> nameWith(final String name) {
            this.typeBuilder.setBindName(name);
            return this;
        }
        @Override
        public LinkedBindingBuilder<T> uniqueName() {
            this.typeBuilder.setBindName(UUID.randomUUID().toString());
            return this;
        }
        @Override
        public MetaDataBindingBuilder<T> toScope(final Scope scope) {
            return this.toScope(new InstanceProvider<Scope>(scope));
        }
        @Override
        public MetaDataBindingBuilder<T> toInstance(final T instance) {
            return this.toProvider(new InstanceProvider<T>(instance));
        }
        @Override
        public InjectPropertyBindingBuilder<T> to(final Class<? extends T> implementation) {
            this.typeBuilder.setSourceType(implementation);
            return this;
        }
        @Override
        public InjectConstructorBindingBuilder<T> toConstructor(final Constructor<? extends T> constructor) {
            Class<? extends T> targetType = constructor.getDeclaringClass();
            //因为设置了构造方法因此重新设置SourceTypeF
            this.typeBuilder.setSourceType(targetType);
            //
            Class<?>[] params = constructor.getParameterTypes();
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    Object defaultValue = BeanUtils.getDefaultValue(params[i]);//取得参数的默认值
                    this.typeBuilder.setConstructor(i, params[i], new InstanceProvider<Object>(defaultValue));
                }
                this.initParams = params;
            }
            return this;
        }
        @Override
        public MetaDataBindingBuilder<T> toScope(final Provider<Scope> scope) {
            this.typeBuilder.setScopeProvider(scope);
            return this;
        }
        @Override
        public MetaDataBindingBuilder<T> toScope(String scopeName) {
            return this.toScope(getScopManager().findScope(scopeName));
        }
        @Override
        public LifeBindingBuilder<T> toProvider(final Provider<? extends T> provider) {
            if (provider != null) {
                this.typeBuilder.setCustomerProvider(provider);
            }
            return this;
        }
        //
        @Override
        public InjectPropertyBindingBuilder<T> injectValue(final String property, final Object value) {
            return this.inject(property, new InstanceProvider<Object>(value));
        }
        @Override
        public InjectPropertyBindingBuilder<T> inject(final String property, final BindInfo<?> valueInfo) {
            this.typeBuilder.addInject(property, valueInfo);
            return this;
        }
        @Override
        public InjectPropertyBindingBuilder<T> inject(final String property, final Provider<?> valueProvider) {
            this.typeBuilder.addInject(property, valueProvider);
            return this;
        }
        @Override
        public InjectConstructorBindingBuilder<T> injectValue(final int index, final Object value) {
            return this.inject(index, new InstanceProvider<Object>(value));
        }
        @Override
        public InjectConstructorBindingBuilder<T> inject(final int index, final BindInfo<?> valueInfo) {
            if (index >= this.initParams.length) {
                throw new IndexOutOfBoundsException("index out of bounds.");
            }
            this.typeBuilder.setConstructor(index, this.initParams[index], valueInfo);
            return this;
        }
        @Override
        public InjectConstructorBindingBuilder<T> inject(final int index, final Provider<?> valueProvider) {
            if (index >= this.initParams.length) {
                throw new IndexOutOfBoundsException("index out of bounds.");
            }
            this.typeBuilder.setConstructor(index, this.initParams[index], valueProvider);
            return this;
        }
        @Override
        public BindInfo<T> toInfo() {
            return this.typeBuilder.toInfo();
        }
    }
    //
}