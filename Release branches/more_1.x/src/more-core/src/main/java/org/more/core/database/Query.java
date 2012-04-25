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
/**
 * 通用查询接口.
 * @version : 2011-11-9
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Query<T> {
    /**获取查询的sql语句*/
    public String getQueryString();
    /**返回查询结果的类型。*/
    public Class<T> getResultType();
    /*-----------------------------------------------------------------------------------XXXX*/
    /**执行查询返回受影响的行数。*/
    public int executeQuery();
    /**执行查询语言并且返回这个查询结果.*/
    public List<T> query();
    /**执行查询，将查询结果进行分页。*/
    public PagesList<T> queryForPages(int pageSize);
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public T firstUnique();
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public T lastUnique();
    /*-----------------------------------------------------------------------------------带参数*/
    /**执行查询返回受影响的行数。*/
    public int executeQuery(Object... params);
    /**执行查询并且返回这个查询结果.*/
    public List<T> query(Object... params);
    /**执行查询，将查询结果进行分页。*/
    public PagesList<T> queryForPages(int pageSize, Object... params);
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public T firstUnique(Object... params);
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public T lastUnique(Object... params);
    /*-----------------------------------------------------------------------------------带回调*/
    /**执行查询返回受影响的行数。*/
    public int executeQuery(QueryCallBack callBack, Object... params);
    /**执行查询并且返回这个查询结果.*/
    public List<T> query(QueryCallBack callBack, Object... params);
    /**执行查询，将查询结果进行分页。*/
    public PagesList<T> queryForPages(int pageSize, QueryCallBack callBack, Object... params);
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public T firstUnique(QueryCallBack callBack, Object... params);
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public T lastUnique(QueryCallBack callBack, Object... params);
};