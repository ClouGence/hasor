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
    /** 集合 或 Map 是否为空 */
    public static boolean isEmpty(Object target) {
        if (target instanceof List) {
            return ((List) target).isEmpty();
        }
        if (target instanceof ListModel) {
            return ((ListModel) target).size() == 0;
        }
        if (target instanceof Map) {
            return ((Map) target).isEmpty();
        }
        if (target instanceof ObjectModel) {
            return ((ObjectModel) target).size() == 0;
        }
        return false;
    }

    /**
     * 如果是空：返回 0，
     * 如果是 Map 返回字段数量
     * 如果是数组：返回数组长度
     */
    public static int size(Object target) {
        if (target == null) {
            return 0;
        }
        if (target instanceof Map) {
            return ((Map) target).size();
        }
        return foreach(target).size();
    }
    // -------------------------------------------------------------------------------------------------------------------------- List

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

    /** 合并多个对象合成为一个新的对象（冲突Key会被覆盖） */
    public static Map<String, Object> mergeMap(UdfParams dataArrays) {
        if (dataArrays == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> finalMap = new LinkedHashMap<>();
        Object[] allParams = dataArrays.allParams();
        for (int i = 0; i < allParams.length; i++) {
            Object object = allParams[i];
            if (object instanceof Map) {
                ((Map) object).forEach((o, o2) -> {
                    finalMap.put(o.toString(), o2);
                });
            }
            object = DomainHelper.convertTo(object);
            if (object instanceof ObjectModel) {
                finalMap.putAll(((ObjectModel) object).unwrap());
            } else {
                throw new IllegalArgumentException("all args must be Map.");
            }
        }
        return finalMap;
    }
    // -------------------------------------------------------------------------------------------------------------------------- List

    /**
     * 对集合进行过滤
     * @param valueList 集合数据
     * @param filter 过滤器 Predicate or 返回值为 Boolean 的 UDF
     * @param hints 选项
     */
    public static List<Object> filter(List<Object> valueList, Udf filter, Hints hints) {
        if (valueList == null || valueList.isEmpty()) {
            return null;
        }
        if (filter == null) {
            return valueList;
        }
        // Udf to Predicate
        AtomicReference<Predicate<Object>> refPredicate = new AtomicReference<>(o -> {
            try {
                return (boolean) filter.call(hints, o);
            } catch (Throwable e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        });
        //
        return valueList.stream().filter(refPredicate.get()).collect(Collectors.toList());
    }

    /**
     * 对Map进行过滤
     * @param mapData 集合数据
     * @param keyFilter key过滤器
     * @param hints 选项
     */
    public static Map<String, Object> filterMap(Map<String, Object> mapData, Udf keyFilter, Hints hints) throws Throwable {
        if (keyFilter == null || mapData.isEmpty()) {
            return mapData;
        }
        Map<String, Object> finalMap = new LinkedHashMap<>();
        for (String key : mapData.keySet()) {
            if ((boolean) keyFilter.call(hints, key)) {
                finalMap.put(key, mapData.get(key));
            }
        }
        return finalMap;
    }
    // -------------------------------------------------------------------------------------------------------------------------- List

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
    public static Map<String, Udf> newList(Object maybeCollection) {
        List<Object> initData = new ArrayList<>();
        if (maybeCollection != null) {
            initData.addAll(foreach(maybeCollection));
        }
        return new Inner_ListStateUdfSource(initData).getUdfResource(Finder.DEFAULT).get();
    }

    /** 对 List 进行排序 */
    public static List<Object> listSort(List<Object> listData, Udf sortUdf, Hints hints) {
        if (listData == null) {
            return Collections.emptyList();
        }
        if (sortUdf == null) {
            listData.sort((o1, o2) -> {
                int hc1 = (o1 == null) ? 0 : o1.hashCode();
                int hc2 = (o2 == null) ? 0 : o2.hashCode();
                return Integer.compare(hc1, hc2);
            });
            return listData;
        } else {
            listData.sort((o1, o2) -> {
                try {
                    return (Integer) sortUdf.call(hints, new Object[] { o1, o2 });
                } catch (Throwable e) {
                    throw ExceptionUtils.toRuntimeException(e);
                }
            });
            return listData;
        }
    }

    /** List 转为 Map */
    public static Map<String, Object> list2map(List<Object> valueList, Object key, Udf convert, Hints hints) throws Throwable {
        if (key == null) {
            throw new IllegalArgumentException("The key parameter cannot be null");
        }
        if (!(key instanceof String || key instanceof Udf)) {
            throw new IllegalArgumentException("key arg must be Udf or String");
        }
        if (key instanceof String) {
            return list2map_string(valueList, key.toString(), convert, hints);
        } else {
            return list2map_udf(valueList, (Udf) key, convert, hints);
        }
    }

    private static Map<String, Object> list2map_string(List<Object> valueList, String key, Udf convert, Hints hints) throws Throwable {
        return list2map_udf(valueList, (readOnly, params) -> {
            int rowNumber = (int) params[0];
            if (params[1] == null) {
                throw new NullPointerException("element " + rowNumber + " data is null");
            }
            DataModel rowData = DomainHelper.convertTo(params[1]);
            if (!rowData.isObject()) {
                throw new NullPointerException("element " + rowNumber + " type is not Object");
            }
            DataModel keyValue = ((ObjectModel) rowData).get(key);
            if (keyValue == null) {
                throw new NullPointerException("element " + rowNumber + " key '" + key + "' is not exist");
            }
            if (!keyValue.isValue()) {
                throw new NullPointerException("element " + rowNumber + " key '" + key + "' type must primary");
            }
            return String.valueOf(keyValue.unwrap());
        }, convert, hints);
    }

    private static Map<String, Object> list2map_udf(List<Object> valueList, Udf extractKey, Udf convert, Hints hints) throws Throwable {
        ListModel convertTo = (ListModel) DomainHelper.convertTo(valueList);
        if (convertTo == null || convertTo.size() == 0) {
            return Collections.emptyMap();
        }
        if (extractKey == null) {
            throw new IllegalArgumentException("extractKey Udf is null"); // Key 提取函数丢失了
        }
        //
        Map<String, Object> mapData = new LinkedHashMap<>();
        Map<String, Object> errorData = new LinkedHashMap<>();
        for (int i = 0; i < convertTo.size(); i++) {
            DataModel valueData = convertTo.get(i);
            try {
                DataModel keyData = DomainHelper.convertTo(extractKey.call(hints, i, valueData));
                if (!keyData.isValue()) {
                    throw new NullPointerException("element " + i + " key type must primary");
                }
                String unwrapKey = String.valueOf(keyData.unwrap());
                if (convert != null) {
                    Object mapValue = convert.call(hints, i, valueData);
                    valueData = DomainHelper.convertTo(mapValue);
                }
                mapData.put(unwrapKey, valueData);
            } catch (Exception e) {
                LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
                hashMap.put("errorMsg", e.getMessage());
                hashMap.put("errorData", valueData);
                errorData.put("idx_" + i, hashMap);
            }
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
    // -------------------------------------------------------------------------------------------------------------------------- List

    /** 对 List 进行分组 */
    public static Map<String, Object> groupBy(final List<Object> valueList, final Object key, final Hints hints) throws Throwable {
        return list2map(valueList, key, (readOnly1, params1) -> {
            DataModel dataModel = DomainHelper.convertTo(params1[1]);
            if (!dataModel.isObject()) {
                throw new UnsupportedOperationException(params1[0] + " element require Object");
            }
            final Object parentKeyValue = ((ObjectModel) dataModel).getValue(key.toString()).unwrap();
            return filter(valueList, (readOnly2, params2) -> {
                if (params2 == null) {
                    return false;
                }
                DataModel dataModel1 = DomainHelper.convertTo(params2[0]);
                Object targetKeyValue = ((ObjectModel) dataModel1).getValue(key.toString()).unwrap();
                return Objects.deepEquals(parentKeyValue, targetKeyValue);
            }, hints);
        }, hints);
    }

    // -------------------------------------------------------------------------------------------------------------------------- Map
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

    /** 创建一个有状态的 Map 对象 */
    public static Map<String, Udf> newMap(Map<String, Object> collection) {
        Map<String, Object> initData = new LinkedHashMap<>();
        if (collection != null) {
            initData.putAll(collection);
        }
        return new Inner_MapStateUdfSource(initData).getUdfResource(Finder.DEFAULT).get();
    }

    /** 将两个 Map List 进行链接，行为和 sql 中的 left join 相同 */
    public static List<Map<String, Object>> mapJoin(List<Object> data1, List<Object> data2, Map<String, String> join) {
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

    /** Map 的 Key 统一转小写 */
    public static Map<String, Object> mapKeyToLowerCase(Map<String, Object> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();
        map.forEach((s, o) -> newMap.put(s.toLowerCase(), o));
        return newMap;
    }

    /** Map 的 Key 统一转大写 */
    public static Map<String, Object> mapKeyToUpperCase(Map<String, Object> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();
        map.forEach((s, o) -> newMap.put(s.toUpperCase(), o));
        return newMap;
    }

    /** Map 的 Key 统一转驼峰 */
    public static Map<String, Object> mapKeyToHumpCase(Map<String, Object> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();
        map.forEach((s, o) -> newMap.put(StringUdfSource.lineToHump(s.toLowerCase()), o));
        return newMap;
    }

    /** 提取 Map 的 Key */
    public static List<String> mapKeys(Map<String, Object> map) {
        if (map == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(map.keySet());
    }

    /** 提取 Map 的 values */
    public static List<Object> mapValues(Map<String, Object> map) {
        if (map == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(map.values());
    }

    /** 对 Map 进行排序 */
    public static Map<String, Object> mapSort(Map<String, Object> mapData, Udf sortUdf, Hints hints) {
        if (mapData == null) {
            return Collections.emptyMap();
        }
        List<Object> keySort = listSort(new ArrayList<>(mapData.keySet()), sortUdf, hints);
        Map<String, Object> newMap = new LinkedHashMap<>();
        for (Object key : keySort) {
            String keyStr = key.toString();
            newMap.put(keyStr, mapData.get(keyStr));
        }
        return newMap;
    }

    /** Map 转为 List */
    public static List<Object> map2list(Map<String, Object> mapValue, Udf convert, Hints hints) throws Throwable {
        if (mapValue == null || mapValue.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Object> listData = new ArrayList<>();
        Set<Map.Entry<String, Object>> entrySet = mapValue.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            if (convert != null) {
                listData.add(convert.call(hints, entry.getKey(), entry.getValue()));
            } else {
                ObjectModel objectModel = DomainHelper.newObject();
                objectModel.put("key", entry.getKey());
                objectModel.put("value", entry.getValue());
                listData.add(objectModel);
            }
        }
        return listData;
    }

    /** Map 转为字符串 */
    public static String map2string(Map<String, Object> mapValue, String joinStr, Udf convert, Hints hints) throws Throwable {
        if (mapValue == null || mapValue.size() == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : mapValue.keySet()) {
            Object value = mapValue.get(key);
            stringBuilder.append(convert.call(hints, key, value));
            stringBuilder.append(joinStr);
        }
        if (stringBuilder.length() > 0) {
            int joinLength = joinStr.length();
            return stringBuilder.substring(0, stringBuilder.length() - joinLength);
        }
        return stringBuilder.toString();
    }

    /** Map 的 Key 替换  */
    public static Map<String, Object> mapKeyReplace(Map<String, Object> mapValue, Udf replaceKey, Hints hints) throws Throwable {
        if (replaceKey == null || mapValue == null || mapValue.size() == 0) {
            return mapValue;
        }
        //
        Map<String, Object> dataMap = new LinkedHashMap<>();
        Set<Map.Entry<String, Object>> entrySet = mapValue.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            String entryKey = entry.getKey();
            Object entryValue = entry.getValue();
            //
            entryKey = String.valueOf(replaceKey.call(hints, entryKey, entryValue));
            dataMap.put(entryKey, entryValue);
        }
        return dataMap;
    }

    /** Map 的 Value 替换  */
    public static Map<String, Object> mapValueReplace(Map<String, Object> mapValue, Udf replaceValue, Hints hints) throws Throwable {
        if (replaceValue == null || mapValue == null || mapValue.size() == 0) {
            return mapValue;
        }
        //
        Map<String, Object> dataMap = new LinkedHashMap<>();
        Set<Map.Entry<String, Object>> entrySet = mapValue.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            String entryKey = entry.getKey();
            Object entryValue = entry.getValue();
            //
            entryValue = replaceValue.call(hints, entryKey, entryValue);
            dataMap.put(entryKey, entryValue);
        }
        return dataMap;
    }
}