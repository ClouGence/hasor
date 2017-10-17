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
package net.hasor.dataql.udf.funs;
import java.util.*;
/**
 * 集合函数基类
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class CollectionUDFs {
    /**循环遍历函数*/
    public static Collection<Object> foreach(Object collection) {
        Collection<Object> listData = null;
        if (collection == null) {
            listData = new ArrayList<Object>();
        } else {
            if (!(collection instanceof Collection)) {
                if (collection.getClass().isArray()) {
                    listData = new ArrayList<Object>();
                    for (Object obj : (Object[]) collection) {
                        listData.add(obj);
                    }
                } else {
                    listData = Arrays.asList(collection);
                }
            } else {
                listData = (Collection<Object>) collection;
            }
        }
        //
        return listData;
    }
    //
    /** 将一个对象添加到另外一个集合中 */
    public static Object addTo(Object newValue, Object collection) {
        if (collection == null) {
            return null;
        }
        //
        List<Object> destList = new ArrayList<Object>(foreach(collection));
        List<Object> srcList = new ArrayList<Object>(foreach(newValue));
        destList.addAll(srcList);
        //
        return destList;
    }
    /** 取第一个元素 */
    public static Object first(Object collection) {
        if (collection == null) {
            return null;
        }
        //
        Collection<Object> objects = foreach(collection);
        if (objects.isEmpty()) {
            return null;
        }
        //
        return objects.iterator().next();
    }
    /** 取最后一个元素 */
    public static Object last(Object collection) {
        if (collection == null) {
            return null;
        }
        //
        Collection<Object> objects = foreach(collection);
        if (objects.isEmpty()) {
            return null;
        }
        //
        if (objects instanceof List) {
            List<?> list = (List<?>) objects;
            return list.get(list.size() - 1);
        } else {
            Iterator<Object> iterator = objects.iterator();
            Object curData = null;
            while (iterator.hasNext()) {
                curData = iterator.next();
            }
            return curData;
        }
    }
    /** 截取一部分，返回一个集合 */
    public static List<Object> limit(Object collection, int startInt, int limitInt) {
        Collection<Object> objects = foreach(collection);
        if (objects.isEmpty()) {
            return null;
        }
        //
        if (limitInt <= 0) {
            limitInt = Integer.MAX_VALUE;
        }
        //
        int curIndex = 0;
        Iterator<Object> iterator = objects.iterator();
        ArrayList<Object> finalList = new ArrayList<Object>();
        while (iterator.hasNext()) {
            Object curData = iterator.next();
            curIndex++;
            if (startInt <= curIndex && curIndex <= limitInt) {
                finalList.add(curData);
            }
        }
        return finalList;
    }
}