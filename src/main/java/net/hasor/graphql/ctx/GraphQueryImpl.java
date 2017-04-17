/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.graphql.ctx;
import net.hasor.core.AppContext;
import net.hasor.graphql.GraphQuery;
import net.hasor.graphql.QueryContext;
import net.hasor.graphql.QueryResult;
import net.hasor.graphql.UDF;
import net.hasor.graphql.dsl.QueryModel;
import net.hasor.graphql.runtime.AbstractQueryTask;
import net.hasor.graphql.runtime.TaskParser;

import java.util.List;
import java.util.Map;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class GraphQueryImpl implements GraphQuery {
    private AppContext appContext;
    private QueryModel queryModel;
    //
    public GraphQueryImpl(QueryModel queryModel) {
        this.queryModel = queryModel;
    }
    //
    @Override
    public String getQueryString(boolean useFragment) {
        if (useFragment) {
            return this.queryModel.buildQuery();
        } else {
            return this.queryModel.buildQueryWithoutFragment();
        }
    }
    //
    @Override
    public <T> T query(Map<String, Object> queryContext, Class<?> toType) {
        QueryResult result = this.query(queryContext);
        return null;
    }
    @Override
    public QueryResult query(Map<String, Object> queryContext) {
        AbstractQueryTask queryTask = new TaskParser().doParser(this.queryModel.getDomain());
        this.runTasks(new QueryContextImpl(queryContext) {
            @Override
            public UDF findUDF(String udfName) {
                return GraphQueryImpl.this.findUDF(udfName);
            }
        }, queryTask);
        return null;
    }
    private void runTasks(QueryContext context, AbstractQueryTask queryTask) {
        List<AbstractQueryTask> allTask = queryTask.getAllTask();
        int runCount = 0;
        do {
            runCount = 0;
            for (AbstractQueryTask t : allTask) {
                if (t.isWaiting()) {
                    t.run(context, null);
                    runCount++;
                }
            }
        } while (runCount != 0);
    }
    //
    //
    private UDF findUDF(String udfName) {
        return this.appContext.findBindingBean(udfName, GraphUDF.class);
    }
}
