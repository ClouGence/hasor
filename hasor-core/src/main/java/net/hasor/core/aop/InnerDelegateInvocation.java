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
package net.hasor.core.aop;
import net.hasor.core.PropertyDelegate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 *
 * @version : 2020-09-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class InnerDelegateInvocation {
    private final static Map<String, PropertyDelegate> methodMapping = new ConcurrentHashMap<>();

    static class DelegateInfo {
        Supplier<? extends PropertyDelegate> delegateSupplier;
        Class<?>                             propertyType;
        ReadWriteType                        rwType;

        public DelegateInfo(Class<?> propertyType, Supplier<? extends PropertyDelegate> delegateSupplier, ReadWriteType rwType) {
            this.propertyType = propertyType;
            this.delegateSupplier = delegateSupplier;
            this.rwType = rwType;
        }
    }

    private static PropertyDelegate init(String cacheKey, Object target, String name) {
        return methodMapping.computeIfAbsent(cacheKey, s -> {
            Class<?> targetClass = target.getClass();
            ClassLoader loader = targetClass.getClassLoader();
            if (loader instanceof AopClassLoader) {
                AopClassConfig cc = ((AopClassLoader) loader).findClassConfig(targetClass.getName());
                return cc.findPropertyDelegate(name).get();
            } else {
                throw new IllegalArgumentException(cacheKey + " missing Delegate.");
            }
        });
    }

    public static void setProperty(String cacheKey, Object target, String name, Object newValue) throws Throwable {
        init(cacheKey, target, name).set(target, newValue);
    }

    public static Object getProperty(String cacheKey, Object target, String name) throws Throwable {
        return init(cacheKey, target, name).get(target);
    }
}