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
package net.hasor.dataql.runtime;
import net.hasor.core.future.BasicFuture;
import net.hasor.core.utils.StringUtils;
import net.hasor.dataql.*;
import net.hasor.dataql.domain.compiler.InstOpcodes;
import net.hasor.dataql.domain.compiler.QueryType;
import net.hasor.dataql.result.DataModel;
import net.hasor.dataql.result.ListModel;
import net.hasor.dataql.result.ObjectModel;
import net.hasor.dataql.runtime.mem.LocalData;
import net.hasor.dataql.runtime.mem.MemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * 用于封装和引发 QL 查询执行。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class QueryInstance extends OptionSet implements Query {
    private BasicFuture<QueryResult> future;
    private QueryType                instSequence;
    private QueryEngine              queryEngine;
    private Map<String, Object>      queryContext;
    //
    QueryInstance(QueryEngine queryEngine, QueryType instSequence) {
        super(queryEngine);
        this.future = new BasicFuture<QueryResult>();
        this.queryEngine = queryEngine;
        this.instSequence = instSequence;
        this.queryContext = new HashMap<String, Object>();
    }
    //
    //
    @Override
    public void addParameter(String key, Object value) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        this.queryContext.put(key, value);
    }
    @Override
    public void addParameterMap(Map<String, Object> queryData) {
        if (queryData == null || queryData.isEmpty()) {
            return;
        }
        this.queryContext.putAll(queryData);
    }
    @Override
    public QueryResult execute() throws InvokerProcessException {
        Object resultData = null;
        try {
            // .准备执行环境堆栈
            MemStack memStack = new MemStack(); // 堆栈
            LocalData local = new LocalData();  // DS
            InstSequence sec = new InstSequence(0, this.instSequence.getArrays());
            // .执行指令序列
            this.queryEngine.processInset(sec, memStack, local);
            // .结果集
            resultData = memStack.getResult();
        } catch (ProcessException e) {
            if (e instanceof BreakProcessException) {
                BreakProcessException ipe = (BreakProcessException) e;
                if (InstOpcodes.EXIT == ipe.getInstOpcodes()) {
                    int errorCode = ipe.getErrorCode();
                    Object errorData = ipe.getErrorMsg();
                    DataModel res = evalQueryResult(errorData);
                    return new QueryResultImpl(errorCode, res);
                }
            } else {
                if (e instanceof InvokerProcessException) {
                    throw (InvokerProcessException) e;
                } else {
                    throw new InvokerProcessException(0, e.getMessage(), e);
                }
            }
        }
        // .返回值
        DataModel res = evalQueryResult(resultData);
        return new QueryResultImpl(0, res);
    }
    private DataModel evalQueryResult(Object resultData) {
        if (resultData == null) {
            return null;
        }
        if (resultData instanceof DataModel) {
            return (DataModel) resultData;
        }
        if (resultData instanceof Collection || resultData.getClass().isArray()) {
            return new ListModel(resultData);
        }
        return new ObjectModel(resultData);
    }
}