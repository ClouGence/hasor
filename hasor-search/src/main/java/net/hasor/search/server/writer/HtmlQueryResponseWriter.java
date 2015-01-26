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
package net.hasor.search.server.writer;
import java.io.IOException;
import java.io.Writer;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.QueryResponseWriter;
import org.apache.solr.response.SolrQueryResponse;
/**
 * 
 * @version : 2015年1月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class HtmlQueryResponseWriter implements QueryResponseWriter {
    static String  CONTENT_TYPE_HTML_UTF8 = "text/html; charset=UTF-8";
    private String contentType            = CONTENT_TYPE_HTML_UTF8;
    @Override
    public void init(NamedList namedList) {
        String contentType = (String) namedList.get("content-type");
        if (contentType != null) {
            this.contentType = contentType;
        }
    }
    @Override
    public String getContentType(SolrQueryRequest request, SolrQueryResponse response) {
        return contentType;
    }
    @Override
    public void write(Writer writer, SolrQueryRequest request, SolrQueryResponse response) throws IOException {
        // TODO Auto-generated method stub
        System.out.println();
    }
}