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
import net.hasor.dataql.*;
import net.hasor.dataql.domain.compiler.Opcodes;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.result.DataModel;
import net.hasor.dataql.result.ListModel;
import net.hasor.dataql.result.ObjectModel;
import net.hasor.dataql.result.ValueModel;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
import net.hasor.utils.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 用于封装和引发 QL 查询执行。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class QueryInstance extends OptionSet implements Query {
    private QIL                 instSequence;
    private QueryEngine         queryEngine;
    private Map<String, Object> queryContext;
    //
    QueryInstance(QueryEngine queryEngine, QIL instSequence) {
        super(queryEngine);
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
        long startTime = System.currentTimeMillis();
        Object resultData = null;
        try {
            // .准备执行环境堆栈
            MemStack memStack = new MemStack(0); // 堆栈
            StackStruts local = new StackStruts();  // DS
            InstSequence sec = new InstSequence(0, this.instSequence);
            // .执行指令序列
            this.queryEngine.processInset(sec, memStack, local);
            // .结果集
            resultData = memStack.getResult();
        } catch (ProcessException e) {
            if (e instanceof BreakProcessException) {
                BreakProcessException ipe = (BreakProcessException) e;
                int errorCode = ipe.getErrorCode();
                Object errorData = ipe.getErrorMsg();
                DataModel res = evalQueryResult(errorData);
                //
                if (Opcodes.EXIT == ipe.getInstOpcodes()) {
                    return new QueryResultImpl(errorCode, executionTime(startTime), res);
                } else {
                    return new QueryResultImpl(true, errorCode, executionTime(startTime), res);
                }
            }
            if (e instanceof InvokerProcessException) {
                throw (InvokerProcessException) e;
            } else {
                throw new InvokerProcessException(0, e.getMessage(), e);
            }
        }
        // .返回值
        DataModel res = evalQueryResult(resultData);
        return new QueryResultImpl(0, executionTime(startTime), res);
    }
    private static long executionTime(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
    //
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
        if (resultData.getClass().isPrimitive() || //
                resultData instanceof Number || //
                resultData instanceof Boolean || //
                resultData instanceof Date || //
                resultData instanceof Character ||//
                resultData instanceof String//
                ) {
            return new ValueModel(resultData);
        }
        //
        return new ObjectModel(resultData);
    }
}