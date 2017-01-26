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
package test.net.hasor.rsf.alone;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import test.net.hasor.rsf.services.EchoService;
import test.net.hasor.rsf.services.EchoServiceImpl;
import test.net.hasor.rsf.services.MessageService;
import test.net.hasor.rsf.services.MessageServiceImpl;
/**
 * 启动服务端
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProviderServer {
    public static void main(String[] args) throws Throwable {
        //Server
        AppContext appContext = Hasor.createAppContext("/alone/provider-config.xml", new RsfModule() {
            @Override
            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
                apiBinder.rsfService(EchoService.class).toInstance(new EchoServiceImpl()).register();
                apiBinder.rsfService(MessageService.class).toInstance(new MessageServiceImpl()).register();
            }
        });
        //
        System.out.println("server start.");
        RsfContext rsf = appContext.getInstance(RsfContext.class);
        System.in.read();
    }
}