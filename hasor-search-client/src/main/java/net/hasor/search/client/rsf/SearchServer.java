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
package net.hasor.search.client.rsf;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import net.hasor.rsf.BindCenter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.search.client.DumpService;
import net.hasor.search.client.SearchService;
import org.more.classcode.delegate.faces.MethodClassConfig;
/**
 * 
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class SearchServer {
    private WriteOptionFilter optionFilter = null;
    private RsfContext        rsfContext   = null;
    //
    SearchServer(URL rsfHost, SearchServerFactory searchServerFactory) throws MalformedURLException {
        RsfContext rsfContext = searchServerFactory.getRsfContext();
        this.rsfClient = rsfContext.getRsfClient();
        this.optionFilter = new WriteOptionFilter();
        //
        String dumpName = DumpService.class.getName();
        //
        this.dumpInfo = bindCenter.getService(group, dumpName, version);
        //
        if (this.dumpInfo == null) {
            this.dumpInfo = bindCenter.getRsfBinder().rsfService(DumpService.class)//
                    .ngv(group, dumpName, version)//
                    .bindFilter("CoreNameFilter", this.optionFilter)//
                    .bindAddress(rsfHost.getHost(), rsfHost.getPort()).register();
        }
    }
    //
    public SearchService getSearchService(String coreName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        return getService(this.queryInfo, coreName, SearchService.class);
    }
    public DumpService getDumpService(String coreName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        return getService(this.dumpInfo, coreName, DumpService.class);
    }
    //
    //
    //
    private static Object                              LOCK        = new Object();
    private static ConcurrentHashMap<String, Class<?>> CACHE_TYPES = new ConcurrentHashMap<String, Class<?>>();
    private <T> T getService(String coreName, Class<T> type)//
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        //
        String group = this.rsfContext.getSettings().getDefaultGroup();
        String name = SearchService.class.getName();
        String version = this.rsfContext.getSettings().getDefaultVersion();
        //
        BindCenter bindCenter = rsfContext.getBindCenter();
        RsfBindInfo<?> info = bindCenter.getService(group, name, version);
        if (this.queryInfo == null) {
            this.queryInfo = bindCenter.getRsfBinder().rsfService(SearchService.class)//
                    .ngv(group, queryName, version)//
                    .bindFilter("CoreNameFilter", this.optionFilter)//
                    .bindAddress(rsfHost.getHost(), rsfHost.getPort()).register();
        }
        //
        T search = this.rsfClient.wrapper(info, type);
        Class<?> serviceType = CACHE_TYPES.get(coreName);
        if (serviceType == null) {
            synchronized (LOCK) {
                serviceType = CACHE_TYPES.get(coreName);
                if (serviceType == null) {
                    MethodClassConfig decConfig = new MethodClassConfig();
                    decConfig.addDelegate(type, new ServiceWarp(coreName, this.coreNameFilter, search));
                    serviceType = decConfig.toClass();
                    CACHE_TYPES.putIfAbsent(coreName, serviceType);
                }
            }
        }
        return (T) serviceType.newInstance();
    }
}