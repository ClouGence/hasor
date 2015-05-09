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
package test.net.hasor.rsf.client.sync;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import test.net.hasor.rsf.service.EchoService;
/**
 * 
 * @version : 2015年5月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class SyncClient {
    private int        chientID;
    private RsfContext rsfContext;
    public SyncClient(int chientID, RsfContext rsfContext) {
        this.chientID = chientID;
        this.rsfContext = rsfContext;
    }
    //
    public void syncRun() throws InterruptedException {
        //启动调用线程
        int threadCount = rsfContext.getSettings().getInteger("testConfig.threadCount");
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            new Thread() {
                public void run() {
                    String tName = chientID + "-T-" + index;
                    doCall(tName, rsfContext);
                };
            }.start();
        }
        System.out.println(rsfContext);
        while (true) {
            Thread.sleep(1000);
        }
    }
    private void doCall(String threadName, RsfContext rsfContext) {
        RsfClient rsfClient = rsfContext.getRsfClient();
        RsfBindInfo<EchoService> bindInfo = rsfContext.getBindCenter().getService(EchoService.class);
        EchoService remoteService = rsfClient.getRemote(bindInfo);
        //
        int i = 0;
        String sayMessage = "sayData:" + threadName;
        while (true) {
            Object result = remoteService.echo(sayMessage, i++);
            if (i % 50000 == 0) {
                System.out.println(i + "\t" + result);
            }
        }
    }
}
