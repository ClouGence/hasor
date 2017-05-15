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
package net.hasor.rsf.domain;
/**
 * 事件名
 * @version : 2015年5月6日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfEvent {
    /**发布服务*/
    public static final String Rsf_ProviderService = "RsfEvent_ProviderService";
    /**订阅服务*/
    public static final String Rsf_ConsumerService = "RsfEvent_ConsumerService";
    /**删除发布或订阅*/
    public static final String Rsf_DeleteService   = "RsfEvent_DeleteService";
    //
    /**应用上线*/
    public static final String Rsf_Online          = "RsfEvent_Online";
    /**应用下线*/
    public static final String Rsf_Offline         = "RsfEvent_Offline";
}