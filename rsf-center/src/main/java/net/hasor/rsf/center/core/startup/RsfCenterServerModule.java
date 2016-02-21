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
package net.hasor.rsf.center.core.startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.LifeModule;
import net.hasor.core.Settings;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.center.RsfCenterListener;
import net.hasor.rsf.center.RsfCenterRegister;
import net.hasor.rsf.center.core.dao.DaoModule;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperModule;
import net.hasor.rsf.center.domain.constant.RsfCenterCfg;
import net.hasor.rsf.center.domain.constant.WorkMode;
import net.hasor.rsf.center.server.RsfCenterRegisterProvider;
import net.hasor.rsf.domain.Events;
/**
 * WebMVC各组件初始化配置。
 * 
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCenterServerModule implements LifeModule {
    protected Logger     logger = LoggerFactory.getLogger(getClass());
    private RsfCenterCfg rsfCenterCfg;
    //
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
        // 1.将“rsfCenter.rsfPort”配置映射到“hasor.rsfConfig.port”
        Settings settings = apiBinder.getEnvironment().getSettings();
        int rsfPort = settings.getInteger("rsfCenter.rsfPort", 2180);
        settings.setSetting("hasor.rsfConfig.port", rsfPort, "http://project.hasor.net/hasor/schema/main");
        apiBinder.getEnvironment().getEventContext().addListener(Events.Rsf_Initialized, new EventListener<RsfContext>() {
            @Override
            public void onEvent(String event, RsfContext eventData) throws Throwable {
                eventData.getSettings().refreshRsfConfig();
            }
        });
        this.rsfCenterCfg = RsfCenterCfg.buildFormConfig(apiBinder.getEnvironment());
        //
        // 2.工作模式
        apiBinder.bindType(RsfCenterCfg.class).toInstance(this.rsfCenterCfg);
        WorkMode workMode = this.rsfCenterCfg.getWorkMode();
        logger.info("rsf work mode at : ({}){}", workMode.getCodeType(), workMode.getCodeString());
        //
        // 3.数据源
        apiBinder.installModule(new DaoModule(this.rsfCenterCfg));
        //
        // 4.Zookeeper环境
        apiBinder.installModule(new ZooKeeperModule(this.rsfCenterCfg));
        //
        // 5.RSF框架，发布注册中心接口
        apiBinder.installModule(new RsfModule() {
            @Override
            public void loadRsf(RsfContext rsfContext) throws Throwable {
                RsfBinder rsfBinder = rsfContext.binder();
                rsfBinder.rsfService(RsfCenterRegister.class).to(RsfCenterRegisterProvider.class).register();
                rsfBinder.rsfService(RsfCenterListener.class).register();
            }
        });
        //
    }
    //
    public void onStart(AppContext appContext) throws Throwable {
        //
    }
    public void onStop(AppContext appContext) throws Throwable {
        //
    }
}