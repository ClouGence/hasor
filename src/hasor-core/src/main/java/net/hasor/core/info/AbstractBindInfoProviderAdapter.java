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
package net.hasor.core.info;
import java.util.HashMap;
import java.util.Map;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoBuilder;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2014年7月3日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractBindInfoProviderAdapter<T> implements BindInfoBuilder<T>, BindInfo<T> {
    //1.基本属性
    private String              bindID           = null;
    private String              bindName         = null;
    private Class<T>            bindType         = null;
    private Class<? extends T>  sourceType       = null;
    private boolean             singleton        = false;
    //2.系统属性
    private Provider<T>         customerProvider = null;
    private Provider<Scope>     scopeProvider    = null;
    private Map<String, Object> metaData         = new HashMap<String, Object>();
    //
    public String getBindID() {
        return this.bindID;
    }
    public void setBindID(String newID) {
        if (StringUtils.isBlank(newID) == true) {
            throw new NullPointerException("newID is null.");
        }
        this.bindID = newID;
    }
    public void setBindName(final String bindName) {
        this.bindName = bindName;
    }
    public String getBindName() {
        return this.bindName;
    }
    public Class<T> getBindType() {
        return this.bindType;
    }
    public void setBindType(final Class<T> bindType) {
        this.bindType = bindType;
    }
    public void setSourceType(final Class<? extends T> sourceType) {
        this.sourceType = sourceType;
    }
    public Class<? extends T> getSourceType() {
        return this.sourceType;
    }
    public void setSingleton(final boolean singleton) {
        this.singleton = singleton;
    }
    public boolean isSingleton() {
        return this.singleton;
    }
    public void setMetaData(final String key, final Object value) {
        this.metaData.put(key, value);
    }
    public Object getMetaData(final String key) {
        return this.metaData.get(key);
    }
    public void setCustomerProvider(final Provider<T> customerProvider) {
        this.customerProvider = customerProvider;
    }
    /***/
    public Provider<T> getCustomerProvider() {
        return this.customerProvider;
    }
    public void setScopeProvider(final Provider<Scope> scopeProvider) {
        this.scopeProvider = scopeProvider;
    }
    public Provider<Scope> getScopeProvider() {
        return this.scopeProvider;
    }
    public BindInfo<T> toInfo() {
        return this;
    }
}