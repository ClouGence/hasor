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
package net.hasor.dataway.schema.types;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataql.domain.ValueModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数组或集合类型
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-05-21
 */
public class TypesUtils {
    public static Type extractType(String prefix, AtomicInteger atomicInteger, DataModel atData) {
        //
        if (atData.isObject()) {
            ObjectModel objectModel = (ObjectModel) atData;
            StrutsType strutsType = autoName(prefix, atomicInteger, new StrutsType());
            List<String> stringList = objectModel.fieldNames();
            Map<String, Type> strutsTypeMap = new LinkedHashMap<>();
            for (String key : stringList) {
                DataModel fieldTypeDataModel = objectModel.get(key);
                Type type = extractType(prefix, atomicInteger, fieldTypeDataModel);
                if (type != null) {
                    strutsTypeMap.put(key, type);
                }
            }
            strutsType.setProperties(strutsTypeMap);
            return strutsType;
        }
        //
        if (atData.isList()) {
            ListModel listModel = (ListModel) atData;
            ArrayType arrayType = autoName(prefix, atomicInteger, new ArrayType());
            Type lastType = null;
            for (DataModel dataModel : listModel.asOri()) {
                if (lastType != null && lastType.getType() == TypeEnum.Map) {
                    break;
                }
                Type type = extractType(prefix, atomicInteger, dataModel);
                if (type == null) {
                    continue;
                }
                if (lastType == null) {
                    lastType = type;
                    continue;
                }
                if (lastType.getType() != type.getType()) {
                    lastType = autoName(prefix, atomicInteger, new MapType());
                } else {
                    lastType = mergeType(lastType, type);
                }
            }
            arrayType.setGenericType(lastType);
            return arrayType;
        }
        //
        if (atData.isValue()) {
            ValueModel valueModel = (ValueModel) atData;
            if (valueModel.isNumber()) {
                NumberType numberType = autoName(prefix, atomicInteger, new NumberType());
                numberType.setDefaultValue(valueModel.asNumber());
                return numberType;
            }
            if (valueModel.isBoolean()) {
                BooleanType booleanType = autoName(prefix, atomicInteger, new BooleanType());
                booleanType.setDefaultValue(valueModel.asBoolean());
                return booleanType;
            }
            if (valueModel.isString()) {
                StringType stringType = autoName(prefix, atomicInteger, new StringType());
                stringType.setDefaultValue(valueModel.asString());
                return stringType;
            }
            if (valueModel.isNull()) {
                return autoName(prefix, atomicInteger, new AnyType());
            }
        }
        //
        if (atData.isUdf()) {
            return null;
        }
        //
        return autoName(prefix, atomicInteger, new AnyType());
    }

    private static <T extends Type> T autoName(String prefix, AtomicInteger atomicInteger, T type) {
        // 只有一些复杂类型才需要设置name，基本类型无需设置name
        if (type.getType() == TypeEnum.Array //
                || type.getType() == TypeEnum.Struts//
                || type.getType() == TypeEnum.Ref//
                || type.getType() == TypeEnum.Map//
        ) {
            type.setName(prefix + atomicInteger.incrementAndGet());
        }
        return type;
    }

    private static Type mergeType(Type fstType, Type secType) {
        TypeEnum fstTypeType = fstType.getType();
        TypeEnum secTypeType = secType.getType();
        if (fstTypeType == secTypeType) {
            if (fstTypeType == TypeEnum.Array) {
                Type fstArrayType = ((ArrayType) fstType).getGenericType();
                Type secArrayType = ((ArrayType) secType).getGenericType();
                return mergeType(fstArrayType, secArrayType);
            }
            if (fstTypeType == TypeEnum.Struts) {
                StrutsType fstMapType = ((StrutsType) fstType);
                StrutsType secMapType = ((StrutsType) secType);
                //
                Map<String, Type> fstFieldTypeMap = fstMapType.getProperties();
                for (Map.Entry<String, Type> ent : secMapType.getProperties().entrySet()) {
                    String key = ent.getKey();
                    if (!fstFieldTypeMap.containsKey(key)) {
                        fstFieldTypeMap.put(key, ent.getValue());
                    } else {
                        Type merged = mergeType(fstFieldTypeMap.get(key), ent.getValue());
                        fstFieldTypeMap.put(key, merged);
                    }
                }
                return fstMapType;
            }
        }
        //
        //
        return fstType;
    }

    public static JSONObject toJsonSchema(Type type, boolean useRef) {
        Map<String, JSONObject> defTypes = new LinkedHashMap<>();
        JSONObject root = toJsonSchema(defTypes, type, useRef);
        root.put("$schema", "http://json-schema.org/draft-04/schema#");
        if (!defTypes.isEmpty()) {
            root.put("definitions", defTypes);
        }
        return root;
    }

    private static JSONObject toJsonSchema(Map<String, JSONObject> defTypes, Type type, boolean useRef) {
        if (type.getType() == TypeEnum.String) {
            JSONObject root = new JSONObject();
            root.put("type", "string");
            return root;
        }
        if (type.getType() == TypeEnum.Boolean) {
            JSONObject root = new JSONObject();
            root.put("type", "boolean");
            return root;
        }
        if (type.getType() == TypeEnum.Number) {
            JSONObject root = new JSONObject();
            root.put("type", "number");
            return root;
        }
        if (type.getType() == TypeEnum.Struts) {
            JSONObject root = new JSONObject();
            root.put("type", "object");
            StrutsType strutsType = (StrutsType) type;
            final JSONObject propertiesJson = new JSONObject();
            strutsType.getProperties().forEach((key, propType) -> {
                propertiesJson.put(key, toJsonSchema(defTypes, propType, useRef));
            });
            root.put("properties", propertiesJson);
            //
            if (useRef) {
                defTypes.put(strutsType.getName(), root);
                JSONObject ref = new JSONObject();
                ref.put("$ref", "#/definitions/" + strutsType.getName());
                root = ref;
            }
            return root;
        }
        if (type.getType() == TypeEnum.Array) {
            JSONObject root = new JSONObject();
            root.put("type", "array");
            Type arrayType = ((ArrayType) type).getGenericType();
            root.put("items", toJsonSchema(defTypes, arrayType, useRef));
            return root;
        }
        if (type.getType() == TypeEnum.Ref) {
            JSONObject root = new JSONObject();
            RefType arrayType = (RefType) type;
            root.put("$ref", "#/definitions/" + arrayType.getRefType());
            return root;
        }
        if (type.getType() == TypeEnum.Map) {
            JSONObject root = new JSONObject();
            root.put("type", "object");
            return root;
        }
        //
        JSONObject root = new JSONObject();
        root.put("type", new JSONArray() {{
            add("string");
            add("boolean");
            add("number");
            add("object");
            add("array");
            add("null");
        }});
        return root;
    }
}