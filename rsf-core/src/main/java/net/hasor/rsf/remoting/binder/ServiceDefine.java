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
package net.hasor.rsf.remoting.binder;
import java.util.Collection;
import java.util.Map;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBinder.RegisterReference;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.adapter.AbstractRsfContext;
import net.hasor.rsf.adapter.RsfBindDefine;
import net.hasor.rsf.domain.ServiceDomain;
/**
 * 服务的描述信息，包括了服务的发布和订阅信息。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
class ServiceDefine<T> implements RsfBindDefine<T>, RegisterReference<T> {
    private ServiceDomain<T>                 serviceDomain;
    private AbstractRsfContext               rsfContext;
    private Provider<RsfFilter>[]            rsfFilterArray;
    private Map<String, Provider<RsfFilter>> rsfFilterMap;
    private Provider<T>                      rsfProvider;
    //
    public ServiceDefine(ServiceDomain<T> serviceDomain, AbstractRsfContext rsfContext, Map<String, Provider<RsfFilter>> rsfFilterMap, Provider<T> rsfProvider) {
        Collection<Provider<RsfFilter>> filterCollection = rsfFilterMap.values();
        Provider<RsfFilter>[] filterArray = filterCollection.toArray(new Provider[filterCollection.size()]);
        //
        this.serviceDomain = serviceDomain;
        this.rsfContext = rsfContext;
        this.rsfFilterArray = filterArray;
        this.rsfFilterMap = rsfFilterMap;
        this.rsfProvider = rsfProvider;
    }
    //
    public Provider<T> getCustomerProvider() {
        return this.rsfProvider;
    }
    public void unRegister() {
        this.rsfContext.getBindCenter().recoverService(this);
    }
    public Provider<RsfFilter>[] getFilterProvider() {
        return this.rsfFilterArray;
    }
    public RsfFilter getFilter(String filterID) {
        Provider<? extends RsfFilter> provider = this.rsfFilterMap.get(filterID);
        return provider == null ? null : provider.get();
    }
    //
    public String getBindID() {
        return this.serviceDomain.getBindID();
    }
    public String getBindName() {
        return this.serviceDomain.getBindName();
    }
    public String getBindGroup() {
        return this.serviceDomain.getBindGroup();
    }
    public String getBindVersion() {
        return this.serviceDomain.getBindVersion();
    }
    public Class<T> getBindType() {
        return this.serviceDomain.getBindType();
    }
    public int getClientTimeout() {
        return this.serviceDomain.getClientTimeout();
    }
    public String getSerializeType() {
        return this.serviceDomain.getSerializeType();
    }
    public Object getMetaData(String key) {
        return this.serviceDomain.getMetaData(key);
    }
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("");
        if (rsfFilterMap != null) {
            for (String fName : rsfFilterMap.keySet()) {
                buffer.append(fName + ",");
            }
        }
        return "ServiceDefine[Domain=" + serviceDomain + ",Filters=" + buffer.toString() + "]";
    }
}