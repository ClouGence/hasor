/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"){};
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
package org.more.core.database.assembler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.more.core.copybean.CopyBeanUtil;
import org.more.core.database.DataBaseSupport;
import org.more.core.database.PagesList;
import org.more.core.database.Query;
import org.more.core.database.QueryCallBack;
import org.more.core.error.LoadException;
/**
 * 通用查询接口实现类.
 * Date : 2010-6-21
 * @author 赵永春
 */
public abstract class AbstractQuery<T extends AbstractDataBaseSupport> implements Query {
    private String queryString = null;
    private T      support     = null;
    /***/
    public AbstractQuery(String queryString, T support) {
        this.queryString = queryString;
        this.support = support;
    };
    /**获取查询的语句*/
    public String getQuery() {
        return this.queryString;
    };
    /**获取本次查询所使用的{@link DataBaseSupport}接口。*/
    protected T getSupport() {
        return this.support;
    };
    /*-----------------------------------------------------------------------------------XXXX*/
    public int executeQuery() {
        return this.executeQuery(new DefaultQueryCallBack(this), new Object[0]);
    }
    /**执行查询语言并且返回这个查询结果.*/
    public List<Map<String, Object>> query() {
        return this.query(new DefaultQueryCallBack(this), new Object[0]);
    };
    /**执行查询语言并且返回这个查询结果.*/
    public <E> List<E> query(Class<E> toType) {
        return this.query(toType, new DefaultQueryCallBack(this), new Object[0]);
    };
    /**执行查询，将查询结果进行分页。*/
    public PagesList queryForPages(int pageSize) {
        return this.queryForPages(pageSize, new DefaultQueryCallBack(this), new Object[0]);
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public Object firstUnique() {
        return this.firstUnique(new DefaultQueryCallBack(this), new Object[0]);
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public <E> E firstUnique(Class<E> toType) {
        return this.firstUnique(toType, new DefaultQueryCallBack(this), new Object[0]);
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public Object lastUnique() {
        return this.lastUnique(new DefaultQueryCallBack(this), new Object[0]);
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public <E> E lastUnique(Class<E> toType) {
        return this.lastUnique(toType, new DefaultQueryCallBack(this), new Object[0]);
    };
    /*-----------------------------------------------------------------------------------带参数*/
    public int executeQuery(Object... params) {
        return this.executeQuery(new DefaultQueryCallBack(this), params);
    }
    /**执行查询并且返回这个查询结果.*/
    public List<Map<String, Object>> query(Object... params) {
        return this.query(new DefaultQueryCallBack(this), params);
    };
    /**执行查询并且返回这个查询结果.*/
    public <E> List<E> query(Class<E> toType, Object... params) {
        return this.query(toType, new DefaultQueryCallBack(this), params);
    };
    /**执行查询，将查询结果进行分页。*/
    public PagesList queryForPages(int pageSize, Object... params) {
        return this.queryForPages(pageSize, new DefaultQueryCallBack(this), params);
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public Object firstUnique(Object... params) {
        return this.firstUnique(new DefaultQueryCallBack(this), params);
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public <E> E firstUnique(Class<E> toType, Object... params) {
        return this.firstUnique(toType, new DefaultQueryCallBack(this), params);
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public Object lastUnique(Object... params) {
        return this.lastUnique(new DefaultQueryCallBack(this), params);
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public <E> E lastUnique(Class<E> toType, Object... params) {
        return this.lastUnique(toType, new DefaultQueryCallBack(this), params);
    };
    /*-----------------------------------------------------------------------------------带回调*/
    /**执行查询并且返回这个查询结果.*/
    public <E> List<E> query(Class<E> toType, QueryCallBack callBack, Object... params) {
        List<Map<String, Object>> resultSet = this.query(callBack, params);
        ArrayList<E> resList = new ArrayList<E>();
        for (Map<String, Object> entity : resultSet)
            try {
                E entityObject = toType.newInstance();
                CopyBeanUtil.copyTo(entity, entityObject);
                resList.add(entityObject);
            } catch (Exception e) {
                throw new LoadException("create type ‘" + toType + "’ or copy property error.", e);
            }
        return resList;
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public Object firstUnique(QueryCallBack callBack, Object... params) {
        List<?> list = this.query(callBack, params);
        if (list == null || list.size() == 0)
            return null;
        return list.get(0);
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public <E> E firstUnique(Class<E> toType, QueryCallBack callBack, Object... params) {
        try {
            Object obj = this.firstUnique(callBack, params);
            E entityObject = toType.newInstance();
            CopyBeanUtil.copyTo(obj, entityObject);
            return entityObject;
        } catch (Exception e) {
            throw new LoadException("create type ‘" + toType + "’ or copy property error.", e);
        }
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public Object lastUnique(QueryCallBack callBack, Object... params) {
        List<?> list = this.query(callBack, params);
        if (list == null || list.size() == 0)
            return null;
        return list.get(list.size() - 1);
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public <E> E lastUnique(Class<E> toType, QueryCallBack callBack, Object... params) {
        try {
            Object obj = this.lastUnique(callBack, params);
            E entityObject = toType.newInstance();
            CopyBeanUtil.copyTo(obj, entityObject);
            return entityObject;
        } catch (Exception e) {
            throw new LoadException("create type ‘" + toType + "’ or copy property error.", e);
        }
    };
};