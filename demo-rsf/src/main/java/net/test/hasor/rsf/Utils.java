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
package net.test.hasor.rsf;
import net.hasor.core.AppContext;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.plugins.qps.QPSPlugin;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Utils {
    public static void startQPS(RsfContext rsfContext) throws Throwable {//2.监视
        final QPSPlugin qpsPlugin = rsfContext.findFilter("QPS");
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {}
                    if (qpsPlugin != null) {
                        System.out.println("QPS         :" + qpsPlugin.getQPS());
                        System.out.println("requestCount:" + qpsPlugin.getOkCount());
                        System.out.println();
                    }
                }
            }
        }).start();
    }
    public static void startQPS(AppContext appContext) throws Throwable {
        startQPS(appContext.getInstance(RsfContext.class));
    }
}