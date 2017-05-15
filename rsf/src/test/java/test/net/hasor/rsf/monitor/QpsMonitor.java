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
package test.net.hasor.rsf.monitor;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class QpsMonitor implements RsfFilter {
    protected Logger     logger    = LoggerFactory.getLogger(getClass());
    private   AtomicLong sendCount = new AtomicLong(0);
    private   long       startTime = System.currentTimeMillis();
    private   long       lastTime  = System.currentTimeMillis();
    //
    //
    public void printInfo(long rtTime) {
        long checkTime = System.currentTimeMillis();
        if (checkTime - startTime == 0) {
            return;
        }
        //
        if (checkTime - lastTime < 1000) {
            return;//10秒打印一条
        }
        lastTime = System.currentTimeMillis();
        long qpsSecnd = (sendCount.get() / ((checkTime - startTime) / 1000));
        logger.info("count:{} , QPS:{} , RT:{}", sendCount, qpsSecnd, rtTime);
        //
        /*1000亿次调用之后重置统计数据*/
        if (sendCount.get() >= 100000000L) {
            sendCount.set(0);
            startTime = System.currentTimeMillis() / 1000;
            lastTime = System.currentTimeMillis();
        }
    }
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        sendCount.getAndIncrement();
        long startTime = System.currentTimeMillis();
        chain.doFilter(request, response);
        printInfo(System.currentTimeMillis() - startTime);
    }
}