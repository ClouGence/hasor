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
package net.hasor.rsf.runtime.register;
import net.hasor.core.Provider;
import net.hasor.core.info.CustomerProvider;
import net.hasor.core.info.MetaDataAdapter;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfBindInfo;
import net.hasor.rsf.runtime.RsfBinder.RegisterReference;
import net.hasor.rsf.runtime.RsfFilter;
/**
 * 服务的描述信息，包括了服务的发布和订阅信息。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
class ServiceDefine<T> extends MetaDataAdapter implements RsfBindInfo<T>, CustomerProvider<T>, RegisterReference<T> {
    private ServiceMetaData        serviceMetaData;
    private AbstractRegisterCenter registerCenter;
    private Provider<RsfFilter>[]  rsfFilterArray;
    private Provider<T>            rsfProvider;
    //
    public ServiceDefine(ServiceMetaData serviceMetaData, AbstractRegisterCenter registerCenter, Provider<RsfFilter>[] rsfFilterArray, Provider<T> rsfProvider) {
        this.serviceMetaData = serviceMetaData;
        this.registerCenter = registerCenter;
        this.rsfFilterArray = rsfFilterArray;
        this.rsfProvider = rsfProvider;
    }
    public String getBindID() {
        // TODO Auto-generated method stub
        return null;
    }s
    public String getBindName() {
        // TODO Auto-generated method stub
        return null;
    }
    public Class<T> getBindType() {
        // TODO Auto-generated method stub
        return null;
    }
    public Provider<T> getCustomerProvider() {
        return this.rsfProvider;
    }
    public ServiceMetaData getMetaData() {
        return this.serviceMetaData;
    }
    public void unRegister() {
        this.registerCenter.recoverService(this);
    }
}