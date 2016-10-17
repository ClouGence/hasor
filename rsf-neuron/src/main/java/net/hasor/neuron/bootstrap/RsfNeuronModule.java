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
package net.hasor.neuron.bootstrap;
import net.hasor.core.*;
import net.hasor.neuron.domain.NeuronEvent;
import net.hasor.neuron.domain.ServerStatus;
import net.hasor.neuron.election.ElectionService;
import net.hasor.neuron.election.ElectionServiceImpl;
import net.hasor.neuron.root.NeuronServer;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfPlugin;
/**
 * 启动入口
 *
 * @version : 2016年10月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfNeuronModule implements Module, RsfPlugin {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
        // .注册Bean
        apiBinder.bindType(NeuronServer.class).asEagerSingleton();
        apiBinder.bindType(ElectionService.class).to(ElectionServiceImpl.class).asEagerSingleton();
        apiBinder.bindType(NeuronTimerManager.class).toInstance(new NeuronTimerManager(500));
        //
        Environment env = apiBinder.getEnvironment();
        Hasor.addShutdownListener(env, new EventListener<Object>() {
            public void onEvent(String event, Object eventData) throws Throwable {
                //
            }
        });
    }
    @Override
    public void loadRsf(RsfContext rsfContext) throws Throwable {
        //
        // .注册选举服务(隐藏的消息服务)
        Provider<ElectionService> esProvider = rsfContext.getAppContext().getProvider(ElectionService.class);
        rsfContext.binder().rsfService(ElectionService.class)//
                .toProvider(esProvider).asShadow().asMessage().register();
    }
}
