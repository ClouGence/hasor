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
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfBinder.ConfigurationBuilder;
import net.hasor.rsf.plugins.hasor.RsfApiBinder;
import net.hasor.rsf.plugins.hasor.RsfModule;
import net.hasor.search.client.DumpService;
import net.hasor.search.client.SearchService;
import net.hasor.search.server.rsf.ReadOptionFilter;
import net.hasor.search.server.rsf.SorlDumpService;
import net.hasor.search.server.rsf.SorlSearchService;
/**
 * 查询服务接口
 * @version : 2015年1月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class QueryInstall extends RsfModule {
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
        BindInfo<ReadOptionFilter> filterInfo = apiBinder.bindType(ReadOptionFilter.class).toInstance(new ReadOptionFilter()).toInfo();
        BindInfo<SearchService> searchInfo = apiBinder.bindType(SearchService.class).to(SorlSearchService.class).toInfo();
        BindInfo<DumpService> dumpInfo = apiBinder.bindType(DumpService.class).to(SorlDumpService.class).toInfo();
        //
        //BindInfo to Provider
        Provider<ReadOptionFilter> filterBean = toProvider(apiBinder, filterInfo);
        Provider<SearchService> searchBean = toProvider(apiBinder, searchInfo);
        Provider<DumpService> dumpBean = toProvider(apiBinder, dumpInfo);
        //
        //发布 RSf 服务
        RsfBinder rsfBinder = apiBinder.getRsfBinder();
        bindFilter(filterBean, rsfBinder.rsfService(SearchService.class).toProvider(searchBean));
        bindFilter(filterBean, rsfBinder.rsfService(DumpService.class).toProvider(dumpBean));
    }
    protected void bindFilter(Provider<ReadOptionFilter> filterBean, ConfigurationBuilder<?> rsfBuilder) {
        rsfBuilder.bindFilter("CoreNameFilter", filterBean).register();
    }
}