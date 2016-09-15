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
package net.hasor.rsf.center.server;
import net.hasor.rsf.center.server.domain.entity.AuthInfo;
import net.hasor.rsf.center.server.domain.entity.ConsumerInfo;
import net.hasor.rsf.center.server.domain.entity.ProviderInfo;
import net.hasor.rsf.center.server.domain.entity.ServiceInfo;
import org.more.bizcommon.Result;
/**
 *
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public interface DataAdapter {
    /** 存储ServiceInfo 信息,并返回是否成功。*/
    public Result<Boolean> storeService(AuthInfo authInfo, ServiceInfo serviceDO);

    /** 存储服务提供者并返回标识符。*/
    public Result<String> storeProvider(AuthInfo authInfo, String serviceID, ProviderInfo serviceDO);

    /** 存储服务提供者并返回标识符。*/
    public Result<String> storeConsumer(AuthInfo authInfo, String serviceID, ConsumerInfo serviceDO);
}