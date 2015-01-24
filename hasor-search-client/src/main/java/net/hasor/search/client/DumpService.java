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
package net.hasor.search.client;
import java.util.List;
import java.util.Map;
import net.hasor.search.domain.SearchDocument;
import net.hasor.search.domain.UpdateSearchResult;
/**
 * Dump接口
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
public interface DumpService {
    /**新增*/
    public UpdateSearchResult addMap(Map<String, ?> doc);
    /**新增*/
    public UpdateSearchResult addMap(Map<String, ?> doc, int commitWithinMs);
    /**新增*/
    public UpdateSearchResult addMap(List<Map<String, ?>> doc);
    /**新增*/
    public UpdateSearchResult addMap(List<Map<String, ?>> doc, int commitWithinMs);
    /**新增*/
    public UpdateSearchResult add(SearchDocument doc);
    /**新增*/
    public UpdateSearchResult add(SearchDocument doc, int commitWithinMs);
    /**新增*/
    public UpdateSearchResult addList(List<SearchDocument> doc);
    /**新增*/
    public UpdateSearchResult addList(List<SearchDocument> doc, int commitWithinMs);
    //
    /**删除*/
    public UpdateSearchResult deleteByID(String id);
    /**删除*/
    public UpdateSearchResult deleteByID(String id, int commitWithinMs);
    /**批量删除*/
    public UpdateSearchResult deleteByIDs(String[] ids);
    /**批量删除*/
    public UpdateSearchResult deleteByIDs(String[] ids, int commitWithinMs);
    /**批量删除*/
    public UpdateSearchResult deleteByIDs(List<String> ids);
    /**批量删除*/
    public UpdateSearchResult deleteByIDs(List<String> ids, int commitWithinMs);
    /**根据查询批量删除*/
    public UpdateSearchResult deleteByQuery(String queryString);
    /**根据查询批量删除*/
    public UpdateSearchResult deleteByQuery(String queryString, int commitWithinMs);
}