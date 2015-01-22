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
package net.hasor.search.server.query;
import java.io.IOException;
import java.io.StringWriter;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.StartModule;
import net.hasor.search.server.installs.SorlInstall;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.QueryResponseWriter;
import org.apache.solr.response.SolrQueryResponse;
/**
 * 
 * @version : 2015年1月16日
 * @author 赵永春(zyc@hasor.net)
 */
public class SorlQuery implements StartModule {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.installModule(new SorlInstall());
    }
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        CoreContainer cores = appContext.getInstance(CoreContainer.class);
        SolrCore solrCore = cores.getCore("new_core");
        //
        
        //
        query(solrCore, new SolrQuery("*:*"));
    }
    public void query(SolrCore solrCore, SolrQuery solrQuery) throws IOException {
        // 
        SolrQueryRequest solrReq = new LocalSolrQueryRequest(solrCore, solrQuery);
        SolrQueryResponse solrRes = new SolrQueryResponse();
        //
        String qt = solrReq.getParams().get(CommonParams.QT);
        SolrRequestHandler handler = solrCore.getRequestHandler(qt);
        // 
        solrCore.execute(handler, solrReq, solrRes);
        //
        QueryResponseWriter responseWriter = solrCore.getQueryResponseWriter(solrReq);
        StringWriter sw = new StringWriter();
        responseWriter.write(sw, solrReq, solrRes);
        //
        System.out.println();
        //
    }
}