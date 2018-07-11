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
import net.hasor.core.*;
import net.hasor.core.Type;
import net.hasor.core.info.*;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;
/**
 * 负责创建Bean对象，以及依赖注入和Aop的实现。
 * @version : 2015年6月26日
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class TemplateBeanBuilder implements BeanBuilder {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    public <T> AbstractBindInfoProviderAdapter<T> createInfoAdapter(Class<T> bindType, Class<?> binderSource) {
        return new DefaultBindInfoProviderAdapter<T>(bindType);
    }
    //
    //
    //
    public <T> T getInstance(Class<T> targetType, AppContext appContext) {
        if (targetType == null) {
            return null;
        }
        try {
            return createObject(targetType, null, null, appContext);
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    public <T> T getInstance(Constructor<T> targetConstructor, AppContext appContext) {
        if (targetConstructor == null) {
            return null;
        }
        try {
            return createObject(targetConstructor.getDeclaringClass(), targetConstructor, null, appContext);
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    public <T> T getInstance(final BindInfo<T> bindInfo, final AppContext appContext) {
        Provider<? extends T> instanceProvider = null;
        Provider<Scope> scopeProvider = null;
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
            instanceProvider = new Provider<T>() {
                public T get() {
                    Class<T> targetType = bindInfo.getBindType();
                    Class<T> superType = ((AbstractBindInfoProviderAdapter) bindInfo).getSourceType();
                    if (superType != null) {
                        targetType = superType;
                    }
                    try {
                        return createObject(targetType, null, bindInfo, appContext);
                    } catch (Throwable e) {
                        throw ExceptionUtils.toRuntimeException(e);
                    }
                }
            };
        } else if (instanceProvider == null) {
            instanceProvider = new Provider<T>() {
                public T get() {
                    return getInstance(bindInfo.getBindType(), appContext);
                }
            };
        }
        //scope
        if (scopeProvider != null) {
            instanceProvider = scopeProvider.get().scope(bindInfo, instanceProvider);
        }
        return instanceProvider.get();
    }
    //
    //
    //
    protected <T> Class<T> findImplClass(final Class<?> notSureType) {
        Class<?> tmpType = notSureType;
        ImplBy implBy = null;
        do {
            implBy = tmpType.getAnnotation(ImplBy.class);
            if (implBy != null) {
                tmpType = implBy.value();
            }
            if (tmpType == notSureType) {
                break;
            }
        } while (implBy != null);
        return (Class<T>) tmpType;
    }
    protected <T> T createObject(Class<T> targetType, Constructor<T> referConstructor, BindInfo<T> bindInfo, AppContext appContext) throws Throwable {
        //
        // .targetType也许只是一个接口或者抽象类，找到真正创建的那个类型
        targetType = findImplClass(targetType);
        //
        // .check
        int modifiers = targetType.getModifiers();
        if (targetType.isInterface() || targetType.isEnum() || (modifiers == (modifiers | Modifier.ABSTRACT))) {
            return null;
        }
        if (targetType.isPrimitive()) {
            return (T) BeanUtils.getDefaultValue(targetType);
        }
        if (targetType.isArray()) {
            Class<?> comType = targetType.getComponentType();
            return (T) Array.newInstance(comType, 0);
        }
        //
        // .准备Aop
        List<BindInfo<AopBindInfoAdapter>> aopBindList = appContext.findBindingRegister(AopBindInfoAdapter.class);
        List<AopBindInfoAdapter> aopList = new ArrayList<AopBindInfoAdapter>();
        for (BindInfo<AopBindInfoAdapter> info : aopBindList) {
            aopList.add(this.getInstance(info, appContext));
        }
        //
        // .动态代理
        ClassLoader rootLoader = appContext.getClassLoader();
        Class<?> newType = ClassEngine.buildType(targetType, rootLoader, aopList, appContext);
        //
        // .确定要调用的构造方法 & 构造入参
        Constructor<?> constructor = null;
        Object[] paramObjects = null;
        if (bindInfo instanceof DefaultBindInfoProviderAdapter) {
            //
            DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
            constructor = defBinder.getConstructor(newType, appContext);
            //
            Provider<?>[] paramProviders = defBinder.getConstructorParams(newType, appContext);
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
                constructor = newType.getConstructor(referConstructor.getParameterTypes());
            } else {
                try {
                    constructor = targetType.getConstructor();
                } catch (Exception e) {
                    constructor = newType.getConstructor(targetType.getConstructors()[0].getParameterTypes());
                }
                parameterTypes = constructor.getParameterTypes();
                parameterAnnos = constructor.getParameterAnnotations();
            }
            //
            paramObjects = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Annotation[] annotations = parameterAnnos[i];
                //
                Inject inject = findAnnotation(Inject.class, annotations);
                if (inject != null) {
                    if (Type.ByName == inject.byType() && StringUtils.isNotBlank(inject.value())) {
                        paramObjects[i] = appContext.findBindingBean(inject.value(), parameterTypes[i]);
                    } else {
                        paramObjects[i] = appContext.getInstance(parameterTypes[i]);
                    }
                    continue;
                }
                InjectSettings injectSettings = findAnnotation(InjectSettings.class, annotations);
                if (injectSettings != null) {
                    paramObjects[i] = injSettings(appContext, injectSettings, parameterTypes[i]);
                    continue;
                }
                paramObjects[i] = BeanUtils.getDefaultValue(parameterTypes[i]);
            }
        }
        //
        // .创建对象。
        if (paramObjects.length == 0) {
            T targetBean = (T) constructor.newInstance();
            return doInject(targetBean, bindInfo, appContext, newType);
        } else {
            T targetBean = (T) constructor.newInstance(paramObjects);
            return doInject(targetBean, bindInfo, appContext, newType);
        }
    }
    private <T extends Annotation> T findAnnotation(Class<T> annoType, Annotation[] annotations) {
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        for (Annotation anno : annotations) {
            if (annoType.isAssignableFrom(annoType)) {
                return (T) anno;
            }
        }
        return null;
    }
    //
    //
    //
    /**执行依赖注入*/
    protected <T> T doInject(T targetBean, BindInfo<?> bindInfo, AppContext appContext, Class<?> targetType) throws Throwable {
        //1.Aware接口的执行
        if (bindInfo != null && targetBean instanceof BindInfoAware) {
            ((BindInfoAware) targetBean).setBindInfo(bindInfo);
        }
        if (targetBean instanceof AppContextAware) {
            ((AppContextAware) targetBean).setAppContext(appContext);
        }
        //2.依赖注入
        targetType = (targetType == null) ? targetBean.getClass() : targetType;
        if (targetBean instanceof InjectMembers) {
            ((InjectMembers) targetBean).doInject(appContext);
        } else {
            injectObject(targetBean, bindInfo, appContext, targetType);
        }
        //3.Init初始化方法。
        initObject(targetBean, bindInfo);
        //
        return targetBean;
    }
    /**/
    private <T> void injectObject(T targetBean, BindInfo<?> bindInfo, AppContext appContext, Class<?> targetType) throws IllegalAccessException {
        Set<String> injectFileds = new HashSet<String>();
        /*a.配置注入*/
        if (bindInfo != null && bindInfo instanceof DefaultBindInfoProviderAdapter) {
            DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
            Map<String, Provider<?>> propMaps = defBinder.getPropertys(appContext);
            for (Entry<String, Provider<?>> propItem : propMaps.entrySet()) {
                String propertyName = propItem.getKey();
                Class<?> propertyType = BeanUtils.getPropertyOrFieldType(targetType, propertyName);
                boolean canWrite = BeanUtils.canWriteProperty(propertyName, targetType);
                //
                if (!canWrite) {
                    String logMsg = "doInject, property " + propertyName + " can not write.";
                    logger.error(logMsg);
                    throw new IllegalStateException(logMsg);
                }
                Provider<?> provider = propItem.getValue();
                if (provider == null) {
                    String logMsg = "can't injection ,property " + propertyName + " data Provider is null.";
                    logger.error(logMsg);
                    throw new IllegalStateException(logMsg);
                }
                //
                Object propertyVal = ConverterUtils.convert(propertyType, provider.get());
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
                String logMsg = "doInject , " + targetType + " , property " + name + " duplicate.";
                logger.warn(logMsg);
                throw new IllegalStateException(logMsg);
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            //
            boolean inj = injInject(targetBean, appContext, field);// @Inject
            if (!inj) {
                Object settingValue = injSettings(appContext, field.getAnnotation(InjectSettings.class), field.getDeclaringClass()); // @InjectSettings
                if (settingValue != null) {
                    field.set(targetBean, settingValue);
                }
            }
            //
            injectFileds.add(field.getName());
        }
    }
    //
    private <T> boolean injInject(T targetBean, AppContext appContext, Field field) throws IllegalAccessException {
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
            field.set(targetBean, obj);
            return true;
        } else {
            return false;
        }
    }
    //
    private Object injSettings(AppContext appContext, InjectSettings injectSettings, Class<?> toType) throws IllegalAccessException {
        if (injectSettings == null || StringUtils.isBlank(injectSettings.value())) {
            return BeanUtils.getDefaultValue(toType);
        }
        String settingVar = injectSettings.value();
        String settingValue = null;
        if (settingVar.startsWith("${") && settingVar.endsWith("}")) {
            settingVar = settingVar.substring(2, settingVar.length() - 1);
            settingValue = appContext.getEnvironment().evalString("%" + settingVar + "%");
        } else {
            settingValue = appContext.getEnvironment().getSettings().getString(injectSettings.value(), injectSettings.defaultValue());
        }
        return ConverterUtils.convert(settingValue, toType);
    }
    //
    private void initObject(Object targetBean, BindInfo<?> bindInfo) throws Throwable {
        try {
            Method initMethod = findInitMethod(targetBean.getClass(), bindInfo);
            //
            if (initMethod != null) {
                Class<?>[] paramArray = initMethod.getParameterTypes();
                Object[] paramObject = BeanUtils.getDefaultValue(paramArray);
                initMethod.invoke(targetBean, paramObject);
            }
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
    //
    /** 查找类的默认初始化方法*/
    public static Method findInitMethod(Class<?> targetBeanType, BindInfo<?> bindInfo) {
        Method initMethod = null;
        //a.注解形式（注解优先）
        if (targetBeanType != null) {
            List<Method> methodList = BeanUtils.getMethods(targetBeanType);
            for (Method method : methodList) {
                boolean hasAnno = method.isAnnotationPresent(Init.class);
                if (hasAnno) {
                    initMethod = method;
                    break;
                }
            }
        }
        //b.可能存在的配置。
        if (initMethod == null && bindInfo instanceof DefaultBindInfoProviderAdapter) {
            DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
            initMethod = defBinder.getInitMethod(targetBeanType);
        }
        return initMethod;
    }
    /** 检测是否为单例（注解优先）*/
    public static boolean testSingleton(Class<?> targetType, BindInfo<?> bindInfo, Settings settings) {
        Prototype prototype = targetType.getAnnotation(Prototype.class);
        Singleton singleton = targetType.getAnnotation(Singleton.class);
        if (prototype != null && singleton != null) {
            throw new IllegalArgumentException(targetType + " , @Prototype and @Singleton appears only one.");
        }
        //
        boolean isSingleton = false;
        if (prototype != null) {
            isSingleton = false;
        } else if (singleton != null) {
            isSingleton = true;
        } else {
            if (settings != null) {
                isSingleton = settings.getBoolean("hasor.default.asEagerSingleton", true);
            }
            if (bindInfo != null && bindInfo instanceof AbstractBindInfoProviderAdapter) {
                Boolean sing = ((AbstractBindInfoProviderAdapter) bindInfo).isSingleton();
                if (sing != null) {
                    isSingleton = sing;
                }
            }
        }
        return isSingleton;
    }
}