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
import net.hasor.core.BindInfo;
import net.hasor.rsf.domain.RsfServiceType;

import java.util.Set;
/**
 * Rsf绑定信息。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfBindInfo<T> extends BindInfo<T> {
    /** @return 唯一标识（客户端唯一标识）。*/
    public String getBindID();

    /** @return 服务名称。*/
    public String getBindName();

    /** @return 获取已经定义的别名 */
    public Set<String> getAliasTypes();

    /** @return 别名 */
    public String getAliasName(String aliasType);

    /** @return 服务分组。*/
    public String getBindGroup();

    /** @return 服务版本。*/
    public String getBindVersion();

    /** @return 注册的服务类型。*/
    public Class<T> getBindType();

    /**是提供者还是消费者*/
    public RsfServiceType getServiceType();

    /**
     * 返回接口是否为一个 Message 接口。
     * @see RsfMessage
     */
    public boolean isMessage();

    /** 接口是否要求工作在隐藏模式下。*/
    public boolean isShadow();

    /** @return 获取客户端调用服务超时时间。*/
    public int getClientTimeout();

    /** @return 获取序列化方式*/
    public String getSerializeType();

    /** 服务的执行线程池是否为共享线程池。 */
    public boolean isSharedThreadPool();
}