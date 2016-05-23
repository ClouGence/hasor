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
package net.hasor.rsf.center.server.startup;
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
import net.hasor.rsf.center.server.core.commands.CenterCommandPlugin;
import net.hasor.rsf.center.server.data.daos.DaoModule;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
import net.hasor.rsf.center.server.domain.WorkMode;
import net.hasor.rsf.center.server.push.PushQueue;
import net.hasor.rsf.center.server.remote.RsfCenterRegisterProvider;
import net.hasor.rsf.center.server.remote.RsfCenterServerVerifyFilter;
/**
 * 注册中心启动入口。
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2015年5月5日
 */
public class RsfCenterServerModule implements LifeModule {
    protected Logger     logger = LoggerFactory.getLogger(getClass());
    private RsfCenterCfg rsfCenterCfg;
    public RsfCenterServerModule() {}
    public RsfCenterServerModule(RsfCenterCfg rsfCenterCfg) {
        this.rsfCenterCfg = rsfCenterCfg;
    }
    //
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
        // 1.解析注册中心配置信息。
        if (this.rsfCenterCfg == null) {
            this.rsfCenterCfg = RsfCenterCfg.buildFormConfig(apiBinder.getEnvironment());
        }
        apiBinder.bindType(RsfCenterCfg.class).toInstance(this.rsfCenterCfg);
        //
        // 2.工作模式确定
        WorkMode workMode = this.rsfCenterCfg.getWorkMode();
        logger.info("rsf work mode at : ({}){}", workMode.getCodeType(), workMode.getCodeString());
        //
        // 3.连接数据库
        apiBinder.installModule(new DaoModule(this.rsfCenterCfg));
        //
        // 4.启动RSF框架，发布注册中心接口
        apiBinder.installModule(new RsfModule() {
            public void loadRsf(RsfContext rsfContext) throws Throwable {
                rsfContext.offline();//切换下线，暂不接收任何Rsf请求
                //
                RsfBinder rsfBinder = rsfContext.binder();
                rsfBinder.rsfService(RsfCenterRegister.class).to(RsfCenterRegisterProvider.class)//
                        .bindFilter("VerificationFilter", new RsfCenterServerVerifyFilter(rsfContext))//
                        .register();
                //
                rsfBinder.rsfService(RsfCenterListener.class)// 
                        .bindFilter("VerificationFilter", new RsfCenterServerVerifyFilter(rsfContext))//
                        .register();
            }
        });
        //
        // 5.注册PushQueue类型，当容器启动之后会启动，注册中心推送线程。
        apiBinder.bindType(PushQueue.class);
        //
        // 6.命令集
        apiBinder.installModule(new CenterCommandPlugin());
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