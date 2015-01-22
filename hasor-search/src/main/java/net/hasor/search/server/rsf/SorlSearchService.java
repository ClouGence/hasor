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
package net.hasor.search.server.rsf;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.hasor.core.AppContext;
import net.hasor.core.InjectMembers;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.search.client.SearchService;
import net.hasor.search.domain.SearchDocument;
import net.hasor.search.domain.SearchResult;
import net.hasor.search.query.SearchQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.core.CoreContainer;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2015年1月16日
 * @author 赵永春(zyc@hasor.net)
 */
public class SorlSearchService implements SearchService, RsfFilter, InjectMembers {
    private static final ThreadLocal<String> UseCoreName = new ThreadLocal<String>();
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        String coreName = request.getOption("CoreName");
        if (StringUtils.isBlank(coreName) == true) {
            response.sendStatus(ProtocolStatus.Forbidden, "coreName is empty.");
            return;
        }
        //
        try {
            UseCoreName.set(coreName);
            chain.doFilter(request, response);
        } finally {
            UseCoreName.remove();
        }
    }
    //
    //
    //
    //
    //
    //
    private CoreContainer coreContainer = null;
    @Override
    public void doInject(AppContext appContext) {
        this.coreContainer = appContext.getInstance(CoreContainer.class);
        if (this.coreContainer == null) {
            throw new NullPointerException();
        }
    }
    protected SolrServer getSolrServer() {
        return new EmbeddedSolrServer(coreContainer, UseCoreName.get());
    }
    //
    @Override
    public SearchResult requestQuery(SearchQuery searchQuery) throws Throwable {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.add(new MultiMapSolrParams(searchQuery.toMap()));
        QueryResponse response = getSolrServer().query(solrQuery);
        SolrDocumentList docList = response.getResults();
        //
        //
        List<SearchDocument> documentList = new ArrayList<SearchDocument>();
        if (docList != null) {
            for (SolrDocument solrDocument : docList) {
                Set<Map.Entry<String, Object>> docDataEntrySet = solrDocument.entrySet();
                SearchDocument document = new SearchDocument();
                for (Map.Entry<String, Object> entry : docDataEntrySet) {
                    document.setField(entry.getKey(), entry.getValue());
                }
                documentList.add(document);
            }
        }
        //
        SearchResult searchResult = new SearchResult(documentList);
        searchResult.setElapsedTime(response.getElapsedTime());
        searchResult.setMaxScore(docList.getMaxScore());
        searchResult.setNumFound(docList.getNumFound());
        searchResult.setStart(docList.getStart());
        searchResult.setStatus(response.getStatus());
        searchResult.setQueryTime(response.getQTime());
        return searchResult;
    }
}