/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.jdbc.lambda;
import net.hasor.db.jdbc.ResultSetExtractor;
import net.hasor.db.jdbc.RowCallbackHandler;
import net.hasor.db.jdbc.RowMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * lambda SQL 执行
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface QueryExecute<T> {
    /** 参考的样本对象 */
    public Class<T> exampleType();

    /** QueryExecute 的查询类型转换为另外一个类型。*/
    public <V> QueryExecute<V> wrapperType(Class<V> wrapperType) throws SQLException;

    /** 执行查询，并通过 ResultSetExtractor 转换结果集。*/
    public <V> V query(ResultSetExtractor<V> rse) throws SQLException;

    /** 执行查询，并使用 RowMapper 处理结果集。*/
    public <V> List<V> query(RowMapper<V> rowMapper) throws SQLException;

    /** 执行查询，并通过 RowCallbackHandler 处理结果集。*/
    public void query(RowCallbackHandler rch) throws SQLException;

    /** 执行查询，并结果将被映射到一个列表(一个条目为每一行)的对象，列表中每一条记录都是<code>elementType</code>参数指定的类型对象。*/
    public List<T> queryForList() throws SQLException;

    /** 执行查询，并结果将被映射到一个列表(一个条目为每一行)的对象，列表中每一条记录都是<code>elementType</code>参数指定的类型对象。*/
    public T queryForObject() throws SQLException;

    /** 执行查询，并将结果集数据转换成<code>Map</code>。
     * 预计该方法只会处理一条数据，如果查询结果存在多条数据将取第一条记录作为结果。
     * @return 当不存在记录时返回<code>null</code>。
     */
    public Map<String, Object> queryForMap() throws SQLException;

    /** 执行查询，结果将被映射到一个列表(一个条目为每一行)的对象，
     * 列表中每一条记录都是<code>Map</code>类型对象。*/
    public List<Map<String, Object>> queryForMapList() throws SQLException;

    /** 生成 select count() 查询语句并查询总数。*/
    public int queryForCount() throws SQLException;

    /** 生成 select count() 查询语句并查询总数。*/
    public long queryForLargeCount() throws SQLException;
}
