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
import net.hasor.core.Type;
import net.hasor.core.*;
import net.hasor.core.aop.AopClassConfig;
import net.hasor.core.aop.AsmTools;
import net.hasor.core.info.*;
import net.hasor.utils.ArrayUtils;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterBean;
import net.hasor.utils.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static net.hasor.core.container.ContainerUtils.*;
/**
 * 负责创建Bean对象，以及依赖注入和Aop的实现。
 * @version : 2015年6月26日
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class TemplateBeanBuilder implements BeanBuilder {
    protected static Logger                                      logger         = LoggerFactory.getLogger(TemplateBeanBuilder.class);
    private static   ConverterBean                               converterUtils = null;
    private static   ConcurrentHashMap<Class<?>, AopClassConfig> buildEngineMap = null;

    static {
        buildEngineMap = new ConcurrentHashMap<>();
        converterUtils = new ConverterBean();
        converterUtils.deregister();
        converterUtils.register(true, true, 0);
    }

    //
    public <T> AbstractBindInfoProviderAdapter<T> createInfoAdapter(Class<T> bindType) {
        return new DefaultBindInfoProviderAdapter<>(bindType);
    }
    //
    //
    //
    public <T> Supplier<? extends T> getProvider(final Class<T> targetType, final AppContext appContext, Object[] params) {
        if (targetType == null) {
            return null;
        }
        return (Supplier<T>) () -> createObject(targetType, null, null, appContext, params);
    }
    //
    public <T> Supplier<? extends T> getProvider(final Constructor<T> targetConstructor, final AppContext appContext, Object[] params) {
        if (targetConstructor == null) {
            return null;
        }
        return (Supplier<T>) () -> createObject(targetConstructor.getDeclaringClass(), targetConstructor, null, appContext, params);
    }
    //
    public <T> Supplier<? extends T> getProvider(final BindInfo<T> bindInfo, final AppContext appContext, Object[] params) {
        if (bindInfo == null) {
            return null;
        }
        //
        Supplier<? extends T> instanceProvider = null;
        Supplier<? extends Scope> scopeProvider = null;
        //
        //可能存在的 CustomerProvider
        if (bindInfo instanceof CustomerProvider) {
            CustomerProvider<? extends T> adapter = (CustomerProvider<T>) bindInfo;
            instanceProvider = adapter.getCustomerProvider();
        }
        //可能存在的 ScopeProvider
        if (bindInfo instanceof ScopeProvider) {
            ScopeProvider adapter = (ScopeProvider) bindInfo;
            scopeProvider = adapter.getScopeProvider();
        }
        //create Provider.
        if (instanceProvider == null && bindInfo instanceof AbstractBindInfoProviderAdapter) {
            instanceProvider = (Supplier<T>) () -> {
                Class<T> targetType = bindInfo.getBindType();
                Class<T> superType = ((AbstractBindInfoProviderAdapter) bindInfo).getSourceType();
                if (superType != null) {
                    targetType = superType;
                }
                return createObject(targetType, null, bindInfo, appContext, params);
            };
        } else if (instanceProvider == null) {
            instanceProvider = getProvider(bindInfo.getBindType(), appContext, params);
        }
        //scope
        if (scopeProvider != null) {
            instanceProvider = scopeProvider.get().scope(bindInfo, instanceProvider);
        }
        Supplier<? extends T> finalInstanceProvider = instanceProvider;
        return (Supplier<T>) () -> {
            T targetBean = finalInstanceProvider.get();
            doCreaterListener(targetBean, bindInfo);
            return targetBean;
        };
    }
    //
    //
    //
    protected <T> T createObject(Class<T> targetType, Constructor<T> referConstructor, BindInfo<T> bindInfo, AppContext appContext, Object[] params) {
        //
        // .targetType也许只是一个接口或者抽象类，找到真正创建的那个类型
        targetType = findImplClass(targetType);
        //
        // .check
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
            // Integer.TYPE 判断结果为 true & targetType.isArray() 情况下也为 true
            return null;// 因此要放在后面
        }
        //
        // .准备Aop
        List<BindInfo<AopBindInfoAdapter>> aopBindList = appContext.findBindingRegister(AopBindInfoAdapter.class);
        List<AopBindInfoAdapter> aopList;
        if (!aopBindList.isEmpty()) {
            aopList = new ArrayList<>();
            for (BindInfo<AopBindInfoAdapter> info : aopBindList) {
                aopList.add(this.getProvider(info, appContext, params).get());
            }
        } else {
            aopList = Collections.emptyList();
        }
        //
        // .动态代理，需要满足三个条件（1.类型必须支持Aop、2.没有被@AopIgnore排除在外、3.具有至少一个有效的拦截器）
        ClassLoader rootLoader = Objects.requireNonNull(appContext.getClassLoader());
        Class<?> newType = targetType;
        if (AsmTools.isSupport(targetType) && !testAopIgnore(targetType, rootLoader) && !aopList.isEmpty()) {
            AopClassConfig engine = buildEngineMap.get(targetType);
            if (engine == null) {
                engine = new AopClassConfig(targetType, rootLoader);
                for (AopBindInfoAdapter aop : aopList) {
                    if (!aop.getMatcherClass().test(targetType)) {
                        continue;
                    }
                    engine.addAopInterceptor(aop.getMatcherMethod(), aop);
                }
                engine = buildEngineMap.putIfAbsent(targetType, engine);
                if (engine == null) {
                    engine = buildEngineMap.get(targetType);
                }
            }
            try {
                newType = engine.buildClass();
            } catch (Exception e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }
        //
        // .确定要调用的构造方法 & 构造入参
        Constructor<?> constructor = null;
        Object[] paramObjects = null;
        if (bindInfo instanceof DefaultBindInfoProviderAdapter) {
            //
            DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
            constructor = defBinder.getConstructor(newType, appContext);
            //
            Supplier<?>[] paramProviders = defBinder.getConstructorParams(appContext);
            paramObjects = new Object[paramProviders.length];
            for (int i = 0; i < paramProviders.length; i++) {
                paramObjects[i] = paramProviders[i].get();
            }
        } else {
            //
            Class<?>[] parameterTypes = null;
            Annotation[][] parameterAnnos = null;
            if (referConstructor != null) {
                parameterTypes = referConstructor.getParameterTypes();
                parameterAnnos = referConstructor.getParameterAnnotations();
                constructor = ConstructorUtils.getAccessibleConstructor(newType, referConstructor.getParameterTypes());
            } else {
                Constructor<?>[] constructorArrays = newType.getConstructors();
                for (Constructor<?> c : constructorArrays) {
                    if (c.isAnnotationPresent(ConstructorBy.class)) {
                        constructor = c;
                        parameterTypes = c.getParameterTypes();
                        parameterAnnos = c.getParameterAnnotations();
                        break;
                    }
                }
                if (constructor == null) {
                    constructor = ConstructorUtils.getMatchingAccessibleConstructor(newType, ArrayUtils.EMPTY_CLASS_ARRAY);
                    parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
                    parameterAnnos = new Annotation[0][0];
                }
            }
            //
            if (constructor == null) {
                throw new RuntimeException("No default constructor found.");
            }
            //
            paramObjects = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Annotation[] annotations = parameterAnnos[i];
                //
                Inject inject = findAnnotation(Inject.class, annotations);
                if (inject != null) {
                    if (Type.ByID == inject.byType()) {
                        paramObjects[i] = appContext.getInstance(inject.value());
                    } else if (Type.ByName == inject.byType()) {
                        paramObjects[i] = appContext.findBindingBean(inject.value(), parameterTypes[i]);
                    }
                    continue;
                }
                InjectSettings injectSettings = findAnnotation(InjectSettings.class, annotations);
                if (injectSettings != null) {
                    paramObjects[i] = injSettings(appContext, injectSettings, parameterTypes[i]);
                    continue;
                }
                //
                if (params != null && params.length > i) {
                    paramObjects[i] = params[i];
                    continue;
                }
                //
                paramObjects[i] = BeanUtils.getDefaultValue(parameterTypes[i]);
            }
        }
        //
        // .创建对象
        T targetBean = null;
        try {
            if (paramObjects.length == 0) {
                targetBean = (T) constructor.newInstance();
                targetBean = doInject(targetBean, bindInfo, appContext, newType);
            } else {
                targetBean = (T) constructor.newInstance(paramObjects);
                targetBean = doInject(targetBean, bindInfo, appContext, newType);
            }
            //
            // .生命周期相关方法
            doLife(targetBean, bindInfo, appContext, newType);
            doCreaterListener(targetBean, bindInfo);
            return targetBean;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    private <T extends Annotation> T findAnnotation(Class<T> annoType, Annotation[] annotations) {
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        for (Annotation anno : annotations) {
            if (annoType.isInstance(anno)) {
                return (T) anno;
            }
        }
        return null;
    }
    //
    /**执行依赖注入*/
    protected <T> T doInject(T targetBean, BindInfo<?> bindInfo, AppContext appContext, Class<?> targetType) {
        //
        // 1.Aware接口的执行
        if (bindInfo != null && targetBean instanceof BindInfoAware) {
            ((BindInfoAware) targetBean).setBindInfo(bindInfo);
        }
        if (targetBean instanceof AppContextAware) {
            ((AppContextAware) targetBean).setAppContext(appContext);
        }
        //
        // 2.依赖注入
        targetType = (targetType == null) ? targetBean.getClass() : targetType;
        if (targetBean instanceof InjectMembers) {
            try {
                ((InjectMembers) targetBean).doInject(appContext);
            } catch (Throwable e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        } else {
            injectObject(targetBean, bindInfo, appContext, targetType);
        }
        //
        return targetBean;
    }
    /** 执行生命周期相关方法 */
    protected void doLife(Object targetBean, BindInfo<?> bindInfo, AppContext appContext, Class<?> targetType) {
        //
        // 3.Init初始化方法。
        Method initMethod = findInitMethod(targetBean.getClass(), bindInfo);
        if (initMethod != null && Modifier.isPublic(initMethod.getModifiers())) {
            invokeMethod(targetBean, initMethod);
        }
        //
        // 4.注册销毁事件
        Method destroyMethod = findDestroyMethod(targetBean.getClass(), bindInfo);
        if (destroyMethod != null && Modifier.isPublic(destroyMethod.getModifiers())) {
            if (!testSingleton(targetType, bindInfo, appContext.getEnvironment().getSettings())) {
                throw new java.lang.IllegalStateException("destroyMethod bean must be single.");
            }
            HasorUtils.pushShutdownListener(appContext.getEnvironment(), (EventListener<AppContext>) (event, eventData) -> {
                invokeMethod(targetBean, destroyMethod);
            });
        }
    }
    /** 执行BeanCreaterListener相关方法 */
    protected void doCreaterListener(Object targetBean, BindInfo<?> bindInfo) {
        if (bindInfo instanceof AbstractBindInfoProviderAdapter) {
            List<Supplier<? extends BeanCreaterListener<?>>> listenerList = ((AbstractBindInfoProviderAdapter<?>) bindInfo).getCreaterListener();
            if (listenerList != null && !listenerList.isEmpty()) {
                for (Supplier<? extends BeanCreaterListener<?>> provider : listenerList) {
                    try {
                        BeanCreaterListener<Object> createrListener = (BeanCreaterListener<Object>) provider.get();
                        createrListener.beanCreated(targetBean, bindInfo);
                    } catch (Throwable e) {
                        logger.error("do call beanCreated -> " + e.getMessage(), e);
                    }
                }
            }
        }
    }
    //
    /**/
    private <T> void injectObject(T targetBean, BindInfo<?> bindInfo, AppContext appContext, Class<?> targetType) {
        Set<String> injectFileds = new HashSet<>();
        /*a.配置注入*/
        if (bindInfo != null && bindInfo instanceof DefaultBindInfoProviderAdapter) {
            DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
            Map<String, Supplier<?>> propMaps = defBinder.getPropertys(appContext);
            for (Entry<String, Supplier<?>> propItem : propMaps.entrySet()) {
                String propertyName = propItem.getKey();
                Class<?> propertyType = BeanUtils.getPropertyOrFieldType(targetType, propertyName);
                boolean canWrite = BeanUtils.canWriteProperty(propertyName, targetType);
                //
                if (!canWrite) {
                    throw new IllegalStateException("doInject, property " + propertyName + " can not write.");
                }
                Supplier<?> provider = propItem.getValue();
                if (provider == null) {
                    throw new IllegalStateException("can't injection ,property " + propertyName + " data Provider is null.");
                }
                //
                Object propertyVal = converterUtils.convert(provider.get(), propertyType);
                BeanUtils.writePropertyOrField(targetBean, propertyName, propertyVal);
                injectFileds.add(propertyName);
            }
        }
        /*b.注解注入*/
        List<Field> fieldList = BeanUtils.findALLFields(targetType);
        for (Field field : fieldList) {
            String name = field.getName();
            field.getAnnotations();
            boolean hasAnno_1 = field.isAnnotationPresent(Inject.class);
            boolean hasAnno_2 = field.isAnnotationPresent(InjectSettings.class);
            //
            if (!hasAnno_1 && !hasAnno_2) {
                continue;
            }
            boolean hasInjected = injectFileds.contains(name);
            if (hasInjected) {
                throw new IllegalStateException("doInject , " + targetType + " , property '" + name + "' duplicate.");
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            //
            boolean inj = injInject(targetBean, appContext, field);// @Inject
            if (!inj) {
                Object settingValue = injSettings(appContext, field.getAnnotation(InjectSettings.class), field.getDeclaringClass()); // @InjectSettings
                if (settingValue != null) {
                    setField(field, targetBean, settingValue);
                }
            }
            //
            injectFileds.add(field.getName());
        }
    }
    //
    private <T> boolean injInject(T targetBean, AppContext appContext, Field field) {
        Inject inject = field.getAnnotation(Inject.class);
        if (inject == null) {
            return false;
        }
        Type byType = inject.byType();
        Object obj = null;
        if (StringUtils.isBlank(inject.value())) {
            obj = appContext.getInstance(field.getType());
        } else {
            /*   */
            if (Type.ByID == byType) {
                obj = appContext.getInstance(inject.value());
            } else if (Type.ByName == byType) {
                obj = appContext.findBindingBean(inject.value(), field.getType());
            }
        }
        if (obj != null) {
            setField(field, targetBean, obj);
            return true;
        } else {
            return false;
        }
    }
    //
    public static Object injSettings(AppContext appContext, InjectSettings injectSettings, Class<?> toType) {
        if (injectSettings == null || StringUtils.isBlank(injectSettings.value())) {
            return BeanUtils.getDefaultValue(toType);
        }
        String settingVar = injectSettings.value();
        String settingValue = null;
        if (settingVar.startsWith("${") && settingVar.endsWith("}")) {
            settingVar = settingVar.substring(2, settingVar.length() - 1);
            settingValue = appContext.getEnvironment().evalString("%" + settingVar + "%");
        } else {
            String defaultVal = injectSettings.defaultValue();
            if (StringUtils.isBlank(defaultVal)) {
                defaultVal = null;// 行为保持和 Convert 一致
            }
            settingValue = appContext.getEnvironment().getSettings().getString(injectSettings.value(), defaultVal);
        }
        //
        return converterUtils.convert(settingValue, toType);
    }
    //
    //
    //
    private void invokeMethod(Object targetBean, Method initMethod) {
        //
        Class<?>[] paramArray = initMethod.getParameterTypes();
        Object[] paramObject = BeanUtils.getDefaultValue(paramArray);
        //
        try {
            try {
                initMethod.invoke(targetBean, paramObject);
            } catch (IllegalAccessException e) {
                initMethod.setAccessible(true);
                try {
                    initMethod.invoke(targetBean, paramObject);
                } catch (IllegalAccessException e1) {
                    logger.error(e1.getMessage(), e);
                }
            }
        } catch (InvocationTargetException e2) {
            throw ExceptionUtils.toRuntimeException(e2.getTargetException());
        }
    }
    private void setField(Field field, Object targetBean, Object newValue) {
        Class<?> toType = field.getType();
        newValue = converterUtils.convert(newValue, toType);
        try {
            field.set(targetBean, newValue);
        } catch (IllegalAccessException e) {
            try {
                field.setAccessible(true);
                field.set(targetBean, newValue);
            } catch (IllegalAccessException e1) {
                logger.error(e1.getMessage(), e);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}