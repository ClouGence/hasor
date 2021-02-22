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
import net.hasor.dataql.CustomizeScope;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Query;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.runtime.inset.OpcodesPool;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;
import net.hasor.dataql.runtime.mem.ExitType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于封装和引发 QL 查询执行。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class QueryImpl extends HintsSet implements Query {
    private final QIL                 qil;
    private final Finder              finder;
    private final Map<String, Object> shareVarMap;

    QueryImpl(QIL qil, Finder finder) {
        this.qil = qil;
        this.finder = finder;
        this.shareVarMap = new HashMap<>();
    }

    @Override
    public Query clone() {
        QueryImpl query = new QueryImpl(this.qil, this.finder);
        query.shareVarMap.putAll(this.shareVarMap);
        return query;
    }

    @Override
    public void addShareVar(String key, Object value) {
        this.shareVarMap.put(key, value);
    }

    @Override
    public QueryResultImpl execute(CustomizeScope customize) throws InstructRuntimeException {
        InstSequence instSequence = new InstSequence(0, this.qil);
        //
        // .创建指令执行环境
        if (customize == null) {
            customize = symbol -> Collections.emptyMap();
        }
        InsetProcessContext processContext = new InsetProcessContext(customize, this.finder);
        // .汇总Option
        processContext.currentHints().setHints(this);
        // .创建堆栈
        DataStack dataStack = new DataStack();  // 指令执行 - 栈
        DataHeap dataHeap = new DataHeap();     // 指令执行 - 堆
        EnvStack envStack = new EnvStack();     // 环境数据 - 栈
        this.qil.getCompilerVar().forEach((varName, varLocalIdx) -> {
            Object varVal = shareVarMap.get(varName);
            dataHeap.saveData(varLocalIdx, varVal);
        });
        //
        // .执行指令序列
        OpcodesPool opcodesPool = OpcodesPool.defaultOpcodesPool();
        while (instSequence.hasNext()) {
            opcodesPool.doWork(instSequence, dataHeap, dataStack, envStack, processContext);
            instSequence.doNext(1);
        }
        // .结果处理
        ExitType exitType = (dataStack.getExitType() == null) ? ExitType.Return : dataStack.getExitType();
        int resultCode = dataStack.getResultCode();
        DataModel result = dataStack.getResult();
        long executionTime = processContext.executionTime();
        return new QueryResultImpl(exitType, resultCode, result, executionTime);
    }
}
