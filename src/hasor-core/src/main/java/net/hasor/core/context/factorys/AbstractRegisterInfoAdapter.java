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
package net.hasor.core.context.factorys;
import java.util.HashMap;
import java.util.Map;
import net.hasor.core.Provider;
import net.hasor.core.RegisterInfo;
import net.hasor.core.Scope;
import net.hasor.core.binder.TypeBuilder;
import net.hasor.core.context.adapter.RegisterInfoAdapter;
/**
 * 
 * @version : 2014年7月3日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRegisterInfoAdapter<T> implements RegisterInfoAdapter<T>, TypeBuilder<T> {
    //1.基本属性
    private String                  bindName         = null;
    private Class<T>                bindType         = null;
    private Class<? extends T>      sourceType       = null;
    private boolean                 singleton        = false;
    //2.系统属性
    private Provider<T>             customerProvider = null;
    private Provider<Scope>         scopeProvider    = null;
    private AbstractRegisterFactory factory          = null;
    private Map<String, Object>     metaData         = new HashMap<String, Object>();
    //
    void setFactory(final AbstractRegisterFactory factory) {
        this.factory = factory;
    }
    @Override
    public void setBindName(final String bindName) {
        this.bindName = bindName;
    }
    @Override
    public String getBindName() {
        return this.bindName;
    }
    @Override
    public Class<T> getBindType() {
        return this.bindType;
    }
    public void setBindType(final Class<T> bindType) {
        this.bindType = bindType;
    }
    @Override
    public void setSourceType(final Class<? extends T> sourceType) {
        this.sourceType = sourceType;
    }
    public Class<? extends T> getSourceType() {
        return this.sourceType;
    }
    @Override
    public void setSingleton(final boolean singleton) {
        this.singleton = singleton;
    }
    public boolean isSingleton() {
        return this.singleton;
    }
    @Override
    public void setMetaData(final String key, final Object value) {
        this.metaData.put(key, value);
    }
    @Override
    public Object getMetaData(final String key) {
        return this.metaData.get(key);
    }
    @Override
    public void setCustomerProvider(final Provider<T> customerProvider) {
        this.customerProvider = customerProvider;
    }
    /***/
    public Provider<T> getCustomerProvider() {
        return this.customerProvider;
    }
    @Override
    public Provider<T> getProvider() {
        if (this.customerProvider == null) {
            return new FactoryProvider<T>(this, this.factory);
        }
        return this.customerProvider;
    }
    @Override
    public void setScopeProvider(final Provider<Scope> scopeProvider) {
        this.scopeProvider = scopeProvider;
    }
    public Provider<Scope> getScopeProvider() {
        return this.scopeProvider;
    }
}
/** RegisterInfo的默认的Provider，作用是通过RegisterFactory的getInstance方法来创建对象 */
class FactoryProvider<T> implements Provider<T> {
    private RegisterInfo<T>         adapter = null;
    private AbstractRegisterFactory factory = null;
    public FactoryProvider(final RegisterInfo<T> adapter, final AbstractRegisterFactory factory) {
        this.adapter = adapter;
        this.factory = factory;
    }
    @Override
    public T get() {
        return this.factory.getInstance(this.adapter);
    }
}