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
package test.net.hasor.rsf.client.async;
import java.util.concurrent.atomic.AtomicLong;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import org.more.future.FutureCallback;
import test.net.hasor.rsf.service.EchoService;
/**
 * 
 * @version : 2015年5月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class AsyncClient {
    private int        chientID;
    private RsfContext rsfContext;
    public AsyncClient(int chientID, RsfContext rsfContext) {
        this.chientID = chientID;
        this.rsfContext = rsfContext;
    }
    //
    public void syncRun() {
        int threadCount = rsfContext.getSettings().getInteger("testConfig.threadCount");
        final AtomicLong atomicLong = new AtomicLong(threadCount);
        final RsfClient rsfClient = rsfContext.getRsfClient();
        RsfBindInfo<EchoService> bindInfo = rsfContext.getBindCenter().getService(EchoService.class);
        Class<?>[] parameterTypes = new Class<?>[] { String.class, Integer.class };
        //
        FutureCallback<Object> listener = new FutureCallback<Object>() {
            @Override
            public void failed(Throwable ex) {
                atomicLong.incrementAndGet();
                System.out.print("err" + ex.getMessage());
            }
            @Override
            public void completed(Object result) {
                atomicLong.incrementAndGet();
            }
            @Override
            public void cancelled() {
                atomicLong.incrementAndGet();
            }
        };
        //
        //
        int i = 0;
        while (true) {
            String sayMessage = "sayData:" + chientID;
            Object[] parameterObjects = new Object[] { sayMessage, i };
            //
            if (atomicLong.get() > 0) {
                atomicLong.getAndDecrement();
                rsfClient.doCallBackInvoke(bindInfo, "echo", parameterTypes, parameterObjects, listener);
                if (i % 1000000 == 0) {
                    System.out.println(i + "\t call...");
                }
            }
        }
    }
}
