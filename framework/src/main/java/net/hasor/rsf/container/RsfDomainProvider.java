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
import net.hasor.rsf.domain.ServiceDomain;
/**
 * 可以让你在 Rsf_ProviderService、Rsf_ConsumerService 两个事件中得到 ServiceDomain 的对象的接口。
 * @version : 2017年02月23日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfDomainProvider<T> {
    /**获取服务元信息。*/
    public ServiceDomain<T> getDomain();
}
