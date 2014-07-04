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
    private String              bindName         = null;
    private Class<T>            bindType         = null;
    private Class<? extends T>  sourceType       = null;
    private boolean             singleton        = false;
    //2.系统属性
    private Provider<T>         instanceProvider = null;
    private Provider<Scope>     scopeProvider    = null;
    private Map<String, Object> metaData         = new HashMap<String, Object>();
    //
    public void setBindName(String bindName) {
        this.bindName = bindName;
    }
    public String getBindName() {
        return this.bindName;
    }
    public Class<T> getBindType() {
        return this.bindType;
    }
    public void setBindType(Class<T> bindType) {
        this.bindType = bindType;
    }
    public void setSourceType(Class<? extends T> sourceType) {
        this.sourceType = sourceType;
    }
    public Class<? extends T> getSourceType() {
        return this.sourceType;
    }
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
    public boolean isSingleton() {
        return this.singleton;
    }
    public void setMetaData(String key, Object value) {
        this.metaData.put(key, value);
    }
    public Object getMetaData(String key) {
        return this.metaData.get(key);
    }
    public void setProvider(Provider<T> instanceProvider) {
        this.instanceProvider = instanceProvider;
    }
    public Provider<T> getProvider() {
        return this.instanceProvider;
    }
    public void setScopeProvider(Provider<Scope> scopeProvider) {
        this.scopeProvider = scopeProvider;
    }
    public Provider<Scope> getScopeProvider() {
        return this.scopeProvider;
    }
}