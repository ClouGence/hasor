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
package net.test.hasor.rsf._02_hasor;
import java.net.InetAddress;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.plugins.hasor.RsfApiBinder;
import net.hasor.rsf.plugins.hasor.RsfModule;
import net.test.hasor.rsf.EchoService;
/**
 * 负责注册远程服务
 * @version : 2014年9月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfConsumer extends RsfModule {
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        //1.声明RSF服务
        RsfBinder rsfBinder = apiBinder.getRsfBinder();
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        rsfBinder.bindAddress(hostAddress, 8001);//分布式的远程服务提供者：1
        rsfBinder.bindAddress(hostAddress, 8002);//分布式的远程服务提供者：2
        RsfBindInfo<EchoService> bindInfo = rsfBinder.rsfService(EchoService.class).register();
        //
        //2.将服务注册到Hasor容器中
        apiBinder.bindType(EchoService.class, toProvider(apiBinder, bindInfo));
    }
}