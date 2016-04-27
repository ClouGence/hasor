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
import java.util.Map;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.center.domain.ConsumerPublishInfo;
import net.hasor.rsf.center.domain.ProviderPublishInfo;
import net.hasor.rsf.center.domain.ReceiveResult;
/**
 * 服务发布接口，该接口需要远端注册中心实现
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
@RsfService(group = "RSF", version = "1.0.0")
public interface RsfCenterRegister {
    //
    /**发布服务
     * @return 返回订阅ID。*/
    public String publishService(String rsfHostString, ProviderPublishInfo info);
    /**发布服务心跳*/
    public Map<String, Boolean> publishServiceBeat(String rsfHostString, Map<String, String> beatMap);
    /**删除发布*/
    public boolean removePublish(String rsfHostString, String serviceID);
    //
    /** 订阅服务
     * @return 返回订阅信息。*/
    public ReceiveResult receiveService(String rsfHostString, ConsumerPublishInfo info);
    /**订阅服务心跳*/
    public Map<String, Boolean> receiveServiceBeat(String rsfHostString, Map<String, String> beatMap);
    /**删除订阅*/
    public boolean removeReceive(String rsfHostString, String serviceID);
}