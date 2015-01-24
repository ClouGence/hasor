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
import net.hasor.search.client.SearchService;
import net.hasor.search.domain.QuerySearchResult;
import net.hasor.search.domain.SearchDocument;
import net.hasor.search.query.SearchQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MultiMapSolrParams;
/**
 * 
 * @version : 2015年1月16日
 * @author 赵永春(zyc@hasor.net)
 */
public class SorlSearchService extends AbstractSearchService implements SearchService {
    @Override
    public QuerySearchResult query(SearchQuery searchQuery) throws Throwable {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.add(new MultiMapSolrParams(searchQuery.toMap()));
        QueryResponse response = getSolrServer().query(solrQuery);
        SolrDocumentList docList = response.getResults();
        //
        List<SearchDocument> documentList = new ArrayList<SearchDocument>();
        if (docList != null) {
            for (SolrDocument solrDocument : docList) {
                SearchDocument document = convetTo(solrDocument);
                documentList.add(document);
            }
        }
        //
        QuerySearchResult searchResult = new QuerySearchResult(documentList);
        searchResult.setElapsedTime(response.getElapsedTime());
        searchResult.setMaxScore(docList.getMaxScore());
        searchResult.setNumFound(docList.getNumFound());
        searchResult.setStart(docList.getStart());
        searchResult.setStatus(response.getStatus());
        searchResult.setQueryTime(response.getQTime());
        return searchResult;
    }
}