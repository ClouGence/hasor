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
import java.util.*;
import java.util.stream.Collectors;

import static net.hasor.dataway.dal.FieldDef.ID;
import static net.hasor.dataway.dal.FieldDef.PATH;

/**
 * DAO 层接口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
@Singleton
public class InterfaceReleaseDal extends AbstractDal {
    /** INFO 表中的唯一索引列 */
    protected static final Map<FieldDef, String> pubIndexColumn = new HashMap<FieldDef, String>() {{
        put(FieldDef.ID, "pub_id");
    }};

    private static Map<FieldDef, String> mapToDef(Map<String, Object> entMap) {
        Map<FieldDef, String> dataMap = new HashMap<>();
        dataMap.put(ID, entMap.get("pub_id").toString());
        dataMap.put(FieldDef.API_ID, entMap.get("pub_api_id").toString());
        dataMap.put(FieldDef.METHOD, entMap.get("pub_method").toString());
        dataMap.put(PATH, entMap.get("pub_path").toString());
        dataMap.put(FieldDef.STATUS, entMap.get("pub_status").toString());
        if (entMap.containsKey("pub_comment")) {
            Object pubComment = entMap.get("pub_comment");
            dataMap.put(FieldDef.COMMENT, pubComment == null ? "" : pubComment.toString());
        }
        dataMap.put(FieldDef.TYPE, entMap.get("pub_type").toString());
        //
        if (entMap.containsKey("pub_script")) {
            dataMap.put(FieldDef.SCRIPT, entMap.get("pub_script").toString());
        }
        if (entMap.containsKey("pub_script_ori")) {
            dataMap.put(FieldDef.SCRIPT_ORI, entMap.get("pub_script_ori").toString());
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
            dataMap.put(FieldDef.REQ_HEADER_SCHEMA, (requestHeaderSchema != null) ? requestHeaderSchema.toJSONString() : null);
            dataMap.put(FieldDef.REQ_BODY_SCHEMA, (requestBodySchema != null) ? requestBodySchema.toJSONString() : null);
            dataMap.put(FieldDef.RES_HEADER_SCHEMA, (responseHeaderSchema != null) ? responseHeaderSchema.toJSONString() : null);
            dataMap.put(FieldDef.RES_BODY_SCHEMA, (responseBodySchema != null) ? responseBodySchema.toJSONString() : null);
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
            dataMap.put(FieldDef.REQ_HEADER_SAMPLE, (requestHeader == null) ? "[]" : requestHeader);
            dataMap.put(FieldDef.REQ_BODY_SAMPLE, StringUtils.isBlank(requestBody) ? "{}" : requestBody);
            dataMap.put(FieldDef.RES_HEADER_SAMPLE, (responseHeader == null) ? "[]" : responseHeader);
            dataMap.put(FieldDef.RES_BODY_SAMPLE, StringUtils.isBlank(responseBody) ? "{}" : responseBody);
        }
        //
        // PREPARE_HINT
        //
        Object apiOption = entMap.get("pub_option");
        dataMap.put(FieldDef.OPTION, apiOption != null ? apiOption.toString() : null);
        //dataMap.put(FieldDef.CREATE_TIME, String.valueOf(((Date) entMap.get("api_create_time")).getTime()));
        //dataMap.put(FieldDef.GMT_TIME, String.valueOf(((Date) entMap.get("api_gmt_time")).getTime()));
        dataMap.put(FieldDef.RELEASE_TIME, String.valueOf(((Date) entMap.get("pub_release_time")).getTime()));
        //
        return dataMap;
    }

    private static Map<String, Object> defToMap(Map<FieldDef, String> entMap) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.computeIfAbsent("pub_id", s -> entMap.get(FieldDef.ID));
        dataMap.computeIfAbsent("pub_api_id", s -> entMap.get(FieldDef.API_ID));
        dataMap.computeIfAbsent("pub_method", s -> entMap.get(FieldDef.METHOD));
        dataMap.computeIfAbsent("pub_path", s -> entMap.get(FieldDef.PATH));
        dataMap.computeIfAbsent("pub_status", s -> entMap.get(FieldDef.STATUS));
        dataMap.computeIfAbsent("pub_comment", s -> entMap.get(FieldDef.COMMENT));
        dataMap.computeIfAbsent("pub_type", s -> entMap.get(FieldDef.TYPE));
        dataMap.computeIfAbsent("pub_script", s -> entMap.get(FieldDef.SCRIPT));
        dataMap.computeIfAbsent("pub_script_ori", s -> entMap.get(FieldDef.SCRIPT_ORI));
        //
        dataMap.computeIfAbsent("pub_schema", s -> {
            StringBuilder schemaData = new StringBuilder();
            schemaData.append("{");
            schemaData.append("\"requestHeader\":" + entMap.get(FieldDef.REQ_HEADER_SCHEMA) + ",");
            schemaData.append("\"requestBody\":" + entMap.get(FieldDef.REQ_BODY_SCHEMA) + ",");
            schemaData.append("\"responseHeader\":" + entMap.get(FieldDef.RES_HEADER_SCHEMA) + ",");
            schemaData.append("\"responseBody\":" + entMap.get(FieldDef.RES_BODY_SCHEMA));
            schemaData.append("}");
            return schemaData.toString();
        });
        //
        dataMap.computeIfAbsent("pub_sample", s -> {
            StringBuffer sampleData = new StringBuffer();
            sampleData.append("{");
            sampleData.append("\"requestHeader\":" + JSON.toJSONString(entMap.get(FieldDef.REQ_HEADER_SAMPLE)) + ",");
            sampleData.append("\"requestBody\":" + JSON.toJSONString(entMap.get(FieldDef.REQ_BODY_SAMPLE)) + ",");
            sampleData.append("\"responseHeader\":" + JSON.toJSONString(entMap.get(FieldDef.RES_HEADER_SAMPLE)) + ",");
            sampleData.append("\"responseBody\":" + JSON.toJSONString(entMap.get(FieldDef.RES_BODY_SAMPLE)));
            sampleData.append("}");
            return sampleData.toString();
        });
        //
        dataMap.computeIfAbsent("pub_option", s -> entMap.get(FieldDef.OPTION));
        dataMap.computeIfAbsent("pub_release_time", s -> entMap.get(FieldDef.RELEASE_TIME));
        return dataMap;
    }

    public Map<FieldDef, String> getObjectBy(FieldDef indexKey, String index) throws SQLException {
        // .如果是 Path 那么要先查询最近一次发布ID
        if (indexKey == PATH) {
            String sqlQuery = "" + //
                    "select pub_id,pub_api_id from interface_release " +//
                    "where pub_status != ? and pub_path = ? " + //
                    "order by pub_release_time desc";//
            List<Object> data = new ArrayList<>();
            data.add(targetConvert.get(columnTypes.get("pub_status")).apply(String.valueOf(ApiStatusEnum.Delete.typeNum())));
            data.add(targetConvert.get(columnTypes.get("pub_path")).apply(index));
            //
            List<Map<String, Object>> tempList = this.jdbcTemplate.queryForList(sqlQuery, data.toArray());
            if (tempList == null || tempList.isEmpty()) {
                return null;
            }
            Map<String, Object> objectMap = tempList.get(0);
            index = objectMap.get("pub_id").toString();
        }
        // 根据发布 ID 查询
        String sqlQuery = "" +//
                "select * from interface_release " +//
                "where pub_status != ? and pub_id = ?";
        List<Object> data = new ArrayList<>();
        data.add(targetConvert.get(columnTypes.get("pub_status")).apply(String.valueOf(ApiStatusEnum.Delete.typeNum())));
        data.add(targetConvert.get(columnTypes.get("pub_id")).apply(index));
        Map<String, Object> objectMap = this.jdbcTemplate.queryForMap(sqlQuery, data.toArray());
        //
        return (objectMap != null) ? mapToDef(objectMap) : null;
    }

    public List<Map<FieldDef, String>> listObjectBy(Map<QueryCondition, Object> conditions) throws SQLException {
        String releaseList = "" +//
                "select pub_id,pub_api_id,pub_method,pub_path,pub_status,pub_type,pub_release_time " +//
                "from interface_release " +//
                "where pub_status != -1 and pub_api_id = ? " + //
                "order by pub_release_time desc";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(releaseList, conditions.get(QueryCondition.ApiId));
        return mapList.parallelStream().map(InterfaceReleaseDal::mapToDef).collect(Collectors.toList());
    }

    public boolean deleteObjectBy(FieldDef indexKey, String index) throws SQLException {
        throw new SQLException("table interface_release cannot be modified.");
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
                "update interface_release set " + //
                sqlBuffer.toString() + //
                "where " + pubIndexColumn.get(indexKey) + " = ?";
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
                "insert into interface_release (" + //
                insertColumnBuffer.toString() + //
                ") values (" +//
                insertParamsBuffer.toString() + //
                ");";
        return this.jdbcTemplate.executeUpdate(sqlQuery, insertData.toArray()) > 0;
    }
}