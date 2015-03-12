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
package net.hasor.rsf.adapter;
import java.net.URL;
import java.util.List;
import net.hasor.rsf.RsfBindInfo;
/**
 * 地址管理中心，负责维护服务的远程服务提供者列表。
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstracAddressCenter {
    /**查找一个有效主机地址*/
    public abstract Address findHostAddress(RsfBindInfo<?> bindInfo);
    /**被明确为无效的地址*/
    public abstract void invalidAddress(Address refereeAddress);
    /**更新静态服务提供地址*/
    public abstract void updateAddress(RsfBindInfo<?> bindInfo, List<URL> serviceURLs);
}