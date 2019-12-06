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
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.runtime.inset.OpcodesPool;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;

import java.util.Objects;

/**
 * 用于封装和引发 QL 查询执行。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class QueryEngineImpl extends OptionSet implements QueryEngine {
    private final static OpcodesPool opcodesPool = OpcodesPool.defaultOpcodesPool();
    private final        Finder      finder;
    private final        QIL         qil;

    public QueryEngineImpl(QIL qil) {
        this(qil, new Finder() {
        });
    }

    public QueryEngineImpl(QIL qil, Finder finder) {
        this.qil = Objects.requireNonNull(qil, "qil is null.");
        this.finder = Objects.requireNonNull(finder, "beanFinder is null.");
    }

    @Override
    public QIL getQil() {
        return this.qil;
    }

    /** 创建一个新查询实例。 */
    public QueryImpl newQuery() {
        return new QueryImpl(this);
    }

    DataStack processInset(         //
            InstSequence sequence,  // 指令序列
            DataHeap dataHeap,      // 数据堆
            DataStack dataStack,    // 数据栈
            EnvStack envStack,      // 环境栈
            Option optionSet,       //
            CustomizeScope customize) throws InstructRuntimeException {
        //
        // .默认Option
        InsetProcessContext processContext = new InsetProcessContext(customize, this.finder);
        processContext.setOptionSet(this);
        processContext.setOptionSet(optionSet);
        for (OptionKeys optionKey : OptionKeys.values()) {
            processContext.putIfAbsent(optionKey.name(), optionKey.getDefaultVal());
        }
        //
        while (sequence.hasNext()) {
            opcodesPool.doWork(sequence, dataHeap, dataStack, envStack, processContext);
            sequence.doNext(1);
        }
        return dataStack;
    }
}