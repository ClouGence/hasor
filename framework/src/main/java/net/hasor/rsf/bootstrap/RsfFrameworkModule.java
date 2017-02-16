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
package net.hasor.rsf.bootstrap;
import net.hasor.core.Provider;
import net.hasor.core.context.ContextShutdownListener;
import net.hasor.core.context.ContextStartListener;
import net.hasor.rsf.*;
import net.hasor.rsf.filters.local.LocalPref;
import net.hasor.rsf.filters.online.OnlineRsfFilter;
import net.hasor.rsf.filters.thread.LocalWarpFilter;
import net.hasor.rsf.filters.thread.RsfRequestLocal;
import net.hasor.rsf.filters.thread.RsfResponseLocal;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Rsf 框架启动入口。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public final class RsfFrameworkModule extends RsfModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public final void loadModule(RsfApiBinder apiBinder) throws Throwable {
        //
        logger.info("rsf framework starting.");
        //
        //1.组装 RsfContext 对象
        final RsfEnvironment environment = apiBinder.getEnvironment();
        final AbstractRsfContext rsfContext = new AbstractRsfContext(environment) {
        };
        //
        //2.注册Hasor生命周期
        apiBinder.bindType(ContextStartListener.class).toInstance(rsfContext);
        apiBinder.bindType(ContextShutdownListener.class).toInstance(rsfContext);
        //
        //3.将重要的接口注册到 Hasor 提供依赖注入
        apiBinder.bindType(RsfSettings.class).toInstance(environment.getSettings());
        //apiBinder.bindType(RsfEnvironment.class).toInstance(environment);//避免重复注册（注册是在RsfModule类中）
        apiBinder.bindType(RsfContext.class).toInstance(rsfContext);
        apiBinder.bindType(OnlineStatus.class).toInstance(rsfContext);
        apiBinder.bindType(RsfUpdater.class).toInstance(rsfContext.getUpdater());
        apiBinder.bindType(RsfClient.class).toProvider(new Provider<RsfClient>() {
            public RsfClient get() {
                return rsfContext.getRsfClient();
            }
        });
        apiBinder.bindType(RsfRequest.class).toInstance(new RsfRequestLocal());
        apiBinder.bindType(RsfResponse.class).toInstance(new RsfResponseLocal());
        //
        //4.重要的内置插件
        RsfPublisher rsfPublisher = rsfContext.publisher();
        apiBinder.bindType(RsfPublisher.class).toInstance(rsfPublisher);
        rsfPublisher.bindFilter("LocalPref", new LocalPref());
        rsfPublisher.bindFilter("LocalWarpFilter", new LocalWarpFilter());
        rsfPublisher.bindFilter("SecurityRsfFilter", new OnlineRsfFilter());
        //
        logger.info("rsf framework init finish.");
    }
}