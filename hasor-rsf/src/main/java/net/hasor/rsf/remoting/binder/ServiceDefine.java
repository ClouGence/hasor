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
import java.util.UUID;
import net.hasor.core.Provider;
import net.hasor.core.info.CustomerProvider;
import net.hasor.core.info.MetaDataAdapter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfBinder.RegisterReference;
import net.hasor.rsf.common.metadata.ServiceMetaData;
/**
 * 服务的描述信息，包括了服务的发布和订阅信息。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
class ServiceDefine<T> extends MetaDataAdapter implements RsfBindInfo<T>, CustomerProvider<T>, RegisterReference<T> {
    private String                uniqueID = UUID.randomUUID().toString();
    private ServiceMetaData<T>    serviceMetaData;
    private AbstractBindCenter    registerCenter;
    private Provider<RsfFilter>[] rsfFilterArray;
    private Provider<T>           rsfProvider;
    //
    public ServiceDefine(ServiceMetaData<T> serviceMetaData, AbstractBindCenter registerCenter, Provider<RsfFilter>[] rsfFilterArray, Provider<T> rsfProvider) {
        this.serviceMetaData = serviceMetaData;
        this.registerCenter = registerCenter;
        this.rsfFilterArray = rsfFilterArray;
        this.rsfProvider = rsfProvider;
    }
    //
    public String getBindID() {
        return this.uniqueID;
    }
    public String getBindName() {
        return this.serviceMetaData.getServiceName();
    }
    public String getBindGroup() {
        return this.serviceMetaData.getServiceGroup();
    }
    public String getBindVersion() {
        return this.serviceMetaData.getServiceVersion();
    }
    public Class<T> getBindType() {
        return this.serviceMetaData.getServiceType();
    }
    //
    public Provider<T> getCustomerProvider() {
        return this.rsfProvider;
    }
    public ServiceMetaData<T> getMetaData() {
        return this.serviceMetaData;
    }
    public void unRegister() {
        this.registerCenter.recoverService(this);
    }
    public Provider<RsfFilter>[] getFilterProvider() {
        return this.rsfFilterArray;
    }
}