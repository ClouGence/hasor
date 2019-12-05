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
import net.hasor.dataql.Query;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;
import net.hasor.dataql.runtime.mem.ExitType;

import java.util.Collections;

/**
 * 用于封装和引发 QL 查询执行。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class QueryImpl extends OptionSet implements Query {
    private QueryEngineImpl queryEngine;

    QueryImpl(QueryEngineImpl queryEngine) {
        super(queryEngine);
        this.queryEngine = queryEngine;
    }

    @Override
    public QueryResultImpl execute(CustomizeScope customize) throws InstructRuntimeException {
        if (customize == null) {
            customize = symbol -> Collections.emptyMap();
        }
        //
        long startTime = System.currentTimeMillis();
        InstSequence instSequence = new InstSequence(0, this.queryEngine.getQil());
        DataStack dataStack = this.queryEngine.processInset(//
                instSequence,   // 指令序列
                new DataHeap(), // 数据堆
                new DataStack(),// 数据栈
                new EnvStack(), // 环境栈
                this, //
                customize       //
        );
        //
        // .结果
        ExitType exitType = dataStack.getExitType();
        long executionTime = executionTime(startTime);
        int resultCode = dataStack.getResultCode();
        DataModel result = dataStack.getResult();
        //
        if (ExitType.Exit == exitType) {
            return new QueryResultImpl(false, resultCode, result, executionTime);
        } else if (ExitType.Throw == exitType) {
            return new QueryResultImpl(true, resultCode, result, executionTime);
        } else if (ExitType.Return == exitType) {
            return new QueryResultImpl(false, resultCode, result, executionTime);
        } else {
            throw new InstructRuntimeException(exitType + " ExitType undefined.");
        }
    }

    private static long executionTime(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
}