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
package net.hasor.dataql.sdk;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSource;
import net.hasor.dataql.domain.ListModel;
import net.hasor.utils.ExceptionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 集合函数。函数库引入 <code>import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
public class CollectionUdfSource extends TypeUdfMap implements UdfSource {
    public CollectionUdfSource() {
        super(CollectionUdfSource.class);
    }

    @Override
    public Map<String, Udf> getUdfResource() {
        return this;
    }
    // ----------------------------------------------------------------------------------

    /**循环遍历函数*/
    private static Collection<Object> foreach(Object collection) {
        Collection<Object> listData = null;
        if (collection == null) {
            listData = new ArrayList<>();
        } else {
            if (!(collection instanceof Collection)) {
                if (collection.getClass().isArray()) {
                    listData = new ArrayList<>();
                    Collections.addAll(listData, (Object[]) collection);
                } else {
                    listData = Collections.singletonList(collection);
                }
            } else {
                listData = (Collection<Object>) collection;
            }
        }
        //
        return listData;
    }

    /** 合并多个对象或者集合 */
    public static List<Object> merge(UdfParams dataArrays) {
        if (dataArrays == null) {
            return null;
        }
        List<Object> dataList = new ArrayList<>();
        for (Object object : dataArrays.allParams()) {
            if (object instanceof ListModel) {
                dataList.addAll(foreach(((ListModel) object).asOri()));
            } else {
                dataList.addAll(foreach(object));
            }
        }
        return dataList;
    }

    /**
     * 对集合进行过滤
     * @param valueList 集合数据
     * @param filter 过滤器 Predicate or 返回值为 Boolean 的 UDF
     * @param option 选项
     */
    public static Object filter(List<Object> valueList, Object filter, Hints option) {
        if (valueList == null || valueList.isEmpty()) {
            return null;
        }
        if (filter == null) {
            return valueList;
        }
        //
        AtomicReference<Predicate<Object>> predicateAtomicReference = new AtomicReference<>();
        if (filter instanceof Predicate) {
            predicateAtomicReference.set((Predicate<Object>) filter);
        } else if (filter instanceof Udf) {
            predicateAtomicReference.set(o -> {
                try {
                    return (boolean) ((Udf) filter).call(option, o);
                } catch (Throwable e) {
                    throw ExceptionUtils.toRuntimeException(e);
                }
            });
        } else {
            throw new NullPointerException("filter is null or Type is not Predicate or Udf.");
        }
        //
        return valueList.stream().filter(predicateAtomicReference.get()).collect(Collectors.toList());
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
        ArrayList<Object> finalList = new ArrayList<>();
        while (iterator.hasNext()) {
            Object curData = iterator.next();
            if (curIndex >= startInt && limitInt > 0) {
                finalList.add(curData);
                limitInt--;
            }
            curIndex++;
        }
        return finalList;
    }
}