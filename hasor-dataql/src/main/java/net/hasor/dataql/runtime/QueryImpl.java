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
import net.hasor.dataql.HintNames;
import net.hasor.dataql.Query;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.runtime.inset.OpcodesPool;
import net.hasor.dataql.runtime.mem.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于封装和引发 QL 查询执行。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class QueryImpl extends HintsSet implements Query {
    private QIL                 qil;
    private Finder              finder;
    private Map<String, Object> compilerVar;

    QueryImpl(QIL qil, Finder finder) {
        this.qil = qil;
        this.finder = finder;
        this.compilerVar = new HashMap<>();
    }

    public void setCompilerVar(String compilerVar, Object object) {
        this.compilerVar.put(compilerVar, object);
    }

    private static long executionTime(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public QueryResultImpl execute(CustomizeScope customize) throws InstructRuntimeException {
        long startTime = System.currentTimeMillis();
        InstSequence instSequence = new InstSequence(0, this.qil);
        //
        // .创建指令执行环境
        if (customize == null) {
            customize = symbol -> Collections.emptyMap();
        }
        InsetProcessContext processContext = new InsetProcessContext(customize, this.finder);
        // .汇总Option
        processContext.setHints(this);
        for (HintNames optionKey : HintNames.values()) {
            processContext.putIfAbsent(optionKey.name(), optionKey.getDefaultVal());
        }
        // .创建堆栈
        DataStack dataStack = new DataStack();  // 指令执行 - 栈
        DataHeap dataHeap = new DataHeap();     // 指令执行 - 堆
        EnvStack envStack = new EnvStack();     // 环境数据 - 栈
        this.qil.getCompilerVar().forEach((varName, varLocalIdx) -> {
            Object varVal = compilerVar.get(varName);
            dataHeap.saveData(varLocalIdx, varVal);
        });
        //
        // .执行指令序列
        try {
            OpcodesPool opcodesPool = OpcodesPool.defaultOpcodesPool();
            while (instSequence.hasNext()) {
                opcodesPool.doWork(instSequence, dataHeap, dataStack, envStack, processContext);
                instSequence.doNext(1);
            }
        } catch (RefLambdaCallException e) {
            dataStack.setExitType(ExitType.Throw);
            dataStack.setResultCode(e.getResultCode());
            dataStack.setResult(e.getResult());
        }
        // .结果处理
        ExitType exitType = dataStack.getExitType();
        long executionTime = executionTime(startTime);
        int resultCode = dataStack.getResultCode();
        DataModel result = dataStack.getResult();
        if (ExitType.Exit == exitType) {
            return new QueryResultImpl(false, resultCode, result, executionTime);
        } else if (ExitType.Throw == exitType) {
            if (1 == 2) {
                return new QueryResultImpl(true, resultCode, result, executionTime);
            } else {
                throw new ThrowRuntimeException("udf or lambda failed.", resultCode, executionTime, result);
            }
        } else if (ExitType.Return == exitType) {
            return new QueryResultImpl(false, resultCode, result, executionTime);
        } else {
            throw new InstructRuntimeException(exitType + " ExitType undefined.");
        }
    }
}