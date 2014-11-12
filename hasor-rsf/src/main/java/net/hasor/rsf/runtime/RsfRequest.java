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
package net.hasor.rsf.runtime;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfRequest {
    /***请求ID。*/
    public String getRequestID();
    /**远程调用请求IP地址。*/
    public String getRemotHost();
    //
    /**请求超时时间。*/
    public int getTimeout();
    /**获取服务名。*/
    public String getServiceName();
    /**获取服务方法名。*/
    public String getServiceMethod();
    //
    /**获取请求参数类型。*/
    public Class<?>[] getParameterTypes();
    /**获取请求参数值。*/
    public Object[] getParameterObject();
}