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
package net.example.nutz.provider;
import net.example.domain.consumer.EchoService;
import net.example.domain.consumer.MessageService;
import net.hasor.core.ApiBinder;
import net.hasor.rsf.RsfApiBinder;
import org.nutz.integration.hasor.NutzModule;
import org.nutz.integration.hasor.annotation.HasorConfiguration;
/**
 * Nutz Module ，发布 RPC 服务
 * @version : 2017年02月21日
 * @author 赵永春(zyc@hasor.net)
 */
@HasorConfiguration
public class RpcModule extends NutzModule {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // .切换成 RSF RsfApiBinder
        RsfApiBinder rsfApiBinder = apiBinder.tryCast(RsfApiBinder.class);
        //
        // .服务发布，发布Nutz中的Bean 到 RSF 中
        rsfApiBinder.rsfService(EchoService.class)                          // 声明服务接口
                .toProvider(nutzBean(rsfApiBinder, EchoService.class))      // 使用 nutz Bean 中的Bean 作为实现类
                .register();                                                // 发布服务
        //
        rsfApiBinder.rsfService(MessageService.class)                       // 声明服务接口
                .toProvider(nutzBean(rsfApiBinder, MessageService.class))   // 使用 nutz Bean 中的Bean 作为实现类
                .register();                                                // 发布服务
        //
    }
}