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
package net.hasor.core.binder.register;
import java.lang.reflect.Constructor;
import javax.inject.Provider;
import net.hasor.core.Scope;
import net.hasor.core.binder.TypeRegister;
/**
 * 
 * @version : 2014-3-20
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractTypeRegister<T> implements TypeRegister<T> {
    private String                   name            = null;
    private Class<T>                 type            = null;
    private Scope                    scope           = null;
    private boolean                  isSingleton     = false;
    //
    private Provider<T>              provider        = null;
    private Class<? extends T>       implType        = null;
    private Constructor<? extends T> implConstructor = null;
    //
    //
    //
    public AbstractTypeRegister(Class<T> type) {
        this.type = type;
    }
    public String toString() {
        return String.format("name = %s ,Type = %s ", name, type);
    }
    public Class<T> getType() {
        return this.type;
    }
    protected void setType(Class<T> type) {
        this.type = type;
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
    public void setScope(Scope scope) {
        this.scope = scope;
    }
    public Scope getScope() {
        return this.scope;
    }
    public Provider<T> getProvider() {
        return this.provider;
    }
    public void toImpl(Class<? extends T> implType) {
        this.implType = implType;
    }
    public void toConstructor(Constructor<? extends T> implConstructor) {
        this.implConstructor = implConstructor;
    }
    /**获取实现类*/
    public Class<? extends T> getImplType() {
        return implType;
    }
    /**获取用于创建该类的构造方法*/
    public Constructor<? extends T> getImplConstructor() {
        return implConstructor;
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