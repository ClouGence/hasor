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
package net.hasor.core.context.factory;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.more.classcode.aop.AopClassConfig;
import org.more.classcode.aop.AopMatcher;
import org.more.convert.ConverterUtils;
import org.more.util.BeanUtils;
import org.more.util.ExceptionUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoAware;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.InjectMembers;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import net.hasor.core.context.BeanBuilder;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.info.AopBindInfoAdapter;
import net.hasor.core.info.CustomerProvider;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.core.info.ScopeProvider;
/**
 * 负责根据Class或BindInfo创建Bean。
 * @version : 2015年6月26日
 * @author 赵永春(zyc@hasor.net)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class FactoryBeanBuilder implements BeanBuilder {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /**创建一个AbstractBindInfoProviderAdapter*/
    public <T> AbstractBindInfoProviderAdapter<T> createBindInfoByType(Class<T> bindType) {
        return new DefaultBindInfoProviderAdapter<T>(bindType);
    }
    /** 通过{@link BindInfo}创建Bean。 */
    public <T> T getInstance(final BindInfo<T> bindInfo, final AppContext appContext) {
        Provider<T> instanceProvider = null;
        Provider<Scope> scopeProvider = null;
        //
        //可能存在的 CustomerProvider
        if (bindInfo instanceof CustomerProvider) {
            CustomerProvider<T> adapter = (CustomerProvider<T>) bindInfo;
            instanceProvider = adapter.getCustomerProvider();
        }
        //可能存在的 ScopeProvider
        if (bindInfo instanceof ScopeProvider) {
            ScopeProvider adapter = (ScopeProvider) bindInfo;
            scopeProvider = adapter.getScopeProvider();
        }
        //create Provider.
        if (instanceProvider == null && bindInfo instanceof AbstractBindInfoProviderAdapter == true) {
            instanceProvider = new Provider<T>() {
                public T get() {
                    Class<T> targetType = bindInfo.getBindType();
                    Class<T> superType = ((AbstractBindInfoProviderAdapter) bindInfo).getSourceType();
                    if (superType != null) {
                        targetType = superType;
                    }
                    T targetBean = createObject(targetType, bindInfo, appContext);
                    return targetBean;
                }
            };
        } else if (instanceProvider == null) {
            instanceProvider = new Provider<T>() {
                public T get() {
                    T targetBean = getDefaultInstance(bindInfo.getBindType(), appContext);
                    return targetBean;
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
    /**创建一个未绑定过的类型*/
    public <T> T getDefaultInstance(final Class<T> oriType, AppContext appContext) {
        if (oriType == null) {
            return null;
        }
        T targetBean = createObject(oriType, null, appContext);
        return targetBean;
    }
    //
    //
    //
    private ConcurrentHashMap<Class<?>, AopClassConfig> buildEngineMap = new ConcurrentHashMap<Class<?>, AopClassConfig>();
    /**获取用于创建Bean的 Engine。*/
    public AopClassConfig buildEngine(Class<?> targetType, List<AopBindInfoAdapter> aopList, ClassLoader rootLosder) {
        AopClassConfig engine = new AopClassConfig(targetType, rootLosder);
        for (AopBindInfoAdapter aop : aopList) {
            if (aop.getMatcherClass().matches(targetType) == false) {
                continue;
            }
            AopMatcher aopMatcher = new FactoryBeanAopMatcher(aop.getMatcherMethod());
            engine.addAopInterceptor(aopMatcher, aop);
        }
        return engine;
    }
    //
    /**创建对象*/
    private <T> T createObject(Class<T> targetType, BindInfo<T> bindInfo, AppContext appContext) {
        try {
            //
            //1.特殊类型创建处理。
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
            //2.准备Aop
            List<BindInfo<AopBindInfoAdapter>> aopBindList = appContext.findBindingRegister(AopBindInfoAdapter.class);
            List<AopBindInfoAdapter> aopList = new ArrayList<AopBindInfoAdapter>();
            for (BindInfo<AopBindInfoAdapter> info : aopBindList) {
                aopList.add(this.getInstance(info, appContext));
            }
            //
            //2.动态代理
            Class<?> newType = targetType;
            if (AopClassConfig.isSupport(targetType)) {
                AopClassConfig cc = this.buildEngineMap.get(targetType);
                if (cc == null) {
                    AopClassConfig newCC = buildEngine(targetType, aopList, appContext.getClassLoader());
                    cc = this.buildEngineMap.putIfAbsent(targetType, newCC);
                    if (cc == null) {
                        cc = newCC;
                    }
                }
                if (cc.hasChange() == true) {
                    newType = cc.toClass();
                } else {
                    newType = cc.getSuperClass();
                }
            }
            //
            //3.确定要调用的构造方法。
            Constructor<?> constructor = null;
            Provider<?>[] paramProviders = null;
            if (bindInfo != null && bindInfo instanceof DefaultBindInfoProviderAdapter) {
                DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
                constructor = defBinder.getConstructor(newType, appContext);
                paramProviders = defBinder.getConstructorParams(newType, appContext);
            } else {
                constructor = newType.getConstructor();
                paramProviders = new Provider<?>[0];
            }
            //
            //4.创建对象。
            if (paramProviders == null || paramProviders.length == 0) {
                T targetBean = (T) constructor.newInstance();
                return doInject(targetBean, bindInfo, appContext);
            } else {
                Object[] paramObjects = new Object[paramProviders.length];
                for (int i = 0; i < paramProviders.length; i++) {
                    paramObjects[i] = paramProviders[i].get();
                }
                T targetBean = (T) constructor.newInstance(paramObjects);
                return doInject(targetBean, bindInfo, appContext);
            }
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    /**执行依赖注入*/
    private <T> T doInject(T targetBean, BindInfo<T> bindInfo, AppContext appContext) throws Throwable {
        //1.Aware接口的执行
        if (targetBean instanceof BindInfoAware) {
            ((BindInfoAware) targetBean).setBindInfo(bindInfo);
        }
        if (targetBean instanceof AppContextAware) {
            ((AppContextAware) targetBean).setAppContext(appContext);
        }
        //2.依赖注入
        Class<?> targetType = targetBean.getClass();
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
    private <T> void injectObject(T targetBean, BindInfo<T> bindInfo, AppContext appContext, Class<?> targetType) throws IllegalAccessException {
        Set<String> injectFileds = new HashSet<String>();
        /*a.配置注入*/
        if (bindInfo instanceof DefaultBindInfoProviderAdapter) {
            DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
            Map<String, Provider<?>> propMaps = defBinder.getPropertys(appContext);
            for (Entry<String, Provider<?>> propItem : propMaps.entrySet()) {
                String propertyName = propItem.getKey();
                Class<?> propertyType = BeanUtils.getPropertyOrFieldType(targetType, propertyName);
                boolean canWrite = BeanUtils.canWriteProperty(propertyName, targetType);
                //
                if (canWrite == false) {
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
            boolean hasAnno = field.isAnnotationPresent(Inject.class);
            boolean hasInjected = injectFileds.contains(name);
            if (hasAnno == false) {
                continue;
            }
            if (hasInjected) {
                String logMsg = "doInject, property " + name + " duplicate.";
                logger.warn(logMsg);
                throw new IllegalStateException(logMsg);
            }
            if (field.isAccessible() == false) {
                field.setAccessible(true);
            }
            Inject inject = field.getAnnotation(Inject.class);
            Object obj = null;
            if (StringUtils.isBlank(inject.value())) {
                obj = appContext.getInstance(field.getType());
            } else {
                obj = appContext.getInstance(inject.value());
            }
            if (obj != null) {
                field.set(targetBean, obj);
            }
            injectFileds.add(field.getName());
        }
    }
    private <T> void initObject(T targetBean, BindInfo<T> bindInfo) throws Throwable {
        try {
            //a.可能存在的配置。
            if (bindInfo instanceof DefaultBindInfoProviderAdapter) {
                DefaultBindInfoProviderAdapter<?> defBinder = (DefaultBindInfoProviderAdapter<?>) bindInfo;
                Method initMethod = defBinder.getInitMethod(targetBean.getClass());
                if (initMethod != null) {
                    initMethod.invoke(targetBean);
                    return;
                }
            }
            //b.注解形式
            List<Method> methodList = BeanUtils.getMethods(targetBean.getClass());
            for (Method method : methodList) {
                boolean hasAnno = method.isAnnotationPresent(Init.class);
                if (hasAnno == false) {
                    continue;
                }
                //
                Class<?>[] paramArray = method.getParameterTypes();
                Object[] paramObject = BeanUtils.getDefaultValue(paramArray);
                method.invoke(targetBean, paramObject);
            }
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}