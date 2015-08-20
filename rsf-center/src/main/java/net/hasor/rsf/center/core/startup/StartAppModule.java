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
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.StartModule;
import net.hasor.mvc.support.ControllerModule;
import net.hasor.mvc.support.LoadHellper;
import net.hasor.rsf.center.core.controller.RsfControllerModule;
import net.hasor.rsf.center.core.dao.DaoModule;
import net.hasor.rsf.center.core.freemarker.FreemarkerModule;
import net.hasor.rsf.center.core.jump.JumpModule;
import net.hasor.rsf.center.core.valid.ValidModule;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperModule;
import net.hasor.rsf.center.domain.constant.WorkMode;
import net.hasor.web.WebApiBinder;
/**
 * WebMVC各组件初始化配置。
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class StartAppModule extends ControllerModule implements StartModule {
    public static final String CenterStartEvent = "CenterStartEvent";
    @Override
    protected void loadController(LoadHellper helper) throws Throwable {
        //WorkAt
        WebApiBinder apiBinder = helper.apiBinder();
        WorkMode workAt = apiBinder.getEnvironment().getSettings().getEnum("rsfCenter.workAt", WorkMode.class, WorkMode.None);
        logger.info("rsf work mode at : ({}){}", workAt.getCodeType(), workAt.getCodeString());
        //
        //Jump
        apiBinder.installModule(new JumpModule(workAt));
        //Valid
        apiBinder.installModule(new ValidModule(workAt));
        //Controller
        apiBinder.installModule(new RsfControllerModule(workAt));
        //Freemarker
        apiBinder.installModule(new FreemarkerModule(workAt));
        //DataSource
        apiBinder.installModule(new DaoModule(workAt));
        //Zookeeper
        apiBinder.installModule(new ZooKeeperModule(workAt));
    }
    //
    //
    public void onStart(AppContext appContext) throws Throwable {
        Environment env = appContext.getEnvironment();
        env.getEventContext().fireSyncEvent(CenterStartEvent, appContext);//fire Event
    }
}