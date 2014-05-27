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
package net.hasor.core.context.rm.guice;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.hasor.core.Environment;
import net.hasor.core.Provider;
import net.hasor.core.RegisterInfo;
import net.hasor.core.Scope;
import net.hasor.core.binder.AopConst;
import net.hasor.core.binder.AopMatcherRegister;
import net.hasor.core.binder.TypeRegister;
import net.hasor.core.binder.register.AbstractTypeRegister;
import net.hasor.core.binder.register.FreeTypeRegister;
import net.hasor.core.builder.BeanBuilder;
import net.hasor.core.context.AbstractAppContext;
import net.hasor.core.context.AbstractRegisterManager;
import net.hasor.core.context.RegisterManager;
import net.hasor.core.context.RegisterManagerCreater;
import org.more.util.Iterators;
import org.more.util.Iterators.Converter;
import org.more.util.StringUtils;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.name.Names;
/**
 * 
 * @version : 2014-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class GuiceRegisterManagerCreater implements RegisterManagerCreater {
    public RegisterManager create(Environment env) {
        return new GuiceRegisterManager();
    }
}
/*RegisterManager接口实现*/
class GuiceRegisterManager extends AbstractRegisterManager {
    //
    /*-----------------------------------------------------------------Collect GuiceTypeRegisters*/
    protected <T> TypeRegister<T> createRegisterType(Class<T> type) {
        return new GuiceTypeRegister<T>(type);
    }
    //
    /*-------------------------------------------------------------------------------add to Guice*/
    public void doInitializeCompleted(AbstractAppContext appContext) {
        super.doInitializeCompleted(appContext);
        Injector guiceInjector = Guice.createInjector(new Module() {
            public void configure(Binder binder) {
                Iterator<TypeRegister<?>> regTypeRegister = registerIterator();
                while (regTypeRegister.hasNext()) {
                    GuiceTypeRegister<Object> register = (GuiceTypeRegister<Object>) regTypeRegister.next();
                    //1.处理绑定
                    configRegister(register, binder);
                    //2.处理Aop
                    if (register.getType().isAssignableFrom(AopMatcherRegister.class)) {
                        if (register.getMetaData().containsKey(AopConst.AopAssembly)) {
                            final AopMatcherRegister amr = (AopMatcherRegister) register.getProvider().get();
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
                    }
                    //                    GuiceTypeRegister<Object> register = (GuiceTypeRegister<Object>) tempItem;
                }
            }
        });
        this.guiceBeanBuilder = new GuiceBeanBuilder(guiceInjector);
    }
    private void configRegister(GuiceTypeRegister<Object> register, Binder binder) {
        binder.bind(RegisterInfo.class).annotatedWith(UniqueAnnotations.create()).toInstance(register);
        //1.绑定类型
        AnnotatedBindingBuilder<Object> annoBinding = binder.bind(register.getType());
        LinkedBindingBuilder<Object> linkedBinding = annoBinding;
        ScopedBindingBuilder scopeBinding = annoBinding;
        //2.绑定名称
        boolean haveName = false;
        String name = register.getName();
        if (!StringUtils.isBlank(name)) {
            linkedBinding = annoBinding.annotatedWith(Names.named(name));
            haveName = true;
        }
        //3.绑定实现
        if (register.getProvider() != null)
            scopeBinding = linkedBinding.toProvider(new ToGuiceProvider<Object>(register.getProvider()));
        else if (register.getImplConstructor() != null)
            scopeBinding = linkedBinding.toConstructor(register.getImplConstructor());
        else if (register.getImplType() != null)
            scopeBinding = linkedBinding.to(register.getImplType());
        else {
            if (haveName == true)
                scopeBinding = linkedBinding.to(register.getType());/*自己绑定自己*/
        }
        //4.处理单例
        if (register.isSingleton()) {
            scopeBinding.asEagerSingleton();
            return;/*第五步不进行处理*/
        }
        //5.绑定作用域
        Scope scope = register.getScope();
        if (scope != null)
            scopeBinding.in(new GuiceScope(scope));
        //
    }
    private GuiceBeanBuilder guiceBeanBuilder = null;
    public BeanBuilder getBeanBuilder() {
        return this.guiceBeanBuilder;
    }
}
/**用来创建Bean、查找Bean*/
class GuiceBeanBuilder implements BeanBuilder {
    private Injector injector;
    //
    public GuiceBeanBuilder(Injector injector) {
        this.injector = injector;
    }
    //
    public <T> T getInstance(RegisterInfo<T> oriType) {
        if (oriType == null)
            return null;
        //
        if (oriType instanceof GuiceTypeRegister) {
            Key<T> key = ((GuiceTypeRegister<T>) oriType).getKey();
            return this.injector.getInstance(key);
        } else if (oriType instanceof FreeTypeRegister) {
            Class<T> createType = ((FreeTypeRegister<T>) oriType).getType();
            return this.injector.getInstance(createType);
        }
        throw new UnsupportedOperationException(String.format("%s RegisterInfo.", oriType.getClass()));
    }
    public Iterator<RegisterInfo<?>> getRegisterIterator() {
        //1.通过Guice的方式查找已经绑定的所有RegisterInfo。
        TypeLiteral<RegisterInfo> BindingType_DEFS = TypeLiteral.get(RegisterInfo.class);
        List<Binding<RegisterInfo>> bindList = this.injector.findBindingsByType(BindingType_DEFS);
        final Iterator<Binding<RegisterInfo>> bindIterator = bindList.iterator();
        //2.将Binding类型迭代器转换为RegisterInfo类型迭代器。
        return Iterators.converIterator(bindIterator, new Converter<Binding<RegisterInfo>, RegisterInfo<?>>() {
            public RegisterInfo<?> converter(Binding<RegisterInfo> target) {
                return target.getProvider().get();
            }
        });
    }
    public <T> Iterator<RegisterInfo<T>> getRegisterIterator(Class<T> bindingType) {
        final Iterator<RegisterInfo<?>> bindIterator = this.getRegisterIterator();
        List<RegisterInfo<T>> bindList = new ArrayList<RegisterInfo<T>>();
        while (bindIterator.hasNext()) {
            RegisterInfo<?> info = bindIterator.next();
            if (info.getType() == bindingType)
                bindList.add((RegisterInfo<T>) info);
        }
        return bindList.iterator();
    }
}
//
/*---------------------------------------------------------------------------------------Util*/
class GuiceTypeRegister<T> extends AbstractTypeRegister<T> {
    public GuiceTypeRegister(Class<T> type) {
        super(type);
    }
    public Key<T> getKey() {
        if (getName() == null)
            return Key.get(this.getType());
        return Key.get(this.getType(), Names.named(getName()));
    }
}
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