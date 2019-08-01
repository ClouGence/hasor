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
package net.hasor.core.container;
import net.hasor.core.EventListener;
import net.hasor.core.*;
import net.hasor.core.aop.AopClassConfig;
import net.hasor.core.aop.AsmTools;
import net.hasor.core.binder.BindInfoBuilderFactory;
import net.hasor.core.info.AopBindInfoAdapter;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.provider.SingleProvider;
import net.hasor.core.scope.PrototypeScope;
import net.hasor.core.spi.AppContextAware;
import net.hasor.core.spi.BindInfoAware;
import net.hasor.core.spi.CreaterProvisionListener;
import net.hasor.core.spi.InjectMembers;
import net.hasor.utils.ArrayUtils;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.convert.ConverterUtils;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.hasor.core.container.ContainerUtils.*;

/**
 * 负责创建 Bean
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2015年11月25日
 */
public class BeanContainer extends AbstractContainer implements BindInfoBuilderFactory {
    private Environment                                 environment        = null;
    private SpiCallerContainer                          spiCallerContainer = null;
    private BindInfoContainer                           bindInfoContainer  = null;
    private ScopContainer                               scopContainer      = null;
    private ConcurrentHashMap<Class<?>, AopClassConfig> classEngineMap     = null;

    public BeanContainer(Environment environment) {
        this.environment = Objects.requireNonNull(environment, "need Environment.");
        this.spiCallerContainer = new SpiCallerContainer();
        this.bindInfoContainer = new BindInfoContainer(spiCallerContainer);
        this.scopContainer = new ScopContainer(spiCallerContainer);
        this.classEngineMap = new ConcurrentHashMap<>();
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public BindInfoContainer getBindInfoContainer() {
        return this.bindInfoContainer;
    }

    @Override
    public SpiCallerContainer getSpiContainer() {
        return this.spiCallerContainer;
    }

    @Override
    public ScopContainer getScopContainer() {
        return this.scopContainer;
    }

    /*-------------------------------------------------------------------------------------------*/

    /**
     * 仅通过 targetType 类型创建Bean（不参考BindInfoContainer）
     */
    public <T> Supplier<? extends T> providerOnlyType(Class<T> targetType, AppContext appContext, Object[] params) {
        if (targetType == null) {
            return null;
        }
        //
        // .targetType也许只是一个被标记了 ImplBy 注解的类型。因此需要找到真正需要创建的那个类型。
        Class<T> implClass = findImplClass(targetType);
        //
        // .（构造方法）确定创建 implClass 类型对象时使用的构造方法，使用 Supplier 封装。
        Supplier<Executable> constructorSupplier = new SingleProvider<>(() -> {
            Constructor<?>[] constructors = Arrays.stream(implClass.getConstructors()).filter(constructor -> {
                return constructor.getParameterCount() == 0 || isInjectConstructor(constructor);
            }).sorted((o1, o2) -> {
                int a = o1.getParameterCount() == 0 ? -1 : 1;
                int b = o2.getParameterCount() == 0 ? -1 : 1;
                return Integer.compare(a, b);
            }).toArray(Constructor[]::new);
            //
            // .查找构造方法
            Constructor<?> constructor = null;
            if (constructors.length >= 1) {
                constructor = constructors[0];
            } else {
                throw new IllegalArgumentException("No default constructor found.");
            }
            return constructor;
        });
        //
        // .（构造参数）构造方法用到的参数
        Supplier<Object[]> parameterSupplier = parameterSupplier(constructorSupplier, appContext, params, true);
        //
        // .创建对象
        return (Supplier<T>) () -> createObject(implClass, constructorSupplier, parameterSupplier, null, appContext);
    }

    /**
     * 仅通过 targetConstructor 类型创建Bean（不参考BindInfoContainer）
     */
    public <T> Supplier<? extends T> providerOnlyConstructor(Constructor<T> targetConstructor, AppContext appContext, Object[] params) {
        if (targetConstructor == null) {
            return null;
        }
        // .（构造方法）
        Supplier<Executable> constructorSupplier = InstanceProvider.of(targetConstructor);
        //
        // .（构造参数）构造方法用到的参数
        Supplier<Object[]> parameterSupplier = parameterSupplier(constructorSupplier, appContext, params, true);
        //
        // .创建对象
        return (Supplier<T>) () -> createObject(targetConstructor.getDeclaringClass(), constructorSupplier, parameterSupplier, null, appContext);
    }

    /**
     * 通过 BindInfo 类型创建Bean
     */
    public <T> Supplier<? extends T> providerOnlyBindInfo(BindInfo<T> bindInfo, AppContext appContext) {
        if (bindInfo == null) {
            return null;
        }
        DefaultBindInfoProviderAdapter<?> adapter = (DefaultBindInfoProviderAdapter) bindInfo;
        //
        // .（构造方法）确定创建 BindInfo 使用的构造方法，使用 Supplier 封装。
        Supplier<Executable> constructorSupplier = new SingleProvider<>(() -> {
            //
            // .如果指定了 SourceType 那么使用 SourceType 作为 targetType
            Class<?> targetType = adapter.getSourceType() != null ? adapter.getSourceType() : adapter.getBindType();
            //
            // .targetType也许只是一个被标记了 ImplBy 注解的类型。因此需要找到真正需要创建的那个类型。
            Class<T> implClass = findImplClass(targetType);
            //
            Constructor<?> constructor = adapter.getConstructor(implClass, appContext);
            return Objects.requireNonNull(constructor, "constructor is not found.");
        });
        //
        // .（构造参数）构造方法用到的参数
        Supplier<Object[]> parameterSupplier = () -> {
            return Arrays.stream(adapter.getConstructorParams(appContext))  //
                    .map((Function<Supplier<?>, Object>) Supplier::get)     //
                    .toArray(Object[]::new);
        };
        //
        // .创建对象
        return (Supplier<T>) () -> createObject(bindInfo.getBindType(), constructorSupplier, parameterSupplier, bindInfo, appContext);
    }

    /**
     * 仅通过 Annotation 来创建Bean。targetType 作为参考类型。
     */
    public <T> Supplier<? extends T> providerOnlyAnnotation(Class<T> targetType, Annotation anno, AppContext appContext) {
        if (anno == null) {
            return null;
        }
        if (anno instanceof InjectSettings) {
            return () -> (T) injSettings(appContext, (InjectSettings) anno, targetType);
        }
        if (anno instanceof ID) {
            return () -> appContext.getInstance(((ID) anno).value());
        }
        if (anno instanceof javax.inject.Named) {
            BindInfo<T> bindInfo = getBindInfoContainer().findBindInfo(((Named) anno).value(), targetType);
            if (bindInfo != null) {
                return providerOnlyBindInfo(bindInfo, appContext);
            } else {
                return providerOnlyType(targetType, appContext, null);
            }
        }
        throw new UnsupportedOperationException(anno.annotationType() + " Annotation is not support.");
    }

    /**
     * 创建一个构造方法对应的参数Supplier
     */
    private Supplier<Object[]> parameterSupplier(Supplier<Executable> executableSupplier, AppContext appContext, Object[] params, boolean alwaysInject) {
        return new SingleProvider<>(() -> {
            // .基础数据
            Executable constructor = executableSupplier.get();                      // 方法
            Class<?>[] parameterTypes = constructor.getParameterTypes();            // 方法参数
            Annotation[][] parameterAnnos = constructor.getParameterAnnotations();  // 方法参数上的注解
            //
            Object[] paramObjects = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Annotation injectInfo = findInject(false, parameterAnnos[i]);
                if (injectInfo != null) {
                    paramObjects[i] = providerOnlyAnnotation(parameterTypes[i], injectInfo, appContext).get();
                    continue;
                }
                //
                if (params != null && params.length > i) {
                    paramObjects[i] = params[i];
                    continue;
                }
                //
                BindInfo<?> bindInfo = getBindInfoContainer().findBindInfo("", parameterTypes[i]);
                if (bindInfo != null) {
                    paramObjects[i] = providerOnlyBindInfo(bindInfo, appContext).get();
                } else {
                    if (ClassUtils.wrapperToPrimitive(parameterTypes[i]) != null || parameterTypes[i].isPrimitive()) {
                        paramObjects[i] = BeanUtils.getDefaultValue(parameterTypes[i]);
                    } else {
                        paramObjects[i] = providerOnlyType(parameterTypes[i], appContext, null).get();
                    }
                }
            }
            return paramObjects;
        });
    }

    /**
     * 创建Bean {@link BindInfo}创建Bean。
     *
     * @param targetType           表示目标类型
     * @param referConstructor     表示使用的构造方法
     * @param constructorParameter 构造方法所使用的参数
     * @param bindInfo             可能为空，表示参考的 BindInfo
     * @param appContext           容器
     */
    private <T> T createObject(Class<T> targetType, Supplier<Executable> referConstructor, Supplier<Object[]> constructorParameter, BindInfo<T> bindInfo, AppContext appContext) {
        // .check基本类型
        if (targetType.isPrimitive()) {
            return (T) BeanUtils.getDefaultValue(targetType);
        }
        if (targetType.isArray()) {
            Class<?> comType = targetType.getComponentType();
            return (T) Array.newInstance(comType, 0);
        }
        if (targetType.isInterface() || targetType.isEnum()) {
            return null;
        }
        if (Modifier.isAbstract(targetType.getModifiers())) {
            return null;// Integer.TYPE 判断结果为 true & targetType.isArray() 情况下也为 true，因此要放在后面
        }
        // .作用域
        Supplier<Scope>[] scopeProvider = null;
        if (bindInfo != null) {
            scopeProvider = this.scopContainer.collectScope(bindInfo);
        }
        if (ArrayUtils.isEmpty(scopeProvider)) {
            scopeProvider = this.scopContainer.collectScope(targetType);
        }
        Scope[] scope = null;
        if (ArrayUtils.isNotEmpty(scopeProvider)) {
            scope = Arrays.stream(scopeProvider).map(supplier -> {
                return Objects.requireNonNull(supplier.get(), "scope is null.");
            }).toArray(Scope[]::new);
        }
        //
        DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
        Supplier<T> targetSupplier = defBinder != null ? (Supplier<T>) defBinder.getCustomerProvider() : null;
        if (targetSupplier == null) {
            targetSupplier = () -> {
                //
                // .Aop 代理
                Class<T> proxyType = proxyType(targetType, appContext);
                //
                // .重定向构造方法
                Constructor<T> tConstructor = (Constructor<T>) referConstructor.get();
                try {
                    tConstructor = proxyType.getConstructor(tConstructor.getParameterTypes());
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException(e);
                }
                // .创建对象
                T targetObject = null;
                try {
                    if (tConstructor.getParameterCount() > 0) {
                        targetObject = tConstructor.newInstance(constructorParameter.get());
                    } else {
                        targetObject = tConstructor.newInstance(ArrayUtils.EMPTY_OBJECT_ARRAY);
                    }
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException(e.getTargetException());
                } catch (Exception e) {
                    throw ExceptionUtils.toRuntimeException(e, IllegalStateException::new);
                }
                //
                // .执行依赖注入
                justInject(targetObject, targetType, bindInfo, appContext);
                //
                // .执行生命周期
                doLife(targetObject, bindInfo, appContext);
                //
                T finalTargetObject = targetObject;
                spiCallerContainer.callSpi(CreaterProvisionListener.class, listener -> {
                    listener.beanCreated(finalTargetObject, bindInfo);
                });
                return targetObject;
            };
        }
        //
        // .创建对象，如果存在作用域那么就从作用域中获取对象
        if (ArrayUtils.isEmpty(scope)) {
            return targetSupplier.get();
        } else {
            String key = (bindInfo != null) ? bindInfo.getBindID() : targetType.getName();
            return PrototypeScope.SINGLETON.chainScope(key, scope, targetSupplier).get();
        }
    }

    /**
     * 生成动态代理类型
     */
    private <T> Class<T> proxyType(Class<T> targetType, AppContext appContext) {
        // .@AopIgnore排除在外
        ClassLoader rootLoader = Objects.requireNonNull(appContext.getClassLoader());
        if (testAopIgnore(targetType, rootLoader)) {
            return targetType;
        }
        //
        // .准备Aop
        List<BindInfo<AopBindInfoAdapter>> aopBindList = this.bindInfoContainer.findBindInfoList(AopBindInfoAdapter.class);
        List<AopBindInfoAdapter> aopList = aopBindList.stream()//
                .map((Function<BindInfo<AopBindInfoAdapter>, AopBindInfoAdapter>) info -> {
                    Supplier<? extends AopBindInfoAdapter> bindInfo = providerOnlyBindInfo(info, appContext);
                    return bindInfo.get();
                }).collect(Collectors.toList());
        //
        // .动态代理，需要满足三个条件（1.类型必须支持Aop、2.没有被@AopIgnore排除在外、3.具有至少一个有效的拦截器）
        Class<?> newType = targetType;
        if (AsmTools.isSupport(targetType) && !aopList.isEmpty()) {
            AopClassConfig engine = classEngineMap.get(targetType);
            if (engine == null) {
                engine = new AopClassConfig(targetType, rootLoader);
                for (AopBindInfoAdapter aop : aopList) {
                    if (!aop.getMatcherClass().test(targetType)) {
                        continue;
                    }
                    engine.addAopInterceptor(aop.getMatcherMethod(), aop);
                }
                engine = classEngineMap.putIfAbsent(targetType, engine);
                if (engine == null) {
                    engine = classEngineMap.get(targetType);
                }
            }
            try {
                newType = engine.buildClass();
            } catch (Exception e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }
        //
        return (Class<T>) newType;
    }

    /*-------------------------------------------------------------------------------------------*/

    /**
     * 仅执行依赖注入
     */
    public <T> T justInject(T object, Class<?> referType, AppContext appContext) {
        this.justInject(object, referType, null, appContext);
        return object;
    }

    /**
     * 仅执行依赖注入
     */
    public <T> T justInject(T object, BindInfo<?> bindInfo, AppContext appContext) {
        DefaultBindInfoProviderAdapter<?> adapter = (DefaultBindInfoProviderAdapter) bindInfo;
        Class<?> referType = adapter.getSourceType() != null ? adapter.getSourceType() : adapter.getBindType();
        //
        this.justInject(object, referType, bindInfo, appContext);
        return object;
    }

    private <T> void justInject(T targetBean, Class<?> targetType, BindInfo<?> bindInfo, AppContext appContext) {
        //
        // .Aware接口的执行
        if (bindInfo != null && targetBean instanceof BindInfoAware) {
            ((BindInfoAware) targetBean).setBindInfo(bindInfo);
        }
        if (targetBean instanceof AppContextAware) {
            ((AppContextAware) targetBean).setAppContext(appContext);
        }
        //
        // .依赖注入(InjectMembers接口)
        targetType = (targetType == null) ? targetBean.getClass() : targetType;
        if (targetBean instanceof InjectMembers) {
            try {
                ((InjectMembers) targetBean).doInject(appContext);
            } catch (Throwable e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
            return;
        }
        // a.配置注入
        Set<String> injectFileds = new HashSet<>();
        boolean isOverwriteAnnotation = false;
        if (bindInfo instanceof DefaultBindInfoProviderAdapter) {
            DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
            isOverwriteAnnotation = defBinder.isOverwriteAnnotation();
            //
            Map<String, Supplier<?>> propMaps = defBinder.getPropertys(appContext);
            for (Map.Entry<String, Supplier<?>> propItem : propMaps.entrySet()) {
                String propertyName = propItem.getKey();
                Class<?> propertyType = BeanUtils.getPropertyOrFieldType(targetType, propertyName);
                boolean canWrite = BeanUtils.canWriteProperty(propertyName, targetType);
                //
                if (!canWrite) {
                    // 理论上进不到这里，原因是在DefaultBindInfoProviderAdapter 配置阶段就会拦截到没有属性对应 set 方法的情况。
                    throw new IllegalStateException("doInject, property " + propertyName + " can not write.");
                }
                Supplier<?> provider = propItem.getValue();
                if (provider == null) {
                    // 理论上进不到这里，原因是在DefaultBindInfoProviderAdapter 配置阶段就会拦截到空情况。
                    throw new IllegalStateException("can't injection ,property " + propertyName + " data Provider is null.");
                }
                //
                Object propertyVal = ConverterUtils.convert(propertyType, provider.get());
                BeanUtils.writePropertyOrField(targetBean, propertyName, propertyVal);
                injectFileds.add(propertyName);
            }
        }
        // b.注解注入
        List<Field> fieldList = BeanUtils.findALLFields(targetType);
        fieldList = fieldList == null ? new ArrayList<>(0) : fieldList;
        for (Field field : fieldList) {
            Annotation injectInfo = findInject(false, field.getAnnotations());
            if (injectInfo == null) {
                continue;
            }
            //
            String name = field.getName();
            boolean hasInjected = injectFileds.contains(name);
            if (hasInjected) {
                if (isOverwriteAnnotation) {
                    continue;//如果强制覆盖注解配置启用了，那么这里遇到冲突的时候自动忽略即可。
                }
                throw new IllegalStateException("doInject , " + targetType + " , property '" + name + "' duplicate.");
            }
            //
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            invokeField(field, targetBean, providerOnlyAnnotation(field.getType(), injectInfo, appContext).get());
            injectFileds.add(field.getName());
        }
        // c.方法注入
        List<Method> methodList = BeanUtils.findALLMethods(targetType);
        methodList = methodList == null ? new ArrayList<>(0) : methodList;
        for (Method method : methodList) {
            Annotation injectInfo = findInject(false, method.getAnnotations());
            if (injectInfo == null) {
                continue;
            }
            //
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            //
            Supplier<Object[]> parameterSupplier = parameterSupplier(InstanceProvider.of(method), appContext, ArrayUtils.EMPTY_OBJECT_ARRAY, true);
            try {
                method.invoke(targetBean, parameterSupplier.get());
            } catch (InvocationTargetException e2) {
                throw ExceptionUtils.toRuntimeException(e2.getTargetException());
            } catch (Exception e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }
    }

    /*-------------------------------------------------------------------------------------------*/

    private <T> void doLife(T targetObject, BindInfo<T> bindInfo, AppContext appContext) {
        //
        // .Init初始化方法。
        Method initMethod = findInitMethod(targetObject.getClass(), bindInfo);
        if (initMethod != null && Modifier.isPublic(initMethod.getModifiers())) {
            invokeMethod(targetObject, initMethod);
        }
        //
        // .注册销毁事件
        Method destroyMethod = findDestroyMethod(targetObject.getClass(), bindInfo);
        if (destroyMethod != null && Modifier.isPublic(destroyMethod.getModifiers())) {
            boolean single = false;
            if (bindInfo != null) {
                single = getScopContainer().isSingleton(bindInfo);
            } else {
                single = getScopContainer().isSingleton(targetObject.getClass());
            }
            if (single) {
                HasorUtils.pushShutdownListener(appContext.getEnvironment(), (EventListener<AppContext>) (event, eventData) -> {
                    invokeMethod(targetObject, destroyMethod);
                });
            }
        }
    }

    public void preInitialize() {
        tryInit(this.spiCallerContainer);
        tryInit(this.scopContainer);
    }

    @Override
    protected void doInitialize() {
        preInitialize();
        tryInit(this.bindInfoContainer);
        //
        this.bindInfoContainer.forEach(bindInfo -> {
            DefaultBindInfoProviderAdapter<?> infoAdapter = (DefaultBindInfoProviderAdapter<?>) bindInfo;
            Method initMethod = findInitMethod(infoAdapter.getBindType(), infoAdapter); // 配置了init方法
            boolean singleton = scopContainer.isSingleton(bindInfo);                    // 配置了单例（只有单例的才会在容器启动时调用）
            if (initMethod != null && singleton) {
                // 当前为 doInitialize 阶段，需要在 doStart 阶段开始调用 Bean 的 init。执行 init 只需要 get 它们。
                HasorUtils.pushStartListener(this.environment, (EventListener<AppContext>) (event, eventData) -> {
                    eventData.getInstance(infoAdapter);//执行init
                });
            }
        });
    }

    @Override
    protected void doClose() {
        this.classEngineMap.clear();
        tryClose(this.bindInfoContainer);
        tryClose(this.scopContainer);
        tryClose(this.spiCallerContainer);
    }

    private void tryInit(AbstractContainer container) {
        if (!container.isInit()) {
            container.init();
        }
    }

    private void tryClose(AbstractContainer container) {
        if (container.isInit()) {
            container.close();
        }
    }
}