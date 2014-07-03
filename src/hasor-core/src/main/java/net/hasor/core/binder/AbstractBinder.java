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
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContextAware;
import net.hasor.core.Environment;
import net.hasor.core.EventCallBackHook;
import net.hasor.core.EventListener;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.RegisterInfo;
import net.hasor.core.Scope;
import net.hasor.core.Settings;
import net.hasor.core.binder.aop.AopMatcherMethodInterceptorData;
import net.hasor.core.binder.aop.matcher.AopMatchers;
import org.aopalliance.intercept.MethodInterceptor;
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
    protected AbstractBinder(Environment envContext) {
        this.environment = envContext;
    }
    public Environment getEnvironment() {
        return this.environment;
    }
    public Settings getSettings() {
        return this.getEnvironment().getSettings();
    }
    public void registerAware(AppContextAware aware) {
        this.bindingType(AppContextAware.class).uniqueName().toInstance(aware);
    }
    public Set<Class<?>> findClass(Class<?> featureType) {
        if (featureType == null)
            return null;
        return this.getEnvironment().findClass(featureType);
    }
    public void installModule(Module module) throws Throwable {
        module.loadModule(this);
    }
    //
    /*--------------------------------------------------------------------------------------Event*/
    public void pushListener(String eventType, EventListener eventListener) {
        this.getEnvironment().pushListener(eventType, eventListener);
    }
    public void addListener(String eventType, EventListener eventListener) {
        this.getEnvironment().addListener(eventType, eventListener);
    }
    public void removeListener(String eventType, EventListener eventListener) {
        this.getEnvironment().removeListener(eventType, eventListener);
    }
    public void fireSyncEvent(String eventType, Object... objects) {
        this.getEnvironment().fireSyncEvent(eventType, objects);
    }
    public void fireSyncEvent(String eventType, EventCallBackHook callBack, Object... objects) {
        this.getEnvironment().fireSyncEvent(eventType, callBack, objects);
    }
    public void fireAsyncEvent(String eventType, Object... objects) {
        this.getEnvironment().fireAsyncEvent(eventType, objects);
    }
    public void fireAsyncEvent(String eventType, EventCallBackHook callBack, Object... objects) {
        this.getEnvironment().fireAsyncEvent(eventType, callBack, objects);
    }
    //
    /*---------------------------------------------------------------------------------------Bean*/
    private static long referIndex = 0;
    private static long referIndex() {
        return referIndex++;
    }
    public BeanBindingBuilder defineBean(String beanName) {
        return new BeanBindingBuilderImpl().aliasName(beanName);
    }
    /** BeanBindingBuilder接口实现 */
    private class BeanBindingBuilderImpl implements BeanBindingBuilder {
        private ArrayList<String> names = new ArrayList<String>();
        public BeanBindingBuilder aliasName(String aliasName) {
            if (!StringUtils.isBlank(aliasName))
                this.names.add(aliasName);
            return this;
        }
        public <T> LinkedBindingBuilder<T> bindType(Class<T> beanType) {
            if (this.names.isEmpty() == true)
                throw new UnsupportedOperationException("the bean name is undefined!");
            /*将Bean类型注册到Hasor上，并且附上随机ID,用于和BeanInfo绑定。*/
            String referID = beanType.getName() + "#" + String.valueOf(referIndex());
            LinkedBindingBuilder<T> returnData = bindingType(beanType).nameWith(referID);
            //
            String[] aliasNames = this.names.toArray(new String[this.names.size()]);
            BeanInfoData<T> beanInfo = new BeanInfoData<T>(aliasNames, referID, beanType);
            /*将名字和BeanInfo绑到一起*/
            for (String nameItem : this.names) {
                bindingType(BeanInfo.class).nameWith(nameItem).toInstance(beanInfo);
            }
            return returnData;
        }
    }
    //
    /*------------------------------------------------------------------------------------Binding*/
    /**注册一个类型*/
    protected abstract <T> TypeBuilder<T> createTypeBuilder(Class<T> type);
    //
    public <T> NamedBindingBuilder<T> bindingType(Class<T> type) {
        TypeBuilder<T> typeBuilder = this.createTypeBuilder(type);
        return new NamedBindingBuilderImpl<T>(typeBuilder);
    }
    public <T> MetaDataBindingBuilder<T> bindingType(Class<T> type, T instance) {
        return this.bindingType(type).toInstance(instance);
    }
    public <T> InjectPropertyBindingBuilder<T> bindingType(Class<T> type, Class<? extends T> implementation) {
        return this.bindingType(type).to(implementation);
    }
    public <T> ScopedBindingBuilder<T> bindingType(Class<T> type, Provider<T> provider) {
        return this.bindingType(type).toProvider(provider);
    }
    public <T> InjectPropertyBindingBuilder<T> bindingType(String withName, Class<T> type) {
        return this.bindingType(type).nameWith(withName).to(type);
    }
    public <T> MetaDataBindingBuilder<T> bindingType(String withName, Class<T> type, T instance) {
        return this.bindingType(type).nameWith(withName).toInstance(instance);
    }
    public <T> InjectPropertyBindingBuilder<T> bindingType(String withName, Class<T> type, Class<? extends T> implementation) {
        return this.bindingType(type).nameWith(withName).to(type);
    }
    public <T> LifeBindingBuilder<T> bindingType(String withName, Class<T> type, Provider<T> provider) {
        return this.bindingType(type).nameWith(withName).toProvider(provider);
    }
    //
    /*----------------------------------------------------------------------------------------Aop*/
    public void bindInterceptor(String matcherExpression, MethodInterceptor interceptor) {
        Matcher<Class<?>> matcherClass = AopMatchers.expressionClass(matcherExpression);
        Matcher<Method> matcherMethod = AopMatchers.expressionMethod(matcherExpression);
        this.bindInterceptor(matcherClass, matcherMethod, interceptor);
    }
    public void bindInterceptor(Matcher<Class<?>> matcherClass, Matcher<Method> matcherMethod, MethodInterceptor interceptor) {
        AopMatcherMethodInterceptorData ard = new AopMatcherMethodInterceptorData(matcherClass, matcherMethod, interceptor);
        this.bindingType(AopMatcherMethodInterceptorData.class).uniqueName().toInstance(ard);
    }
    //
    /*------------------------------------------------------------------------------------Binding*/
    /**实体类型的Provider代理 */
    private static class InstanceProvider<T> implements Provider<T> {
        private T instance = null;
        public InstanceProvider(T instance) {
            this.instance = instance;
        }
        public T get() {
            return this.instance;
        }
    }
    /** 一堆接口的实现 */
    private static class NamedBindingBuilderImpl<T> implements //
            InjectConstructorBindingBuilder<T>, InjectPropertyBindingBuilder<T>,//
            NamedBindingBuilder<T>, LinkedBindingBuilder<T>, LifeBindingBuilder<T>, ScopedBindingBuilder<T>, MetaDataBindingBuilder<T> {
        private TypeBuilder<T> typeBuilder = null;
        private Class<?>[]     initParams  = new Class<?>[0];
        //
        public NamedBindingBuilderImpl(TypeBuilder<T> typeBuilder) {
            this.typeBuilder = typeBuilder;
        }
        public MetaDataBindingBuilder<T> metaData(String key, Object value) {
            this.typeBuilder.setMetaData(key, value);
            return this;
        }
        public MetaDataBindingBuilder<T> asEagerSingleton() {
            this.typeBuilder.setSingleton(true);
            return this;
        }
        public LinkedBindingBuilder<T> nameWith(String name) {
            this.typeBuilder.setName(name);
            return this;
        }
        public LinkedBindingBuilder<T> uniqueName() {
            this.typeBuilder.setName(UUID.randomUUID().toString());
            return this;
        }
        public MetaDataBindingBuilder<T> toScope(Scope scope) {
            return this.toScope(new InstanceProvider<Scope>(scope));
        }
        public MetaDataBindingBuilder<T> toInstance(T instance) {
            return this.toProvider(new InstanceProvider<T>(instance));
        }
        public InjectPropertyBindingBuilder<T> to(Class<? extends T> implementation) {
            this.typeBuilder.setSourceType(implementation);
            return this;
        }
        public InjectConstructorBindingBuilder<T> toConstructor(Constructor<? extends T> constructor) {
            Class<? extends T> targetType = constructor.getDeclaringClass();
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
        public MetaDataBindingBuilder<T> toScope(Provider<Scope> scope) {
            this.typeBuilder.setScope(scope);
            return this;
        }
        public LifeBindingBuilder<T> toProvider(Provider<T> provider) {
            if (provider != null)
                this.typeBuilder.setProvider(provider);
            return this;
        }
        //
        public InjectPropertyBindingBuilder<T> injectValue(String property, Object value) {
            return this.inject(property, new InstanceProvider<Object>(value));
        }
        public InjectPropertyBindingBuilder<T> inject(String property, RegisterInfo<?> valueInfo) {
            this.typeBuilder.addInject(property, valueInfo);
            return this;
        }
        public InjectPropertyBindingBuilder<T> inject(String property, Provider<?> valueProvider) {
            this.typeBuilder.addInject(property, valueProvider);
            return this;
        }
        public InjectConstructorBindingBuilder<T> injectValue(int index, Object value) {
            return this.inject(index, new InstanceProvider<Object>(value));
        }
        public InjectConstructorBindingBuilder<T> inject(int index, RegisterInfo<?> valueInfo) {
            if (index >= this.initParams.length)
                throw new IndexOutOfBoundsException("index out of bounds.");
            this.typeBuilder.setInitParam(index, this.initParams[index], valueInfo);
            return this;
        }
        public InjectConstructorBindingBuilder<T> inject(int index, Provider<?> valueProvider) {
            if (index >= this.initParams.length)
                throw new IndexOutOfBoundsException("index out of bounds.");
            this.typeBuilder.setInitParam(index, this.initParams[index], valueProvider);
            return this;
        }
        public RegisterInfo<T> toInfo() {
            return this.typeBuilder;
        }
    }
    //
}