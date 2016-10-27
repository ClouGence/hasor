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
package net.hasor.rsf.center.server.bootstrap;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.center.server.domain.RsfCenterSettings;
import net.hasor.rsf.center.server.domain.WorkMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 注册中心启动入口。
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2015年5月5日
 */
public class RsfCenterFrameworkModule extends RsfModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        //
        RsfCenterSettings centerSettings = new RsfCenterSettings(apiBinder.getEnvironment());
        // .判断RSF目前是否配置了启用Center功能
        if (centerSettings.getWorkMode() == WorkMode.None) {
            this.logger.warn("this application has been started form the client mode, so rsfCenter cannot be started.");
            return;
        }
        //
        apiBinder.installModule(new RsfCenterServerModule(centerSettings));
    }
}