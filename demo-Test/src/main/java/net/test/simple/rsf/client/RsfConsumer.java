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
package net.test.simple.rsf.client;
import java.net.InetAddress;
import net.hasor.rsf.plugins.hasor.RsfApiBinder;
import net.hasor.rsf.plugins.hasor.RsfModule;
/**
 * 负责注册远程服务
 * @version : 2014年9月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfConsumer extends RsfModule {
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        apiBinder.rsfService(EchoService.class)//
                .bindAddress(hostAddress, 8000)//
                .bindAddress(hostAddress, 8001)//
                .bindAddress(hostAddress, 8002)//
                .bindAddress(hostAddress, 8003)//
                .register();
    }
}