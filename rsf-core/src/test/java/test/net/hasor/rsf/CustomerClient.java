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
package test.net.hasor.rsf;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.address.InterAddress;
import test.net.hasor.rsf.services.EchoService;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CustomerClient {
    public static void main(String[] args) throws Throwable {
        //Client
        AppContext clientContext = Hasor.createAppContext("customer-config.xml", new RsfModule() {
            @Override
            public void loadRsf(RsfContext rsfContext) throws Throwable {
                RsfBinder rsfBinder = rsfContext.binder();
                InterAddress local = new InterAddress("rsf://127.0.0.1:8100/default");
                rsfBinder.rsfService(EchoService.class).bindAddress(local).register();
            }
        });
        System.out.println("server start.");
        //
        //Client -> Server
        RsfClient client = clientContext.getInstance(RsfClient.class);
        EchoService echoService = client.wrapper(EchoService.class);
        for (int i = 0; i < 208; i++) {
            try {
                String res = echoService.sayHello("Hello Word");
                System.out.println(res);
            } catch (Exception e) {
            }
        }
    }
}