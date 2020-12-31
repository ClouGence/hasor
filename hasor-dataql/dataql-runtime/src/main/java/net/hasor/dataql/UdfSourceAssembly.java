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
package net.hasor.dataql;
import net.hasor.core.Provider;
import net.hasor.dataql.domain.DataModel;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * UDF UdfSource 的装配接口，请注意：不支持函数重载
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-11
 */
public interface UdfSourceAssembly extends UdfSource {
    public default Supplier<?> getSupplier(Class<?> targetType, Finder finder) {
        return () -> this;// the Supplier return self.
    }

    public default Predicate<Method> getPredicate(Class<?> targetType) {
        return method -> {
            // ignore all method form Object\UdfSource\UdfSourceAssembly
            boolean testA = method.getDeclaringClass() != Object.class;
            boolean testB = method.getDeclaringClass() != UdfSource.class;
            boolean testC = method.getDeclaringClass() != UdfSourceAssembly.class;
            return testA && testB && testC;
        };
    }

    /** 获取所有参数 */
    @FunctionalInterface
    public interface UdfParams {
        public Object[] allParams();
    }

    /** 函数名 */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    public @interface UdfName {
        public String value();
    }

    @Override
    public default Supplier<Map<String, Udf>> getUdfResource(Finder finder) {
        Class<?> targetType = getClass();
        Supplier<?> supplier = getSupplier(targetType, finder);
        Predicate<Method> predicate = getPredicate(targetType);
        return Provider.of(new TypeUdfMap(targetType, supplier, predicate));
    }

    public static class TypeUdfMap extends HashMap<String, Udf> {
        public TypeUdfMap(Class<?> utilType) {
            this(utilType, method -> true);
        }

        public TypeUdfMap(Class<?> utilType, Predicate<Method> methodTypeMatcher) {
            this(utilType, Provider.of((Callable<Object>) utilType::newInstance).asSingle(), methodTypeMatcher);
        }

        public TypeUdfMap(Class<?> utilType, Supplier<?> provider, Predicate<Method> methodTypeMatcher) {
            List<Method> methodList = BeanUtils.getMethods(utilType);
            for (Method method : methodList) {
                initMethod(provider, methodTypeMatcher, method);
            }
        }

        private void initMethod(Supplier<?> provider, Predicate<Method> methodTypeMatcher, Method method) {
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
            // .确定函数名
            UdfName udfName = method.getAnnotation(UdfName.class);
            if (udfName == null) {
                udfName = method.getDeclaringClass().getAnnotation(UdfName.class);
                if (udfName == null) {
                    udfName = new UdfName() {
                        public Class<? extends Annotation> annotationType() {
                            return UdfName.class;
                        }

                        public String value() {
                            return method.getName();
                        }
                    };
                }
            }
            if (StringUtils.isBlank(udfName.value())) {
                throw new NullPointerException("udfName is null -> " + method.toString());
            }
            //
            if (Modifier.isStatic(modifiers)) {
                this.put(udfName.value(), new StaticUdf(method));
            } else {
                this.put(udfName.value(), new ObjectUdf(method, provider));
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
            private final Method target;

            public StaticUdf(Method target) {
                this.target = target;
                this.target.setAccessible(true);
            }

            @Override
            public Object call(Hints readOnly, Object... values) throws Throwable {
                return doInvoke(target, null, values, readOnly);
            }
        }

        private static class ObjectUdf implements Udf {
            private final Method      target;
            private final Supplier<?> provider;

            public ObjectUdf(Method target, Supplier<?> provider) {
                this.target = target;
                this.target.setAccessible(true);
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
}
