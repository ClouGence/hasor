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
import net.hasor.core.LifeModule;
import net.hasor.rsf.center.core.dao.DaoModule;
import net.hasor.rsf.center.core.filters.JumpFilter;
import net.hasor.rsf.center.core.filters.VarFilter;
import net.hasor.rsf.center.domain.constant.WorkMode;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/**
 * WebMVC各组件初始化配置。
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class StartAppModule extends WebModule implements LifeModule {
    public static final String CenterStartEvent = "CenterStartEvent";
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //WorkAt
        WorkMode workAt = apiBinder.getEnvironment().getSettings().getEnum("rsfCenter.workAt", WorkMode.class, WorkMode.None);
        logger.info("rsf work mode at : ({}){}", workAt.getCodeType(), workAt.getCodeString());
        //
        //Filters
        apiBinder.filter("/*").through(new JumpFilter());
        apiBinder.filter("/*").through(new VarFilter());
        //DataSource
        apiBinder.installModule(new DaoModule(workAt));
        //Zookeeper
        //        apiBinder.installModule(new ZooKeeperModule(workAt));
    }
    //
    //
    public void onStart(AppContext appContext) throws Throwable {
        Environment env = appContext.getEnvironment();
        env.getEventContext().fireSyncEvent(CenterStartEvent, appContext);//fire Event
    }
    public void onStop(AppContext appContext) throws Throwable {
        // TODO Auto-generated method stub
    }
}