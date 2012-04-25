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
 * 
 * @version : 2011-12-27
 * @author 赵永春 (zyc@byshell.org)
 */
public interface OrmSupport {
    /**保存对象*/
    public void insertObject(Object entityObject);
    /**更新对象*/
    public void updateObject(Object entityObject);
    /**删除这个新对象*/
    public void deleteObject(Object entityObject);
    /**查询所有对象列表*/
    public List<Object> getObjectList(Class<?> entityType);
    /**查询所有对象列表*/
    public Object getObjectByID(Object objectID, Class<?> entityType);
    /**获取实体上ID列的值。*/
    public Object getID(Object entityObject, Class<?> entityType);
}