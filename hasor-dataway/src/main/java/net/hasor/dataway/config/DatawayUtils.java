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

    /** 在选项中判断，是否保留外层结构 */
    public static boolean isResultStructure(Map<String, Object> optionMap) {
        if (optionMap == null) {
            return true;
        }
        return (boolean) optionMap.getOrDefault("resultStructure", true);
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
            return Result.of(new LinkedHashMap<String, Object>() {{
                put("success", true);
                put("message", "OK");
                put("code", queryResult.getCode());
                put("lifeCycleTime", currentLostTime());
                put("executionTime", queryResult.executionTime());
                put("value", resultValue);
            }});
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