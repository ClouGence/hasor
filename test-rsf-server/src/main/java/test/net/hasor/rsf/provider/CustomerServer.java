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
package test.net.hasor.rsf.provider;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import net.hasor.rsf.bootstrap.WorkMode;
import net.hasor.rsf.plugins.monitor.QpsMonitor;
import test.net.hasor.rsf.service.EchoService;
import test.net.hasor.rsf.service.EchoServiceImpl;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CustomerServer {
    public static void main(String[] args) throws Throwable {
        RsfBootstrap boot = new RsfBootstrap();
        RsfContext rsfContext = boot.doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) throws Throwable {
                rsfBinder.bindFilter("Monitor", new QpsMonitor());//监控
                rsfBinder.rsfService(EchoService.class).toInstance(new EchoServiceImpl()).register();
            }
        }).workAt(WorkMode.None).sync();
        //
        //
        System.out.println(rsfContext);
        while (true) {
            Thread.sleep(1000);
        }
    }
}