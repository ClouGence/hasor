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
package test.net.hasor.rsf._01_provider;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfBinder.RegisterBuilder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import test.net.hasor.rsf.services.EchoService;
import test.net.hasor.rsf.services.EchoServiceImpl;
/**
 * 启动服务端
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProviderServer {
    public static void main(String[] args) throws Throwable {
        String hostAddress = "127.0.0.1";//RSF服务绑定的本地IP地址。
        int hostPort = 8001;//使用的端口
        //
        RsfContext rsfContext = new RsfBootstrap().doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) throws Throwable {
                //声明服务
                RegisterBuilder<?> regBuilder = rsfBinder.rsfService(EchoService.class).toInstance(new EchoServiceImpl());
                //发布服务
                regBuilder.register();
            }
        }).socketBind(hostAddress, hostPort).sync();
        //
        System.out.println("server start.");
        while (true) {
            Thread.sleep(100);
        }
    }
}