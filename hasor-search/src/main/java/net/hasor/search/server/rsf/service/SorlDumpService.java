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
package net.hasor.search.server.rsf.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.search.SearchException;
import net.hasor.search.client.DumpService;
import net.hasor.search.domain.OptionConstant;
import net.hasor.search.domain.SearchDocument;
import net.hasor.search.domain.UpdateSearchResult;
import net.hasor.search.utils.StrUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.more.logger.LoggerHelper;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2015年1月16日
 * @author 赵永春(zyc@hasor.net)
 */
public class SorlDumpService extends AbstractSearchService implements DumpService {
    @Override
    public UpdateSearchResult addMap(Map<String, ?> docMap) {
        return this.addSolrDoc(Arrays.asList(convetTo(docMap)), null);
    }
    @Override
    public UpdateSearchResult addMap(Map<String, ?> docMap, int commitWithinMs) {
        return this.addSolrDoc(Arrays.asList(convetTo(docMap)), commitWithinMs);
    }
    @Override
    public UpdateSearchResult addMap(List<Map<String, ?>> docMaps) {
        return this.addMapDoc(docMaps, null);
    }
    @Override
    public UpdateSearchResult addMap(List<Map<String, ?>> docMaps, int commitWithinMs) {
        return this.addMapDoc(docMaps, commitWithinMs);
    }
    @Override
    public UpdateSearchResult add(SearchDocument searchDoc) {
        return this.addSearchDoc(Arrays.asList(searchDoc), null);
    }
    @Override
    public UpdateSearchResult addList(List<SearchDocument> searchDocs) {
        return this.addSearchDoc(searchDocs, null);
    }
    @Override
    public UpdateSearchResult add(SearchDocument searchDoc, int commitWithinMs) {
        return this.addSearchDoc(Arrays.asList(searchDoc), Integer.valueOf(commitWithinMs));
    }
    @Override
    public UpdateSearchResult addList(List<SearchDocument> searchDocs, int commitWithinMs) {
        return this.addSearchDoc(searchDocs, Integer.valueOf(commitWithinMs));
    }
    @Override
    public UpdateSearchResult deleteByID(String id) {
        return this.deleteByIDs(Arrays.asList(id), null);
    }
    @Override
    public UpdateSearchResult deleteByID(String id, int commitWithinMs) {
        return this.deleteByIDs(Arrays.asList(id), Integer.valueOf(commitWithinMs));
    }
    @Override
    public UpdateSearchResult deleteByIDs(String[] ids) {
        return this.deleteByIDs(Arrays.asList(ids), null);
    }
    @Override
    public UpdateSearchResult deleteByIDs(String[] ids, int commitWithinMs) {
        return this.deleteByIDs(Arrays.asList(ids), Integer.valueOf(commitWithinMs));
    }
    @Override
    public UpdateSearchResult deleteByIDs(List<String> ids) {
        return this.deleteByIDs(ids, null);
    }
    @Override
    public UpdateSearchResult deleteByIDs(List<String> ids, int commitWithinMs) {
        return this.deleteByIDs(ids, Integer.valueOf(commitWithinMs));
    }
    @Override
    public UpdateSearchResult deleteByQuery(String queryString) {
        return this.deleteByQuery(queryString, null);
    }
    @Override
    public UpdateSearchResult deleteByQuery(String queryString, int commitWithinMs) {
        return this.deleteByQuery(queryString, Integer.valueOf(commitWithinMs));
    }
    //
    private UpdateSearchResult addMapDoc(List<Map<String, ?>> docMaps, Integer commitWithinMs) {
        if (docMaps == null || docMaps.isEmpty()) {
            return addSolrDoc(null, commitWithinMs);
        }
        List<SolrInputDocument> solrDocs = new ArrayList<SolrInputDocument>();
        for (Map<String, ?> docMap : docMaps) {
            solrDocs.add(convetTo(docMap));
        }
        return addSolrDoc(solrDocs, commitWithinMs);
    }
    private UpdateSearchResult addSearchDoc(List<SearchDocument> searchDocs, Integer commitWithinMs) {
        if (searchDocs == null || searchDocs.isEmpty()) {
            return addSolrDoc(null, commitWithinMs);
        }
        List<SolrInputDocument> solrDocs = new ArrayList<SolrInputDocument>();
        for (Map<String, Object> map : searchDocs) {
            solrDocs.add(convetTo(map));
        }
        return addSolrDoc(solrDocs, commitWithinMs);
    }
    //
    private UpdateSearchResult addSolrDoc(final List<SolrInputDocument> solrDocs, final Integer commitWithinMs) {
        if (solrDocs == null || solrDocs.isEmpty()) {
            UpdateSearchResult result = new UpdateSearchResult();
            result.setSuccess(false);
            result.setMessage("docs is empty or null.");
            LoggerHelper.logWarn("addList failure, %s", result);
            return result;
        }
        //
        return this.doExecute(new ExecuteService() {
            @Override
            public UpdateResponse doExecute(SolrServer solrServer) throws Throwable {
                if (commitWithinMs == null) {
                    return solrServer.add(solrDocs);
                } else {
                    return solrServer.add(solrDocs, commitWithinMs.intValue());
                }
            }
        });
    }
    private UpdateSearchResult deleteByIDs(final List<String> ids, final Integer commitWithinMs) {
        if (ids == null || ids.isEmpty()) {
            UpdateSearchResult result = new UpdateSearchResult();
            result.setSuccess(false);
            result.setMessage("ids is empty or null.");
            LoggerHelper.logWarn("deleteByIDs failure, %s", result);
            return result;
        }
        //
        return this.doExecute(new ExecuteService() {
            @Override
            public UpdateResponse doExecute(SolrServer solrServer) throws Throwable {
                if (commitWithinMs == null) {
                    return solrServer.deleteById(ids);
                } else {
                    return solrServer.deleteById(ids, commitWithinMs.intValue());
                }
            }
        });
    }
    private UpdateSearchResult deleteByQuery(final String queryString, final Integer commitWithinMs) {
        if (StringUtils.isBlank(queryString) == true) {
            UpdateSearchResult result = new UpdateSearchResult();
            result.setSuccess(false);
            result.setMessage("queryString is empty or null.");
            LoggerHelper.logWarn("deleteByQuery failure, %s", result);
            return result;
        }
        //
        return this.doExecute(new ExecuteService() {
            @Override
            public UpdateResponse doExecute(SolrServer solrServer) throws Throwable {
                if (commitWithinMs == null) {
                    return solrServer.deleteByQuery(queryString);
                } else {
                    return solrServer.deleteByQuery(queryString, commitWithinMs.intValue());
                }
            }
        });
    }
    private UpdateSearchResult doExecute(ExecuteService exec) {
        try {
            SolrServer solrServer = this.getSolrServer();
            RsfOptionSet optionSet = this.getRsfOptionSet();
            String commit = optionSet.getOption(OptionConstant.COMMIT_KEY);
            //
            UpdateResponse res = exec.doExecute(solrServer);
            if (StringUtils.equalsBlankIgnoreCase(commit, OptionConstant.COMMIT_VALUE)) {
                boolean waitFlush = StrUtils.parseBool(optionSet.getOption(OptionConstant.WAIT_FLUSH_KEY), true);
                boolean waitSearcher = StrUtils.parseBool(optionSet.getOption(OptionConstant.WAIT_SEARCHER_KEY), true);
                boolean softCommit = StrUtils.parseBool(optionSet.getOption(OptionConstant.SOFT_COMMIT_KEY), false);
                res = solrServer.commit(waitFlush, waitSearcher, softCommit);
            }
            //
            UpdateSearchResult result = new UpdateSearchResult();
            result.setSuccess(false);
            result.setElapsedTime(res.getElapsedTime());
            result.setStatus(res.getStatus());
            result.setQueryTime(res.getQTime());
            return result;
        } catch (Throwable e) {
            UpdateSearchResult result = new UpdateSearchResult();
            result.setSuccess(false);
            result.setThrowable(new SearchException(e.getMessage()));
            LoggerHelper.logSevere(e.getMessage(), e);
            return result;
        }
    }
}