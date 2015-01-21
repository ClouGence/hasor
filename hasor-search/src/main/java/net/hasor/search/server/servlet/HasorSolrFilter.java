/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.search.server.servlet;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import net.hasor.core.AppContext;
import net.hasor.web.startup.RuntimeListener;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.servlet.SolrDispatchFilter;
/**
 * 
 * @version : 2015年1月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class HasorSolrFilter extends SolrDispatchFilter {
    private AppContext appContext = null;
    @Override
    protected CoreContainer createCoreContainer() {
        return this.appContext.getInstance(CoreContainer.class);
    }
    @Override
    public void init(FilterConfig config) throws ServletException {
        this.appContext = RuntimeListener.getLocalAppContext();
        super.init(config);
    }
}