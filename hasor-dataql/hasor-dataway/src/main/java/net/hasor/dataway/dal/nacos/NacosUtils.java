package net.hasor.dataway.dal.nacos;
import net.hasor.dataway.dal.FieldDef;

import java.util.*;

class NacosUtils {
    public static Map<FieldDef, String> mapToDef(Map<String, Object> entMap) {
        if (entMap == null) {
            return null;
        }
        final Map<FieldDef, String> dataMap = new HashMap<>();
        entMap.forEach((key, value) -> {
            for (FieldDef def : FieldDef.values()) {
                if (def.name().equalsIgnoreCase(key)) {
                    dataMap.put(def, (value == null) ? "" : value.toString());
                }
            }
        });
        return dataMap;
    }

    public static Map<String, Object> defToMap(Map<FieldDef, String> entMap) {
        if (entMap == null) {
            return null;
        }
        final Map<String, Object> dataMap = new HashMap<>();
        entMap.forEach((key, value) -> {
            if (value == null) {
                dataMap.put(key.name().toUpperCase(), "");
            } else {
                dataMap.put(key.name().toUpperCase(), value);
            }
        });
        return dataMap;
    }

    public static String evalDirectoryKey(String dat) {
        return "DIRECTORY_" + dat;
    }

    /** 留下最新的 ApiJson */
    public static Map<String, ApiJson> removeDuplicate(List<ApiJson> indexDirectory) {
        if (indexDirectory == null || indexDirectory.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, ApiJson> pretreatment = new LinkedHashMap<>();
        for (ApiJson apiJson : indexDirectory) {
            String jsonId = apiJson.getId();
            // 预处理中如果存在，那么比对一下留下最新的
            if (pretreatment.containsKey(jsonId)) {
                ApiJson dataEnt = pretreatment.get(jsonId);
                if (apiJson.getTime() >= dataEnt.getTime()) {
                    pretreatment.put(jsonId, apiJson);
                }
            } else {
                pretreatment.put(jsonId, apiJson);
            }
        }
        return pretreatment;
    }
}
