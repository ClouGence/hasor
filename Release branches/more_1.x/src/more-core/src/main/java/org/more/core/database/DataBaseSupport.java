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
package org.more.core.database;
import java.util.List;
import java.util.Map;
import org.more.core.database.meta.TableMetaData;
/**
 * 数据库基本操作接口，该接口不支持批量操作。
 * @version : 2011-11-9
 * @author 赵永春 (zyc@byshell.org)
 */
public interface DataBaseSupport {
    public TableMetaData getTableInfo(String tableName);
    //    /**利用map进行插入操作。*/
    //    public void insertBatchForMap(String tableName, Map<String, Object> values);
    //    /**利用map进行更新操作。*/
    //    public void updateBatchForMap(String tableName, Map<String, Object> values, Map<String, Object> whereMap);
    //    
    //    
    //    /**利用map进行插入操作。*/
    //    public void insertForMap(String tableName, Map<String, Object> values);
    //    /**利用map进行更新操作。*/
    //    public void updateForMap(String tableName, Map<String, Object> values, Map<String, Object> whereMap);
    /**根据SQL语句创建一个查询接口对象.利用该对象可以进行复杂查询.*/
    public Query createQuery(String queryString);
    /*-----------------------------------------------------------------------------------XXXX*/
    /**执行查询返回受影响的行数。*/
    public int executeQuery(String queryString);
    /**执行查询语言并且返回这个查询结果.*/
    public List<Map<String, Object>> query(String queryString);
    /**执行查询，将查询结果进行分页。*/
    public PagesList queryForPages(String queryString, int pageSize);
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public Object firstUnique(String queryString);
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public Object lastUnique(String queryString);
    /*-----------------------------------------------------------------------------------带参数*/
    /**执行查询返回受影响的行数。*/
    public int executeQuery(String queryString, Object... params);
    /**执行查询并且返回这个查询结果.*/
    public List<Map<String, Object>> query(String queryString, Object... params);
    /**执行查询，将查询结果进行分页。*/
    public PagesList queryForPages(String queryString, int pageSize, Object... params);
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public Object firstUnique(String queryString, Object... params);
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public Object lastUnique(String queryString, Object... params);
    /*-----------------------------------------------------------------------------------带回调*/
    /**执行查询返回受影响的行数。*/
    public int executeQuery(String queryString, QueryCallBack callBack, Object... params);
    /**执行查询并且返回这个查询结果.*/
    public List<Map<String, Object>> query(String queryString, QueryCallBack callBack, Object... params);
    /**执行查询，将查询结果进行分页。*/
    public PagesList queryForPages(String queryString, int pageSize, QueryCallBack callBack, Object... params);
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public Object firstUnique(String queryString, QueryCallBack callBack, Object... params);
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public Object lastUnique(String queryString, QueryCallBack callBack, Object... params);
};