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
import net.hasor.core.Inject;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataway.authorization.AuthorizationType;
import net.hasor.dataway.authorization.RefAuthorization;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.dal.Constant;
import net.hasor.dataway.dal.EntityDef;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.dataway.dal.HeaderData;
import net.hasor.dataway.service.ApiCallService;
import net.hasor.dataway.service.schema.types.Type;
import net.hasor.dataway.service.schema.types.TypesUtils;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.CallSource;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.interceptor.Transactional;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.invoker.HttpParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 冒烟测试（于执行不同的是，冒烟测试的执行脚本和脚本类型信息来源于已保存的）
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/smoke")
@RefAuthorization(AuthorizationType.ApiEdit)
public class SmokeController extends BasicController implements Constant {
    protected static Logger         logger = LoggerFactory.getLogger(SmokeController.class);
    @Inject
    private          ApiCallService apiCallService;

    @Post
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doSmoke(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody, Invoker invoker) throws Throwable {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        //
        // .查询接口数据
        Map<FieldDef, String> objectBy = this.dataAccessLayer.getObjectBy(EntityDef.INFO, FieldDef.ID, apiId);
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setCallSource(CallSource.InterfaceUI);
        apiInfo.setApiID(objectBy.get(FieldDef.ID));
        apiInfo.setReleaseID("");
        apiInfo.setMethod(objectBy.get(FieldDef.METHOD));
        apiInfo.setApiPath(objectBy.get(FieldDef.PATH));
        String strCodeValue = objectBy.get(FieldDef.SCRIPT);
        //
        // .准备参数
        apiInfo.setParameterMap(JSON.parseObject(objectBy.get(FieldDef.REQ_BODY_SAMPLE)));
        apiInfo.setOptionMap(JSON.parseObject(objectBy.get(FieldDef.OPTION)));
        //
        // 把 Header 设置到 HttpParameter 中。
        List<HeaderData> headerDataList = JSON.parseArray(objectBy.get(FieldDef.REQ_HEADER_SAMPLE), HeaderData.class);
        Map<String, String> reqHeader = new HashMap<>();
        Map<String, List<String>> reqHeaderList = new HashMap<>();
        headerDataList.forEach(headerData -> {
            if (headerData.isChecked()) {
                reqHeader.put(headerData.getName(), headerData.getValue());
                reqHeaderList.put(headerData.getName(), Collections.singletonList(headerData.getValue()));
            }
        });
        HttpParameters.clearReplaceHeaderArrayMap(reqHeaderList);// 替换 Header
        //
        // .执行调用
        Object resData = this.apiCallService.doCallWithoutError(apiInfo, jsonParam -> strCodeValue);
        this.updateSchema(apiId, objectBy,              //
                reqHeader, apiInfo.getParameterMap(),   //
                Collections.emptyMap(), resData         //
        );
        //
        DatawayUtils.responseData(//
                this.spiTrigger, apiInfo, invoker.getMimeType("json"), invoker, resData//
        );
    }

    private void updateSchema(String apiID, Map<FieldDef, String> objectBy, //
            Map<String, String> reqHeader, Map<String, Object> reqData,     //
            Map<String, String> resHeader, Object resData) {
        //
        AtomicInteger atomicInteger = new AtomicInteger();
        Type reqHeaderType = TypesUtils.extractType(ReqHeadSchemaPrefix.apply(apiID), atomicInteger, DomainHelper.convertTo(reqHeader));
        Type reqBodyType = TypesUtils.extractType(ReqBodySchemaPrefix.apply(apiID), atomicInteger, DomainHelper.convertTo(reqData));
        Type resHeaderType = TypesUtils.extractType(ResHeadSchemaPrefix.apply(apiID), atomicInteger, DomainHelper.convertTo(resHeader));
        Type resBodyType = TypesUtils.extractType(ResBodySchemaPrefix.apply(apiID), atomicInteger, DomainHelper.convertTo(resData));
        //
        objectBy.put(FieldDef.REQ_HEADER_SCHEMA, TypesUtils.toJsonSchema(reqHeaderType, false).toJSONString());
        objectBy.put(FieldDef.REQ_BODY_SCHEMA, TypesUtils.toJsonSchema(reqBodyType, false).toJSONString());
        //
        objectBy.put(FieldDef.RES_HEADER_SCHEMA, TypesUtils.toJsonSchema(resHeaderType, false).toJSONString());
        objectBy.put(FieldDef.RES_BODY_SCHEMA, TypesUtils.toJsonSchema(resBodyType, false).toJSONString());
        objectBy.put(FieldDef.RES_HEADER_SAMPLE, JSON.toJSONString(resHeader));
        objectBy.put(FieldDef.RES_BODY_SAMPLE, JSON.toJSONString(resData, true));
        //
        this.dataAccessLayer.updateObjectBy(EntityDef.INFO, FieldDef.ID, apiID, objectBy);
        //logger.info("update schema apiID = " + apiID, ", result = " + JSON.toJSONString(result));
    }
}