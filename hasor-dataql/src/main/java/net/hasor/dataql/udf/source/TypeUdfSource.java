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
package net.hasor.dataql.udf.source;
import net.hasor.dataql.Option;
import net.hasor.dataql.UDF;
import net.hasor.dataql.udf.SimpleUdfSource;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.convert.ConverterUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
/**
 * 支持把某个Bean的所有方法都注册成为 UDF。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class TypeUdfSource<T> extends SimpleUdfSource {
    public TypeUdfSource(Class<T> utilType, TypeProvider<T> provider, TypeMatcher<Method> methodTypeMatcher) {
        List<Method> methodList = BeanUtils.getMethods(utilType);
        for (Method method : methodList) {
            initMethod(provider, methodTypeMatcher, method);
        }
    }
    private void initMethod(TypeProvider<T> provider, TypeMatcher<Method> methodTypeMatcher, Method method) {
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
        if (methodTypeMatcher != null && !methodTypeMatcher.matches(method)) {
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
            this.put(method.getName(), new StaticUDF(method));
        } else {
            this.put(method.getName(), new ObjectUDF(method, provider));
        }
    }
    //
    private static Object doInvoke(Method targetMethod, Object target, Object[] values, Option readOnly) {
        try {
            Class<?>[] parameterTypes = targetMethod.getParameterTypes();
            Object[] inData = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Object paramData;
                if (Option.class.isAssignableFrom(parameterTypes[i])) {
                    paramData = readOnly;
                } else {
                    paramData = (i < values.length) ? values[i] : null;
                }
                //
                inData[i] = ConverterUtils.convert(parameterTypes[i], paramData);
            }
            return targetMethod.invoke(target, inData);
        } finally {
            return null;
        }
    }
    //
    private static class StaticUDF implements UDF {
        private Method target;
        public StaticUDF(Method target) {
            this.target = target;
        }
        @Override
        public Object call(Object[] values, Option readOnly) throws Throwable {
            return doInvoke(target, null, values, readOnly);
        }
    }
    private static class ObjectUDF implements UDF {
        private Method          target;
        private TypeProvider<?> provider;
        public ObjectUDF(Method target, TypeProvider<?> provider) {
            this.target = target;
            this.provider = provider;
        }
        @Override
        public Object call(Object[] values, Option readOnly) throws Throwable {
            Object targetObject = this.provider.get();
            if (targetObject == null) {
                throw new NullPointerException("target Object is null.");
            }
            return doInvoke(target, targetObject, values, readOnly);
        }
    }
}