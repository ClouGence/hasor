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
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfPlugin;
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
public final class RsfFrameworkModule implements Module, RsfPlugin {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public final void loadModule(ApiBinder apiBinder) throws Throwable {
        Environment env = apiBinder.getEnvironment();
        boolean enable = env.getSettings().getBoolean("hasor.rsfConfig.enable", false);
        if (enable == false) {
            logger.info("rsf framework disable -> 'hasor.rsfConfig.enable' is false");
            return;
        }
        //
        final RsfEnvironment environment = new DefaultRsfEnvironment(env);
        final RsfBeanContainer rsfContainer = Hasor.autoAware(environment, new RsfBeanContainer(environment));
        final AbstractRsfContext rsfContext = Hasor.autoAware(environment, new AbstractRsfContext(rsfContainer) {});
        Hasor.addShutdownListener(environment, new EventListener<AppContext>() {
            @Override
            public void onEvent(String event, AppContext eventData) throws Throwable {
                logger.info("rsf framework shutdown.");
                rsfContext.shutdown();
            }
        });
        Hasor.addStartListener(environment, new EventListener<AppContext>() {
            @Override
            public void onEvent(String event, AppContext eventData) throws Throwable {
                logger.info("rsf framework starting.");
                List<RsfPlugin> pluginList = eventData.findBindingBean(RsfPlugin.class);
                if (pluginList != null) {
                    pluginList = new ArrayList<RsfPlugin>(pluginList);
                }
                pluginList.add(0, RsfFrameworkModule.this);
                rsfContext.start(pluginList.toArray(new RsfPlugin[pluginList.size()]));
            }
        });
        //
        apiBinder.bindType(RsfSettings.class).toInstance(environment.getSettings());
        apiBinder.bindType(RsfEnvironment.class).toInstance(environment);
        apiBinder.bindType(RsfContext.class).toInstance(rsfContext);
        apiBinder.bindType(RsfUpdater.class).toInstance(rsfContainer.getAddressPool());
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
    }
    @Override
    public void loadRsf(RsfContext rsfContext) throws Throwable {
        RsfBinder rsfBinder = rsfContext.binder();
        rsfBinder.bindFilter("LocalPref", new LocalPref());
        rsfBinder.bindFilter("LocalWarpFilter", new LocalWarpFilter());
    }
}