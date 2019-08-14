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
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.reflect.ConstructorUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 *
 * @version : 2014年7月4日
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultBindInfoProviderAdapter<T> extends AbstractBindInfoProviderAdapter<T> {
    private Map<Integer, ParamInfo> constructorParams;
    private Map<String, ParamInfo>  injectProperty;
    private boolean                 overwriteAnnotation;
    private String                  initMethod;
    private String                  destroyMethod;

    public DefaultBindInfoProviderAdapter() {
        this.injectProperty = new HashMap<>();
        this.constructorParams = new HashMap<>();
    }

    public DefaultBindInfoProviderAdapter(Class<T> bindingType) {
        this();
        this.setBindID(UUID.randomUUID().toString().replace("-", ""));
        this.setBindType(bindingType);
    }

    @Override
    public void setConstructor(final int index, final Class<?> paramType, final Supplier<?> valueProvider) {
        Objects.requireNonNull(paramType, "paramType parameter is null.");
        Objects.requireNonNull(valueProvider, "valueProvider parameter is null.");
        this.constructorParams.put(index, new ParamInfo(paramType, valueProvider));
    }

    @Override
    public void setConstructor(final int index, final Class<?> paramType, final BindInfo<?> valueInfo) {
        Objects.requireNonNull(paramType, "paramType parameter is null.");
        Objects.requireNonNull(valueInfo, "valueInfo parameter is null.");
        this.constructorParams.put(index, new ParamInfo(paramType, valueInfo));
    }

    @Override
    public void addInject(final String property, final Supplier<?> valueProvider) {
        Objects.requireNonNull(property, "property parameter is null.");
        Objects.requireNonNull(valueProvider, "valueProvider parameter is null.");
        Class<?> propertyType = Objects.requireNonNull(lookupPropertyType(property), "not found '" + property + "' property.");
        this.injectProperty.put(property, new ParamInfo(propertyType, valueProvider));
    }

    @Override
    public void addInject(final String property, final BindInfo<?> valueInfo) {
        Objects.requireNonNull(property, "paramType parameter is null.");
        Objects.requireNonNull(valueInfo, "valueInfo parameter is null.");
        Class<?> propertyType = Objects.requireNonNull(lookupPropertyType(property), "not found '" + property + "' property.");
        this.injectProperty.put(property, new ParamInfo(propertyType, valueInfo));
    }

    private Class<?> lookupPropertyType(String propertyName) {
        return BeanUtils.getPropertyOrFieldType(lookupType(), propertyName);
    }

    private ConstructorInfo genConstructorInfo(AppContext appContext) {
        ArrayList<Integer> ints = new ArrayList<>(constructorParams.keySet());
        Collections.sort(ints);
        //check
        int size = ints.size();
        if (!ints.isEmpty() && ints.get(size - 1) != (size - 1)) {
            throw new java.lang.IllegalStateException("Constructor param index error.");
        }
        //
        Class<?>[] types = new Class<?>[size];
        Supplier<?>[] providers = new Supplier<?>[size];
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
    public Constructor<?> getConstructor(Class<?> targetClass, AppContext appContext) {
        Constructor<?> c = ConstructorUtils.getAccessibleConstructor(targetClass, genConstructorInfo(appContext).types);
        if (c == null) {
            c = targetClass.getConstructors()[0];
        }
        return c;
    }

    /**获得需要IoC的属性列表*/
    public Supplier<?>[] getConstructorParams(AppContext appContext) {
        return genConstructorInfo(appContext).providers;
    }

    /**获得需要IoC的属性列表*/
    public Map<String, Supplier<?>> getPropertys(AppContext appContext) {
        Map<String, Supplier<?>> propertys = new HashMap<>();
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

    @Override
    public void destroyMethod(String methodName) {
        this.destroyMethod = methodName;
    }

    @Override
    public void overwriteAnnotation(boolean overwrite) {
        this.overwriteAnnotation = overwrite;
    }

    public boolean isOverwriteAnnotation() {
        return overwriteAnnotation;
    }

    private Class<?> lookupType() {
        Class<?> sourceType = this.getSourceType();
        if (sourceType == null) {
            sourceType = this.getBindType();
        }
        return sourceType;
    }

    /**获得初始化方法。*/
    public Method getInitMethod(Class<?> targetClass) {
        try {
            if (StringUtils.isNotBlank(this.initMethod)) {
                return targetClass.getMethod(this.initMethod);
            }
        } catch (NoSuchMethodException e) {
            logger.error("not found init method " + this.initMethod);
        }
        return null;
    }

    /**获得销毁方法。*/
    public Method getDestroyMethod(Class<?> targetClass) {
        try {
            if (StringUtils.isNotBlank(this.destroyMethod)) {
                return targetClass.getMethod(this.destroyMethod);
            }
        } catch (NoSuchMethodException e) {
            logger.error("not found destroy method " + this.destroyMethod);
        }
        return null;
    }
}

class ConstructorInfo {
    public ConstructorInfo(Class<?>[] types, Supplier<?>[] providers) {
        this.types = types;
        this.providers = providers;
    }

    public Class<?>[]    types;
    public Supplier<?>[] providers;
}

class ParamInfo {
    public ParamInfo(Class<?> paramType, Supplier<?> valueProvider) {
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
    public Supplier<?> valueProvider;
}
