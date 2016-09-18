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
package net.hasor.rsf.center.server;
import net.hasor.rsf.center.server.domain.ObjectDO;
import net.hasor.rsf.center.server.domain.Result;

import java.util.List;
/**
 * 服务数据存储检索
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public interface DataAdapter {
    /** 查询对象 */
    public Result<ObjectDO> queryObjectByID(String objectID);

    /** 删除对象 */
    public Result<Boolean> removeObjectByID(String objectID);

    /** 保存对象,并将改对象关联到refObjectID对象上 */
    public Result<Boolean> storeObject(ObjectDO object);

    /** 检索关联的对象列表 */
    public Result<List<ObjectDO>> queryObjectListByID(String refObjectID, QueryOption option);

    /** 清空对象上的所有关联 */
    public Result<Boolean> clearRef(String objectID);

    /** 刷新对象的时间(可选支持,如果不支持,center将会采取storeObject方法重新保存) */
    public Result<Boolean> refreshObject(String objectID);
}