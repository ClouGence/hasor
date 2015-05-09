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
package test.net.hasor.rsf.provider._02_hasor;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.plugins.hasor.RsfApiBinder;
import net.hasor.rsf.plugins.hasor.RsfModule;
import net.hasor.rsf.plugins.monitor.QpsMonitor;
import test.net.hasor.rsf.service.EchoService;
import test.net.hasor.rsf.service.EchoServiceImpl;
/**
 * 通过Hasor插件形式发布 RSF 服务
 * @version : 2014年9月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class HasorProvider extends RsfModule {
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        RsfBinder rsfBinder = apiBinder.getRsfBinder();
        rsfBinder.bindFilter("QPS", new QpsMonitor());//QpS统计
        rsfBinder.rsfService(EchoService.class, new EchoServiceImpl()).register(); //发布服务
    }
    //
    public static void main(String[] args) throws Throwable {
        AppContext appContext = Hasor.createAppContext("rsf-config.xml", new HasorProvider());
        //
        System.out.println(appContext);
        while (true) {
            Thread.sleep(1000);
        }
    }
}