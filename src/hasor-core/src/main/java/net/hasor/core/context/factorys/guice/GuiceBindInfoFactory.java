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
package net.hasor.core.context.factorys.guice;
import java.lang.reflect.Method;
import java.util.Iterator;
import net.hasor.core.ApiBinder;
import net.hasor.core.ApiBinder.Matcher;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import net.hasor.core.context.AbstractAppContext;
import net.hasor.core.context.factorys.AbstractBindInfoFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
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
    protected <T> T newInstance(final BindInfo<T> oriType) {
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
    public <T> T getDefaultInstance(final Class<T> oriType) {
        if (this.guiceInjector == null) {
            return super.getDefaultInstance(oriType);
        }
        return this.guiceInjector.getInstance(oriType);
    }
    public void registerAop(Matcher<Class<?>> matcherClass, Matcher<Method> matcherMethod, net.hasor.core.MethodInterceptor interceptor) {
        // TODO Auto-generated method stub
    }
    //
    /*-------------------------------------------------------------------------------add to Guice*/
    /**GuiceRegisterFactory的初始化过程，负责将{@link com.google.inject.Injector}类型绑定到  Hasor 中。
     * 在程序运行时可以通过<code>AppContext.getInstance(Injector.class)</code>方法获取原生的 Guice 接口。
     * @see net.hasor.core.AppContext#getInstance(Class) */
    public void doInitialize(ApiBinder apiBinder) {
        super.doInitialize(apiBinder);
        apiBinder.bindType(Injector.class).toProvider(new Provider<Injector>() {
            public Injector get() {
                return guiceInjector;
            }
        });
    }
    //
    //将Bean信息绑定到Guice上
    public void doInitializeCompleted(final AbstractAppContext appContext) {
        //1.系统自检
        super.doInitializeCompleted(appContext);
        //2.执行绑定
        this.guiceInjector = this.createInjector(new com.google.inject.Module() {
            public void configure(final Binder binder) {
                Iterator<AbstractRegisterInfoAdapter<?>> registerIterator = GuiceBindInfoFactory.this.getRegisterIterator();
                while (registerIterator.hasNext()) {
                    AbstractRegisterInfoAdapter<Object> register = (AbstractRegisterInfoAdapter<Object>) registerIterator.next();
                    //1.处理绑定 
                    configRegister(register, binder);
                    //2.处理Aop 
                    configAopRegister(register, binder);
                }
            }
        });
    }
    //
    //处理Aop配置
    private void configAopRegister(final AbstractRegisterInfoAdapter<Object> register, final Binder binder) {
        if (register.getBindType().isAssignableFrom(AopMatcherMethodInterceptorData.class) == false)
            return;
        //
        final AopMatcherMethodInterceptor amr = (AopMatcherMethodInterceptor) register.getProvider().get();
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
    //处理一般配置
    private void configRegister(final AbstractRegisterInfoAdapter<Object> register, final Binder binder) {
        binder.bind(RegisterInfo.class).annotatedWith(UniqueAnnotations.create()).toInstance(register);
        //1.绑定类型
        AnnotatedBindingBuilder<Object> annoBinding = binder.bind(register.getBindType());
        LinkedBindingBuilder<Object> linkedBinding = annoBinding;
        ScopedBindingBuilder scopeBinding = annoBinding;
        //2.绑定名称
        boolean haveName = false;
        String name = register.getBindName();
        if (!StringUtils.isBlank(name)) {
            linkedBinding = annoBinding.annotatedWith(Names.named(name));
            haveName = true;
        }
        //3.绑定实现
        if (register.getCustomerProvider() != null) {
            scopeBinding = linkedBinding.toProvider(new ToGuiceProvider<Object>(register.getProvider()));
        } else if (register.getSourceType() != null) {
            scopeBinding = linkedBinding.to(register.getSourceType());
        } else {
            if (haveName == true) {
                /*有了BindName一定要，有impl绑定，所以只能自己绑定自己*/
                scopeBinding = linkedBinding.to(register.getBindType());
            }
        }
        //3.处理单例
        if (register.isSingleton()) {
            scopeBinding.asEagerSingleton();
            return;/*第五步不进行处理*/
        }
        //4.绑定作用域
        Provider<Scope> scopeProvider = register.getScopeProvider();
        if (scopeProvider != null) {
            Scope scope = scopeProvider.get();
            if (scope != null) {
                scopeBinding.in(new GuiceScope(scope));
            }
        }
        //
    }
}
//
/*---------------------------------------------------------------------------------------Util*/
/**Hasor Aop 到 Aop 联盟的桥*/
class MethodInterceptorAdapter implements MethodInterceptor {
    private AopMatcherMethodInterceptor aopInterceptor = null;
    public MethodInterceptorAdapter(final AopMatcherMethodInterceptor aopInterceptor) {
        this.aopInterceptor = aopInterceptor;
    }
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        return this.aopInterceptor.invoke(new net.hasor.core.MethodInvocation() {
            public Object proceed() throws Throwable {
                return invocation.proceed();
            }
            public Object getThis() {
                return invocation.getThis();
            }
            public Method getMethod() {
                return invocation.getMethod();
            }
            public Object[] getArguments() {
                return invocation.getArguments();
            }
        });
    }
}
/**负责net.hasor.core.Scope与com.google.inject.Scope的对接转换*/
class GuiceScope implements com.google.inject.Scope {
    private Scope scope = null;
    public GuiceScope(final Scope scope) {
        this.scope = scope;
    }
    public String toString() {
        return this.scope.toString();
    };
    public <T> com.google.inject.Provider<T> scope(final Key<T> key, final com.google.inject.Provider<T> unscoped) {
        Provider<T> returnData = this.scope.scope(key, new ToHasorProvider<T>(unscoped));
        if (returnData instanceof com.google.inject.Provider) {
            return (com.google.inject.Provider<T>) returnData;
        } else if (returnData instanceof ToHasorProvider) {
            return ((ToHasorProvider) returnData).getProvider();
        } else {
            return new ToGuiceProvider(returnData);
        }
    }
}
/** 负责com.google.inject.Provider到net.hasor.core.Provider的对接转换*/
class ToHasorProvider<T> implements net.hasor.core.Provider<T> {
    private com.google.inject.Provider<T> provider;
    public ToHasorProvider(final com.google.inject.Provider<T> provider) {
        this.provider = provider;
    }
    public T get() {
        return this.provider.get();
    }
    public com.google.inject.Provider<T> getProvider() {
        return this.provider;
    }
}
class ToGuiceProvider<T> implements com.google.inject.Provider<T> {
    private Provider<T> provider;
    public ToGuiceProvider(final Provider<T> provider) {
        this.provider = provider;
    }
    public T get() {
        return this.provider.get();
    }
    public Provider<T> getProvider() {
        return this.provider;
    }
}