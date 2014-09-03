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
package net.hasor.core.factorys.guice;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import net.hasor.core.BindInfo;
import net.hasor.core.InjectMembers;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import net.hasor.core.factorys.AbstractBindInfoFactory;
import net.hasor.core.factorys.AopMatcherMethodInterceptor;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import org.more.util.StringUtils;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.name.Names;
/**
 * 
 * @version : 2014年7月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class GuiceBindInfoFactory extends AbstractBindInfoFactory {
    private Injector guiceInjector = null;
    //
    public Injector getGuice() {
        return this.guiceInjector;
    }
    /**创建Guice*/
    protected Injector createInjector(final com.google.inject.Module rootModule) {
        return Guice.createInjector(rootModule);
    }
    /**重写newInstance，使用Guice创建对象。*/
    public <T> T getInstance(final BindInfo<T> oriType) {
        if (oriType == null) {
            return null;
        }
        if (this.guiceInjector == null) {
            throw new IllegalStateException("Guice is not ready.");
        }
        //
        if (StringUtils.isBlank(oriType.getBindName()) == false) {
            Key<T> key = Key.get(oriType.getBindType(), Names.named(oriType.getBindName()));
            return this.guiceInjector.getInstance(key);
        } else {
            return this.guiceInjector.getInstance(oriType.getBindType());
        }
    }
    /**重写getDefaultInstance，使用Guice创建对象。*/
    public <T> T getDefaultInstance(final Class<T> oriType) {
        if (this.guiceInjector == null) {
            return super.getDefaultInstance(oriType);
        }
        if (Injector.class == oriType) {
            return (T) this.guiceInjector;
        }
        return this.guiceInjector.getInstance(oriType);
    }
    //
    /*-------------------------------------------------------------------------------add to Guice*/
    /** 重写 doInitializeCompleted，使doInitializeCompleted的执行过程处于Guice创建过程中。<p>
     * 目的是将{@link #doInitializeCompleted(Object)} 方法的 context 传入参数改变为Guice 的 {@link com.google.inject.Binder}*/
    public void doInitializeCompleted(final Object context) {
        this.guiceInjector = this.createInjector(new com.google.inject.Module() {
            public void configure(Binder binder) {
                GuiceBindInfoFactory.super.doInitializeCompleted(binder);
            }
        });
    }
    //
    protected void configBindInfo(AbstractBindInfoProviderAdapter<Object> bindInfo, Object context) {
        Binder binder = (Binder) context;
        configRegister(bindInfo, binder);
        if (bindInfo.getBindType().isAssignableFrom(AopMatcherMethodInterceptor.class) == true) {
            configAopRegister(bindInfo, binder);
        }
    }
    //
    /**处理Aop的注册*/
    private void configAopRegister(final AbstractBindInfoProviderAdapter<Object> bindInfo, final Binder binder) {
        final AopMatcherMethodInterceptor amr = (AopMatcherMethodInterceptor) bindInfo.getCustomerProvider().get();
        binder.bindInterceptor(new AbstractMatcher<Class<?>>() {
            public boolean matches(final Class<?> targetClass) {
                return amr.matcher(targetClass);
            }
        }, new AbstractMatcher<Method>() {
            public boolean matches(final Method targetMethod) {
                return amr.matcher(targetMethod);
            }
        }, new MethodInterceptorAdapter(amr));
    }
    //
    /**处理一般配置*/
    private void configRegister(final AbstractBindInfoProviderAdapter<Object> bindInfo, final Binder binder) {
        //0.内置绑定
        String bindID = bindInfo.getBindID();
        Annotation bindAnnotation = (bindID != null) ? Names.named(bindInfo.getBindID()) : UniqueAnnotations.create();
        binder.bind(BindInfo.class).annotatedWith(bindAnnotation).toInstance(bindInfo);
        //1.绑定类型
        AnnotatedBindingBuilder<Object> annoBinding = binder.bind(bindInfo.getBindType());
        LinkedBindingBuilder<Object> linkedBinding = annoBinding;
        ScopedBindingBuilder scopeBinding = annoBinding;
        //2.绑定名称
        String name = bindInfo.getBindName();
        if (!StringUtils.isBlank(name)) {
            linkedBinding = annoBinding.annotatedWith(Names.named(name));
        }
        //3.绑定实现
        if (bindInfo.getCustomerProvider() != null) {
            scopeBinding = linkedBinding.toProvider(new ToGuiceProviderAdapter<Object>(bindInfo.getCustomerProvider()));
        } else {
            scopeBinding = linkedBinding.toProvider(this.defProvider(bindInfo));
        }
        //3.处理单例
        if (bindInfo.isSingleton()) {
            scopeBinding.asEagerSingleton();
            return;/*第五步不进行处理*/
        }
        //4.绑定作用域
        Provider<Scope> scopeProvider = bindInfo.getScopeProvider();
        if (scopeProvider != null) {
            Scope scope = scopeProvider.get();
            if (scope != null) {
                scopeBinding.in(new GuiceScopeAdapter(scope));
            }
        }
        //
    }
    //
    private com.google.inject.Provider<Object> defProvider(AbstractBindInfoProviderAdapter<Object> register) {
        Class<?> finalClass = register.getBindType();
        if (register.getSourceType() != null) {
            finalClass = register.getSourceType();
        }
        return new NewInstanceProvider(this, finalClass);
    }
}
//
/*---------------------------------------------------------------------------------------Util*/
class NewInstanceProvider implements com.google.inject.Provider<Object> {
    private Class<?>             targetType = null;
    private GuiceBindInfoFactory factory    = null;
    //
    public NewInstanceProvider(GuiceBindInfoFactory factory, Class<?> targetType) {
        this.targetType = targetType;
        this.factory = factory;
    }
    public Object get() {
        try {
            Object target = this.targetType.newInstance();
            this.factory.getGuice().injectMembers(target);
            //
            if (target instanceof InjectMembers) {
                ((InjectMembers) target).doInject(this.factory.getAppContext());
            }
            return target;
        } catch (Exception e) {
            e.printStackTrace();
            throw null;
        }
    }
}