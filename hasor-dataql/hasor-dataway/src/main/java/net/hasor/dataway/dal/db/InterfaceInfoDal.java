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
package net.hasor.dataway.dal.db;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.dal.ApiTypeEnum;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.dataway.dal.QueryCondition;
import net.hasor.utils.StringUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static net.hasor.dataway.dal.FieldDef.*;

/**
 * DAO 层接口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
public class InterfaceInfoDal extends AbstractDal {
    /** INFO 表中的唯一索引列 */
    private static final Map<FieldDef, String> infoIndexColumn = new HashMap<FieldDef, String>() {{
        put(ID, "api_id");
        put(PATH, "api_path");
    }};

    private static Map<FieldDef, String> mapToDef(Map<String, Object> entMap) {
        Map<FieldDef, String> dataMap = new HashMap<>();
        dataMap.put(ID, String.valueOf(entMap.get("api_id")));
        dataMap.put(API_ID, String.valueOf(entMap.get("api_id")));
        dataMap.put(METHOD, String.valueOf(entMap.get("api_method")));
        dataMap.put(PATH, String.valueOf(entMap.get("api_path")));
        dataMap.put(STATUS, String.valueOf(entMap.get("api_status")));
        Object apiComment = entMap.get("api_comment");
        dataMap.put(COMMENT, apiComment != null ? apiComment.toString() : null);
        dataMap.put(TYPE, String.valueOf(entMap.get("api_type")));
        Object apiOption = entMap.get("api_option");
        dataMap.put(OPTION, apiOption != null ? apiOption.toString() : null);
        dataMap.put(CREATE_TIME, String.valueOf(((Date) entMap.get("api_create_time")).getTime()));
        dataMap.put(GMT_TIME, String.valueOf(((Date) entMap.get("api_gmt_time")).getTime()));
        //
        if (entMap.containsKey("api_schema")) {
            JSONObject jsonObject = JSON.parseObject(entMap.get("api_schema").toString());
            JSONObject requestHeaderSchema = jsonObject.getJSONObject("requestHeader");
            JSONObject requestBodySchema = jsonObject.getJSONObject("requestBody");
            JSONObject responseHeaderSchema = jsonObject.getJSONObject("responseHeader");
            JSONObject responseBodySchema = jsonObject.getJSONObject("responseBody");
            //
            /*4.1.14之前老版本覆盖兼容*/
            if (jsonObject.containsKey("requestSchema") || jsonObject.containsKey("responseSchema")) {
                requestBodySchema = jsonObject.getJSONObject("requestSchema");
                responseBodySchema = jsonObject.getJSONObject("responseSchema");
            }
            //
            dataMap.put(REQ_HEADER_SCHEMA, (requestHeaderSchema != null) ? requestHeaderSchema.toJSONString() : null);
            dataMap.put(REQ_BODY_SCHEMA, (requestBodySchema != null) ? requestBodySchema.toJSONString() : null);
            dataMap.put(RES_HEADER_SCHEMA, (responseHeaderSchema != null) ? responseHeaderSchema.toJSONString() : null);
            dataMap.put(RES_BODY_SCHEMA, (responseBodySchema != null) ? responseBodySchema.toJSONString() : null);
        }
        //
        if (entMap.containsKey("api_sample")) {
            JSONObject sampleObject = JSON.parseObject(entMap.get("api_sample").toString());
            String requestHeader = sampleObject.getString("requestHeader");
            String requestBody = sampleObject.getString("requestBody");
            String responseHeader = sampleObject.getString("responseHeader");
            String responseBody = sampleObject.getString("responseBody");
            //
            /*4.1.14之前老版本覆盖兼容*/
            if (sampleObject.containsKey("headerData")) {
                requestHeader = sampleObject.getJSONArray("headerData").toJSONString();
            }
            //
            dataMap.put(REQ_HEADER_SAMPLE, (requestHeader == null) ? "[]" : requestHeader);
            dataMap.put(REQ_BODY_SAMPLE, StringUtils.isBlank(requestBody) ? "{}" : requestBody);
            dataMap.put(RES_HEADER_SAMPLE, (responseHeader == null) ? "[]" : responseHeader);
            dataMap.put(RES_BODY_SAMPLE, StringUtils.isBlank(responseBody) ? "{}" : responseBody);
        }
        //
        if (entMap.containsKey("api_script")) {
            String scriptOri = entMap.get("api_script").toString();
            String scriptTarget = scriptOri;
            ApiTypeEnum typeEnum = ApiTypeEnum.typeOf(dataMap.get(TYPE));
            if (ApiTypeEnum.SQL == typeEnum) {
                String requestBodySample = dataMap.get(REQ_BODY_SAMPLE);
                Map<String, Object> strRequestBody = JSON.parseObject(requestBodySample);
                strRequestBody = strRequestBody == null ? Collections.emptyMap() : strRequestBody;
                scriptTarget = DatawayUtils.evalCodeValueForSQL(scriptOri, strRequestBody);
            }
            dataMap.put(SCRIPT, scriptTarget);
            dataMap.put(SCRIPT_ORI, scriptOri);
        }
        //
        return dataMap;
    }

    private static Map<String, Object> defToMap(Map<FieldDef, String> entMap) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.computeIfAbsent("api_id", s -> entMap.get(ID));
        dataMap.computeIfAbsent("api_method", s -> entMap.get(METHOD));
        dataMap.computeIfAbsent("api_path", s -> entMap.get(PATH));
        dataMap.computeIfAbsent("api_status", s -> entMap.get(STATUS));
        dataMap.computeIfAbsent("api_comment", s -> entMap.get(COMMENT));
        dataMap.computeIfAbsent("api_type", s -> entMap.get(TYPE));
        dataMap.computeIfAbsent("api_script", s -> entMap.get(SCRIPT_ORI));
        dataMap.computeIfAbsent("api_schema", s -> {
            StringBuilder schemaData = new StringBuilder();
            schemaData.append("{");
            if (entMap.get(REQ_HEADER_SCHEMA) != null) {
                schemaData.append("\"requestHeader\":" + entMap.get(REQ_HEADER_SCHEMA) + ",");
            }
            if (entMap.get(REQ_BODY_SCHEMA) != null) {
                schemaData.append("\"requestBody\":" + entMap.get(REQ_BODY_SCHEMA) + ",");
            }
            if (entMap.get(RES_HEADER_SCHEMA) != null) {
                schemaData.append("\"responseHeader\":" + entMap.get(RES_HEADER_SCHEMA) + ",");
            }
            if (entMap.get(RES_BODY_SCHEMA) != null) {
                schemaData.append("\"responseBody\":" + entMap.get(RES_BODY_SCHEMA) + ",");
            }
            if (schemaData.length() > 1) {
                schemaData.deleteCharAt(schemaData.length() - 1);
            }
            schemaData.append("}");
            return schemaData.toString();
        });
        dataMap.computeIfAbsent("api_sample", s -> {
            StringBuilder sampleData = new StringBuilder();
            sampleData.append("{");
            if (entMap.get(REQ_HEADER_SAMPLE) != null) {
                sampleData.append("\"requestHeader\":" + JSON.toJSONString(entMap.get(REQ_HEADER_SAMPLE)) + ",");
            }
            if (entMap.get(REQ_BODY_SAMPLE) != null) {
                sampleData.append("\"requestBody\":" + JSON.toJSONString(entMap.get(REQ_BODY_SAMPLE)) + ",");
            }
            if (entMap.get(RES_HEADER_SAMPLE) != null) {
                sampleData.append("\"responseHeader\":" + JSON.toJSONString(entMap.get(RES_HEADER_SAMPLE)) + ",");
            }
            if (entMap.get(RES_BODY_SAMPLE) != null) {
                sampleData.append("\"responseBody\":" + JSON.toJSONString(entMap.get(RES_BODY_SAMPLE)) + ",");
            }
            if (sampleData.length() > 1) {
                sampleData.deleteCharAt(sampleData.length() - 1);
            }
            sampleData.append("}");
            return sampleData.toString();
        });
        dataMap.computeIfAbsent("api_option", s -> entMap.get(OPTION));
        dataMap.computeIfAbsent("api_gmt_time", s -> entMap.get(GMT_TIME));
        dataMap.computeIfAbsent("api_create_time", s -> entMap.get(CREATE_TIME));
        //    PREPARE_HINT,//
        return dataMap;
    }

    public Map<FieldDef, String> getObjectBy(FieldDef indexKey, String index) throws SQLException {
        String indexField = infoIndexColumn.get(indexKey);
        if (StringUtils.isBlank(indexField)) {
            throw new SQLException("table interface_info not index " + indexKey.name());
        }
        //
        String sqlQuery = "select * from interface_info where " + indexField + " = ?";
        Map<String, Object> data = this.jdbcTemplate.queryForMap(sqlQuery, index);
        return (data != null) ? mapToDef(data) : null;
    }

    public List<Map<FieldDef, String>> listObjectBy(Map<QueryCondition, Object> conditions) throws SQLException {
        String sqlQuery = "" +//
                "select api_id,api_method,api_path,api_status,api_comment,api_type,api_create_time,api_gmt_time " +//
                "from interface_info " + //
                "order by api_create_time asc";
        //
        List<Map<String, Object>> mapList = this.jdbcTemplate.queryForList(sqlQuery);
        return mapList.parallelStream().map(InterfaceInfoDal::mapToDef).collect(Collectors.toList());
    }

    public boolean deleteObjectBy(FieldDef indexKey, String index) throws SQLException {
        String indexField = infoIndexColumn.get(indexKey);
        if (StringUtils.isBlank(indexField)) {
            throw new SQLException("table interface_info not index " + indexKey.name());
        }
        String sqlQuery = "delete from interface_info where " + infoIndexColumn.get(indexKey) + " = ?";
        return this.jdbcTemplate.executeUpdate(sqlQuery, index) > 0;
    }

    public boolean updateObjectBy(FieldDef indexKey, String index, Map<FieldDef, String> newData) throws SQLException {
        List<Object> updateData = new ArrayList<>();
        StringBuffer sqlBuffer = new StringBuffer();
        defToMap(newData).forEach((key, value) -> {
            if (wontUpdateColumn.contains(key.toLowerCase())) {
                return;
            }
            sqlBuffer.append("," + key + " = ? ");
            updateData.add(targetConvert.get(columnTypes.get(key)).apply(value.toString()));
        });
        sqlBuffer.deleteCharAt(0);
        //
        updateData.add(index);
        String sqlQuery = "" + //
                "update interface_info set " + //
                sqlBuffer.toString() + //
                "where " + infoIndexColumn.get(indexKey) + " = ?";
        return this.jdbcTemplate.executeUpdate(sqlQuery, updateData.toArray()) > 0;
    }

    public boolean createObjectBy(Map<FieldDef, String> newData) throws SQLException {
        List<Object> insertData = new ArrayList<>();
        StringBuffer insertColumnBuffer = new StringBuffer();
        StringBuffer insertParamsBuffer = new StringBuffer();
        defToMap(newData).forEach((key, value) -> {
            insertColumnBuffer.append("," + key);
            insertParamsBuffer.append(",?");
            insertData.add(targetConvert.get(columnTypes.get(key)).apply(value.toString()));
        });
        insertColumnBuffer.deleteCharAt(0);
        insertParamsBuffer.deleteCharAt(0);
        //
        String sqlQuery = "" + //
                "insert into interface_info (" + //
                insertColumnBuffer.toString() + //
                ") values (" +//
                insertParamsBuffer.toString() + //
                ")";
        return this.jdbcTemplate.executeUpdate(sqlQuery, insertData.toArray()) > 0;
    }
}