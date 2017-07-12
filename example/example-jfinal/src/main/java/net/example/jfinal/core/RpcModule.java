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
package net.example.jfinal.core;
import net.example.domain.consumer.EchoService;
import net.example.domain.consumer.MessageService;
import net.example.domain.consumer.UserService;
import net.example.jfinal.provider.EchoServiceImpl;
import net.example.jfinal.provider.MessageServiceImpl;
import net.example.jfinal.provider.UserServiceImpl;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfModule;
/**
 *
 * @version : 2017年02月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class RpcModule extends RsfModule {
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        //
        apiBinder.rsfService(EchoService.class).toInfo(         // 声明 RSF 的服务来自容器中哪一个 Bean
                apiBinder.bindType(EchoServiceImpl.class)       // 将 Bean 注册到 Hasor 容器
                        .toInfo()).register();                  // 发布 RPC 服务
        //
        apiBinder.rsfService(MessageService.class).toInfo(      // 声明 RSF 的服务来自容器中哪一个 Bean
                apiBinder.bindType(MessageServiceImpl.class)    // 将 Bean 注册到 Hasor 容器
                        .toInfo()).register();                  // 发布 RPC 服务
        //
        apiBinder.rsfService(UserService.class).toInfo(         // 声明 RSF 的服务来自容器中哪一个 Bean
                apiBinder.bindType(UserServiceImpl.class)       // 将 Bean 注册到 Hasor 容器
                        .toInfo()).register();                  // 发布 RPC 服务
    }
}