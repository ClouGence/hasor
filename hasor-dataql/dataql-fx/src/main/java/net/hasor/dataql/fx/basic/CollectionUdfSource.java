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
package net.hasor.dataql.fx.basic;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.utils.ExceptionUtils;

import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 集合函数。函数库引入 <code>import 'net.hasor.dataql.fx.basic.CollectionUdfSource' as collect;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
@Singleton
public class CollectionUdfSource implements UdfSourceAssembly {
    /** 循环遍历函数 */
    protected static Collection<Object> foreach(Object collection) {
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
        return listData;
    }

    /** 合并多个对象或者集合成为一个新的集合 */
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

    /** 集合是否为空 */
    public static boolean isEmpty(List<Object> valueList) {
        return valueList == null || valueList.isEmpty();
    }

    /** 截取一部分，返回一个集合 */
    public static List<Object> limit(List<Object> collection, int startInt, int limitInt) {
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

    /** 创建一个有状态的 Array 对象 */
    @UdfName("new")
    public static Map<String, Udf> newArray(Object mabeCollection) {
        List<Object> initData = new ArrayList<>();
        if (mabeCollection != null) {
            initData.addAll(foreach(mabeCollection));
        }
        return new InnerCollectionStateUdfSource(initData).getUdfResource(Finder.DEFAULT).get();
    }

    /** List 转为 Map */
    public static Map<String, Object> list2map(List<Object> valueList, String key, Udf convert, Hints option) throws Throwable {
        ListModel convertTo = (ListModel) DomainHelper.convertTo(valueList);
        if (convertTo == null || convertTo.size() == 0) {
            return Collections.emptyMap();
        }
        Map<String, Object> mapData = new LinkedHashMap<>();
        Map<String, Object> errorData = new LinkedHashMap<>();
        for (int i = 0; i < convertTo.size(); i++) {
            DataModel dataModel = convertTo.get(i);
            String errorMessage = "";
            if (dataModel.isObject()) {
                DataModel objectModel = dataModel;
                DataModel keyValue = ((ObjectModel) objectModel).get(key);
                if (keyValue != null) {
                    if (keyValue.isValue()) {
                        if (convert != null) {
                            Object call = convert.call(option, objectModel);
                            objectModel = DomainHelper.convertTo(call);
                        }
                        mapData.put(keyValue.unwrap().toString(), objectModel);
                        continue;
                    } else {
                        errorMessage = "element key '" + key + "' type must primary.";
                    }
                } else {
                    errorMessage = "element key '" + key + "' is not exist.";
                }
            } else {
                errorMessage = "element type is not Object.";
            }
            //
            LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("errorMsg", errorMessage);
            hashMap.put("errorData", dataModel);
            errorData.put("idx_" + i, hashMap);
        }
        //
        if (!errorData.isEmpty()) {
            int i = 0;
            String mapKey = "errorData";
            while (true) {
                if (mapData.containsKey(mapKey)) {
                    i++;
                    mapKey = "errorData_" + i;
                    continue;
                }
                mapData.put(mapKey, errorData);
                break;
            }
        }
        return mapData;
    }

    /** Map 转为 List */
    public static List<Object> map2list(Object mapValue, Udf convert, Hints option) throws Throwable {
        ObjectModel convertTo = (ObjectModel) DomainHelper.convertTo(mapValue);
        if (convertTo == null || convertTo.size() == 0) {
            return Collections.emptyList();
        }
        ArrayList<Object> listData = new ArrayList<>();
        Set<Map.Entry<String, DataModel>> entrySet = convertTo.asOri().entrySet();
        for (Map.Entry<String, DataModel> entry : entrySet) {
            DataModel objectModel = DomainHelper.newObject();
            ((ObjectModel) objectModel).put("key", entry.getKey());
            ((ObjectModel) objectModel).put("value", entry.getValue());
            if (convert != null) {
                objectModel = DomainHelper.convertTo(convert.call(option, objectModel));
            }
            listData.add(objectModel);
        }
        return listData;
    }

    private static String evalJoinKey(Object data, String[] joinField) {
        ObjectModel objectModel = (ObjectModel) DomainHelper.convertTo(data);
        StringBuilder joinKey = new StringBuilder("");
        Arrays.stream(joinField).forEach(s -> {
            Object unwrap = objectModel.get(s).unwrap();
            unwrap = (unwrap == null) ? "NULL" : ("s" + unwrap.toString());
            joinKey.append(unwrap).append(",");
        });
        return joinKey.toString();
    }

    /** 将两个 Map List 进行链接，行为和 sql 中的 left join 相同 */
    public static List<Map<String, Object>> mapjoin(List<Object> data1, List<Object> data2, Map<String, String> join, Hints option) {
        //
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>(join);
        String[] joinKey1 = linkedHashMap.keySet().toArray(new String[0]);
        String[] joinKey2 = linkedHashMap.values().toArray(new String[0]);
        //
        //
        Map<String, Object> joinMap = new HashMap<>();
        for (Object dat : data2) {
            joinMap.put(evalJoinKey(dat, joinKey2), dat);
        }
        //
        List<Map<String, Object>> returnData = new ArrayList<>();
        for (Object dat1 : data1) {
            String joinKey = evalJoinKey(dat1, joinKey1);
            returnData.add(new HashMap<String, Object>() {{
                put("data1", dat1);
                put("data2", joinMap.get(joinKey));
            }});
        }
        return returnData;
    }
}