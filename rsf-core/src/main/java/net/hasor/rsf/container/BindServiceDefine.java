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
import net.hasor.core.EventContext;
import net.hasor.core.Provider;
import net.hasor.core.info.CustomerProvider;
import net.hasor.rsf.RsfBinder.RegisterReference;
import net.hasor.rsf.domain.ServiceDomain;
/**
 * 服务状态管理器。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
abstract class BindServiceDefine<T> extends ServiceInfo<T>implements RegisterReference<T>, CustomerProvider<T> {
    private Provider<T> customerProvider;
    //
    public BindServiceDefine(Class<T> domainType, EventContext eventPublisher) {
        super(new ServiceDomain<T>(domainType));
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
}