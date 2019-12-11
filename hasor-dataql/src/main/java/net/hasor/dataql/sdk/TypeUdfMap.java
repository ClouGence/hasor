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
package net.hasor.dataql.sdk;
import net.hasor.core.provider.SingleProvider;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;
import net.hasor.dataql.domain.DataModel;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.convert.ConverterUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 支持把某个Bean的所有方法都注册成为 UDF。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class TypeUdfMap extends HashMap<String, Udf> {
    public TypeUdfMap(Class<?> utilType) {
        this(utilType, method -> true);
    }

    public TypeUdfMap(Class<?> utilType, Predicate<Method> methodTypeMatcher) {
        this(utilType, new SingleProvider<>(() -> {
            try {
                return utilType.newInstance();
            } catch (Exception e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }), methodTypeMatcher);
    }

    public TypeUdfMap(Class<?> utilType, Supplier<?> provider, Predicate<Method> methodTypeMatcher) {
        List<Method> methodList = BeanUtils.getMethods(utilType);
        for (Method method : methodList) {
            initMethod(provider, methodTypeMatcher, method);
        }
    }

    private void initMethod(Supplier provider, Predicate<Method> methodTypeMatcher, Method method) {
        int modifiers = method.getModifiers();
        // .必须是共有方法
        if (!Modifier.isPublic(modifiers)) {
            return;
        }
        // .如果是非静态方法必须要有provider
        if (!Modifier.isStatic(modifiers) && provider == null) {
            return;
        }
        // .如果配置了TypeMatcher，那么检测必须通过
        if (methodTypeMatcher != null && !methodTypeMatcher.test(method)) {
            return;
        }
        // .直接来自于 Object 的方法不注册
        if (method.getDeclaringClass() == Object.class) {
            return;
        }
        // .已经存在的不支持重载
        if (this.containsKey(method.getName())) {
            return;
        }
        //
        if (Modifier.isStatic(modifiers)) {
            this.put(method.getName(), new StaticUdf(method));
        } else {
            this.put(method.getName(), new ObjectUdf(method, provider));
        }
    }

    private static Object doInvoke(Method targetMethod, Object target, Object[] values, Hints readOnly) throws Exception {
        Class<?>[] parameterTypes = targetMethod.getParameterTypes();
        Object[] inData = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Object paramData;
            if (Hints.class.isAssignableFrom(parameterTypes[i])) {
                paramData = readOnly;
            } else if (UdfParams.class.isAssignableFrom(parameterTypes[i])) {
                paramData = (UdfParams) () -> values;
            } else {
                paramData = (i < values.length) ? values[i] : null;
            }
            //
            if (paramData instanceof DataModel) {
                paramData = ((DataModel) paramData).asOri();
            }
            //
            inData[i] = ConverterUtils.convert(parameterTypes[i], paramData);
        }
        return targetMethod.invoke(target, inData);
    }

    private static class StaticUdf implements Udf {
        private Method target;

        public StaticUdf(Method target) {
            this.target = target;
        }

        @Override
        public Object call(Hints readOnly, Object... values) throws Throwable {
            return doInvoke(target, null, values, readOnly);
        }
    }

    private static class ObjectUdf implements Udf {
        private Method      target;
        private Supplier<?> provider;

        public ObjectUdf(Method target, Supplier<?> provider) {
            this.target = target;
            this.provider = provider;
        }

        @Override
        public Object call(Hints readOnly, Object... values) throws Throwable {
            Object targetObject = this.provider.get();
            if (targetObject == null) {
                throw new NullPointerException("target Object is null.");
            }
            return doInvoke(target, targetObject, values, readOnly);
        }
    }
}