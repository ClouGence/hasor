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
package net.hasor.test.core.scope;
import net.hasor.core.Provider;
import net.hasor.core.Scope;

import java.util.HashMap;
import java.util.function.Supplier;

public class HashRemainderScope implements Scope {
    private int                          modulus   = 0;
    private int                          remainder = 0;
    private HashMap<Object, Supplier<?>> scopeMap  = new HashMap<>();

    public HashRemainderScope(int modulus, int remainder) {
        this.modulus = modulus;
        this.remainder = remainder;
    }

    @Override
    public String toString() {
        return "HashRemainderScope{" + "modulus=" + modulus + ", remainder=" + remainder + '}';
    }

    public <T> Supplier<T> scope(Object key, final Supplier<T> provider) {
        Supplier<?> returnData = this.scopeMap.get(key);
        if (returnData == null) {
            T t = provider.get();
            if (t == null) {
                return provider;
            }
            //
            if (t.hashCode() % modulus == remainder) {
                Supplier<T> newSingleProvider = Provider.of(provider).asSingle();
                returnData = this.scopeMap.putIfAbsent(key, newSingleProvider);
                if (returnData == null) {
                    returnData = newSingleProvider;
                }
            } else {
                returnData = provider;
            }
            //
        }
        return (Supplier<T>) returnData;
    }

    public int getModulus() {
        return modulus;
    }

    public int getRemainder() {
        return remainder;
    }

    public HashMap<Object, Supplier<?>> getScopeMap() {
        return scopeMap;
    }
}
