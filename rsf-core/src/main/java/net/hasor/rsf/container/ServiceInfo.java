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
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.domain.ServiceDomain;
/**
 * 服务对象，封装了服务元信息、RsfFilter、服务提供者（如果有）。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceInfo<T> implements RsfBindInfo<T> {
    private final ServiceDomain<T>   domain;
    private final List<FilterDefine> filterList;
    private Provider<T>              provider;
    //
    public ServiceInfo(ServiceDomain<T> domain) {
        this.domain = domain;
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
    public List<RsfFilter> getFilters() {
        return new ArrayList<RsfFilter>(this.filterList);
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
    public Provider<T> getProvider() {
        return this.provider;
    }
    /**设置服务提供者。*/
    public void setProvider(Provider<T> provider) {
        this.provider = provider;
    }
    //
    //
    /**获取domain*/
    protected ServiceDomain<T> getDomain() {
        return this.domain;
    }
    public String getBindID() {
        return this.getDomain().getBindID();
    }
    public String getBindName() {
        return this.getDomain().getBindName();
    }
    public String getBindGroup() {
        return this.getDomain().getBindGroup();
    }
    public String getBindVersion() {
        return this.getDomain().getBindVersion();
    }
    public Class<T> getBindType() {
        return this.getDomain().getBindType();
    }
    public int getClientTimeout() {
        return this.getDomain().getClientTimeout();
    }
    public String getSerializeType() {
        return this.getDomain().getSerializeType();
    }
    public Object getMetaData(String key) {
        return this.getDomain().getMetaData(key);
    }
    //
    //
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
        return "ServiceDefine[Domain=" + this.getDomain() + ",Filters=" + buffer.toString() + "]";
    }
}