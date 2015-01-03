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
package net.hasor.rsf.plugins.hasor;
import java.net.MalformedURLException;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.adapter.AbstractRsfContext;
import net.hasor.rsf.remoting.binder.RsfBindBuilder;
import net.hasor.rsf.remoting.binder.RsfBindBuilder.LinkedBuilderImpl;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AbstractRsfApiBinder extends ApiBinderWrap implements RsfApiBinder {
    private RsfContext rsfContext = null;
    private RsfBinder  rsfBinder  = null;
    protected AbstractRsfApiBinder(ApiBinder apiBinder, RsfContext rsfContext) {
        super(apiBinder);
        this.rsfContext = rsfContext;
        this.rsfBinder = rsfContext.getBindCenter().getRsfBinder();
    }
    public <T> Provider<T> toProvider(BindInfo<T> bindInfo) {
        // TODO Auto-generated method stub
        return null;
    }
    //
    public void bindAddress(String remoteHost, int remotePort) throws MalformedURLException {
        this.rsfBinder.bindAddress(remoteHost, remotePort);
    }
    public void bindFilter(String id, RsfFilter instance) {
        this.bindType(id, RsfFilter.class, instance);
        this.rsfBinder.bindFilter(id, instance);
    }
    public void bindFilter(String id, Provider<RsfFilter> provider) {
        this.bindType(id, RsfFilter.class, provider);
        this.rsfBinder.bindFilter(id, provider);
    }
    public <T> LinkedBuilder<T> rsfService(Class<T> type) {
        return new HasorLinkedBuilderImpl<T>(this, (RsfBindBuilder) this.rsfBinder, type, (AbstractRsfContext) this.rsfContext);
    }
    public <T> NamedBuilder<T> rsfService(Class<T> type, T instance) {
        BindInfo<T> bindInfo = this.bindType(type, instance).toInfo();
        return this.rsfBinder.rsfService(type, this.toProvider(bindInfo));
    }
    public <T> NamedBuilder<T> rsfService(Class<T> type, Class<? extends T> implementation) {
        BindInfo<T> bindInfo = this.bindType(type, implementation).toInfo();
        return this.rsfBinder.rsfService(type, this.toProvider(bindInfo));
    }
    public <T> NamedBuilder<T> rsfService(Class<T> type, Provider<T> provider) {
        BindInfo<T> bindInfo = this.bindType(type, provider).toInfo();
        return this.rsfBinder.rsfService(type, this.toProvider(bindInfo));
    }
    //
    private class HasorLinkedBuilderImpl<T> extends LinkedBuilderImpl<T> {
        private ApiBinder apiBinder = null;
        protected HasorLinkedBuilderImpl(ApiBinder apiBinder, RsfBindBuilder rsfBindBuilder, Class<T> serviceType, AbstractRsfContext rsfContext) {
            rsfBindBuilder.super(serviceType, rsfContext);
            this.apiBinder = apiBinder;
        }
    }
}