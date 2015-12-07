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
package net.hasor.rsf.container;
import java.util.ArrayList;
import java.util.List;
import org.more.util.StringUtils;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.core.info.CustomerProvider;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.domain.ServiceDomain;
/**
 * 服务对象，封装了服务元信息、RsfFilter、服务提供者（如果有）。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
class ServiceInfo<T> implements CustomerProvider<T> {
    private final ServiceDomain<T>   domain;
    private final List<FilterDefine> filterList;
    private Provider<T>              customerProvider;
    //
    //
    public ServiceInfo(Class<T> bindType) {
        this.domain = new ServiceDomain<T>(Hasor.assertIsNotNull(bindType));
        this.filterList = new ArrayList<FilterDefine>();
    }
    public ServiceInfo(ServiceDomain<T> domain) {
        this.domain = Hasor.assertIsNotNull(domain);
        this.filterList = new ArrayList<FilterDefine>();
    }
    //
    /**添加Filter*/
    public void addRsfFilter(String filterID, RsfFilter rsfFilter) {
        this.addRsfFilter(filterID, new InstanceProvider<RsfFilter>(Hasor.assertIsNotNull(rsfFilter)));
    }
    /**添加Filter*/
    public void addRsfFilter(String filterID, Provider<? extends RsfFilter> rsfFilter) {
        this.addRsfFilter(new FilterDefine(filterID, Hasor.assertIsNotNull(rsfFilter)));
    }
    /**添加Filter*/
    public void addRsfFilter(FilterDefine filterDefine) {
        for (FilterDefine filterDef : this.filterList) {
            if (StringUtils.equals(filterDef.filterID(), filterDefine.filterID())) {
                return;
            }
        }
        this.filterList.add(filterDefine);
    }
    /**获取服务上配置有效的过滤器*/
    public List<FilterDefine> getFilterSnapshots() {
        return new ArrayList<FilterDefine>(this.filterList);
    }
    /**查找注册的Filter*/
    public RsfFilter getFilter(String filterID) {
        if (StringUtils.isBlank(filterID)) {
            return null;
        }
        List<FilterDefine> defines = this.filterList;
        if (defines == null || defines.isEmpty()) {
            return null;
        }
        for (FilterDefine defineProvider : defines) {
            if (StringUtils.equals(filterID, defineProvider.filterID())) {
                return defineProvider;
            }
        }
        return null;
    }
    /**获取服务提供者。*/
    @Override
    public Provider<T> getCustomerProvider() {
        return this.customerProvider;
    }
    public void setCustomerProvider(Provider<T> customerProvider) {
        this.customerProvider = customerProvider;
    }
    /**获取服务元信息。*/
    public ServiceDomain<T> getDomain() {
        return this.domain;
    }
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("");
        List<FilterDefine> defines = this.filterList;
        if (defines == null) {
            buffer.append(" null");
        } else {
            for (FilterDefine define : defines) {
                buffer.append(define.filterID() + ",");
            }
        }
        return "ServiceDefine[Domain=" + this.domain + ",Filters=" + buffer.toString() + "]";
    }
}