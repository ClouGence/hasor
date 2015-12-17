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
package net.hasor.rsf.plugins.center.client;
/***
 * 
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public interface CenterParams {
    String Terminal_ID           = "Terminal_ID";
    String Terminal_AccessKey    = "Terminal_AccessKey";
    String Terminal_HostName     = "Terminal_HostName";
    String Terminal_HostPort     = "Terminal_HostPort";
    String Terminal_HostUnit     = "Terminal_HostUnit";
    String Terminal_Version      = "Terminal_Version";
    //
    String Service_BindID        = "Service_BindID";
    String Service_BindName      = "Service_BindName";     //服务名
    String Service_BindGroup     = "Service_BindGroup";    //服务分组
    String Service_BindVersion   = "Service_BindVersion";  //服务版本
    String Service_BindType      = "Service_BindType";     //服务类型
    String Service_ClientTimeout = "Service_ClientTimeout"; //调用超时（毫秒）
    String Service_SerializeType = "Service_SerializeType"; //传输序列化类型
    //
    String Service_Persona       = "Service_Persona";      //
    String HeartBeat             = "HeartBeat";            //心跳(消费者/提供者)
}