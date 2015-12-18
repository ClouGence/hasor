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
package test.net.hasor.rsf._08_context;
import org.junit.Test;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
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
        AppContext serverAppContext = Hasor.createAppContext("07_server-config.xml");
        RsfSettings serverSetting = new DefaultRsfSettings(serverAppContext.getEnvironment().getSettings());
        RsfEnvironment serverEnvironment = new DefaultRsfEnvironment(null, serverSetting);
        RsfBeanContainer serverContainer = new RsfBeanContainer(serverEnvironment);
        serverContainer.createBinder().rsfService(EchoService.class).toInstance(new EchoServiceImpl()).register();
        AbstractRsfContext server = new AbstractRsfContext() {};
        server.init(serverAppContext, serverContainer);
        //
        //
        //
        //Client
        AppContext clientAppContext = Hasor.createAppContext("07_client-config.xml");
        RsfSettings clientSetting = new DefaultRsfSettings(clientAppContext.getEnvironment().getSettings());
        RsfEnvironment clientEnvironment = new DefaultRsfEnvironment(null, clientSetting);
        RsfBeanContainer clientContainer = new RsfBeanContainer(clientEnvironment);
        clientContainer.createBinder().rsfService(EchoService.class).bindAddress("rsf://127.0.0.1:8000/local").register();
        AbstractRsfContext client = new AbstractRsfContext() {};
        client.init(clientAppContext, clientContainer);
        //
        //
        //
        //Client -> Server
        EchoService echoService = client.getRsfClient().wrapper(EchoService.class);
        for (int i = 0; i < 208; i++) {
            //发起四次调用，然后让这四个球在RSF容器里弹来弹去。
            String res = echoService.sayHello("Hello Word");
            System.out.println(res);
        }
    }
}