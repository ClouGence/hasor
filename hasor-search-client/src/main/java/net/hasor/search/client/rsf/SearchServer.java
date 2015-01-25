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
import java.net.URL;
import net.hasor.rsf.BindCenter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.search.client.Commit;
import net.hasor.search.client.DumpService;
import net.hasor.search.client.SearchService;
/**
 * 
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class SearchServer {
    private URL        rsfHost    = null;
    private RsfContext rsfContext = null;
    //
    SearchServer(URL rsfHost, SearchServerFactory searchServerFactory) {
        this.rsfHost = rsfHost;
        this.rsfContext = searchServerFactory.getRsfContext();
    }
    //
    public SearchService getSearchService(String coreName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        return getService(coreName, SearchService.class, null);
    }
    public SearchService getSearchService(String coreName, Commit commitMode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        return getService(coreName, SearchService.class, commitMode);
    }
    public DumpService getDumpService(String coreName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        return getService(coreName, DumpService.class, null);
    }
    public DumpService getDumpService(String coreName, Commit commitMode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        return getService(coreName, DumpService.class, commitMode);
    }
    //
    //
    //
    private <T> T getService(String coreName, Class<T> serviceType, Commit commitMode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        String name = serviceType.getName();
        String version = this.rsfContext.getSettings().getDefaultVersion();
        //
        BindCenter bindCenter = this.rsfContext.getBindCenter();
        RsfBindInfo<?> serviceInfo = bindCenter.getService(coreName, name, version);
        //
        if (serviceInfo == null) {
            serviceInfo = bindCenter.getRsfBinder().rsfService(serviceType)//
                    .ngv(coreName, name, version)//
                    .bindFilter(CommitOptionFilter.class.getName(), new CommitOptionFilter(commitMode))//
                    .bindAddress(rsfHost.getHost(), rsfHost.getPort()).register();
        }
        //
        RsfClient rsfClient = this.rsfContext.getRsfClient();
        return rsfClient.wrapper(serviceInfo, serviceType);
    }
}