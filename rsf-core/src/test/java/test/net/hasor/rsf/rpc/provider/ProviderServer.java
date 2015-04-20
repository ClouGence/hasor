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
package test.net.hasor.rsf.rpc.provider;
import java.util.ArrayList;
import java.util.List;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import net.hasor.rsf.bootstrap.WorkMode;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProviderServer {
    public void start(String host, int port) throws Throwable {
        RsfBootstrap boot = new RsfBootstrap();
        RsfContext rsfContext = boot.doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) throws Throwable {
                //监控
                rsfBinder.bindFilter("Monitor", new Monitor());
                //发布一个服务
                List<String> list = new ArrayList<String>();
                list.add("AAA");
                list.add("BBB");
                list.add("CCC");
                list.add("DDD");
                rsfBinder.rsfService(List.class).toInstance(list).register();
            }
        }).socketBind(host, port).workAt(WorkMode.None).sync();
        //
        //
        System.in.read();
        System.out.println(rsfContext);
    }
    public static void main(String[] args) throws Throwable {
        ProviderServer server = new ProviderServer();
        server.start("127.0.0.1", 8000);
    }
}