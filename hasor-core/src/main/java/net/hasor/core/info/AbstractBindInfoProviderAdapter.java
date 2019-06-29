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
package net.hasor.core.info;
import net.hasor.core.*;
import net.hasor.core.binder.BindInfoBuilder;
import net.hasor.core.spi.BeanCreaterListener;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
/**
 * 用于定义Bean，实现了Bean配置接口{@link BindInfoBuilder}，配置的信息通过{@link BindInfo}接口展现出来。
 * <p>同时实现了{@link CustomerProvider}和{@link ScopeProvider}接口。表示着这个Bean定义支持自定义{@link Supplier}和{@link Scope}。
 * @version : 2014年7月3日
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractBindInfoProviderAdapter<T> extends MetaDataAdapter implements//
        BindInfoBuilder<T>, BindInfo<T>, CustomerProvider<T>, ScopeProvider {
    protected static Logger                                           logger           = LoggerFactory.getLogger(AbstractBindInfoProviderAdapter.class);
    //1.基本属性
    private          String                                           bindID           = null;
    private          String                                           bindName         = null;
    private          Class<T>                                         bindType         = null;
    private          Class<? extends T>                               sourceType       = null;
    //2.系统属性
    private          Supplier<? extends T>                            customerProvider = null;
    private          Supplier<Scope>                                  scopeProvider    = null;
    private          List<Supplier<? extends BeanCreaterListener<?>>> createrListener  = null;
    //
    public String getBindID() {
        if (this.bindID == null) {
            this.bindID = this.bindType.getName() + "#" + this.bindName;
        }
        return this.bindID;
    }
    //
    public String getBindName() {
        return this.bindName;
    }
    //
    /** 调用 bindType 使用的类型。 */
    public Class<T> getBindType() {
        return this.bindType;
    }
    //
    /** 调用 bindType 之后，并通过 to 方法明确指明的具体实现类。*/
    public Class<? extends T> getSourceType() {
        return this.sourceType;
    }
    //
    /**获取 {@link #setCustomerProvider(Supplier)} 方法设置的 Provider 对象。*/
    public Supplier<? extends T> getCustomerProvider() {
        return this.customerProvider;
    }
    public Supplier<Scope> getCustomerScopeProvider() {
        return this.scopeProvider;
    }
    public List<Supplier<? extends BeanCreaterListener<?>>> getCreaterListener() {
        return createrListener;
    }
    public BindInfo<T> toInfo() {
        return this;
    }
    //
    public void setBindID(String newID) {
        if (StringUtils.isBlank(newID)) {
            throw new NullPointerException("newID is null.");
        }
        // 发个消息出来给 BeanContainer，让它来检测是否重复。
        this.notify(new NotifyData("bindID", this.bindID, newID));
        this.bindID = newID;
    }
    public void setBindName(final String bindName) {
        // 发个消息出来给 BeanContainer，让它来检测是否重复。
        this.notify(new NotifyData("bindName", this.bindName, bindName));
        this.bindName = bindName;
    }
    public void setBindType(final Class<T> bindType) {
        // 发个消息出来给 BeanContainer，让它来检测是否重复。
        this.notify(new NotifyData("bindType", this.bindType, bindType));
        this.bindType = bindType;
    }
    public void setSourceType(final Class<? extends T> sourceType) {
        // 发个消息出来给 BeanContainer，让它来检测是否重复。
        this.notify(new NotifyData("sourceType", this.sourceType, sourceType));
        this.sourceType = sourceType;
    }
    public void setCustomerProvider(final Supplier<? extends T> customerProvider) {
        // 发个消息出来给 BeanContainer，让它来检测是否重复。
        this.notify(new NotifyData("customerProvider", this.customerProvider, customerProvider));
        this.customerProvider = customerProvider;
    }
    public void setScopeProvider(final Supplier<Scope> scopeProvider) {
        // 发个消息出来给 BeanContainer，让它来检测是否重复。
        this.notify(new NotifyData("scopeProvider", this.scopeProvider, scopeProvider));
        this.scopeProvider = scopeProvider;
    }
    public void addCreaterListener(Supplier<? extends BeanCreaterListener<?>> createrListener) {
        // 发个消息出来给 BeanContainer，让它来检测是否重复。
        this.notify(new NotifyData("createrListener", this.createrListener, createrListener));
        if (this.createrListener == null) {
            this.createrListener = new ArrayList<>();
        }
        this.createrListener.add(createrListener);
    }
}