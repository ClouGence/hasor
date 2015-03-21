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
package net.hasor.rsf.constants;
/**
 * Server:Unknown、Message、MovedPermanently、Unauthorized
 * Client:
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolStatus {
    /**未定义*/
    public static final short Unknown             = 0;
    //-----------------------------------------------------
    /**内容正确返回。*/
    public static final short OK                  = 200;
    /**已经接受请求处理正在进行中。*/
    public static final short Accepted            = 202;
    //-----------------------------------------------------
    /**服务器要求客户端选择其它服务提供者处理该请求。*/
    public static final short ChooseOther         = 302;
    //-----------------------------------------------------
    /**试图调用受保护的服务。*/
    public static final short Unauthorized        = 401;
    /**服务资源不可用。*/
    public static final short Forbidden           = 403;
    /**找不到服务*/
    public static final short NotFound            = 404;
    /**调用服务超时*/
    public static final short RequestTimeout      = 408;
    //-----------------------------------------------------
    /**序列化异常。*/
    public static final short SerializeError      = 503;
    /**调用服务执行出错，通常是遭到异常抛出。*/
    public static final short InternalServerError = 500;
    /**协议错误。*/
    public static final short ProtocolError       = 502;
    //-----------------------------------------------------
    /**客户端错误。*/
    public static final short ClientError         = 600;
}