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
package net.hasor.registry.boot;
import net.hasor.registry.CenterMode;
import net.hasor.registry.RsfCenterSettings;
import net.hasor.registry.client.RegistryClientModule;
import net.hasor.registry.server.manager.RegistryServerModule;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 注册中心启动入口。
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2015年5月5日
 */
public class RegistryBootModule extends RsfModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        RsfEnvironment rsfEnvironment = apiBinder.getEnvironment();
        RsfCenterSettings settings = new RsfCenterSettingsImpl(rsfEnvironment.getSettings());
        apiBinder.bindType(RsfCenterSettings.class).toInstance(settings);
        //
        if (CenterMode.None.equals(settings.getMode())) {
            this.logger.warn("registry workAt None mode, so registry cannot be started.");
            return;
        }
        //
        if (CenterMode.Client.equals(settings.getMode())) {
            this.logger.warn("registry workAt Client mode, so registry will maintain your service info.");
            apiBinder.installModule(new RegistryClientModule(settings));
            return;
        }
        //
        if (CenterMode.Server.equals(settings.getMode())) {
            this.logger.warn("registry workAt Server mode, so registry will managing all service info.");
            apiBinder.installModule(new RegistryServerModule(rsfEnvironment, settings));
            return;
        }
        //
        if (CenterMode.Cluster.equals(settings.getMode())) {
            this.logger.warn("registry workAt Cluster mode, Temporary does not support.");
            throw new UnsupportedOperationException("Temporary does not support");
        }
    }
}