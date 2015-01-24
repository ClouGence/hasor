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
package net.test.simple.search._01_simple;
import java.util.HashMap;
import java.util.Map;
import net.hasor.search.client.DumpService;
import net.hasor.search.client.SearchService;
import net.hasor.search.client.rsf.SearchServer;
import net.hasor.search.client.rsf.SearchServerFactory;
import net.hasor.search.domain.QuerySearchResult;
import net.hasor.search.query.SearchQuery;
import org.junit.Test;
/**
 * 
 * @version : 2015年1月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class SimpleQuery {
    @Test
    public void insertQuesy() throws Throwable {
        SearchServerFactory factory = new SearchServerFactory();
        SearchServer server = factory.connect("local", 8000);
        //
        DumpService dump = server.getDumpService("collection1");
        //
        for (int i = 0; i < 10; i++) {
            Map<String, String> userInfo = new HashMap<String, String>();
            userInfo.put("id", String.valueOf(i));
            userInfo.put("title", "yongchun.zyc-" + String.valueOf(i));
            userInfo.put("author", "Num." + String.valueOf(i));
            dump.addMap(userInfo,1000);
        }
        //
        System.out.println();
    }
    @Test
    public void testQuesy() throws Throwable {
        SearchServerFactory factory = new SearchServerFactory();
        SearchServer server = factory.connect("local", 8000);
        //
        SearchService search = server.getSearchService("collection1");
        //
        QuerySearchResult res = search.query(new SearchQuery("*:*"));
        //
        System.out.println(res);
    }
}