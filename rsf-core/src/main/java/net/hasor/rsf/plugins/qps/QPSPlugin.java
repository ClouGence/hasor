/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.plugins.qps;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.constants.ProtocolStatus;
/**
 * 统计QPS。
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public class QPSPlugin implements RsfFilter {
    private volatile long startTime    = System.currentTimeMillis();
    private volatile long requestCount = 0;
    private volatile long okCount      = 0;
    private volatile long errorCount   = 0;
    //
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        try {
            requestCount++;
            chain.doFilter(request, response);
            //
            if (response.getResponseStatus() == ProtocolStatus.OK)
                okCount++;
            else
                errorCount++;
        } catch (Throwable e) {
            errorCount++;
            throw e;
        }
    }
    //
    /**调用总量QPS（单位:秒）*/
    public double getQPS() {
        long duration = System.currentTimeMillis() - this.startTime;
        return (double) (this.requestCount * 1000) / (double) duration;
    }
    /**正确调用的QPS（单位:秒）*/
    public double getOkQPS() {
        long duration = System.currentTimeMillis() - this.startTime;
        return (double) (this.okCount * 1000) / (double) duration;
    }
    /**异常调用的QPS（单位:秒）*/
    public double getErrorQPS() {
        long duration = System.currentTimeMillis() - this.startTime;
        return (double) (this.errorCount * 1000) / (double) duration;
    }
    /**获取请求总数*/
    public long getRequestCount() {
        return this.requestCount;
    }
    /**正确调用总数*/
    public long getOkCount() {
        return this.okCount;
    }
    /**异常调用总数*/
    public long getErrorCount() {
        return this.errorCount;
    }
}