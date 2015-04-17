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
package net.hasor.rsf.domain;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.Provider;
import net.hasor.core.info.CustomerProvider;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfBinder.RegisterReference;
import org.more.util.StringUtils;
/**
 * 获取服务上配置有效的过滤器。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceDefine<T> implements RegisterReference<T>, CustomerProvider<T> {
    private final ServiceDomain<T>   domain;
    private final List<FilterDefine> filterDefine;
    private final List<URI>          providerList;
    //
    public ServiceDefine(ServiceDomain<T> domain) {
        this.domain = domain;
        this.filterDefine = new ArrayList<FilterDefine>();
        this.providerList = new ArrayList<URI>();
    }
    //
    protected ServiceDomain<T> getDomain() {
        return this.domain;
    }
    //
    public void addRsfFilter(String filterID,RsfFilter rsfFilter){
        
    }
    public void addRsfFilter(String filterID,Provider<RsfFilter> rsfFilter){
        
    }

    
//    /**获取服务上配置有效的过滤器*/
//    protected Provider<FilterDefine>[] getFilters() {
//        return getFilterManager().findAllComfitByObjectID(this.getBindID());
//    }
//    /**获取Provider对象，可以直接取得对象实例。*/
//    public Provider<T> getCustomerProvider() {
//        return rsfContext.getProvider(getDomain());
//    }
//    public void unRegister() {
//        this.getRsfContext().getBindCenter().recoverService(this.getDomain());
//    }
    //
    /**查找注册的Filter*/
    public Provider<FilterDefine> getFilter(String filterID) {
        if (StringUtils.isBlank(filterID)) {
            return null;
        }
        Provider<FilterDefine>[] defines = getFilters();
        if (defines == null || defines.length == 0) {
            return null;
        }
        for (Provider<FilterDefine> defineProvider : defines) {
            if (StringUtils.equals(filterID, defineProvider.get().filterID())) {
                return defineProvider;
            }
        }
        return null;
    }
    //
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
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("");
        Provider<FilterDefine>[] defines = getFilters();
        if (defines == null) {
            buffer.append(" null");
        } else {
            for (Provider<FilterDefine> define : defines) {
                buffer.append(define.get().filterID() + ",");
            }
        }
        return "ServiceDefine[Domain=" + this.getDomain() + ",Filters=" + buffer.toString() + "]";
    }
}