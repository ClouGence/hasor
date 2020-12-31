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
package net.hasor.dataway.dal;
import java.util.List;
import java.util.Map;

/**
 * 数据访问层接口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
public interface ApiDataAccessLayer {
    /** 点查 or 唯一索引 */
    public Map<FieldDef, String> getObjectBy(EntityDef objectType, FieldDef indexKey, String indexValue);

    /** 列表/搜索 */
    public List<Map<FieldDef, String>> listObjectBy(EntityDef objectType, Map<QueryCondition, Object> conditions);

    /** 生成 ID */
    public String generateId(EntityDef objectType, String apiPath);

    /** 删除对象 */
    public boolean deleteObject(EntityDef objectType, String id);

    /** 更新对象 */
    public boolean updateObject(EntityDef objectType, String id, Map<FieldDef, String> newData);

    /** 新增对象 */
    public boolean createObject(EntityDef objectType, Map<FieldDef, String> newData);
}