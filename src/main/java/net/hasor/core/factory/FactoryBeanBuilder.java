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
package net.hasor.core.factory;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoAware;
import net.hasor.core.Inject;
import net.hasor.core.InjectMembers;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import net.hasor.core.context.BeanBuilder;
import net.hasor.core.context.DefineContainer;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.info.AopBindInfoAdapter;
import net.hasor.core.info.CustomerProvider;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.core.info.ScopeProvider;
import org.more.classcode.MoreClassLoader;
import org.more.classcode.aop.AopClassConfig;
import org.more.util.BeanUtils;
import org.more.util.ExceptionUtils;
/**
 * 
 * @version : 2015年6月26日
 * @author 赵永春(zyc@hasor.net)
 */
public class FactoryBeanBuilder implements BeanBuilder {
    private ClassLoader masterLosder = new MoreClassLoader();
    //
    /**创建一个AbstractBindInfoProviderAdapter*/
    public <T> AbstractBindInfoProviderAdapter<T> createBindInfoByType(Class<T> bindType) {
        return new FactoryBindInfoProviderAdapter<T>(bindType, masterLosder);
    }
    /** 通过{@link BindInfo}创建Bean。 */
    public <T> T getInstance(final BindInfo<T> bindInfo, final DefineContainer container, final AppContext appContext) {
        Provider<?> instanceProvider = null;
        Provider<Scope> scopeProvider = null;
        //
        //可能存在的 CustomerProvider
        if (bindInfo instanceof CustomerProvider) {
            CustomerProvider<?> adapter = (CustomerProvider<?>) bindInfo;
            instanceProvider = adapter.getCustomerProvider();
        }
        //可能存在的 ScopeProvider
        if (bindInfo instanceof ScopeProvider) {
            ScopeProvider adapter = (ScopeProvider) bindInfo;
            scopeProvider = adapter.getScopeProvider();
        }
        //create Provider.
        if (instanceProvider == null && bindInfo instanceof FactoryBindInfoProviderAdapter == true) {
            final FactoryBindInfoProviderAdapter<T> infoAdapter = (FactoryBindInfoProviderAdapter<T>) bindInfo;
            instanceProvider = new Provider<Object>() {
                public Object get() {
                    Object object = createProvider(infoAdapter, bindInfo, container, appContext);
                    return doAfter(object, bindInfo, appContext);
                }
            };
        } else if (instanceProvider == null) {
            instanceProvider = new Provider<Object>() {
                public Object get() {
                    Object object = getDefaultInstance(bindInfo.getBindType(), container, appContext);
                    return doAfter(object, bindInfo, appContext);
                }
            };
        }
        //scope
        if (scopeProvider != null) {
            instanceProvider = scopeProvider.get().scope(bindInfo, instanceProvider);
        }
        return (T) instanceProvider.get();
    }
    //
    /**创建一个未绑定过的类型*/
    public <T> T getDefaultInstance(final Class<T> oriType, DefineContainer container, AppContext appContext) {
        if (oriType == null) {
            return null;
        }
        try {
            int modifiers = oriType.getModifiers();
            if (oriType.isInterface() || oriType.isEnum() || (modifiers == (modifiers | Modifier.ABSTRACT))) {
                return null;
            }
            if (oriType.isPrimitive()) {
                return (T) BeanUtils.getDefaultValue(oriType);
            }
            if (oriType.isArray()) {
                Class<?> comType = oriType.getComponentType();
                return (T) Array.newInstance(comType, 0);
            }
            return (T) doAfter(createObject(oriType, container, appContext), null, appContext);
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    //
    //
    private Object createProvider(FactoryBindInfoProviderAdapter<?> infoAdapter, BindInfo<?> bindInfo, DefineContainer container, AppContext appContext) {
        try {
            //1.准备Aop
            List<BindInfo<AopBindInfoAdapter>> aopBindList = container.getBindInfoByType(AopBindInfoAdapter.class);
            List<AopBindInfoAdapter> aopList = new ArrayList<AopBindInfoAdapter>();
            for (BindInfo<AopBindInfoAdapter> info : aopBindList) {
                aopList.add(this.getInstance(info, container, appContext));
            }
            //2.动态代理
            AopClassConfig cc = infoAdapter.buildEngine(aopList);
            Class<?> newType = null;
            if (cc.hasChange() == true) {
                newType = cc.toClass();
            } else {
                newType = cc.getSuperClass();
            }
            //3.创建对象
            return createObject(newType, container, appContext);
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    /**创建对象*/
    private Object createObject(Class<?> targetType, DefineContainer container, AppContext appContext) throws Throwable {
        Object targetBean = targetType.newInstance();
        return targetBean;
    }
    private Object doAfter(Object targetBean, BindInfo<?> bindInfo, AppContext appContext) {
        try {
            //1.Aware接口的执行
            if (targetBean instanceof BindInfoAware) {
                ((BindInfoAware) targetBean).setBindInfo(bindInfo);
            }
            if (targetBean instanceof AppContextAware) {
                ((AppContextAware) targetBean).setAppContext(appContext);
            }
            //2.依赖注入
            if (targetBean instanceof InjectMembers) {
                ((InjectMembers) targetBean).doInject(appContext);
            } else {
                /*注解注入*/
                List<Field> fieldList = BeanUtils.findALLFields(bindInfo.getBindType());
                for (Field field : fieldList) {
                    if (field.isAnnotationPresent(Inject.class) == false) {
                        continue;
                    }
                    field.setAccessible(true);
                    Object obj = appContext.getInstance(field.getType());
                    if (obj != null) {
                        field.set(targetBean, obj);
                    }
                }
                /*配置注入*/
                if (bindInfo instanceof DefaultBindInfoProviderAdapter) {
                    DefaultBindInfoProviderAdapter
                    //
                }
            }
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
        return targetBean;
    }
}