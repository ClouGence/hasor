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
package net.test.simple.rsf;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.plugins.qps.QPSPlugin;
import net.test.simple.rsf.provider.RsfProvider;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Server8000 {
    public static void main(String[] args) throws Throwable {
        //1.创建并启动环境
        AppContext app = Hasor.createAppContext(new RsfProvider(8000));
        //2.监视
        final QPSPlugin qpsPlugin = (QPSPlugin) app.findBindingBean("QPS", RsfFilter.class);
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
}