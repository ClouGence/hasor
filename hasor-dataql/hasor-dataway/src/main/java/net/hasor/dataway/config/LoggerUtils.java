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
import com.alibaba.fastjson.JSONObject;
import net.hasor.utils.builder.ToStringBuilder;
import net.hasor.utils.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * 日志工具
 * @version : 2016年1月10日
 * @author 赵永春 (zyc@hasor.net)
 */
public class LoggerUtils {
    private static final Logger              logger = LoggerFactory.getLogger(LoggerUtils.class);
    private final        Map<String, Object> map    = new TreeMap<>();

    private LoggerUtils() {
    }

    public static LoggerUtils create() {
        return new LoggerUtils();
    }

    private void put(String key, Object value) {
        if (key != null && map.get(key) != null) {
            logger.error("duplicat key = " + key);
        }
        map.put(key, value);
        if (map.keySet().size() > 20) {
            logger.error("NEED_SMS_NOTIFY map.key.size = " + map.keySet().size());
        }
    }

    public LoggerUtils addString(String message) {
        if (message == null) {
            message = "valueIsNull";
        }
        put("message", message);
        return this;
    }

    public LoggerUtils addLog(String key, Object value) {
        if (value == null) {
            value = "valueIsNull";
        }
        put(key, value);
        return this;
    }

    public LoggerUtils logException(Throwable ex) {
        if (ex != null) {
            put("exceptionMsg", ex.getMessage());
        }
        return this;
    }

    /** 注意, 仅是为了方便日志输出及查看, 本方法未进行html转义, 所以切不可输出到客户页面. */
    public String toJson() {
        return JSONObject.toJSONString(map);
    }

    /** 对于Json转换出的异常, 需要使用此方法. 以避免死循环 */
    public String toStringBuilder() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> kv : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            String s = null;
            if (kv.getValue() == null) {
                s = "null";
            } else {
                if (kv.getValue() instanceof String || kv.getValue() instanceof Long || kv.getValue() instanceof Integer || kv.getValue() instanceof Double) {
                    s = String.valueOf(kv.getValue());
                } else {
                    s = ToStringBuilder.reflectionToString(kv.getValue(), ToStringStyle.SHORT_PREFIX_STYLE);
                }
            }
            sb.append("[" + kv.getKey() + "]=[" + s + "]");
        }
        return sb.toString();
    }
}