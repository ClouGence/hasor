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
import net.hasor.core.Singleton;
import net.hasor.dataway.dal.ApiStatusEnum;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.dataway.dal.QueryCondition;
import net.hasor.utils.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.hasor.dataway.dal.FieldDef.*;

/**
 * 数据库存储层访问接口 - interface_release 表
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-11
 */
@Singleton
public class InterfaceReleaseDal extends AbstractDal {
    /** INFO 表中的唯一索引列 */
    protected static final Map<FieldDef, String> pubIndexColumn = new HashMap<FieldDef, String>() {{
        put(ID, "pub_id");
        put(API_ID, "pub_api_id");
        put(PATH, "pub_path");
    }};

    private static Map<FieldDef, String> mapToDef(Map<String, Object> entMap) {
        Map<FieldDef, String> dataMap = new HashMap<>();
        dataMap.put(ID, entMap.get("pub_id").toString());
        dataMap.put(API_ID, entMap.get("pub_api_id").toString());
        dataMap.put(METHOD, entMap.get("pub_method").toString());
        dataMap.put(PATH, entMap.get("pub_path").toString());
        dataMap.put(STATUS, entMap.get("pub_status").toString());
        if (entMap.containsKey("pub_comment")) {
            Object pubComment = entMap.get("pub_comment");
            dataMap.put(COMMENT, pubComment == null ? "" : pubComment.toString());
        }
        dataMap.put(TYPE, entMap.get("pub_type").toString());
        //
        if (entMap.containsKey("pub_script")) {
            dataMap.put(SCRIPT, entMap.get("pub_script").toString());
        }
        if (entMap.containsKey("pub_script_ori")) {
            dataMap.put(SCRIPT_ORI, entMap.get("pub_script_ori").toString());
        }
        //
        if (entMap.containsKey("pub_schema")) {
            JSONObject jsonObject = JSON.parseObject(entMap.get("pub_schema").toString());
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
        if (entMap.containsKey("pub_sample")) {
            JSONObject sampleObject = JSON.parseObject(entMap.get("pub_sample").toString());
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
        // PREPARE_HINT
        //
        Object apiOption = entMap.get("pub_option");
        dataMap.put(OPTION, apiOption != null ? apiOption.toString() : "{}");
        dataMap.put(RELEASE_TIME, entMap.get("pub_release_time").toString());
        //
        return dataMap;
    }

    private static Map<String, Object> defToMap(Map<FieldDef, String> entMap) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.computeIfAbsent("pub_id", s -> entMap.get(ID));
        dataMap.computeIfAbsent("pub_api_id", s -> entMap.get(API_ID));
        dataMap.computeIfAbsent("pub_method", s -> entMap.get(METHOD));
        dataMap.computeIfAbsent("pub_path", s -> entMap.get(PATH));
        dataMap.computeIfAbsent("pub_status", s -> entMap.get(STATUS));
        dataMap.computeIfAbsent("pub_comment", s -> entMap.get(COMMENT));
        dataMap.computeIfAbsent("pub_type", s -> entMap.get(TYPE));
        dataMap.computeIfAbsent("pub_script", s -> entMap.get(SCRIPT));
        dataMap.computeIfAbsent("pub_script_ori", s -> entMap.get(SCRIPT_ORI));
        //
        dataMap.computeIfAbsent("pub_schema", s -> {
            StringBuilder schemaData = new StringBuilder();
            schemaData.append("{");
            schemaData.append("\"requestHeader\":" + entMap.get(REQ_HEADER_SCHEMA) + ",");
            schemaData.append("\"requestBody\":" + entMap.get(REQ_BODY_SCHEMA) + ",");
            schemaData.append("\"responseHeader\":" + entMap.get(RES_HEADER_SCHEMA) + ",");
            schemaData.append("\"responseBody\":" + entMap.get(RES_BODY_SCHEMA));
            schemaData.append("}");
            return schemaData.toString();
        });
        //
        dataMap.computeIfAbsent("pub_sample", s -> {
            StringBuffer sampleData = new StringBuffer();
            sampleData.append("{");
            sampleData.append("\"requestHeader\":" + JSON.toJSONString(entMap.get(REQ_HEADER_SAMPLE)) + ",");
            sampleData.append("\"requestBody\":" + JSON.toJSONString(entMap.get(REQ_BODY_SAMPLE)) + ",");
            sampleData.append("\"responseHeader\":" + JSON.toJSONString(entMap.get(RES_HEADER_SAMPLE)) + ",");
            sampleData.append("\"responseBody\":" + JSON.toJSONString(entMap.get(RES_BODY_SAMPLE)));
            sampleData.append("}");
            return sampleData.toString();
        });
        //
        dataMap.computeIfAbsent("pub_option", s -> entMap.get(OPTION));
        dataMap.computeIfAbsent("pub_release_time", s -> entMap.get(RELEASE_TIME));
        return dataMap;
    }

    public Map<FieldDef, String> getObjectBy(FieldDef indexKey, String indexValue) throws SQLException {
        // .如果是 Path 那么要先查询最近一次发布ID
        if (indexKey == PATH) {
            String sqlQuery = "" + //
                    "select pub_id,pub_api_id from interface_release " +//
                    "where pub_status != ? and pub_path = ? " + //
                    "order by pub_release_time desc";//
            List<Object> data = new ArrayList<>();
            data.add(String.valueOf(ApiStatusEnum.Delete.typeNum()));
            data.add(indexValue);
            //
            List<Map<String, Object>> tempList = this.jdbcTemplate.queryForList(sqlQuery, data.toArray());
            if (tempList == null || tempList.isEmpty()) {
                return null;
            }
            Map<String, Object> objectMap = tempList.get(0);
            indexValue = objectMap.get("pub_id").toString();
        }
        // 根据发布 ID 查询
        String sqlQuery = "" +//
                "select * from interface_release " +//
                "where pub_status != ? and pub_id = ?";
        List<Object> data = new ArrayList<>();
        data.add(String.valueOf(ApiStatusEnum.Delete.typeNum()));
        data.add(indexValue);
        Map<String, Object> objectMap = this.jdbcTemplate.queryForMap(sqlQuery, data.toArray());
        //
        return (objectMap != null) ? mapToDef(objectMap) : null;
    }

    public List<Map<FieldDef, String>> listObjectBy(Map<QueryCondition, Object> conditions) throws SQLException {
        String releaseList = "" +//
                "select pub_id,pub_api_id,pub_method,pub_path,pub_status,pub_type,pub_comment,pub_schema,pub_release_time " +//
                "from interface_release ";
        //
        List<Object> data = new ArrayList<>();
        if (conditions.containsKey(QueryCondition.ApiId)) {
            releaseList += "where pub_status != ? and pub_api_id = ? " + //
                    "order by pub_release_time desc";
            data.add(String.valueOf(ApiStatusEnum.Delete.typeNum()));
            data.add(conditions.get(QueryCondition.ApiId).toString());
        } else {
            releaseList += "where pub_status != ? " + //
                    "order by pub_release_time desc";
            data.add(String.valueOf(ApiStatusEnum.Delete.typeNum()));
        }
        //
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(releaseList, data.toArray());
        return mapList.parallelStream().map(InterfaceReleaseDal::mapToDef).collect(Collectors.toList());
    }

    public boolean deleteObject(String id) throws SQLException {
        throw new SQLException("table interface_release cannot be modified.");
    }

    public boolean updateObject(String id, Map<FieldDef, String> newData) throws SQLException {
        List<Object> updateData = new ArrayList<>();
        StringBuffer sqlBuffer = new StringBuffer();
        defToMap(newData).forEach((key, value) -> {
            if (wontUpdateColumn.contains(key.toLowerCase())) {
                return;
            }
            sqlBuffer.append("," + key + " = ? ");
            updateData.add(fixString(value.toString()));
        });
        sqlBuffer.deleteCharAt(0);
        //
        updateData.add(id);
        String sqlQuery = "" + //
                "update interface_release set " + //
                sqlBuffer.toString() + //
                "where pub_id = ?";// TODO 需要在加上一个 乐观锁，用以处理并发导致数据丢失的风险
        return this.jdbcTemplate.executeUpdate(sqlQuery, updateData.toArray()) > 0;
    }

    public boolean createObject(Map<FieldDef, String> newData) throws SQLException {
        List<Object> insertData = new ArrayList<>();
        StringBuffer insertColumnBuffer = new StringBuffer();
        StringBuffer insertParamsBuffer = new StringBuffer();
        defToMap(newData).forEach((key, value) -> {
            insertColumnBuffer.append("," + key);
            insertParamsBuffer.append(",?");
            insertData.add(fixString(value.toString()));
        });
        insertColumnBuffer.deleteCharAt(0);
        insertParamsBuffer.deleteCharAt(0);
        //
        String sqlQuery = "" + //
                "insert into interface_release (" + //
                insertColumnBuffer.toString() + //
                ") values (" +//
                insertParamsBuffer.toString() + //
                ")";
        return this.jdbcTemplate.executeUpdate(sqlQuery, insertData.toArray()) > 0;
    }
}