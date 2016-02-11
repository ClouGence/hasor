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
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.RsfUpdater;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.plugins.filters.local.LocalPref;
import net.hasor.rsf.plugins.filters.thread.LocalWarpFilter;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.web.WebApiBinder;
/**
 * Rsf 制定 Hasor Module。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfModule implements LifeModule {
    @Override
    public final void onStart(AppContext appContext) throws Throwable {
        RsfBeanContainer rsfContainer = appContext.getInstance(RsfBeanContainer.class);
        AbstractRsfContext rsfContext = appContext.getInstance(AbstractRsfContext.class);
        rsfContext.init(appContext, rsfContainer);
    }
    @Override
    public final void onStop(AppContext appContext) throws Throwable {
        AbstractRsfContext rsfContext = appContext.getInstance(AbstractRsfContext.class);
        rsfContext.shutdown();
    }
    @Override
    public final void loadModule(ApiBinder apiBinder) throws Throwable {
        final RsfEnvironment environment = new DefaultRsfEnvironment(apiBinder.getEnvironment());
        final RsfBeanContainer container = new RsfBeanContainer(environment);
        final AbstractRsfContext rsfContext = new AbstractRsfContext() {};
        apiBinder.bindType(RsfBeanContainer.class).toInstance(container);
        apiBinder.bindType(AbstractRsfContext.class).toInstance(rsfContext);
        //
        apiBinder.bindType(RsfSettings.class).toInstance(environment.getSettings());
        apiBinder.bindType(RsfEnvironment.class).toInstance(environment);
        apiBinder.bindType(RsfContext.class).toInstance(rsfContext);
        apiBinder.bindType(RsfUpdater.class).toInstance(container.getAddressPool());
        apiBinder.bindType(RsfClient.class).toProvider(new Provider<RsfClient>() {
            public RsfClient get() {
                return rsfContext.getRsfClient();
            }
        });
        //
        if (apiBinder instanceof WebApiBinder) {
            //WebApiBinder webApiBinder = (WebApiBinder) apiBinder;
            //webApiBinder.serve("*.rsf").with(RsfServlet.class);
        }
        //
        RsfBinder rsfBinder = container.createBinder();
        rsfBinder.bindFilter("LocalPref", new LocalPref());
        rsfBinder.bindFilter("LocalWarpFilter", new LocalWarpFilter());
        this.loadModule(apiBinder, container.createBinder());
    }
    public void loadModule(ApiBinder apiBinder, RsfBinder rsfBinder) throws Throwable {
        //
    }
}