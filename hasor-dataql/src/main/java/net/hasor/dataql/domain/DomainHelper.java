package net.hasor.dataql.domain;
import net.hasor.dataql.UDF;
import net.hasor.dataql.runtime.operator.OperatorUtils;
import net.hasor.utils.ref.BeanMap;

import java.util.*;

public class DomainHelper {
    public static ListModel newList() {
        return new ListModel();
    }

    public static ObjectModel newObject() {
        return new ObjectModel();
    }

    public static DataModel convertTo(Object object) {
        if (object instanceof DataModel) {
            // 已经是 DataModel
            return (DataModel) object;
        } else if (object == null) {
            // 基础类型：空
            return ValueModel.NULL;
        } else if (OperatorUtils.isBoolean(object)) {
            // 基础类型：boolean
            if ((boolean) object) {
                return ValueModel.TRUE;
            } else {
                return ValueModel.FALSE;
            }
        } else if (object instanceof CharSequence) {
            // 基础类型：字符串
            return new ValueModel(String.valueOf(object));
        } else if (OperatorUtils.isNumber(object)) {
            // 基础类型：数字
            return new ValueModel(object);
        } else if (object instanceof Date) {
            // 外部类型：时间
            return new ValueModel(((Date) object).getTime());
        } else if (object.getClass().isEnum()) {
            // 外部类型：枚举 -> ValueModel（字符串）
            return new ValueModel(((Enum<?>) object).name());
        } else if (object instanceof Map) {
            // 外部类型：Map -> ObjectModel
            Map mapData = (Map) object;
            Set entrySet = mapData.entrySet();
            ObjectModel objectModel = new ObjectModel();
            for (Object entry : entrySet) {
                if (entry instanceof Map.Entry) {
                    Object key = ((Map.Entry) entry).getKey();
                    Object val = ((Map.Entry) entry).getValue();
                    objectModel.put(key.toString(), convertTo(val));
                }
            }
            return objectModel;
        } else if (object.getClass().isArray()) {
            // 外部类型：数组 -> ListModel
            return new ListModel(Arrays.asList((Object[]) object));
        } else if (object instanceof Collection) {
            // 外部类型：集合 -> ListModel
            return new ListModel((Collection<?>) object);
        } else if (object instanceof UDF) {
            // 外部类型：UDF -> CallModel
            return new UdfModel((UDF) object);
        } else {
            // 外部类型：Bean -> ObjectModel
            BeanMap beanMap = new BeanMap(object);
            ObjectModel objectModel = new ObjectModel();
            for (String entryKey : beanMap.keySet()) {
                if ("class".equals(entryKey)) {
                    objectModel.put(entryKey, convertTo(beanMap.getBean().getClass().getName()));
                } else {
                    objectModel.put(entryKey, convertTo(beanMap.get(entryKey)));
                }
            }
            return objectModel;
        }
    }
}
