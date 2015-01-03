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
package net.hasor.rsf._test;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import net.hasor.rsf.bootstrap.WorkMode;
import net.hasor.rsf.plugins.qps.QPSPlugin;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Server8003 {
    public static void main(String[] args) throws Throwable {
        RsfContext rsfContext = new RsfBootstrap().doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) {
                //rsfBinder.bindFilter("QPS", new QPSPlugin());
                //rsfBinder.bindFilter("LocalPre", new LocalPrefPlugin());
                //
                rsfBinder.rsfService(ITestServices.class, new TestServices()).register();
            }
        }).workAt(WorkMode.None).socketBind(8003).sync();
        //
        final QPSPlugin qps = rsfContext.findFilter(ITestServices.class, "QPS");
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {}
                    if (qps != null) {
                        System.out.println("QPS         :" + qps.getQPS());
                        System.out.println("requestCount:" + qps.getOkCount());
                        System.out.println();
                    }
                }
            }
        }).start();
    }
}