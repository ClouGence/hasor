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
package net.hasor.dataway.config;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.runtime.ThrowRuntimeException;
import net.hasor.dataway.dal.ApiStatusEnum;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.dataway.dal.QueryCondition;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.SerializationChainSpi;
import net.hasor.dataway.spi.SerializationChainSpi.SerializationInfo;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;
import net.hasor.web.Invoker;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

/**
 * 工具。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
public class DatawayUtils {
    public static String evalCodeValueForSQL(String strCodeValue, Map<String, Object> strRequestBody) {
        StringBuilder paramKeyBuilder = new StringBuilder("");
        StringBuilder callKeyBuilder = new StringBuilder("");
        for (String key : strRequestBody.keySet()) {
            paramKeyBuilder.append("`" + key + "`,");
            callKeyBuilder.append("${" + key + "},");
        }
        if (paramKeyBuilder.length() > 0) {
            paramKeyBuilder.deleteCharAt(paramKeyBuilder.length() - 1);
            callKeyBuilder.deleteCharAt(callKeyBuilder.length() - 1);
        }
        strCodeValue = "var tempCall = @@sql(" + paramKeyBuilder.toString() + ")<%" + strCodeValue + "%>;\n";
        strCodeValue = strCodeValue + "return tempCall(" + callKeyBuilder.toString() + ");";
        return strCodeValue;
    }

    private static final ThreadLocal<Long> localRequestTime = ThreadLocal.withInitial(System::currentTimeMillis);

    public static long resetLocalTime() {
        long currentTimeMillis = System.currentTimeMillis();
        localRequestTime.remove();
        localRequestTime.set(currentTimeMillis);
        return currentTimeMillis;
    }

    public static long currentLostTime() {
        return System.currentTimeMillis() - localRequestTime.get();
    }

    /** 在选项中判断，是否保留外层结构 */
    public static boolean isResultStructure(Map<String, Object> optionMap) {
        if (optionMap == null) {
            return true;
        }
        return (boolean) optionMap.getOrDefault("resultStructure", true);
    }

    /** 所有参数全部打包到新的变量中 */
    public static String wrapParameterName(Map<String, Object> optionMap) {
        if (optionMap == null) {
            return null;
        }
        boolean wrapAllParameters = (boolean) optionMap.getOrDefault("wrapAllParameters", false);
        if (wrapAllParameters) {
            String wrapParameterName = (String) optionMap.getOrDefault("wrapParameterName", "root");
            return StringUtils.isBlank(wrapParameterName) ? null : wrapParameterName.trim();
        }
        return null;
    }

    public static Result<Object> queryResultToResultWithSpecialValue(Map<String, Object> optionMap, QueryResult queryResult, Object specialValue) {
        Object resultValue;
        if (specialValue instanceof DataModel) {
            resultValue = ((DataModel) specialValue).unwrap();
        } else {
            resultValue = specialValue;
        }
        //
        if (!isResultStructure(optionMap)) {
            return Result.of(resultValue);
        } else {
            Map<String, Object> resultData = new LinkedHashMap<String, Object>() {{
                put("success", true);
                put("message", "OK");
                put("code", queryResult.getCode());
                put("lifeCycleTime", currentLostTime());
                put("executionTime", queryResult.executionTime());
                put("value", resultValue);
            }};
            return Result.of(doResponseFormat(optionMap, resultData));
        }
    }

    public static Result<Object> exceptionToResult(Throwable e) {
        if (e instanceof ThrowRuntimeException) {
            return exceptionToResultWithSpecialValue(null, e, ((ThrowRuntimeException) e).getResult().unwrap());
        } else {
            return exceptionToResultWithSpecialValue(null, e, e.getMessage());
        }
    }

    public static Result<Object> exceptionToResultWithSpecialValue(Map<String, Object> optionMap, Throwable e, Object specialValue) {
        if (!isResultStructure(optionMap) && specialValue != null) {
            return Result.of(specialValue);
        }
        //
        Map<String, Object> resultData = null;
        if (e instanceof ThrowRuntimeException) {
            resultData = new LinkedHashMap<>();
            resultData.put("success", false);
            resultData.put("message", e.getMessage());
            resultData.put("code", ((ThrowRuntimeException) e).getThrowCode());
            resultData.put("lifeCycleTime", currentLostTime());
            resultData.put("executionTime", ((ThrowRuntimeException) e).getExecutionTime());
            resultData.put("value", specialValue);
        } else {
            resultData = new LinkedHashMap<>();
            resultData.put("success", false);
            resultData.put("message", e.getMessage());
            resultData.put("code", 500);
            resultData.put("lifeCycleTime", currentLostTime());
            resultData.put("executionTime", -1);
            resultData.put("value", specialValue);
        }
        return Result.of(doResponseFormat(optionMap, resultData));
    }

    private static Object doResponseFormat(Map<String, Object> optionMap, Map<String, Object> resultData) {
        if (optionMap == null || !optionMap.containsKey("responseFormat")) {
            return resultData;
        }
        Object responseFormat = optionMap.get("responseFormat");
        Map<String, Object> finalResult = new LinkedHashMap<>();
        LinkedHashMap<?, ?> jsonObject = JSONObject.parseObject(responseFormat.toString(), LinkedHashMap.class);
        for (Object key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value == null) {
                finalResult.put(key.toString(), null);
                continue;
            }
            // "success"      : "@resultStatus",
            // "message"      : "@resultMessage",
            // "code"         : "@resultCode",
            // "lifeCycleTime": "@timeLifeCycle",
            // "executionTime": "@timeExecution",
            // "value"        : "@resultData"
            switch (value.toString()) {
            case "@resultStatus":
                finalResult.put(key.toString(), resultData.get("success"));
                break;
            case "@resultMessage":
                finalResult.put(key.toString(), resultData.get("message"));
                break;
            case "@resultCode":
                finalResult.put(key.toString(), resultData.get("code"));
                break;
            case "@timeLifeCycle":
                finalResult.put(key.toString(), resultData.get("lifeCycleTime"));
                break;
            case "@timeExecution":
                finalResult.put(key.toString(), resultData.get("executionTime"));
                break;
            case "@resultData":
                finalResult.put(key.toString(), resultData.get("value"));
                break;
            default:
                finalResult.put(key.toString(), value);
                break;
            }
        }
        return finalResult;
    }

    public static byte[] toBytes(ApiInfo apiInfo, String body, String characterEncoding) throws UnsupportedEncodingException {
        if (apiInfo.isPerform()) {
            return body.getBytes(characterEncoding);
        } else {
            return body.getBytes(StandardCharsets.ISO_8859_1);
        }
    }

    public static Object responseData(SpiTrigger spiTrigger, ApiInfo apiInfo, String contentType, Invoker invoker, Object objectMap) throws IOException {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        if (!httpResponse.isCommitted()) {
            Object resultData = spiTrigger.chainSpi(SerializationChainSpi.class, (listener, lastResult) -> {
                return listener.doSerialization(apiInfo, invoker, lastResult);
            }, objectMap);
            //
            String contentDisposition = null;// 仅在 Bytes 下有效
            long contentLength = -1;         // 仅在 Bytes 下有效
            if (resultData instanceof SerializationInfo) {
                contentType = ((SerializationInfo) resultData).getContentType();
                contentDisposition = ((SerializationInfo) resultData).getContentDisposition();
                contentLength = ((SerializationInfo) resultData).getContentLength();
                resultData = ((SerializationInfo) resultData).getData();
            }
            //
            if (resultData instanceof String) {
                //
                setUIContextType(httpRequest, httpResponse, "text");
                String characterEncoding = httpResponse.getCharacterEncoding();
                responseString(httpResponse, contentType, characterEncoding, (String) resultData);
            } else if (resultData instanceof byte[]) {
                //
                setUIContextType(httpRequest, httpResponse, "bytes");
                byte[] bodyByte = (byte[]) resultData;
                responseBytes(httpResponse, contentType, contentDisposition, bodyByte.length, new ByteArrayInputStream(bodyByte));
            } else if (resultData instanceof InputStream) {
                //
                setUIContextType(httpRequest, httpResponse, "bytes");
                responseBytes(httpResponse, contentType, contentDisposition, -1, (InputStream) resultData);
            } else {
                //
                setUIContextType(httpRequest, httpResponse, "json");
                String characterEncoding = httpResponse.getCharacterEncoding();
                String body = JSON.toJSONString(resultData, SerializerFeature.WriteMapNullValue);
                responseString(httpResponse, contentType, characterEncoding, body);
            }
        }
        return objectMap;
    }

    private static void setUIContextType(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String responseContextType) {
        if ("true".equalsIgnoreCase(httpRequest.getHeader("X-InterfaceUI-Info"))) {
            httpResponse.setHeader("X-InterfaceUI-ContextType", responseContextType);
        }
    }

    private static void responseBytes(HttpServletResponse httpResponse, String contentType, String contentDisposition, long contentLength, InputStream bodyInputStream) throws IOException {
        httpResponse.setContentType(contentType);
        if (StringUtils.isNotBlank(contentDisposition)) {
            httpResponse.setHeader("Content-Disposition", contentDisposition);
        }
        if (contentLength > 0) {
            if (contentLength > Integer.MAX_VALUE) {
                httpResponse.setContentLengthLong(contentLength);
            } else {
                httpResponse.setContentLength((int) contentLength);
            }
        }
        try (ServletOutputStream output = httpResponse.getOutputStream()) {
            IOUtils.copy(bodyInputStream, output);
            output.flush();
        }
    }

    private static void responseString(HttpServletResponse httpResponse, String contentType, String characterEncoding, String contentBody) throws IOException {
        if (StringUtils.isNotBlank(characterEncoding)) {
            contentType = contentType + ";charset=" + characterEncoding; // 如果有 charset 那么加上 charset，否则会造成编码丢失问题。
        }
        httpResponse.setContentType(contentType);
        try (PrintWriter writer = httpResponse.getWriter()) {
            writer.write(contentBody);
            writer.flush();
        }
    }

    public static String generateID() {
        long timeMillis = System.currentTimeMillis();
        int nextInt = new Random(timeMillis).nextInt();
        String s = Integer.toString(nextInt, 24);
        if (s.length() > 4) {
            s = s.substring(0, 4);
        } else {
            s = StringUtils.rightPad(s, 4, "0");
        }
        //
        return Long.toString(timeMillis, 24) + s;
    }

    public static final Supplier<Map<FieldDef, String>> STATUS_UPDATE_TO_EDITOR    = () -> {
        return new HashMap<FieldDef, String>() {{
            put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Editor.typeNum()));
            put(FieldDef.GMT_TIME, String.valueOf(System.currentTimeMillis()));
        }};
    };
    public static final Supplier<Map<FieldDef, String>> STATUS_UPDATE_TO_PUBLISHED = () -> {
        return new HashMap<FieldDef, String>() {{
            put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Published.typeNum()));
            put(FieldDef.GMT_TIME, String.valueOf(System.currentTimeMillis()));
        }};
    };
    public static final Supplier<Map<FieldDef, String>> STATUS_UPDATE_TO_CHANGES   = () -> {
        return new HashMap<FieldDef, String>() {{
            put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Changes.typeNum()));
            put(FieldDef.GMT_TIME, String.valueOf(System.currentTimeMillis()));
        }};
    };
    public static final Supplier<Map<FieldDef, String>> STATUS_UPDATE_TO_DISABLE   = () -> {
        return new HashMap<FieldDef, String>() {{
            put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Disable.typeNum()));
            put(FieldDef.GMT_TIME, String.valueOf(System.currentTimeMillis()));
        }};
    };
    public static final Supplier<Map<FieldDef, String>> STATUS_UPDATE_TO_DELETE    = () -> {
        return new HashMap<FieldDef, String>() {{
            put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Delete.typeNum()));
            put(FieldDef.GMT_TIME, String.valueOf(System.currentTimeMillis()));
        }};
    };

    public static Map<QueryCondition, Object> conditionByApiId(String apiId) {
        return new HashMap<QueryCondition, Object>() {{
            put(QueryCondition.ApiId, apiId);
        }};
    }
}