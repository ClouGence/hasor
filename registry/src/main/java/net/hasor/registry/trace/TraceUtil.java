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
package net.hasor.registry.trace;
import net.hasor.rsf.utils.StringUtils;
import org.slf4j.MDC;

import java.util.UUID;
/**
 * 分布式RPC,全链路调用跟踪系统,工具包。
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class TraceUtil {
    public static final  String              KEY     = "traceID";
    private final static ThreadLocal<String> TraceID = new ThreadLocal<String>();
    //
    public static String getTraceID() {
        if (StringUtils.isBlank(TraceID.get())) {
            updateTraceID(UUID.randomUUID().toString().replace("-", ""));
        }
        return TraceID.get();
    }
    protected static void updateTraceID(String oldTreaceID) {
        clearTraceID();
        TraceID.set(oldTreaceID);
        MDC.put(KEY, oldTreaceID);
    }
    protected static void clearTraceID() {
        if (TraceID.get() != null) {
            TraceID.remove();
        }
        MDC.remove(KEY);
    }
}