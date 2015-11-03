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
package net.hasor.core.binder;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoBuilder;
import net.hasor.core.Environment;
import net.hasor.core.Hasor;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import net.hasor.core.binder.aop.matcher.AopMatchers;
import net.hasor.core.context.BeanBuilder;
import net.hasor.core.context.DataContext;
import net.hasor.core.info.AopBindInfoAdapter;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 标准的 {@link ApiBinder} 接口实现，Hasor 在初始化模块时会为每个模块独立分配一个 ApiBinder 接口实例。
 * <p>抽象方法 {@link #contextData()} ,会返回一个类( {@link DataContext} )
 * 用于配置Bean信息。
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractBinder implements ApiBinder {
    private Logger logger = LoggerFactory.getLogger(getClass());
    //
    public Environment getEnvironment() {
        return this.dataContext().getEnvironment();
    }
    public Set<Class<?>> findClass(final Class<?> featureType) {
        if (featureType == null) {
            return null;
        }
        Set<Class<?>> res = this.getEnvironment().findClass(featureType);
        return res;
    }
    public void installModule(final Module module) throws Throwable {
        logger.info("installModule ->" + module);
        module.loadModule(this);
        /*确保由代码加载的module也可以接收到onStart方法的调用。*/
        Hasor.onStart(this.getEnvironment(), module);
    }
    //
    /*------------------------------------------------------------------------------------Binding*/
    /**注册一个类型*/
    protected abstract DataContext dataContext();
    //
    public <T> NamedBindingBuilder<T> bindType(final Class<T> type) {
        BeanBuilder builder = this.dataContext().getBeanBuilder();
        BindInfoBuilder<T> typeBuilder = this.dataContext().getBindInfoContainer().createBuilder(type, builder);
        typeBuilder.setBindID(UUID.randomUUID().toString());/*设置唯一ID*/
        return new BindingBuilderImpl<T>(typeBuilder);
    }
    public <T> MetaDataBindingBuilder<T> bindType(final Class<T> type, final T instance) {
        return this.bindType(type).toInstance(instance);
    }
    public <T> InjectPropertyBindingBuilder<T> bindType(final Class<T> type, final Class<? extends T> implementation) {
        return this.bindType(type).to(implementation);
    }
    public <T> ScopedBindingBuilder<T> bindType(final Class<T> type, final Provider<T> provider) {
        return this.bindType(type).toProvider(provider);
    }
    public <T> InjectPropertyBindingBuilder<T> bindType(final String withName, final Class<T> type) {
        return this.bindType(type).nameWith(withName).to(type);
    }
    public <T> MetaDataBindingBuilder<T> bindType(final String withName, final Class<T> type, final T instance) {
        return this.bindType(type).nameWith(withName).toInstance(instance);
    }
    public <T> InjectPropertyBindingBuilder<T> bindType(final String withName, final Class<T> type, final Class<? extends T> implementation) {
        return this.bindType(type).nameWith(withName).to(implementation);
    }
    public <T> LifeBindingBuilder<T> bindType(final String withName, final Class<T> type, final Provider<T> provider) {
        return this.bindType(type).nameWith(withName).toProvider(provider);
    }
    //
    /*----------------------------------------------------------------------------------------Aop*/
    //    static {
    //        final String tmpWordReg = "[^\\s,]+";
    //        final String tmpParamsReg = "(?:(?:W *, *){0,}W)?";
    //        final String tmpMethodReg = "(W) (W.W)\\((P)\\)";
    //        String methodReg = tmpMethodReg.replace("P", tmpParamsReg).replace("W", tmpWordReg);
    //        InterceptorPattern = java.util.regex.Pattern.compile(methodReg);
    //    }
    //    private static final Pattern InterceptorPattern;
    public void bindInterceptor(final String matcherExpression, final MethodInterceptor interceptor) {
        //
        //
        Matcher<Class<?>> matcherClass = AopMatchers.expressionClass(matcherExpression);
        Matcher<Method> matcherMethod = AopMatchers.expressionMethod(matcherExpression);
        this.bindInterceptor(matcherClass, matcherMethod, interceptor);
    }
    public void bindInterceptor(final Matcher<Class<?>> matcherClass, final Matcher<Method> matcherMethod, final MethodInterceptor interceptor) {
        Hasor.assertIsNotNull(matcherClass, "matcherClass is null.");
        Hasor.assertIsNotNull(matcherMethod, "matcherMethod is null.");
        Hasor.assertIsNotNull(interceptor, "interceptor is null.");
        //
        AopBindInfoAdapter aopAdapter = new AopBindInfoAdapter(matcherClass, matcherMethod, interceptor);
        aopAdapter = Hasor.autoAware(this.getEnvironment(), aopAdapter);
        this.bindType(AopBindInfoAdapter.class).uniqueName().toInstance(aopAdapter);
    }
    //
    /*------------------------------------------------------------------------------------Binding*/
    /** 一堆接口的实现 */
    private static class BindingBuilderImpl<T> implements //
            InjectConstructorBindingBuilder<T>, InjectPropertyBindingBuilder<T>,//
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
        public MetaDataBindingBuilder<T> metaData(final String key, final Object value) {
            this.typeBuilder.setMetaData(key, value);
            return this;
        }
        public MetaDataBindingBuilder<T> asEagerSingleton() {
            this.typeBuilder.setSingleton(true);
            return this;
        }
        public NamedBindingBuilder<T> idWith(String newID) {
            if (StringUtils.isBlank(newID) == false) {
                this.typeBuilder.setBindID(newID);
            }
            return this;
        }
        public LinkedBindingBuilder<T> nameWith(final String name) {
            this.typeBuilder.setBindName(name);
            return this;
        }
        public LinkedBindingBuilder<T> uniqueName() {
            this.typeBuilder.setBindName(UUID.randomUUID().toString());
            return this;
        }
        public MetaDataBindingBuilder<T> toScope(final Scope scope) {
            return this.toScope(new InstanceProvider<Scope>(scope));
        }
        public MetaDataBindingBuilder<T> toInstance(final T instance) {
            return this.toProvider(new InstanceProvider<T>(instance));
        }
        public InjectPropertyBindingBuilder<T> to(final Class<? extends T> implementation) {
            this.typeBuilder.setSourceType(implementation);
            return this;
        }
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
        public MetaDataBindingBuilder<T> toScope(final Provider<Scope> scope) {
            this.typeBuilder.setScopeProvider(scope);
            return this;
        }
        public LifeBindingBuilder<T> toProvider(final Provider<T> provider) {
            if (provider != null) {
                this.typeBuilder.setCustomerProvider(provider);
            }
            return this;
        }
        //
        public InjectPropertyBindingBuilder<T> injectValue(final String property, final Object value) {
            return this.inject(property, new InstanceProvider<Object>(value));
        }
        public InjectPropertyBindingBuilder<T> inject(final String property, final BindInfo<?> valueInfo) {
            this.typeBuilder.addInject(property, valueInfo);
            return this;
        }
        public InjectPropertyBindingBuilder<T> inject(final String property, final Provider<?> valueProvider) {
            this.typeBuilder.addInject(property, valueProvider);
            return this;
        }
        public InjectConstructorBindingBuilder<T> injectValue(final int index, final Object value) {
            return this.inject(index, new InstanceProvider<Object>(value));
        }
        public InjectConstructorBindingBuilder<T> inject(final int index, final BindInfo<?> valueInfo) {
            if (index >= this.initParams.length) {
                throw new IndexOutOfBoundsException("index out of bounds.");
            }
            this.typeBuilder.setConstructor(index, this.initParams[index], valueInfo);
            return this;
        }
        public InjectConstructorBindingBuilder<T> inject(final int index, final Provider<?> valueProvider) {
            if (index >= this.initParams.length) {
                throw new IndexOutOfBoundsException("index out of bounds.");
            }
            this.typeBuilder.setConstructor(index, this.initParams[index], valueProvider);
            return this;
        }
        public BindInfo<T> toInfo() {
            return this.typeBuilder.toInfo();
        }
    }
    //
}