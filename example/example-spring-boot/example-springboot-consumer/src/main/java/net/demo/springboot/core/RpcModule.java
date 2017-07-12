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
package net.demo.springboot.core;
import net.example.domain.consumer.EchoService;
import net.example.domain.consumer.MessageService;
import net.example.domain.consumer.UserService;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
/**
 * 发布服务
 * @version : 2017年02月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Component
@Configuration
public class RpcModule extends RsfModule {
    @Autowired
    private EchoService    echoService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService    userService;
    //
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        //
        apiBinder.rsfService(EchoService.class)     //
                .toInstance(this.echoService)       // 服务实现类来自于 Spring Bean
                .register();                        // 发布 RPC 服务
        //
        apiBinder.rsfService(MessageService.class)  //
                .toInstance(this.messageService)    // 服务实现类来自于 Spring Bean
                .register();                        // 发布 RPC 服务
        //
        apiBinder.rsfService(UserService.class)     //
                .toInstance(this.userService)       // 服务实现类来自于 Spring Bean
                .register();                        // 发布 RPC 服务
    }
}