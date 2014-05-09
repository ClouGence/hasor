/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.core.binder.register;
import java.lang.reflect.Constructor;
import javax.inject.Provider;
import net.hasor.core.RegisterInfo;
import net.hasor.core.binder.TypeRegister;
/**
 * 
 * @version : 2014-3-20
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public abstract class AbstractTypeRegister<T> implements TypeRegister<T>, RegisterInfo<T> {
    private String               name        = null;
    private Class<T>             type        = null;
    private String               scope       = null;
    private boolean              isSingleton = false;
    private volatile Provider<T> provider    = null;
    //
    public AbstractTypeRegister(Class<T> type) {
        this.type = type;
    }
    public Class<T> getType() {
        return this.type;
    }
    public void toInstance(final T instance) {
        this.toProvider(new ProviderInstance(instance));
        this.setSingleton();
    }
    public void toProvider(Provider<T> provider) {
        this.provider = provider;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    public void setSingleton() {
        this.isSingleton = true;
    }
    public boolean isSingleton() {
        return this.isSingleton;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public String getScope() {
        return this.scope;
    }
    public Provider<T> getProvider() {
        return this.provider;
    }
    public void toImpl(Class<? extends T> implementation) {
        AbstractFramework factory = this.getPack().getFramework();
        this.createProvider = new ProviderClass(implementation, factory);
    }
    public void toConstructor(Constructor<? extends T> constructor) {
        AbstractFramework factory = this.getPack().getFramework();
        this.createProvider = new ProviderConstructor(constructor, factory);
    }
    //
    /***/
    private class ProviderInstance implements Provider<T> {
        private T instance = null;
        public ProviderInstance(T instance) {
            this.instance = instance;
        }
        public T get() {
            return this.instance;
        }
    }
}