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
/**
 * 将查询结果进行分页提取。
 * @version : 2011-11-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface PagesList {
    /**获取一个值，该值表示分页的页大小。*/
    public int getPageSize();
    /**获取当前分页数据的首条记录，在整个查询结果的位置。*/
    public int getIndex();
    /**获取总页数*/
    public int getPageCount();
    /**获取当前页码*/
    public int getCurrentPageNumber();
    //
    /**执行当前分页查询，返回结果使用Map封装。*/
    public List<Map<String, Object>> query();
    /**执行当前分页查询，返回结果使用给定的类型封装。*/
    public <T> List<T> query(Class<T> dataType);
    //
    /**查询第一页并且返回分页对象。*/
    public PagesList firstPage();
    /**查询上一页并且返回分页对象。*/
    public PagesList previousPage();
    /**查询下一页并且返回分页对象。*/
    public PagesList nextPage();
    /**查询最后一页并且返回分页对象。*/
    public PagesList lastPage();
}