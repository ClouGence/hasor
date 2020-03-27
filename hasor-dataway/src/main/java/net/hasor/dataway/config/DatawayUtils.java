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
import net.hasor.dataql.runtime.ThrowRuntimeException;

import java.util.HashMap;
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
        strCodeValue = "var tempCall = @@inner_dataway_sql(" + paramKeyBuilder.toString() + ")<%" + strCodeValue + "%>;\n";
        strCodeValue = strCodeValue + "return tempCall(" + callKeyBuilder.toString() + ");";
        return strCodeValue;
    }

    public static Result<Map<String, Object>> exceptionToError(Exception e) {
        if (e instanceof ThrowRuntimeException) {
            return Result.of(new HashMap<String, Object>() {{
                put("success", false);
                put("message", e.getMessage());
                put("code", ((ThrowRuntimeException) e).getThrowCode());
                put("executionTime", ((ThrowRuntimeException) e).getExecutionTime());
                put("value", ((ThrowRuntimeException) e).getResult().unwrap());
            }});
        } else {
            return Result.of(new HashMap<String, Object>() {{
                put("success", false);
                put("message", e.getMessage());
                put("code", 500);
                put("executionTime", -1);
                put("value", e.getMessage());
            }});
        }
    }

    public static Result<Map<String, Object>> exceptionToResult(Exception e) {
        if (e instanceof ThrowRuntimeException) {
            return Result.of(new HashMap<String, Object>() {{
                put("success", false);
                put("code", ((ThrowRuntimeException) e).getThrowCode());
                put("executionTime", ((ThrowRuntimeException) e).getExecutionTime());
                put("value", ((ThrowRuntimeException) e).getResult().unwrap());
            }});
        } else {
            return Result.of(new HashMap<String, Object>() {{
                put("success", false);
                put("code", 500);
                put("executionTime", -1);
                put("value", e.getMessage());
            }});
        }
    }
}
