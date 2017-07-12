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
package net.hasor.registry.server.adapter;
import net.hasor.registry.domain.server.ObjectDO;
import net.hasor.registry.server.domain.Result;

import java.util.List;
/**
 * 服务数据存储检索
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public interface DataAdapter {
    /**
     * 查询对象
     *  <i>核心方法,负责数据查询。</i>
     */
    public Result<ObjectDO> queryObjectByID(String objectID);

    /**
     * 保存对象,并将改对象关联到refObjectID对象上 
     *  <i>核心方法,负责数据存储。</i>
     */
    public Result<Boolean> storeObject(ObjectDO object);

    /**
     * 检索关联的对象列表
     *  <i>核心方法,负责数据查询。</i>
     *  <i>负责查询服务下的 (提供者 or 消费者) 列表,以及服务路由等信息。</i>
     *  <i>option参数是可选支持: 负责限定查询条件,因为服务提供者和服务消费者都会挂在 refObjectID 下面,因此实现该参数会有助于减轻Center的负担。</i>
     * @param refObjectID 关联的服务ID
     * @param option 查询过滤参数(可选)
     */
    public Result<List<ObjectDO>> queryObjectListByID(String refObjectID, QueryOption option);

    /**
     * 刷新对象的时间
     *  <i>RSF服务心跳依赖这个方法。</i>
     *  <i>可选支持: 如果不支持,RSF客户端会在每次心跳时进行服务的重新注册。</i>
     */
    public Result<Boolean> refreshObject(String objectID);

    /**
     * 删除对象
     *  <i>服务下线需要实现这个接口。</i>
     *  <i>可选支持: 如果不支持,Center在每次推送地址的时候会推送失效的地址。虽然RSF客户端有地址有效性校验机制,但是这会加重运行负担。</i>
     */
    public Result<Boolean> removeObjectByID(String objectID);

    /**
     *  清空对象上的所有关联
     *  <i>Center定时扫描服务进行数据清理需要实现这个接口。</i>
     *  <i>可选支持: Center会定时的检查服务数据健康度,并做相应的清理工作。如果不实现该方法,在每次推送地址的时候会推送失效的地址。虽然RSF客户端有地址有效性校验机制,但是这会加重运行负担。</i>
     */
    public Result<Boolean> clearRef(String objectID);
}