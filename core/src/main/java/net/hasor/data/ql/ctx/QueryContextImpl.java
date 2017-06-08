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
package net.hasor.data.ql.ctx;
import net.hasor.data.ql.QueryContext;
import net.hasor.data.ql.QueryUDF;
import net.hasor.data.ql.UDF;

import java.util.HashMap;
import java.util.Map;
/**
 * QL 查询上下文，一个扩展的 Map 对象。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class QueryContextImpl implements QueryContext {
    private String              pathName   = null;
    private QueryContext        parent     = null;
    private Object              input      = null;
    private Object              output     = null;
    private Map<String, Object> contextMap = null;
    private QueryUDF            queryUDF   = null;
    //
    public QueryContextImpl(QueryUDF queryUDF, Map<String, Object> contextMap) {
        this.pathName = "$";
        this.contextMap = new HashMap<String, Object>(contextMap);
        this.queryUDF = queryUDF;
    }
    private QueryContextImpl(String pathName, QueryContextImpl parent, Object input) {
        //
        this.pathName = pathName;
        this.parent = parent;
        this.input = input;
        this.contextMap = parent.contextMap;
        this.queryUDF = parent.queryUDF;
    }
    //
    @Override
    public UDF findUDF(String udfName) {
        return this.queryUDF.findUDF(udfName);
    }
    @Override
    public Object get(String name) {
        return this.contextMap.get(name);
    }
    @Override
    public String getName() {
        return this.pathName;
    }
    @Override
    public QueryContext getParent() {
        return this.parent;
    }
    @Override
    public Object getInput() {
        return this.input;
    }
    @Override
    public void setInput(Object input) {
        this.input = input;
    }
    @Override
    public Object getOutput() {
        return this.output;
    }
    @Override
    public void setOutput(Object output) {
        this.output = output;
    }
    @Override
    public String getPath() {
        StringBuilder strBuild = new StringBuilder();
        QueryContext attr = this;
        do {
            strBuild = strBuild.insert(0, '/').insert(0, attr.getName());
            attr = attr.getParent();
        } while (attr.getParent() != null);
        return strBuild.toString();
    }
    @Override
    public QueryContext newStack(String pathName) {
        return this.newStack(pathName, this.input);
    }
    @Override
    public QueryContext newStack(String pathName, Object input) {
        return new QueryContextImpl(pathName, this, input);
    }
}