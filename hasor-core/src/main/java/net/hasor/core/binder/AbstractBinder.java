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
import net.hasor.core.aop.ReadWriteType;
import net.hasor.core.aop.SimplePropertyDelegate;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.core.info.AopBindInfoAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.spi.SpiJudge;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 标准的 {@link ApiBinder} 接口实现，Hasor 在初始化模块时会为每个模块独立分配一个 ApiBinder 接口实例。
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractBinder implements ApiBinder {
    protected     Logger      logger = LoggerFactory.getLogger(getClass());
    private final Environment environment;

    public AbstractBinder(Environment environment) {
        this.environment = Objects.requireNonNull(environment, "environment is null.");
    }

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
    public Set<Class<?>> findClass(final Class<?> featureType, final String... scanPackages) {
        if (featureType == null || scanPackages == null || scanPackages.length == 0) {
            return null;
        }
        return this.getEnvironment().findClass(featureType, scanPackages);
    }

    @Override
    public ApiBinder installModule(final Module... modules) throws Throwable {
        Environment environment = getEnvironment();
        for (Module module : modules) {
            logger.info("installModule ->" + module);
            /*加载*/
            module.loadModule(self());
            /*启动*/
            HasorUtils.pushStartListener(environment, (net.hasor.core.EventListener<AppContext>) (event, eventData) -> module.onStart(eventData));
            /*停止*/
            HasorUtils.pushShutdownListener(environment, (net.hasor.core.EventListener<AppContext>) (event, eventData) -> module.onStop(eventData));
        }
        return this;
    }

    @Override
    public <T extends ApiBinder> T tryCast(final Class<T> castApiBinder) {
        ApiBinder self = self();
        return (!castApiBinder.isInstance(self)) ? null : (T) self;
    }

    protected abstract ApiBinder self();

    @Override
    public boolean isSingleton(BindInfo<?> bindInfo) {
        return containerFactory().getScopeContainer().isSingleton(bindInfo);
    }

    @Override
    public boolean isSingleton(Class<?> targetType) {
        BindInfo<?> bindInfo = containerFactory().getBindInfoContainer().findBindInfo("", targetType);
        if (bindInfo != null) {
            return containerFactory().getScopeContainer().isSingleton(bindInfo);
        } else {
            return containerFactory().getScopeContainer().isSingleton(targetType);
        }
    }

    /*------------------------------------------------------------------------------------Binding*/
    protected abstract BindInfoBuilderFactory containerFactory();

    //
    @Override
    public <T> NamedBindingBuilder<T> bindType(final Class<T> type) {
        BindInfoBuilder<T> typeBuilder = this.containerFactory().getBindInfoContainer().createInfoAdapter(type, this);
        return new BindingBuilderImpl<>(typeBuilder);
    }

    @Override
    public <T extends EventListener> void bindSpiListener(final Class<T> spiType, final Supplier<T> listener) {
        containerFactory().getSpiContainer().addListener(spiType, listener);
    }

    @Override
    public <T extends EventListener> void bindSpiJudge(Class<T> spiType, Supplier<SpiJudge> chainProcessorSupplier) {
        this.containerFactory().getSpiContainer().bindSpiJudge(spiType, chainProcessorSupplier);
    }

    @Override
    public <T extends Scope> Supplier<T> bindScope(final String scopeName, final Supplier<T> scopeProvider) {
        return this.containerFactory().getScopeContainer().registerScopeProvider(scopeName, scopeProvider);
    }

    @Override
    public Supplier<Scope> findScope(String scopeName) {
        return this.containerFactory().getScopeContainer().findScope(scopeName);
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
        Predicate<Class<?>> matcherClass = Matchers.expressionClass(matcherExpression);
        Predicate<Method> matcherMethod = Matchers.expressionMethod(matcherExpression);
        this.bindInterceptor(matcherClass, matcherMethod, interceptor);
    }

    @Override
    public void bindInterceptor(final Predicate<Class<?>> matcherClass, final Predicate<Method> matcherMethod, final MethodInterceptor interceptor) {
        Objects.requireNonNull(matcherClass, "matcherClass is null.");
        Objects.requireNonNull(matcherMethod, "matcherMethod is null.");
        Objects.requireNonNull(interceptor, "interceptor is null.");
        //
        AopBindInfoAdapter aopAdapter = new AopBindInfoAdapter(matcherClass, matcherMethod, interceptor);
        aopAdapter = HasorUtils.autoAware(this.getEnvironment(), aopAdapter);
        this.bindType(AopBindInfoAdapter.class).uniqueName().toInstance(aopAdapter);
    }

    @Override
    public <T> List<BindInfo<T>> findBindingRegister(final Class<T> bindType) {
        Objects.requireNonNull(bindType, "bindType is null.");
        return this.containerFactory().getBindInfoContainer().findBindInfoList(bindType);
    }

    @Override
    public <T> BindInfo<T> findBindingRegister(final String withName, final Class<T> bindType) {
        Objects.requireNonNull(withName, "withName is null.");
        Objects.requireNonNull(bindType, "bindType is null.");
        return this.containerFactory().getBindInfoContainer().findBindInfo(withName, bindType);
    }

    @Override
    public <T> BindInfo<T> getBindInfo(final String bindID) {
        Objects.requireNonNull(bindID, "bindID is null.");
        return this.containerFactory().getBindInfoContainer().findBindInfo(bindID);
    }

    @Override
    public <T> BindInfo<T> getBindInfo(final Class<T> bindType) {
        Objects.requireNonNull(bindType, "bindType is null.");
        return this.containerFactory().getBindInfoContainer().findBindInfo(null, bindType);
    }
    //
    /*------------------------------------------------------------------------------------Binding*/

    /** 一堆接口的实现 */
    private class BindingBuilderImpl<T> implements //
            InjectConstructorBindingBuilder<T>, InjectPropertyBindingBuilder<T>, //
            NamedBindingBuilder<T>, LinkedBindingBuilder<T>, LifeBindingBuilder<T>, ScopedBindingBuilder<T>, MetaDataBindingBuilder<T> {
        private BindInfoBuilder<T> typeBuilder = null;
        private Class<? extends T> sourceType  = null;
        private Class<?>[]         initParams  = new Class<?>[0];

        public BindingBuilderImpl(final BindInfoBuilder<T> typeBuilder) {
            this.typeBuilder = typeBuilder;
        }

        @Override
        public LifeBindingBuilder<T> initMethod(final String methodName) {
            this.typeBuilder.initMethod(methodName);
            return this;
        }

        @Override
        public LifeBindingBuilder<T> destroyMethod(final String methodName) {
            this.typeBuilder.destroyMethod(methodName);
            return this;
        }

        @Override
        public MetaDataBindingBuilder<T> metaData(final String key, final Object value) {
            this.typeBuilder.setMetaData(key, value);
            return this;
        }

        @Override
        public NamedBindingBuilder<T> idWith(final String newID) {
            if (!StringUtils.isBlank(newID)) {
                this.typeBuilder.setBindID(newID);
            }
            return this;
        }

        @Override
        public NamedBindingBuilder<T> bothWith(final String nameString) {
            if (!StringUtils.isBlank(nameString)) {
                this.typeBuilder.setBindID(nameString);
                this.typeBuilder.setBindName(nameString);
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
            String newID = UUID.randomUUID().toString().replace("-", "");
            this.typeBuilder.setBindName(newID);
            return this;
        }

        @Override
        public TypeSupplierBindingBuilder<T> to(final Class<? extends T> implementation) {
            Objects.requireNonNull(implementation, "implementation is null.");
            this.sourceType = implementation;
            this.typeBuilder.setSourceType(implementation);
            return this;
        }

        @Override
        public InjectConstructorBindingBuilder<T> toConstructor(final Constructor<? extends T> constructor) {
            Objects.requireNonNull(constructor, "constructor is null.");
            Class<? extends T> targetType = constructor.getDeclaringClass();
            //因为设置了构造方法因此重新设置SourceTypeF
            this.typeBuilder.setSourceType(targetType);
            //
            Class<?>[] params = constructor.getParameterTypes();
            for (int i = 0; i < params.length; i++) {
                Object defaultValue = BeanUtils.getDefaultValue(params[i]);//取得参数的默认值
                this.typeBuilder.setConstructor(i, params[i], new InstanceProvider<>(defaultValue));
            }
            this.initParams = params;
            return this;
        }

        @Override
        public OptionPropertyBindingBuilder<T> toScope(final Supplier<Scope>... scope) {
            Objects.requireNonNull(scope, "the Provider of Scope is null.");
            this.typeBuilder.addScopeProvider(scope);
            return this;
        }

        @Override
        public OptionPropertyBindingBuilder<T> toScope(final String... scopeName) {
            Supplier[] supplierArrays = Arrays.stream(scopeName).map(name -> {
                Supplier<Scope> scope = containerFactory().getScopeContainer().findScope(name);
                if (scope == null) {
                    throw new IllegalStateException("scope '" + name + "' Have not yet registered");
                }
                return scope;
            }).toArray(Supplier[]::new);
            return this.toScope(supplierArrays);
        }

        @Override
        public LifeBindingBuilder<T> toProvider(final Supplier<? extends T> provider) {
            if (provider != null) {
                this.typeBuilder.setCustomerProvider(provider);
            }
            return this;
        }

        @Override
        public InjectPropertyBindingBuilder<T> injectValue(final String property, final Object value) {
            return this.inject(property, new InstanceProvider<>(value));
        }

        @Override
        public InjectPropertyBindingBuilder<T> inject(final String property, final BindInfo<?> valueInfo) {
            this.typeBuilder.addInject(property, valueInfo);
            return this;
        }

        @Override
        public InjectPropertyBindingBuilder<T> inject(final String property, final Supplier<?> valueProvider) {
            this.typeBuilder.addInject(property, valueProvider);
            return this;
        }

        @Override
        public InjectPropertyBindingBuilder<T> inject(final String property, final Class<?> valueType) {
            this.typeBuilder.addInject(property, bindType(valueType).toInfo());
            return this;
        }

        @Override
        public InjectPropertyBindingBuilder<T> overwriteAnnotation() {
            this.typeBuilder.overwriteAnnotation(true);
            return this;
        }

        @Override
        public InjectConstructorBindingBuilder<T> injectValue(final int index, final Object value) {
            return this.inject(index, new InstanceProvider<>(value));
        }

        @Override
        public InjectConstructorBindingBuilder<T> inject(final int index, final BindInfo<?> valueInfo) {
            checkIndex(this.initParams, index);
            this.typeBuilder.setConstructor(index, this.initParams[index], valueInfo);
            return this;
        }

        @Override
        public InjectConstructorBindingBuilder<T> inject(final int index, final Supplier<?> valueProvider) {
            checkIndex(this.initParams, index);
            this.typeBuilder.setConstructor(index, this.initParams[index], valueProvider);
            return this;
        }

        @Override
        public InjectConstructorBindingBuilder<T> inject(final int index, final Class<?> valueType) {
            checkIndex(this.initParams, index);
            this.typeBuilder.setConstructor(index, this.initParams[index], bindType(valueType).toInfo());
            return this;
        }

        @Override
        public BindInfo<T> toInfo() {
            return this.typeBuilder.toInfo();
        }

        @Override
        public LifeBindingBuilder<T> toTypeSupplier(TypeSupplier typeSupplier) {
            final Class<? extends T> bindType = (this.sourceType == null) ?//
                    this.typeBuilder.toInfo().getBindType() ://
                    this.sourceType;
            //
            this.toProvider(() -> typeSupplier.get(bindType));
            return this;
        }

        @Override
        public TypeSupplierBindingBuilder<T> dynamicProperty(String name, Class<?> propertyType) {
            Object defaultValue = BeanUtils.getDefaultValue(propertyType);
            return this.dynamicProperty(name, propertyType, new SimplePropertyDelegate(defaultValue));
        }

        @Override
        public TypeSupplierBindingBuilder<T> dynamicProperty(String name, Class<?> propertyType, Class<? extends PropertyDelegate> delegate) {
            return this.dynamicProperty(name, propertyType, getProvider(delegate));
        }

        @Override
        public TypeSupplierBindingBuilder<T> dynamicProperty(String name, Class<?> propertyType, BindInfo<? extends PropertyDelegate> delegate) {
            return this.dynamicProperty(name, propertyType, getProvider(delegate));
        }

        @Override
        public TypeSupplierBindingBuilder<T> dynamicProperty(String name, Class<?> propertyType, Supplier<? extends PropertyDelegate> delegate) {
            this.typeBuilder.addDynamicProperty(name, propertyType, delegate, ReadWriteType.ReadWrite);
            return this;
        }

        @Override
        public TypeSupplierBindingBuilder<T> dynamicReadOnlyProperty(String name, Class<?> propertyType, Class<? extends PropertyDelegate> delegate) {
            Object defaultValue = BeanUtils.getDefaultValue(propertyType);
            return this.dynamicReadOnlyProperty(name, propertyType, new SimplePropertyDelegate(defaultValue));
        }

        @Override
        public TypeSupplierBindingBuilder<T> dynamicReadOnlyProperty(String name, Class<?> propertyType, BindInfo<? extends PropertyDelegate> delegate) {
            return this.dynamicReadOnlyProperty(name, propertyType, getProvider(delegate));
        }

        @Override
        public TypeSupplierBindingBuilder<T> dynamicReadOnlyProperty(String name, Class<?> propertyType, Supplier<? extends PropertyDelegate> delegate) {
            this.typeBuilder.addDynamicProperty(name, propertyType, delegate, ReadWriteType.ReadOnly);
            return this;
        }
    }

    private void checkIndex(final Class<?>[] length, final int index) {
        if (index >= length.length) {
            throw new IndexOutOfBoundsException("index out of bounds.");
        }
    }
}
