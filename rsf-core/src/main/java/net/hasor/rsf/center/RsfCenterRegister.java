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
package net.hasor.rsf.center;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.center.domain.ConsumerPublishInfo;
import net.hasor.rsf.center.domain.ProviderPublishInfo;

import java.util.List;
/**
 * 服务发布接口，该接口需要远端注册中心实现
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
@RsfService(group = "RSF", version = "1.0.0")
public interface RsfCenterRegister {
    /** 发布服务,返回服务注册ID */
    public RsfCenterResult<String> registerProvider(String rsfAddress, ProviderPublishInfo info);

    /** 订阅服务,返回服务订阅ID */
    public RsfCenterResult<String> registerConsumer(String rsfAddress, ConsumerPublishInfo info);

    /** 解除发布或订阅 */
    public RsfCenterResult<Boolean> unRegister(String rsfAddress, String registerID, String serviceID);

    /** 心跳 */
    public RsfCenterResult<Boolean> serviceBeat(String rsfAddress, String registerID, String serviceID);

    /** 拉取服务提供者列表 */
    public RsfCenterResult<List<String>> pullProviders(String rsfAddress, String serviceID);

    /** 请求远程把服务地址重新推送过来 */
    public RsfCenterResult<Boolean> requestPushProviders(String rsfAddress, String serviceID);
}