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
package net.hasor.rsf.container;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.info.CustomerProvider;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.address.RouteTypeEnum;
import net.hasor.rsf.domain.ServiceDomain;
import net.hasor.rsf.domain.warp.RsfBindInfoWrap;
import net.hasor.rsf.utils.StringUtils;

import java.util.*;
/**
 * 服务对象，封装了服务元信息、RsfFilter、服务提供者（如果有）。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
class ServiceDefine<T> extends RsfBindInfoWrap<T> implements CustomerProvider<T>, RsfBindInfo<T>, RsfDomainProvider<T> {
    private final List<FilterDefine>         filterList;
    private       Provider<? extends T>      customerProvider;
    private       String                     oriFlowControl;
    private final Map<RouteTypeEnum, String> oriRouteScript;
    private final Set<InterAddress>          oriAddressSet;
    //
    public ServiceDefine(Class<T> bindType) {
        this(new ServiceDomain<T>(Hasor.assertIsNotNull(bindType)));
    }
    public ServiceDefine(ServiceDomain<T> domain) {
        super(Hasor.assertIsNotNull(domain));
        this.filterList = new ArrayList<FilterDefine>();
        this.oriRouteScript = new HashMap<RouteTypeEnum, String>();
        this.oriAddressSet = new HashSet<InterAddress>();
    }
    /**添加Filter*/
    public void addRsfFilter(FilterDefine filterDefine) {
        Hasor.assertIsNotNull(filterDefine.filterID());
        for (FilterDefine filterDef : this.filterList) {
            String defFilterID = filterDef.filterID();
            String filterID = filterDefine.filterID();
            //
            if (filterID.equals(defFilterID)) {
                return;
            }
        }
        this.filterList.add(filterDefine);
    }
    /**获取服务上配置有效的过滤器*/
    public List<FilterDefine> getFilterSnapshots() {
        return Collections.unmodifiableList(this.filterList);
    }
    /**获取服务提供者。*/
    @Override
    public Provider<? extends T> getCustomerProvider() {
        return this.customerProvider;
    }
    public void setCustomerProvider(Provider<? extends T> customerProvider) {
        this.customerProvider = customerProvider;
    }
    //
    public void addAddress(InterAddress rsfAddress) {
        this.oriAddressSet.add(Hasor.assertIsNotNull(rsfAddress));
    }
    public Set<InterAddress> getAddressSet() {
        return Collections.unmodifiableSet(this.oriAddressSet);
    }
    //
    /** 获得流控策略 */
    public String getFlowControl() {
        return this.oriFlowControl;
    }
    public void setFlowControl(String oriFlowControl) {
        this.oriFlowControl = oriFlowControl;
    }
    //
    /** 设置路由策略 */
    public void setRouteScript(RouteTypeEnum routeType, String scriptBody) {
        if (routeType == null || StringUtils.isBlank(scriptBody)) {
            return;
        }
        this.oriRouteScript.put(routeType, scriptBody);
    }
    public Map<RouteTypeEnum, String> getRouteScript() {
        return Collections.unmodifiableMap(this.oriRouteScript);
    }
    //
    /**获取服务元信息。*/
    public ServiceDomain<T> getDomain() {
        return (ServiceDomain<T>) this.getTarget();
    }
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder("");
        List<FilterDefine> defines = this.filterList;
        if (defines == null) {
            buffer.append(" null");
        } else {
            for (FilterDefine define : defines) {
                buffer.append(define.filterID());
                buffer.append(",");
            }
        }
        return "ServiceDefine[Domain=" + this.getTarget() + ",Filters=" + buffer.toString() + "]";
    }
}