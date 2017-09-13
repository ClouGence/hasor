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
package net.hasor.core.info;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
/**
 *
 * @version : 2014年7月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultBindInfoProviderAdapter<T> extends AbstractBindInfoProviderAdapter<T> {
    private Map<Integer, ParamInfo> constructorParams;
    private Map<String, ParamInfo>  injectProperty;
    private String                  initMethod;
    //
    public DefaultBindInfoProviderAdapter() {
        this.injectProperty = new HashMap<String, ParamInfo>();
        this.constructorParams = new HashMap<Integer, ParamInfo>();
    }
    public DefaultBindInfoProviderAdapter(Class<T> bindingType) {
        this();
        this.setBindID(UUID.randomUUID().toString());
        this.setBindType(bindingType);
    }
    @Override
    public void setConstructor(final int index, final Class<?> paramType, final Provider<?> valueProvider) {
        Hasor.assertIsNotNull(paramType, "paramType parameter is null.");
        Hasor.assertIsNotNull(valueProvider, "valueProvider parameter is null.");
        this.constructorParams.put(index, new ParamInfo(paramType, valueProvider));
    }
    @Override
    public void setConstructor(final int index, final Class<?> paramType, final BindInfo<?> valueInfo) {
        Hasor.assertIsNotNull(paramType, "paramType parameter is null.");
        Hasor.assertIsNotNull(valueInfo, "valueInfo parameter is null.");
        this.constructorParams.put(index, new ParamInfo(paramType, valueInfo));
    }
    @Override
    public void addInject(final String property, final Provider<?> valueProvider) {
        Hasor.assertIsNotNull(property, "property parameter is null.");
        Hasor.assertIsNotNull(valueProvider, "valueProvider parameter is null.");
        this.injectProperty.put(property, new ParamInfo(null, valueProvider));
    }
    @Override
    public void addInject(final String property, final BindInfo<?> valueInfo) {
        Hasor.assertIsNotNull(property, "paramType parameter is null.");
        Hasor.assertIsNotNull(valueInfo, "valueInfo parameter is null.");
        this.injectProperty.put(property, new ParamInfo(null, valueInfo));
    }
    //
    //
    private ConstructorInfo genConstructorInfo(AppContext appContext) throws NoSuchMethodException, SecurityException {
        ArrayList<Integer> ints = new ArrayList<Integer>(constructorParams.keySet());
        Collections.sort(ints);
        //check
        int size = ints.size();
        if (!ints.isEmpty() && ints.get(size - 1) != (size - 1)) {
            throw new java.lang.IllegalStateException("Constructor param index error.");
        }
        //
        Class<?>[] types = new Class<?>[size];
        Provider<?>[] providers = new Provider<?>[size];
        for (Integer val : ints) {
            ParamInfo pinfo = constructorParams.get(val);
            types[val] = pinfo.paramType;
            if (pinfo.useProvider) {
                providers[val] = pinfo.valueProvider;
            } else {
                providers[val] = appContext.getProvider(pinfo.valueInfo);
            }
        }
        return new ConstructorInfo(types, providers);
    }
    /**获得需要IoC的属性列表*/
    public Constructor<?> getConstructor(Class<?> targetClass, AppContext appContext) throws NoSuchMethodException, SecurityException {
        Class<?>[] types = genConstructorInfo(appContext).types;
        return targetClass.getConstructor(types);
    }
    /**获得需要IoC的属性列表*/
    public Provider<?>[] getConstructorParams(Class<?> targetClass, AppContext appContext) throws NoSuchMethodException, SecurityException {
        return genConstructorInfo(appContext).providers;
    }
    /**获得需要IoC的属性列表*/
    public Map<String, Provider<?>> getPropertys(AppContext appContext) {
        Map<String, Provider<?>> propertys = new HashMap<String, Provider<?>>();
        for (Entry<String, ParamInfo> ent : injectProperty.entrySet()) {
            String propKey = ent.getKey();
            ParamInfo propVal = ent.getValue();
            if (propVal == null) {
                continue;
            }
            if (propVal.useProvider) {
                propertys.put(propKey, propVal.valueProvider);
            } else {
                propertys.put(propKey, appContext.getProvider(propVal.valueInfo));
            }
        }
        return propertys;
    }
    @Override
    public void initMethod(String methodName) {
        this.initMethod = methodName;
    }
    /**获得初始化方法。*/
    public Method getInitMethod(Class<?> targetClass) {
        try {
            if (StringUtils.isNotBlank(this.initMethod)) {
                return targetClass.getMethod(this.initMethod);
            }
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
        return null;
    }
}
//
//
class ConstructorInfo {
    public ConstructorInfo(Class<?>[] types, Provider<?>[] providers) {
        this.types = types;
        this.providers = providers;
    }
    public Class<?>[]    types;
    public Provider<?>[] providers;
}
class ParamInfo {
    public ParamInfo(Class<?> paramType, Provider<?> valueProvider) {
        this.paramType = paramType;
        this.valueProvider = valueProvider;
        this.useProvider = true;
    }
    public ParamInfo(Class<?> paramType, BindInfo<?> valueInfo) {
        this.paramType = paramType;
        this.valueInfo = valueInfo;
        this.useProvider = false;
    }
    public Class<?>    paramType;
    public boolean     useProvider;
    public BindInfo<?> valueInfo;
    public Provider<?> valueProvider;
}
