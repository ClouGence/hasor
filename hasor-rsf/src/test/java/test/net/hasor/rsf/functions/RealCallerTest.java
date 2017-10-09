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
package test.net.hasor.rsf.functions;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.*;
import net.hasor.utils.future.FutureCallback;
import org.junit.Test;
import test.net.hasor.rsf.services.EchoService;
import test.net.hasor.rsf.services.EchoServiceImpl;
import test.net.hasor.rsf.services.MessageService;
import test.net.hasor.rsf.services.MessageServiceImpl;

import java.lang.reflect.Method;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RealCallerTest {
    @Test
    public void realCallOnce() throws Throwable {
        //Server
        AppContext serverAppContext = Hasor.createAppContext("alone/provider-config.xml", new RsfModule() {
            @Override
            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
                apiBinder.rsfService(EchoService.class).toInstance(new EchoServiceImpl()).register();
                apiBinder.rsfService(MessageService.class).toInstance(new MessageServiceImpl()).register();
            }
        });
        System.out.println("server start.");
        Thread.sleep(2000);
        //
        //
        //Client
        AppContext clientContext = Hasor.createAppContext("alone/customer-config.xml", new RsfModule() {
            @Override
            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
                InterAddress local = new InterAddress("rsf://127.0.0.1:2180/default");
                apiBinder.rsfService(EchoService.class).bindAddress(local).register();
                apiBinder.rsfService(MessageService.class).bindAddress(local).register();
            }
        });
        System.out.println("client start.");
        Thread.sleep(2000);
        //
        RsfClient client = clientContext.getInstance(RsfClient.class);
        EchoService echoService = client.wrapper(EchoService.class);
        String res = echoService.sayHello("Hello Word for Invoker");
        System.out.println("invoker -> " + res);
        //
    }
    @Test
    public void realCallerTest() throws Throwable {
        //Server
        AppContext serverAppContext = Hasor.createAppContext("alone/provider-config.xml", new RsfModule() {
            @Override
            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
                apiBinder.rsfService(EchoService.class).toInstance(new EchoServiceImpl()).register();
                apiBinder.rsfService(MessageService.class).toInstance(new MessageServiceImpl()).register();
            }
        });
        System.out.println("server start.");
        Thread.sleep(2000);
        //
        //
        //Client
        AppContext clientContext = Hasor.createAppContext("alone/customer-config.xml", new RsfModule() {
            @Override
            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
                InterAddress local = new InterAddress("rsf://127.0.0.1:2180/default");
                apiBinder.rsfService(EchoService.class).bindAddress(local).register();
                apiBinder.rsfService(MessageService.class).bindAddress(local).register();
            }
        });
        System.out.println("client start.");
        RsfClient client = clientContext.getInstance(RsfClient.class);
        EchoService echoService = client.wrapper(EchoService.class);
        for (int i = 0; i < 20; i++) {
            try {
                String res = echoService.sayHello("Hello Word for Invoker");
                System.out.println("invoker -> " + res);
            } catch (Exception e) {
            }
        }
        MessageService messageService = client.wrapper(MessageService.class);
        for (int i = 0; i < 20; i++) {
            try {
                RsfResult res = messageService.sayHello("Hello Word for Message.");
                System.out.println("message -> " + res.isSuccess());
            } catch (Exception e) {
            }
        }
        Thread.sleep(5000);
        //
        //
        MessageService warper = client.wrapper(MessageService.class);
        System.out.print(warper.sayHello("SSSS"));
        //
        RsfContext rsfContext = clientContext.getInstance(RsfContext.class);
        RsfBindInfo<?> echoServiceInfo = rsfContext.getServiceInfo(EchoService.class);
        Method helloMethod = EchoService.class.getMethod("sayHello", String.class);
        RsfFuture rsfFuture = client.asyncInvoke(echoServiceInfo, helloMethod.getName(), helloMethod.getParameterTypes(), new Object[] { "my name is zyc!" });
        System.out.println(rsfFuture.get().getStatus());
        System.out.println(rsfFuture.get().getData());
        Thread.sleep(2000);
        //
        client.callBackInvoke(echoServiceInfo, helloMethod.getName(), helloMethod.getParameterTypes(), new Object[] { "my name is zyc!" }, new FutureCallback<Object>() {
            @Override
            public void completed(Object result) {
                System.out.println("callBackInvoke -> result :" + result);
            }
            @Override
            public void failed(Throwable ex) {
                System.out.println("callBackInvoke -> exception :" + ex.getMessage());
            }
            @Override
            public void cancelled() {
                System.out.println("callBackInvoke -> cancelled.");
            }
        });
        Thread.sleep(2000);
        //
        client.callBackRequest(echoServiceInfo, helloMethod.getName(), helloMethod.getParameterTypes(), new Object[] { "my name is zyc!" }, new FutureCallback<RsfResponse>() {
            @Override
            public void completed(RsfResponse result) {
                System.out.println("callBackRequest -> status :" + result.getStatus());
                System.out.println("callBackRequest -> result :" + result.getData());
            }
            @Override
            public void failed(Throwable ex) {
                System.out.println("callBackRequest -> exception :" + ex.getMessage());
            }
            @Override
            public void cancelled() {
                System.out.println("callBackRequest -> cancelled.");
            }
        });
        Thread.sleep(2000);
        //
        EchoService remote = (EchoService) client.getRemote(echoServiceInfo);
        System.out.print(remote.sayHello("RRRRR"));
        Thread.sleep(2000);
        //
        //
        clientContext.shutdown();
        serverAppContext.shutdown();
    }
}