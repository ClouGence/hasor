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
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.utils.StringUtils;
/**
 * 优先检查本地是否有服务提供（优先本地服务提供者的调用）。
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public class TraceFilter implements RsfFilter {
    private static class InnerTraceUtil extends TraceUtil {
        public static void updateTraceID(String oldTreaceID) {
            TraceUtil.updateTraceID(oldTreaceID);
        }
    }
    //
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        String traceID = request.getOption(InnerTraceUtil.KEY);
        if (!StringUtils.isBlank(traceID)) {
            InnerTraceUtil.updateTraceID(traceID);
        } else {
            traceID = InnerTraceUtil.getTraceID();
        }
        request.addOption(InnerTraceUtil.KEY, traceID);
        response.addOption(InnerTraceUtil.KEY, traceID);
        chain.doFilter(request, response);
    }
}