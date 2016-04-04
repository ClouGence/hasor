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
package net.hasor.rsf.center.server.core.startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.center.RsfCenterListener;
import net.hasor.rsf.center.RsfCenterRegister;
import net.hasor.rsf.center.server.core.zookeeper.ZooKeeperModule;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
import net.hasor.rsf.center.server.domain.WorkMode;
import net.hasor.rsf.center.server.push.PushQueue;
import net.hasor.rsf.center.server.services.RsfCenterRegisterProvider;
import net.hasor.rsf.center.server.services.RsfCenterRegisterVerificationFilter;
/**
 * 注册中心启动入口。
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2015年5月5日
 */
public class RsfCenterServerModule implements LifeModule {
    protected Logger     logger = LoggerFactory.getLogger(getClass());
    private RsfCenterCfg rsfCenterCfg;
    //
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
        // 1.将“rsfCenter.rsfPort”配置映射到“hasor.rsfConfig.port” -> 映射配置的目的是让配置文件看起来更规整。
        //        Settings settings = apiBinder.getEnvironment().getSettings();
        //        int rsfPort = settings.getInteger("rsfCenter.rsfPort", 2180);
        //        String rsfAddress = settings.getString("rsfCenter.bindAddress", "local");
        //        settings.setSetting("hasor.rsfConfig.port", rsfPort, "http://project.hasor.net/hasor/schema/main");
        //        settings.setSetting("hasor.rsfConfig.address", rsfAddress, "http://project.hasor.net/hasor/schema/main");
        //        apiBinder.getEnvironment().getEventContext().addListener(RsfEvent.Rsf_Initialized, new EventListener<RsfContext>() {
        //            public void onEvent(String event, RsfContext eventData) throws Throwable {
        //                //Rsf_Initialized 是Rsf在启动之前的事件通知，这个时候Rsf框架刚刚准备初始化，在这里重载配置是为了让，前面映射的配置生效。
        //                eventData.getSettings().refreshRsfConfig();
        //            }
        //        });
        //
        // 2.解析注册中心配置信息。
        this.rsfCenterCfg = RsfCenterCfg.buildFormConfig(apiBinder.getEnvironment());
        //
        // 3.工作模式确定
        apiBinder.bindType(RsfCenterCfg.class).toInstance(this.rsfCenterCfg);
        WorkMode workMode = this.rsfCenterCfg.getWorkMode();
        logger.info("rsf work mode at : ({}){}", workMode.getCodeType(), workMode.getCodeString());
        //
        // 4.启动Zookeeper集群
        apiBinder.installModule(new ZooKeeperModule(this.rsfCenterCfg));
        //
        // 5.启动RSF框架，发布注册中心接口
        apiBinder.installModule(new RsfModule() {
            @Override
            public void loadRsf(RsfContext rsfContext) throws Throwable {
                rsfContext.offline();//切换下线，暂不接收任何Rsf请求
                //
                RsfBinder rsfBinder = rsfContext.binder();
                rsfBinder.rsfService(RsfCenterRegister.class).to(RsfCenterRegisterProvider.class)//
                        .bindFilter("VerificationFilter", new RsfCenterRegisterVerificationFilter(rsfContext))//
                        .register();
                //
                rsfBinder.rsfService(RsfCenterListener.class)// 
                        .bindFilter("VerificationFilter", new RsfCenterRegisterVerificationFilter(rsfContext))//
                        .register();
            }
        });
        //
        // 6.注册PushQueue类型，当容器启动之后会启动，注册中心推送线程。
        apiBinder.bindType(PushQueue.class);
    }
    //
    public void onStart(AppContext appContext) throws Throwable {
        appContext.getInstance(RsfContext.class).online();//切换上线，开始提供服务
        logger.info("rsfCenter online.");
        //
    }
    public void onStop(AppContext appContext) throws Throwable {
        //
    }
}