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
package net.hasor.data.ql.runtime;
import net.hasor.data.ql.UDF;
import net.hasor.data.ql.ctx.QueryUDF;

import java.util.HashMap;
import java.util.Map;
/**
 * QL 查询上下文，用于查询引擎调度时使用。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class QueryContext implements QueryUDF {
    private String              pathName   = null;
    private QueryContext        parent     = null;
    private Object              input      = null;
    private Object              output     = null;
    private Map<String, Object> contextMap = null;
    private QueryUDF            queryUDF   = null;
    //
    public QueryContext(QueryUDF queryUDF, Map<String, Object> contextMap) {
        this.pathName = "$";
        this.contextMap = new HashMap<String, Object>(contextMap);
        this.queryUDF = queryUDF;
    }
    private QueryContext(String pathName, QueryContext parent, Object input) {
        //
        this.pathName = pathName;
        this.parent = parent;
        this.input = input;
        this.contextMap = parent.contextMap;
        this.queryUDF = parent.queryUDF;
    }
    //
    @Override
    public boolean containsUDF(String udfName) {
        return this.queryUDF.containsUDF(udfName);
    }
    @Override
    public UDF findUDF(String udfName) {
        return this.queryUDF.findUDF(udfName);
    }
    //
    public Object get(String name) {
        return this.contextMap.get(name);
    }
    public String getName() {
        return this.pathName;
    }
    public QueryContext getParent() {
        return this.parent;
    }
    public Object getInput() {
        return this.input;
    }
    public void setInput(Object input) {
        this.input = input;
    }
    public Object getOutput() {
        return this.output;
    }
    public void setOutput(Object output) {
        this.output = output;
    }
    //
    public String getPath() {
        StringBuilder strBuild = new StringBuilder();
        QueryContext attr = this;
        do {
            strBuild = strBuild.insert(0, '/').insert(0, attr.getName());
            attr = attr.getParent();
        } while (attr.getParent() != null);
        return strBuild.toString();
    }
    public QueryContext newStack(String pathName, Object input) {
        return new QueryContext(pathName, this, input);
    }
}