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
package net.hasor.rsf;
import net.hasor.core.BindInfo;
/**
 * Rsf绑定信息。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfBindInfo<T> extends BindInfo<T> {
    /** @return 唯一标识。*/
    public String getBindID();
    /** @return 服务名称。*/
    public String getBindName();
    /** @return 服务分组。*/
    public String getBindGroup();
    /** @return 服务版本。*/
    public String getBindVersion();
    /** @return 注册的服务类型。*/
    public Class<T> getBindType();
    //
    /** @return 获取客户端调用服务超时时间。*/
    public int getClientTimeout();
    /** @return 获取序列化方式*/
    public String getSerializeType();
}