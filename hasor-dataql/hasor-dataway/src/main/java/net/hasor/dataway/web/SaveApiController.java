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
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.daos.impl.EntityDef;
import net.hasor.dataway.daos.impl.FieldDef;
import net.hasor.dataway.domain.ApiStatusEnum;
import net.hasor.dataway.domain.ApiTypeEnum;
import net.hasor.dataway.domain.Constant;
import net.hasor.dataway.domain.HeaderData;
import net.hasor.dataway.service.CheckService;
import net.hasor.dataway.service.schema.types.Type;
import net.hasor.dataway.service.schema.types.TypesUtils;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.interceptor.Transactional;
import net.hasor.utils.convert.ConverterUtils;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 把编辑的结果保存起来。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/save-api")
@RefAuthorization(AuthorizationType.ApiEdit)
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class SaveApiController extends BasicController implements Constant {
    @Inject
    private CheckService checkService;

    @Post
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result<Object> doSave(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody) {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        //
        String apiPath = (String) requestBody.get("apiPath");
        Map<FieldDef, String> apiInfo = null;
        boolean useCreate = false;
        if ("-1".equalsIgnoreCase(apiId)) {
            this.checkService.checkApi(apiPath);
            apiInfo = new LinkedHashMap<>();
            apiInfo.put(FieldDef.ID, this.dataAccessLayer.generateId(EntityDef.INFO));
            apiInfo.put(FieldDef.METHOD, (String) requestBody.get("select"));
            apiInfo.put(FieldDef.PATH, apiPath);
            apiInfo.put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Editor.typeNum()));
            apiInfo.put(FieldDef.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
            useCreate = true;
        } else {
            apiInfo = this.dataAccessLayer.getObjectBy(EntityDef.INFO, FieldDef.ID, apiId);
            ApiStatusEnum statusEnum = ApiStatusEnum.typeOf(apiInfo.get(FieldDef.STATUS));
            if (statusEnum != ApiStatusEnum.Editor) {
                apiInfo.put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Changes.typeNum()));
            }
            useCreate = false;
        }
        //
        AtomicInteger atomicInteger = new AtomicInteger();
        ApiTypeEnum codeType = Objects.requireNonNull(ApiTypeEnum.typeOf(requestBody.get("codeType")), "Undefined code type");
        String codeValue = (String) requestBody.get("codeValue");
        apiInfo.put(FieldDef.API_ID, apiInfo.get(FieldDef.ID));
        apiInfo.put(FieldDef.TYPE, codeType.typeString());
        apiInfo.put(FieldDef.COMMENT, (String) requestBody.get("comment"));
        apiInfo.put(FieldDef.SCRIPT, codeValue);
        apiInfo.put(FieldDef.SCRIPT_ORI, codeValue);
        //
        List<Map<String, Object>> headerData = (List) requestBody.get("headerData");
        List<HeaderData> headerDataList = new ArrayList<>();
        Map<String, String> headerDataMap = new LinkedHashMap<>();
        headerData.forEach(dataMap -> {
            HeaderData dat = new HeaderData();
            dat.setChecked((Boolean) ConverterUtils.convert(Boolean.TYPE, dataMap.get("checked")));
            dat.setName(dataMap.get("name").toString());
            dat.setValue(dataMap.get("value").toString());
            headerDataList.add(dat);
            if (dat.isChecked()) {
                headerDataMap.put(dat.getName(), dat.getValue());
            }
        });
        Type rehType = TypesUtils.extractType(ReqHeadSchemaPrefix.apply(apiId), atomicInteger, DomainHelper.convertTo(headerDataMap));
        apiInfo.put(FieldDef.REQ_HEADER_SCHEMA, TypesUtils.toJsonSchema(rehType, false).toJSONString());
        apiInfo.put(FieldDef.REQ_HEADER_SAMPLE, JSON.toJSONString(headerDataList));
        //
        String requestJsonBody = (String) requestBody.get("requestBody");
        Object sampleObj = JSON.parseObject(requestJsonBody);
        Type reqType = TypesUtils.extractType(ReqBodySchemaPrefix.apply(apiId), atomicInteger, DomainHelper.convertTo(sampleObj));
        apiInfo.put(FieldDef.REQ_BODY_SCHEMA, TypesUtils.toJsonSchema(reqType, false).toJSONString());
        apiInfo.put(FieldDef.REQ_BODY_SAMPLE, requestJsonBody);
        //
        apiInfo.put(FieldDef.RES_HEADER_SCHEMA, null);
        apiInfo.put(FieldDef.RES_HEADER_SAMPLE, null);
        apiInfo.put(FieldDef.RES_BODY_SCHEMA, null);
        apiInfo.put(FieldDef.RES_BODY_SAMPLE, null);
        //
        Map<String, Object> optionInfo = (Map) requestBody.get("optionInfo");
        apiInfo.put(FieldDef.OPTION, JSON.toJSONString(optionInfo));
        apiInfo.put(FieldDef.GMT_TIME, String.valueOf(System.currentTimeMillis()));
        //
        boolean result = useCreate ?//
                this.dataAccessLayer.createObjectBy(EntityDef.INFO, apiInfo) ://
                this.dataAccessLayer.updateObjectBy(EntityDef.INFO, FieldDef.ID, apiId, apiInfo);
        if (result) {
            return Result.of(apiId);
        } else {
            throw new IllegalArgumentException("db update failed.");
        }
    }
}