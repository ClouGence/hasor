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
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.runtime.ThrowRuntimeException;

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

    public static Result<Map<String, Object>> queryResultToResultWithSpecialValue(QueryResult queryResult, Object specialValue) {
        return Result.of(new LinkedHashMap<String, Object>() {{
            put("success", true);
            put("message", "OK");
            put("code", queryResult.getCode());
            put("lifeCycleTime", currentLostTime());
            put("executionTime", queryResult.executionTime());
            //
            if (specialValue instanceof DataModel) {
                put("value", ((DataModel) specialValue).unwrap());
            } else {
                put("value", specialValue);
            }
        }});
    }

    public static Result<Map<String, Object>> exceptionToResult(Exception e) {
        if (e instanceof ThrowRuntimeException) {
            return exceptionToResultWithSpecialValue(e, ((ThrowRuntimeException) e).getResult().unwrap());
        } else {
            return exceptionToResultWithSpecialValue(e, e.getMessage());
        }
    }

    public static Result<Map<String, Object>> exceptionToResultWithSpecialValue(Exception e, Object specialValue) {
        if (e instanceof ThrowRuntimeException) {
            return Result.of(new LinkedHashMap<String, Object>() {{
                put("success", false);
                put("message", e.getMessage());
                put("code", ((ThrowRuntimeException) e).getThrowCode());
                put("lifeCycleTime", currentLostTime());
                put("executionTime", ((ThrowRuntimeException) e).getExecutionTime());
                put("value", specialValue);
            }});
        } else {
            return Result.of(new LinkedHashMap<String, Object>() {{
                put("success", false);
                put("message", e.getMessage());
                put("code", 500);
                put("lifeCycleTime", currentLostTime());
                put("executionTime", -1);
                put("value", specialValue);
            }});
        }
    }
}