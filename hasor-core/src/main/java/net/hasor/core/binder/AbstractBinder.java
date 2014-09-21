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
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoBuilder;
import net.hasor.core.BindInfoDefineManager;
import net.hasor.core.Environment;
import net.hasor.core.EventCallBackHook;
import net.hasor.core.EventListener;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import net.hasor.core.Settings;
import net.hasor.core.binder.aop.matcher.AopMatchers;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
/**
 * 标准的 {@link ApiBinder} 接口实现，Hasor 在初始化模块时会为每个模块独立分配一个 ApiBinder 接口实例。
 * <p>抽象方法 {@link #configModule()} ,会返回一个接口( {@link net.hasor.core.ApiBinder.ModuleSettings ModuleSettings} )
 * 用于配置当前模块依赖情况。
 * <p><b><i>提示：</i></b>模块代理类 {@link net.hasor.core.module.ModuleProxy} 可以为 {@link #configModule()} 方法提供支持。
 * @see net.hasor.core.module.ModuleProxy
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractBinder implements ApiBinder {
    private Environment environment = null;
    //
    protected AbstractBinder(final Environment envContext) {
        this.environment = envContext;
    }
    public Environment getEnvironment() {
        return this.environment;
    }
    public Settings getSettings() {
        return this.getEnvironment().getSettings();
    }
    public <T extends AppContextAware> T autoAware(final T aware) {
        this.bindType(AppContextAware.class).uniqueName().toInstance(aware);
        return aware;
    }
    public Set<Class<?>> findClass(final Class<?> featureType) {
        if (featureType == null) {
            return null;
        }
        return this.getEnvironment().findClass(featureType);
    }
    public void installModule(final Module module) throws Throwable {
        module.loadModule(this);
    }
    //
    /*--------------------------------------------------------------------------------------Event*/
    public void pushListener(final String eventType, final EventListener eventListener) {
        this.getEnvironment().pushListener(eventType, eventListener);
    }
    public void addListener(final String eventType, final EventListener eventListener) {
        this.getEnvironment().addListener(eventType, eventListener);
    }
    public void removeListener(final String eventType, final EventListener eventListener) {
        this.getEnvironment().removeListener(eventType, eventListener);
    }
    public void fireSyncEvent(final String eventType, final Object... objects) {
        this.getEnvironment().fireSyncEvent(eventType, objects);
    }
    public void fireSyncEvent(final String eventType, final EventCallBackHook callBack, final Object... objects) {
        this.getEnvironment().fireSyncEvent(eventType, callBack, objects);
    }
    public void fireAsyncEvent(final String eventType, final Object... objects) {
        this.getEnvironment().fireAsyncEvent(eventType, objects);
    }
    public void fireAsyncEvent(final String eventType, final EventCallBackHook callBack, final Object... objects) {
        this.getEnvironment().fireAsyncEvent(eventType, callBack, objects);
    }
    //
    /*------------------------------------------------------------------------------------Binding*/
    /**注册一个类型*/
    protected abstract BindInfoDefineManager getBuilderRegister();
    //
    public <T> NamedBindingBuilder<T> bindType(final Class<T> type) {
        BindInfoBuilder<T> typeBuilder = this.getBuilderRegister().createBuilder(type);
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
        return this.bindType(type).nameWith(withName).to(type);
    }
    public <T> LifeBindingBuilder<T> bindType(final String withName, final Class<T> type, final Provider<T> provider) {
        return this.bindType(type).nameWith(withName).toProvider(provider);
    }
    //
    /*----------------------------------------------------------------------------------------Aop*/
    public void bindInterceptor(final String matcherExpression, final MethodInterceptor interceptor) {
        Matcher<Class<?>> matcherClass = AopMatchers.expressionClass(matcherExpression);
        Matcher<Method> matcherMethod = AopMatchers.expressionMethod(matcherExpression);
        this.bindInterceptor(matcherClass, matcherMethod, interceptor);
    }
    public void bindInterceptor(final Matcher<Class<?>> matcherClass, final Matcher<Method> matcherMethod, final MethodInterceptor interceptor) {
        this.getBuilderRegister().addAop(matcherClass, matcherMethod, interceptor);
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
                this.typeBuilder.setBindName(newID);
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
                    this.typeBuilder.setInitParam(i, params[i], new InstanceProvider<Object>(defaultValue));
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
            this.typeBuilder.setInitParam(index, this.initParams[index], valueInfo);
            return this;
        }
        public InjectConstructorBindingBuilder<T> inject(final int index, final Provider<?> valueProvider) {
            if (index >= this.initParams.length) {
                throw new IndexOutOfBoundsException("index out of bounds.");
            }
            this.typeBuilder.setInitParam(index, this.initParams[index], valueProvider);
            return this;
        }
        public BindInfo<T> toInfo() {
            return this.typeBuilder.toInfo();
        }
    }
    //
}