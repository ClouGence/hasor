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
package net.hasor.core.context.factorys.spring;
import java.lang.reflect.Method;
import java.util.Iterator;
import net.hasor.core.Provider;
import net.hasor.core.RegisterInfo;
import net.hasor.core.Scope;
import net.hasor.core.binder.aop.AopMatcherMethodInterceptor;
import net.hasor.core.context.AbstractAppContext;
import net.hasor.core.context.adapter.RegisterInfoAdapter;
import net.hasor.core.context.factorys.AbstractRegisterFactory;
import net.hasor.core.context.factorys.AbstractRegisterInfoAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.matcher.AbstractMatcher;
/**
 * 
 * @version : 2014年7月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class SpringRegisterFactory extends AbstractRegisterFactory {
    private AbstractApplicationContext spring = null;
    //
    public ApplicationContext getSpring() {
        return this.spring;
    }
    /**创建Guice*/
    protected AbstractApplicationContext createSpring() {
        return new ClassPathXmlApplicationContext();
    }
    protected <T> AbstractRegisterInfoAdapter<T> createRegisterInfoAdapter(Class<T> bindingType) {
        SpringRegisterInfoAdapter<T> adapter = new SpringRegisterInfoAdapter<T>();
        adapter.setBindType(bindingType);
        return adapter;
    }
    public <T> T getInstance(RegisterInfo<T> oriType) {
        if (oriType == null)
            return null;
        if (this.guiceInjector == null)
            throw new IllegalStateException("Guice is not ready.");
        //
        if (oriType instanceof SpringRegisterInfoAdapter) {
            Key<T> key = ((SpringRegisterInfoAdapter<T>) oriType).getKey();
            return this.guiceInjector.getInstance(key);
        }
        return this.guiceInjector.getInstance(oriType.getBindType());
    }
    //
    /*-------------------------------------------------------------------------------add to Guice*/
    public void doInitializeCompleted(AbstractAppContext appContext) {
        //1.系统自检
        super.doInitializeCompleted(appContext);
        //2.执行绑定
        this.guiceInjector = this.createInjector(new Module() {
            public void configure(Binder binder) {
                Iterator<RegisterInfoAdapter<?>> registerIterator = getRegisterIterator();
                while (registerIterator.hasNext()) {
                    SpringRegisterInfoAdapter<Object> register = (SpringRegisterInfoAdapter<Object>) registerIterator.next();
                    //1.处理绑定
                    configRegister(register, binder);
                    //2.处理Aop
                    if (register.getBindType().isAssignableFrom(AopMatcherMethodInterceptor.class)) {
                        final AopMatcherMethodInterceptor amr = (AopMatcherMethodInterceptor) register.getProvider().get();
                        binder.bindInterceptor(new AbstractMatcher<Class<?>>() {
                            public boolean matches(Class<?> targetClass) {
                                return amr.matcher(targetClass);
                            }
                        }, new AbstractMatcher<Method>() {
                            public boolean matches(Method targetMethod) {
                                return amr.matcher(targetMethod);
                            }
                        }, amr);
                    }
                    //                    GuiceTypeRegister<Object> register = (GuiceTypeRegister<Object>) tempItem;
                }
            }
        });
    }
    private void configRegister(SpringRegisterInfoAdapter<Object> register, Binder binder) {
        binder.bind(RegisterInfo.class).annotatedWith(UniqueAnnotations.create()).toInstance(register);
        //1.绑定类型
        LinkedBindingBuilder<Object> linkedBinding = binder.bind(register.getKey());
        ScopedBindingBuilder scopeBinding = linkedBinding;
        //2.绑定实现
        if (register.getCustomerProvider() != null)
            scopeBinding = linkedBinding.toProvider(new ToGuiceProvider<Object>(register.getCustomerProvider()));
        else if (register.getSourceType() != null)
            scopeBinding = linkedBinding.to(register.getSourceType());
        //3.处理单例
        if (register.isSingleton()) {
            scopeBinding.asEagerSingleton();
            return;/*第五步不进行处理*/
        }
        //4.绑定作用域
        Provider<Scope> scopeProvider = register.getScopeProvider();
        if (scopeProvider != null) {
            Scope scope = scopeProvider.get();
            if (scope != null)
                scopeBinding.in(new GuiceScope(scope));
        }
        //
    }
}
//
/*---------------------------------------------------------------------------------------Util*/
/**负责net.hasor.core.Scope与com.google.inject.Scope的对接转换*/
class GuiceScope implements com.google.inject.Scope {
    private Scope scope = null;
    public GuiceScope(Scope scope) {
        this.scope = scope;
    }
    public String toString() {
        return this.scope.toString();
    };
    public <T> com.google.inject.Provider<T> scope(Key<T> key, com.google.inject.Provider<T> unscoped) {
        Provider<T> returnData = this.scope.scope(key, new ToHasorProvider<T>(unscoped));
        if (returnData instanceof com.google.inject.Provider)
            return (com.google.inject.Provider<T>) returnData;
        else if (returnData instanceof ToHasorProvider)
            return ((ToHasorProvider) returnData).getProvider();
        else
            return new ToGuiceProvider(returnData);
    }
}
/** 负责com.google.inject.Provider到net.hasor.core.Provider的对接转换*/
class ToHasorProvider<T> implements net.hasor.core.Provider<T> {
    private com.google.inject.Provider<T> provider;
    public ToHasorProvider(com.google.inject.Provider<T> provider) {
        this.provider = provider;
    }
    public T get() {
        return this.provider.get();
    }
    public com.google.inject.Provider<T> getProvider() {
        return provider;
    }
}
class ToGuiceProvider<T> implements com.google.inject.Provider<T> {
    private Provider<T> provider;
    public ToGuiceProvider(Provider<T> provider) {
        this.provider = provider;
    }
    public T get() {
        return this.provider.get();
    }
    public Provider<T> getProvider() {
        return provider;
    }
}