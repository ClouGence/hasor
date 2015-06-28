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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.InjectMembers;
import net.hasor.core.Provider;
import net.hasor.core.context.BeanBuilder;
import net.hasor.core.context.DefineContainer;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.info.AopBindInfoAdapter;
import net.hasor.core.info.CustomerProvider;
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
    public <T> T getInstance(BindInfo<T> bindInfo, DefineContainer container, AppContext appContext) {
        if (bindInfo instanceof CustomerProvider) {
            //1.可能存在的 CustomerProvider。 
            CustomerProvider<T> adapter = (CustomerProvider<T>) bindInfo;
            Provider<T> provider = adapter.getCustomerProvider();
            if (provider != null) {
                return provider.get();
            }
        }
        //
        //2.HasorBindInfoProviderAdapter 类型。
        if (bindInfo instanceof FactoryBindInfoProviderAdapter == true) {
            //3.Build 对象。
            FactoryBindInfoProviderAdapter<T> infoAdapter = (FactoryBindInfoProviderAdapter<T>) bindInfo;
            try {
                List<BindInfo<AopBindInfoAdapter>> aopBindList = container.getBindInfoByType(AopBindInfoAdapter.class);
                List<AopBindInfoAdapter> aopList = new ArrayList<AopBindInfoAdapter>();
                for (BindInfo<AopBindInfoAdapter> info : aopBindList) {
                    aopList.add(this.getInstance(info, container, appContext));
                }
                AopClassConfig cc = infoAdapter.buildEngine(aopList);
                Class<?> newType = null;
                if (cc.hasChange() == true) {
                    newType = cc.toClass();
                } else {
                    newType = cc.getSuperClass();
                }
                Object targetBean = createObject(newType, container, appContext);
                //                if (targetBean instanceof BindInfoAware) {
                //                    ((BindInfoAware) targetBean).setBindInfo(bindInfo);;
                //                }
                return (T) targetBean;
            } catch (Exception e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }
        return getDefaultInstance(bindInfo.getBindType(), container, appContext);
    }
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
            return createObject(oriType, container, appContext);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    /**创建对象*/
    protected <T> T createObject(Class<T> targetType, DefineContainer container, AppContext appContext) throws Exception {
        T targetBean = targetType.newInstance();
        if (targetBean instanceof InjectMembers) {
            ((InjectMembers) targetBean).doInject(appContext);
        }
        return targetBean;
    }
}