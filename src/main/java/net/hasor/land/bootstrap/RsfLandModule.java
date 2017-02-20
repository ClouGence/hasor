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
package net.hasor.land.bootstrap;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.context.ContextStartListener;
import net.hasor.land.domain.LandEvent;
import net.hasor.land.domain.ServerStatus;
import net.hasor.land.election.ElectionService;
import net.hasor.land.election.ElectionServiceManager;
import net.hasor.land.node.AskNameService;
import net.hasor.land.node.ServerNode;
import net.hasor.land.utils.LandTimerManager;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfModule;
/**
 * 启动入口
 *
 * @version : 2016年10月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfLandModule extends RsfModule implements ContextStartListener {
    @Override
    public void loadModule(final RsfApiBinder apiBinder) throws Throwable {
        //
        // .注册Bean
        apiBinder.bindType(ServerNode.class).asEagerSingleton();
        apiBinder.bindType(ElectionService.class).to(ElectionServiceManager.class).asEagerSingleton();
        ClassLoader classLoader = apiBinder.getEnvironment().getClassLoader();
        apiBinder.bindType(LandTimerManager.class).toInstance(new LandTimerManager(500, classLoader));
        //
        // .注册选举服务(隐藏的消息服务)
        apiBinder.rsfService(apiBinder.getBindInfo(ElectionService.class)).asShadow().asMessage().register();
        apiBinder.rsfService(AskNameService.class).toInfo(apiBinder.getBindInfo(ServerNode.class)).asShadow().register();
        //
        // .消息监听器
        final Environment env = apiBinder.getEnvironment();
        Hasor.pushShutdownListener(env, new EventListener<Object>() {
            public void onEvent(String event, Object eventData) throws Throwable {
                //
            }
        });
        Hasor.pushStartListener(env, new EventListener<Object>() {
            public void onEvent(String event, Object eventData) throws Throwable {
                //
            }
        });
    }
    @Override
    public void doStart(AppContext appContext) {
        //
    }
    @Override
    public void doStartCompleted(AppContext appContext) {
        // .异步方式触发事件,让节点进入 Follower 状态
        appContext.getEnvironment().getEventContext().fireAsyncEvent(LandEvent.ServerStatus, ServerStatus.Follower);
    }
}