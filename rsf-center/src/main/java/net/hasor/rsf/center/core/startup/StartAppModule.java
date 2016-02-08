/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.center.core.startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.rsf.bootstrap.RsfModule;
import net.hasor.rsf.center.core.dao.DaoModule;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperModule;
import net.hasor.rsf.center.domain.constant.RsfCenterCfg;
import net.hasor.rsf.center.domain.constant.WorkMode;
/**
 * WebMVC各组件初始化配置。
 * 
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class StartAppModule implements LifeModule {
    protected Logger           logger                      = LoggerFactory.getLogger(getClass());
    public static final String RSFCenterCluster_StartEvent = "RSFCenterCluster_StartEvent";
    public static final String RSFCenterCluster_StopEvent  = "RSFCenterCluster_StopEvent";
    private RsfCenterCfg       rsfCenterCfg;
    //
    public StartAppModule() {}
    public StartAppModule(RsfCenterCfg rsfCenterCfg) {
        this.rsfCenterCfg = rsfCenterCfg;
    }
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        if (this.rsfCenterCfg == null) {
            this.rsfCenterCfg = RsfCenterCfg.buildFormConfig(apiBinder.getEnvironment());
        }
        //
        apiBinder.bindType(RsfCenterCfg.class).toInstance(this.rsfCenterCfg);
        WorkMode workMode = this.rsfCenterCfg.getWorkMode();
        logger.info("rsf work mode at : ({}){}", workMode.getCodeType(), workMode.getCodeString());
        // DataSource
        apiBinder.installModule(new DaoModule(this.rsfCenterCfg));
        // Zookeeper环境
        apiBinder.installModule(new ZooKeeperModule(this.rsfCenterCfg));
        // RSF
        String ns = "http://project.hasor.net/hasor/schema/main";
        apiBinder.getEnvironment().getSettings().setSetting("hasor.rsfConfig.port", rsfCenterCfg.getRsfPort(), ns);
        apiBinder.installModule(new RsfModule());
    }
    //
    public void onStart(AppContext appContext) throws Throwable {
        appContext.getEnvironment().getEventContext().fireSyncEvent(RSFCenterCluster_StartEvent, appContext);
    }
    public void onStop(AppContext appContext) throws Throwable {
        appContext.getEnvironment().getEventContext().fireSyncEvent(RSFCenterCluster_StopEvent, appContext);
    }
}