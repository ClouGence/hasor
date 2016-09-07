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
package net.hasor.rsf;
import net.hasor.rsf.address.InterAddress;

import java.lang.reflect.Method;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfRequest extends RsfHeader {
    /**请求是否为本地发起的。*/
    public boolean isLocal();

    /** 请求是否为消息类请求,对于消息类请求返回值是无效的。*/
    public boolean isMessage();

    /**获取要调用的目标方法。*/
    public Method getMethod();

    /**获取上下文。*/
    public RsfContext getContext();

    /**请求到达时间（如果是本地发起的请求，该值为当前时间）。*/
    public long getReceiveTime();

    /**超时时间。*/
    public int getTimeout();

    /**获取请求参数类型。*/
    public Class<?>[] getParameterTypes();

    /**获取请求参数值。*/
    public Object[] getParameterObject();
    //

    /**获取发送请求的远程服务器使用的地址和端口，如果是本地发起的该地址则是本地RSF的地址。*/
    public InterAddress getRemoteAddress();

    /**获取请求准备发送的目标地址（如果是分布式调用该方法会返回null）*/
    public InterAddress getTargetAddress();
}