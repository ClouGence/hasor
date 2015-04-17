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
package net.hasor.rsf.binder;
import net.hasor.core.Provider;
import net.hasor.core.info.CustomerProvider;
import net.hasor.rsf.RsfBinder.RegisterReference;
import net.hasor.rsf.domain.ServiceDefine;
import net.hasor.rsf.domain.ServiceDomain;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
class BindServiceDefine<T> extends ServiceDefine<T> implements RegisterReference<T>, CustomerProvider<T> {
    private AbstractRsfContext rsfContext;
    private Provider<T>        customerProvider;
    public BindServiceDefine(Class<T> domainType, AbstractRsfContext rsfContext) {
        super(new ServiceDomain<T>(domainType));
        this.rsfContext = rsfContext;
    }
    //
    @Override
    protected ServiceDomain<T> getDomain() {
        return super.getDomain();
    }
    @Override
    public Provider<T> getCustomerProvider() {
        return this.customerProvider;
    }
    public void setCustomerProvider(Provider<T> customerProvider) {
        this.customerProvider = customerProvider;
    }
    @Override
    public void unRegister() {
        this.rsfContext.getAddressPool().recoverService(this);
        this.rsfContext.getBindCenter().recoverService(this);
    }
}