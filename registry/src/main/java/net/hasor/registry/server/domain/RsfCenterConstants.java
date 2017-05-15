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
package net.hasor.registry.server.domain;
/**
 * 各种常量
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfCenterConstants {
    public static final String Center_DataKey_Service  = "S|";                  //服务
    public static final String Center_DataKey_Provider = "P|";                  //提供者
    public static final String Center_DataKey_Consumer = "C|";                  //订阅者
    // ----------------------------------------------------
    //
    public static final String Center_Request_AuthInfo = "RSF_REQUEST_AUTH";     //远程Request请求携带的验证信息
}