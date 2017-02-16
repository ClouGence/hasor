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
import net.hasor.registry.client.RsfCenterModule;
import net.hasor.rsf.RsfApiBinder;
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
        RsfCenterSettings settings = new RsfCenterSettingsImpl(apiBinder.getEnvironment().getSettings());
        this.logger.warn("registry workAt {}.", settings.getMode());
        //
        if (CenterMode.None.equals(settings.getMode())) {
            this.logger.warn("registry workAt none mode, so registry cannot be started.");
            return;
        }
        //
        if (CenterMode.Client.equals(settings.getMode())) {
            apiBinder.installModule(new RsfCenterModule(settings));
        }
        //
        //
    }
}