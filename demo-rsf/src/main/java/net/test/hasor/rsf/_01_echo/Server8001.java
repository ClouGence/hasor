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
package net.test.hasor.rsf._01_echo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import net.hasor.rsf.plugins.local.LocalPrefPlugin;
import net.hasor.rsf.plugins.qps.QPSPlugin;
import net.test.hasor.rsf.EchoService;
import net.test.hasor.rsf.EchoServiceImpl;
import net.test.hasor.rsf.Utils;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Server8001 {
    public static void main(String[] args) throws Throwable {
        RsfBootstrap bootstrap = new RsfBootstrap();
        bootstrap.doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) throws Throwable {
                rsfBinder.bindFilter("QPS", new QPSPlugin());
                rsfBinder.bindFilter("LocalPre", new LocalPrefPlugin());
                //
                rsfBinder.rsfService(EchoService.class, new EchoServiceImpl()).register();
            }
        }).socketBind(8001);
        RsfContext rsfContext = bootstrap.sync();
        Utils.startQPS(rsfContext);/*启动QPS实时报告*/
    }
}