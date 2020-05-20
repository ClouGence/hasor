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
package net.hasor.dataway.web;
import com.alibaba.fastjson.JSON;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.*;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.daos.ApiDetailQuery;
import net.hasor.dataway.schema.types.*;
import net.hasor.dataway.service.ApiCallService;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 冒烟测试（于执行不同的是，冒烟测试的执行脚本和脚本类型信息来源于已保存的）
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/smoke")
public class SmokeController extends BasicController {
    @Inject
    private ApiCallService apiCallService;

    @Post
    public void doSmoke(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody, Invoker invoker) throws Throwable {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        //
        // .查询接口数据
        QueryResult queryDetail = new ApiDetailQuery(this.dataQL).execute(new HashMap<String, String>() {{
            put("apiId", apiId);
        }});
        ObjectModel objectModel = ((ObjectModel) queryDetail.getData());
        //
        // .获取API信息
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setPerform(true);
        apiInfo.setApiID(apiId);
        apiInfo.setReleaseID("");
        apiInfo.setMethod(objectModel.getValue("select").asString());
        apiInfo.setApiPath(objectModel.getValue("path").asString());
        String strCodeType = objectModel.getValue("codeType").asString();
        String strCodeValue = objectModel.getObject("codeInfo").getValue("codeValue").asString();
        //
        // .准备参数
        String jsonParamValue = objectModel.getObject("codeInfo").getValue("requestBody").asString();
        jsonParamValue = (StringUtils.isBlank(jsonParamValue)) ? "{}" : jsonParamValue;
        apiInfo.setParameterMap(JSON.parseObject(jsonParamValue));
        apiInfo.setOptionMap(objectModel.getObject("optionData").unwrap());
        //
        // .执行调用
        Object objectMap = this.apiCallService.doCallWithoutError(apiInfo, jsonParam -> {
            if ("sql".equalsIgnoreCase(strCodeType)) {
                // .如果是 SQL 还需要进行代码替换
                return DatawayUtils.evalCodeValueForSQL(strCodeValue, jsonParam);
            } else {
                // .如果是 DataQL 那么就返回
                return strCodeValue;
            }
        });
        this.updateSchema(apiId, apiInfo.getParameterMap(), objectMap);
        //
        DatawayUtils.responseData(//
                this.spiTrigger, apiInfo, invoker.getMimeType("json"), invoker, objectMap//
        );
    }

    private void updateSchema(String apiID, Object requestData, Object responseData) {
        DataModel dataModel = DomainHelper.convertTo(responseData);
        StrutsType strutsType = new StrutsType();
        AtomicInteger atomicInteger = new AtomicInteger();
        //
        Map<String, Type> finalMap = new LinkedHashMap<>();
        Type type = this.appendSchema(atomicInteger, dataModel);
        System.out.println();
    }
    //
    // {
    //    "$schema": "http://json-schema.org/draft-04/schema#",
    //    "title": "TestInfo",
    //    "description": "some information about test",
    //    "type": "object",
    //    "properties": {
    //        "name": {
    //            "description": "Name of the test",
    //            "type": "string"
    //        },
    //        "age": {
    //            "description": "age of test",
    //            "type": "integer"
    //        }
    //    },
    //    "required": [
    //        "name"
    //    ]
    // }
    //

    private Type appendSchema(AtomicInteger atomicInteger, DataModel atData) {
        //
        if (atData.isObject()) {
            ObjectModel objectModel = (ObjectModel) atData;
            StrutsType strutsType = autoName(atomicInteger, new StrutsType());
            List<String> stringList = objectModel.fieldNames();
            Map<String, Type> strutsTypeMap = new LinkedHashMap<>();
            for (String key : stringList) {
                DataModel fieldTypeDataModel = objectModel.get(key);
                Type type = appendSchema(atomicInteger, fieldTypeDataModel);
                if (type != null) {
                    strutsTypeMap.put(key, type);
                }
            }
            strutsType.setFieldNames(new ArrayList<>(strutsTypeMap.keySet()));
            strutsType.setFieldTypeMap(strutsTypeMap);
            return strutsType;
        }
        //
        if (atData.isList()) {
            ListModel listModel = (ListModel) atData;
            ArrayType arrayType = autoName(atomicInteger, new ArrayType());
            Type lastType = null;
            for (DataModel dataModel : listModel.asOri()) {
                if (lastType != null && lastType.getType() == TypeEnum.Map) {
                    break;
                }
                Type type = appendSchema(atomicInteger, dataModel);
                if (type == null) {
                    continue;
                }
                if (lastType == null) {
                    lastType = type;
                    continue;
                }
                if (lastType.getType() != type.getType()) {
                    lastType = autoName(atomicInteger, new MapType());
                } else {
                    lastType = mergeType(lastType, type);
                }
            }
            arrayType.setGenricType(lastType);
            return arrayType;
        }
        //
        if (atData.isValue()) {
            ValueModel valueModel = (ValueModel) atData;
            if (valueModel.isNumber()) {
                NumberType numberType = autoName(atomicInteger, new NumberType());
                numberType.setDefaultValue(valueModel.asNumber());
                return numberType;
            }
            if (valueModel.isBoolean()) {
                BooleanType booleanType = autoName(atomicInteger, new BooleanType());
                booleanType.setDefaultValue(valueModel.asBoolean());
                return booleanType;
            }
            if (valueModel.isString()) {
                StringType stringType = autoName(atomicInteger, new StringType());
                stringType.setDefaultValue(valueModel.asString());
                return stringType;
            }
            if (valueModel.isNull()) {
                StringType stringType = autoName(atomicInteger, new StringType());
                stringType.setDefaultValue(null);
                return stringType;
            }
        }
        return null;
    }

    private static <T extends Type> T autoName(AtomicInteger atomicInteger, T type) {
        type.setName("Type_" + atomicInteger.incrementAndGet());
        return type;
    }

    private Type mergeType(Type fstType, Type secType) {
        TypeEnum fstTypeType = fstType.getType();
        if (fstTypeType == TypeEnum.Array) {
            Type fstArrayType = ((ArrayType) fstType).getGenricType();
            Type secArrayType = ((ArrayType) secType).getGenricType();
            return mergeType(fstArrayType, secArrayType);
        }
        if (fstTypeType == TypeEnum.Struts) {
            StrutsType fstMapType = ((StrutsType) fstType);
            StrutsType secMapType = ((StrutsType) secType);
            //
            Map<String, Type> fstFieldTypeMap = fstMapType.getFieldTypeMap();
            for (Map.Entry<String, Type> ent : secMapType.getFieldTypeMap().entrySet()) {
                String key = ent.getKey();
                if (!fstFieldTypeMap.containsKey(key)) {
                    fstFieldTypeMap.put(key, ent.getValue());
                } else {
                    Type merged = mergeType(fstFieldTypeMap.get(key), ent.getValue());
                    fstFieldTypeMap.put(key, merged);
                }
            }
            fstMapType.setFieldNames(new ArrayList<>(fstMapType.getFieldTypeMap().keySet()));
            return fstMapType;
        }
        return fstType;
    }
}