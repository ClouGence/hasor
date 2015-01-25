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
package net.hasor.search.server.installs;
import net.hasor.core.BindInfo;
import net.hasor.core.Environment;
import net.hasor.core.EventContext;
import net.hasor.rsf.plugins.hasor.RsfApiBinder;
import net.hasor.rsf.plugins.hasor.RsfModule;
import net.hasor.search.client.DumpService;
import net.hasor.search.client.SearchService;
import net.hasor.search.server.rsf.monitor.CoreMonitor;
import net.hasor.search.server.rsf.service.SorlDumpService;
import net.hasor.search.server.rsf.service.SorlSearchService;
/**
 * 查询服务接口
 * @version : 2015年1月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfInstall extends RsfModule {
    @Override
    protected String bindAddress(Environment env) {
        return env.envVar("SEARCH-HOST");
    }
    @Override
    protected int bindPort(Environment env) {
        return Integer.parseInt(env.envVar("SEARCH-PORT"));
    }
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        //
        //注册到Hasor
        BindInfo<SearchService> searchInfo = apiBinder.bindType(SearchService.class).to(SorlSearchService.class).toInfo();
        BindInfo<DumpService> dumpInfo = apiBinder.bindType(DumpService.class).to(SorlDumpService.class).toInfo();
        //
        //当容器启动时启动监听器，负责根据CoreName变化维护每个服务的注册状态
        EventContext eventContext = apiBinder.getEnvironment().getEventContext();
        eventContext.pushListener(EventContext.ContextEvent_Started, apiBinder.autoAware(new CoreMonitor(searchInfo)));
        eventContext.pushListener(EventContext.ContextEvent_Started, apiBinder.autoAware(new CoreMonitor(dumpInfo)));
    }
}