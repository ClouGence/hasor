/*
 * Copyright 2008-2009 the original author or authors.
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
package test.net.hasor.rsf._08_context;
import org.junit.Test;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.bootstrap.RsfFrameworkModule;
import test.net.hasor.rsf.services.EchoService;
import test.net.hasor.rsf.services.EchoServiceImpl;
/**
 * 
 * @version : 2015年12月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfContextTest {
    @Test
    public void test() throws Throwable {
        //
        //Server
        AppContext serverContext = Hasor.createAppContext("07_server-config.xml", new RsfFrameworkModule() {
            public void loadModule(ApiBinder apiBinder, RsfBinder rsfBinder) throws Throwable {
                rsfBinder.rsfService(EchoService.class).toInstance(new EchoServiceImpl()).register();
            }
        });
        //
        //Client
        AppContext clientContext = Hasor.createAppContext("07_client-config.xml", new RsfFrameworkModule() {
            public void loadModule(ApiBinder apiBinder, RsfBinder rsfBinder) throws Throwable {
                rsfBinder.rsfService(EchoService.class).bindAddress("rsf://127.0.0.1:8000/local").register();
            }
        });
        //
        //
        //
        //Client -> Server
        RsfClient client = clientContext.getInstance(RsfClient.class);
        EchoService echoService = client.wrapper(EchoService.class);
        for (int i = 0; i < 208; i++) {
            try {
                String res = echoService.sayHello("Hello Word");
                System.out.println(res);
            } catch (Exception e) {}
        }
        //
        System.out.println();
    }
}