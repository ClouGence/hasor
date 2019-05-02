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
package net.hasor.core.scope;
import net.hasor.core.Scope;
import net.hasor.core.provider.SingleProvider;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
/**
 * 单例
 * @version : 2015年6月28日
 * @author 赵永春 (zyc@hasor.net)
 */
public class SingletonScope implements Scope {
    private ConcurrentHashMap<Object, Supplier<?>> scopeMap = new ConcurrentHashMap<>();
    public <T> Supplier<T> scope(Object key, final Supplier<T> provider) {
        Supplier<?> returnData = this.scopeMap.get(key);
        if (returnData == null) {
            Supplier<T> newSingleProvider = new SingleProvider<T>(provider);
            returnData = this.scopeMap.putIfAbsent(key, newSingleProvider);
            if (returnData == null) {
                returnData = newSingleProvider;
            }
        }
        return (Supplier<T>) returnData;
    }
    public Map<Object, Supplier<?>> getSingletonData() {
        return Collections.unmodifiableMap(this.scopeMap);
    }
}