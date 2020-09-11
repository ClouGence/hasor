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
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.SerializationChainSpi;
import net.hasor.dataway.spi.SerializationChainSpi.SerializationInfo;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;
import net.hasor.web.Invoker;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public static byte[] toBytes(ApiInfo apiInfo, String body) {
        if (apiInfo.isPerform()) {
            return body.getBytes(StandardCharsets.UTF_8);
        } else {
            return body.getBytes();
        }
    }

    public static Object responseData(SpiTrigger spiTrigger, ApiInfo apiInfo, String mimeType, Invoker invoker, Object objectMap) throws IOException {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        if (!httpResponse.isCommitted()) {
            Object resultData = spiTrigger.chainSpi(SerializationChainSpi.class, (listener, lastResult) -> {
                return listener.doSerialization(apiInfo, invoker, lastResult);
            }, objectMap);
            //
            if (resultData instanceof SerializationInfo) {
                mimeType = ((SerializationInfo) resultData).getMimeType();
                resultData = ((SerializationInfo) resultData).getData();
            }
            //
            String responseContextType = null;
            int dataLength = -1;
            InputStream bodyInputStream = null;
            if (resultData instanceof String) {
                responseContextType = "text";
                byte[] bodyByte = toBytes(apiInfo, ((String) resultData));// 前端会通过 UTF-8 进行解码（仅限UI）
                dataLength = bodyByte.length;
                bodyInputStream = new ByteArrayInputStream(bodyByte);
            } else if (resultData instanceof byte[]) {
                responseContextType = "bytes";
                byte[] bodyByte = (byte[]) resultData;
                dataLength = bodyByte.length;
                bodyInputStream = new ByteArrayInputStream(bodyByte);
            } else if (resultData instanceof InputStream) {
                responseContextType = "bytes";
                dataLength = -1;
                bodyInputStream = (InputStream) resultData;
            } else {
                responseContextType = "json";
                String body = JSON.toJSONString(resultData, SerializerFeature.WriteMapNullValue);
                byte[] bodyByte = toBytes(apiInfo, body);// 前端会通过 UTF-8 进行解码（仅限UI）
                dataLength = bodyByte.length;
                bodyInputStream = new ByteArrayInputStream(bodyByte);
            }
            //
            if (dataLength > 0) {
                httpResponse.setContentLength(dataLength);
            }
            if ("true".equalsIgnoreCase(httpRequest.getHeader("X-InterfaceUI-Info"))) {
                httpResponse.setHeader("X-InterfaceUI-ContextType", responseContextType);
            }
            httpResponse.setContentType(mimeType);
            //
            try (ServletOutputStream output = httpResponse.getOutputStream()) {
                IOUtils.copy(bodyInputStream, output);
                output.flush();
            }
        }
        return objectMap;
    }
}