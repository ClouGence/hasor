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
import net.hasor.core.provider.InstanceProvider;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSource;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * UDF UdfSource 的装配接口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-11
 */
public interface UdfSourceAssembly extends UdfSource {
    @Override
    public default Supplier<Map<String, Udf>> getUdfResource(Finder finder) {
        Class<?> targetType = getClass();
        Supplier<?> supplier = getSupplier(targetType, finder);
        Predicate<Method> predicate = getPredicate(targetType);
        return InstanceProvider.of(new TypeUdfMap(targetType, supplier, predicate));
    }

    public default Supplier<?> getSupplier(Class<?> targetType, Finder finder) {
        return () -> this;
    }

    public default Predicate<Method> getPredicate(Class<?> targetType) {
        return method -> {
            boolean testA = method.getDeclaringClass() != Object.class;
            boolean testB = method.getDeclaringClass() != UdfSource.class;
            boolean testC = method.getDeclaringClass() != UdfSourceAssembly.class;
            return testA && testB && testC;
        };
    }
}